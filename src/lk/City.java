package lk;

import java.util.ArrayList;

public class City{

	// number of total linked cities / version of the path /
	
	private final Double   NAME, X, Y;						// this city coordinates
	private final int	 MATRIX_INDEX;						// matrix index of this city to get computed distances
	private ArrayList<City>  neighbors = new ArrayList<>();	// this city neighbors (as pairs)
	private ArrayList<City> clusterEnd = new ArrayList<>();	// last city of the current cluster of this city
	private final Map MAP;						// cities container
	public City[] 			  closest;						// array of closest cities

	public City(final double n, final Double x, final Double y, final int id, final Map m){
		// initialising non static constants
		MATRIX_INDEX= id;
		MAP			= m;
		NAME		= n;
		X	 		= x;
		Y	 		= y;
	}


	// ....... setter methods ......

	private void neighborAdd(final City that){
		neighbors.add(that);
		if(getClusterEnd().clusterEnd.size() < MAP.getVersion()){
			getClusterEnd().clusterEnd.add(this.getClusterEnd()==that.getClusterEnd()? that: that.getClusterEnd());
		}else getClusterEnd().clusterEnd.set(MAP.getVersion()-1, this.getClusterEnd()==that.getClusterEnd()? that: that.getClusterEnd());
	}

	private boolean setNeighbors(final City that){
		if(canLinkTo(that)){
			this.neighborAdd(that);
			that.neighborAdd(this);
			MAP.increaseLinkedCities();
			return true;
		}else return false;
	}

	public boolean linkClosest(){
		if(isLinkable()){
			for(int i = 1; i<closest.length; i++){
				if(setNeighbors(getClosest(i))) return true;
			}
		}
		return false;
	}

	// setting neighbor 1
	private void setNeighbor1(final City c){
		neighbors.set(MAP.indexNeighbor1(), c);
	}
	// setting neighbor 2
	private void setNeighbor2(final City c){
		neighbors.set(MAP.indexNeighbor2(), c);
	}

	public void replaceNeighbour(final City OLD, final City NEW){
		if	   (getNeighbour1() == OLD) setNeighbor1(NEW);
		else if(getNeighbour2() == OLD) setNeighbor2(NEW);
		else if(getNeighbour1() == NEW) setNeighbor1(OLD);
		else if(getNeighbour2() == NEW) setNeighbor2(OLD);
	}


	// ....... getter methods ........


	// getting edges status
	private boolean isLinkable(){
		return getNeighborsQty()<2 && !routeComplete();
	}

	private boolean canLinkTo(final City that){
		return this != that && (this != that.getClusterEnd() || MAP.getLinkedCitiesQty() == closest.length-1) && isLinkable() && that.isLinkable();
	}

	// getting the other end of this city cluster
	private City getClusterEnd(){
		return clusterEnd.size() < MAP.getVersion()? this :clusterEnd.get(MAP.getVersion()-1);
	}

	// getting the name of the city at the edge 1
	public City getNeighbour1(){
		return neighbors.get(MAP.indexNeighbor1());
	}

	// getting the name of the city at the edge 2
	public City getNeighbour2(){
		return neighbors.get(MAP.indexNeighbor2());
	}

	// getting city id
	public int getMatrixIndex(){
		return MATRIX_INDEX;
	}
	
	// getting city name
	public double getName(){
		return NAME;
	}

	// getting x axis
	public double getX(){
		return X;
	}

	// getting Y axis
	public double getY(){
		return Y;
	}

	// get amount of neighbors
	private int getNeighborsQty(){
		return neighbors.size()+2 - (2*MAP.getVersion());
	}

	public City getClosest(int n){
		n = Math.max(n, 1);
		return closest[n] != this? closest[n]: closest[n-1];
	}
	
	// return true if the overall routing is complete
	public boolean routeComplete(){
		return MAP.getLinkedCitiesQty() == closest.length;
	}

	public City getNextCity(final City prevCity){
		return prevCity == getNeighbour1()? getNeighbour2(): getNeighbour1();
	}
}