package stitching_preibisch;



import ij.IJ;
import ij.gui.Roi;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.util.Util;
import preibisch_code.TranslationModel2D;
import preibisch_code.TranslationModel3D;




public class CollectionStitchingImgLib2 
{

	public static ArrayList<ImagePlusTimePoint2> stitchCollection( final ArrayList< ImageCollectionElement2 > elements, final StitchingParameters params, final Boolean invertIT )
	{
		// the result
		final ArrayList<ImagePlusTimePoint2> optimized;
		
	
		if ( params.computeOverlap )
		{
			// find overlapping tiles
			
			final Vector< ComparePair2 > pairs = findOverlappingTiles( elements, params );
			//System.out.println("done overlapping Tiles size 1k " + pairs.size());
			if ( pairs == null || pairs.size() == 0 )
			{
				IJ.log( "No overlapping tiles could be found given the approximate layout." );
				return null;
			}
			
			// compute all compare pairs
			// compute all matchings
			final AtomicInteger ai = new AtomicInteger(0);
			
			final int numThreads;
			
			if ( params.cpuMemChoice == 0 )
				numThreads = 1;
			else
				numThreads = Runtime.getRuntime().availableProcessors();
			
			
			
			
	        final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
	    	
	        for ( int ithread = 0; ithread < threads.length; ++ithread )
	            threads[ ithread ] = new Thread(new Runnable()
	            {
	                public void run()
	                {		
	                   	final int myNumber = ai.getAndIncrement();
	                    
	                    for ( int i = 0; i < pairs.size(); i++ )
	                    {
	                    	if ( i % numThreads == myNumber )
	                    	{
	                    		final ComparePair2 pair = pairs.get( i );
	                    		
	                    		long start = System.currentTimeMillis();			
	                			
	                    		// where do we approximately overlap?
	                			final Roi roi1 = getROI( pair.getTile1().getElement(), pair.getTile2().getElement() );
	                			final Roi roi2 = getROI( pair.getTile2().getElement(), pair.getTile1().getElement() );
	                			
	                			//pair.getImagePlus1().show();
	                			//pair.getImagePlus2().show();
	                			final PairWiseStitchingResult result;
	                			
	                			if(invertIT == true){
	                			result = PairWiseStitchingImgLib.stitchPairwise( pair.getImagePlus2(), pair.getImagePlus1(), roi1, roi2, pair.getTimePoint1(), pair.getTimePoint2(), params );			
	    	                		
	                			}else{
	                			result = PairWiseStitchingImgLib.stitchPairwise( pair.getImagePlus1(), pair.getImagePlus2(), roi1, roi2, pair.getTimePoint1(), pair.getTimePoint2(), params );			
	                			}
	                			
	            				if ( result == null )
	            				{
	            					IJ.log( "Collection stitching failed" );
	            					return;
	            				}
	
	            				if ( params.dimensionality == 2 )
	            					pair.setRelativeShift( new float[]{ result.getOffset( 0 ), result.getOffset( 1 ) } );
	            				else
	            					pair.setRelativeShift( new float[]{ result.getOffset( 0 ), result.getOffset( 1 ), result.getOffset( 2 ) } );
	            				
	            				pair.setCrossCorrelation( result.getCrossCorrelation() );
	            				
	            				IJ.log( pair.getImagePlus1().getTitle() + "[" + pair.getTimePoint1() + "]" + " <- " + pair.getImagePlus2().getTitle() + "[" + pair.getTimePoint2() + "]" + ": " + 
	            						Util.printCoordinates( result.getOffset() ) + " correlation (R)=" + result.getCrossCorrelation() + " (" + (System.currentTimeMillis() - start) + " ms)");
	                    	}
	                    }
	                }
	            });
	        
	        final long time = System.currentTimeMillis();
	        SimpleMultiThreading.startAndJoin( threads );
	        
	        // get the final positions of all tiles
			optimized = GlobalOptimization.optimize( pairs, pairs.get( 0 ).getTile1(), params );
			IJ.log( "Finished registration process (" + (System.currentTimeMillis() - time) + " ms)." );
		}
		else
		{
			// all ImagePlusTimePoints, each of them needs its own model
			optimized = new ArrayList< ImagePlusTimePoint2 >();
			
			for ( final ImageCollectionElement2 element : elements )
			{
				final ImagePlusTimePoint2 imt = new ImagePlusTimePoint2( element.open( params.virtual ), element.getIndex(), 1, element.getModel(), element );
				
				// set the models to the offset
				if ( params.dimensionality == 2 )
				{
					final TranslationModel2D model = (TranslationModel2D)imt.getModel();
					model.set( element.getOffset( 0 ), element.getOffset( 1 ) );
				}
				else
				{
					final TranslationModel3D model = (TranslationModel3D)imt.getModel();
					model.set( element.getOffset( 0 ), element.getOffset( 1 ), element.getOffset( 2 ) );					
				}
				
				optimized.add( imt );
			}
			
		}
		
		return optimized;
	}

