

import ij.process.Blitter;
import ij.process.ImageProcessor;

public class MaximumProjector implements Projector {

	private ImageProcessor ip = null;

	@Override
	public void reset() {
		ip = null;
	}

	@Override
	public void add(ImageProcessor ip) {
		if(this.ip == null)
			this.ip = ip;
		else
			this.ip.copyBits(ip, 0, 0, Blitter.MAX);
	}
	
	
	public void add_ROI(ImageProcessor newimage) {
		if(this.ip == null)
			this.ip = newimage;
		else
			this.ip.copyBits(newimage, 0, 0, Blitter.MAX);
	}

	@Override
	public ImageProcessor getProjection() {
		return ip;
	}
}
