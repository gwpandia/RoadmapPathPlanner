package planner.data;

public class Constant{
	public static final int BITMAPSIZE = 128, INIT = 254, OBST = 255, GOAL = 0, 
		HC = 800, SWP = 1, MAXCONFIG = 300, MAXPOINT = 300, POINTED = 253, 
		ANGLESPACE = 36, XSPACE = BITMAPSIZE, YSPACE = BITMAPSIZE, 
		POTENTIALMAX = OBST, PERSTRUCT = 8000, OCCUPIED = -1;
	public static final int NONE = 0, ROBOT_I = 1, ROBOT_G = 2, OBS = 3, FILES = 8;
	public static final int OUTOFBOUND = 2, COLLISION = 1, FREE = 0;
	public static final boolean NOTVISIT = false, VISIT = true;
	public static final double SPC = (double)HC / (double)BITMAPSIZE;
	public static final String DATROOT = "http://www.cs.nccu.edu.tw/~s9429/csproj/planner/DAT";
}