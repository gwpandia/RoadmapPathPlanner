package planner.data;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import sun.awt.geom.Crossings;
import java.util.Arrays;

import planner.interfaces.Transformable;

@SuppressWarnings("serial")
public class MyPolygon implements java.awt.Shape, java.io.Serializable, Transformable {

	public int npoints;

	public double xpoints[];

	public double ypoints[];

	protected Rectangle2D.Double bounds;

	private static final int MIN_LENGTH = 4;

	public MyPolygon() {
		xpoints = new double[MIN_LENGTH];
		ypoints = new double[MIN_LENGTH];
	}

	public MyPolygon(double xpoints[], double ypoints[], int npoints) {
		// Fix 4489009: should throw IndexOutofBoundsException instead
		// of OutofMemoryException if npoints is huge and > {x,y}points.length
		if (npoints > xpoints.length || npoints > ypoints.length) {
			throw new IndexOutOfBoundsException("npoints > xpoints.length || "
					+ "npoints > ypoints.length");
		}
		// Fix 6191114: should throw NegativeArraySizeException with
		// negative npoints
		if (npoints < 0) {
			throw new NegativeArraySizeException("npoints < 0");
		}
		// Fix 6343431: Applet compatibility problems if arrays are not
		// exactly npoints in length
		this.npoints = npoints;
		this.xpoints = Arrays.copyOf(xpoints, npoints);
		this.ypoints = Arrays.copyOf(ypoints, npoints);
	}

	public void reset() {
		npoints = 0;
		bounds = null;
	}

	public void invalidate() {
		bounds = null;
	}

	public void translate(double deltaX, double deltaY) {
		for (int i = 0; i < npoints; i++) {
			xpoints[i] += deltaX;
			ypoints[i] += deltaY;
		}
		if (bounds != null) {
			translate(deltaX, deltaY);
		}
	}

	void calculateBounds(double xpoints[], double ypoints[], int npoints) {
		double boundsMinX = Double.MAX_VALUE;
		double boundsMinY = Double.MAX_VALUE;
		double boundsMaxX = Double.MIN_VALUE;
		double boundsMaxY = Double.MIN_VALUE;

		for (int i = 0; i < npoints; i++) {
			double x = xpoints[i];
			boundsMinX = Math.min(boundsMinX, x);
			boundsMaxX = Math.max(boundsMaxX, x);
			double y = ypoints[i];
			boundsMinY = Math.min(boundsMinY, y);
			boundsMaxY = Math.max(boundsMaxY, y);
		}
		bounds = new Rectangle2D.Double(boundsMinX, boundsMinY,
				 (boundsMaxX - boundsMinX),
				 (boundsMaxY - boundsMinY));
	}

	void updateBounds(double x, double y) {
		if (x < bounds.x) {
			bounds.width = (int) (bounds.width + (bounds.x - x));
			bounds.x = (int) x;
		} else {
			bounds.width = Math.max(bounds.width, (int) (x - bounds.x));
			// bounds.x = bounds.x;
		}

		if (y < bounds.y) {
			bounds.height = (int) (bounds.height + (bounds.y - y));
			bounds.y = (int) y;
		} else {
			bounds.height = Math.max(bounds.height, (int) (y - bounds.y));
			// bounds.y = bounds.y;
		}
	}

	public void addPoint(double x, double y) {
		if (npoints >= xpoints.length || npoints >= ypoints.length) {
			int newLength = npoints * 2;
			// Make sure that newLength will be greater than MIN_LENGTH and
			// aligned to the power of 2
			if (newLength < MIN_LENGTH) {
				newLength = MIN_LENGTH;
			} else if ((newLength & (newLength - 1)) != 0) {
				newLength = Integer.highestOneBit(newLength);
			}

			xpoints = Arrays.copyOf(xpoints, newLength);
			ypoints = Arrays.copyOf(ypoints, newLength);
		}
		xpoints[npoints] = x;
		ypoints[npoints] = y;
		npoints++;
		if (bounds != null) {
			updateBounds(x, y);
		}
	}

	public Rectangle getBounds() {
		return getBoundingBox();
	}

	@Deprecated
	public Rectangle getBoundingBox() {
		if (npoints == 0) {
			return new Rectangle();
		}
		if (bounds == null) {
			calculateBounds(xpoints, ypoints, npoints);
		}
		return bounds.getBounds();
	}

	public boolean contains(Point p) {
		return contains(p.x, p.y);
	}

	public boolean contains(int x, int y) {
		return contains((double) x, (double) y);
	}

	@Deprecated
	public boolean inside(int x, int y) {
		return contains((double) x, (double) y);
	}

	public Rectangle2D getBounds2D() {
		return getBounds();
	}
	
