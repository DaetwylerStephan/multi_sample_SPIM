/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

package preibisch_imagelib2.img.imageplus;

import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
import preibisch_imagelib2.exception.ImgLibException;
import preibisch_imagelib2.img.basictypeaccess.array.FloatArray;
import preibisch_imagelib2.type.NativeType;
import preibisch_imagelib2.util.Fraction;

/**
 * {@link ImagePlusImg} for float-stored data.
 *
 * @author Funke
 * @author Preibisch
 * @author Saalfeld
 * @author Schindelin
 * @author Jan Funke
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Johannes Schindelin
 */
public class FloatImagePlus< T extends NativeType< T > > extends ImagePlusImg< T, FloatArray >
{
	final ImagePlus imp;

	public FloatImagePlus( final long[] dim, final Fraction entitiesPerPixel )
	{
		super( dim, entitiesPerPixel );

		if ( entitiesPerPixel.getRatio() == 1 )
		{
			final ImageStack stack = new ImageStack( width, height );
			for ( int i = 0; i < numSlices; ++i )
				stack.addSlice( "", new FloatProcessor( width, height ) );
			imp = new ImagePlus( "image", stack );
			imp.setDimensions( channels, depth, frames );
			if ( numSlices > 1 )
				imp.setOpenAsHyperStack( true );

			mirror.clear();
			for ( int t = 0; t < frames; ++t )
				for ( int z = 0; z < depth; ++z )
					for ( int c = 0; c < channels; ++c )
						mirror.add( new FloatArray( ( float[] )imp.getStack().getProcessor( imp.getStackIndex( c + 1, z + 1 , t + 1 ) ).getPixels() ) );
		}
		else
		{
			imp = null;

			mirror.clear();
			for ( int i = 0; i < numSlices; ++i )
				mirror.add( new FloatArray( numEntities(entitiesPerPixel) ) );
		}
	}

	public FloatImagePlus( final ImagePlus imp )
	{
		super( imp );

		this.imp = imp;

		mirror.clear();
		for ( int t = 0; t < frames; ++t )
			for ( int z = 0; z < depth; ++z )
				for ( int c = 0; c < channels; ++c )
					mirror.add( new FloatArray( ( float[] )imp.getStack().getProcessor( imp.getStackIndex( c + 1, z + 1 , t + 1 ) ).getPixels() ) );
	}

	/**
	 * This has to be overwritten, otherwise two different instances exist (one in the imageplus, one in the mirror)
	 */
	@Override
	public void setPlane( final int no, final FloatArray plane )
	{
		System.arraycopy( plane.getCurrentStorageArray(), 0, mirror.get( no ).getCurrentStorageArray(), 0, plane.getCurrentStorageArray().length );
	}

	@Override
	public void close()
	{
		if ( imp != null )
			imp.close();
	}

	@Override
	public ImagePlus getImagePlus() throws ImgLibException
	{
		if ( imp == null )
			throw new ImgLibException( this, "has no ImagePlus instance, it is not a standard type of ImagePlus (" + entitiesPerPixel + " entities per pixel)" );
		return imp;
	}
}

