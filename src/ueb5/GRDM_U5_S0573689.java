package ueb5;

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
public class GRDM_U5_S0573689 implements PlugIn {

    ImagePlus imp; // ImagePlus object
    private int[] origPixels;
    private int width;
    private int height;

    String[] items = {"Original", "Weichzeichner", "Hochpassfilter", "Scharfe Kanten"};


    public static void main(String args[]) {

        IJ.open("D:\\Janik\\Documents\\Uni\\2tesSem_SS20\\gdm\\uebung\\src\\ueb5\\sail.jpg");
        //IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

        GRDM_U5_S0573689 pw = new GRDM_U5_S0573689();
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

            if (method.equals("Weichzeichner")) {
                for (int y=0; y<height; y++) {
                    for (int x=1; x<width-1; x++) {
                        int pos = y*width + x;

                        int limit=9;
                        int b=0;
                        if(y==0){
                            b=3;
                        } else if(y==height-1){
                            limit=6;
                        }

                        int argb[] = new int[limit-b];

                        int rn = 0;
                        int gn = 0;
                        int bn = 0;

                        for(int p = b;p<argb.length;p++) {
                            if(p<3) {
                                argb[p] = origPixels[(pos-width)+(p-1)];
                            }
                            if(p>2&&p<6) {
                                argb[p] = origPixels[(pos)+(p-4)];
                            }
                            if(p>5) {
                                argb[p] = origPixels[(pos+width)+(p-7)];
                            }
                            rn += ((argb[p] >> 16) & 0xff);
                            gn += ((argb[p] >> 8) & 0xff);
                            bn += (argb[p] & 0xff);

                        }

                        rn = rn/(limit-b);
                        gn = gn/(limit-b);
                        bn = bn/(limit-b);

                        pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
                    }
                }

            }

            if (method.equals("Hochpassfilter")) {
                for (int y=1; y<height-1; y++) {
                    for (int x=1; x<width-1; x++) {
                        int pos = y*width + x;
                        int argb[] = new int[9];
                        int rnn,gnn,bnn;
                        int rn = 0;
                        int gn = 0;
                        int bn = 0;
                        for(int p = 0;p<argb.length;p++) {
                            if(p<3) {
                                argb[p] = origPixels[(pos-width)+(p-1)];
                            }
                            if(p>2&&p<6) {
                                argb[p] = origPixels[(pos)+(p-4)];
                            }
                            if(p>5) {
                                argb[p] = origPixels[(pos+width)+(p-7)];
                            }
                            rn += ((argb[p] >> 16) & 0xff);
                            gn += ((argb[p] >> 8) & 0xff);
                            bn += (argb[p] & 0xff);

                        }

                        rn = rn/9;
                        gn = gn/9;
                        bn = bn/9;
                        int placeholder = origPixels[pos];
                        rnn = ((placeholder >> 16) & 0xff)-rn+128;
                        gnn = ((placeholder >> 8) & 0xff)-gn+128;
                        bnn = ((placeholder) & 0xff)-bn+128;

                        pixels[pos] = (0xFF<<24) | (rnn<<16) | (gnn << 8) | bnn;
                    }
                }

            }


            if (method.equals("Scharfe Kanten")) {
                for (int y=1; y<height-1; y++) {
                    for (int x=1; x<width-1; x++) {
                        int pos = y*width + x;
                        int argb[] = new int[9];
                        int rnn,gnn,bnn;
                        int rn = 0;
                        int gn = 0;
                        int bn = 0;
                        for(int p = 0;p<argb.length;p++) {
                            if(p<3) {
                                argb[p] = origPixels[(pos-width)+(p-1)];
                            }
                            if(p>2&&p<6) {
                                argb[p] = origPixels[(pos)+(p-4)];
                            }
                            if(p>5) {
                                argb[p] = origPixels[(pos+width)+(p-7)];
                            }
                            rn += ((argb[p] >> 16) & 0xff);
                            gn += ((argb[p] >> 8) & 0xff);
                            bn += (argb[p] & 0xff);

                        }

                        rn = rn/9;
                        gn = gn/9;
                        bn = bn/9;
                        int placeholder = origPixels[pos];
                        rnn = ((placeholder >> 16) & 0xff)-rn+128;
                        gnn = ((placeholder >> 8) & 0xff)-gn+128;
                        bnn = ((placeholder) & 0xff)-bn+128;
                        int rnnn = ((placeholder >> 16) & 0xff)+rnn-128;
                        int gnnn = ((placeholder >> 8) & 0xff)+gnn-128;
                        int bnnn = ((placeholder) & 0xff)+bnn-128;

                        if(rnnn<0){
                            rnnn=0;
                        }
                        if(gnnn<0){
                            gnnn=0;
                        }
                        if(bnnn<0){
                            bnnn=0;
                        }
                        if(rnnn>255){
                            rnnn=255;
                        }
                        if(gnnn>255){
                            gnnn=255;
                        }
                        if(bnnn>255){
                            bnnn=255;
                        }

                        pixels[pos] = (0xFF<<24) | (rnnn<<16) | (gnnn << 8) | bnnn;
                    }
                }

            }


        }


    } // CustomWindow inner class
} 