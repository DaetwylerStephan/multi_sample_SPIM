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

package preibisch_imagelib2.img.cell;

import preibisch_imagelib2.img.basictypeaccess.array.ArrayDataAccess;
import preibisch_imagelib2.img.list.ListImg;
import preibisch_imagelib2.img.list.ListImgFactory;
import preibisch_imagelib2.img.list.ListLocalizingCursor;
import preibisch_imagelib2.util.Fraction;

/**
 * Implementation of {@link Cells} that uses {@link DefaultCell}s and keeps them
 * all in memory all the time in a {@link ListImg}.
 * 
 * 
 * @author ImgLib2 developers
 * @author Tobias Pietzsch Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class ListImgCells< A extends ArrayDataAccess< A > > extends AbstractCells< A, DefaultCell< A >, ListImg< DefaultCell< A > > >
{
	private final ListImg< DefaultCell< A > > cells;

	public ListImgCells( final A creator, final Fraction entitiesPerPixel, final long[] dimensions, final int[] cellDimensions )
	{
		super( entitiesPerPixel, dimensions, cellDimensions );
		cells = new ListImgFactory< DefaultCell< A > >().create( numCells, new DefaultCell< A >( creator, new int[ 1 ], new long[ 1 ], entitiesPerPixel ) );

		final long[] cellGridPosition = new long[ n ];
		final long[] cellMin = new long[ n ];
		final int[] cellDims = new int[ n ];
		final ListLocalizingCursor< DefaultCell< A > > cellCursor = cells.localizingCursor();
		while ( cellCursor.hasNext() )
		{
			cellCursor.fwd();
			cellCursor.localize( cellGridPosition );
			getCellDimensions( cellGridPosition, cellMin, cellDims );
			cellCursor.set( new DefaultCell< A >( creator, cellDims, cellMin, entitiesPerPixel ) );
		}
	}

	@Override
	protected ListImg< DefaultCell< A > > cells()
	{
		return cells;
	}
}
