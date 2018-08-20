

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.IndexColorModel;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static java.nio.file.StandardCopyOption.*;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import fiji.util.gui.GenericDialogPlus;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.integer.UnsignedShortType;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.models.InvertibleBoundable;
import mpicbg.models.TranslationModel2D;
import mpicbg.models.TranslationModel3D;
import mpicbg.stitching.StitchingParameters;
import ij.CompositeImage;
import ij.IJ;
import ij.ImageJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.GenericDialog;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.gui.Roi;
import ij.io.Opener;
import ij.plugin.Colors;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.process.LUT;
import ij.process.ShortProcessor;



public class SPIM_AdjustPosition_ implements PlugIn, KeyListener, ImageListener {
	
	private ImageWindow win;
	private ImageCanvas canvas;
	private int movehorizontal = 0;
	private int movevertical = 0;
	private int movezplane = 0;
	private double rotateClockwise =0;
	private double rotateincrement = 0.2;
	private int imagePlane2;
	private CompositeImage finalimage;
	private Roi insertframe;
	private ImagePlus shiftimage;
	private Display_TwoColors display = new Display_TwoColors();
	private String writetofilename;
	private ImagePlus originalimage1;
	private ImagePlus originalimage2;
	private String firstimage;
	private String secondimage;
	private Color[] colorset;
	private ImagePlus displayimage1;
	private ImagePlus displayimage2;
	
