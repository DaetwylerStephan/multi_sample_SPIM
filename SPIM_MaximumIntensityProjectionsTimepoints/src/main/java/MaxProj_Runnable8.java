  	 	

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import fiji.util.gui.GenericDialogPlus;
import ij.IJ;
import ij.ImageJ;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;


public class MaxProj_Runnable8 implements Runnable {

	public File timefolder;
	public File anglefolder;
	public Boolean runtifffiles;
	public Boolean runonselected;
	public String selectedtimepoint;
	public String foldername_save;
	public String index_timepoints;
	public String index_samples;
	public String event_index;
	public String angleinfo;//anglelist[i]
	public int height;
	public int width;
	public Boolean runsingletifffiles;
	
	@Override
	public void run() {
		
	
			
		if(runtifffiles == false){	
			if(runsingletifffiles ==false){
			IJ.log("raw process " + index_timepoints);
		
			if(runonselected==true){
				if(!Objects.equals(index_timepoints,selectedtimepoint)){return;}					
				}
			
			//sample data path example  L:\SPIM1_rawdata\GFP\experiment\e003\s000\t00001\r000\a330\c001\zstack (raw files)
			//timefolder is a folder such as  L:\SPIM1_rawdata\GFP\experiment\e003\s000\t00001 
			
			String[] regions             = filter(timefolder.list(), "r\\d{3}?"); Arrays.sort(regions);    timefolder = new File(timefolder, regions[0]);
			String[] angles              = filter(timefolder.list(), "a\\d{3}?"); Arrays.sort(angles);     timefolder = new File(timefolder, angles[0]);
			String[] channels            = filter(timefolder.list(), "c\\d{3}?"); Arrays.sort(channels);   timefolder = new File(timefolder, channels[0]);
			timefolder = new File(timefolder, "zstack");
			
			
			maxproj_stack2 d = new maxproj_stack2();
			d.width=width;
			d.height = height;
		
			d.setparent(timefolder.getAbsolutePath());
			String pathsave= foldername_save +File.separator+ "maxprojections" + File.separator + event_index + File.separator + index_samples + File.separator + index_timepoints + ".tif";
			d.setfoldertosave(pathsave);
			
			//check if file exists. If yes> skip; if no> do maxprojection
			File maxfile = new File(pathsave);
			if(maxfile.exists()){
				IJ.log("file exists");
				
			}else{
			try{
			d.makemaxproj();
			}
			catch(Exception e){
				System.out.println("There was a mistake in " + pathsave);
				return;
			}
			}}
			else{
				IJ.log("process " + index_timepoints);
				maxproj_stack2 d = new maxproj_stack2();
				d.width=width;
				d.height = height;
				d.settype("tif");
				
				d.setparent(timefolder.getAbsolutePath());
				String pathsave= foldername_save +File.separator+ "maxprojections" + File.separator + event_index + File.separator + index_samples + File.separator + index_timepoints + ".tif";
				d.setfoldertosave(pathsave);
				
				//check if file exists. If yes> skip; if no> do maxprojection
				File maxfile = new File(pathsave);
				if(maxfile.exists()){
					IJ.log("file exists");
					
				}else{
				try{
				d.makemaxproj();
				}
				catch(Exception e){
					System.out.println("There was a mistake in " + pathsave);
					return;
				}
				
				
			}
			}}
			
			
		else{
	
					
						File timepointfolder = this.timefolder;
						File planefolder = new File(timepointfolder, "zstack");
						maxproj_stack2 d = new maxproj_stack2();
						d.setparent(planefolder.getAbsolutePath());
						d.width=width;
						d.height = height;
						String pathsave= foldername_save +File.separator+ "StitchedImagesMaxProj" + File.separator + event_index + File.separator + angleinfo + File.separator + index_timepoints + ".tif";
						d.setfoldertosave(pathsave);
						d.settype("tif");
						
						//check if file exists. If yes> skip; if no> do maxprojection
						File maxfile = new File(pathsave);
						if(maxfile.exists()){
							
						}else{
						try{
						d.makemaxproj();
						}
						catch(Exception e){
							System.out.println("There was a mistake in " + pathsave);
							return;
						}}
									
				}
		
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
