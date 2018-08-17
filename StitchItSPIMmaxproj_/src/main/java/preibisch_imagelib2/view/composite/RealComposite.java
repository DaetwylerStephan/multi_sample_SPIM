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
package preibisch_imagelib2.view.composite;

import preibisch_imagelib2.RandomAccess;
import preibisch_imagelib2.img.Img;
import preibisch_imagelib2.img.array.ArrayImg;
import preibisch_imagelib2.img.array.ArrayImgFactory;
import preibisch_imagelib2.img.list.ListImgFactory;
import preibisch_imagelib2.type.NativeType;
import preibisch_imagelib2.type.numeric.NumericType;
import preibisch_imagelib2.type.numeric.RealType;

/**
 * A vector of {@link RealType} scalars.  It is a {@link NumericType}
 * itself, implementing the {@link NumericType} algebra as element-wise
 * operations.
 *
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 */
public class RealComposite< T extends RealType< T > > extends AbstractNumericComposite< T, RealComposite< T > >
{
	static public class Factory< T extends RealType< T > > implements CompositeFactory< T, RealComposite< T > > 
	{
		final protected int numChannels;
		
		public Factory( final int numChannels )
		{
			this.numChannels = numChannels;
		}
		
		@Override
		public RealComposite< T > create( final RandomAccess< T > sourceAccess )
		{
			return new RealComposite< T >( sourceAccess, numChannels );
		}
	}
	
	public RealComposite( final RandomAccess< T > sourceAccess, final int length )
	{
		super( sourceAccess, length );
	}

	/**
	 * Generates a 1D {@link ArrayImg}&lt;T&gt; 
	 */
	@Override
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public RealComposite< T > createVariable()
	{
		final T t = sourceAccess.get();
		final Img< T > img;
		if ( NativeType.class.isInstance( t ) )
			img = ( ( NativeType )t ).createSuitableNativeImg( new ArrayImgFactory(), new long[]{ length } );
		else
			img = new ListImgFactory< T >().create( new long[]{ length }, t );
		return new RealComposite< T >( img.randomAccess(), length );
	}

	@Override
	public RealComposite< T > copy()
	{
		return new RealComposite< T >( sourceAccess.copyRandomAccess(), length );
	}
}
