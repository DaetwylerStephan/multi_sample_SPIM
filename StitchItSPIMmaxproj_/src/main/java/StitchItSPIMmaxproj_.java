

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
import stitching_preibisch.CollectionStitchingImgLib2;
import preibisch_fusion.*;
import stitching_preibisch.ImageCollectionElement2;
import stitching_preibisch.ImagePlusTimePoint2;
import stitching_preibisch.StitchingParameters;
import preibisch_code.InvertibleBoundable;
import preibisch_code.TranslationModel2D;
import preibisch_code.TranslationModel3D;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Macro;
import ij.io.Opener;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;



public class StitchItSPIMmaxproj_ implements PlugIn {

	/**
	 * @param args
	 */
	@Override
	public void run(String args) {
		
		//generate an object of GenericDialog class to which options can be added and which can be evaluated
		final GenericDialogPlus gd = new GenericDialogPlus( "Indicate folder" );
			
		//generate options for the Plugin to work on SPIM data from the multi-sample SPIM
		gd.addFileField( "Foldername_to_process", "L:\\SPIM1_rawdata\\GFP\\180204_vasculatureMovietest\\", 50 );
		gd.addFileField("Foldername_to_save","L:\\SPIM1_rawdata\\GFP\\180204_vasculatureMovietest\\" ,50);
		gd.addStringField("Set_magnification","10", 30);
		gd.addStringField("Set_nb_of_timepoints","5", 30);
		gd.addCheckbox("Invert_it?", false);
		gd.addCheckbox("Use_previous_registration", false);
		
		gd.showDialog();
		
		if ( gd.wasCanceled() )
			return;
		
		// read in input from plugin interface
		String directory_name = gd.getNextString();
		String foldername_save = gd.getNextString();
		int magnification = Integer.parseInt(gd.getNextString());
		int nbtimepoints = Integer.parseInt(gd.getNextString());
		Boolean invertIT = false;
		Boolean invertIT2 = gd.getNextBoolean();
		Boolean previousreg = gd.getNextBoolean();
		
		// read in input if run as a plugin/macro on the cluster
		if(IJ.isMacro()){
			String options = Macro.getOptions();
			String[] listOptions = options.split(" ");
			String[] parsedOptions = new String[listOptions.length];
			IJ.log(options);
			for(int iter=0; iter<listOptions.length; iter++){
				String iterString = listOptions[iter];
				String[] tmplist = iterString.split("=");
				IJ.log(iterString);
				parsedOptions[iter] = tmplist[1];
				
			}
			directory_name = parsedOptions[0];
			foldername_save = parsedOptions[1];
			magnification = Integer.parseInt(parsedOptions[2]);
			nbtimepoints = Integer.parseInt(parsedOptions[3]);
			if(Integer.parseInt(parsedOptions[4])>0.1){
				invertIT2 =true;
			}else{invertIT2=false;}
			
			if(Integer.parseInt(parsedOptions[5])>0.1){
				previousreg =true;
			}else{previousreg=false;}
		}
		
		//find all timepoints to process (adapted to the data structure from the microscope that writes as
		//sample data path for a timepoint of one region (here called sample): L:\SPIM1_rawdata\GFP\experiment\maxprojections\e002\s000
		//with directory_name = L:\SPIM1_rawdata\GFP\experiment\
		
		
		//find all events in the folder...
		File directoryfolder = new File(directory_name);
		File maxprojectionfolder = new File(directory_name,"maxprojections");
		
		String[] events             =  filter(maxprojectionfolder.list(), "e\\d{3}?"); Arrays.sort(events);
		//find all samples
		if (events.length !=0) {
			for (String event_index: events){
				try{
			File eventfolder = new File(maxprojectionfolder, event_index);
			
			
			//read in stage positions so that the maxprojections regions can be parsed into the different angles.
			//save those parameters into the csvinfo class.
			
			CSVimportToTileWriter3 csvinfo = new CSVimportToTileWriter3();
			if(!new File(directoryfolder.getAbsolutePath() + File.separator+ event_index+".csv").exists()){IJ.log("Couldn't find .csv file");}
			csvinfo.setmagnification(magnification);
			csvinfo.importdata(directoryfolder.getAbsolutePath() + File.separator+ event_index+".csv");
			
			System.out.println("positionsy" + Arrays.toString(csvinfo.PositionY));
			
			
			double[] positionX = csvinfo.getPositionX();
			
			ArrayList<Integer> startpoints = csvinfo.getStartpoints();
			startpoints.add(positionX.length);
			System.out.println(startpoints.toString());
			
			String[] samplelist= eventfolder.list();
			System.out.println(eventfolder.getAbsolutePath());
			
			String[] filtersamplelist = filter(eventfolder.list(), "s\\d{3}?");
			Arrays.sort(filtersamplelist);
			
			System.out.println(Arrays.toString(filtersamplelist));
			
			int[] samplelistnb = new int[filtersamplelist.length];
			for(int i = 0; i<samplelist.length;i++){ samplelistnb[i]=Integer.parseInt(filtersamplelist[i].substring(1, 4));}
			
			
			////////////////////////////////////////////////////
			//group angles together: groupanglelist as container for different samples
			////////////////////////////////////////////////////
			
			ArrayList<ArrayList<Integer>> groupanglelist = new ArrayList<ArrayList<Integer>>();
			
			for(int i= 0;i<startpoints.size()-1;i++){groupanglelist.add(new ArrayList<Integer>());}
			
			int index=0;
			int startsamplepoint = startpoints.get(index+1);
			System.out.println("startsamplepoint. " + startsamplepoint);
			System.out.println(Arrays.toString(samplelistnb));
			for(int i:samplelistnb){
			
			if(i<startsamplepoint){
			groupanglelist.get(index).add(i);	
			}else{
			while(!(i<startsamplepoint)){
				index++;
				System.out.println(startpoints.get(index+1));
				startsamplepoint = startpoints.get(index+1);
			}
			groupanglelist.get(index).add(i);
			}	
			}
			
			//print out ordering to control
			for(int i=0; i<groupanglelist.size();i++){
				System.out.println(groupanglelist.get(i).toString());
			}
			
			////////////////////////////////////////////////////
			//generate structure
			//filenamestostitch: Array containing filenames dir/e00x/s00x
			//planelistList> List with all timepoints which are in that folder
			////////////////////////////////////////////////////

			
			for(int i=0; i<groupanglelist.size();i++){
				String[] filenamestostitch;
		
				if(!groupanglelist.get(i).isEmpty()){
					
					filenamestostitch = new String[groupanglelist.get(i).size()];
					double[] positionXtoit= new double[groupanglelist.get(i).size()];
					double[] positionYtoit= new double[groupanglelist.get(i).size()];
					ArrayList<String> retainedplanes = new ArrayList<String>();
					ArrayList<String> planelistList = new ArrayList<String>();
					
					for(int sample=0; sample<groupanglelist.get(i).size(); sample++){
						filenamestostitch[sample] =  eventfolder.getAbsolutePath()+File.separator+"s" + IJ.pad(groupanglelist.get(i).get(sample),3);
						
						positionXtoit[sample] = csvinfo.getPositionX()[groupanglelist.get(i).get(sample)]; 
						positionYtoit[sample] = csvinfo.getPositionY()[groupanglelist.get(i).get(sample)];	
						File firstsample = new File(filenamestostitch[sample]);
						String[] planelist = filter(firstsample.list(), "([^\\s]+(\\.(?i)(jpg|png|tif|tiff))$)");
						System.out.println(Arrays.toString(planelist));
						if(sample==0){
							retainedplanes=new ArrayList<String>(Arrays.asList(planelist));
							Collections.sort(retainedplanes);
							planelistList=new ArrayList<String>(Arrays.asList(planelist));
							Collections.sort(planelistList);}
						else{
							ArrayList<String>  planelistListtmp=new ArrayList<String>(Arrays.asList(planelist));
							Collections.sort(planelistListtmp);
							retainedplanes.retainAll(planelistListtmp);
							planelistList.removeAll(planelistListtmp);
							planelistList.addAll(planelistListtmp);
						
						}
						
					}
					
					Collections.sort(planelistList);
					System.out.println("a: " + retainedplanes.toString());
					System.out.println("b: " + planelistList.toString());
					System.out.println("c: " + Arrays.toString(filenamestostitch));
					
					//////////////////////////////////////////////////////////////////////////////////////////
					//special case> no stitching since only one region selected: only copy files in this case
					//////////////////////////////////////////////////////////////////////////////////////////
					
					
					
					if(groupanglelist.get(i).size()==1){
						for(String planelist_iter: retainedplanes){
							
							String outputdirectory1 = foldername_save+File.separator+ "StitchedImages"+File.separator+event_index +  File.separator+ "angle"+ IJ.pad(i, 3) + File.separator + planelist_iter;
							File mkoutputdir = new File(outputdirectory1);
							mkoutputdir.getParentFile().mkdirs();
							Opener opener = new Opener();
							ImagePlus imp = opener.openImage(filenamestostitch[0]+ File.separator + planelist_iter);
							IJ.saveAsTiff(imp, outputdirectory1);
							
							String writetofilename = foldername_save+File.separator+ "RegistrationInfoMax"+File.separator+ event_index + "angle"+ IJ.pad(i, 3) +planelist_iter.substring(0, planelist_iter.length()-4)+ ".txt" ;
							ArrayList<Double> CalculatedPositionX = new ArrayList<Double>();
							ArrayList<Double> CalculatedPositionY = new ArrayList<Double>();
							CalculatedPositionX.add((double) 0);CalculatedPositionY.add((double) 0);
							writeregistrationfile(CalculatedPositionX, CalculatedPositionY, writetofilename);
						
						}
						continue;
					}
					
					
					ArrayList<String> planelistmax = new ArrayList<String>();
					/////////////////////////////////////////////////////////////
					//select timepoints to stitch
					/////////////////////////////////////////////////////////////
					int totalnbtimepoints=nbtimepoints;
					
					
					for(int i1=0; i1<Math.min(retainedplanes.size(),totalnbtimepoints); i1++){
						planelistmax.add(retainedplanes.get((int) (i1*retainedplanes.size()/totalnbtimepoints)));
					}
					/*
					 if desired, you can also decide manually which timepoints the stitching should be done at. Later timepoints showed to be good as the fish grew larger
					
					int[] listtostitch = {2,3,4,5};
					int[] listtostitch = {160,180,200,210, 217, 230,240,260,270,280};
					
					System.out.println("here" + Arrays.toString(listtostitch));
					for(int i1=0; i1<listtostitch.length; i1++){
						planelistmax.add(retainedplanes.get(listtostitch[i1]));
						System.out.println(retainedplanes.get(listtostitch[i1]));
					}
					System.out.println("here" + Arrays.toString(listtostitch));
					*/
					
					System.out.println("e: " + retainedplanes.toString());
					System.out.println("f: " + planelistmax.toString());
					
					final StitchingParameters params = new StitchingParameters();
					params.fusionMethod = 0;
					params.regThreshold = 0.3;
					params.relativeThreshold = 2.5;		
					params.absoluteThreshold = 3.5;
					params.computeOverlap = true;
					params.subpixelAccuracy = true;
					params.virtual = false;
					params.cpuMemChoice = 0;
					params.outputVariant =  1;
					params.outputDirectory = foldername_save;
					// we need to set this
					params.channel1 = 0;
					params.channel2 = 0;
					params.timeSelect = 0;
					params.checkPeaks = 60;
					
					
					/////////////////////////////////////////////////////
					//if no registration  detail present
					///////////////////////////////////////////////////
					ArrayList<Double> CalculatedPositionX = new ArrayList<Double>();
					ArrayList<Double> CalculatedPositionY = new ArrayList<Double>();
					
					if(!previousreg){
					
						
					
					ArrayList<ImageCollectionElement2> elements3D= puttogetherimagelements(filenamestostitch, invertIT2, planelistmax, positionXtoit, positionYtoit, params);
					
					try{
						ArrayList<ImagePlusTimePoint2> optimized3D= new ArrayList<ImagePlusTimePoint2>();
						
							
				    	optimized3D = CollectionStitchingImgLib2.stitchCollection( elements3D, params,invertIT );
						
						if ( optimized3D == null ) return;
				  
						for ( final ImageCollectionElement2 element : elements3D )
				        {
							if(params.dimensionality==2){
								final TranslationModel2D m = (TranslationModel2D)element.getModel();
								 double[] tmp = new double[ 2];
								m.applyInPlace( tmp ); 
								System.out.println(tmp[0]); System.out.println(tmp[1]);
									CalculatedPositionX.add(tmp[0]);
									CalculatedPositionY.add(tmp[1]);
							}
							else{
							final TranslationModel3D m = (TranslationModel3D)element.getModel();
						  double[] tmp = new double[ 3];
						 m.applyInPlace( tmp );
						 
						 System.out.println(tmp[0]); System.out.println(tmp[1]);
						CalculatedPositionX.add(tmp[0]); CalculatedPositionY.add(tmp[1]);
							}
						
				       }
						}catch(Exception e){
							
							IJ.log("not succesful stitching of " + elements3D.toString());
							int totalnbtimepointsNeu=60;
							for(int i1=0; i1<Math.min(retainedplanes.size(),totalnbtimepointsNeu); i1++){
								planelistmax.add(retainedplanes.get((int) (i1*retainedplanes.size()/totalnbtimepointsNeu)));
								
							}
							
							ArrayList<ImageCollectionElement2> elements3Dnew= puttogetherimagelements(filenamestostitch, invertIT2, planelistmax, positionXtoit, positionYtoit, params);
							ArrayList<ImagePlusTimePoint2> optimized3D= new ArrayList<ImagePlusTimePoint2>();
							
							
					    	optimized3D = CollectionStitchingImgLib2.stitchCollection( elements3Dnew, params,invertIT );
							
							if ( optimized3D == null ) return;
					  
							for ( final ImageCollectionElement2 element : elements3Dnew )
					        {
								if(params.dimensionality==2){
									final TranslationModel2D m = (TranslationModel2D)element.getModel();
									 double[] tmp = new double[ 2];
									m.applyInPlace( tmp ); 
									System.out.println(tmp[0]); System.out.println(tmp[1]);
										CalculatedPositionX.add(tmp[0]);
										CalculatedPositionY.add(tmp[1]);
								}
								else{
								final TranslationModel3D m = (TranslationModel3D)element.getModel();
							  double[] tmp = new double[ 3];
							 m.applyInPlace( tmp );
							 
							 System.out.println(tmp[0]); System.out.println(tmp[1]);
							CalculatedPositionX.add(tmp[0]); CalculatedPositionY.add(tmp[1]);
								}
							
							
					     }
						
					/*
					 * .
					 * 
						*/	
					}}
					else{
						//if you just want to apply already calculated registration files e.g. from another fluorescent channel, then this will do it

						String textfilename = directory_name+File.separator+ "RegistrationInfoMax"+File.separator+ event_index + "angle"+ IJ.pad(i, 3) + planelistList.get(0).substring(0, planelistList.get(0).length()-4)+".txt" ;
						
						
						System.out.println("current folder that is processed: " + textfilename);
						
						//skip if registration for the current folder doesn-t exist
						if(!new File(textfilename).exists()){continue;}
						
						
						//read in registration parameters
						try {
							File myFile = new File(textfilename);
							FileReader fileReader;
							fileReader = new FileReader(myFile);
							BufferedReader reader = new BufferedReader(fileReader);
							String line =null;
							System.out.println("read in");
							while((line = reader.readLine())!=null){
								String[] twopositions = line.split("ypos");
								String xpos1 = twopositions[0].substring(5, twopositions[0].length()-1);
								String ypos1 = twopositions[1].substring(1, twopositions[1].length()-1);
								
								for(String s : xpos1.split(",")){
									double res = Double.parseDouble(s); CalculatedPositionX.add(res);
								}		
								for(String s : ypos1.split(",")){
									double res = Double.parseDouble(s); CalculatedPositionY.add(res);
								}		
								
								System.out.println(CalculatedPositionY.toString());
							}
							reader.close();
						} catch (FileNotFoundException e) {
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
						
						
				
					////////////////////////////////////////////////////////////////////////
					//now fuse all images, BASED on above calculated or read in registration
					///////////////////////////////////////////////////////////////////////
					
					
					for(String planelist_iter: planelistList){
						
						ArrayList< ImageCollectionElement2> elementsPLANE =new ArrayList<ImageCollectionElement2>();
					
						for(int iter = 0; iter<filenamestostitch.length;iter++){
								ImageCollectionElement2 element = new ImageCollectionElement2( new File( filenamestostitch[iter] + File.separator + planelist_iter), iter);
						double[] offset = new double[ 2 ];
						offset[0]= CalculatedPositionX.get(iter);
						offset[1]= CalculatedPositionY.get(iter);
						element.setOffset( offset );
						element.setDimensionality( 2 );
						element.setModel( new TranslationModel2D() );	
						elementsPLANE.add( element );
						}
					
					
						
					ArrayList<ImagePlusTimePoint2> optimized_all = new ArrayList< ImagePlusTimePoint2 >();
					for ( final ImageCollectionElement2 elementiter : elementsPLANE )
					{	Opener opener = new Opener();
						System.out.println(elementiter.getFile().getAbsolutePath());
						File filename = elementiter.getFile();
						ImagePlus imp;
						
						if(filename.exists()){
						ImagePlus imp_tmp = opener.openImage(elementiter.getFile().getAbsolutePath());
						if(invertIT == true){
							ImageProcessor iptmp = imp_tmp.getProcessor();
							iptmp.flipHorizontal();
							imp = new ImagePlus("tese", iptmp);
						}
						else if(invertIT2==true){
							ImageProcessor iptmp = imp_tmp.getProcessor();
							iptmp.flipHorizontal();
							imp = new ImagePlus("tese", iptmp);
						}else{
						
							imp=imp_tmp;
						}
						}else{
							ImageProcessor emptyimage = new ShortProcessor(960,960); //960 is the size of a full image from the multi-sample SPIM camera
							emptyimage.setValue(960); ///average background pixel from the multi-sample SPIM camera
							imp = new ImagePlus("empty",emptyimage);
						}
						
						
						final ImagePlusTimePoint2 imt = new ImagePlusTimePoint2( imp, elementiter.getIndex(), 1, elementiter.getModel(), elementiter );
						
						if(elementiter.isEmptyImage()==true){params.fusionMethod = 3;} //use maximum fusion and not linear blending if one image is empty
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
					
					for ( final ImagePlusTimePoint2 imt : optimized_all )
					{	
						ImagePlus imp = imt.getImagePlus();
						images.add( imp );	
					}
					
					
					for ( final ImagePlusTimePoint2 imt : optimized_all )
						models.add((InvertibleBoundable) imt.getModel() );
			
					ImagePlus imp = null;
					
					// test if there is no overlap between any of the tiles
					// if so fusion can be much faster
					boolean noOverlap = false;
					params.dimensionality=2;

					
					String outputdirectory = foldername_save+File.separator+ "StitchedImages"+File.separator+event_index +  File.separator+ "angle"+ IJ.pad(i, 3) + File.separator + planelist_iter;
					System.out.println(outputdirectory);
					File outp = new File(outputdirectory);
					outp.getParentFile().mkdirs();
					Fusion.setOutputFileName(outputdirectory);
					
					
					imp = Fusion.fuse( images, models, params.dimensionality, params.subpixelAccuracy, params.fusionMethod, params.outputDirectory, noOverlap, false, params.displayFusion );

					params.fusionMethod = 0;//set fusion type back to linear blending...
					
					if ( imp != null )
					{
						imp.setTitle( "Fused" );
						imp.show();
					}

					////////////////////////////////////////////////////////////////////////////////////////////
					//write the registration info to a file...
					////////////////////////////////////////////////////////////////////////////////////////////
					
					String writetofilename = foldername_save+File.separator+ "RegistrationInfoMax"+File.separator+ event_index + "angle"+ IJ.pad(i, 3)  +planelist_iter.substring(0, planelist_iter.length()-4)+ ".txt" ;
					writeregistrationfile(CalculatedPositionX, CalculatedPositionY, writetofilename);

					}
			
			}}			
			}catch(Exception e){
				
			}}}
	System.out.println("finished:)");
	}			
			
			

	
	

	
	private void writeregistrationfile(ArrayList<Double> calculatedPositionX, ArrayList<Double> calculatedPositionY, String writetofilename) {
	//function to write the registration file	
		try{
			
			File folderwriteto = new File(writetofilename);
			folderwriteto.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(writetofilename);
			writer.write("xpos" + calculatedPositionX.toString());
			writer.write("ypos" + calculatedPositionY.toString());
			writer.close();
			
		}catch(IOException ex){
		IJ.log("Writing registration file did not work.");	
		}
	}


public ArrayList<ImageCollectionElement2> puttogetherimagelements(String[] filenamestostitch,Boolean invertIT2, ArrayList<String> planelistmax, double[] positionXtoit, double[] positionYtoit,  StitchingParameters params){

	
	ArrayList< ImageCollectionElement2> elements3D = new ArrayList< ImageCollectionElement2>();
	
	for(int iter = 0; iter<filenamestostitch.length;iter++){
	ImageStack ipSTACK = putstacktogether(planelistmax, filenamestostitch[iter],  invertIT2);
	
	ImageCollectionElement2 element3D = new ImageCollectionElement2(new ImagePlus("test", ipSTACK),iter);
	double[] offset3D = new double[ 3 ];
	offset3D[0]=(double) positionXtoit[iter];
	offset3D[1]=(double) positionYtoit[iter];
	offset3D[2]= 0;
	element3D.setOffset( offset3D );
	
	if(ipSTACK.getSize()>1){
	element3D.setModel( new TranslationModel3D() );	element3D.setDimensionality( 3 );params.dimensionality=3; 
	}else{element3D.setModel(new TranslationModel2D());element3D.setDimensionality( 2 );params.dimensionality=2; }
	
	System.out.println("offset " + Arrays.toString(offset3D));
	
	elements3D.add( element3D );
}
return elements3D;}


 public ImageStack putstacktogether(ArrayList<String> planelistmax, String filename, Boolean invertIT2){
 
	ImageStack ipSTACK = new ImageStack(960, 960);

	for(String planeiter : planelistmax){
	
	//System.out.println(new File(filenamestostitch[iter]+ File.separator + planeiter));
	Opener opener = new Opener();
	ImagePlus imp = opener.openImage(filename+ File.separator + planeiter);
	System.out.println(filename+ File.separator + planeiter);
	ImageProcessor ip = imp.getProcessor();
	
	ipSTACK.addSlice(ip);
	}
	
	if(invertIT2==true){
		for(int itermirror = 1; itermirror<=ipSTACK.getSize();itermirror++){
			ipSTACK.getProcessor(itermirror).flipHorizontal();
		}
	}
	return ipSTACK;
	}


	public static void main(String[] args2) {
		
		
		new ImageJ();
		
		IJ.runPlugIn("StitchItSPIMmaxproj_","");
		
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
	
	public static void copyFile( File from, File to ) throws IOException {
	    Files.copy( from.toPath(), to.toPath(),REPLACE_EXISTING);
	}
}
