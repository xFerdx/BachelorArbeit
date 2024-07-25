package lks;

import java.util.ArrayList;

public class LK {

	public static ArrayList<Integer> solve(double[][] points){
		Map map = new Map(points);
		return map.getRoute();
	}

	public static ArrayList<Integer> solve(double[][] lengths, int z){
		Map map = new Map(lengths,z);
		return map.getRoute();
	}
}
