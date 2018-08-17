package preibisch_imagelib2.algorithm.neighborhood;

import preibisch_imagelib2.RandomAccess;

public interface PairOfPointsNeighborhoodFactory< T >
{
	Neighborhood< T > create( long[] position, long[] offset, RandomAccess< T > sourceRandomAccess );
}
