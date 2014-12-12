package planner.data;

public class Robot{
	private double [] cpsx;
	private double [] cpsy;
	private Configuration goalc;
	private Configuration initc;
	private MyPolygon [] polygons;
	
	public Robot(double [] cpsx , double [] cpsy , MyPolygon [] polygons, Configuration ic, Configuration gc){
		setPolygons(polygons);
		setCpsx(cpsx);
		setCpsy(cpsy);
		setInitc(ic);
		setGoalc(gc);
	}
	
	public double getCPX( int i ){
		return cpsx[i];
	}
	
	public double getCPY( int i ){
		return cpsy[i];
	}
	
	public int getCPNum(){
		return (cpsx.length == cpsy.length) ? cpsx.length : null;
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
	
	public double[] getCpsx() {
		return cpsx;
	}

	public void setCpsx(double[] cpsx) {
		this.cpsx = cpsx;
	}

	public double[] getCpsy() {
		return cpsy;
	}

	public void setCpsy(double[] cpsy) {
		this.cpsy = cpsy;
	}

	public Configuration getGoalc() {
		return goalc;
	}

	public void setGoalc(Configuration goalc) {
		this.goalc = goalc;
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
		System.out.println("GoalX: " + goalc.getX() + " GoalY: " + goalc.getY() + " GoalA: " + goalc.getAngle());
		System.out.println("CPX: ");
		for(int j = 0 ; j < cpsx.length ; j++ )
			System.out.print(cpsx[j] + " ");
		System.out.println("\nCPY: ");
		for(int j = 0 ; j < cpsy.length ; j++ )
			System.out.print(cpsy[j] + " ");
		System.out.println();
	}

}