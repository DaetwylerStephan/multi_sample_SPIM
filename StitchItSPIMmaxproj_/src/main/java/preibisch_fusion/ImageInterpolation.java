package preibisch_fusion;

import preibisch_imagelib2.RandomAccessible;
import preibisch_imagelib2.RealRandomAccess;
import preibisch_imagelib2.RealRandomAccessible;
import preibisch_imagelib2.img.Img;
import preibisch_imagelib2.interpolation.InterpolatorFactory;
import preibisch_imagelib2.type.numeric.RealType;
import preibisch_imagelib2.view.Views;

/**
 * This class is necessary as it can create an {@link Interpolator} for an {@link Image} even if hold it as < ? extends RealType< ? > >
 * 
 * @author preibischs
 *
 * @param <T>
 */
public class ImageInterpolation< T extends RealType< T > > 
{
	final Img< T > image;
	final RealRandomAccessible< T > interpolated;
	final InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory;
	
	public ImageInterpolation( final Img< T > image, final InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory, final boolean mirror )
	{
		this.image = image;
		this.interpolatorFactory = interpolatorFactory;
		if ( mirror )
			this.interpolated = Views.interpolate( Views.extendMirrorSingle( image ), interpolatorFactory );
		else
			this.interpolated = Views.interpolate( Views.extendZero( image ), interpolatorFactory );
	}
	
	public Img< T > getImg() { return image; }
	public RealRandomAccess< T > createInterpolator() { return interpolated.realRandomAccess(); }
}
