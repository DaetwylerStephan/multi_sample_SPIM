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

package preibisch_imagelib2.view;

import java.util.Iterator;

import preibisch_imagelib2.AbstractWrappedInterval;
import preibisch_imagelib2.Cursor;
import preibisch_imagelib2.FlatIterationOrder;
import preibisch_imagelib2.Interval;
import preibisch_imagelib2.IterableInterval;
import preibisch_imagelib2.IterableRealInterval;
import preibisch_imagelib2.RandomAccess;
import preibisch_imagelib2.RandomAccessibleInterval;

/**
 * Generates {@link Cursor Cursors} that iterate a
 * {@link RandomAccessibleInterval} in flat order, that is: row by row, plane by
 * plane, cube by cube, ...
 * 
 * @author Stephan Saalfeld
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class IterableRandomAccessibleInterval< T > extends AbstractWrappedInterval< RandomAccessibleInterval< T > > implements IterableInterval< T >, RandomAccessibleInterval< T >
{
	final long size;

	public static < T > IterableRandomAccessibleInterval< T > create( final RandomAccessibleInterval< T > interval )
	{
		return new IterableRandomAccessibleInterval< T >( interval );
	}

	public IterableRandomAccessibleInterval( final RandomAccessibleInterval< T > interval )
	{
		super( interval );
		final int n = numDimensions();
		long s = interval.dimension( 0 );
		for ( int d = 1; d < n; ++d )
			s *= interval.dimension( d );
		size = s;
	}

	@Override
	public long size()
	{
		return size;
	}

	@Override
	public T firstElement()
	{
		// we cannot simply create an randomaccessible on interval
		// this does not ensure it will be placed at the first element
		return cursor().next();
	}

	@Override
	public FlatIterationOrder iterationOrder()
	{
		return new FlatIterationOrder( sourceInterval );
	}

	@Override
	public Iterator< T > iterator()
	{
		return cursor();
	}

	@Override
	public Cursor< T > cursor()
	{
		return new RandomAccessibleIntervalCursor< T >( sourceInterval );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return cursor();
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return sourceInterval.randomAccess();
	}

	@Override
	public RandomAccess< T > randomAccess( final Interval i )
	{
		return sourceInterval.randomAccess( i );
	}
}
