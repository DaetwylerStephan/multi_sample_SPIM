/*
 * the folderstructure defines which keyword describes the regions...in our microscope it was "zstack" where the raw data of the files was saved. 
 * the folderstructure generates the file tree for later stitching.
 * 
 */

import java.io.File;


public class FolderStructureP {

private String keyword;
private TreeP foldertree;

public void setkeyword(String p){keyword = p;}
public TreeP getfoldertree(){return foldertree;}	
	
//generate Foldertree 
public TreeP generateFolderStructure(String directory){
	
	//generate TreeP
	File directory_file = new File(directory);
	foldertree = new TreeP(directory_file);		
	searchFile(directory_file);//go through all files
    return foldertree;			
}

private void searchFile(File dir) {
	
	File[] files = dir.listFiles();
	
	if (files != null) {
		  for (int i = 0; i < files.length; i++) {
			  if (files[i].getName().equalsIgnoreCase(keyword)){
				  NodeP newNodeP = new NodeP();
				  newNodeP.setdata(files[i].getParentFile());
				  newNodeP.setfulldata(files[i].getParentFile());
				  foldertree.insert(newNodeP);
			  break;
			  }
			  if (files[i].isDirectory()) {
				     searchFile(files[i]);
				  } 
		  }}
}

}


