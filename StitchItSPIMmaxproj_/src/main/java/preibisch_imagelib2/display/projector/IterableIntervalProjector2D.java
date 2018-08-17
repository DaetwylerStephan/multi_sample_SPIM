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
package preibisch_imagelib2.display.projector;

import preibisch_imagelib2.Cursor;
import preibisch_imagelib2.FinalInterval;
import preibisch_imagelib2.FlatIterationOrder;
import preibisch_imagelib2.IterableInterval;
import preibisch_imagelib2.RandomAccess;
import preibisch_imagelib2.RandomAccessible;
import preibisch_imagelib2.converter.Converter;
import preibisch_imagelib2.view.RandomAccessibleIntervalCursor;
import preibisch_imagelib2.view.Views;

/**
 * A general 2D Projector that uses two dimensions as input to create the 2D
 * result. The output of the projection is written into a {@link IterableInterval}.
 * 
 * Depending on input and output an optimal strategy is chosen in the map() method.
 * 
 * Starting from the reference point two dimensions are sampled such
 * that a plain gets cut out of a higher dimensional data volume. <br>
 * The mapping function can be specified with a {@link Converter}. <br>
 * A basic example is cutting out a time frame from a (greyscale) video.
 * 
 * @author Michael Zinsmaier, Martin Horn, Christian Dietz
 * 
 * @param <A>
 * @param <B>
 */
public class IterableIntervalProjector2D< A, B > extends AbstractProjector2D
{
	final protected Converter< ? super A, B > converter;

	final protected RandomAccessible< A > source;

	final protected IterableInterval< B > target;

	final int numDimensions;

	private final int dimX;

	private final int dimY;

	protected final int X = 0;

	protected final int Y = 1;

	/**
	 * creates a new 2D projector that samples a plain in the dimensions dimX,
	 * dimY.
	 * 
	 * @param dimX
	 * @param dimY
	 * @param source
	 * @param target
	 * @param converter
	 *            a converter that is applied to each point in the plain. This
	 *            can e.g. be used for normalization, conversions, ...
	 */
	public IterableIntervalProjector2D( final int dimX, final int dimY, final RandomAccessible< A > source, final IterableInterval< B > target, final Converter< ? super A, B > converter )
	{
		super( source.numDimensions() );
		this.dimX = dimX;
		this.dimY = dimY;
		this.target = target;
		this.source = source;
		this.converter = converter;
		this.numDimensions = source.numDimensions();
	}

	/**
	 * projects data from the source to the target and applies the former
	 * specified {@link Converter} e.g. for normalization.
	 */
	@Override
	public void map()
	{
		// fix interval for all dimensions
		for ( int d = 0; d < position.length; ++d )
			min[ d ] = max[ d ] = position[ d ];

		min[ dimX ] = target.min( X );
		min[ dimY ] = target.min( Y );
		max[ dimX ] = target.max( X );
		max[ dimY ] = target.max( Y );

		// TODO: this is ugly, but the only way to make sure, that iteration
		// order fits in the case of one sized dims. Tobi?
		final IterableInterval< A > ii = Views.iterable( Views.interval( source, new FinalInterval( min, max ) ) );

		final Cursor< A > sourceCursor = ii.cursor();

		if ( target.iterationOrder().equals( ii.iterationOrder() ) && !( sourceCursor instanceof RandomAccessibleIntervalCursor ) )
		{
			final Cursor< B > targetCursor = target.cursor();
			while ( targetCursor.hasNext() )
			{
				converter.convert( sourceCursor.next(), targetCursor.next() );
			}
		}
		else if ( target.iterationOrder() instanceof FlatIterationOrder )
		{
			final Cursor< B > targetCursor = target.cursor();
			targetCursor.fwd();
			final FinalInterval sourceInterval = new FinalInterval( min, max );

			// use localizing cursor
			final RandomAccess< A > sourceRandomAccess = source.randomAccess( sourceInterval );
			sourceRandomAccess.setPosition( position );

			final long cr = -target.dimension( dimX );

			final long width = target.dimension( dimX );
			final long height = target.dimension( dimY );

			sourceRandomAccess.setPosition( min );
			for ( long y = 0; y < height; ++y )
			{
				for ( long x = 0; x < width; ++x )
				{
					converter.convert( sourceRandomAccess.get(), targetCursor.get() );
					sourceRandomAccess.fwd( dimX );
					targetCursor.fwd();
				}
				sourceRandomAccess.move( cr, dimX );
				sourceRandomAccess.fwd( dimY );
			}

		}
		else
		{
			final Cursor< B > targetCursor = target.localizingCursor();
			
			// use localizing cursor
			final RandomAccess< A > sourceRandomAccess = source.randomAccess();
			sourceRandomAccess.setPosition( position );
			while ( targetCursor.hasNext() )
			{
				final B b = targetCursor.next();
				sourceRandomAccess.setPosition( targetCursor.getLongPosition( X ), dimX );
				sourceRandomAccess.setPosition( targetCursor.getLongPosition( Y ), dimY );

				converter.convert( sourceRandomAccess.get(), b );
			}
		}
	}
}
