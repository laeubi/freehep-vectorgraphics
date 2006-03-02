// AUTOMATICALLY GENERATED by FreeHEP JAVAGraphics2D

package org.freehep.graphicsio.java.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import org.freehep.graphics2d.TagString;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.test.TestingPanel;

public class TestText2D extends TestingPanel {

    public TestText2D(String[] args) throws Exception {
        super(args);
        setName("TestText2D");
    } // contructor

    public void paint(Graphics g) {
        vg[0] = VectorGraphics.create(g);
        vg[0].setCreator("FreeHEP JAVAGraphics2D");
        Paint0s0.paint(vg);
    } // paint

    private static class Paint0s0 {
        public static void paint(VectorGraphics[] vg) {
            vg[0].setColor(new Color(51, 51, 51, 255));
            vg[0].setFont(new Font("Dialog", 0, 12));
            vg[1] = (VectorGraphics)vg[0].create();
            vg[1].setClip(0, 0, 600, 600);
            vg[1].setColor(new Color(255, 255, 255, 255));
            vg[1].fillRect(0, 0, 600, 600);
            vg[1].setColor(new Color(0, 255, 0, 255));
            vg[1].drawLine(0, 30, 600, 30);
            vg[1].drawLine(0, 90, 600, 90);
            vg[1].drawLine(0, 150, 600, 150);
            vg[1].drawLine(0, 210, 600, 210);
            vg[1].drawLine(0, 270, 600, 270);
            vg[1].drawLine(0, 330, 600, 330);
            vg[1].drawLine(0, 390, 600, 390);
            vg[1].drawLine(0, 450, 600, 450);
            vg[1].drawLine(100, 0, 100, 600);
            vg[1].drawLine(300, 0, 300, 600);
            vg[1].drawLine(500, 0, 500, 600);
            vg[1].setColor(new Color(255, 0, 0, 255));
            vg[1].setFont(new Font("SansSerif", 0, 10));
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 100.0, 30.0, 3, 0);
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 300.0, 30.0, 2, 0);
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 500.0, 30.0, 1, 0);
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 100.0, 90.0, 3, 1);
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 300.0, 90.0, 2, 1);
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 500.0, 90.0, 1, 1);
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 100.0, 150.0, 3, 2);
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 300.0, 150.0, 2, 2);
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 500.0, 150.0, 1, 2);
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 100.0, 210.0, 3, 3);
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 300.0, 210.0, 2, 3);
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 500.0, 210.0, 1, 3);
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 100.0, 270.0, 3, 0, true, new Color(0, 255, 255, 255), 2.0, true, new Color(0, 0, 0, 255));
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 300.0, 270.0, 2, 0, true, new Color(0, 255, 255, 255), 2.0, true, new Color(0, 0, 0, 255));
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 500.0, 270.0, 1, 0, true, new Color(0, 255, 255, 255), 2.0, true, new Color(0, 0, 0, 255));
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 100.0, 330.0, 3, 1, false, new Color(0, 255, 255, 255), 2.0, true, new Color(0, 0, 0, 255));
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 300.0, 330.0, 2, 1, false, new Color(0, 255, 255, 255), 2.0, true, new Color(0, 0, 0, 255));
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 500.0, 330.0, 1, 1, false, new Color(0, 255, 255, 255), 2.0, true, new Color(0, 0, 0, 255));
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 100.0, 390.0, 3, 2, true, new Color(0, 255, 255, 255), 2.0, false, new Color(0, 0, 0, 255));
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 300.0, 390.0, 2, 2, true, new Color(0, 255, 255, 255), 2.0, false, new Color(0, 0, 0, 255));
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 500.0, 390.0, 1, 2, true, new Color(0, 255, 255, 255), 2.0, false, new Color(0, 0, 0, 255));
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 100.0, 450.0, 3, 3, false, new Color(0, 255, 255, 255), 2.0, false, new Color(0, 0, 0, 255));
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 300.0, 450.0, 2, 3, false, new Color(0, 255, 255, 255), 2.0, false, new Color(0, 0, 0, 255));
            vg[1].drawString(new TagString("&lt;Vector<sup><b>\\Graphics%</b></sup> &amp; Card<i><sub>)Adapter)</sub></i>&gt;"), 500.0, 450.0, 1, 3, false, new Color(0, 255, 255, 255), 2.0, false, new Color(0, 0, 0, 255));
            vg[1].dispose();
        } // paint
    } // class Paint0s0

    private VectorGraphics vg[] = new VectorGraphics[2];

    public static void main(String[] args) throws Exception {
        new TestText2D(args).runTest(600, 600);
    }
} // class