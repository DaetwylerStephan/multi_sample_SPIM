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

package preibisch_imagelib2.converter;

import java.util.Iterator;

import preibisch_imagelib2.AbstractWrappedInterval;
import preibisch_imagelib2.Interval;
import preibisch_imagelib2.IterableInterval;
import preibisch_imagelib2.IterableRealInterval;
import preibisch_imagelib2.RandomAccessible;
import preibisch_imagelib2.RandomAccessibleInterval;

/**
 * TODO
 * 
 */
abstract public class AbstractConvertedIterableRandomAccessibleInterval< A, B, S extends RandomAccessible< A > & IterableInterval< A > > extends AbstractWrappedInterval< S > implements IterableInterval< B >, RandomAccessibleInterval< B >
{
	public AbstractConvertedIterableRandomAccessibleInterval( final S source )
	{
		super( source );
	}

	@Override
	abstract public AbstractConvertedRandomAccess< A, B > randomAccess();

	@Override
	abstract public AbstractConvertedRandomAccess< A, B > randomAccess( final Interval interval );

	@Override
	public long size()
	{
		return sourceInterval.size();
	}

	@Override
	public Object iterationOrder()
	{
		return sourceInterval.iterationOrder();
	}

	@Override
	public Iterator< B > iterator()
	{
		return cursor();
	}

	@Override
	public B firstElement()
	{
		return cursor().next();
	}

	@Override
	abstract public AbstractConvertedCursor< A, B > cursor();

	@Override
	abstract public AbstractConvertedCursor< A, B > localizingCursor();
}
