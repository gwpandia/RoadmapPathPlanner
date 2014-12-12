package planner.utils;

import planner.data.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

public class ObstacleReader{

	public static Obstacle [] getObstacleData(String of){
		Obstacle [] obstacles;
		try {
			int obstacleNo, polygonNo, vertexNo;
			StreamTokenizer st;
			st = new StreamTokenizer(new BufferedReader(new FileReader(new File(of))));
			st.commentChar('#');
			st.nextToken();
			st.parseNumbers();
			obstacleNo = (int)st.nval;
			obstacles = new Obstacle[obstacleNo];
			for(int i = 0 ; i < obstacleNo ; i++ ){
				st.nextToken();
				st.parseNumbers();
				polygonNo = (int)st.nval;
				MyPolygon polygons [] = new MyPolygon[polygonNo];
				for(int j = 0 ; j < polygonNo ; j++ ){
					st.nextToken();
					st.parseNumbers();
					vertexNo = (int)st.nval;
					double x [] = new double[vertexNo];
					double y [] = new double[vertexNo];
					for( int k = 0 ; k < vertexNo ; k++ ){
						st.nextToken();
						st.parseNumbers();
						x[k] = st.nval;
						st.nextToken();
						st.parseNumbers();
						y[k] = st.nval;
					}
					polygons[j] = new MyPolygon( x , y , vertexNo );
				}
				
				double initx, inity, inita;
				st.nextToken();
				st.parseNumbers();
				initx = st.nval;
				st.nextToken();
				st.parseNumbers();
				inity = st.nval;
				st.nextToken();
				st.parseNumbers();
				inita = st.nval;
				Configuration initc = new Configuration(initx,inity,inita);
				obstacles[i] = new Obstacle(polygons, initc);
			}
			return obstacles;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/*
	public void ShowResult(){
		for(int i = 0 ; i < obstacles.length ; i++ ){
			System.out.println("Obstacle " + i);
			obstacles[i].showData();
		}
	}*/
}