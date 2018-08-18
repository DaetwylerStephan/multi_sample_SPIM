/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package preibisch_imagelib2.img.array;

import preibisch_imagelib2.Cursor;
import preibisch_imagelib2.FlatIterationOrder;
import preibisch_imagelib2.Interval;
import preibisch_imagelib2.img.AbstractNativeImg;
import preibisch_imagelib2.img.Img;
import preibisch_imagelib2.type.NativeType;
import preibisch_imagelib2.util.Fraction;
import preibisch_imagelib2.util.IntervalIndexer;
import preibisch_imagelib2.util.Intervals;
import preibisch_imagelib2.view.iteration.SubIntervalIterable;

/**
 * This {@link Img} stores an image in a single linear array of basic types. By
 * that, it provides the fastest possible access to data while limiting the
 * number of basic types stored to {@link Integer#MAX_VALUE}. Keep in mind that
 * this does not necessarily reflect the number of pixels, because a pixel can
 * be stored in less than or more than a basic type entry.
 * 
 * @param <T>
 * @param <A>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class ArrayImg< T extends NativeType< T >, A > extends AbstractNativeImg< T, A > implements SubIntervalIterable< T >
{
	final int[] steps, dim;

	// the DataAccess created by the ArrayContainerFactory
	final private A data;

	/**
	 * TODO check for the size of numPixels being < Integer.MAX_VALUE? TODO Type
	 * is suddenly not necessary anymore
	 * 
	 * @param factory
	 * @param data
	 * @param dim
	 * @param entitiesPerPixel
	 */
	public ArrayImg( final A data, final long[] dim, final Fraction entitiesPerPixel )
	{
		super( dim, entitiesPerPixel );
		this.dim = new int[ n ];
		for ( int d = 0; d < n; ++d )
			this.dim[ d ] = ( int ) dim[ d ];

		this.steps = new int[ n ];
		IntervalIndexer.createAllocationSteps( this.dim, this.steps );
		this.data = data;
	}

	@Override
	public A update( final Object o )
	{
		return data;
	}

	@Override
	public ArrayCursor< T > cursor()
	{
		return new ArrayCursor< T >( this );
	}

	@Override
	public ArrayLocalizingCursor< T > localizingCursor()
	{
		return new ArrayLocalizingCursor< T >( this );
	}

	@Override
	public ArrayRandomAccess< T > randomAccess()
	{
		return new ArrayRandomAccess< T >( this );
	}

	@Override
	public ArrayRandomAccess< T > randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	@Override
	public FlatIterationOrder iterationOrder()
	{
		return new FlatIterationOrder( this );
	}

	@Override
	public ArrayImgFactory< T > factory()
	{
		return new ArrayImgFactory< T >();
	}

	@Override
	public ArrayImg< T, ? > copy()
	{
		final ArrayImg< T, ? > copy = factory().create( dimension, firstElement().createVariable() );

		final ArrayCursor< T > source = this.cursor();
		final ArrayCursor< T > target = copy.cursor();

		while ( source.hasNext() )
			target.next().set( source.next() );

		return copy;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cursor< T > cursor( final Interval interval )
	{
		final int dimLength = fastCursorAvailable( interval );

		assert dimLength > 0;

		return new ArraySubIntervalCursor< T >( this, ( int ) offset( interval, dimLength ), ( int ) size( interval, dimLength ) );
	}

	private long size( final Interval interval, final int length )
	{
		long size = interval.dimension( 0 );
		for ( int d = 1; d < length; ++d )
		{
			size *= interval.dimension( d );
		}

		return size;
	}

	private long offset( final Interval interval, final int length )
	{
		final int maxDim = numDimensions() - 1;
		long i = interval.min( maxDim );
		for ( int d = maxDim - 1; d >= 0; --d )
		{
			i = i * dimension( d ) + interval.min( d );
		}

		return i;
	}

	/**
	 * If method returns -1 no fast cursor is available, else the amount of dims
	 * (starting from zero) which can be iterated fast are returned.
	 */
	private int fastCursorAvailable( final Interval interval )
	{
		// first check whether the interval is completely contained.
		if ( !Intervals.contains( this, interval ) )
			return -1;

		// find the first dimension in which image and interval differ
		int dimIdx = 0;
		for ( ; dimIdx < n; ++dimIdx )
			if ( interval.dimension( dimIdx ) != dimension( dimIdx ) )
				break;

		if ( dimIdx == n )
			return dimIdx;

		// in the dimension after that, image and interval may differ
		++dimIdx;

		// but image extents of all higher dimensions must equal 1
		for ( int d = dimIdx; d < n; ++d )
			if ( interval.dimension( d ) != 1 )
				return -1;

		return dimIdx;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cursor< T > localizingCursor( final Interval interval )
	{
		final int dimLength = fastCursorAvailable( interval );

		assert dimLength > 0;

		return new ArrayLocalizingSubIntervalCursor< T >( this, ( int ) offset( interval, dimLength ), ( int ) size( interval, dimLength ) );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supportsOptimizedCursor( final Interval interval )
	{
		return fastCursorAvailable( interval ) > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object subIntervalIterationOrder( final Interval interval )
	{
		return new FlatIterationOrder( interval );
	}
}
