


import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.Blitter;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import preibisch_code.TranslationModel2D;
import preibisch_code.TranslationModel3D;
import stitching_preibisch.CollectionStitchingImgLib2;
import stitching_preibisch.ImageCollectionElement2;
import stitching_preibisch.ImagePlusTimePoint2;
import stitching_preibisch.StitchingParameters;

/*
 * get parameters for fusion 
 */

public class DoStitching {
	private ArrayList<Double> positionsX;
	private ArrayList<Double> positionsY;
	
	public void setTimepoints(ArrayList<String> timepo){
	}
	public ArrayList<Double> getPositionX(){return positionsX;}
	public ArrayList<Double> getPositionY(){return positionsY;} 
	public ArrayList<Double> getaveragePositionX(){return positionsX;}
	public ArrayList<Double> getaveragePositionY(){return positionsY;} 
	
	
	public void doStitching(ArrayList<Double> CalculatedPositionX_average, ArrayList<Double> CalculatedPositionY_average, StitchingParameters params, CSVimportToTileWriter3 csvinfo, String[] filenameparent, ArrayList<String> retainedplanes, ArrayList<Integer> currentsamplelist, Boolean maximumsprojection, Boolean invertIT, int nbplanes){
		ArrayList< ImageCollectionElement2> elements = new ArrayList< ImageCollectionElement2>();
		ArrayList< ImageCollectionElement2> elements3D = new ArrayList< ImageCollectionElement2>();
		ArrayList<Double> CalculatedPositionX = new ArrayList<Double>();
		ArrayList<Double> CalculatedPositionY = new ArrayList<Double>();
	
		
		ArrayList<String> planelistmax = new ArrayList<String>();
		int totalnbtimepoints =nbplanes;
		
		for(int i=0; i<Math.min(retainedplanes.size(),totalnbtimepoints); i++){
			planelistmax.add(retainedplanes.get((int) (i*retainedplanes.size()/totalnbtimepoints)));
			
		}
		
		
		if(maximumsprojection ==true){
		for(int iter = 0; iter<currentsamplelist.size();iter++){
			
		int sampleind = currentsamplelist.get(iter);		
		if (retainedplanes.size()==0){
			continue;
		}
		
			
		ImageProcessor ipMAX = null;
		for(String planeiter : planelistmax){
			String	filenameplane=filenameparent[iter]+File.separator+planeiter;	
			ImageProcessor ip;	
			try{
				ip = openRaw(filenameplane, 960, 960, 0, 959, 0, 959);
				if (invertIT == true){ip.flipHorizontal();}
				} catch (Exception e){
					continue;
				}
			if(ipMAX == null){ipMAX = ip;}
			ipMAX.copyBits(ip, 0, 0, Blitter.MAX);
		}
		
		
		ImageCollectionElement2 element = new ImageCollectionElement2(new ImagePlus("Max", ipMAX),iter);
		double[] offset = new double[ 2 ];
		offset[0]=(double) csvinfo.getPositionY()[sampleind];
		element.setOffset( offset );
		element.setDimensionality( 2 );
		element.setModel( new TranslationModel2D() );	
		
		elements.add( element );
		
		if(ipMAX == null){continue;}
		if(invertIT == true){ipMAX.flipHorizontal();}
		}

		////////////////////////////////////////////
		//Get the position information for the current angle, save them in CalculatedPositionX&Y
		////////////////////////////////////////////	
		ArrayList<ImagePlusTimePoint2> optimized= new ArrayList<ImagePlusTimePoint2>();
		try{
			optimized = CollectionStitchingImgLib2.stitchCollection( elements, params,invertIT );
		}catch(Exception e){
			IJ.log("There was a problem with the orientations of the files, e.g. invert it?");
			boolean newinvert;
			if(invertIT==true){newinvert=false;} else{newinvert=true;}
			optimized = CollectionStitchingImgLib2.stitchCollection( elements, params,newinvert );

		}
		
		if ( optimized == null ) return;

		for ( final ImageCollectionElement2 element : elements )
		{final TranslationModel2D m = (TranslationModel2D)element.getModel();
		final double[] tmp = new double[ 2 ];
		m.applyInPlace( tmp );

		CalculatedPositionX.add(tmp[0]);
		CalculatedPositionY.add(tmp[1]);
		}

		System.out.println(CalculatedPositionX.toString());
		System.out.println(CalculatedPositionY.toString());
		
		//////////////////////////////////////////////////////
		//3D
		//////////////////////////////////////////////////////
		}else{

		for(int iter = 0; iter<currentsamplelist.size();iter++){
				
		int sampleind = currentsamplelist.get(iter);		
		if (retainedplanes.size()==0){continue;}
		
		ImageStack ipSTACK = new ImageStack(960, 960);
		for(String planeiter : planelistmax){
			String	filenameplane=filenameparent[iter]+File.separator+planeiter;	
			ImageProcessor ip;	
			try{
				ip = openRaw(filenameplane, 960, 960, 0, 959, 0, 959);
				//if (invertIT == true){ip.flipHorizontal();}
				} catch (Exception e){
					continue;
				}
			ipSTACK.addSlice(ip);
			
		}
				
		ImageCollectionElement2 element3D = new ImageCollectionElement2(new ImagePlus("test", ipSTACK),iter);
		double[] offset3D = new double[ 3 ];
		offset3D[0]=(double) csvinfo.getPositionX()[sampleind];
		offset3D[1]=(double) csvinfo.getPositionY()[sampleind];
		offset3D[2]= 0;
		element3D.setOffset( offset3D );
		element3D.setDimensionality( 3 );
		element3D.setModel( new TranslationModel3D() );	
		
		elements3D.add( element3D );
		}
		
		try{
		ArrayList<ImagePlusTimePoint2> optimized3D= new ArrayList<ImagePlusTimePoint2>();
		
		params.dimensionality=3; 	
	
		optimized3D = CollectionStitchingImgLib2.stitchCollection( elements3D, params,invertIT );
    	
		if ( optimized3D == null ) return;
  
		for ( final ImageCollectionElement2 element : elements3D )
        {final TranslationModel3D m = (TranslationModel3D)element.getModel();
		 final double[] tmp = new double[ 3];
		 m.applyInPlace( tmp );

		CalculatedPositionX.add(tmp[0]);
		CalculatedPositionY.add(tmp[1]);
		
        }
		}catch(Exception e){
			if(CalculatedPositionX_average.size()>0){
			for(int iii = 0; iii<CalculatedPositionX_average.size();iii++){
				CalculatedPositionX.add(CalculatedPositionX_average.get(iii));
				CalculatedPositionY.add(CalculatedPositionY_average.get(iii));
			}
			
			IJ.log("There was a problem, copying stitching info from previous timepoint");}
			else{
				for(int iii = 0; iii<csvinfo.getPositionX().length;iii++){
					CalculatedPositionX.add((double) csvinfo.getPositionX()[iii]);
					CalculatedPositionY.add((double) csvinfo.getPositionY()[iii]);
				}	
			IJ.log("Taking motorinfo");	
			}
			
        }
		
	
		}
		positionsX = CalculatedPositionX;
		positionsY = CalculatedPositionY;
		
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

