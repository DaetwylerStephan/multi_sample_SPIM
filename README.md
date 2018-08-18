# multi_sample_SPIM

Selective plane illumination microscopy enables the imaging of many samples in parallel. Such an acquisition scheme produces several terabytes of data / day. To deal with such a huge amount of data, automatic image processing is desirable. Here, we provide the necessary code for the processing of the data coming from a custom SPIM microscope which is however easily adaptable to any other SPIM platform.

The following plugins can be found:

## SPIM_MaximumIntensityProjectionsTimepoints

This plugin iterates over all the samples and timepoints to generate the maximum projections in a parallelized matter

## StitchItSPIMmaxproj_

This plugin calculates the stitching parameter using phase correlation based on the code from Stephan Preibisch et al. [1] and fuses them together.





[1]	Preibisch, S., Saalfeld, S., & Tomancak, P. (2009). Globally optimal stitching of tiled 3D microscopic image acquisitions. Bioinformatics




