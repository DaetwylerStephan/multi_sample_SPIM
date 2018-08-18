/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */


package preibisch_imagelib2.type.numeric.integer;

import preibisch_imagelib2.img.NativeImg;
import preibisch_imagelib2.img.basictypeaccess.LongAccess;
import preibisch_imagelib2.type.AbstractBitType;
import preibisch_imagelib2.type.numeric.IntegerType;
import preibisch_imagelib2.util.Util;

/**
 * The performance of this type is traded off for the gain in memory storage.
 * 
 * @author Albert Cardona
 */
public abstract class AbstractIntegerBitType<T extends AbstractIntegerBitType<T>> extends AbstractBitType<T> implements IntegerType<T> 
{
	// Maximum count is Integer.MAX_VALUE * (64 / getBitsPerPixel())
	//protected long i = 0;

	//final protected NativeImg<T, ? extends LongAccess> img;

	// the DataAccess that holds the information
	//protected LongAccess dataAccess;

	// this is the constructor if you want it to read from an array
	public AbstractIntegerBitType(
			final NativeImg<T,? extends LongAccess> bitStorage, 
			final int nBits )
	{
		super( bitStorage, nBits );
	}

	public abstract long get();
	public abstract void set( final long value );

	@Override
	public int getBitsPerPixel() { return nBits; }

	@Override
	public double getMinIncrement() { return 1; }

	@Override
	public void mul( final float c ) { setReal( getRealDouble() * c ); }

	@Override
	public void mul( final double c ) { setReal( getRealDouble() * c ); }

	@Override
	public float getRealFloat() { return getIntegerLong(); }
	@Override
	public double getRealDouble() { return getIntegerLong(); }
	
	@Override
	public void setReal( final float real ){ setInteger( Util.round( real ) ); }
	@Override
	public void setReal( final double real ){ setInteger( Util.round( real ) ); }	

	@Override
	public void setZero() { setInteger( 0 ); }
	@Override
	public void setOne() { setInteger( 1 ); }	

	@Override
	public int compareTo( final T c ) 
	{ 
		final long a = getIntegerLong();
		final long b = c.getIntegerLong();
		if ( a > b )
			return 1;
		else if ( a < b )
			return -1;
		else 
			return 0;
	}
	
	@Override
	public String toString() { return "" + getIntegerLong(); }	
	
	@Override
	public int getInteger() { return (int)get(); }

	@Override
	public long getIntegerLong() { return get(); }

	@Override
	public void setInteger( final int f ) { set( f ); }

	@Override
	public void setInteger( final long f ) { set( f ); }

	/** The maximum value that can be stored is {@code Math.pow(2, nBits) -1}. */
	@Override
	public double getMaxValue() { return Math.pow(2, getBitsPerPixel()) -1; }
	@Override
	public double getMinValue()  { return 0; }

	@Override
	public void inc() {	set(get() + 1); }

	@Override
	public void dec() {	set(get() - 1); }

	@Override
	public void add(final T t) { set(get() + t.get()); }

	@Override
	public void sub(final T t) { set(get() - t.get()); }

	@Override
	public void mul(final T t) { set(get() * t.get()); }

	@Override
	public void div(final T t) { set(get() / t.get()); }
	
	@Override
	public void set( final T c ) { set( c.get() ); }

	@Override
	public float getImaginaryFloat() { return 0; }
	@Override
	public double getImaginaryDouble() { return 0; }
	
	@Override
	public void setImaginary( final float complex ){}
	@Override
	public void setImaginary( final double complex ){}

	@Override
	public float getPhaseFloat() { return 0; }
	@Override
	public double getPhaseDouble() { return 0; }

	@Override
	public float getPowerFloat() { return getRealFloat(); }
	@Override
	public double getPowerDouble() { return getRealDouble(); }
	
	@Override
	public void setComplexNumber( final float r, final float i ) { setReal( r ); }
	@Override
	public void setComplexNumber( final double r, final double i ) { setReal( r ); }

	@Override
	public void complexConjugate(){}
}