	/**
	 * @param args
	 */
	@Override
	public void run(String args) {
		
		//generate an object of GenericDialog class to which options can be added and which can be evaluated
		final GenericDialogPlus gd = new GenericDialogPlus( "Indicate folder" );
		
		//generate options for the Plugin to work on SPIM data from the multi-sample SPIM or any TIFF stack
		gd.addStringField( "First Image", "D:\\adjust_timepoints\\GFP.tif", 50);
		gd.addStringField("Second Image","D:\\adjust_timepoints\\RFP.tif" ,50);
		gd.addChoice("Color:", Colors.colors, Colors.colors[4]);
		gd.addChoice("Second color:", Colors.colors, Colors.colors[3]);
	       
		gd.addFileField("Save file to","L:\\vascular_data\\180112_vasculatureMovie\\registration" ,50);
		   
		gd.addStringField("Set imageplane_1","100", 50);
		gd.addStringField("Set imageplane_2","100", 50);
		gd.addNumericField("Maximum expected displacement", 1.5,3);//ensuring that files of different sizes could be aligned
		gd.addCheckbox("Flip 2nd image horizontally", false);
		gd.showDialog();
		
		
		if ( gd.wasCanceled() )
			return;
		
		// read in input from plugin interface
		this.firstimage = gd.getNextString();
		this.secondimage = gd.getNextString();
		String firstcolor = gd.getNextChoice();
		String secondcolor = gd.getNextChoice();
		Color[] colorsetin = {Colors.getColor(firstcolor, Color.magenta), Colors.getColor(secondcolor, Color.cyan)};
		this.colorset = colorsetin;
		display.setcolor(colorset);
			
		this.writetofilename = gd.getNextString();
		int imagePlane1 = Integer.parseInt(gd.getNextString());
		this.imagePlane2 = Integer.parseInt(gd.getNextString());
		double displacementfactor = gd.getNextNumber();
		Boolean flip_it = gd.getNextBoolean();
		
		this.movezplane = imagePlane2 - imagePlane1;
		
		ImagePlus imp = new ImagePlus();
		ImagePlus imp2 = new ImagePlus();
		
		if(firstimage.endsWith(".tif")){
			imp = IJ.openImage(firstimage, imagePlane1);
			}else{
			imp = openStackPlane(firstimage,imagePlane1);
		}
		
		if(secondimage.endsWith(".tif")){
			imp2 = IJ.openImage(secondimage, imagePlane2);
			}else{
			imp2 = openStackPlane(secondimage,imagePlane2);		
		}
		
		if(flip_it==true){
			imp2.getProcessor().flipHorizontal();
		}
		
		this.shiftimage = imp2;
		this.originalimage1 = imp.duplicate();
		this.originalimage2 = imp2.duplicate();
		
		int w = Math.max(imp.getWidth(),imp2.getWidth());
	    int h = Math.max(imp.getHeight(), imp2.getHeight());
	    
	    //-------make sure that also images of different sizes can be fit. Insert "displacementfactor" and insert images into a larger stack.
	   	insertframe = new Roi((int) Math.ceil(w*(displacementfactor-1)/2), (int) Math.ceil(h*(displacementfactor-1)/2), imp2.getWidth(),imp2.getHeight());
	    if(displacementfactor<1){
	    	IJ.log("displacement factor needs to be larger than 1.");
	    	return;
	    }
	    IJ.log(String.valueOf((int) Math.ceil(w*(displacementfactor-1)/2)));
	 	  
	    
	    ImageProcessor ip1 = new ShortProcessor((int) Math.ceil(displacementfactor*w), (int) Math.ceil(displacementfactor*h));  
	    ImageProcessor ip2 = new ShortProcessor((int) Math.ceil(displacementfactor*w), (int) Math.ceil(displacementfactor*h)); 
	    ip1.insert(imp.getProcessor(),(int) Math.ceil(w*(displacementfactor-1)/2), (int) Math.ceil(h*(displacementfactor-1)/2));
	    ip2.insert(imp2.getProcessor(),(int) Math.ceil(w*(displacementfactor-1)/2), (int) Math.ceil(h*(displacementfactor-1)/2));
	    
		//DisplayColors
	    this.finalimage = display.generateOverlay(new ImagePlus("title1", ip1), new ImagePlus("title1", ip2));
		finalimage.show();
		
		//---------add the tools so that the keys have special functions for this plugin. 
		win = finalimage.getWindow();
		canvas = win.getCanvas();
		win.removeKeyListener(IJ.getInstance());
		canvas.removeKeyListener(IJ.getInstance());
	
		win.addKeyListener(this);
		canvas.addKeyListener(this);
		ImagePlus.addImageListener(this);
		IJ.log("addKeyListener");
		
		
		
		//----------display tool list
		 final GenericDialog gd2 = new GenericDialog( "Options" );
		gd2.addMessage("The following keys can be used to align the two images: "
					+ "\n \"s\" and  \"w\" move vertically "
					+ "\n  \"a\" and  \"d\" move horizontally"
					+ " \n \"x\" and  \"z\" change plane number"
					+ " \n \"e\" and  \"r\" rotate image "
					+ " \n \"o\" overlay only the plane you look at and display it"
					+ " \n \"i\" overlay full stack and display it"
					+ " \n \"p\" save registration parameters to " + writetofilename);
			gd2.setModal(false);
			gd2.showDialog();
		
		///---start to open images now
			if(firstimage.endsWith(".tif")){
				this.displayimage1 = IJ.openImage(firstimage);
			}else{
				this.displayimage1 = openStack(firstimage);
			}
			
			if(secondimage.endsWith(".tif")){
				this.displayimage2 = IJ.openImage(secondimage);
			}else{
				this.displayimage2 = openStack(secondimage);
			}
        			
	
	System.out.println("finished:)");
	}			
			
			

	
	
	private static ImagePlus openStack(String directoryname){
		
		File loadimage = new File(directoryname);
		String [] loadimagelist = loadimage.list();
		String[] loadimagelist_new= filter(loadimagelist, "(.*).tif$");
		Arrays.sort(loadimagelist_new);
		ImagePlus init = IJ.openImage(directoryname+File.separator + loadimagelist_new[0]);
		ImageStack imp = new ImageStack(init.getProcessor().getWidth(), init.getProcessor().getHeight());
		for(int i = 0; i< loadimagelist_new.length;i++){
			ImagePlus tmp = IJ.openImage(directoryname+File.separator + loadimagelist_new[i]);
			imp.addSlice(tmp.getProcessor());
		}
		ImagePlus returnimage = new ImagePlus("stack", imp);
		return returnimage;
	}

