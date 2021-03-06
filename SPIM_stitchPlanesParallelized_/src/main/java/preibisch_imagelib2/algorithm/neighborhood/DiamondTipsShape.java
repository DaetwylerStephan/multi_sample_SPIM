package preibisch_imagelib2.algorithm.neighborhood;

import java.util.Iterator;

import preibisch_imagelib2.AbstractEuclideanSpace;
import preibisch_imagelib2.AbstractInterval;
import preibisch_imagelib2.Cursor;
import preibisch_imagelib2.FlatIterationOrder;
import preibisch_imagelib2.Interval;
import preibisch_imagelib2.IterableInterval;
import preibisch_imagelib2.RandomAccess;
import preibisch_imagelib2.RandomAccessible;
import preibisch_imagelib2.RandomAccessibleInterval;


/**
 * TODO
 * 
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com>
 * @author Jonathan Hale (University of Konstanz)
 */
public class DiamondTipsShape implements Shape
{

	private final long radius;

	/**
	 * Constructor
	 * 
	 * @param radius
	 *            Radius for the diamond shape.
	 */
	public DiamondTipsShape( final long radius )
	{
		this.radius = radius;
	}

	@Override
	public < T > NeighborhoodsIterableInterval< T > neighborhoods( final RandomAccessibleInterval< T > source )
	{
		return new NeighborhoodsIterableInterval< T >( source, radius, DiamondTipsNeighborhoodUnsafe.< T >factory() );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessible( final RandomAccessible< T > source )
	{
		final DiamondTipsNeighborhoodFactory< T > f = DiamondTipsNeighborhoodUnsafe.< T >factory();
		return new NeighborhoodsAccessible< T >( source, radius, f );
	}

	@Override
	public < T > NeighborhoodsIterableInterval< T > neighborhoodsSafe( final RandomAccessibleInterval< T > source )
	{
		return new NeighborhoodsIterableInterval< T >( source, radius, DiamondTipsNeighborhood.< T >factory() );
	}

	@Override
	public < T > NeighborhoodsAccessible< T > neighborhoodsRandomAccessibleSafe( final RandomAccessible< T > source )
	{
		final DiamondTipsNeighborhoodFactory< T > f = DiamondTipsNeighborhood.< T >factory();
		return new NeighborhoodsAccessible< T >( source, radius, f );
	}

	/**
	 * @return Radius of this shape.
	 * @see DiamondTipsShape#DiamondTipsShape(long)
	 */
	public long getRadius()
	{
		return radius;
	}

	@Override
	public String toString()
	{
		return "DiamondTipsShape, radius = " + radius;
	}

	public static final class NeighborhoodsIterableInterval< T > extends AbstractInterval implements IterableInterval< Neighborhood< T > >
	{
		final RandomAccessibleInterval< T > source;

		final DiamondTipsNeighborhoodFactory< T > factory;

		final long size;

		private final long radius;

		public NeighborhoodsIterableInterval( final RandomAccessibleInterval< T > source, final long radius, final DiamondTipsNeighborhoodFactory< T > factory )
		{
			super( source );
			this.source = source;
			this.radius = radius;
			this.factory = factory;
			long s = source.dimension( 0 );
			for ( int d = 1; d < n; ++d )
			{
				s *= source.dimension( d );
			}
			size = s;
		}

		@Override
		public Cursor< Neighborhood< T >> cursor()
		{
			return new DiamondTipsNeighborhoodCursor< T >( source, radius, factory );
		}

		@Override
		public long size()
		{
			return size;
		}

		@Override
		public Neighborhood< T > firstElement()
		{
			return cursor().next();
		}

		@Override
		public Object iterationOrder()
		{
			return new FlatIterationOrder( this );
		}

		@Override
		public Iterator< Neighborhood< T >> iterator()
		{
			return cursor();
		}

		@Override
		public Cursor< Neighborhood< T >> localizingCursor()
		{
			return cursor();
		}
	}

	public static final class NeighborhoodsAccessible< T > extends AbstractEuclideanSpace implements RandomAccessible< Neighborhood< T > >
	{
		final RandomAccessible< T > source;

		final DiamondTipsNeighborhoodFactory< T > factory;

		private final long radius;

		public NeighborhoodsAccessible( final RandomAccessible< T > source, final long radius, final DiamondTipsNeighborhoodFactory< T > factory )
		{
			super( source.numDimensions() );
			this.source = source;
			this.radius = radius;
			this.factory = factory;
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess()
		{
			return new DiamondTipsNeighborhoodRandomAccess< T >( source, radius, factory );
		}

		@Override
		public RandomAccess< Neighborhood< T >> randomAccess( final Interval interval )
		{
			return randomAccess();
		}

		@Override
		public int numDimensions()
		{
			return source.numDimensions();
		}

	}
}
