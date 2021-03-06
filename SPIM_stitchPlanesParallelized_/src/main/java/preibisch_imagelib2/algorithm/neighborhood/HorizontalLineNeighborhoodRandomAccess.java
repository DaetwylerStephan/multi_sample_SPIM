package preibisch_imagelib2.algorithm.neighborhood;

import preibisch_imagelib2.Interval;
import preibisch_imagelib2.Localizable;
import preibisch_imagelib2.RandomAccess;
import preibisch_imagelib2.RandomAccessible;

public class HorizontalLineNeighborhoodRandomAccess< T > extends HorizontalLineNeighborhoodLocalizableSampler< T > implements RandomAccess< Neighborhood< T > >
{
	public HorizontalLineNeighborhoodRandomAccess( final RandomAccessible< T > source, final long span, final int dim, final boolean skipCenter, final HorizontalLineNeighborhoodFactory< T > factory, final Interval interval )
	{
		super( source, span, dim, skipCenter, factory, interval );
	}

	public HorizontalLineNeighborhoodRandomAccess( final RandomAccessible< T > source, final long span, final int dim, final boolean skipCenter, final HorizontalLineNeighborhoodFactory< T > factory )
	{
		super( source, span, dim, skipCenter, factory, null );
	}

	private HorizontalLineNeighborhoodRandomAccess( final HorizontalLineNeighborhoodRandomAccess< T > c )
	{
		super( c );
	}

	@Override
	public void fwd( final int d )
	{
		++currentPos[ d ];
	}

	@Override
	public void bck( final int d )
	{
		--currentPos[ d ];
	}

	@Override
	public void move( final int distance, final int d )
	{
		currentPos[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		currentPos[ d ] += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = localizable.getLongPosition( d );
			currentPos[ d ] += distance;
		}
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] += distance[ d ];
		}
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] += distance[ d ];
		}
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long position = localizable.getLongPosition( d );
			currentPos[ d ] = position;
		}
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] = position[ d ];
		}
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] = position[ d ];
		}
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		currentPos[ d ] = position;
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		currentPos[ d ] = position;
	}

	@Override
	public HorizontalLineNeighborhoodRandomAccess< T > copy()
	{
		return new HorizontalLineNeighborhoodRandomAccess< T >( this );
	}

	@Override
	public HorizontalLineNeighborhoodRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}
