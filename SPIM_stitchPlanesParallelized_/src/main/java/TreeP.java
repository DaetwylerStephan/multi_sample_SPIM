/*
 * this class is responsible for the tree structure representing the data structure of the experiment
 */
import java.io.File;
import java.util.ArrayList;


import java.util.regex.Pattern;


public class TreeP {
	private File parentdirectory;
	private NodeP root = new NodeP();
	private int lengthofparentdirectory;
	
	//initizialize TreeP structures
	public TreeP(File p){
		root.setrootdata(p);
		root.setfulldata(p);
		parentdirectory=p;
		lengthofparentdirectory = p.getAbsolutePath().length();
	}
	public TreeP(NodeP n){
		root = n;
		parentdirectory = new File(n.getfulldata());
		lengthofparentdirectory = n.getfulldata().length();	
	}
	
	//return root of a TreeP
	public NodeP getroot(){return root;}
	
	//inserts a NodeP at the appropriate position
	public void insert(NodeP NodePtoinsert){
		//insert NodeP based on its data into the file TreeP
		String[] tmp_parsed1 = parsefilename(NodePtoinsert.getfulldata());

		//remove potentially empty first elements in the array list
		String[] tmp_parsed;
		if (tmp_parsed1[0].isEmpty()){
			tmp_parsed=new String[tmp_parsed1.length -1];
			for(int i=0;i<tmp_parsed1.length-1;i++){tmp_parsed[i]= tmp_parsed1[i+1];}}
		else{tmp_parsed = tmp_parsed1;}
		
		//set the rood as NodeP n, we iterate over tmp_parsed1 and go down. If it does not found the NodeP in the childlist, then 
		//it creates a new NodeP
		NodeP n = root;
		boolean fast = false;
		for(String tmp_parsed_index:tmp_parsed){
		
		if(fast ==true || n.checkNodePcontent(tmp_parsed_index)<0){
			NodeP tmp1 = new NodeP(tmp_parsed_index);
			tmp1.setparent(n);
			n.setchildren(tmp1);
			n = tmp1;
			fast= true;
		}
		else{n=n.getchild(n.checkNodePcontent(tmp_parsed_index));}
		}
		
	}
	
	//returns the filename of a NodeP
	public  String getfilename(NodeP newroot){
		String name = "";
		try{
			name = newroot.getdata();
			if(newroot.getparent()!= null){
			name = getfilename(newroot.getparent())+ File.separator + name; 
			}
		}
		catch (NullPointerException e){}
		
	return name;
	}
	//returns the subTreeP starting at the NodeP newroot
	public TreeP getsubtree(NodeP newroot){
		NodeP newroot2 = new NodeP();
		newroot2.setfulldata(new File(getfilename(newroot)));
		for(NodeP i:newroot.getchildren()){newroot2.setchildren(i);}
		newroot2.setrootdata(new File(newroot2.getfulldata()));
		newroot2.setparent(null);
		TreeP newtree = new TreeP(newroot2);
		return newtree;
	}
	//gives back the number of NodePs in the subtree of NodeP p	
	public int numberofNodePs(NodeP p){
		int num=0;
		for(int i=0;i<p.getchildren().size();i++){
		num = num+numberofNodePs(p.getchild(i));
		}
		num = num + p.getchildren().size();
		return num;
	}
	//gives out the folder names, e.g. e002, e003,... in a list
	public ArrayList<String> searchtree(String pattern){
		ArrayList<String> list=new ArrayList<String>();
		
		list = gothroughtree(this.getroot(), list, pattern);
		return list;
	}
	private ArrayList<String> gothroughtree(NodeP n, ArrayList<String> list, String t){
		for (NodeP i: n.getchildren()){
		list = gothroughtree(i,list,t);
		if(i.getdata().matches(t)){list.add(i.getdata());}
		}
		return list;
	}	
	//gives out the full filename of the pattern
	public ArrayList<String> searchfilename(String pattern){
		ArrayList<String> list=new ArrayList<String>();
		
		list = gothroughtreefilename(this.getroot(), list, pattern);
		return list;
	}
	private ArrayList<String> gothroughtreefilename(NodeP n, ArrayList<String> list, String t){
		for (NodeP i: n.getchildren()){
		list = gothroughtreefilename(i,list,t);
		if(i.getdata().matches(t)){list.add(getfilename(i));}
		}
		return list;
	}	
	//gives out the NodePs matching the pattern
	public ArrayList<NodeP> searchNodePname(String pattern){
		ArrayList<NodeP> list=new ArrayList<NodeP>();
		
		list = gothroughtreeNodePname(this.getroot(), list, pattern);
		return list;
	}
	private ArrayList<NodeP> gothroughtreeNodePname(NodeP n, ArrayList<NodeP> list, String t){
		for (NodeP i: n.getchildren()){
		list = gothroughtreeNodePname(i,list,t);
		if(i.getdata().matches(t)){list.add(i);}
		}
		return list;
	}	
	

	
public static void main(String[] args) {
		
		NodeP testNodeP = new NodeP();
		String name ="/Users/daetwyle/Desktop/testit/e056/s002/t00000/r000/a145/c002/zstack/0000000000.dat";
		testNodeP.setdata(new File(name));
		testNodeP.setfulldata(new File(name));
		NodeP testNodeP2 = new NodeP();
		String name2 ="/Users/daetwyle/Desktop/testit/e056/s002/t00000/r000/a145/c001/zstack/0000000000.dat";
		testNodeP2.setdata(new File(name2));
		testNodeP2.setfulldata(new File(name2));
		//generate TreeP
		String directory_name = "/Users/daetwyle/Desktop/testit";
		TreeP testtree = new TreeP(new File(directory_name));
		
		
		
		
		testtree.insert(testNodeP);
		testtree.insert(testNodeP2);
		System.out.println(testtree.getfilename(testtree.getroot()));
		//System.out.println(testtree.getroot().getchild(0).getdata());
		TreeP newtree = testtree.getsubtree(testtree.getroot().getchild(0));//attention also old NodeP is changed!
		System.out.println(newtree.getroot().getdata());
		System.out.println(newtree.getroot().getchild(0).getdata());
		//testtree.getroot().getparent().getdata();
		//obviously that does not work because testNodeP is not the NodeP in the tree structure above, e.g. no parent is defined...
		//System.out.println(testtree.getfilename(testtree.getroot().getchild(0).getchild(0).getchild(0)));
		ArrayList<String> test = testtree.searchtree("s\\d{3}?");
		ArrayList<NodeP> testNodePs = testtree.searchNodePname("s\\d{3}");
		System.out.println(testNodePs.get(0).getparent().getdata());
		System.out.println(test.toString());
		
		}


private String[] parsefilename(String filename){
	String tmp=filename.substring(filename.lastIndexOf(parentdirectory.getAbsolutePath())+lengthofparentdirectory);
	String pattern = Pattern.quote(System.getProperty("file.separator"));
	String[] fileparsed = tmp.split(pattern);
	return fileparsed;
	//System.out.println(Arrays.toString(fileparsed));
}	


}