	public Rectangle2D.Double getBounds2DDouble(){
		double MAXX = Double.MIN_VALUE;
		double MINX = Double.MAX_VALUE;
		double MAXY = Double.MIN_VALUE;
		double MINY = Double.MAX_VALUE;
		for(int i = 0; i < npoints; i++){
			if(xpoints[i] < MINX){
				MINX = xpoints[i];
			}
			if(ypoints[i] < MINY){
				MINY = ypoints[i];
			}
			if(xpoints[i] >= MAXX){
				MAXX = xpoints[i];
			}
			if(ypoints[i] >= MAXY){
				MAXY = ypoints[i];
			}
		}
		return new Rectangle2D.Double(MINX, MINY, MAXX-MINX, MAXY-MINY);
	}

	public boolean contains(double x, double y) {
		if (npoints <= 2 || !getBoundingBox().contains(x, y)) {
			return false;
		}
		int hits = 0;

		double lastx = xpoints[npoints - 1];
		double lasty = ypoints[npoints - 1];
		double curx, cury;

		// Walk the edges of the polygon
		for (int i = 0; i < npoints; lastx = curx, lasty = cury, i++) {
			curx = xpoints[i];
			cury = ypoints[i];

			if (cury == lasty) {
				continue;
			}

			double leftx;
			if (curx < lastx) {
				if (x >= lastx) {
					continue;
				}
				leftx = curx;
			} else {
				if (x >= curx) {
					continue;
				}
				leftx = lastx;
			}

			double test1, test2;
			if (cury < lasty) {
				if (y < cury || y >= lasty) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - curx;
				test2 = y - cury;
			} else {
				if (y < lasty || y >= cury) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - lastx;
				test2 = y - lasty;
			}

			if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
				hits++;
			}
		}

		return ((hits & 1) != 0);
	}

	private Crossings getCrossings(double xlo, double ylo, double xhi,
			double yhi) {
		Crossings cross = new Crossings.EvenOdd(xlo, ylo, xhi, yhi);
		double lastx = xpoints[npoints - 1];
		double lasty = ypoints[npoints - 1];
		double curx, cury;

		// Walk the edges of the polygon
		for (int i = 0; i < npoints; i++) {
			curx = xpoints[i];
			cury = ypoints[i];
			if (cross.accumulateLine(lastx, lasty, curx, cury)) {
				return null;
			}
			lastx = curx;
			lasty = cury;
		}

		return cross;
	}

	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	public boolean intersects(double x, double y, double w, double h) {
		if (npoints <= 0 || !getBoundingBox().intersects(x, y, w, h)) {
			return false;
		}

		Crossings cross = getCrossings(x, y, x + w, y + h);
		return (cross == null || !cross.isEmpty());
	}

	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public boolean contains(double x, double y, double w, double h) {
		if (npoints <= 0 || !getBoundingBox().intersects(x, y, w, h)) {
			return false;
		}

		Crossings cross = getCrossings(x, y, x + w, y + h);
		return (cross != null && cross.covers(y, y + h));
	}

	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return new PolygonPathIterator(this, at);
	}

	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return getPathIterator(at);
	}

	class PolygonPathIterator implements PathIterator {
		MyPolygon poly;
		AffineTransform transform;
		int index;

		public PolygonPathIterator(MyPolygon pg, AffineTransform at) {
			poly = pg;
			transform = at;
			if (pg.npoints == 0) {
				// Prevent a spurious SEG_CLOSE segment
				index = 1;
			}
		}

		public int getWindingRule() {
			return WIND_EVEN_ODD;
		}

		public boolean isDone() {
			return index > poly.npoints;
		}

		public void next() {
			index++;
		}

		public int currentSegment(float[] coords) {
			if (index >= poly.npoints) {
				return SEG_CLOSE;
			}
			coords[0] = (float) poly.xpoints[index];
			coords[1] = (float) poly.ypoints[index];
			if (transform != null) {
				transform.transform(coords, 0, coords, 0, 1);
			}
			return (index == 0 ? SEG_MOVETO : SEG_LINETO);
		}

		public int currentSegment(double[] coords) {
			if (index >= poly.npoints) {
				return SEG_CLOSE;
			}
			coords[0] = poly.xpoints[index];
			coords[1] = poly.ypoints[index];
			if (transform != null) {
				transform.transform(coords, 0, coords, 0, 1);
			}
			return (index == 0 ? SEG_MOVETO : SEG_LINETO);
		}
	}

	@Override
	public java.awt.geom.Point2D.Double[] getPoints() {
		Point2D.Double [] points = new Point2D.Double[this.npoints];
		for(int i = 0; i < points.length; i++){
			points[i] = new Point2D.Double(xpoints[i], ypoints[i]);
		}
		return points;
	}
}