	private static ImagePlus openStackPlane(String directoryname, int nb){
		
		File loadimage = new File(directoryname);
		String [] loadimagelist = loadimage.list();Arrays.sort(loadimagelist);
		String[] loadimagelist_new= filter(loadimagelist, "(.*).tif$");Arrays.sort(loadimagelist_new);
		ImagePlus init = IJ.openImage(directoryname+File.separator + loadimagelist_new[nb]);
		return init;
	}






	public static void main(String[] args2) {
		
		
		new ImageJ();
		
		IJ.runPlugIn("SPIM_AdjustPosition_","");
		
	}
	




	@Override
	public void imageOpened(ImagePlus imp) {}


	@Override
	public void imageUpdated(ImagePlus imp) {}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyPressed(KeyEvent e) {
		        int keyCode = e.getKeyCode();
		        char keyChar = e.getKeyChar();
		        int flags = e.getModifiers();
		        IJ.log("keyPressed: keyCode=" + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")");
		        
		        if(keyCode == 83){//s
		        	this.movevertical = this.movevertical+1;
		        	ImageProcessor ip = this.finalimage.getProcessor(2);
			        
		        	ip.setRoi(insertframe);ip.setValue(0); ip.fill();
		        	ip.reset(ip.getMask());

		          	insertframe.setLocation(insertframe.getPolygon().xpoints[0], insertframe.getPolygon().ypoints[0]+1);
		        	ip.insert(shiftimage.getProcessor(), insertframe.getPolygon().xpoints[0], insertframe.getPolygon().ypoints[0]);
		        	
		        	display.UpdateImage(finalimage, ip); finalimage.show();
		        	
		        	System.out.println(insertframe.getPolygon().ypoints[0]);
		        	System.out.println(movevertical);
		        }
		        if(keyCode==87){//w
		        	this.movevertical = this.movevertical-1;
		        	ImageProcessor ip = this.finalimage.getProcessor(2);
			        
		        	//delete previous image
		        	ip.setRoi(insertframe);ip.setValue(0); ip.fill();
		        	ip.reset(ip.getMask());
		        	
		          	insertframe.setLocation(insertframe.getPolygon().xpoints[0], insertframe.getPolygon().ypoints[0]- 1);
		        	ip.insert(shiftimage.getProcessor(), insertframe.getPolygon().xpoints[0], insertframe.getPolygon().ypoints[0]);

		        	display.UpdateImage(finalimage, ip); 
		        	finalimage.show();
		        	
		        	System.out.println(insertframe.getPolygon().ypoints[0]);
		        	System.out.println(movevertical);
		        }
		        
		        if(keyCode==90){//z
		        	this.movezplane = this.movezplane-1;
		        	ImageProcessor ip = this.finalimage.getProcessor(2);
			        
		        	ip.setRoi(insertframe);ip.setValue(0); ip.fill();
		        	ip.reset(ip.getMask());
		        	
		          	insertframe.setLocation(insertframe.getPolygon().xpoints[0], insertframe.getPolygon().ypoints[0]);
		          	
		          	if(firstimage.endsWith(".tif")){
		    			shiftimage = IJ.openImage(secondimage, imagePlane2+movezplane);
		    			}else{
		    			ImagePlus imp2 = openStackPlane(secondimage,imagePlane2+movezplane);
				        shiftimage = imp2;
		    		} 	

		        	ImageProcessor ip2 = shiftimage.getProcessor();
		        	ip2.setInterpolationMethod(2);
		        	ip2.rotate(rotateClockwise);
		        	
		          	ip.insert(shiftimage.getProcessor(), insertframe.getPolygon().xpoints[0], insertframe.getPolygon().ypoints[0]);

		        	display.UpdateImage(finalimage, ip); 
		        	finalimage.show();
		        	
		        	System.out.println(insertframe.getPolygon().ypoints[0]);
		        	System.out.println(movezplane);
		        }
		        
		        if(keyCode==88){//x
		        	this.movezplane = this.movezplane+1;
		        	ImageProcessor ip = this.finalimage.getProcessor(2);
			        
		        	ip.setRoi(insertframe);ip.setValue(0); ip.fill();
		        	ip.reset(ip.getMask());
		        	
		          	insertframe.setLocation(insertframe.getPolygon().xpoints[0], insertframe.getPolygon().ypoints[0]);
		          	
		          	if(firstimage.endsWith(".tif")){
		    			shiftimage = IJ.openImage(secondimage, imagePlane2+movezplane);
		    			}else{
		    			ImagePlus imp2 = openStackPlane(secondimage,imagePlane2+movezplane);
				        shiftimage = imp2;
		    		} 	

		        	ImageProcessor ip2 = shiftimage.getProcessor();
		        	ip2.setInterpolationMethod(2);
		        	ip2.rotate(rotateClockwise);
		        	
		          	ip.insert(shiftimage.getProcessor(), insertframe.getPolygon().xpoints[0], insertframe.getPolygon().ypoints[0]);

		        	display.UpdateImage(finalimage, ip); 
		        	finalimage.show();
		        	
		        	System.out.println(insertframe.getPolygon().ypoints[0]);
		        	System.out.println(movezplane);
		        }
		        
		        if(keyCode ==65){//A
		        	movehorizontal = movehorizontal - 1;
		        	ImageProcessor ip = this.finalimage.getProcessor(2);
			        
		        	ip.setRoi(insertframe);ip.setValue(0); ip.fill();
		        	 
		          	insertframe.setLocation(insertframe.getPolygon().xpoints[0]- 1, insertframe.getPolygon().ypoints[0]);
		        	ip.insert(shiftimage.getProcessor(), insertframe.getPolygon().xpoints[0], insertframe.getPolygon().ypoints[0]);

		        	display.UpdateImage(finalimage, ip); finalimage.show();
		        	System.out.println(movehorizontal);
		        }
		        
		        if(keyCode==68){//D
		        	movehorizontal = movehorizontal + 1;
		        	ImageProcessor ip = this.finalimage.getProcessor(2);
		        
		        	ip.setRoi(insertframe);ip.setValue(0); ip.fill();
		        	 
		          	insertframe.setLocation(insertframe.getPolygon().xpoints[0]+ 1, insertframe.getPolygon().ypoints[0]);
		          	ip.insert(shiftimage.getProcessor(), insertframe.getPolygon().xpoints[0], insertframe.getPolygon().ypoints[0]);
		        	
		 
		        	display.UpdateImage(finalimage, ip); finalimage.show();
		        	System.out.println(movehorizontal);
		        	 
		        }
		        
		        if(keyCode==79){//o. The plane you are looking at
		        	ArrangeStacks arrangeit = new ArrangeStacks();
		        	arrangeit.setColorScheme(colorset);
		        	arrangeit.getregistrationinfo(movehorizontal, movevertical, rotateClockwise, movezplane);
		        	arrangeit.cutstack(originalimage1.getProcessor(), originalimage2.getProcessor());
		        }
		        
		        if(keyCode==73){//i: generate full stack
		        	ArrangeStacks arrangeit = new ArrangeStacks();
		        	arrangeit.setColorScheme(colorset);
		        	arrangeit.getregistrationinfo(movehorizontal, movevertical, rotateClockwise, movezplane);
		        	
		        	
		        	arrangeit.alignstack(this.displayimage1.getStack(), this.displayimage2.getStack());
		        	
		        	
		        }
		        if(keyCode ==69){//E
		        	rotateClockwise =rotateClockwise +0.2;
		        	
		        	ImageProcessor ip = this.finalimage.getProcessor(2);
		        	ip.setRoi(insertframe);ip.setValue(0); ip.fill();
		        	
		        	
		        	ImageProcessor ip2 = shiftimage.getProcessor().duplicate();
		        	ip2.setInterpolationMethod(2);
		        	ip2.rotate(0.2);
		        	System.out.println(ip2.getInterpolationMethod());
		         
		          	ip.insert(ip2, insertframe.getPolygon().xpoints[0], insertframe.getPolygon().ypoints[0]);
		        	this.shiftimage = new ImagePlus("test",ip2);
		        	//ImagePlus reta2= new ImagePlus("reta2", ip);
		        	//reta2.show();
		        
		        	display.UpdateImage(finalimage, ip); finalimage.show();
		        	System.out.println(rotateClockwise);
		        	
		        }
		        if(keyCode == 82 ){//R
		        	rotateClockwise =rotateClockwise -0.2;
		        	
		        	ImageProcessor ip = this.finalimage.getProcessor(2);
		        	ip.setRoi(insertframe);ip.setValue(0); ip.fill();
		        	
		        	ImageProcessor ip2 = shiftimage.getProcessor().duplicate();;
		        	ip2.setInterpolationMethod(2);
		        	ip2.rotate(-0.2);
		         
		          	ip.insert(ip2, insertframe.getPolygon().xpoints[0], insertframe.getPolygon().ypoints[0]);
		          	this.shiftimage = new ImagePlus("test",ip2);
		        	//ImagePlus reta2= new ImagePlus("reta2", ip);
		        	//reta2.show();
		        
		        	display.UpdateImage(finalimage, ip); finalimage.show();
		        	System.out.println(rotateClockwise);
		        }
		        
		        if(keyCode ==80){//P
		        	writeregistrationfile(); 
		        }
		     
		   }
	
	



