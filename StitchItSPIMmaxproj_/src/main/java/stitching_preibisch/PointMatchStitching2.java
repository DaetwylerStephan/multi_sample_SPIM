package stitching_preibisch;

import stitchit_model.Point;
import stitchit_model.PointMatch;


public class PointMatchStitching2 extends PointMatch 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ComparePair2 pair;
	
	/**
	 * Constructor
	 * 
	 * Create a {@link PointMatch} with one weight.
	 * 
	 * @param p1 Point 1
	 * @param p2 Point 2
	 * @param weight Weight
	 */
	public PointMatchStitching2(
			Point p1,
			Point p2,
			float weight,
			ComparePair2 pair )
	{
		super ( p1, p2, weight );
		
		this.pair = pair;
	}
	
	public ComparePair2 getPair() { return pair; }

}
