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
package preibisch_imagelib2.type.volatiles;

import preibisch_imagelib2.Volatile;
import preibisch_imagelib2.type.numeric.RealType;

/**
 * Abstract base class for {@link VolatileRealType}s that wrap {@link RealType}.
 * 
 * @param <R>
 *            wrapped {@link RealType}.
 * @param <T>
 *            type of derived concrete class.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 */
public abstract class AbstractVolatileRealType< R extends RealType< R >, T extends AbstractVolatileRealType< R, T > >
		extends Volatile< R >
		implements RealType< T >
{
	public AbstractVolatileRealType( final R t, final boolean valid )
	{
		super( t, valid );
	}

	@Override
	public double getRealDouble()
	{
		return t.getRealDouble();
	}

	@Override
	public float getRealFloat()
	{
		return t.getRealFloat();
	}

	@Override
	public double getImaginaryDouble()
	{
		return t.getImaginaryDouble();
	}

	@Override
	public float getImaginaryFloat()
	{
		return t.getImaginaryFloat();
	}

	@Override
	public void setReal( final float f )
	{
		t.setReal( f );
	}

	@Override
	public void setReal( final double f )
	{
		t.setReal( f );
	}

	@Override
	public void setImaginary( final float f )
	{
		t.setImaginary( f );
	}

	@Override
	public void setImaginary( final double f )
	{
		t.setImaginary( f );
	}

	@Override
	public void setComplexNumber( final float r, final float i )
	{
		t.setComplexNumber( r, i );
	}

	@Override
	public void setComplexNumber( final double r, final double i )
	{
		t.setComplexNumber( r, i );
	}

	@Override
	public float getPowerFloat()
	{
		return t.getPowerFloat();
	}

	@Override
	public double getPowerDouble()
	{
		return t.getPowerDouble();
	}

	@Override
	public float getPhaseFloat()
	{
		return t.getPhaseFloat();
	}

	@Override
	public double getPhaseDouble()
	{
		return t.getPhaseDouble();
	}

	@Override
	public void complexConjugate()
	{
		t.complexConjugate();
	}

	@Override
	public int compareTo( final T o )
	{
		return t.compareTo( o.t );
	}

	@Override
	public void inc()
	{
		t.inc();
	}

	@Override
	public void dec()
	{
		t.dec();
	}

	@Override
	public double getMaxValue()
	{
		return t.getMaxValue();
	}

	@Override
	public double getMinValue()
	{
		return t.getMinValue();
	}

	@Override
	public double getMinIncrement()
	{
		return t.getMinIncrement();
	}

	@Override
	public int getBitsPerPixel()
	{
		return t.getBitsPerPixel();
	}

	@Override
	public void set( final T c )
	{
		t.set( c.t );
		valid = c.valid;
	}

	@Override
	public void add( final T c )
	{
		t.add( c.t );
		valid &= c.valid;
	}

	@Override
	public void sub( final T c )
	{
		t.sub( c.t );
		valid &= c.valid;
	}

	@Override
	public void mul( final T c )
	{
		t.mul( c.t );
		valid &= c.valid;
	}

	@Override
	public void div( final T c )
	{
		t.div( c.t );
		valid &= c.valid;
	}

	@Override
	public void setZero()
	{
		t.setZero();
	}

	@Override
	public void setOne()
	{
		t.setOne();
	}

	@Override
	public void mul( final float c )
	{
		t.mul( c );
	}

	@Override
	public void mul( final double c )
	{
		t.mul( c );
	}
}
