package stitching_preibisch;



import mpicbg.stitching.ImagePlusTimePoint;
import ij.ImagePlus;
import preibisch_code.Model;
import preibisch_code.Tile;
import preibisch_code.TranslationModel2D;

public class ComparePair2 
{
	final ImagePlusTimePoint2 impA, impB;
	float crossCorrelation;
	boolean validOverlap;
	
	// the local shift of impB relative to impA 
	float[] relativeShift;
	
	public ComparePair2( final ImagePlusTimePoint2 impA, final ImagePlusTimePoint2 impB )
	{
		this.impA = impA;
		this.impB = impB;
		this.validOverlap = true;
	}
	
	public ImagePlusTimePoint2 getTile1() { return impA; }
	public ImagePlusTimePoint2 getTile2() { return impB; }
	
	public ImagePlus getImagePlus1() { return impA.getImagePlus(); }
	public ImagePlus getImagePlus2() { return impB.getImagePlus(); }
	
	public int getTimePoint1() { return impA.getTimePoint(); }
	public int getTimePoint2() { return impB.getTimePoint(); }
	
	public Model< ? > getModel1() { return impA.getModel(); }
	public Model< ? > getModel2() { return impB.getModel(); }
	
	public void setCrossCorrelation( final float r ) { this.crossCorrelation = r; }
	public float getCrossCorrelation() { return crossCorrelation; }

	public void setRelativeShift( final float[] relativeShift ) { this.relativeShift = relativeShift; }
	public float[] getRelativeShift() { return relativeShift; }
	
	public void setIsValidOverlap( final boolean state ) { this.validOverlap = state; }
	public boolean getIsValidOverlap() { return validOverlap; }
}
