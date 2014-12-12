package planner.data;

public class Configuration{
	private double x , y , angle;

	public Configuration(double x, double y, double a){
		setX(x);
		setY(y);
		setAngle(a);
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		//x = RoadMapModel.adjustRange(x, 0, Constant.BITMAPSIZE);
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		//y = RoadMapModel.adjustRange(y, 0, Constant.BITMAPSIZE);
		this.y = y;
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
		while(this.angle<0){
			this.angle+=360.0;
		}
		this.angle %=360.0;
	}
}