package planner.utils;

import planner.data.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;

public class RobotReader{
	public static Robot [] getRobotData(String rf){
		Robot [] robots;
		try {
			int robotNo, polygonNo, vertexNo, cpNo;
			StreamTokenizer st;
			st = new StreamTokenizer(new BufferedReader(new FileReader(new File(rf))));
			st.commentChar('#');
			st.nextToken();
			st.parseNumbers();
			robotNo = (int)st.nval;
			robots = new Robot[robotNo];
			for(int i = 0 ; i < robotNo ; i++ ){
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
				double initx, inity, inita, goalx, goaly, goala;
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
				st.nextToken();
				st.parseNumbers();
				goalx = st.nval;
				st.nextToken();
				st.parseNumbers();
				goaly = st.nval;
				st.nextToken();
				st.parseNumbers();
				goala = st.nval;
				Configuration goalc = new Configuration(goalx,goaly,goala);
				st.nextToken();
				st.parseNumbers();
				cpNo = (int)st.nval;
				double cpx [] = new double[cpNo];
				double cpy [] = new double[cpNo];
				for( int l = 0 ; l < cpNo ; l++ ){
					st.nextToken();
					st.parseNumbers();
					cpx[l] = st.nval;
					st.nextToken();
					st.parseNumbers();
					cpy[l] = st.nval;
				}
				robots[i] = new Robot(cpx, cpy, polygons, initc, goalc);
			}
			return robots;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	/*
	public void ShowResult(){
		for(int i = 0 ; i < robots.length ; i++ ){
			System.out.println("Robot " + i);
			robots[i].showData();
		}
	}*/
}