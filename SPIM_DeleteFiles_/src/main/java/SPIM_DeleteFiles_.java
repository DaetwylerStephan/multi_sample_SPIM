  	 	

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import ij.IJ;
import ij.ImageJ;
import ij.Macro;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

/*
 * delete selected timepoints or regions from the dataste
 */

public class SPIM_DeleteFiles_ implements PlugIn {

	/**
	 * @param args
	 */
	@Override
	public void run(String args) {
		
		//generate an object of GenericDialog class to which options can be added and which can be evaluated
		final GenericDialog gd = new GenericDialog( "Indicate folder" );
		
		//generate options for the Plugin to work on SPIM data from the multi-sample SPIM
		gd.addStringField( "Foldername_to_process", "D:\\region_to_observe\\160113_daetwyler\\GFP\\e027", 50 );
		gd.addStringField("Delete_in_Samples", "0-2",50);
		gd.addStringField("Delete_Timepoints", "10;20;30-140",50);
		
		
		gd.showDialog();
		
		if ( gd.wasCanceled() )
			return;
		
		// read in input from plugin interface
		String directory_name = gd.getNextString();
		String selectedsamples = gd.getNextString();
		String selectedtimepoint = gd.getNextString();
		
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
			selectedsamples = parsedOptions[1];
			selectedtimepoint = parsedOptions[2];
			
		}
		
		//parse input strings and get sample and timepointlist
		ReturnList getlist = new ReturnList();
		List<String> samplelist = getlist.returnlist(selectedsamples, 1);
		List<String> timepointlist = getlist.returnlist(selectedtimepoint,0);
		
		Set<String> intersect = new HashSet<String>(samplelist);

	
		File eventfolder = new File(directory_name);
		String[] samples = filter(eventfolder.list(), "s\\d{3}?"); Arrays.sort(samples);
			if (samples.length !=0) {
				
				intersect.retainAll(Arrays.asList(samples));
				List<String> sample_arraylist = new ArrayList<String>(intersect);
				Collections.sort(sample_arraylist);
				System.out.println(sample_arraylist);
				
			//find all timepoints
			for (String index_samples: sample_arraylist){
				File samplefolder = new File(eventfolder, index_samples);
			 	String[] timepoints= filter(samplefolder.list(), "t\\d{5}?"); Arrays.sort(timepoints);
			 	
			 	Set<String> intersect_timepoints = new HashSet<String>(timepointlist);
			 	intersect_timepoints.retainAll(Arrays.asList(timepoints));
			 	List<String> timepoint_arraylist = new ArrayList<String>(intersect_timepoints);
				Collections.sort(timepoint_arraylist);
				System.out.println(timepoint_arraylist);
				
			for (String index_timepoints:timepoint_arraylist){
				 File timepointfolder = new File(samplefolder, index_timepoints);
				 IJ.log(timepointfolder.getAbsolutePath());

				 deleteDirectory(timepointfolder);
				 timepointfolder.delete();
				 
			}}
			}
	
			IJ.log("Execution successful!");
		
	}

	public static void main(String[] args2) {
		
		System.out.println("test");
		new ImageJ();
		
		IJ.runPlugIn("SPIM_DeleteFiles_","");
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
	
	private static boolean deleteDirectory(File directory){
		if(directory.exists()){
			File[] files = directory.listFiles();
			if(null!=files){
				for(int i=0;i<files.length;i++){
					if(files[i].isDirectory()){
						deleteDirectory(files[i]);
					}else{
						files[i].delete();
					}
				}
			}
		}
		return(directory.delete());
	}
}
