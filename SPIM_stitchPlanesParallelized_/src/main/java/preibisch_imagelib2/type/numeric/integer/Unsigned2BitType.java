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
import preibisch_imagelib2.img.NativeImgFactory;
import preibisch_imagelib2.img.basictypeaccess.LongAccess;
import preibisch_imagelib2.img.basictypeaccess.array.LongArray;
import preibisch_imagelib2.type.Type;
import preibisch_imagelib2.util.Fraction;

/**
 * A {@link Type} with a bit depth of 2.
 * 
 * The performance of this type is traded off for the gain in memory storage.
 * 
 * @author Albert Cardona
 */
public class Unsigned2BitType extends AbstractIntegerBitType<Unsigned2BitType>
{
	// A mask for bit and, containing nBits of 1
	private final static long mask = 3; // 11 in binary

	// this is the constructor if you want it to read from an array
	public Unsigned2BitType(
			final NativeImg<Unsigned2BitType,
			? extends LongAccess> bitStorage)
	{
		super( bitStorage, 2 );
	}

	// this is the constructor if you want it to be a variable
	public Unsigned2BitType( final long value )
	{
		this( (NativeImg<Unsigned2BitType, ? extends LongAccess>)null );
		dataAccess = new LongArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public Unsigned2BitType( final LongAccess access )
	{
		this( (NativeImg<Unsigned2BitType, ? extends LongAccess>)null );
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public Unsigned2BitType() { this( 0 ); }

	@Override
	public NativeImg<Unsigned2BitType, ? extends LongAccess> createSuitableNativeImg( final NativeImgFactory<Unsigned2BitType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<Unsigned2BitType, ? extends LongAccess> container = storageFactory.createLongInstance( dim, new Fraction( getBitsPerPixel(), 64 ) );

		// create a Type that is linked to the container
		final Unsigned2BitType linkedType = new Unsigned2BitType( container );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public Unsigned2BitType duplicateTypeOnSameNativeImg() { return new Unsigned2BitType( img ); }

	@Override
	public long get() {
		/*
		final int k = i * 2;
		return (dataAccess.getValue(k >>> 6) >>> (k % 64)) & mask;
		*/
		// Same as above minus one multiplication, plus one shift to multiply the reminder by 2
		//return (dataAccess.getValue((int)(i >>> 5)) >>> ((i % 32) << 1)) & mask;
		// Even less operations
		// div 32 == shr 5
		return (dataAccess.getValue((int)(i >>> 5)) >>> ((i & 31) << 1)) & mask;
	}

	// Crops value to within mask
	@Override
	public void set( final long value ) {
		/*
		final int k = i * 2;
		final int i1 = k >>> 6; // k / 64;
		final long shift = k % 64;
		*/
		// Same as above, minus one multiplication, plus one shift to multiply the reminder by 2
		final int i1 = (int)(i >>> 5); // Same as (i * 2) / 64 = (i << 1) >>> 6
		final long shift = (i << 1) & 63; // Same as (i * 2) % 64
		// Clear the bits first, then or the masked value
		dataAccess.setValue(i1, (dataAccess.getValue(i1) & ~(mask << shift)) | ((value & mask) << shift));
	}

	@Override
	public Unsigned2BitType createVariable(){ return new Unsigned2BitType( 0 ); }

	@Override
	public Unsigned2BitType copy(){ return new Unsigned2BitType( get() ); }
}
