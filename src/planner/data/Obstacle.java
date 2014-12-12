package planner.data;

public class Obstacle{
	private MyPolygon [] polygons;
	private Configuration initc;
	
	public Obstacle(MyPolygon [] polygons, Configuration c){
		setPolygons(polygons);
		setInitc(c);
	}

	public MyPolygon[] getPolygons() {
		return polygons;
	}

	public void setPolygons(MyPolygon[] polygons) {
		this.polygons = polygons;
	}

	public Configuration getInitc() {
		return initc;
	}

	public void setInitc(Configuration initc) {
		this.initc = initc;
	}
	
	public MyPolygon getPolygon( int i ){
		return polygons[i];
	}
	
	public void showData(){
		System.out.println("InitX: " + getInitc().getX() + 
				" InitY: " + getInitc().getY() + " InitA: " + getInitc().getAngle());
		for(int i = 0 ; i < getPolygons().length ; i++ ){
			System.out.println("X: ");
			for(int j = 0 ; j < getPolygon(i).xpoints.length ; j++ )
				System.out.print(getPolygon(i).xpoints[j] + " ");
			System.out.println("\nY: ");
			for(int j = 0 ; j < getPolygon(i).ypoints.length ; j++ )
				System.out.print(getPolygon(i).ypoints[j] + " ");
			System.out.println();
		}
	}
	
}