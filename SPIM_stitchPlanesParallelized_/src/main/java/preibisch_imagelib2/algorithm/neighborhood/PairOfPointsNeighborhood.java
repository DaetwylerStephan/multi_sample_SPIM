package preibisch_imagelib2.algorithm.neighborhood;

import java.util.Iterator;

import preibisch_imagelib2.AbstractEuclideanSpace;
import preibisch_imagelib2.AbstractLocalizable;
import preibisch_imagelib2.Cursor;
import preibisch_imagelib2.Interval;
import preibisch_imagelib2.Positionable;
import preibisch_imagelib2.RandomAccess;
import preibisch_imagelib2.RealPositionable;
import preibisch_imagelib2.util.Intervals;

public class PairOfPointsNeighborhood< T > extends AbstractLocalizable implements Neighborhood< T >
{
	public static < T > PairOfPointsNeighborhoodFactory< T > factory()
	{
		return new PairOfPointsNeighborhoodFactory< T >()
				{
			@Override
			public Neighborhood< T > create( final long[] position, final long[] offset, final RandomAccess< T > sourceRandomAccess )
			{
				return new PairOfPointsNeighborhood< T >( position, offset, sourceRandomAccess );
			}
				};
	}

	private final long[] offset;

	private final int ndims;

	private final RandomAccess< T > ra;

	PairOfPointsNeighborhood( final long[] position, final long[] offset, final RandomAccess< T > sourceRandomAccess )
	{
		super( position );
		ra = sourceRandomAccess.copyRandomAccess();
		this.offset = offset;
		this.ndims = sourceRandomAccess.numDimensions();

	}

	public class LocalCursor extends AbstractEuclideanSpace implements Cursor< T >
	{
		private int index;

		private RandomAccess< T > source;

		private LocalCursor( final RandomAccess< T > source )
		{
			super( source.numDimensions() );
			this.source = source;
			reset();
		}

		private LocalCursor( final LocalCursor c )
		{
			this( c.source.copyRandomAccess() );
			this.index = c.index;
		}

		@Override
		public T get()
		{
			return source.get();
		}

		@Override
		public void fwd()
		{
			index++;
			if ( index == 1 )
			{
				for ( int d = 0; d < offset.length; d++ )
				{
					source.move( offset[ d ], d );
				}
			}
		}

		@Override
		public void jumpFwd( final long steps )
		{
			for ( long i = 0; i < steps; ++i )
			{
				fwd();
			}
		}

		@Override
		public T next()
		{
			fwd();
			return get();
		}

		@Override
		public void remove()
		{
			// NB: no action.
		}

		@Override
		public void reset()
		{
			index = -1;
			source.setPosition( position );
		}

		@Override
		public boolean hasNext()
		{
			return index < 1;
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return source.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return source.getDoublePosition( d );
		}

		@Override
		public int getIntPosition( final int d )
		{

			return source.getIntPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return source.getLongPosition( d );
		}

		@Override
		public void localize( final long[] position )
		{
			for ( int d = 0; d < position.length; d++ )
			{
				position[ d ] = source.getLongPosition( d );
			}
		}

		@Override
		public void localize( final float[] position )
		{
			for ( int d = 0; d < position.length; d++ )
			{
				position[ d ] = source.getFloatPosition( d );
			}
		}

		@Override
		public void localize( final double[] position )
		{
			for ( int d = 0; d < position.length; d++ )
			{
				position[ d ] = source.getDoublePosition( d );
			}
		}

		@Override
		public void localize( final int[] position )
		{
			for ( int d = 0; d < position.length; d++ )
			{
				position[ d ] = source.getIntPosition( d );
			}
		}

		@Override
		public LocalCursor copy()
		{
			return new LocalCursor( this );
		}

		@Override
		public LocalCursor copyCursor()
		{
			return copy();
		}
	}

	@Override
	public Interval getStructuringElementBoundingBox()
	{
		final long[] minmax = new long[ ndims * 2 ];
		for ( int d = 0; d < ndims; d++ )
		{
			minmax[ d ] = Math.min( position[ d ], position[ d ] + offset[ d ] );
		}
		for ( int d = ndims; d < 2 * ndims; d++ )
		{
			final int sd = d - ndims;
			minmax[ d ] = Math.max( position[ sd ], position[ sd ] + offset[ sd ] );
		}
		return Intervals.createMinMax( minmax );
	}

	@Override
	public long size()
	{
		return 2l;
	}

	@Override
	public T firstElement()
	{
		return cursor().next();
	}

	@Override
	public Object iterationOrder()
	{
		return this; // iteration order is only compatible with ourselves
	}

	@Override
	public double realMin( final int d )
	{
		return Math.min( position[ d ], position[ d ] + offset[ d ] );
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = Math.min( position[ d ], position[ d ] + offset[ d ] );
		}
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < min.numDimensions(); d++ )
		{
			min.setPosition( Math.min( position[ d ], position[ d ] + offset[ d ] ), d );
		}
	}

	@Override
	public double realMax( final int d )
	{
		return Math.max( position[ d ], position[ d ] + offset[ d ] );
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < max.length; d++ )
		{
			max[ d ] = Math.max( position[ d ], position[ d ] + offset[ d ] );
		}
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < max.numDimensions(); d++ )
		{
			max.setPosition( Math.max( position[ d ], position[ d ] + offset[ d ] ), d );
		}
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	public long min( final int d )
	{
		return Math.min( position[ d ], position[ d ] + offset[ d ] );
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = Math.min( position[ d ], position[ d ] + offset[ d ] );
		}
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < min.numDimensions(); d++ )
		{
			min.setPosition( Math.min( position[ d ], position[ d ] + offset[ d ] ), d );
		}
	}

	@Override
	public long max( final int d )
	{
		return Math.max( position[ d ], position[ d ] + offset[ d ] );
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < max.length; d++ )
		{
			max[ d ] = Math.max( position[ d ], position[ d ] + offset[ d ] );
		}
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < max.numDimensions(); d++ )
		{
			max.setPosition( Math.max( position[ d ], position[ d ] + offset[ d ] ), d );
		}
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < dimensions.length; d++ )
		{
			dimensions[ d ] = Math.abs( offset[ d ] ) + 1;
		}
	}

	@Override
	public long dimension( final int d )
	{
		return Math.abs( offset[ d ] ) + 1;
	}

	@Override
	public LocalCursor cursor()
	{
		return new LocalCursor( ra.copyRandomAccess() );
	}

	@Override
	public LocalCursor localizingCursor()
	{
		return cursor();
	}

}
