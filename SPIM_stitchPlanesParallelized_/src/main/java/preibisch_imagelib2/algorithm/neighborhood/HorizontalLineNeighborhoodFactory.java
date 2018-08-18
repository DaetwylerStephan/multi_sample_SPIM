package preibisch_imagelib2.algorithm.neighborhood;

import preibisch_imagelib2.RandomAccess;

public interface HorizontalLineNeighborhoodFactory< T >
{
	public Neighborhood< T > create( final long[] position, final long span, final int dim, final boolean skipCenter, final RandomAccess< T > sourceRandomAccess );
}
