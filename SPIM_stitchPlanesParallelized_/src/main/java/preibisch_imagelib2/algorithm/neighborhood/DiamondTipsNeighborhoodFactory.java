package preibisch_imagelib2.algorithm.neighborhood;

import preibisch_imagelib2.RandomAccess;

public interface DiamondTipsNeighborhoodFactory< T >
{
	public Neighborhood< T > create( final long[] position, final long radius, final RandomAccess< T > sourceRandomAccess );
}
