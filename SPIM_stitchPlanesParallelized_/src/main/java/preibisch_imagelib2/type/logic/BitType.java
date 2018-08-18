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

package preibisch_imagelib2.type.logic;

import preibisch_imagelib2.img.NativeImg;
import preibisch_imagelib2.img.NativeImgFactory;
import preibisch_imagelib2.img.basictypeaccess.LongAccess;
import preibisch_imagelib2.img.basictypeaccess.array.LongArray;
import preibisch_imagelib2.type.BooleanType;
import preibisch_imagelib2.type.NativeType;
import preibisch_imagelib2.type.numeric.IntegerType;
import preibisch_imagelib2.type.numeric.integer.AbstractIntegerType;
import preibisch_imagelib2.util.Fraction;

/**
 * TODO
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class BitType extends AbstractIntegerType<BitType> implements BooleanType<BitType>, NativeType<BitType>, IntegerType<BitType>
{
	// Maximum count is Integer.MAX_VALUE * (64 / getBitsPerPixel())
	protected int i = 0;

	final protected NativeImg<BitType, ? extends LongAccess> img;

	// the DataAccess that holds the information
	protected LongAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public BitType( final NativeImg<BitType,? extends LongAccess> bitStorage )
	{
		img = bitStorage;
	}

	// this is the constructor if you want it to be a variable
	public BitType( final boolean value )
	{
		this( (NativeImg<BitType, ? extends LongAccess>)null );
		dataAccess = new LongArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public BitType( final LongAccess access )
	{
		this( (NativeImg<BitType, ? extends LongAccess>)null );
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public BitType()
	{
		this( false );
	}

	@Override
	public NativeImg<BitType, ? extends LongAccess> createSuitableNativeImg( final NativeImgFactory<BitType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<BitType, ? extends LongAccess> container = storageFactory.createLongInstance( dim, new Fraction( 1, 64 ) );

		// create a Type that is linked to the container
		final BitType linkedType = new BitType( container );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public void updateContainer( final Object c )
	{
		dataAccess = img.update( c );
	}

	@Override
	public BitType duplicateTypeOnSameNativeImg()
	{
		return new BitType( img );
	}

	@Override
	public boolean get()
	{
		return 1 == ((dataAccess.getValue((int)(i >>> 6)) >>> ((i & 63))) & 1l);
	}

	// Crops value to within mask
	@Override
	public void set( final boolean value ) {
		/*
		final int k = i * 2;
		final int i1 = k >>> 6; // k / 64;
		final long shift = k % 64;
		*/
		// Same as above, minus one multiplication, plus one shift to multiply the reminder by 2
		final int i1 = (int)(i >>> 6); // Same as (i * 2) / 64 = (i << 1) >>> 6
		final long bit = 1l << (i & 63);
		synchronized ( dataAccess ) {
			// Clear or set the bit
			if ( value )
				dataAccess.setValue( i1, dataAccess.getValue(i1) | bit );
			else
				dataAccess.setValue( i1, dataAccess.getValue(i1) & ~bit );
		}
	}

	@Override
	public int getInteger()
	{
		return get() ? 1 : 0;
	}

	@Override
	public long getIntegerLong()
	{
		return get() ? 1 : 0;
	}

	@Override
	public void setInteger( final int f )
	{
		if ( f >= 1 )
			set( true );
		else
			set( false );
	}

	@Override
	public void setInteger( final long f )
	{
		if ( f >= 1 )
			set( true );
		else
			set( false );
	}

	@Override
	public double getMaxValue()
	{
		return 1;
	}

	@Override
	public double getMinValue()
	{
		return 0;
	}

	@Override
	public void set( final BitType c ) { set( c.get() ); }

	@Override
	public void and( final BitType c ) { set( get() && c.get() ); }

	@Override
	public void or( final BitType c ) { set( get() || c.get() ); }

	@Override
	public void xor( final BitType c ) { set( get() ^ c.get() ); }

	@Override
	public void not() { set( !get() ); }

	@Override
	public void add( final BitType c )
	{
		xor( c );
	}

	@Override
	public void div( final BitType c )
	{
		and( c );
	}

	@Override
	public void mul( final BitType c )
	{
		and( c );
	}

	@Override
	public void sub( final BitType c )
	{
		xor( c );
	}

	@Override
	public void mul( final float c )
	{
		if ( c >= 0.5f )
			set( get() && true );
		else
			set( false );
	}

	@Override
	public void mul( final double c )
	{
		if ( c >= 0.5f )
			set( get() && true );
		else
			set( false );
	}

	@Override
	public void setOne() { set( true ); }

	@Override
	public void setZero() { set( false ); }

	@Override
	public void inc() { not(); }

	@Override
	public void dec() { not(); }

	@Override
	public int compareTo( final BitType c )
	{
		final boolean b1 = get();
		final boolean b2 = c.get();

		if ( b1 && !b2 )
			return 1;
		else if ( !b1 && b2 )
			return -1;
		else
			return 0;
	}

	@Override
	public BitType createVariable()
	{
		return new BitType();
	}

	@Override
	public BitType copy(){ return new BitType( get() ); }

	@Override
	public String toString()
	{
		final boolean value = get();

		return value ? "1" : "0";
	}

	@Override
	public Fraction getEntitiesPerPixel() { return new Fraction(); }

	@Override
	public void updateIndex( final int index )
	{
		this.i = index;
	}

	@Override
	public int getIndex()
	{
		return i;
	}

	@Override
	public void incIndex()
	{
		++i;
	}

	@Override
	public void incIndex( final int increment )
	{
		i += increment;
	}

	@Override
	public void decIndex()
	{
		--i;
	}

	@Override
	public void decIndex( final int decrement )
	{
		i -= decrement;
	}

	@Override
	public int getBitsPerPixel()
	{
		return 1;
	}
}
