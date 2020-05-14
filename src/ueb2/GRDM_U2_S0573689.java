package ueb2;
import ij.IJ;
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

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
*/
public class GRDM_U2_S0573689 implements PlugIn {

    ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;
	
	
    public static void main(String args[]) {
		//new ImageJ();
    	//IJ.open("/users/barthel/applications/ImageJ/_images/orchid.jpg");
    	IJ.open("D:\\Janik\\Documents\\Uni\\2tesSem_SS20\\gdm\\uebung\\src\\ueb2\\orchid.jpg");
		
		GRDM_U2_S0573689 pw = new GRDM_U2_S0573689();
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
    
    
    class CustomWindow extends ImageWindow implements ChangeListener {
         
        private JSlider jSliderBrightness;
		private JSlider jSliderContrast;
		private JSlider jSliderSaturation;
		private JSlider jSliderHue;
		private double brightness;
		private double contrast = 555.5;
		private double saturation = 666.6;
		private double hue = 777.7;

		CustomWindow(ImagePlus imp, ImageCanvas ic) {
            super(imp, ic);
            addPanel();
        }
    
        void addPanel() {
        	//JPanel panel = new JPanel();
        	Panel panel = new Panel();

            panel.setLayout(new GridLayout(4, 1));
            jSliderBrightness = makeTitledSilder("Helligkeit", -128, 128, 0);
            jSliderContrast = makeTitledSilder("Kontrast", 0, 100, 50);
            jSliderSaturation = makeTitledSilder("Saettigung",0,100,50);
            jSliderHue = makeTitledSilder("Hue",0,360,180);
            panel.add(jSliderBrightness);
            panel.add(jSliderContrast);
            panel.add(jSliderSaturation);
            panel.add(jSliderHue);
            
            add(panel);
            
            pack();
         }
      
        private JSlider makeTitledSilder(String string, int minVal, int maxVal, int val) {
		
        	JSlider slider = new JSlider(JSlider.HORIZONTAL, minVal, maxVal, val );
        	Dimension preferredSize = new Dimension(width, 50);
        	slider.setPreferredSize(preferredSize);
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(), 
					string, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
			slider.setMajorTickSpacing((maxVal - minVal)/10 );
			slider.setPaintTicks(true);
			slider.addChangeListener(this);
			
			return slider;
		}
        
        private void setSliderTitle(JSlider slider, String str) {
			TitledBorder tb = new TitledBorder(BorderFactory.createEtchedBorder(),
				str, TitledBorder.LEFT, TitledBorder.ABOVE_BOTTOM,
					new Font("Sans", Font.PLAIN, 11));
			slider.setBorder(tb);
		}

		public void stateChanged( ChangeEvent e ){
			JSlider slider = (JSlider)e.getSource();

			if (slider == jSliderBrightness) {
				brightness = slider.getValue();
				String str = "Helligkeit " + brightness; 
				setSliderTitle(jSliderBrightness, str); 
			}
			
			if (slider == jSliderContrast) {
				contrast = slider.getValue();
				if(contrast<=50){
					contrast = (contrast*0.2)/10;
				} else if(contrast>50 && contrast<60){
					contrast = ((contrast-40)/10);
				} else{
					contrast = ((contrast-50)*0.2);
				}
				String str = "Kontrast " + contrast;
				setSliderTitle(jSliderContrast, str);
			}

			if(slider == jSliderSaturation){
				saturation = slider.getValue();
				if(saturation <=50){
					saturation = (saturation*0.2)/10;
				} else if(saturation>50&&saturation<60){
					saturation = ((saturation-40)/10);
				}else{
					saturation = ((saturation-50)*0.2);
				}
				String str = "Saettigung " + saturation;
				setSliderTitle(jSliderSaturation,str);
			}

			if(slider == jSliderHue){
				hue = slider.getValue();
				hue -= 180;
				String str = "Hue " + hue;
				setSliderTitle(jSliderHue,str);
			}

			changePixelValues(imp.getProcessor());
			
			imp.updateAndDraw();
		}

		
		private void changePixelValues(ImageProcessor ip) {
			
			// Array fuer den Zugriff auf die Pixelwerte
			int[] pixels = (int[])ip.getPixels();
			
			for (int y=0; y<height; y++) {
				for (int x=0; x<width; x++) {
					int pos = y*width + x;
					int argb = origPixels[pos];  // Lesen der Originalwerte 
					
					int r = (argb >> 16) & 0xff;
					int g = (argb >>  8) & 0xff;
					int b =  argb        & 0xff;
					
					
					// Transformieren von RGB zu YUV
					int ly = (int)(0.299*r+0.587*g+0.114*b);
					int u  = (int)((b-ly)*0.493);
					int v  = (int)((r-ly)*0.877);

					// Alles wird hier veraendert
					// Brightness
					ly = (int)(ly+(brightness));

					/* Contrast REDACTED
					if(!(contrast == 555.5)) {			// Check ob Slider schon angeschoben, sonst wird der Kontrast ignoriert
						ly = (int) (ly * (contrast)+brightness);
						// u = (int) (u * (contrast));
						// v = (int) (v * (contrast));
					}*/

					// Saturation
					if(!(saturation == 666.6)) {  // Check ob Slider schon angeschoben, sonst wird die Saettigung ignoriert
						u = (int) (u * (saturation));
						v = (int) (v * (saturation));
					}

					// Hue
					if(!(hue == 777.7)) {			// Check ob Slider schon angeschoben, sonst wird die Farbrotation ignoriert
						double h = Math.toRadians(hue);
						int uVor= u;
						int vVor = v;
						// Drehung gegen Uhrzeigersinn (Vermutung)
						u = (int)((uVor*(Math.cos(h))-(vVor*Math.sin(h))));
						v = (int)((uVor*(Math.sin(h))+(vVor*Math.cos(h))));
						// Drehung im Uhrzeigersinn (Vermutung)
						// v = (int)((vVor*(Math.cos(h))-(uVor*Math.sin(h))));
						// u = (int)((vVor*(Math.sin(h))+(uVor*Math.cos(h))));
					}

					// Transformation von YUV zu RGB
					int rn = (int)(ly+(v/0.877));
					int bn = (int)(ly+(u/0.493));
					int gn = (int)((ly/0.587)-((0.299*rn)/0.587)-((0.114*bn)/0.587));

					// Contrast (Formel von https://www.dfstudios.co.uk/articles/programming/image-programming-algorithms/image-processing-algorithms-part-5-contrast-adjustment/)
					if(contrast != 555.5) {		// Check ob Slider schon angeschoben, sonst wird der Kontrast ignoriert
						rn = (int)(contrast * (rn - 127) + 127);
						gn = (int)(contrast * (gn - 127) + 127);
						bn = (int)(contrast * (bn - 127) + 127);
					}


					// Begrenzung auf den gültigen Farbbereich
					if(rn>255){rn=255;}
					if(bn>255){bn=255;}
					if(gn>255){gn=255;}
					if(rn<0){rn=0;}
					if(bn<0){bn=0;}
					if(gn<0){gn=0;}
					
					pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				}
			}
		}
		
    } // CustomWindow inner class
} 