	protected static Roi getROI( final ImageCollectionElement2 imageCollectionElement, final ImageCollectionElement2 imageCollectionElement2 )
	{
		final int start[] = new int[ 2 ], end[] = new int[ 2 ];
		
		for ( int dim = 0; dim < 2; ++dim )
		{			
			// begin of 2 lies inside 1
			if ( imageCollectionElement2.offset[ dim ] >= imageCollectionElement.offset[ dim ] && imageCollectionElement2.offset[ dim ] <= imageCollectionElement.offset[ dim ] + imageCollectionElement.size[ dim ] )
			{
				start[ dim ] = (int) Math.round( imageCollectionElement2.offset[ dim ] - imageCollectionElement.offset[ dim ] );
				
				// end of 2 lies inside 1
				if ( imageCollectionElement2.offset[ dim ] + imageCollectionElement2.size[ dim ] <= imageCollectionElement.offset[ dim ] + imageCollectionElement.size[ dim ] )
					end[ dim ] = (int) Math.round( imageCollectionElement2.offset[ dim ] + imageCollectionElement2.size[ dim ] - imageCollectionElement.offset[ dim ] );
				else
					end[ dim ] = Math.round( imageCollectionElement.size[ dim ] );
			}
			else if ( imageCollectionElement2.offset[ dim ] + imageCollectionElement2.size[ dim ] <= imageCollectionElement.offset[ dim ] + imageCollectionElement.size[ dim ] ) // end of 2 lies inside 1
			{
				start[ dim ] = 0;
				end[ dim ] = (int) Math.round( imageCollectionElement2.offset[ dim ] + imageCollectionElement2.size[ dim ] - imageCollectionElement.offset[ dim ] );
			}
			else // if both outside then the whole image 
			{
				start[ dim ] = -1;
				end[ dim ] = -1;
			}
		
			
			}
		//System.out.println("dimension of roi " + start[0] + " start1 " + start[ 1 ]+ " end " + (end[ 0 ] - start[ 0 ]) + " end2 " + (end[ 1 ] - start[ 1 ]));
		return new Roi( new Rectangle( start[ 0 ], start[ 1 ], end[ 0 ] - start[ 0 ], end[ 1 ] - start[ 1 ] ) );
	}

	protected static Vector<ComparePair2> findOverlappingTiles( final ArrayList< ImageCollectionElement2 > elements, final StitchingParameters params )
	{		
		for ( final ImageCollectionElement2 element : elements )
		{
			if ( element.open( params.virtual ) == null )
				{return null;}
		}
		
		// all ImagePlusTimePoints, each of them needs its own model
		final ArrayList< ImagePlusTimePoint2 > listImp = new ArrayList< ImagePlusTimePoint2 >();
		for ( final ImageCollectionElement2 element : elements )
			{
			listImp.add( new ImagePlusTimePoint2( element.open( params.virtual ), element.getIndex(), 1, element.getModel(), element ) );
			}
		
		// get the connecting tiles
		final Vector< ComparePair2 > overlappingTiles = new Vector< ComparePair2 >();
		
		// Added by John Lapage: if the sequential option has been chosen, pair up each image 
		// with the images within the specified range, and return.
		if ( params.sequential )
		{
			for ( int i = 0; i < elements.size(); i++ )
			{
				for ( int j = 1 ; j <= params.seqRange ; j++ )
				{
					if ( ( i + j ) >= elements.size() ) 
						break;
					
					overlappingTiles.add( new ComparePair2( listImp.get( i ), listImp.get( i+j ) ) );
				}
			}
			return overlappingTiles;
		}
		// end of addition

		for ( int i = 0; i < elements.size() - 1; i++ )
			for ( int j = i + 1; j < elements.size(); j++ )
			{
				final ImageCollectionElement2 e1 = elements.get( i );
				final ImageCollectionElement2 e2 = elements.get( j );
				
				boolean overlapping = true;
				
				
				for ( int d = 0; d < params.dimensionality; ++d )
				{
					
					if ( !( ( e2.offset[ d ] >= e1.offset[ d ] && e2.offset[ d ] <= e1.offset[ d ] + e1.size[ d ] ) || 
						    ( e2.offset[ d ] + e2.size[ d ] >= e1.offset[ d ] && e2.offset[ d ] + e2.size[ d ] <= e1.offset[ d ] + e1.size[ d ] ) ||
						    ( e2.offset[ d ] <= e1.offset[ d ] && e2.offset[ d ] >= e1.offset[ d ] + e1.size[ d ] ) 
					   )  )
									overlapping = false;
							
				}
				
				if ( overlapping )
				{
					//final ImagePlusTimePoint impA = new ImagePlusTimePoint( e1.open(), e1.getIndex(), 1, e1.getModel().copy(), e1 );
					//final ImagePlusTimePoint impB = new ImagePlusTimePoint( e2.open(), e2.getIndex(), 1, e2.getModel().copy(), e2 );
					overlappingTiles.add( new ComparePair2( listImp.get( i ), listImp.get( j ) ) );
				}
			}
		
		return overlappingTiles;
	}
}
