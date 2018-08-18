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

package preibisch_imagelib2.img;

import preibisch_imagelib2.Cursor;
import preibisch_imagelib2.FlatIterationOrder;
import preibisch_imagelib2.Interval;
import preibisch_imagelib2.IterableInterval;
import preibisch_imagelib2.IterableRealInterval;
import preibisch_imagelib2.RandomAccess;
import preibisch_imagelib2.RandomAccessibleInterval;
import preibisch_imagelib2.type.Type;
import preibisch_imagelib2.view.IterableRandomAccessibleInterval;
import preibisch_imagelib2.view.Views;
import preibisch_imagelib2.view.iteration.SubIntervalIterable;

/**
 * Allows a {@link RandomAccessibleInterval} to be treated as an {@link Img}.
 * 
 * @author Tobias Pietzsch
 * @author Christian Dietz (dietzc85@googlemail.com)
 */
public class ImgView< T extends Type< T > > extends
		IterableRandomAccessibleInterval< T > implements Img< T >, SubIntervalIterable< T >
{

	// factory
	private final ImgFactory< T > factory;

	// ImgView ii
	private final IterableInterval< T > ii;

	/**
	 * View on {@link Img} which is defined by a given Interval, but still is an
	 * {@link Img}.
	 * 
	 * @param in
	 *            Source interval for the view
	 * @param fac
	 *            <T> Factory to create img
	 */
	public ImgView( final RandomAccessibleInterval< T > in, final ImgFactory< T > fac )
	{
		super( in );
		factory = fac;
		ii = Views.flatIterable( in );
	}

	@Override
	public ImgFactory< T > factory()
	{
		return factory;
	}

	@Override
	public Img< T > copy()
	{
		final Img< T > copy = factory.create( this, randomAccess().get()
				.createVariable() );

		final Cursor< T > srcCursor = localizingCursor();
		final RandomAccess< T > resAccess = copy.randomAccess();

		while ( srcCursor.hasNext() )
		{
			srcCursor.fwd();
			resAccess.setPosition( srcCursor );
			resAccess.get().set( srcCursor.get() );
		}

		return copy;
	}

	@Override
	public Cursor< T > cursor()
	{
		return ii.cursor();
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return ii.localizingCursor();
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean supportsOptimizedCursor( final Interval interval )
	{
		if ( this.sourceInterval instanceof SubIntervalIterable )
			return ( ( SubIntervalIterable< T > ) this.sourceInterval ).supportsOptimizedCursor( interval );
		else
			return false;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Object subIntervalIterationOrder( final Interval interval )
	{
		if ( this.sourceInterval instanceof SubIntervalIterable )
			return ( ( SubIntervalIterable< T > ) this.sourceInterval ).subIntervalIterationOrder( interval );
		else
			return new FlatIterationOrder( interval );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Cursor< T > cursor( final Interval interval )
	{
		if ( this.sourceInterval instanceof SubIntervalIterable )
			return ( ( SubIntervalIterable< T > ) this.sourceInterval ).cursor( interval );
		else
			return Views.interval( this.sourceInterval, interval ).cursor();
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Cursor< T > localizingCursor( final Interval interval )
	{
		if ( this.sourceInterval instanceof SubIntervalIterable )
			return ( ( SubIntervalIterable< T > ) this.sourceInterval ).localizingCursor( interval );
		else
			return Views.interval( this.sourceInterval, interval )
					.localizingCursor();
	}
}
