  	 	

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import fiji.util.gui.GenericDialogPlus;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.Macro;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;


public class SPIM_copy_rename_vascularDATA_ implements PlugIn {

	/**
	 * copy data and relabel them for segmentation. The RFP channel (red blood cells) is thereby kept as individual stacks, the vascular channel (GFP) written to one stack
	 */
	@Override
	public void run(String args) {
		
		//generate an object of GenericDialog class to which options can be added and which can be evaluated
		final GenericDialogPlus gd = new GenericDialogPlus( "Indicate folder" );
		
		//generate options for the Plugin to work on SPIM data from the multi-sample SPIM
		gd.addFileField("Foldername_to_process", "L:\\SPIM1_rawdata\\GFP\\experiment\\TIFF", 50 );
		gd.addFileField( "Foldername_to_save", "L:\\vascular_data\\experiment", 50 );
		gd.addStringField("GFP (0) or RFP (1)", "0",50);
		
		
		gd.showDialog();
		
		if ( gd.wasCanceled() )
			return;
		
		// read in input from plugin interface
		String directory_name = gd.getNextString();
		String foldername_save = gd.getNextString();
		String channel = gd.getNextString();
		
		
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
			channel = parsedOptions[2];
		}
		
		int channelnb = Integer.parseInt(channel);
		

		//----------go through folder
		
		File parentfolder = new File(directory_name);
		String[] events = filter(parentfolder.list(), "e\\d{3}?"); Arrays.sort(events);
			
		for(int i_event=0;i_event<events.length;i_event++){
			File currenteventfolder = new File(parentfolder,events[i_event]);
			String[] angles = filter(currenteventfolder.list(), "angle\\d{3}?"); Arrays.sort(angles);
			int angleiterator = 0;
			
			for(int i_angle=0;i_angle<angles.length;i_angle++){
				
				File currentanglefolder = new File(currenteventfolder,angles[i_angle]);
				String[] timepoints = filter(currentanglefolder.list(), "t\\d{5}?"); Arrays.sort(timepoints);
				
				for(int i_timepoints = 0; i_timepoints<timepoints.length;i_timepoints++){
					
					//folder to copy from
					File currenttimepointfolder = new File(currentanglefolder,timepoints[i_timepoints]);
					String[] filetocopylist = filter(currenttimepointfolder.list(), "(.*).tif$"); Arrays.sort(filetocopylist);
					
					
					//file to save to
					String currentsavefolder = foldername_save + File.separator + "tiff" + File.separator +  events[i_event]+ File.separator + angles[i_angle] +File.separator+timepoints[i_timepoints];
					File currentsavefolderfile = new File(currentsavefolder);
					currentsavefolderfile.mkdirs();
					
					//distinguish GFP and RFP
					String filecopyto = "";
					if(channelnb ==0){
						filecopyto=currentsavefolder + File.separator +  "GFP_full.tif";
					}else if(channelnb==1){
						filecopyto=currentsavefolder + File.separator + "RFP_s" + IJ.pad(angleiterator, 3) + ".tif";
					}
					
					//which file to copy? Distinguish two cases> planes are reduced to one stack or planes are still in a single stack
					
					if(filetocopylist.length>5){
					//if you haven't put the planes to a stack yet	
						
						//open and initialise ImageStack
						ImagePlus firstimage = IJ.openImage(currenttimepointfolder.getAbsolutePath()+ File.separator + filetocopylist[0]);
						int width = firstimage.getWidth();
						int height = firstimage.getHeight();
						
						ImageStack newstack = new ImageStack(width, height);
						newstack.addSlice(firstimage.getProcessor());
						//add remaining planes to stack
						for(int i=1;i<filetocopylist.length;i++){
							ImagePlus tmpimage = IJ.openImage(currenttimepointfolder.getAbsolutePath()+ File.separator + filetocopylist[i]);
							newstack.addSlice(tmpimage.getProcessor());
						}
						
						ImagePlus fullstack = new ImagePlus(timepoints[i_timepoints], newstack);
						IJ.save(fullstack, filecopyto);
						
					}else{
					//just copy files	
						//don't copy files which only have few planes (artifact of the microscope of running out of memory at the end of a timelapse)
						if(filetocopylist[0].length()>12){continue;}
						
						String filetocopy = currenttimepointfolder.getAbsolutePath() + File.separator + filetocopylist[0];
						
						try {
							
							System.out.println("from> " + filetocopy);
							System.out.println("to> " + filecopyto);
							copyFile(new File(filetocopy), new File(filecopyto));
						//} catch (IOException e) {
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}				
				}
				angleiterator++;
			}		
		}

		IJ.log("Execution successful!");
	}

	public static void main(String[] args2) {
		
		System.out.println("test");
		new ImageJ();
		
		IJ.runPlugIn("SPIM_copy_rename_vascularDATA_","");
	}
	
	public static void copyFile( File from, File to ) throws IOException {
	    Files.copy( from.toPath(), to.toPath() );
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
}
