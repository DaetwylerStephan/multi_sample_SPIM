/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package preibisch_imagelib2.algorithm.neighborhood;

import preibisch_imagelib2.Interval;
import preibisch_imagelib2.Localizable;
import preibisch_imagelib2.RandomAccess;
import preibisch_imagelib2.RandomAccessible;

public final class RectangleNeighborhoodRandomAccess< T > extends RectangleNeighborhoodLocalizableSampler< T > implements RandomAccess< Neighborhood< T > >
{
	public RectangleNeighborhoodRandomAccess( final RandomAccessible< T > source, final Interval span, final RectangleNeighborhoodFactory< T > factory )
	{
		super( source, span, factory, null );
	}

	public RectangleNeighborhoodRandomAccess( final RandomAccessible< T > source, final Interval span, final RectangleNeighborhoodFactory< T > factory, final Interval interval )
	{
		super( source, span, factory, interval );
	}

	private RectangleNeighborhoodRandomAccess( final RectangleNeighborhoodRandomAccess< T > c )
	{
		super( c );
	}

	@Override
	public void fwd( final int d )
	{
		++currentPos[ d ];
		++currentMin[ d ];
		++currentMax[ d ];
	}

	@Override
	public void bck( final int d )
	{
		--currentPos[ d ];
		--currentMin[ d ];
		--currentMax[ d ];
	}

	@Override
	public void move( final int distance, final int d )
	{
		currentPos[ d ] += distance;
		currentMin[ d ] += distance;
		currentMax[ d ] += distance;
	}

	@Override
	public void move( final long distance, final int d )
	{
		currentPos[ d ] += distance;
		currentMin[ d ] += distance;
		currentMax[ d ] += distance;
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long distance = localizable.getLongPosition( d );
			currentPos[ d ] += distance;
			currentMin[ d ] += distance;
			currentMax[ d ] += distance;
		}
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] += distance[ d ];
			currentMin[ d ] += distance[ d ];
			currentMax[ d ] += distance[ d ];
		}
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] += distance[ d ];
			currentMin[ d ] += distance[ d ];
			currentMax[ d ] += distance[ d ];
		}
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long position = localizable.getLongPosition( d );
			currentPos[ d ] = position;
			currentMin[ d ] = position + span.min( d );
			currentMax[ d ] = position + span.max( d );
		}
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] = position[ d ];
			currentMin[ d ] = position[ d ] + span.min( d );
			currentMax[ d ] = position[ d ] + span.max( d );
		}
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			currentPos[ d ] = position[ d ];
			currentMin[ d ] = position[ d ] + span.min( d );
			currentMax[ d ] = position[ d ] + span.max( d );
		}
	}

	@Override
	public void setPosition( final int position, final int d )
	{
		currentPos[ d ] = position;
		currentMin[ d ] = position + span.min( d );
		currentMax[ d ] = position + span.max( d );
	}

	@Override
	public void setPosition( final long position, final int d )
	{
		currentPos[ d ] = position;
		currentMin[ d ] = position + span.min( d );
		currentMax[ d ] = position + span.max( d );
	}

	@Override
	public RectangleNeighborhoodRandomAccess< T > copy()
	{
		return new RectangleNeighborhoodRandomAccess< T >( this );
	}

	@Override
	public RectangleNeighborhoodRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}