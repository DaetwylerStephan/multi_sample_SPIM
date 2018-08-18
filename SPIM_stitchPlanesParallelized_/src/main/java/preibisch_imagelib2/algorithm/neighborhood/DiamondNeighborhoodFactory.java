package preibisch_imagelib2.algorithm.neighborhood;

import preibisch_imagelib2.RandomAccess;

public interface DiamondNeighborhoodFactory< T >
{
	Neighborhood< T > create( long[] position, long radius, RandomAccess< T > sourceRandomAccess );
}
