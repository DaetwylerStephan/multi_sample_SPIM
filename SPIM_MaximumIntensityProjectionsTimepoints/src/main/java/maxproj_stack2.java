import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
/*
 * does the actual processing of the stack 
 * if raw file, loads raw file  openRaw() - replace this function for other cameras -
 * 
 */

public class maxproj_stack2 {
	
private File parentfolder;
private String parentfolderstring;
private String type="raw";
private String foldernametosave;
public int height;
public int width;


	public void setparent(String s){
		 parentfolder = new File(s);
		 parentfolderstring = s;
	}
	public void settype(String s){
		 type = s;
	}
	public void setfoldertosave(String s){
		foldernametosave = s;
		File dir = new File(foldernametosave); 
		dir.getParentFile().mkdirs();
		
	}
	

	public void makemaxproj(){
	
		String[] planelist;		
		MaximumProjector projector = new MaximumProjector(); //initialize projector	
				
				//check directory for all image files...get a list of them out (planelist)
				if(type == "raw"){
				planelist    	 =  filter_end(parentfolder.list(), ".dat");		
				for(String index:planelist){
				String path = parentfolderstring +File.separator+ index;	
				
				
				ImageProcessor ip = openRaw(path, width, height, 0, width-1, 0, height-1);
				 projector.add(ip);
				//make max projection here
			   
				}
				}
				else{
					
				planelist    	 =  filter_end(parentfolder.list(), ".tif");
				IJ.log("tif processing, length " + planelist.length) ;
				for(String index:planelist){
					String path = parentfolderstring + File.separator + index;
					ImagePlus tmpimg = IJ.openImage(path);
					projector.add(tmpimg.getProcessor());
					tmpimg.flush();
					
				}
				}
				
				
				ImageProcessor intermediate = projector.getProjection();
				ImagePlus reta = new ImagePlus("tmp", intermediate);
				IJ.save(reta, foldernametosave);
				reta.flush();
				
}
			


private static String[] filter_end(String[] in, String pattern) {
	ArrayList<String> all = new ArrayList<String>(in.length);
	for(String s : in)
		
		if(s.endsWith(pattern))
			all.add(s);
	String[] out = new String[all.size()];
	all.toArray(out);
	return out;
}
public static ImageProcessor openRaw(String path, int orgW, int orgH, int xMin, int xMax, int yMin, int yMax) {
	//if(xMin == 0 && xMax == orgW - 1 && yMin == 0 && yMax == orgH - 1)
	//	return openRaw(path, orgW, orgH);
	int ws = xMax - xMin + 1;
	int hs = yMax - yMin + 1;
	
	//we need here two arrays since fileinputstream can only read in bytes, datainputstream is OS dependent
	//construct two arrays, and byte needs to be 2times bigger to reduce it then by half
	byte[] bytes = new byte[ws * hs * 2];
	short[] pixels = new short[ws * hs];

	FileInputStream in = null;
	try {
		in = new FileInputStream(path);

		// skip the top
		int toSkip = 2 * (yMin * orgW + xMin);
		while(toSkip > 0)
			toSkip -= in.skip(toSkip);

		// read through it line by line
		int offs = 0;
		for(int r = 0; r < hs; r++) {
			// read the data
			int read = 0;
			while(read < ws)
				//in.read reads the FileInputStream in, into bytes, starting from offs+reads up to 2*ws-read;
				//it returns as variable the number of bytes it has read
				read += in.read(bytes, offs + read, 2 * ws - read);
			offs += 2 * ws;

			// skip to next line
			toSkip = 2 * (orgW - xMax - 1 + xMin);
			while(toSkip > 0)
				toSkip -= in.skip(toSkip);
		}
		in.close();
	} catch(IOException e) {
		throw new RuntimeException("Cannot load " + path, e);
	}
	
	//0xff denotes 255 (hexadezimalsystem), since bytes goes from -127 to 127. You want to convert it to 0...255
	//therefore you use the bitwise operator &
	//do this for both bytes and then but them one after the other, where << denotes a shift by 8 and then do a bitwise or operation
	for(int i = 0; i < pixels.length; i++) {
		int low  = 0xff & bytes[2 * i];
		int high = 0xff & bytes[2 * i + 1];
		pixels[i] = (short)((high << 8) | low);
	}

	return new ShortProcessor(ws, hs, pixels, null);
}


}


