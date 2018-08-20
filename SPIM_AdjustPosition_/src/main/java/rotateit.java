import java.awt.Color;
import java.awt.Rectangle;

import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ij.process.LUT;


public class rotateit {
	private static Color[] colorset = {Color.magenta, Color.cyan};

	
	
	public static void rotatealignment(ImageProcessor ip, double angle){
	 	Rectangle roirect = ip.getRoi();
		short[] pixels2 = (short[])ip.getPixelsCopy();
		short[] pixels = new short[ip.getWidth()*ip.getHeight()];
		
		double centerX = roirect.getX() + (roirect.getWidth()-1)/2.0;
		double centerY = roirect.getY() + (roirect.getHeight()-1)/2.0;
		int xMax = (int) (roirect.getX() + roirect.getWidth() - 1);
		
		double angleRadians = -angle/(180.0/Math.PI);
		double ca = Math.cos(angleRadians);
		double sa = Math.sin(angleRadians);
		double tmp1 = centerY*sa-centerX*ca;
		double tmp2 = -centerX*sa-centerY*ca;
		double tmp3, tmp4, xs, ys;
		int index, ixs, iys;
		double dwidth=ip.getWidth(),dheight=ip.getHeight();
		
		
	
		for (int y=(int) roirect.getY(); y<(roirect.getY() + roirect.getHeight()); y++) {
				index = (int) (y*ip.getWidth() +  roirect.getX());
				tmp3 = tmp1 - y*sa + centerX;
				tmp4 = tmp2 + y*ca + centerY;
				for (int x= (int) roirect.getX(); x<=xMax; x++) {
					xs = x*ca + tmp3;
					ys = x*sa + tmp4;
					if ((xs>=-0.01) && (xs<dwidth) && (ys>=-0.01) && (ys<dheight)) {
						
							ixs = (int)(xs+0.5);
							iys = (int)(ys+0.5);
							if (ixs>=ip.getWidth()) ixs = ip.getWidth() - 1;
							if (iys>=ip.getHeight()) iys = ip.getHeight() -1;
							pixels[index++] = pixels2[ip.getWidth()*iys+ixs];
						
						
				
			}}
		}
	}
		public static void displayIT(ImagePlus imp1, ImagePlus imp2){
	Color[] colors = new Color[2];
	colors[0]=colorset[0];
	colors[1]=colorset[1];
    ImageStack imp1stack = imp1.getStack();
    ImageStack imp2stack = imp2.getStack();
	ImageStack stack2 = new ImageStack(imp1.getWidth(), imp1.getHeight());
	for(int i = 1; i<=imp1.getStackSize();i++){
		stack2.addSlice(imp1stack.getProcessor(i));
		stack2.addSlice(imp2stack.getProcessor(i));
	}
	

    ImagePlus imp3 = new ImagePlus("title", stack2);
    imp3.setDimensions(2, imp1.getStackSize(), 1);
    imp3 = new CompositeImage(imp3, IJ.COMPOSITE);
    imp3.show();
    
    for(int c=0;c<2;c++){
      LUT lut = null;
      lut = LUT.createLutFromColor(colors[c]);
	  ImageProcessor ip = stack2.getProcessor(c+1);
      lut.min = ip.getMin();
      lut.max = ip.getMax();  
        
     ((CompositeImage)imp3).setChannelLut(lut, c+1);
     
     
    } 
    imp3.setOpenAsHyperStack(true);
    imp3.show();
 
	}
	
		public static void displayIT2(ImagePlus imp1, ImagePlus imp2){
			Color[] colors = new Color[2];
			colors[0]=colorset[0];
			colors[1]=colorset[1];
		    ImageStack imp1stack = imp1.getStack();
		    ImageStack imp2stack = imp2.getStack();
			ImageStack stack2 = new ImageStack(imp1.getWidth(), imp1.getHeight());
			for(int i = 1; i<=imp1.getStackSize();i++){
				stack2.addSlice(imp1stack.getProcessor(i));
				
			}
			for(int i = 1; i<=imp1.getStackSize();i++){
				stack2.addSlice(imp2stack.getProcessor(i));
				
			}

		    ImagePlus imp3 = new ImagePlus("title", stack2);
		    imp3.setDimensions(2, imp1.getStackSize(), 1);
		    imp3 = new CompositeImage(imp3, IJ.COMPOSITE);
		    imp3.show();
		    
		    for(int c=0;c<2;c++){
		      LUT lut = null;
		      lut = LUT.createLutFromColor(colors[c]);
			  ImageProcessor ip = stack2.getProcessor(c+1);
		      lut.min = ip.getMin();
		      lut.max = ip.getMax();  
		        
		     ((CompositeImage)imp3).setChannelLut(lut, c+1);
		     
		     
		    } 
		    imp3.setOpenAsHyperStack(true);
		    imp3.show();
		 
			}
			
	public static void main(String[] args2) {
		ImagePlus imp = IJ.openImage("D:\\adjust_timepoints\\GFPshort.tif");
		ImagePlus imp2 = IJ.openImage("D:\\adjust_timepoints\\RFPshort.tif");
		//rotatealignment(imp.getProcessor(),0);
		displayIT(imp,imp2);
		
	}
	
}
