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

package preibisch_imagelib2.img.list;

import java.util.ArrayList;

import preibisch_imagelib2.FlatIterationOrder;
import preibisch_imagelib2.img.AbstractImg;
import preibisch_imagelib2.img.Img;
import preibisch_imagelib2.type.Type;
import preibisch_imagelib2.util.IntervalIndexer;

/**
 * Abstract base class for {@link Img} that store pixels in a single linear
 * array (an {@link ArrayList} or similar).In principle, the number of entities
 * stored is limited to {@link Integer#MAX_VALUE}.
 * 
 * Derived classes need to implement the {@link #get(int)} and
 * {@link #set(int, Object)} methods that are used by accessors to access
 * pixels. These could be implemented to fetch pixels from an {@link ArrayList},
 * create them on the fly, cache to disk, etc.
 * 
 * @param <T>
 *            The value type of the pixels. You can us {@link Type}s or
 *            arbitrary {@link Object}s. If you use non-{@link Type} pixels,
 *            note, that you cannot use {@link Type#set(Type)} to change the
 *            value stored in every reference. Instead, you can use the
 *            {@link ListCursor#set(Object)} and
 *            {@link ListRandomAccess#set(Object)} methods to alter pixels.
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public abstract class AbstractListImg< T > extends AbstractImg< T >
{
	final protected int[] step;

	final protected int[] dim;

	protected AbstractListImg( final long[] dim )
	{
		super( dim );

		this.dim = new int[ n ];
		for ( int d = 0; d < n; ++d )
			this.dim[ d ] = ( int ) dim[ d ];

		step = new int[ n ];
		IntervalIndexer.createAllocationSteps( this.dim, step );
	}

	protected abstract T get( final int index );

	protected abstract void set( final int index, final T value );

	@Override
	public ListCursor< T > cursor()
	{
		return new ListCursor< T >( this );
	}

	@Override
	public ListLocalizingCursor< T > localizingCursor()
	{
		return new ListLocalizingCursor< T >( this );
	}

	@Override
	public ListRandomAccess< T > randomAccess()
	{
		return new ListRandomAccess< T >( this );
	}

	@Override
	public ListImgFactory< T > factory()
	{
		return new ListImgFactory< T >();
	}

	@Override
	public FlatIterationOrder iterationOrder()
	{
		return new FlatIterationOrder( this );
	}
}
