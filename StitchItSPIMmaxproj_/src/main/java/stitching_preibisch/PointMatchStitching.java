package stitching_preibisch;

import preibisch_code.Point;
import preibisch_code.PointMatch;

public class PointMatchStitching extends PointMatch 
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
	public PointMatchStitching(
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
