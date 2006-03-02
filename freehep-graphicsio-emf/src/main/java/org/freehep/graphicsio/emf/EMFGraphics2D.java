// Copyright 2000-2006 FreeHEP
package org.freehep.graphicsio.emf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.RenderedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import org.freehep.graphics2d.PrintColor;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphics2d.font.FontEncoder;
import org.freehep.graphicsio.AbstractVectorGraphicsIO;
import org.freehep.graphicsio.PageConstants;
import org.freehep.graphicsio.font.FontUtilities;
import org.freehep.util.UserProperties;

/**
 * Enhanced Metafile Format Graphics 2D driver.
 * 
 * @author Mark Donszelmann
 * @version $Id: freehep-graphicsio-emf/src/main/java/org/freehep/graphicsio/emf/EMFGraphics2D.java e7c6b553ef3f 2006/03/02 00:03:09 duns $
 */
public class EMFGraphics2D extends AbstractVectorGraphicsIO implements
        EMFConstants {
    public static final String version = "$Revision$";

    private EMFHandleManager handleManager;

    private int penHandle;

    private int brushHandle;

    private Rectangle imageBounds;

    private OutputStream ros;

    private EMFOutputStream os;

    private boolean fontSet = false;

    private Color textColor = null;

    private Color penColor = null;

    private Color brushColor = null;

    private Map fontTable; // java fonts

    private Map unitFontTable; // windows fonts

    private EMFGraphics2D parentGraphics;

    private EMFPathConstructor pathConstructor;

    private boolean evenOdd;

    private static final Rectangle dummy = new Rectangle(0, 0, 0, 0);

    /*
     * ================================================================================
     * Table of Contents: ------------------ 1. Constructors & Factory Methods
     * 2. Document Settings 3. Header, Trailer, Multipage & Comments 3.1 Header &
     * Trailer 3.2 MultipageDocument methods 4. Create & Dispose 5. Drawing
     * Methods 5.1. shapes (draw/fill) 5.1.1. lines, rectangles, round
     * rectangles 5.1.2. polylines, polygons 5.1.3. ovals, arcs 5.1.4. shapes
     * 5.2. Images 5.3. Strings 6. Transformations 7. Clipping 8. Graphics State /
     * Settings 8.1. stroke/linewidth 8.2. paint/color 8.3. font 8.4. rendering
     * hints 9. Auxiliary 10. Private/Utility Methos
     * ================================================================================
     */
    private static final String rootKey = EMFGraphics2D.class.getName();

    public static final String TRANSPARENT = rootKey + "."
            + PageConstants.TRANSPARENT;

    public static final String BACKGROUND = rootKey + "."
            + PageConstants.BACKGROUND;

    public static final String BACKGROUND_COLOR = rootKey + "."
            + PageConstants.BACKGROUND_COLOR;

    public static final String COMPRESS = rootKey + ".Binary";

    private static final UserProperties defaultProperties = new UserProperties();
    static {
        defaultProperties.setProperty(TRANSPARENT, true);
        defaultProperties.setProperty(BACKGROUND, false);
        defaultProperties.setProperty(BACKGROUND_COLOR, Color.GRAY);
        defaultProperties.setProperty(COMPRESS, false);
        defaultProperties.setProperty(CLIP, true);
    }

    public static Properties getDefaultProperties() {
        return defaultProperties;
    }

    public static void setDefaultProperties(Properties newProperties) {
        defaultProperties.setProperties(newProperties);
    }

    /*
     * ================================================================================
     * 1. Constructors & Factory Methods
     * ================================================================================
     */
    public EMFGraphics2D(File file, Dimension size)
            throws FileNotFoundException {
        this(new FileOutputStream(file), size);
    }

    public EMFGraphics2D(File file, Component component)
            throws FileNotFoundException {
        this(new FileOutputStream(file), component);
    }

    public EMFGraphics2D(OutputStream os, Dimension size) {
        super(size, false);
        this.imageBounds = new Rectangle(0, 0, size.width, size.height);
        init(os);
    }

    public EMFGraphics2D(OutputStream os, Component component) {
        super(component, false);
        this.imageBounds = new Rectangle(0, 0, getSize().width,
                getSize().height);
        init(os);
    }

    private void init(OutputStream os) {
        fontTable = new HashMap();
        unitFontTable = new HashMap();
        evenOdd = false;

        handleManager = new EMFHandleManager();
        ros = os;
        initProperties(defaultProperties);
    }

    protected EMFGraphics2D(EMFGraphics2D graphics, boolean doRestoreOnDispose) {
        super(graphics, doRestoreOnDispose);
        // Create a graphics context from a given graphics context.
        // This constructor is used by the system to clone a given graphics
        // context.
        // doRestoreOnDispose is used to call writeGraphicsRestore(),
        // when the graphics context is being disposed off.
        os = graphics.os;
        imageBounds = graphics.imageBounds;
        handleManager = graphics.handleManager;
        fontTable = graphics.fontTable;
        unitFontTable = graphics.unitFontTable;
        pathConstructor = graphics.pathConstructor;
        evenOdd = graphics.evenOdd;
        textColor = graphics.textColor;
        penColor = graphics.penColor;
        brushColor = graphics.brushColor;
        parentGraphics = graphics;
    }
    /*
     * ================================================================================ |
     * 2. Document Settings
     * ================================================================================
     */

    /*
     * ================================================================================ |
     * 3. Header, Trailer, Multipage & Comments
     * ================================================================================
     */
    /* 3.1 Header & Trailer */
    public void writeHeader() throws IOException {
        ros = new BufferedOutputStream(ros);
        if (isProperty(COMPRESS))
            ros = new GZIPOutputStream(ros);
        Dimension device = isDeviceIndependent() ? new Dimension(1024, 768)
                : Toolkit.getDefaultToolkit().getScreenSize();
        String producer = getClass().getName();
        if (!isDeviceIndependent()) {
            producer += " " + version.substring(1, version.length() - 1);
        }
        os = new EMFOutputStream(ros, imageBounds, handleManager, getCreator(),
                producer, device);
        pathConstructor = new EMFPathConstructor(os, imageBounds);

        Point orig = new Point(imageBounds.x, imageBounds.y);
        Dimension size = new Dimension(imageBounds.width, imageBounds.height);

        os.writeTag(new SetMapMode(MM_ANISOTROPIC));
        os.writeTag(new SetWindowOrgEx(orig));
        os.writeTag(new SetWindowExtEx(size));
        os.writeTag(new SetViewportOrgEx(orig));
        os.writeTag(new SetViewportExtEx(size));
        os.writeTag(new SetTextAlign(TA_BASELINE));
        os.writeTag(new SetTextColor(getColor()));
        os.writeTag(new SetPolyFillMode(EMFConstants.WINDING));

    }

    public void writeGraphicsState() throws IOException {
        super.writeGraphicsState();
        // write a special matrix here to scale all written coordinates by a
        // factor of TWIPS
        AffineTransform n = AffineTransform.getScaleInstance(1.0 / TWIPS,
                1.0 / TWIPS);
        os.writeTag(new SetWorldTransform(n));
    }

    public void writeBackground() throws IOException {
        if (isProperty(TRANSPARENT)) {
            setBackground(null);
            os.writeTag(new SetBkMode(BKG_TRANSPARENT));
        } else if (isProperty(BACKGROUND)) {
            os.writeTag(new SetBkMode(BKG_OPAQUE));
            setBackground(getPropertyColor(BACKGROUND_COLOR));
            clearRect(0.0, 0.0, getSize().width, getSize().height);
        } else {
            os.writeTag(new SetBkMode(BKG_OPAQUE));
            setBackground(getComponent() != null ? getComponent()
                    .getBackground() : Color.WHITE);
            clearRect(0.0, 0.0, getSize().width, getSize().height);
        }
    }

    public void writeTrailer() throws IOException {
        // delete any remaining objects
        for (;;) {
            int handle = handleManager.highestHandleInUse();
            if (handle < 0)
                break;
            os.writeTag(new DeleteObject(handle));
            handleManager.freeHandle(handle);
        }
        os.writeTag(new EOF());
    }

    public void closeStream() throws IOException {
        os.close();
    }

    /* 3.2 MultipageDocument methods */

    /*
     * ================================================================================
     * 4. Create & Dispose
     * ================================================================================
     */

    public Graphics create() {
        // Create a new graphics context from the current one.
        try {
            // Save the current context for restore later.
            writeGraphicsSave();
        } catch (IOException e) {
            handleException(e);
        }
        // The correct graphics context should be created.
        return new EMFGraphics2D(this, true);
    }

    public Graphics create(double x, double y, double width, double height) {
        // Create a new graphics context from the current one.
        try {
            // Save the current context for restore later.
            writeGraphicsSave();
        } catch (IOException e) {
            handleException(e);
        }
        // The correct graphics context should be created.
        VectorGraphics graphics = new EMFGraphics2D(this, true);
        graphics.translate(x, y);
        graphics.clipRect(0, 0, width, height);
        return graphics;
    }

    protected void writeGraphicsSave() throws IOException {
        os.writeTag(new SaveDC());
    }

    protected void writeGraphicsRestore() throws IOException {
        if (penHandle != 0)
            os.writeTag(new DeleteObject(handleManager.freeHandle(penHandle)));
        if (brushHandle != 0)
            os
                    .writeTag(new DeleteObject(handleManager
                            .freeHandle(brushHandle)));
        os.writeTag(new RestoreDC());
        // Restore does not restore the font, so if the font has been modified
        // in this context
        // the parents font should now be consider unset.
        if (fontSet && (parentGraphics != null))
            parentGraphics.fontSet = false;
    }

    /*
     * ================================================================================ |
     * 5. Drawing Methods
     * ================================================================================
     */
    /* 5.1.4. shapes */
    Point[] points = new Point[] { new Point(0, 0), new Point(0, 0),
            new Point(0, 0), new Point(0, 0) };

    Color invisible = new Color(0, 0, 0, 0);

    public void draw(Shape shape) {
        try {
            if (getStroke() instanceof BasicStroke) {            
                writePen((BasicStroke) getStroke(), getColor());
                writePath(shape);
                os.writeTag(new StrokePath(imageBounds));
            } else {
                writeBrush(getColor());
                writePath(getStroke().createStrokedShape(shape));
                os.writeTag(new FillPath(imageBounds));
            }
        } catch (IOException e) {
            handleException(e);
        }
    }

    public void fill(Shape shape) {
        try {
            writeBrush(getColor());
            writePath(shape);
            os.writeTag(new FillPath(imageBounds));
        } catch (IOException e) {
            handleException(e);
        }
    }

    public void fillAndDraw(Shape shape, Color fillColor) {
        try {
            writePen((BasicStroke) getStroke(), getColor());
            writeBrush(fillColor);
            writePath(shape);
            os.writeTag(new StrokeAndFillPath(imageBounds));
        } catch (IOException e) {
            handleException(e);
        }
    }

    /* 5.2. Images */
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        writeWarning(getClass()
                + ": copyArea(int, int, int, int, int, int) not implemented.");
        // Mostly unimplemented.
    }

    // NOTE: does not use writeGraphicsSave and writeGraphicsRestore since these
    // delete pen and brush
    protected void writeImage(RenderedImage image, AffineTransform xform,
            Color bkg) throws IOException {
        os.writeTag(new SaveDC());

        AffineTransform imageTransform = new AffineTransform(1.0, 0.0, 0.0,
                -1.0, 0.0, image.getHeight());
        imageTransform.preConcatenate(xform);
        writeTransform(imageTransform);

        os.writeTag(new AlphaBlend(imageBounds, toUnit(0), toUnit(0),
                toUnit(image.getWidth()), toUnit(image.getHeight()),
                new AffineTransform(), image, bkg));
        os.writeTag(new RestoreDC());
    }

    private final static Properties replaceFonts = new Properties();
    static {
        replaceFonts.setProperty("Symbol", "Arial Unicode MS");
        replaceFonts.setProperty("ZapfDingbats", "Arial Unicode MS");
    }

    /* 5.3. Strings */
    public void writeString(String string, double x, double y)
            throws IOException {

        Color color;
        Paint paint = getPaint();
        if (paint instanceof Color) {
            color = (Color) paint;
        } else if (paint instanceof GradientPaint) {
            GradientPaint gp = (GradientPaint) paint;
            color = PrintColor.mixColor(gp.getColor1(), gp.getColor2());
        } else {
            Color bkg = getBackground();
            if (bkg == null) {
                color = Color.BLACK;
            } else {
                color = PrintColor.invert(bkg);
            }
        }
        if (!color.equals(textColor)) {
            textColor = color;
            os.writeTag(new SetTextColor(textColor));
        }

        Font font = getFont();
        Font unitFont = (Font) unitFontTable.get(font);
        if (!fontSet) {
            Integer fontIndex = (Integer) fontTable.get(font);
            if (fontIndex == null) {
                // for special fonts (Symbol, ZapfDingbats) we choose a standard
                // font and
                // encode using unicode.
                String fontName = font.getName();
                string = FontEncoder.getEncodedString(string, fontName);

                fontName = replaceFonts.getProperty(fontName, fontName);
                String windowsFontName = FontUtilities
                        .getWindowsFontName(fontName);

                unitFont = new Font(windowsFontName, font.getStyle(), font
                        .getSize());
                unitFont = unitFont.deriveFont(font.getSize2D()
                        * UNITS_PER_PIXEL * TWIPS);
                unitFontTable.put(font, unitFont);

                ExtLogFontW logFontW = new ExtLogFontW(unitFont);
                int handle = handleManager.getHandle();
                os.writeTag(new ExtCreateFontIndirectW(handle, logFontW));

                fontIndex = new Integer(handle);
                fontTable.put(font, fontIndex);
            }
            os.writeTag(new SelectObject(fontIndex.intValue()));
            fontSet = true;
        }
        int[] widths = new int[string.length()];
        for (int i = 0; i < widths.length; i++) {
            double w = unitFont.getStringBounds(string, i, i + 1,
                    getFontRenderContext()).getWidth();
            widths[i] = (int) w;
        }

        AffineTransform t = font.getTransform();
        if (!t.isIdentity()) {
            writeGraphicsSave();
            writeTransform(t);
        }
        TextW text = new TextW(new Point(toUnit(x), toUnit(y)), string, 0,
                dummy, widths);
        os.writeTag(new ExtTextOutW(imageBounds, EMFConstants.GM_ADVANCED, 1,
                1, text));
        if (!t.isIdentity()) {
            writeGraphicsRestore();
        }
    }

    public void drawGlyphVector(GlyphVector glyphs, float x, float y) {
        writeWarning(getClass()
                + ": drawGlyphVector(GlyphVector, float, float) not implemented.");
        // Write out the string.

        for (int i = 0; i < glyphs.getNumGlyphs(); i++) {
            fill(glyphs.getGlyphOutline(i, x, y));
        }
    }

    /*
     * ================================================================================ |
     * 6. Transformations
     * ================================================================================
     */
    protected void writeTransform(AffineTransform t) throws IOException {
        AffineTransform n = new AffineTransform(t.getScaleX(), t.getShearY(), t
                .getShearX(), t.getScaleY(), t.getTranslateX()
                * UNITS_PER_PIXEL * TWIPS, t.getTranslateY() * UNITS_PER_PIXEL
                * TWIPS);
        os.writeTag(new ModifyWorldTransform(n, EMFConstants.MWT_LEFTMULTIPLY));
    }

    protected void writeSetTransform(AffineTransform t) throws IOException {
        // write a special matrix here to scale all written coordinates by a factor of TWIPS
        AffineTransform n = AffineTransform.getScaleInstance(1.0/TWIPS, 1.0/TWIPS);
        os.writeTag(new SetWorldTransform(n));
        // apply transform
        writeTransform(t);
    }

    /*
     * ================================================================================ |
     * 7. Clipping
     * ================================================================================
     */
    protected void writeSetClip(Shape s) throws IOException {
        if ((s == null) || !isProperty(CLIP))
            return;
        // Write out the clip shape.
        writePath(s);
        os.writeTag(new SelectClipPath(EMFConstants.RGN_COPY));
    }

    protected void writeClip(Shape s) throws IOException {
        if ((s == null) || !isProperty(CLIP))
            return;
        // Write out the clip shape.
        writePath(s);
        os.writeTag(new SelectClipPath(EMFConstants.RGN_AND));
    }

    /*
     * ================================================================================ |
     * 8. Graphics State
     * ================================================================================
     */
    public void writeStroke(Stroke stroke) throws IOException {
        if (stroke instanceof BasicStroke) {
            writePen((BasicStroke) stroke, getColor());
        }
    }

    /* 8.2. paint/color */
    public void setPaintMode() {
        writeWarning(getClass() + ": setPaintMode() not implemented.");
        // Mostly unimplemented.
    }

    public void setXORMode(Color c1) {
        writeWarning(getClass() + ": setXORMode(Color) not implemented.");
        // Mostly unimplemented.
    }

    protected void writePaint(Color p) throws IOException {
        // all color setting delayed
    }

    protected void writePaint(GradientPaint p) throws IOException {
        writeWarning(getClass()
                + ": writePaint(GradientPaint) not implemented.");
        // Write out the gradient paint.
        setColor(PrintColor.mixColor(p.getColor1(), p.getColor2()));
    }

    protected void writePaint(TexturePaint p) throws IOException {
        writeWarning(getClass() + ": writePaint(TexturePaint) not implemented.");
        // Write out the texture paint.
        setColor(Color.RED);
    }

    protected void writePaint(Paint p) throws IOException {
        writeWarning(getClass() + ": writePaint(Paint) not implemented for "
                + p.getClass());
        // Write out the paint.
        setColor(Color.WHITE);
    }

    /* 8.3. font */
    /**
     * This method sets the current font. However, it does not write it to the
     * file. When drawString() wants to show text, it has to call writeFont()
     * first, which will actually write the current font to the document. Thus
     * we avoid embedding unused fonts (e.g. the Dialog font which is set by
     * java for whatsoever reason
     */
    public void setFont(Font font) {
        if (!font.equals(getFont())) {
            fontSet = false;
        }
        super.setFont(font);
    }

    /* 8.4. rendering hints */

    /*
     * ================================================================================ |
     * 9. Auxiliary
     * ================================================================================
     */
    public GraphicsConfiguration getDeviceConfiguration() {
        writeWarning(getClass() + ": getDeviceConfiguration() not implemented.");
        // Mostly unimplemented
        return null;
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        writeWarning(getClass()
                + ": hit(Rectangle, Shape, boolean) not implemented.");
        // Mostly unimplemented
        return false;
    }

    public void writeComment(String comment) throws IOException {
        writeWarning(getClass() + ": writeComment(String) not implemented.");
        // Write out the comment.
    }

    public String toString() {
        return "EMFGraphics2D";
    }

    /**
     * Implementation of createShape makes sure that the points are different by
     * at least one Unit.
     */
    protected Shape createShape(double[] xPoints, double[] yPoints,
            int nPoints, boolean close) {
        GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        if (nPoints > 0) {
            path.moveTo((float) xPoints[0], (float) yPoints[0]);
            double lastX = xPoints[0];
            double lastY = yPoints[0];
            if (close && (Math.abs(xPoints[nPoints - 1] - lastX) < 1)
                    && (Math.abs(yPoints[nPoints - 1] - lastY) < 1)) {
                nPoints--;
            }
            for (int i = 1; i < nPoints; i++) {
                if ((Math.abs(xPoints[i] - lastX) > 1)
                        || (Math.abs(yPoints[i] - lastY) > 1)) {
                    path.lineTo((float) xPoints[i], (float) yPoints[i]);
                    lastX = xPoints[i];
                    lastY = yPoints[i];
                }
            }
            if (close)
                path.closePath();
        }
        return path;
    }

    /*
     * Private methods
     */
    private boolean writePath(Shape shape) throws IOException {
        boolean eo = EMFPathConstructor.isEvenOdd(shape);
        if (eo != evenOdd) {
            evenOdd = eo;
            os.writeTag(new SetPolyFillMode((evenOdd) ? EMFConstants.ALTERNATE
                    : EMFConstants.WINDING));
        }
        os.writeTag(new BeginPath());
        pathConstructor.addPath(shape);
        os.writeTag(new EndPath());
        return evenOdd;
    }

    private void writePen(BasicStroke stroke, Color color) throws IOException {
        if (color.equals(penColor) && stroke.equals(getStroke()))
            return;
        penColor = color;

        int style = EMFConstants.PS_GEOMETRIC;

        switch (stroke.getEndCap()) {
        case BasicStroke.CAP_BUTT:
            style |= EMFConstants.PS_ENDCAP_FLAT;
            break;
        case BasicStroke.CAP_ROUND:
            style |= EMFConstants.PS_ENDCAP_ROUND;
            break;
        case BasicStroke.CAP_SQUARE:
            style |= EMFConstants.PS_ENDCAP_SQUARE;
            break;
        }

        switch (stroke.getLineJoin()) {
        case BasicStroke.JOIN_MITER:
            style |= EMFConstants.PS_JOIN_MITER;
            break;
        case BasicStroke.JOIN_ROUND:
            style |= EMFConstants.PS_JOIN_ROUND;
            break;
        case BasicStroke.JOIN_BEVEL:
            style |= EMFConstants.PS_JOIN_BEVEL;
            break;
        }

        // FIXME int conversion
        // FIXME phase ignored
        float[] dashArray = stroke.getDashArray();
        int[] dash = new int[(dashArray != null) ? dashArray.length : 0];
        style |= (dash.length == 0) ? EMFConstants.PS_SOLID
                : EMFConstants.PS_USERSTYLE;
        for (int i = 0; i < dash.length; i++) {
            dash[i] = toUnit(dashArray[i]);
        }

        int brushStyle = (color.getAlpha() == 0) ? EMFConstants.BS_NULL
                : EMFConstants.BS_SOLID;

        ExtLogPen pen = new ExtLogPen(style, toUnit(stroke.getLineWidth()),
                brushStyle, getPrintColor(color), 0, dash);
        if (penHandle != 0) {
            os.writeTag(new DeleteObject(penHandle));
        } else {
            penHandle = handleManager.getHandle();
        }
        os.writeTag(new ExtCreatePen(penHandle, pen));
        os.writeTag(new SelectObject(penHandle));

        if (!(getStroke() instanceof BasicStroke)
                || (((BasicStroke) getStroke()).getMiterLimit() != stroke
                        .getMiterLimit())) {
            os.writeTag(new SetMiterLimit(toUnit(stroke.getMiterLimit())));
        }
    }

    private void writeBrush(Color color) throws IOException {
        if (color.equals(brushColor))
            return;
        brushColor = color;

        int brushStyle = (color.getAlpha() == 0) ? EMFConstants.BS_NULL
                : EMFConstants.BS_SOLID;

        LogBrush32 brush = new LogBrush32(brushStyle, getPrintColor(color), 0);
        if (brushHandle != 0) {
            os.writeTag(new DeleteObject(brushHandle));
        } else {
            brushHandle = handleManager.getHandle();
        }
        os.writeTag(new CreateBrushIndirect(brushHandle, brush));
        os.writeTag(new SelectObject(brushHandle));
    }

    private int toUnit(double d) {
        return (int) Math.floor(d * UNITS_PER_PIXEL * TWIPS);
    }
}
