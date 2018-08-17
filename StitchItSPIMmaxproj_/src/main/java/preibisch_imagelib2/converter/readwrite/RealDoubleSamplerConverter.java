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

package preibisch_imagelib2.converter.readwrite;

import preibisch_imagelib2.Sampler;
import preibisch_imagelib2.img.basictypeaccess.DoubleAccess;
import preibisch_imagelib2.type.numeric.RealType;
import preibisch_imagelib2.type.numeric.real.DoubleType;

/**
 * TODO
 * 
 */
public final class RealDoubleSamplerConverter< R extends RealType< R > > implements SamplerConverter< R, DoubleType >
{
	@Override
	public DoubleType convert( final Sampler< ? extends R > sampler )
	{
		return new DoubleType( new RealConvertingDoubleAccess< R >( sampler ) );
	}

	private static final class RealConvertingDoubleAccess< R extends RealType< R > > implements DoubleAccess
	{
		private final Sampler< ? extends R > sampler;

		private RealConvertingDoubleAccess( final Sampler< ? extends R > sampler )
		{
			this.sampler = sampler;
		}

		/**
		 * This is only intended to work with DoubleType! We ignore index!!!
		 */
		@Override
		public double getValue( final int index )
		{
			return sampler.get().getRealDouble();
		}

		/**
		 * This is only intended to work with DoubleType! We ignore index!!!
		 */
		@Override
		public void setValue( final int index, final double value )
		{
			sampler.get().setReal( value );
		}
	}
}