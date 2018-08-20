
import java.util.ArrayList;
import java.util.List;


public class ListMath {

	public static float avg(List<? extends Number> list) {
		float average = 0;
		float sum = 0;
		for(Number i: list){
		sum = sum + i.floatValue();	
		}
	    // calculate average here
		if(list.size() != 0)
		{average = sum / list.size();}
		
		return average;
	}	
	
	public static float avg(List<? extends Number> list, int start, int iter) {
		float average = 0;
		float sum = 0;
		int indexiter = 0;
		int count = 0;
		for(Number i: list){
			if(indexiter<start || (indexiter-start) % iter != 0){indexiter++; continue;}	
			sum = sum + i.floatValue();
			count++;
			indexiter++;
		}
	    // calculate average here
		if(count != 0){average = sum / (float) count;}
		
		return average;
	}	
	
public static void main(String[] args2) {
		
	ArrayList<Double> test = new ArrayList<Double>();
	//System.out.println(avg(test));
	test.add((double) 3);test.add((double) 5); test.add((double) 8);test.add((double) 4);test.add((double) 8); test.add((double) 6);
	//System.out.println(avg(test));
	
	System.out.println(avg(test,0,4));
	
	}	
	
	
}
