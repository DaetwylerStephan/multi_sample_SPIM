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
package preibisch_imagelib2.view.iteration;

import preibisch_imagelib2.Cursor;
import preibisch_imagelib2.Interval;
import preibisch_imagelib2.IterableRealInterval;

/**
 * Implementing this interface enables the implementor to return optimized
 * {@link Cursor}s for certain {@link Interval}s
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public interface SubIntervalIterable< T >
{
	/**
	 * Determine whether a {@link Cursor} can be created that iterates the given
	 * {@link Interval}.
	 * 
	 * @param interval
	 *            to be checked
	 * 
	 * @return true if a {@link Cursor} can be optimized given the
	 *         {@link Interval}
	 */
	public boolean supportsOptimizedCursor( Interval interval );

	/**
	 * Returns the iteration order given the interval.
	 * 
	 * @param interval
	 *            {@link Interval} over which to iterate
	 * 
	 * @return the iteration order object @see {@link IterableRealInterval}
	 */
	public Object subIntervalIterationOrder( Interval interval );

	/**
	 * Create a {@link Cursor} to iterate over the given {@link Interval}.
	 * 
	 * @param interval
	 *            {@link Interval} over which to iterate
	 * 
	 * @return {@link Cursor}
	 */
	public Cursor< T > cursor( Interval interval );

	/**
	 * Create a {@link Cursor} to iterate over the given {@link Interval}.
	 * 
	 * @param interval
	 *            {@link Interval} over which to iterate
	 * 
	 * @return {@link Cursor}
	 */
	public Cursor< T > localizingCursor( Interval interval );
}
