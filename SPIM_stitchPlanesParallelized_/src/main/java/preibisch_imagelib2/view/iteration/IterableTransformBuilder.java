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

import java.util.Arrays;
import java.util.Iterator;

import preibisch_imagelib2.AbstractWrappedInterval;
import preibisch_imagelib2.Cursor;
import preibisch_imagelib2.FlatIterationOrder;
import preibisch_imagelib2.Interval;
import preibisch_imagelib2.IterableInterval;
import preibisch_imagelib2.IterableRealInterval;
import preibisch_imagelib2.RandomAccessible;
import preibisch_imagelib2.transform.integer.BoundingBox;
import preibisch_imagelib2.transform.integer.SlicingTransform;
import preibisch_imagelib2.util.Intervals;
import preibisch_imagelib2.view.IterableRandomAccessibleInterval;
import preibisch_imagelib2.view.TransformBuilder;
import preibisch_imagelib2.view.Views;

/**
 * Simplifies View cascades to provide the most efficient {@link Cursor}.
 * 
 * @see #getEfficientIterableInterval(Interval, RandomAccessible)
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class IterableTransformBuilder< T > extends TransformBuilder< T >
{
	/**
	 * Create an {@link IterableInterval} that iterates an {@link Interval} of a
	 * {@link RandomAccessible}. If possible, this should return an optimized
	 * cursor. If not, falls back to creating an
	 * {@link IterableRandomAccessibleInterval}.
	 * 
	 * @param interval
	 *            the interval of {@code randomAccessible} which should be
	 *            iterated.
	 * @param randomAccessible
	 *            the {@link RandomAccessible} that should be iterated.
	 * @return an {@link IterableInterval} that iterates {@code interval} of
	 *         {@code randomAccessible}.
	 */
	public static < S > IterableInterval< S > getEfficientIterableInterval( final Interval interval, final RandomAccessible< S > randomAccessible )
	{
		return new IterableTransformBuilder< S >( interval, randomAccessible ).buildIterableInterval();
	}

	/**
	 * The interval which should be iterated.
	 * 
	 * <p>
	 * Currently, no transformations are done on this, because the cases where
	 * an optimized {@link IterableInterval} can be returned do not allow for
	 * any transformation except a single slicing. In the future, it may become
	 * necessary, to propagated the interval through the transforms down the
	 * view hierarchy.
	 */
	protected Interval interval;

	/**
	 * Create a new IterableTransformBuilder. This calls the the super
	 * constructor to gather and simplify transformations.
	 * 
	 * @param interval
	 *            the interval of {@code randomAccessible} which should be
	 *            iterated.
	 * @param randomAccessible
	 *            the {@link RandomAccessible} that should be iterated.
	 */
	public IterableTransformBuilder( final Interval interval, final RandomAccessible< T > randomAccessible )
	{
		super( interval, randomAccessible );
		this.interval = interval;
	}

	/**
	 * An {@link IterableInterval} on {@link IterableTransformBuilder#interval
	 * interval} of {@link SubIntervalIterable}.
	 */
	private class SubInterval extends AbstractWrappedInterval< Interval > implements IterableInterval< T >
	{
		final long numElements;

		final SubIntervalIterable< T > iterableSource;

		public SubInterval( final SubIntervalIterable< T > iterableSource )
		{
			super( interval );
			numElements = Intervals.numElements( interval );
			this.iterableSource = iterableSource;
		}

		@Override
		public long size()
		{
			return numElements;
		}

		@Override
		public T firstElement()
		{
			return cursor().next();
		}

		@Override
		public Object iterationOrder()
		{
			return iterableSource.subIntervalIterationOrder( interval );
		}

		@Override
		public Iterator< T > iterator()
		{
			return cursor();
		}

		@Override
		public Cursor< T > cursor()
		{
			return iterableSource.cursor( interval );
		}

		@Override
		public Cursor< T > localizingCursor()
		{
			return iterableSource.localizingCursor( interval );
		}
	}

	/**
	 * An {@link IterableInterval} on a slice of a {@link SubIntervalIterable}.
	 */
	private class Slice extends AbstractWrappedInterval< Interval > implements IterableInterval< T >
	{
		final long numElements;

		final SubIntervalIterable< T > iterableSource;

		final Interval sourceInterval;

		final SlicingTransform transformToSource;

		final boolean hasFlatIterationOrder;

		public Slice( final SubIntervalIterable< T > iterableSource, final Interval sourceInterval, final SlicingTransform transformToSource, final boolean hasFlatIterationOrder )
		{
			super( interval );
			numElements = Intervals.numElements( interval );
			this.iterableSource = iterableSource;
			this.sourceInterval = sourceInterval;
			this.transformToSource = transformToSource;
			this.hasFlatIterationOrder = hasFlatIterationOrder;
		}

		@Override
		public long size()
		{
			return numElements;
		}

		@Override
		public T firstElement()
		{
			return cursor().next();
		}

		@Override
		public Object iterationOrder()
		{
			return hasFlatIterationOrder ? new FlatIterationOrder( interval ) : this;
		}

		@Override
		public Iterator< T > iterator()
		{
			return cursor();
		}

		@Override
		public Cursor< T > cursor()
		{
			return new SlicingCursor< T >( iterableSource.cursor( sourceInterval ), transformToSource );
		}

		@Override
		public Cursor< T > localizingCursor()
		{
			return new SlicingCursor< T >( iterableSource.localizingCursor( sourceInterval ), transformToSource );
		}
	}

	/**
	 * Create an {@link IterableInterval} on the {@link Interval} specified in
	 * the constructor of the {@link RandomAccessible} specified in the
	 * constructor.
	 */
	public IterableInterval< T > buildIterableInterval()
	{
		if ( boundingBox != null && SubIntervalIterable.class.isInstance( source ) )
		{
			@SuppressWarnings( "unchecked" )
			final SubIntervalIterable< T > iterableSource = ( SubIntervalIterable< T > ) source;
			if ( transforms.isEmpty() )
			{
				if ( iterableSource.supportsOptimizedCursor( interval ) )
					return new SubInterval( iterableSource );
			}
			else if ( transforms.size() == 1 && SlicingTransform.class.isInstance( transforms.get( 0 ) ) )
			{
				final SlicingTransform t = ( SlicingTransform ) transforms.get( 0 );
				final int m = t.numTargetDimensions();
				final int n = t.numSourceDimensions();

				// Check whether the slicing can be potentially optimized.
				boolean optimizable = true;

				// 1.) Slice dimensions must be mapped to a contiguous range of
				// target dimensions starting with dimension 0.
				int firstZeroDim = 0;
				for ( ; firstZeroDim < m && !t.getComponentZero( firstZeroDim ); ++firstZeroDim );
				for ( int d = firstZeroDim + 1; d < m && optimizable; ++d )
					if ( !t.getComponentZero( d ) )
						optimizable = false;

				// 2.) All slice dimensions must be mapped to a target dimension
				final int[] sourceComponent = new int[ n ];
				if ( optimizable )
				{
					Arrays.fill( sourceComponent, -1 );
					for ( int d = 0; d < m; ++d )
						if ( !t.getComponentZero( d ) )
							sourceComponent[ t.getComponentMapping( d ) ] = d;
					for ( int d = 0; d < n && optimizable; ++d )
						if ( sourceComponent[ d ] < 0 )
							optimizable = false;
				}

				if ( optimizable )
				{
//					System.out.println( "interval = " + Util.printInterval( interval ) );
					final Interval sliceInterval = t.transform( new BoundingBox( interval ) ).getInterval();
//					System.out.println( "transformed interval = " + Util.printInterval( sliceInterval ) );
					if ( iterableSource.supportsOptimizedCursor( sliceInterval ) )
					{
						// check for FlatIterationOrder
						boolean flat = FlatIterationOrder.class.isInstance( iterableSource.subIntervalIterationOrder( sliceInterval ) );
						for ( int d = 0; d < n - 1; ++d )
							if ( sourceComponent[ d + 1 ] <= sourceComponent[ d ] )
								flat = false;
						return new Slice( iterableSource, sliceInterval, t, flat );
					}
				}
			}
		}
		return new IterableRandomAccessibleInterval< T >( Views.interval( build(), interval ) );
	}
}
