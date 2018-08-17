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
package preibisch_imagelib2.display.screenimage.awt;

import java.awt.image.DataBufferByte;

import preibisch_imagelib2.img.array.ArrayImg;
import preibisch_imagelib2.img.basictypeaccess.array.ByteArray;
import preibisch_imagelib2.type.numeric.integer.UnsignedByteType;

/**
 * A {@link AWTScreenImage} that is an
 * {@code ArrayImg<UnsignedByteType, ByteArray>}.
 * 
 * @author Michael Zinsmaier
 * @author Martin Horn
 * @author Christian Dietz
 * @author Curtis Rueden
 */
public class UnsignedByteAWTScreenImage extends ArrayImgAWTScreenImage< UnsignedByteType, ByteArray >
{

	public UnsignedByteAWTScreenImage( final ArrayImg< UnsignedByteType, ByteArray > img )
	{
		super( img );
	}

	public UnsignedByteAWTScreenImage( final UnsignedByteType type, final ByteArray data, final long[] dim )
	{
		super( type, data, dim );
	}

	@Override
	protected DataBufferByte createDataBuffer( final ByteArray data )
	{
		final byte[] sourceArray = data.getCurrentStorageArray();
		return new DataBufferByte( sourceArray, sourceArray.length );
	}

}