		private void writeregistrationfile() {
			
			try{
				//System.out.println(writetofilename);
				
				File registerfile = new File(writetofilename);
				int filenb =-1;
				if(registerfile.exists()){filenb = registerfile.list().length;}
				else{registerfile.mkdirs();}
				
				FileWriter writer = new FileWriter(writetofilename + File.separator+ "registrationfile" + IJ.pad(filenb+1, 3) + ".txt");
				writer.write("1stimage " + firstimage + System.lineSeparator());
				writer.write("2ndimage " + secondimage + System.lineSeparator());
				writer.write("xshift " + movehorizontal);
				writer.write(" yshift " + movevertical);
				writer.write(" rotation "+ rotateClockwise);
				writer.close();
				
			}catch(IOException ex){
			IJ.log("Writing registration file did not work.");	
			}
		}



		public void rotatealignment(ImageProcessor ip, double angle){
			 	int width = ip.getWidth();
			 	int height = ip.getHeight();
			 	
				short[] pixels2 = (short[])ip.getPixelsCopy();
				short[] pixels = new short[width*height];
				
				ImageProcessor ip2 = null;
				double centerX = (width-1)/2.0;
				double centerY = (height-1)/2.0;
				int xMax = width - 1;
				
				double angleRadians = -angle/(180.0/Math.PI);
				double ca = Math.cos(angleRadians);
				double sa = Math.sin(angleRadians);
				double tmp1 = centerY*sa-centerX*ca;
				double tmp2 = -centerX*sa-centerY*ca;
				double tmp3, tmp4, xs, ys;
				int index, ixs, iys;
				double dwidth=width,dheight=height;
				double xlimit = width-1.0, xlimit2 = width-1.001;
				double ylimit = height-1.0, ylimit2 = height-1.001;
				// zero is 32768 for signed images
				int background = 0; 
				

				
				for (int y=0; y<(0 + height); y++) {
						index = y*width;
						tmp3 = tmp1 - y*sa + centerX;
						tmp4 = tmp2 + y*ca + centerY;
						for (int x=0; x<=xMax; x++) {
							xs = x*ca + tmp3;
							ys = x*sa + tmp4;
							if ((xs>=-0.01) && (xs<dwidth) && (ys>=-0.01) && (ys<dheight)) {
								
									ixs = (int)(xs+0.5);
									iys = (int)(ys+0.5);
									if (ixs>=width) ixs = width - 1;
									if (iys>=height) iys = height -1;
									pixels[index++] = pixels2[width*iys+ixs];
								
						}
					}
				
				
		}ip.setPixels(pixels);}



	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}





	private static String[] filter(String[] in, String pattern) {
		ArrayList<String> all = new ArrayList<String>(in.length);
		for(String s : in)
			if(s.matches(pattern))
				all.add(s);
		String[] out = new String[all.size()];
		all.toArray(out);
		return out;
	}








	@Override
	public void imageClosed(ImagePlus imp) {
		// TODO Auto-generated method stub
		
	}

}
