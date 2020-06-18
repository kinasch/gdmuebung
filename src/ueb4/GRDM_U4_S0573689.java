package ueb4;

import ij.*;
import ij.io.*;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;


public class GRDM_U4_S0573689 implements PlugInFilter {

	protected ImagePlus imp;
	final static String[] choices = {"Wischen", "Weiche Blende","Overlay(A,B)","Overlay(B,A)","Schieben", "Chroma Key", "Extra"};

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
		OpenDialog od_A = new OpenDialog("Auswählen des 2. Filmes ...",  "");
				
		// Film A wird dazugeladen
		String dateiA = od_A.getFileName();
		if (dateiA == null) return; // Abbruch
		String pfadA = od_A.getDirectory();
		*/
		ImagePlus A = o.openImage("D:\\Janik\\Documents\\Uni\\2tesSem_SS20\\gdm\\uebung\\src\\ueb4\\StackA.zip");
		if (A == null) return; // Abbruch

		ImageProcessor A_ip = A.getProcessor();
		ImageStack stack_A  = A.getStack();

		if (A_ip.getWidth() != width || A_ip.getHeight() != height)
		{
			IJ.showMessage("Fehler", "Bildgrößen passen nicht zusammen");
			return;
		}
		
		// Neuen Film (Stack) "Erg" mit der kleineren Laenge von beiden erzeugen
		length = Math.min(length,stack_A.getSize());

		ImagePlus Erg = NewImage.createRGBImage("Ergebnis", width, height, length, NewImage.FILL_BLACK);
		ImageStack stack_Erg  = Erg.getStack();

		// Dialog fuer Auswahl des Ueberlagerungsmodus
		GenericDialog gd = new GenericDialog("Überlagerung");
		gd.addChoice("Methode",choices,"");
		gd.showDialog();

		int methode = 0;		
		String s = gd.getNextChoice();
		if (s.equals("Wischen")) methode = 1;
		if (s.equals("Weiche Blende")) methode = 2;
		if (s.equals("Overlay(A,B)")) methode = 3;
		if (s.equals("Overlay(B,A)")) methode = 32;
		if (s.equals("Schieben")) methode = 4;
		if (s.equals("Chroma Key")) methode = 5;
		if (s.equals("Extra")) methode = 6;

		// Arrays fuer die einzelnen Bilder
		int[] pixels_B;
		int[] pixels_A;
		int[] pixels_Erg;

		// Schleife ueber alle Bilder
		for (int z=1; z<=length; z++)
		{
			int alpha = (z * 255)/95;
			pixels_B   = (int[]) stack_B.getPixels(z);
			pixels_A   = (int[]) stack_A.getPixels(z);
			pixels_Erg = (int[]) stack_Erg.getPixels(z);

			int pos = 0;
			for (int y=0; y<height; y++)
				for (int x=0; x<width; x++, pos++)
				{
					int cA = pixels_A[pos];
					int rA = (cA & 0xff0000) >> 16;
					int gA = (cA & 0x00ff00) >> 8;
					int bA = (cA & 0x0000ff);

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
							pixels_Erg[pos] = pixels_A[pos];
					}


					if (methode == 2) {
						// Weiche Blende
						int rn,gn,bn;
						rn = ((alpha*rA)+(255-alpha)*rB)/255;
						gn = ((alpha*gA)+(255-alpha)*gB)/255;
						bn = ((alpha*bA)+(255-alpha)*bB)/255;
						pixels_Erg[pos] = 0xFF000000 + ((rn & 0xff) << 16) + ((gn & 0xff) << 8) + ( bn & 0xff);
					}
					if (methode == 3) {
						// Overlay
						int rn,gn,bn;
						if(rB<=128){
							rn = (rA*rB)/128;
						} else {
							rn = 255 - ((255-rA)*(255-rB)/128);
						}
						if(gB<=128){
							gn = (gA*gB)/128;
						} else {
							gn = 255 - ((255-gA)*(255-gB)/128);
						}
						if(bB<=128){
							bn = (bA*bB)/128;
						} else {
							bn = 255 - ((255-bA)*(255-bB)/128);
						}
						pixels_Erg[pos] = 0xFF000000 + ((rn & 0xff) << 16) + ((gn & 0xff) << 8) + ( bn & 0xff);
					}
					if (methode == 32) {
						// Overlay
						int rn,gn,bn;
						if(rA<=128){
							rn = (rB*rA)/128;
						} else {
							rn = 255 - ((255-rB)*(255-rA)/128);
						}
						if(gA<=128){
							gn = (gB*gA)/128;
						} else {
							gn = 255 - ((255-gB)*(255-gA)/128);
						}
						if(bA<=128){
							bn = (bB*bA)/128;
						} else {
							bn = 255 - ((255-bB)*(255-bA)/128);
						}
						pixels_Erg[pos] = 0xFF000000 + ((rn & 0xff) << 16) + ((gn & 0xff) << 8) + ( bn & 0xff);
					}
					if (methode == 4) {
						// Schieben

					}
					if (methode == 5) {
						// Chroma Key

					}
					if (methode == 6) {
						// Extra

					}
				}
		}

		// neues Bild anzeigen
		Erg.show();
		Erg.updateAndDraw();

	}

}

