
import java.io.File;
import java.util.ArrayList;


public class NodeP {

private	ArrayList<NodeP> children = new ArrayList<NodeP>();
private NodeP parent=null;
private String data;
private String data_name;

public NodeP(String d){
	this.setdata(new File(d));
}
public NodeP(){
	
}

//set and get children
public void setchildren(NodeP p){children.add(p);}
public ArrayList<NodeP> getchildren(){return children;}
public NodeP getchild(int i){return children.get(i);}
//set and get parent
public void setparent(NodeP p){parent = p;}
public NodeP getparent(){return parent;}
//set data
public void setdata(File p){data_name=p.getName();}
public void setrootdata(File p){data_name=p.getAbsolutePath();}
public void setfulldata(File p){data = p.getAbsolutePath();}
//get data
public String getdata(){return data_name;}
public String getfulldata(){return data;}

public int finddatainChildren(String findit){
	int childrenindex=-1;
	
	for(int i = 0;i<children.size();i++){
		if(children.get(i).getdata().equals(findit)){
			childrenindex = i;
		}
	}
	
	return childrenindex;
}

public int checkNodePcontent(String test){
	int evaluate = -1;
	int ind=0;
	if (!children.isEmpty()){
	for(NodeP i:children){	
	if(i.getdata().equals(test)){evaluate = ind;};
	ind++;
	}}
	return evaluate;
}

public String godownNodeP(){
	String filename;
	try{
	filename = this.getdata() +File.separator+ this.getchild(0).godownNodeP(); 
	}
	catch(Exception e){
	filename = this.getdata();
	}
	
	return filename;
}

//returns the filename of a NodeP
	public  String getfilename(){
		String name = "";
		try{
			name = this.getdata();
			if(this.getparent()!= null){
			name = this.getparent().getfilename()+ File.separator + name; 
			}
		}
		catch (NullPointerException e){}
		
	return name;
	}


public ArrayList<String> getchildrendata(){
	ArrayList<String> data = new ArrayList<String>();
	for (NodeP i:this.getchildren()){
		data.add(i.getdata());
	}
	return data;
}


public boolean isempty(){
	if (this == null){
	return true;}
	else {return false;}
}
/*
public String getfilename(){
	String totalname;
	System.out.println("test");
	
	totalname = this.getparent().getfilename() + File.separator+ this.getdata();
	
	return totalname;
}*/
}