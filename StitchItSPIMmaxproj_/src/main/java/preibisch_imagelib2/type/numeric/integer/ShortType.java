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

package preibisch_imagelib2.type.numeric.integer;

import preibisch_imagelib2.img.NativeImg;
import preibisch_imagelib2.img.NativeImgFactory;
import preibisch_imagelib2.img.basictypeaccess.ShortAccess;
import preibisch_imagelib2.util.Fraction;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ShortType extends GenericShortType< ShortType >
{
	// this is the constructor if you want it to read from an array
	public ShortType( final NativeImg< ?, ? extends ShortAccess > img )
	{
		super( img );
	}

	// this is the constructor if you want it to be a variable
	public ShortType( final short value )
	{
		super( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public ShortType( final ShortAccess access )
	{
		super( access );
	}

	// this is the constructor if you want it to be a variable
	public ShortType()
	{
		this( ( short ) 0 );
	}

	@Override
	public NativeImg< ShortType, ? extends ShortAccess > createSuitableNativeImg( final NativeImgFactory< ShortType > storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<ShortType, ? extends ShortAccess> container = storageFactory.createShortInstance( dim, new Fraction() );

		// create a Type that is linked to the container
		final ShortType linkedType = new ShortType( container );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public ShortType duplicateTypeOnSameNativeImg()
	{
		return new ShortType( img );
	}

	public short get()
	{
		return getValue();
	}

	public void set( final short b )
	{
		setValue( b );
	}

	@Override
	public int getInteger()
	{
		return get();
	}

	@Override
	public long getIntegerLong()
	{
		return get();
	}

	@Override
	public void setInteger( final int f )
	{
		set( ( short ) f );
	}

	@Override
	public void setInteger( final long f )
	{
		set( ( short ) f );
	}

	@Override
	public double getMaxValue()
	{
		return Short.MAX_VALUE;
	}

	@Override
	public double getMinValue()
	{
		return Short.MIN_VALUE;
	}

	@Override
	public ShortType createVariable()
	{
		return new ShortType( ( short ) 0 );
	}

	@Override
	public ShortType copy()
	{
		return new ShortType( getValue() );
	}
}
