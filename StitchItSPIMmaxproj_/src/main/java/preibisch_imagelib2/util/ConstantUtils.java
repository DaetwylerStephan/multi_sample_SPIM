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

package preibisch_imagelib2.util;

import preibisch_imagelib2.Interval;
import preibisch_imagelib2.Point;
import preibisch_imagelib2.RandomAccess;
import preibisch_imagelib2.RandomAccessible;
import preibisch_imagelib2.RandomAccessibleInterval;
import preibisch_imagelib2.RealInterval;
import preibisch_imagelib2.RealPoint;
import preibisch_imagelib2.RealRandomAccess;
import preibisch_imagelib2.RealRandomAccessible;
import preibisch_imagelib2.view.Views;

public class ConstantUtils
{
	public static < T > RandomAccessible< T > constantRandomAccessible( final T constant, final int numDimensions )
	{
		return new RandomAccessible< T >()
		{
			@Override
			public int numDimensions()
			{
				return numDimensions;
			}

			final class ConstantRandomAccess extends Point implements RandomAccess< T >
			{
				public ConstantRandomAccess()
				{
					super( numDimensions );
				}

				@Override
				public T get()
				{
					return constant;
				}

				@Override
				public ConstantRandomAccess copy()
				{
					return new ConstantRandomAccess();
				}

				@Override
				public ConstantRandomAccess copyRandomAccess()
				{
					return copy();
				}
			}

			@Override
			public RandomAccess< T > randomAccess()
			{
				return new ConstantRandomAccess();
			}

			@Override
			public RandomAccess< T > randomAccess( final Interval interval )
			{
				return randomAccess();
			}
		};
	}

	public static < T > RandomAccessibleInterval< T > constantRandomAccessibleInterval( final T constant, final int numDimensions, final Interval interval )
	{
		return Views.interval( constantRandomAccessible( constant, numDimensions ), interval );
	}

	public static < T > RealRandomAccessible< T > constantRealRandomAccessible( final T constant, final int numDimensions )
	{
		return new RealRandomAccessible< T >()
		{
			@Override
			public int numDimensions()
			{
				return numDimensions;
			}

			final class ConstantRealRandomAccess extends RealPoint implements RealRandomAccess< T >
			{
				public ConstantRealRandomAccess()
				{
					super( numDimensions );
				}

				@Override
				public T get()
				{
					return constant;
				}

				@Override
				public ConstantRealRandomAccess copy()
				{
					return new ConstantRealRandomAccess();
				}

				@Override
				public ConstantRealRandomAccess copyRealRandomAccess()
				{
					return copy();
				}
			}

			@Override
			public ConstantRealRandomAccess realRandomAccess()
			{
				return new ConstantRealRandomAccess();
			}

			@Override
			public ConstantRealRandomAccess realRandomAccess( final RealInterval interval )
			{
				return realRandomAccess();
			}
		};
	}
}
