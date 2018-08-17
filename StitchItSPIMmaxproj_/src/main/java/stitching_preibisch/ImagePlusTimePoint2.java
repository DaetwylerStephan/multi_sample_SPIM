package stitching_preibisch;




import preibisch_code.Model;
import preibisch_code.Tile;
import preibisch_code.TranslationModel2D;
import ij.ImagePlus;

public class ImagePlusTimePoint2 extends Tile implements Comparable< ImagePlusTimePoint2 >
{
	final ImagePlus imp;
	final int impId;
	final int timePoint, dimensionality;
	
	// might have one if called from grid/collection stitching
	final ImageCollectionElement2 element;
	
	public ImagePlusTimePoint2( final ImagePlus imp, final int impId, final int timepoint, final Model model, final ImageCollectionElement2 element )
	{
		super( model );
		this.imp = imp;
		this.impId = impId;
		this.timePoint = timepoint;
		this.element = element;
		
		if ( TranslationModel2D.class.isInstance( model ) )
			dimensionality = 2;
		else
			dimensionality = 3;
	}
	
	public int getImpId() { return impId; }
	public ImagePlus getImagePlus() { return imp; }
	public int getTimePoint() { return timePoint; }
	public ImageCollectionElement2 getElement() { return element; }

	@Override
	public int compareTo( final ImagePlusTimePoint2 o ) 
	{
		if ( timePoint < o.timePoint )
			return -1;
		else if ( timePoint > o.timePoint )
			return 1;
		else
		{
			if ( impId < o.impId )
				return -1;
			else if ( impId > o.impId )
				return 1;
			else 
				return 0;
		}			
	}
}
