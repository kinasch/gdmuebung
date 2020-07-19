package ueb4;

import ij.*;
import ij.io.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;


public class GRDM_U4_S0573689 implements PlugInFilter {

	protected ImagePlus imp;
	final static String[] choices = {"Wischen", "Weiche Blende","Overlay(a,B)","Overlay(B,a)","Schieben", "Chroma Key", "Extra"};

	public int setup(String arg, ImagePlus imp) {
		this.imp = imp;
		return DOES_RGB+STACK_REQUIRED;
	}
	
	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen 
		ij.exitWhenQuitting(true);
		
		IJ.open("D:\\Janik\\Documents\\Uni\\2tesSem_SS20\\gdm\\uebung\\src\\ueb4\\StackB.zip");
		
		GRDM_U4_S0573689 sd = new GRDM_U4_S0573689();
		sd.imp = IJ.getImage();
		ImageProcessor B_ip = sd.imp.getProcessor();
		sd.run(B_ip);
	}

	public void run(ImageProcessor B_ip) {
		// Film B wird uebergeben
		ImageStack stack_B = imp.getStack();
		
		int length = stack_B.getSize();
		int width  = B_ip.getWidth();
		int height = B_ip.getHeight();
		
		// ermoeglicht das Laden eines Bildes / Films
		Opener o = new Opener();
		/*
		OpenDialog od_a = new OpenDialog("auswählen des 2. Filmes ...",  "");
				
		// Film a wird dazugeladen
		String dateia = od_a.getFileName();
		if (dateia == null) return; // abbruch
		String pfada = od_a.getDirectory();
		*/
		ImagePlus a = o.openImage("D:\\Janik\\Documents\\Uni\\2tesSem_SS20\\gdm\\uebung\\src\\ueb4\\Stacka.zip");
		if (a == null) return; // abbruch

		ImageProcessor a_ip = a.getProcessor();
		ImageStack stack_a  = a.getStack();

		if (a_ip.getWidth() != width || a_ip.getHeight() != height)
		{
			IJ.showMessage("Fehler", "Bildgrößen passen nicht zusammen");
			return;
		}
		
		// Neuen Film (Stack) "Erg" mit der kleineren Laenge von beiden erzeugen
		length = Math.min(length,stack_a.getSize());

		ImagePlus Erg = NewImage.createRGBImage("Ergebnis", width, height, length, NewImage.FILL_BLACK);
		ImageStack stack_Erg  = Erg.getStack();

		// Dialog fuer auswahl des Ueberlagerungsmodus
		GenericDialog gd = new GenericDialog("Überlagerung");
		gd.addChoice("Methode",choices,"");
		gd.showDialog();

		int methode = 0;		
		String s = gd.getNextChoice();
		if (s.equals("Wischen")) methode = 1;
		if (s.equals("Weiche Blende")) methode = 2;
		if (s.equals("Overlay(a,B)")) methode = 3;
		if (s.equals("Overlay(B,a)")) methode = 32;
		if (s.equals("Schieben")) methode = 4;
		if (s.equals("Chroma Key")) methode = 5;
		if (s.equals("Extra")) methode = 6;

		// arrays fuer die einzelnen Bilder
		int[] pixels_B;
		int[] pixels_a;
		int[] pixels_Erg;

		// Schleife ueber alle Bilder
		for (int z=1; z<=length; z++)
		{
			int einschub =(((z-1) * width) / 94);
			int r = ((z-1)*width)/150;
			int alpha = (z * 255)/95;
			pixels_B   = (int[]) stack_B.getPixels(z);
			pixels_a   = (int[]) stack_a.getPixels(z);
			pixels_Erg = (int[]) stack_Erg.getPixels(z);

			int pos = 0;
			for (int y=0; y<height; y++)
				for (int x=0; x<width; x++, pos++)
				{
					int ca = pixels_a[pos];
					int ra = (ca & 0xff0000) >> 16;
					int ga = (ca & 0x00ff00) >> 8;
					int ba = (ca & 0x0000ff);

					int cB = pixels_B[pos];
					int rB = (cB & 0xff0000) >> 16;
					int gB = (cB & 0x00ff00) >> 8;
					int bB = (cB & 0x0000ff);

					if (methode == 1)
					{
						//Wischen
						if (y+1 > (z-1)*(double)height/(length-1))
							pixels_Erg[pos] = pixels_B[pos];
						else
							pixels_Erg[pos] = pixels_a[pos];
					}

					if (methode == 2) {
						// Weiche Blende
						// Nutzung der Formel des Foliensatzes
						int rn,gn,bn;
						rn = ((alpha*ra)+(255-alpha)*rB)/255;
						gn = ((alpha*ga)+(255-alpha)*gB)/255;
						bn = ((alpha*ba)+(255-alpha)*bB)/255;
						pixels_Erg[pos] = 0xFF000000 + ((rn & 0xff) << 16) + ((gn & 0xff) << 8) + ( bn & 0xff);
					}

					if (methode == 3) {
						// Overlay mit B im Hintergrund: Formeln und abfragen aus dem Foliensatz
						int rn,gn,bn;
						if(rB<=128){
							rn = (ra*rB)/128;
						} else {
							rn = 255 - ((255-ra)*(255-rB)/128);
						}
						if(gB<=128){
							gn = (ga*gB)/128;
						} else {
							gn = 255 - ((255-ga)*(255-gB)/128);
						}
						if(bB<=128){
							bn = (ba*bB)/128;
						} else {
							bn = 255 - ((255-ba)*(255-bB)/128);
						}
						pixels_Erg[pos] = 0xFF000000 + ((rn & 0xff) << 16) + ((gn & 0xff) << 8) + ( bn & 0xff);
					}
					if (methode == 32) {
						// Overlay mit a im Hintergrund: Formeln und abfragen aus dem Foliensatz
						int rn,bn,gn;
						if(ra<=128){
							rn = (rB*ra)/128;
						} else {
							rn = 255 - ((255-rB)*(255-ra)/128);
						}
						if(ga<=128){
							gn = (gB*ga)/128;
						} else {
							gn = 255 - ((255-gB)*(255-ga)/128);
						}
						if(ba<=128){
							bn = (bB*ba)/128;
						} else {
							bn = 255 - ((255-bB)*(255-ba)/128);
						}
						pixels_Erg[pos] = 0xFF000000 + ((rn & 0xff) << 16) + ((gn & 0xff) << 8) + ( bn & 0xff);
					}

					if (methode == 4) {
						// Schieben
						// Einschub = (z * width) / 95;
						if(x+1 > einschub){
							pixels_Erg[pos] = pixels_B[pos-einschub];
						} else if(pos+einschub < pixels_a.length) {
							//if(pos+einschub >= pixels_a.length) einschub = pixels_a.length-pos-1;
							pixels_Erg[pos] = pixels_a[pos+einschub];
						}
					}

					if (methode == 5) {
						// Chroma Key

						// abfrage über YUV mit dieser Eingrenzung // (uu<20&&uu>-100)&&(vv<100&&vv>20)
						//int yy = (int)(0.299*ra+0.587*ga+0.114*ba);
						//int uu = (int)((ba-yy)*0.493);
						//int vv = (int)((ra-yy)*0.877);
						// Eingrenzung über RGB mit dieser Formel // ra>100 && ga>100 && ba<128

						// Ist der Pixel in a ähnlich zur Key Farbe, so wird der Pixel von B genommen
						// wenn nicht, dann wird der Pixel von a genommen
						if(ra>100 && ga>100 && ba<128){
							pixels_Erg[pos] = pixels_B[pos];
						} else{
							pixels_Erg[pos] = pixels_a[pos];
						}

						// Ich habe keine ahnung was besser ist...
					}

					if (methode == 6) {
						// Extra
						// Math.pow(r,2)>=(Math.pow((x-(width/2)),2)+Math.pow((y-(height/2)),2))
						// pos+einschub<pixels_a.length
						if(Math.pow(r,2)<=(Math.pow((x-(width/2)),2)+Math.pow((y-(height/2)),2))){
							pixels_Erg[pos] = pixels_a[pos];
						} else{
							pixels_Erg[pos] = pixels_B[pos];
						}

					}
				}
		}

		// neues Bild anzeigen
		Erg.show();
		Erg.updateAndDraw();

	}

}

