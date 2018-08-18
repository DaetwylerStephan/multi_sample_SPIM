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
import preibisch_imagelib2.img.basictypeaccess.LongAccess;
import preibisch_imagelib2.img.basictypeaccess.array.LongArray;
import preibisch_imagelib2.util.Fraction;

/**
 * A 12-bit {@link Type} whose data is stored in a {@link LongAccess}.
 * 
 * @author Albert Cardona
 * @author Stephan Preibisch
 *
 */
public class Unsigned12BitType extends AbstractIntegerBitType<Unsigned12BitType>
{
	// A mask for bit and, containing nBits of 1
	private final long mask;

	// this is the constructor if you want it to read from an array
	public Unsigned12BitType(
			final NativeImg<Unsigned12BitType,
			? extends LongAccess> bitStorage)
	{
		super( bitStorage, 12 );
		this.mask = 4095; // 111111111111 in binary
	}

	// this is the constructor if you want it to be a variable
	public Unsigned12BitType( final long value )
	{
		this( (NativeImg<Unsigned12BitType, ? extends LongAccess>)null );
		dataAccess = new LongArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public Unsigned12BitType( final LongAccess access )
	{
		this( (NativeImg<Unsigned12BitType, ? extends LongAccess>)null );
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public Unsigned12BitType() { this( 0 ); }

	@Override
	public NativeImg<Unsigned12BitType, ? extends LongAccess> createSuitableNativeImg( final NativeImgFactory<Unsigned12BitType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<Unsigned12BitType, ? extends LongAccess> container = storageFactory.createLongInstance( dim, new Fraction( getBitsPerPixel(), 64 ) );

		// create a Type that is linked to the container
		final Unsigned12BitType linkedType = new Unsigned12BitType( container );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public Unsigned12BitType duplicateTypeOnSameNativeImg() { return new Unsigned12BitType( img ); }

	@Override
	public long get() {
		final long k = i * 12;
		final int i1 = (int)(k >>> 6); // k / 64;
		final long shift = k & 63; // k % 64;		
		final long v = dataAccess.getValue(i1);
		final long antiShift = 64 - shift;
		
		if (antiShift < 12) {
			// Number split between two adjacent long
			final long v1 = (v >>> shift) & (mask >>> (12 - antiShift)); // lower part, stored at the upper end
			final long v2 = (dataAccess.getValue(i1 + 1) & (mask >>> antiShift)) << antiShift; // upper part, stored at the lower end
			return v1 | v2;
		} else {
			// Number contained inside a single long
			return (v >>> shift) & mask;
		}
	}

	// Crops value to within mask
	@Override
	public void set( final long value ) {
		final long k = i * 12;
		final int i1 = (int)(k >>> 6); // k / 64;
		final long shift = k & 63; // k % 64;
		final long safeValue = value & mask;

		final long antiShift = 64 - shift;
		final long v = dataAccess.getValue(i1);
		if (antiShift < 12) {
			// Number split between two adjacent longs
			// 1. Store the lower bits of safeValue at the upper bits of v1
			final long v1 = (v & (0xffffffffffffffffL >>> antiShift)) // clear upper bits, keep other values
					| ((safeValue & (mask >>> (12 - antiShift))) << shift); // the lower part of safeValue, stored at the upper end
			dataAccess.setValue(i1, v1);
			// 2. Store the upper bits of safeValue at the lower bits of v2
			final long v2 = (dataAccess.getValue(i1 + 1) & (0xffffffffffffffffL << (12 - antiShift))) // other
					| (safeValue >>> antiShift); // upper part of safeValue, stored at the lower end
			dataAccess.setValue(i1 + 1, v2);
		} else {
			// Number contained inside a single long
			if (0 == v) {
				// Trivial case
				dataAccess.setValue(i1, safeValue << shift);
			} else {
				// Clear the bits first
				dataAccess.setValue(i1, (v & ~(mask << shift)) | (safeValue << shift));
			}
		}
	}

	@Override
	public Unsigned12BitType createVariable(){ return new Unsigned12BitType( 0 ); }

	@Override
	public Unsigned12BitType copy(){ return new Unsigned12BitType( get() ); }
}
