package preibisch_imagelib2.algorithm.neighborhood;

import java.util.Iterator;

import preibisch_imagelib2.AbstractEuclideanSpace;
import preibisch_imagelib2.AbstractLocalizable;
import preibisch_imagelib2.Cursor;
import preibisch_imagelib2.FinalInterval;
import preibisch_imagelib2.Interval;
import preibisch_imagelib2.Positionable;
import preibisch_imagelib2.RandomAccess;
import preibisch_imagelib2.RealPositionable;

public class DiamondNeighborhood< T > extends AbstractLocalizable implements Neighborhood< T >
{
	public static < T > DiamondNeighborhoodFactory< T > factory()
	{
		return new DiamondNeighborhoodFactory< T >()
		{
			@Override
			public Neighborhood< T > create( final long[] position, final long radius, final RandomAccess< T > sourceRandomAccess )
			{
				return new DiamondNeighborhood< T >( position, radius, sourceRandomAccess );
			}
		};
	}

	private final RandomAccess< T > sourceRandomAccess;

	private final long radius;

	private final int maxDim;

	// private final long size;

	private final FinalInterval structuringElementBoundingBox;

	DiamondNeighborhood( final long[] position, final long radius, final RandomAccess< T > sourceRandomAccess )
	{
		super( position );
		this.sourceRandomAccess = sourceRandomAccess;
		this.radius = radius;
		maxDim = n - 1;
		// size = computeSize();

		final long[] min = new long[ n ];
		final long[] max = new long[ n ];

		for ( int d = 0; d < n; d++ )
		{
			min[ d ] = -radius;
			max[ d ] = radius;
		}
		structuringElementBoundingBox = new FinalInterval( min, max );
	}

	public class LocalCursor extends AbstractEuclideanSpace implements Cursor< T >
	{
		protected final RandomAccess< T > source;

		/** The current radius in each dimension we are at. */
		protected final long[] ri;

		/** The remaining number of steps in each dimension we still have to go. */
		protected final long[] s;

		public LocalCursor( final RandomAccess< T > source )
		{
			super( source.numDimensions() );
			this.source = source;
			ri = new long[ n ];
			s = new long[ n ];
			reset();
		}

		protected LocalCursor( final LocalCursor c )
		{
			super( c.numDimensions() );
			source = c.source.copyRandomAccess();
			ri = c.ri.clone();
			s = c.s.clone();
		}

		@Override
		public T get()
		{
			return source.get();
		}

		@Override
		public void fwd()
		{
			if ( --s[ 0 ] >= 0 )
			{
				source.fwd( 0 );
			}
			else
			{
				int d = 1;
				for ( ; d < n; ++d )
				{
					if ( --s[ d ] >= 0 )
					{
						source.fwd( d );
						break;
					}
				}

				for ( ; d > 0; --d )
				{
					final int e = d - 1;
					final long pd = Math.abs( s[ d ] - ri[ d ] );
					final long rad = ri[ d ] - pd;
					ri[ e ] = rad;
					s[ e ] = 2 * rad;

					source.setPosition( position[ e ] - rad, e );
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
			for ( int d = 0; d < maxDim; ++d )
			{
				ri[ d ] = s[ d ] = 0;
				source.setPosition( position[ d ], d );
			}
			source.setPosition( position[ maxDim ] - radius - 1, maxDim );
			ri[ maxDim ] = radius;
			s[ maxDim ] = 1 + 2 * radius;
		}

		@Override
		public boolean hasNext()
		{
			return s[ maxDim ] > 0;
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
			source.localize( position );
		}

		@Override
		public void localize( final float[] position )
		{
			source.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			source.localize( position );
		}

		@Override
		public void localize( final int[] position )
		{
			source.localize( position );
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
		return structuringElementBoundingBox;
	}

	@Override
	public long size()
	{
		if ( n < 1 )
		{
			return 1;
		}
		else if ( n < 2 )
		{
			return 2 * radius + 1;
		}
		else if ( n < 3 )
		{
			return radius * radius + ( radius + 1 ) * ( radius + 1 );
		}
		else
		{
			return diamondSize( radius, n );
		}
	}

	private final static long diamondSize( final long rad, final int dim )
	{
		if ( dim == 2 )
		{
			return rad * rad + ( rad + 1 ) * ( rad + 1 );
		}
		else
		{
			long size = 0;
			for ( int r = 0; r < rad; r++ )
			{
				size += 2 * diamondSize( r, dim - 1 );
			}
			size += diamondSize( rad, dim - 1 );
			return size;
		}

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
		return position[ d ] - radius;
	}

	@Override
	public void realMin( final double[] min )
	{
		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = position[ d ] - radius;
		}
	}

	@Override
	public void realMin( final RealPositionable min )
	{
		for ( int d = 0; d < min.numDimensions(); d++ )
		{
			min.setPosition( position[ d ] - radius, d );
		}
	}

	@Override
	public double realMax( final int d )
	{
		return position[ d ] + radius;
	}

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < max.length; d++ )
		{
			max[ d ] = position[ d ] + radius;
		}
	}

	@Override
	public void realMax( final RealPositionable max )
	{
		for ( int d = 0; d < max.numDimensions(); d++ )
		{
			max.setPosition( position[ d ] + radius, d );
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
		return position[ d ] - radius;
	}

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < min.length; d++ )
		{
			min[ d ] = position[ d ] - radius;
		}
	}

	@Override
	public void min( final Positionable min )
	{
		for ( int d = 0; d < min.numDimensions(); d++ )
		{
			min.setPosition( position[ d ] - radius, d );
		}
	}

	@Override
	public long max( final int d )
	{
		return position[ d ] + radius;
	}

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < max.length; d++ )
		{
			max[ d ] = position[ d ] + radius;
		}
	}

	@Override
	public void max( final Positionable max )
	{
		for ( int d = 0; d < max.numDimensions(); d++ )
		{
			max.setPosition( position[ d ] + radius, d );
		}
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < dimensions.length; d++ )
		{
			dimensions[ d ] = ( 2 * radius ) + 1;
		}
	}

	@Override
	public long dimension( final int d )
	{
		return ( 2 * radius ) + 1;
	}

	@Override
	public LocalCursor cursor()
	{
		return new LocalCursor( sourceRandomAccess.copyRandomAccess() );
	}

	@Override
	public LocalCursor localizingCursor()
	{
		return cursor();
	}

}
