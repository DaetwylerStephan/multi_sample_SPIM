import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import preibisch_code.InvertibleBoundable;
import preibisch_code.TranslationModel2D;
import preibisch_fusion.Fusion2;
import stitching_preibisch.ImageCollectionElement2;
import stitching_preibisch.ImagePlusTimePoint2;
import stitching_preibisch.StitchingParameters;


public class FuseItRunnable implements Runnable {

	
	public Boolean averaging;
	public Boolean invertIT;
	public Boolean maximumsprojection;
	public int eventindex;
	public int nbplanes;
	public int angleindex;
	public int timepoint_index;
	public ArrayList<Double> CalculatedPositionX;
	public ArrayList<Double> CalculatedPositionY;
	public ArrayList<Double> CalculatedPositionX_average;
	public ArrayList<Double> CalculatedPositionY_average;
	public ArrayList<Integer> currentsamplelist;
	public StitchingParameters params;
	public ArrayList<String> event_list2;
	String foldername_save;
	public String[] planelist;
	public String[] filenameparent;
	public CSVimportToTileWriter3 csvinfo;
	ArrayList<String> retainedplanes;
		
	@Override
	public void run() {
		
		
	
	//////////////////////////////////
	//go over all planes and fuse them
	///////////////////////////////////
		
	if (planelist.length==0){IJ.log("empty planelist");}
	else{
	String outputfolder = foldername_save+File.separator+event_list2.get(eventindex).substring(event_list2.get(eventindex).length()-4, event_list2.get(eventindex).length()) +  File.separator+ "angle"+ IJ.pad(angleindex, 3) + File.separator + "t" + IJ.pad(timepoint_index, 5) ;
	File outputfolderfile = new File(outputfolder);
	
	
	//If folder exists, skip rest
	if(outputfolderfile.exists()){}
	else{
	for(String planelist_iter: planelist){
	
	ArrayList< ImageCollectionElement2> elementsPLANE =new ArrayList<ImageCollectionElement2>();
	for(int iter = 0; iter<currentsamplelist.size();iter++){
		System.out.println(filenameparent[iter] + File.separator + planelist_iter);
	ImageCollectionElement2 element = new ImageCollectionElement2( new File( filenameparent[iter] + File.separator + planelist_iter), iter);
	double[] offset = new double[ 2 ];
	offset[0]=(double) CalculatedPositionX.get(iter);
	offset[1]=(double) CalculatedPositionY.get(iter);
	element.setOffset( offset );
	element.setDimensionality( 2 );
	element.setModel( new TranslationModel2D() );	
	elementsPLANE.add( element );
	}
	
		
	ArrayList<ImagePlusTimePoint2> optimized_all = new ArrayList< ImagePlusTimePoint2 >();
	for ( final ImageCollectionElement2 elementiter : elementsPLANE )
	{	
		
		final ImagePlusTimePoint2 imt = new ImagePlusTimePoint2( elementiter.open( params.virtual ), elementiter.getIndex(), 1, elementiter.getModel(), elementiter );
		
		if(elementiter.isEmptyImage()==true){params.fusionMethod = 3;}
		final TranslationModel2D model = (TranslationModel2D)imt.getModel();
		model.set( elementiter.getOffset( 0 ), elementiter.getOffset( 1 ) );

		optimized_all.add( imt );
	}
	// output the result
	for ( final ImagePlusTimePoint2 imt : optimized_all )
		IJ.log( imt.getImagePlus().getTitle() + ": " + imt.getModel() );
	

	
	
	///////////////////////////////////////////////////
	
	// first prepare the models and get the targettype
	final ArrayList<InvertibleBoundable> models = new ArrayList< InvertibleBoundable >();
	final ArrayList<ImagePlus> images = new ArrayList<ImagePlus>();
	
	boolean is32bit = false;
	boolean is16bit = false;
	boolean is8bit = false;
	
	for ( final ImagePlusTimePoint2 imt : optimized_all )
	{	
		
		ImagePlus imp = imt.getImagePlus();
		
		if ( imp.getType() == ImagePlus.GRAY32 )
			is32bit = true;
		else if ( imp.getType() == ImagePlus.GRAY16 )
			is16bit = true;
		else if ( imp.getType() == ImagePlus.GRAY8 )
			is8bit = true;
		
		if (invertIT == true){
		ImageProcessor tmpimp = imp.getProcessor();
		tmpimp.flipHorizontal();
		imp = new ImagePlus("test", tmpimp);
		}
		
		images.add( imp );
		
	}
	
	
	for ( final ImagePlusTimePoint2 imt : optimized_all )
		models.add( (InvertibleBoundable)imt.getModel() );

	ImagePlus imp = null;
	
	// test if there is no overlap between any of the tiles
	// if so fusion can be much faster
	boolean noOverlap = false;
	
	String outputdirectory = foldername_save+File.separator+event_list2.get(eventindex).substring(event_list2.get(eventindex).length()-4, event_list2.get(eventindex).length()) +  File.separator+ "angle"+ IJ.pad(angleindex, 3) + File.separator + "t" + IJ.pad(timepoint_index, 5) + File.separator+ planelist_iter.subSequence(0, planelist_iter.length()-4) +".tif";
	System.out.println(outputdirectory);
	File outp = new File(outputdirectory);
	outp.getParentFile().mkdirs();
	
	///////////////////////////////write the info to a file...
	try{
		String filenamesub = event_list2.get(eventindex).substring(event_list2.get(eventindex).length()-4, event_list2.get(eventindex).length()) + "angle"+ IJ.pad(angleindex, 3) + "t" + IJ.pad(timepoint_index, 5)  +".txt";
		
		String writetofilename = foldername_save + File.separator+"RegistrationFile" + File.separator + filenamesub ;
		File folderwriteto = new File(foldername_save + File.separator+"RegistrationFile");
		folderwriteto.mkdirs();
		
		FileWriter writer = new FileWriter(writetofilename);
		writer.write("xpos" + CalculatedPositionX.toString());
		writer.write("ypos" + CalculatedPositionY.toString());
		writer.close();
		
	}catch(IOException ex){
	IJ.log("Writing registration file did not work.");	
	}
	
	params.fusionMethod = 0;//set fusion type back to linear blending...
	
	
	if ( is32bit )
		imp = Fusion2.fuse(images, models, params.dimensionality, params.subpixelAccuracy, params.fusionMethod, params.outputDirectory, noOverlap,false,false,outputdirectory );
	else if ( is16bit )
		imp = Fusion2.fuse(images, models, params.dimensionality, params.subpixelAccuracy, params.fusionMethod, params.outputDirectory, noOverlap,false,false,outputdirectory );
	else if ( is8bit )
		imp = Fusion2.fuse(images, models, params.dimensionality, params.subpixelAccuracy, params.fusionMethod, params.outputDirectory, noOverlap ,false,false,outputdirectory);
	else
		IJ.log( "Unknown image type for fusion." );
	
	if ( imp != null )
	{
		imp.setTitle( "Fused" );
		imp.show();
	}
	
	// close all images
	for ( final ImageCollectionElement2 element : elementsPLANE )
		element.close();
	}
	
	try{
	for(int iii = 0; iii<CalculatedPositionX.size();iii++){
		CalculatedPositionX_average.set(iii, CalculatedPositionX.get(iii));
		CalculatedPositionY_average.set(iii, CalculatedPositionY.get(iii));
	}

	}catch(Exception e1){
		for(int iii = 0; iii<CalculatedPositionX.size();iii++){
			CalculatedPositionX_average.add(CalculatedPositionX.get(iii));
			CalculatedPositionY_average.add(CalculatedPositionY.get(iii));
		}
	}
			
	}	
	}	
	}

	
	
	
	
}
