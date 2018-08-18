import ij.IJ;

import java.util.ArrayList;
import java.util.List;

/*
 * parse input string of the format 3-5;6;10 to a List of {3,4,5,6,10}
 * if type 0, change return to {t00003, t00004, t00005, t00006,t00010}
 * if type 1, change return to {s003, s004, s005, s006, s010}
 */

public class ReturnList {

	public List<String> returnlist(String inputstring, int type){
	//interpret timepoints
			String[] timepointlist = inputstring.split(";"); 
			
			List<String> list = new ArrayList<String>();
			
			for(String timepointlistindex:timepointlist){
			String[] tmplist = timepointlistindex.split("-");	
				if(tmplist.length>2){
					IJ.log("DeleteFiles list not valid");
					break;
				}
				else if(tmplist.length==1){
					int startpoint = Integer.parseInt(tmplist[0]);
						if(type==0){
							String addit = "t" + IJ.pad(startpoint, 5);
							list.add(addit);}
						if(type==1){
							String addit = "s" + IJ.pad(startpoint, 3);
							list.add(addit);}
						}
				else if(tmplist.length ==2){
					int startpoint = Integer.parseInt(tmplist[0]);
					int endpoint = Integer.parseInt(tmplist[1]);
					
					for(int i = startpoint;i<= endpoint;i++){
						if(type==0){
							String addit = "t" + IJ.pad(i, 5);
							list.add(addit);}
						if(type==1){
							String addit = "s" + IJ.pad(i, 3);
							list.add(addit);}
						}
					}
				}
			
			System.out.println(list);
			return list;	
	}	
}
