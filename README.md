# multi_sample_SPIM

Selective plane illumination microscopy enables the imaging of many samples in parallel. Such an acquisition scheme produces several terabytes of data / day. To deal with such a huge amount of data, automatic image processing is desirable. Here, we provide the necessary code for the processing of the data coming from a custom SPIM microscope which is however easily adaptable to any other SPIM platform. Parts of the code could be adapted to custom image processing and analysis pipelines.

The following plugins can be found:

## SPIM_MaximumIntensityProjectionsTimepoints

This plugin iterates over all the samples and timepoints to generate the maximum projections in a parallelized matter

## StitchItSPIMmaxproj_

This plugin calculates the stitching parameter of the maximum intenstiy projections using phase correlation based on the code from Stephan Preibisch et al. [1] and fuses them together using by default linear blending.

## SPIM_stitchPlanesParallelized_

This plugin calculates the stitching parameter for the 3D stacks and fuses them together using linear blending based on [1]. There are different options to do the stitching: first, the parameters from the maximum projection stitching could be used and applied to fuse the data. Second, the stitching could be calculated for a selected set of timepoints (based on the whole 3D content or of on-site generated maximum projections of those stacks) or for every timepoint individually. In our experience, using the stitching information from the maximum projections resulted in the best stitching results for long-term timelapse SPIM data.

## SPIM_DeleteFiles_

With this plugin, selected timepoints and stacks could be deleted easily.

## 	SPIM_compression

These scripts based on a script by Oscar Gonzales (MPI-CBG) generated zip files of the raw and TIFF stacks for data compression.

## 	SPIM_copy_rename_vascularDATA_

This plugin was applied to take the planes saved as individual files by the stitching and merge them to one stack per timepoint & angle.



[1]	Preibisch, S., Saalfeld, S., & Tomancak, P. (2009). Globally optimal stitching of tiled 3D microscopic image acquisitions. Bioinformatics




