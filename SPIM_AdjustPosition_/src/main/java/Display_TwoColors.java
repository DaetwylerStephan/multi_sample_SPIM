import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ij.process.LUT;

import java.awt.Color;



public class Display_TwoColors {
private Color[] colorset = {Color.magenta, Color.cyan};

public void setcolor(Color[] colorset){
	this.colorset = colorset;
}

public CompositeImage generateOverlay(ImagePlus imp1, ImagePlus imp2){	
	
	if(imp1.getStackSize()<2){
	Color[] colors = new Color[2];
	colors[0]=colorset[0];
	colors[1]=colorset[1];
    
	ImageStack stack2 = new ImageStack(imp1.getWidth(), imp1.getHeight());
	
	
	stack2.addSlice(imp1.getProcessor());
    stack2.addSlice(imp2.getProcessor());
     
    ImagePlus imp3 = new ImagePlus("title", stack2);
    imp3.setDimensions(2, 1, 1);
    imp3 = new CompositeImage(imp3, IJ.COMPOSITE);
  
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
    return (CompositeImage) imp3;}
	else{
		IJ.log("Overlay is generated");
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
	    return (CompositeImage) imp3;
	}
}

public CompositeImage UpdateImage(CompositeImage img, ImageProcessor ip){
	//img.setPosition(2, 1, 1);
	img.setProcessor(ip);
	return img;
}
}
