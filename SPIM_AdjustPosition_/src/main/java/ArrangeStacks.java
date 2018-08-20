import java.awt.Color;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;


public class ArrangeStacks {
public int[] registrationinfo = new int[3];
public double rotationvalue = 0;
private Color[] colorset = {Color.magenta, Color.cyan};;


	public void getregistrationinfo(int movehorizontal, int movevertical, double rotateClockwise, int movezposition){
		registrationinfo[0] = movehorizontal;
		registrationinfo[1] = movevertical;
		registrationinfo[2] = movezposition;
		rotationvalue = rotateClockwise;
	}
	
	public void setColorScheme(Color[] incolor){
		this.colorset = incolor;
	}
	
	public void alignstack(ImageStack ipstack1, ImageStack ipstack2){
		int maxheight = Math.max(ipstack1.getHeight(), ipstack2.getHeight());
		int maxwidth = Math.max(ipstack1.getWidth(), ipstack2.getWidth());
		int stacksizex = maxwidth+Math.abs(registrationinfo[0]);
		int stacksizey = maxheight+Math.abs(registrationinfo[1]);
		ImageStack newip = new ImageStack(stacksizex, stacksizey);
		ImageStack newip2 = new ImageStack(stacksizex, stacksizey);
		
		System.out.println(rotationvalue);
		
		for(int i = 1;i<=ipstack2.getSize();i++){
			ipstack2.getProcessor(i).setInterpolationMethod(2);
			ipstack2.getProcessor(i).rotate(rotationvalue);
		
		}
		
		
		//ImagePlus ret2a1 = new ImagePlus("t", ipstack1);
		//ret2a1.show();
		//ImagePlus re2ta2 = new ImagePlus("t", ipstack2);
		//re2ta2.show();
		
		if(registrationinfo[0]<0){
			if(registrationinfo[1]<0){
				for(int i = 1;i<=ipstack2.getSize();i++){
					ImageProcessor currentip = new ShortProcessor(maxwidth+Math.abs(registrationinfo[0]), maxheight+Math.abs(registrationinfo[1]));
					ImageProcessor currentip2 = new ShortProcessor(maxwidth+Math.abs(registrationinfo[0]), maxheight+Math.abs(registrationinfo[1]));
					newip.addSlice(currentip);
					newip2.addSlice(currentip2);
				(newip.getProcessor(i)).insert(ipstack1.getProcessor(i),Math.abs(registrationinfo[0]),Math.abs(registrationinfo[1]));
				newip2.getProcessor(i).insert(ipstack2.getProcessor(i), 0, 0);
				}
				
			}else{
				for(int i = 1;i<=ipstack2.getSize();i++){
					ImageProcessor currentip = new ShortProcessor(maxwidth+Math.abs(registrationinfo[0]), maxheight+Math.abs(registrationinfo[1]));
					ImageProcessor currentip2 = new ShortProcessor(maxwidth+Math.abs(registrationinfo[0]), maxheight+Math.abs(registrationinfo[1]));
					newip.addSlice(currentip);
					newip2.addSlice(currentip2);
				newip.getProcessor(i).insert(ipstack1.getProcessor(i),Math.abs(registrationinfo[0]),0);
				newip2.getProcessor(i).insert(ipstack2.getProcessor(i), 0, Math.abs(registrationinfo[1]));
				}
			}
		}
		else{
			if(registrationinfo[1]<0){
				for(int i = 1;i<=ipstack2.getSize();i++){
					ImageProcessor currentip = new ShortProcessor(maxwidth+Math.abs(registrationinfo[0]), maxheight+Math.abs(registrationinfo[1]));
					ImageProcessor currentip2 = new ShortProcessor(maxwidth+Math.abs(registrationinfo[0]), maxheight+Math.abs(registrationinfo[1]));
					newip.addSlice(currentip);
					newip2.addSlice(currentip2);
				newip.getProcessor(i).insert(ipstack1.getProcessor(i),0, Math.abs(registrationinfo[1]));
				newip2.getProcessor(i).insert(ipstack2.getProcessor(i), Math.abs(registrationinfo[0]), 0);
				}
				
			}else{
				for(int i = 1;i<=ipstack2.getSize();i++){
				ImageProcessor currentip = new ShortProcessor(maxwidth+Math.abs(registrationinfo[0]), maxheight+Math.abs(registrationinfo[1]));
				ImageProcessor currentip2 = new ShortProcessor(maxwidth+Math.abs(registrationinfo[0]), maxheight+Math.abs(registrationinfo[1]));
				newip.addSlice(currentip);
				newip2.addSlice(currentip2);
				newip.getProcessor(i).insert(ipstack1.getProcessor(i),0,0);
				newip2.getProcessor(i).insert(ipstack2.getProcessor(i), Math.abs(registrationinfo[0]), Math.abs(registrationinfo[1]));
				}
			}	
		}
		
		if(registrationinfo[2] > 0){
			ImageProcessor emptyslice = new ShortProcessor(stacksizex, stacksizey);
			for(int i = 0;i< Math.abs(registrationinfo[2]);i++ ){
			newip.addSlice("label",emptyslice,0);
			newip2.addSlice(emptyslice);
			}
		} else{
			ImageProcessor emptyslice = new ShortProcessor(stacksizex, stacksizey);
			for(int i = 0;i< Math.abs(registrationinfo[2]); i++ ){
				newip2.addSlice("label",emptyslice,0);
				newip.addSlice(emptyslice);
			}
		}
		
		Display_TwoColors display = new Display_TwoColors();
		display.setcolor(colorset);
		
		display.generateOverlay(new ImagePlus("tit", newip), new ImagePlus("tit", newip2));
		
	}
	public void cutstack(ImageProcessor ip1, ImageProcessor ip2){
		int maxheight = Math.max(ip1.getHeight(), ip2.getHeight());
		int maxwidth = Math.max(ip1.getWidth(), ip2.getWidth());
		ImageProcessor newip = new ShortProcessor(maxwidth+Math.abs(registrationinfo[0]), maxheight+Math.abs(registrationinfo[1]));
		ImageProcessor newip2 = new ShortProcessor(maxwidth+Math.abs(registrationinfo[0]), maxheight+Math.abs(registrationinfo[1]));
		
		System.out.println(rotationvalue);
		ip2.setInterpolationMethod(2);
		ip2.rotate(rotationvalue);
		if(registrationinfo[0]<0){
			if(registrationinfo[1]<0){
				newip.insert(ip1,Math.abs(registrationinfo[0]),Math.abs(registrationinfo[1]));
				newip2.insert(ip2, 0, 0);
				
				
			}else{
				newip.insert(ip1,Math.abs(registrationinfo[0]),0);
				newip2.insert(ip2, 0, Math.abs(registrationinfo[1]));
				
			}
		}
		else{
			if(registrationinfo[1]<0){
				newip.insert(ip1,0, Math.abs(registrationinfo[1]));
				newip2.insert(ip2, Math.abs(registrationinfo[0]), 0);
				
			}else{
				newip.insert(ip1,0,0);
				newip2.insert(ip2, Math.abs(registrationinfo[0]), Math.abs(registrationinfo[1]));
			}	
		}
		
		
		Display_TwoColors display = new Display_TwoColors();
		display.setcolor(colorset);
		display.generateOverlay(new ImagePlus("tit", newip), new ImagePlus("tit", newip2));
	}
		

}

