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

package preibisch_imagelib2.img.planar;

import preibisch_imagelib2.AbstractCursorInt;
import preibisch_imagelib2.Interval;
import preibisch_imagelib2.type.NativeType;

/**
 * Basic Iterator for {@link PlanarImg PlanarContainers}
 * 
 * @param <T>
 * 
 * @author Christian Dietz
 * @author Jonathan Hale
 */
public class PlanarPlaneSubsetCursor< T extends NativeType< T >> extends
		AbstractCursorInt< T > implements PlanarImg.PlanarContainerSampler
{

	/**
	 * Access to the type
	 */
	protected final T type;

	/**
	 * Container
	 */
	protected final PlanarImg< T, ? > container;

	/**
	 * Current slice index
	 */
	protected final int sliceIndex;

	/**
	 * Size of one plane
	 */
	protected final int planeSize;
	
	/**
	 * Last index on plane
	 */
	protected final int lastPlaneIndex;
	
	/**
	 * Copy Constructor
	 * 
	 * @param cursor - the cursor to copy from.
	 */
	protected PlanarPlaneSubsetCursor( final PlanarPlaneSubsetCursor< T > cursor )
	{
		super( cursor.numDimensions() );

		container = cursor.container;
		this.type = container.createLinkedType();

		sliceIndex = cursor.sliceIndex;
		planeSize = cursor.planeSize;
		lastPlaneIndex = cursor.lastPlaneIndex;

		type.updateContainer( this );
		type.updateIndex( cursor.type.getIndex() );
	}

	/**
	 * Constructor
	 * 
	 * @param container - the container this cursor shall work on.
	 * @param interval - the interval to iterate over.
	 */
	public PlanarPlaneSubsetCursor( final PlanarImg< T, ? > container, Interval interval )
	{
		super( container.numDimensions() );

		this.type = container.createLinkedType();

		this.container = container;

		this.planeSize = ( ( n > 1 ) ? ( int ) interval.dimension( 1 ) : 1 )
				* ( int ) interval.dimension( 0 );
		
		this.lastPlaneIndex = planeSize - 1;

		this.sliceIndex = ( int ) ( offset( interval ) / planeSize );

		reset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getCurrentSliceIndex()
	{
		return sliceIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final T get()
	{
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlanarPlaneSubsetCursor< T > copy()
	{
		return new PlanarPlaneSubsetCursor< T >( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PlanarPlaneSubsetCursor< T > copyCursor()
	{
		return copy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean hasNext()
	{
		return type.getIndex() < lastPlaneIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void fwd()
	{
		type.incIndex();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void jumpFwd( long steps )
	{
		type.incIndex( ( int ) steps );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void reset()
	{
		// Set index inside the slice
		type.updateIndex( -1 );
		type.updateContainer( this );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return type.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void localize( final int[] position )
	{
		container.indexToGlobalPosition( sliceIndex, type.getIndex(), position );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int getIntPosition( final int dim )
	{
		return container.indexToGlobalPosition( sliceIndex, type.getIndex(), dim );
	}

	private long offset(final Interval interval) {
		final int maxDim = numDimensions() - 1;
		long i = interval.min(maxDim);
		for (int d = maxDim - 1; d >= 0; --d)
			i = i * container.dimension(d) + interval.min(d);

		return i;
	}
}
