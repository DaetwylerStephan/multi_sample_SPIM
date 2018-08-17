package stitching_preibisch;



import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import preibisch_code.Model;

public class ImageCollectionElement2 
{
	final File file;
	ImagePlus imp = null;
	final int index;
	Model model;
	int dimensionality;
	boolean virtual = false;
	boolean directimageset = false;
	boolean emptyimage = false;
	
	//2d or 3d offset
	double[] offset;	
	
	//2d or 3d size if image
	int[] size;
	
	public ImageCollectionElement2( final File file, final int index )
	{
		this.file = file;
		this.index = index;		
	}
	
	public ImageCollectionElement2(final ImagePlus imp2, final int index){
		this.imp = imp2;
		this.directimageset = true;
		if ( imp2.getNSlices() == 1 )
			this.size = new int[] { imp2.getWidth(), imp2.getHeight() };
			
		else
			this.size = new int[] { imp2.getWidth(), imp2.getHeight(), imp2.getNSlices() };
		this.file = null;
		this.index = index;
	}
	
	
	
	public ImageCollectionElement2 transformtoElement(){
		ImageCollectionElement2 returnit = new ImageCollectionElement2(this.file, this.index);
		returnit.setModel(this.model);
		returnit.setDimensionality(this.dimensionality);
		returnit.setImagePlus(this.imp);
		returnit.setOffset(this.offset);
		
		
		return returnit;
	
	}
	
	

	public boolean isEmptyImage(){return this.emptyimage;}
	
	public void setOffset( final double[] offset2 ) { this.offset = offset2; }
	public double[] getOffset() { return offset; }
	public double getOffset( final int dim ) { return offset[ dim ]; }
	
	public int[] getDimensions() { return size; }
	public int getDimension( final int dim ) { return size[ dim ]; }
	
	public int getIndex() { return index; }
	
	public void setModel( final Model model ) { this.model = model; }
	public Model getModel() { return model; }

	public void setDimensionality( final int dimensionality ) { this.dimensionality = dimensionality; }
	public int getDimensionality() { return dimensionality; }
	
	public File getFile() { return file; }
	public boolean isVirtual() { return virtual; }
	
	/**
	 * Used by the multi-series stitching
	 * 
	 * @param imp - the ImagePlus of this series
	 */
	public void setImagePlus( final ImagePlus imp ) 
	{ 
		this.imp = imp; 
		
		if ( imp.getNSlices() == 1 )
			size = new int[] { imp.getWidth(), imp.getHeight() };
		else
			size = new int[] { imp.getWidth(), imp.getHeight(), imp.getNSlices() };	
	}
	
	public ImagePlus openraw(){
		ImageProcessor ip = openRaw(file.getAbsolutePath(), 960, 960, 0, 959, 0, 959);
		ImagePlus imp = new ImagePlus("test", ip);
		return imp;
		
	}
	
	public ImagePlus open( final boolean virtual )
	{	if(directimageset==true){
			return imp;
		}
		else{
	
		
		
		
		String filename = file.getAbsolutePath();
		if (filename.substring(filename.length()-4, filename.length()).equals(".dat")){
			try{
			ImageProcessor ip = openRaw(file.getAbsolutePath(), 960, 960, 0, 959, 0, 959);
			imp = new ImagePlus("test", ip);
			return imp;
			}catch(Exception filenotfound){
				ImageProcessor ip = new ShortProcessor(960, 960);
				imp = new ImagePlus("test", ip);
				this.emptyimage = true;
				return imp;
				
			}
			
			
		}
		if ( imp != null && this.isVirtual() == virtual )
		{
			return imp;
		}
		else
		{
			// TODO: Unify this image loading mechanism with the one in
			// plugin/Stitching_Grid.java. Otherwise changes to how images
			// are loaded must be made in multiple places in the code.
			if ( imp != null )
				imp.close();
			
			this.virtual = virtual;
			
			try 
			{
				if ( !file.exists() )
				{
					IJ.log( "Cannot find file: '" + file + "' - abort stitching." );
					return null;
				}
				
			
				final ImagePlus imp;
				
				
				imp = IJ.openImage(file.getAbsolutePath());
				
			
					if ( imp.getNSlices() == 1 )
						size = new int[] { imp.getWidth(), imp.getHeight() };
					else
						size = new int[] { imp.getWidth(), imp.getHeight(), imp.getNSlices() };
					
					this.imp = imp;
					return this.imp;
				}
				
			 
			catch ( Exception e ) 
			{
				IJ.log( "Cannot open file '" + file + "': " + e );
				//e.printStackTrace();
				return null;
			} 
			
		}}
	}

	public void close() 
	{
		imp.close();
		imp = null;
	}
	

	private static ImageProcessor openRaw(String path, int orgW, int orgH, int xMin, int xMax, int yMin, int yMax) {
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
