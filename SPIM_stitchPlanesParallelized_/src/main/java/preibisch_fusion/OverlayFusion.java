package preibisch_fusion;

import fiji.stacks.Hyperstack_rearranger;
import ij.CompositeImage;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ImageProcessor;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import preibisch_code.InvertibleBoundable;
import preibisch_code.InvertibleCoordinateTransform;
import preibisch_code.NoninvertibleModelException;
import preibisch_imagelib2.Cursor;
import preibisch_imagelib2.RandomAccessible;
import preibisch_imagelib2.RealRandomAccess;
import preibisch_imagelib2.RealRandomAccessible;
import preibisch_imagelib2.exception.ImgLibException;
import preibisch_imagelib2.img.Img;
import preibisch_imagelib2.img.ImgFactory;
import preibisch_imagelib2.img.display.imagej.ImageJFunctions;
import preibisch_imagelib2.img.imageplus.ImagePlusImg;
import preibisch_imagelib2.img.imageplus.ImagePlusImgFactory;
import preibisch_imagelib2.interpolation.InterpolatorFactory;
import preibisch_imagelib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import preibisch_imagelib2.multithreading.Chunk;
import preibisch_imagelib2.multithreading.SimpleMultiThreading;
import preibisch_imagelib2.type.NativeType;
import preibisch_imagelib2.type.numeric.RealType;
import preibisch_imagelib2.type.numeric.real.FloatType;
import preibisch_imagelib2.view.Views;
import preibisch_fusion.utils.*;


public class OverlayFusion 
{
	protected static < T extends RealType< T > & NativeType< T > > CompositeImage createOverlay( final T targetType, final ImagePlus imp1, final ImagePlus imp2, final InvertibleBoundable finalModel1, final InvertibleBoundable finalModel2, final int dimensionality ) 
	{
		final ArrayList< ImagePlus > images = new ArrayList<ImagePlus>();
		images.add( imp1 );
		images.add( imp2 );
		
		final ArrayList< InvertibleBoundable > models = new ArrayList<InvertibleBoundable>();
		models.add( finalModel1 );
		models.add( finalModel2 );
		
		return createOverlay( targetType, images, models, dimensionality, 1, new NLinearInterpolatorFactory<FloatType>() );
	}
	
	public static < T extends RealType< T > & NativeType< T > > ImagePlus createReRegisteredSeries( final T targetType, final ImagePlus imp, final ArrayList<InvertibleBoundable> models, final int dimensionality )
	{
		final int numImages = imp.getNFrames();

		// the size of the new image
		final int[] size = new int[ dimensionality ];
		// the offset relative to the output image which starts with its local coordinates (0,0,0)
		final double[] offset = new double[ dimensionality ];

		final int[][] imgSizes = new int[ numImages ][ dimensionality ];
		
		for ( int i = 0; i < numImages; ++i )
		{
			imgSizes[ i ][ 0 ] = imp.getWidth();
			imgSizes[ i ][ 1 ] = imp.getHeight();
			if ( dimensionality == 3 )
				imgSizes[ i ][ 2 ] = imp.getNSlices();
		}
		
		// estimate the boundaries of the output image and the offset for fusion (negative coordinates after transform have to be shifted to 0,0,0)
		Fusion2.estimateBounds( offset, size, imgSizes, models, dimensionality );
				
		// for output
		final ImgFactory< T > f = new ImagePlusImgFactory< T >();
		// the composite
		final ImageStack stack = new ImageStack( size[ 0 ], size[ 1 ] );

		for ( int t = 1; t <= numImages; ++t )
		{
			for ( int c = 1; c <= imp.getNChannels(); ++c )
			{
				final Img<T> out = f.create( size, targetType );
				final Img< FloatType > in = ImageJFunctions.convertFloat( Hyperstack_rearranger.getImageChunk( imp, c, t ) );

				fuseChannel( out, Views.interpolate( Views.extendZero( in ), new NLinearInterpolatorFactory< FloatType >() ), offset, models.get( t - 1 ) );

				try
				{
					final ImagePlus outImp = ((ImagePlusImg<?,?>)out).getImagePlus();
					for ( int z = 1; z <= out.dimension( 2 ); ++z )
						stack.addSlice( imp.getTitle(), outImp.getStack().getProcessor( z ) );
				} 
				catch (ImgLibException e) 
				{
					Log.error( "Output image has no ImageJ type: " + e );
				}				
			}
		}
		
		//convertXYZCT ...
		ImagePlus result = new ImagePlus( "registered " + imp.getTitle(), stack );
		
		// numchannels, z-slices, timepoints (but right now the order is still XYZCT)
		if ( dimensionality == 3 )
		{
			result.setDimensions( size[ 2 ], imp.getNChannels(), imp.getNFrames() );
			result = OverlayFusion.switchZCinXYCZT( result );
			return CompositeImageFixer.makeComposite( result, CompositeImage.COMPOSITE );
		}
		//Log.info( "ch: " + imp.getNChannels() );
		//Log.info( "slices: " + imp.getNSlices() );
		//Log.info( "frames: " + imp.getNFrames() );
		result.setDimensions( imp.getNChannels(), 1, imp.getNFrames() );
		
		if ( imp.getNChannels() > 1 )
			return CompositeImageFixer.makeComposite( result, CompositeImage.COMPOSITE );
		return result;
	}
	
