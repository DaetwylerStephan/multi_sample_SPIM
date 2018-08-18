/**
 * License: GPL
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package preibisch_code;

import java.util.ArrayList;
import java.util.List;

import preibisch_code.util.Util;

public class CoordinateTransformList< E extends CoordinateTransform > implements Boundable, TransformList< E >
{
	private static final long serialVersionUID = -2481949109498295648L;

	final protected List< E > transforms = new ArrayList< E >();

	@Override
	final public void add( final E t ){ transforms.add( t ); }
	@Override
	final public void remove( final E t ){ transforms.remove( t ); }
	@Override
	final public E remove( final int i ){ return transforms.remove( i ); }
	@Override
	final public E get( final int i ){ return transforms.get( i ); }
	@Override
	final public void clear(){ transforms.clear(); }
	@Override
	final public List< E > getList( final List< E > preAllocatedList )
	{
		final List< E > returnList = ( preAllocatedList == null ) ? new ArrayList< E >() : preAllocatedList;
		returnList.addAll( transforms );
		return returnList;
	}

	@Override
	final public double[] apply( final double[] location )
	{
		final double[] a = location.clone();
		applyInPlace( a );
		return a;
	}

	@Override
	final public void applyInPlace( final double[] location )
	{
		for ( final E t : transforms )
			t.applyInPlace( location );
	}

	/**
	 * {@inheritDoc}
	 *
	 * Estimate the bounds approximately by iteration over a fixed grid of
	 * exemplary locations.
	 *
	 * TODO Find a better solution.
	 */
	@Override
	public void estimateBounds( final double[] min, final double[] max )
	{
		assert min.length == max.length : "min and max have to have equal length.";

		final int g = 32;

		final double[] minBounds = new double[ min.length ];
		final double[] maxBounds = new double[ min.length ];
		final double[] s = new double[ min.length ];
		final int[] i = new int[ min.length ];
		final double[] l = new double[ min.length ];

		for ( int k = 0; k < min.length; ++k )
		{
			minBounds[ k ] = Double.MAX_VALUE;
			maxBounds[ k ] = -Double.MAX_VALUE;
			s[ k ] = ( max[ k ] - min[ k ] ) / ( g - 1 );
			l[ k ] = min[ k ];
		}

		final long d = Util.pow( g, min.length );

		for ( long j = 0; j < d; ++j )
		{
			final double[] m = apply( l );
			for ( int k = 0; k < min.length; ++k )
			{
				if ( m[ k ] < minBounds[ k ] ) minBounds[ k ] = m[ k ];
				if ( m[ k ] > maxBounds[ k ] ) maxBounds[ k ] = m[ k ];
			}

//			for ( final int k : i )
//				System.out.print( k + " " );
//			System.out.print( ": " );
//			for ( final double k : l )
//				System.out.print( k + " " );
//			System.out.print( "-> " );
//			for ( final double k : m )
//				System.out.print( k + " " );
//			System.out.println();

			for ( int k = 0; k < min.length; ++k )
			{
				++i[ k ];
				if ( i[ k ] >= g )
				{
					i[ k ] = 0;
					l[ k ] = min[ k ];
					continue;
				}
				l[ k ] = min[ k ] + i[ k ] * s[ k ];
				break;
			}
		}

		for ( int k = 0; k < min.length; ++k )
		{
			min[ k ] = minBounds[ k ];
			max[ k ] = maxBounds[ k ];
		}
	}
}
