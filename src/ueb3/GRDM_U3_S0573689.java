package ueb3;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 Opens an image window and adds a panel below the image
 */
public class GRDM_U3_S0573689 implements PlugIn {

    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;

    String[] items = {"Original", "Rot-Kanal","Negativ" ,"Graustufen","Binär (SW)","Binär (horizontal)","Sepia","6 Farben"};


    public static void main(String args[]) {

        IJ.open("D:\\Janik\\Documents\\Uni\\2tesSem_SS20\\gdm\\uebung\\src\\ueb3\\Bear.png");
        //IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

        GRDM_U3_S0573689 pw = new GRDM_U3_S0573689();
        pw.imp = IJ.getImage();
        pw.run("");
    }

    public void run(String arg) {
        if (imp==null)
            imp = WindowManager.getCurrentImage();
        if (imp==null) {
            return;
        }
        CustomCanvas cc = new CustomCanvas(imp);

        storePixelValues(imp.getProcessor());

        new CustomWindow(imp, cc);
    }


    private void storePixelValues(ImageProcessor ip) {
        width = ip.getWidth();
        height = ip.getHeight();

        origPixels = ((int []) ip.getPixels()).clone();
    }


    class CustomCanvas extends ImageCanvas {

        CustomCanvas(ImagePlus imp) {
            super(imp);
        }

    } // CustomCanvas inner class


    class CustomWindow extends ImageWindow implements ItemListener {

        private String method;

        CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }

        void addPanel() {
            //JPanel panel = new JPanel();
            Panel panel = new Panel();

            JComboBox cb = new JComboBox(items);
            panel.add(cb);
            cb.addItemListener(this);

            add(panel);
            pack();
        }

        public void itemStateChanged(ItemEvent evt) {

            // Get the affected item
            Object item = evt.getItem();

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                System.out.println("Selected: " + item.toString());
                method = item.toString();
                changePixelValues(imp.getProcessor());
                imp.updateAndDraw();
            }

        }


        private void changePixelValues(ImageProcessor ip) {

            // Array zum Zurückschreiben der Pixelwerte
            int[] pixels = (int[])ip.getPixels();

            if (method.equals("Original")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;

                        pixels[pos] = origPixels[pos];
                    }
                }
            }

            if (method.equals("Rot-Kanal")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        //int g = (argb >>  8) & 0xff;
                        //int b =  argb        & 0xff;

                        int rn = r;
                        int gn = 0;
                        int bn = 0;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }

            if (method.equals("Negativ")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int rn = 255-r;
                        int gn = 255-g;
                        int bn = 255-b;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }

            if (method.equals("Graustufen")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        //int rn = (int)(r*0.299);
                        //int gn = (int)(g*0.587);
                        //int bn = (int)(r*0.114);

                        int rngnbn = (r+g+b)/3;
                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF<<24) | (rngnbn<<16) | (rngnbn<<8) | rngnbn;
                    }
                }
            }

            if (method.equals("Binär (SW)")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int rngnbn = (int)(r*0.299+0.587*g+0.114*b);
                        //int gn = g;
                        //int bn = b;

                        if(rngnbn > 127){
                            rngnbn = 255;
                        } else{
                            rngnbn = 0;
                        }


                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF<<24) | (rngnbn<<16) | (rngnbn<<8) | rngnbn;
                    }
                }
            }

            if (method.equals("Binär (horizontal)")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int rn = r;
                        int gn = g;
                        int bn = b;

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }

            //Fields for 6 Farben
            int cp = 765/6;             // Ein Sechstel des Dreifachen von 255
            int cppp = (cp/3)/2;          // Hälfte des vorherigen Werts
            if (method.equals("6 Farben")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        int rn = r;
                        int gn = g;
                        int bn = b;
                        /*
                        int rgb = r+g+b;
                        if(rgb < cp*1){
                            rn = gn = bn = 0;
                        }
                        if(rgb > cp*1 && rgb < cp*2){
                             rn = 96;
                            gn = 48;
                        }if(rgb > cp*2 && rgb < cp*3){
                            rn = 150;
                            gn = 75;
                        }if(rgb > cp*3 && rgb < cp*4){
                            gn = 66;
                            bn = 150;
                        }if(rgb > cp*4 && rgb < cp*5){
                            gn = 115;
                            bn = 160;
                        }if(rgb > cp*5){
                            rn = gn = bn = 255;
                        }









                        if(r > 220 && b > 220){
                            rn = gn = bn = 255;
                        }
                        if(r < 35 && b < 35){
                            rn = gn = bn = 0;
                        }
                        if(b < 35 && r > 35 && r < 138){
                            rn = 96;
                            gn = 48;
                        }
                        if(b < 35 && r > 137){
                            rn = 150;
                            gn = 75;
                        }
                        if(r < 35 && b > 35 && b < 138){
                            gn = 66;
                            bn = 150;
                        }
                        if(r < 35 && b > 137){
                            gn = 115;
                            bn = 160;
                        }


                        // 007396 rgb(0,115,150)
                        // 004296 rgb(0,66,150)
                        */


                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }
            // Erstellung der sepiaTiefe (für Sepia)
            int sepiaTiefe = 20;
            if (method.equals("Sepia")) {

                for (int y=0; y<height; y++) {
                    for (int x=0; x<width; x++) {
                        int pos = y*width + x;
                        int argb = origPixels[pos];  // Lesen der Originalwerte

                        int r = (argb >> 16) & 0xff;
                        int g = (argb >>  8) & 0xff;
                        int b =  argb        & 0xff;

                        /* Sepia nach Microsoft
                        int rn = (int)(g*0.769+b*0.189+r*0.393);
                        int gn = (int)(r*0.349+g*0.686+b*0.168);
                        int bn = (int)(r*0.272+g*0.534+b*0.131);

                        if(rn>255) rn=255;
                        if(gn>255) gn=255;
                        if(bn>255) bn=255;
                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
                        */


                        // Graustufen
                        int rngnbn = (r+g+b)/3;
                        int rn = rngnbn;
                        int gn = rngnbn;
                        int bn = rngnbn;

                        // Anwendung der Sepiatiefe
                        rn = rn + (sepiaTiefe * 2);
                        gn = gn + (sepiaTiefe);

                        // Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden
                        if(rn>255) rn=255;
                        if(gn>255) gn=255;
                        if(bn>255) bn=255;
                        if(bn<0) bn=0;
                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
                    }
                }
            }

        }

    } // CustomWindow inner class
}

