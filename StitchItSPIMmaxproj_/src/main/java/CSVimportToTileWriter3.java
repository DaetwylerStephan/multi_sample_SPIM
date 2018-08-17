


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

//This plugin reads in .csv files from  the multi-sample SPIM
//Using CSVimportToTileWriter.PositionX will give you the respective X coordinates and
//CSVimportToTileWriter.PositionY will give you the respective Y coordinates  of the motor position of the instruments


public class CSVimportToTileWriter3 {

		double[] Elements;
		double[] PositionX;
		double[] PositionY;
		private int magnification;
		private int numberoftiles=400;
		private ArrayList<Integer> startpoints = new ArrayList<Integer>();
		
		
		//set here which magnification is used to acquire the data
		public void setmagnification(int magnification){this.magnification = magnification;}
		
		public void setnumberoftiles(int numbertiles){this.numberoftiles = numbertiles;}
		
		//set and get Positions and Startpoints
		public void setPositionX( final double[]  r ) { this.PositionX = r; }
		public void setPositionY( final double[]  r ) { this.PositionY = r; }
		public double[] getPositionX(){return this.PositionX;}
		public double[] getPositionY(){return this.PositionY;}
		public ArrayList<Integer> getStartpoints(){return startpoints;}
		
		
		//import the information from the .csv file
		public void importdata(String pathname){
		File file = new File(pathname);
		double[] Elements = new double[numberoftiles * 4];
			
			try {
				Scanner inputStream = new Scanner(file);
				int indexarray = 0;
				while(inputStream.hasNext()){
					String data = inputStream.next();
					Double position = Double.parseDouble(data);
					if (indexarray<numberoftiles * 4)
					{Elements[indexarray]=position;}
					indexarray++;
				}
				inputStream.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int numberofpositions=numberoftiles;
			for(int index2=0;index2<numberoftiles*4;index2++){
			if(Elements[index2]==0 & Elements[index2+1]==0 & Elements[index2+2]==0 & Elements[index2+3]==0){
				numberofpositions=index2;
				break;
			}
			}
			
			numberofpositions=numberofpositions/4;

			double[] PositionX= new double[numberofpositions];
			double[] PositionY= new double[numberofpositions];
			int[] Angle= new int[numberofpositions];
			
			for (int ind =0;ind<numberofpositions;ind++){
				PositionX[ind]=Elements[4*ind];
				PositionY[ind]=Elements[4*ind+1];
				Angle[ind]=(int) Elements[4*ind+3];
			}
			

			double[] PositionA= new double[numberofpositions];
			double[] PositionB= new double[numberofpositions];
			
			
		
			double startindexX=0;
			double startindexY=0;
			
			for (int ind =0;ind<numberofpositions;ind++){
			//bin all angles together...
			if(ind==0){
				 startindexX=PositionX[0];
				 startindexY=PositionY[0];
				 startpoints.add(ind);
			}
			else{
				if(Angle[ind-1]!=Angle[ind]){
				startindexX = PositionX[ind];
				startindexY = PositionY[ind];
				startpoints.add(ind);
				}
			}
				
				//transform the position information from the microscope to pixel coordinates based on magnification
				if (magnification == 10){
				PositionA[ind]=(PositionX[ind]-startindexX)*7/8*1000;
				PositionB[ind]=-(PositionY[ind]-startindexY)*7/8*1000;
				if(PositionB[ind]<0){PositionB[ind]=PositionB[ind]*(-1);}}
				else if (magnification==20) {
				PositionA[ind]=(PositionX[ind]-startindexX)*14/8*1000;
				PositionB[ind]=-(PositionY[ind]-startindexY)*14/8*1000;
				if(PositionB[ind]<0){PositionB[ind]=PositionB[ind]*(-1);}}
				else{
				PositionA[ind]=(PositionX[ind]-startindexX)*28/8*1000;
				PositionB[ind]=-(PositionY[ind]-startindexY)*28/8*1000;	
				if(PositionB[ind]<0){PositionB[ind]=PositionB[ind]*(-1);}}
			
			}
			//save position information in csv-object
			this.PositionX = PositionA;
			this.PositionY=  PositionB;
		}
		
		

		
		
		
		public static void main(String[] args)  {
			
			CSVimportToTileWriter3 tmp = new CSVimportToTileWriter3();
			tmp.setmagnification(10);
			tmp.importdata("D:\\experiment\\e001\\e011.csv");
			
			System.out.println(Arrays.toString(tmp.getPositionX()));
			System.out.println(Arrays.toString(tmp.getPositionY()));
			System.out.println(tmp.getStartpoints().toString());
			
			
		}
		
		
}