	public static <T extends RealType<T> & NativeType<T>> CompositeImage createOverlay(
			final T targetType,
			final ArrayList<ImagePlus> images,
			final ArrayList<InvertibleBoundable> models,
			final int dimensionality,
			final int timepoint,
			final InterpolatorFactory< FloatType, RandomAccessible< FloatType > > factory )
	{	
		final int numImages = images.size();
		
		// the size of the new image
		final int[] size = new int[ dimensionality ];
		// the offset relative to the output image which starts with its local coordinates (0,0,0)
		final double[] offset = new double[ dimensionality ];

		// estimate the boundaries of the output image and the offset for fusion (negative coordinates after transform have to be shifted to 0,0,0)
		Fusion2.estimateBounds( offset, size, images, models, dimensionality );
		
		// for output
		final ImgFactory<T> f = new ImagePlusImgFactory< T >();
		// the composite
		final ImageStack stack = new ImageStack( size[ 0 ], size[ 1 ] );
		
		int numChannels = 0;
		
		//loop over all images
		for ( int i = 0; i < images.size(); ++i )
		{
			final ImagePlus imp = images.get( i );
			
			// loop over all channels
			for ( int c = 1; c <= imp.getNChannels(); ++c )
			{
				final Img<T> out = f.create( size, targetType );
				final Img< FloatType > in = ImageJFunctions.convertFloat( Hyperstack_rearranger.getImageChunk( imp, c, timepoint ) );
				
				fuseChannel( out, Views.interpolate( Views.extendZero( in ), factory), offset, models.get( i + (timepoint - 1) * numImages ) );
				try 
				{
					final ImagePlus outImp = ((ImagePlusImg<?,?>)out).getImagePlus();
					for ( int z = 1; z <= out.dimension( 2 ); ++z )
						stack.addSlice( imp.getTitle(), outImp.getStack().getProcessor( z ) );
				} 
				catch (ImgLibException e) 
				{
					Log.error( "Output image has no ImageJ type: " + e );
				}
				
				// count all channels
				++numChannels;
			}
		}

		//convertXYZCT ...
		ImagePlus result = new ImagePlus( "overlay " + images.get( 0 ).getTitle() + " ... " + images.get( numImages - 1 ).getTitle(), stack );
		
		// numchannels, z-slices, timepoints (but right now the order is still XYZCT)
		if ( dimensionality == 3 )
		{
			result.setDimensions( size[ 2 ], numChannels, 1 );
			result = OverlayFusion.switchZCinXYCZT( result );
		}
		else
		{
			result.setDimensions( numChannels, 1, 1 );
		}
		
		return CompositeImageFixer.makeComposite( result, CompositeImage.COMPOSITE );
	}
		
