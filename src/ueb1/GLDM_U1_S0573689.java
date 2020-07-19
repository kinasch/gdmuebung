package ueb1;

import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

//erste Uebung (elementare Bilderzeugung)

public class GLDM_U1_S0573689 implements PlugIn {

    final static String[] choices = {
            "Schwarzes Bild",
            "Gelbes Bild",
            "Belgische Fahne",
            "Schwarz/Weiss Verlauf",
            "Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf",
            "USA Fahne",
            "Japanische Fahne"
    };

    private String choice;

    public static void main(String args[]) {
        ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen
        ij.exitWhenQuitting(true);

        GLDM_U1_S0573689 imageGeneration = new GLDM_U1_S0573689();
        imageGeneration.run("");
    }

    public void run(String arg) {

        int width  = 566;  // Breite
        int height = 400;  // Hoehe

        // RGB-Bild erzeugen
        ImagePlus imagePlus = NewImage.createRGBImage("GLDM_U1", width, height, 1, NewImage.FILL_BLACK);
        ImageProcessor ip = imagePlus.getProcessor();

        // Arrays fuer den Zugriff auf die Pixelwerte
        int[] pixels = (int[])ip.getPixels();

        dialog();

        ////////////////////////////////////////////////////////////////
        // Hier bitte Ihre Aenderungen / Erweiterungen

        if ( choice.equals("Schwarzes Bild") ) {
            generateBlackImage(width, height, pixels);
        }
        if ( choice.equals("Gelbes Bild") ) {
            generateYellowImage(width, height, pixels);
        }
        if ( choice.equals("Belgische Fahne") ) {
            generateBelgischeFlagge(width, height, pixels);
        }
        if ( choice.equals("Schwarz/Weiss Verlauf") ) {
            generateSWVerlauf(width, height, pixels);
        }
        if ( choice.equals("Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf") ) {
            generateRSBVerlauf(width, height, pixels);
        }
        if ( choice.equals("USA Fahne") ) {
            generateUSAFlag(width, height, pixels);
        }
        if ( choice.equals("Japanische Fahne") ) {
            generateJapFlag(width, height, pixels);
        }


        ////////////////////////////////////////////////////////////////////

        // neues Bild anzeigen
        imagePlus.show();
        imagePlus.updateAndDraw();
    }

    private void generateBlackImage(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                int r = 0;
                int g = 0;
                int b = 0;

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    private void generateYellowImage(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                int r = 255;
                int g = 255;
                int b = 0;

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }
    private void generateBelgischeFlagge(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen
                int r = 0;
                int g = 0;
                int b = 0;
                if(x<=(width/3)) { // Schauen ob ImageProcessing gerade im 1. Drittel
                    r = 0;
                    g = 0;
                    b = 0;
                } if(x>(width/3) && x<((width/3)*2)){ // Schauen ob ImageProcessing gerade im 2. Drittel
                    r = 255;
                    g = 255;
                    b = 0;
                } if(x>=((width/3)*2)){ // Schauen ob ImageProcessing gerade im 3. Drittel
                    r = 255;
                    g = 0;
                    b = 0;
                }
                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    private void generateSWVerlauf(int width, int height, int[] pixels) {
        // Variabel um Rundungsfehler zu vermeiden
        int rundungsBeheber=1000000;
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen
                // Schwarz-Weiß-Verlauf durch erhoehen jedes Farbanteils gleichmaessig
                int rgb = (256);

                // Nach erreichen des hoechstmoeglichsten Farbwert pro Farbanteil bleibt Bleiben die Pixel bis Bildende komplett weiß

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (rgb << 16) | (rgb << 8) |  rgb;
            }
        }
    }

    private void generateRSBVerlauf(int width, int height, int[] pixels) {
        // Variabel um Rundungsfehler zu vermeiden
        int rundungsBeheber=1000000;
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schwarz-Blau-Verlauf Vertikal durch erhoehen des blauen Anteils in der for-Schleife fuer die Hoehe
            int b=((y*rundungsBeheber)/((height*rundungsBeheber)/256));
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen
                // Schwarz-Rot-Verlauf Vertikal durch erhoehen des roten Anteils in der for-Schleife fuer die Breite
                int r = ((x*rundungsBeheber)/((width*rundungsBeheber)/256));
                int g = 0;
                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    private void generateUSAFlag(int width, int height, int[] pixels) {
        // rote Streifen, weisse Streifen
        for(int i=1;i<=13;i++){
            // Schleife ueber die y-Werte
            for (int y=((height*(i-1))/13); y<((height*i)/13); y++) {
                // Schleife ueber die x-Werte
                for (int x=0; x<width; x++) {
                    int pos = y*width + x; // Arrayposition bestimmen

                    int r = 255;
                    int g = 0;
                    int b = 0;
                    if(i%2==0){
                        g=255;b=255;
                    }

                    // Werte zurueckschreiben
                    pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
                }
            }
        }

        // blauer Kasten
        // Schleife ueber die y-Werte
        for (int y=0; y<((height*7)/13); y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<(width/2); x++) {
                int pos = y*width + x; // Arrayposition bestimmen

                int r = 0;
                int g = 0;
                int b = 255;

                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }

    private void generateJapFlag(int width, int height, int[] pixels) {
        // Schleife ueber die y-Werte
        for (int y=0; y<height; y++) {
            // Schleife ueber die x-Werte
            for (int x=0; x<width; x++) {
                int pos = y*width + x; // Arrayposition bestimmen
                // weißer Hintergrund
                int r = 255;
                int g = 255;
                int b = 255;

                // Abfrage ob Pixel ein Teil der Kreisgleichung mit dem Radius 120 ist oder ob der Pixel innerhalb dieses Kreis liegt
                if(Math.pow(120,2)>=(Math.pow((x-(width/2)),2)+Math.pow((y-(height/2)),2))){
                    r=255;g=0;b=0;
                }


                // Werte zurueckschreiben
                pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
            }
        }
    }



    private void dialog() {
        // Dialog fuer Auswahl der Bilderzeugung
        GenericDialog gd = new GenericDialog("Bildart");

        gd.addChoice("Bildtyp", choices, choices[0]);


        gd.showDialog();	// generiere Eingabefenster

        choice = gd.getNextChoice(); // Auswahl uebernehmen

        if (gd.wasCanceled())
            System.exit(0);
    }
}

