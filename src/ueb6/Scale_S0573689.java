package ueb6;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ueb5.GRDM_U5_S0573689;


public class Scale_S0573689 implements PlugInFilter {

    public int setup(String arg, ImagePlus imp) {
        if (arg.equals("about"))
        {showabout(); return DONE;}
        return DOES_RGB+NO_CHANGES;
        // kann RGB-Bilder und veraendert das Original nicht
    }

    ImagePlus imp;

    public static void main(String args[]) {

        IJ.open("D:\\Janik\\Documents\\Uni\\2tesSem_SS20\\gdm\\uebung\\src\\ueb6\\component.jpg");
        //IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

        Scale_S0573689 pw = new Scale_S0573689();
        pw.imp = IJ.getImage();
        ImageProcessor ip = pw.imp.getProcessor();
        pw.run(ip);
    }

    public void run(ImageProcessor ip) {

        String[] dropdownmenue = {"Kopie", "Pixelwiederholung", "Bilinear"};

        GenericDialog gd = new GenericDialog("scale");
        gd.addChoice("Methode",dropdownmenue,dropdownmenue[0]);
        gd.addNumericField("Hoehe:",230,0);
        gd.addNumericField("Breite:",250,0);

        gd.showDialog();

        String methode = gd.getNextChoice();

        int height_n = (int)gd.getNextNumber(); // _n fuer das neue skalierte Bild
        int width_n =  (int)gd.getNextNumber();

        int width  = ip.getWidth();  // Breite bestimmen
        int height = ip.getHeight(); // Hoehe bestimmen

        double ratioHoehe = (double)height_n/(double)height;
        double ratioBreite = (double)width_n/(double)width;
        double ratio = 0;
        if (ratioHoehe > ratioBreite){
            ratio = ratioHoehe;
        } else{
            ratio = ratioBreite;
        }

        //height_n = height;
        //width_n  = width;

        ImagePlus neu = NewImage.createRGBImage("Skaliertes Bild",
                width_n, height_n, 1, NewImage.FILL_BLACK);

        ImageProcessor ip_n = neu.getProcessor();

        int[] pix = (int[])ip.getPixels();
        int[] pix_n = (int[])ip_n.getPixels();

        // Schleife ueber das neue Bild
        for (int y_n=0; y_n<height_n; y_n++) {
            for (int x_n=0; x_n<width_n; x_n++) {
                int y = y_n;
                int x = x_n;

                if(methode.equals("Kopie")) {
                    if (y < height && x < width) {
                        int pos_n = y_n * width_n + x_n;
                        int pos = y * width + x;

                        pix_n[pos_n] = pix[pos];
                    }
                }

                if(methode.equals("Pixelwiederholung")) {
                    if (y < (height-1)*ratio && x < (width-1)*ratio) {
                        int pos_n = y_n * width_n + x_n;
                        int pos = (int)(y_n/ratio) * width + (int)(x_n/ratio);

                        double v = (double)(y_n/ratioHoehe);
                        double h = (double)(x_n/ratioBreite);

                        if(h<0.5 && v<0.5) pos = (int)((y_n)/ratioHoehe) * width + (int)((x_n)/ratioBreite);
                        if(h>=0.5 && v<0.5) pos = (int)((y_n)/ratioHoehe) * width + (int)((x_n+1)/ratioBreite);
                        if(h<0.5 && v>=0.5) pos = (int)((y_n+1)/ratioHoehe) * width + (int)((x_n)/ratioBreite);
                        if(h>=0.5 && v>=0.5) pos = (int)((y_n+1)/ratioHoehe) * width + (int)((x_n+1)/ratioBreite);

                        pix_n[pos_n] = pix[pos];
                    }
                }

                if(methode.equals("Bilinear")) {
                    // Randbehandlung, ein wenig grob, aber es funktioniert
                    if (y < (height-1)*ratio && x < (width-1)*ratio) {
                        int pos_n = y_n * width_n + x_n;
                        int pos = (int)(y_n/ratio) * width + (int)(x_n/ratio);

                        double v = (double)(y_n/ratioHoehe) % 1;
                        double h = (double)(x_n/ratioBreite) % 1;


                        // ungünstige Rundung
                        /*int a = pix[pos];
                        int bb = pix[pos+1];
                        int c = pix[pos+width];
                        int d = pix[pos+width+1];
                        int rgb = (int)((a*(1-h)*(1-v))+(bb*(h)*(1-v))+(c*(1-h)*(v))+(d*(h)*(v)));


                        int r = (rgb >> 16) & 0xff;
                        int g = (rgb >> 8) & 0xff;
                        int b = (rgb) & 0xff;
*/

                        // bessere aufnahme der werte
                        /** WICHTIG!   Dieser Code ist angelehnt an
                         *
                         * https://github.com/judithekoch/gdm/blob/master/GLDM_S0540826/u6/Scale_S0540826.java
                         *
                         * **/

                        int a = pix[pos];
                        double ra = (a >> 16) & 0xff;
                        double ga = (a >> 8) & 0xff;
                        double ba = a & 0xff;

                        int bb = pix[pos + 1];
                        double rb = (bb >> 16) & 0xff;
                        double gb = (bb >> 8) & 0xff;
                        double bbb = bb & 0xff;

                        int c = pix[pos + width];
                        double rc = (c >> 16) & 0xff;
                        double gc = (c >> 8) & 0xff;
                        double bc = c & 0xff;

                        int d = pix[pos + width + 1];
                        double rd = (d >> 16) & 0xff;
                        double gdd = (d >> 8) & 0xff;
                        double bd = d & 0xff;

                        int r = (int)(ra*(1-h)*(1-v) + rb*h*(1-v) + rc*(1-h)*v + rd*h*v);
                        int g = (int)(ga*(1-h)*(1-v) + gb*h*(1-v) + gc*(1-h)*v + gdd*h*v);
                        int b = (int)(ba*(1-h)*(1-v) + bbb*h*(1-v) + bc*(1-h)*v + bd*h*v);

                        if (r < 0)
                            r = 0;
                        else if (r > 255)
                            r = 255;
                        if (g < 0)
                            g = 0;
                        else if (g > 255)
                            g = 255;
                        if (b < 0)
                            b = 0;
                        else if (b > 255)
                            b = 255;

                        pix_n[pos_n] = (0xFF << 24) | (r << 16) | (g << 8) | b;


                    }
                }
            }
        }


        // neues Bild anzeigen
        neu.show();
        neu.updateAndDraw();
    }

    void showabout() {
        IJ.showMessage("");
    }
}