	/**
	 * Fuse one slice/volume (one channel)
	 * 
	 * @param output - same the type of the ImagePlus input
	 * @param input - FloatType, because of Interpolation that needs to be done
	 * @param transform - the transformation
	 */
	protected static <T extends RealType<T>> void fuseChannel( final Img<T> output, final RealRandomAccessible<FloatType> input, final double[] offset, final InvertibleCoordinateTransform transform )
	{
		final int dims = output.numDimensions();
		long imageSize = output.dimension( 0 );
		
		for ( int d = 1; d < output.numDimensions(); ++d )
			imageSize *= output.dimension( d );

		// run multithreaded
		final AtomicInteger ai = new AtomicInteger(0);					
        final Thread[] threads = SimpleMultiThreading.newThreads();

        final Vector<Chunk> threadChunks = SimpleMultiThreading.divideIntoChunks( imageSize, threads.length );
        
        for (int ithread = 0; ithread < threads.length; ++ithread)
            threads[ithread] = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                	// Thread ID
                	final int myNumber = ai.getAndIncrement();
        
                	// get chunk of pixels to process
                	final Chunk myChunk = threadChunks.get( myNumber );
                	final long startPos = myChunk.getStartPosition();
                	final long loopSize = myChunk.getLoopSize();
                	
            		final Cursor<T> out = output.localizingCursor();
            		final RealRandomAccess<FloatType> in = input.realRandomAccess();
            		
            		final double[] tmp = new double[ input.numDimensions() ];
            		
            		try 
            		{
                		// move to the starting position of the current thread
            			out.jumpFwd( startPos );
            			
                		// do as many pixels as wanted by this thread
                        for ( long j = 0; j < loopSize; ++j )
                        {
            				out.fwd();
            				
            				for ( int d = 0; d < dims; ++d )
            					tmp[ d ] = out.getDoublePosition( d ) + offset[ d ];
            				
            				transform.applyInverseInPlace( tmp );
            	
            				in.setPosition( tmp );
            				out.get().setReal( in.get().get() );
            			}
            		} 
            		catch (NoninvertibleModelException e) 
            		{
            			Log.error( "Cannot invert model, qutting." );
            			return;
            		}

                }
            });
        
        SimpleMultiThreading.startAndJoin( threads );
		
        /*
		final LocalizableCursor<T> out = output.createLocalizableCursor();
		final Interpolator<FloatType> in = input.createInterpolator( factory );
		
		final float[] tmp = new float[ input.getNumDimensions() ];
		
		try 
		{
			while ( out.hasNext() )
			{
				out.fwd();
				
				for ( int d = 0; d < dims; ++d )
					tmp[ d ] = out.getPosition( d ) + offset[ d ];
				
				transform.applyInverseInPlace( tmp );
	
				in.setPosition( tmp );			
				out.getType().setReal( in.getType().get() );
			}
		} 
		catch (NoninvertibleModelException e) 
		{
			Log.error( "Cannot invert model, qutting." );
			return;
		}
		*/
	}

			
	/**
	 * Rearranges an ImageJ XYCZT Hyperstack into XYZCT without wasting memory for processing 3d images as a chunk,
	 * if it is already XYZCT it will shuffle it back to XYCZT
	 * 
	 * @param imp - the input {@link ImagePlus}
	 * @return - an {@link ImagePlus} which can be the same instance if the image is XYZT, XYZ, XYT or XY - otherwise a new instance
	 * containing the same processors but in the new order XYZCT
	 */
	public static ImagePlus switchZCinXYCZT( final ImagePlus imp )
	{
		final int numChannels = imp.getNChannels();
		final int numTimepoints = imp.getNFrames();
		final int numZStacks = imp.getNSlices();

		String newTitle;
		if ( imp.getTitle().startsWith( "[XYZCT]" ) )
			newTitle = imp.getTitle().substring( 8, imp.getTitle().length() );
		else
			newTitle = "[XYZCT] " + imp.getTitle();

		// there is only one channel and one z-stack, i.e. XY(T)
		if ( numChannels == 1 && numZStacks == 1 )
		{
			return imp;
		}
		// there is only one channel XYZ(T) or one z-stack XYC(T)
		else if ( numChannels == 1 || numZStacks == 1 )
		{
			// numchannels, z-slices, timepoints 
			// but of course now reversed...
			imp.setDimensions( numZStacks, numChannels, numTimepoints );
			imp.setTitle( newTitle );
			return imp;
		}
		
		// now we have to rearrange
		final ImageStack stack = new ImageStack( imp.getWidth(), imp.getHeight() );
		
		for ( int t = 1; t <= numTimepoints; ++t )
		{
			for ( int c = 1; c <= numChannels; ++c )
			{
				for ( int z = 1; z <= numZStacks; ++z )
				{
					final int index = imp.getStackIndex( c, z, t );
					final ImageProcessor ip = imp.getStack().getProcessor( index );
					stack.addSlice( imp.getStack().getSliceLabel( index ), ip );
				}
			}
		}
				
		final ImagePlus result = new ImagePlus( newTitle, stack );
		// numchannels, z-slices, timepoints 
		// but of course now reversed...
		result.setDimensions( numZStacks, numChannels, numTimepoints );
		result.getCalibration().pixelWidth = imp.getCalibration().pixelWidth;
		result.getCalibration().pixelHeight = imp.getCalibration().pixelHeight;
		result.getCalibration().pixelDepth = imp.getCalibration().pixelDepth;
		final CompositeImage composite = CompositeImageFixer.makeComposite( result );
		
		return composite;
	}

	/**
	 * Rearranges an ImageJ XYCTZ Hyperstack into XYCZT without wasting memory for processing 3d images as a chunk,
	 * if it is already XYCTZ it will shuffle it back to XYCZT
	 * 
	 * @param imp - the input {@link ImagePlus}
	 * @return - an {@link ImagePlus} which can be the same instance if the image is XYC, XYZ, XYT or XY - otherwise a new instance
	 * containing the same processors but in the new order XYZCT
	 */
	public static ImagePlus switchZTinXYCZT( final ImagePlus imp )
	{
		final int numChannels = imp.getNChannels();
		final int numTimepoints = imp.getNFrames();
		final int numZStacks = imp.getNSlices();
		
		String newTitle;
		if ( imp.getTitle().startsWith( "[XYCTZ]" ) )
			newTitle = imp.getTitle().substring( 8, imp.getTitle().length() );
		else
			newTitle = "[XYCTZ] " + imp.getTitle();

		// there is only one timepoint and one z-stack, i.e. XY(C)
		if ( numTimepoints == 1 && numZStacks == 1 )
		{
			return imp;
		}
		// there is only one channel XYZ(T) or one z-stack XYC(T)
		else if ( numTimepoints == 1 || numZStacks == 1 )
		{
			// numchannels, z-slices, timepoints 
			// but of course now reversed...
			imp.setDimensions( numChannels, numTimepoints, numZStacks );
			imp.setTitle( newTitle );
			return imp;
		}
		
		// now we have to rearrange
		final ImageStack stack = new ImageStack( imp.getWidth(), imp.getHeight() );
		
		for ( int z = 1; z <= numZStacks; ++z )
		{
			for ( int t = 1; t <= numTimepoints; ++t )
			{
				for ( int c = 1; c <= numChannels; ++c )
				{
					final int index = imp.getStackIndex( c, z, t );
					final ImageProcessor ip = imp.getStack().getProcessor( index );
					stack.addSlice( imp.getStack().getSliceLabel( index ), ip );
				}
			}
		}
		
		final ImagePlus result = new ImagePlus( newTitle, stack );
		// numchannels, z-slices, timepoints 
		// but of course now reversed...
		result.setDimensions( numChannels, numTimepoints, numZStacks );
		result.getCalibration().pixelWidth = imp.getCalibration().pixelWidth;
		result.getCalibration().pixelHeight = imp.getCalibration().pixelHeight;
		result.getCalibration().pixelDepth = imp.getCalibration().pixelDepth;
		final CompositeImage composite = CompositeImageFixer.makeComposite(  result );
		
		return composite;
	}

}
