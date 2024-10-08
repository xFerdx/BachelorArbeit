package lk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class starts the timer, the file reader (generating all the city objects)
 * and creates an Algo object containing the final solution.
 */
public class Map {
	private int version=1;
	private final int LINKED_CITIES=0, NEIGHBOR1=1, NEIGHBOR2=2;
	private final ArrayList<int[]> VERSIONS;	// parametesrs of first tour versions
	private final Algo			  ROUTE_GEN;	// route generator
	private final City[]			 CITIES;	// list of cities
	private final City		   CENTER_POINT;	// the center of the cartesian map
	private final double[][]	DIST_MATRIX;	// Array of all the distances values
	private final ArrayList<City>	  ROUTE;	// generated route
	private final int			   N_CITIES;	// Total number of cities
	private final double		COMPUT_TIME;	// total computation time
	private final boolean		 FILE_FOUND;	// found file checker

	/**
	 * Cities constructor
	 * @param fileName
	 */
	public Map(final String fileName){
		startTimer();						// starting the timer

		CITIES		= fileReader(fileName);	// reading the file and init the CITIES array
		N_CITIES	= CITIES.length;		// init the number of cities
		FILE_FOUND	= N_CITIES > 0;			// boolean to check whether cities have been found or not
		VERSIONS	= versionsInit();

		if(FILE_FOUND){
			CENTER_POINT= centerPointGen(); // generating the center point of the map
			DIST_MATRIX	= matrixGen();		// generating a distances matrix
			setClosest();					// setting list of closest cities
			ROUTE_GEN	= new Algo(this);	// generating an optimal path
			ROUTE 		= setRoute();		// setting the route array
		}else{
			CENTER_POINT= null;
			DIST_MATRIX = null;
			ROUTE_GEN	= null;
			ROUTE		= null;
		}
		
		COMPUT_TIME		= stopTimer();		// stopping the timer
	}

	public Map(double[][] points){
		startTimer();						// starting the timer

		CITIES		= new City[points.length];	// reading the file and init the CITIES array
		for (int i = 0; i < points.length; i++) {
			CITIES[i] = new City(i,points[i][0],points[i][1],i,this);
		}

		N_CITIES	= CITIES.length;		// init the number of cities
		FILE_FOUND	= N_CITIES > 0;			// boolean to check whether cities have been found or not
		VERSIONS	= versionsInit();

		if(FILE_FOUND){
			CENTER_POINT= centerPointGen(); // generating the center point of the map
			DIST_MATRIX	= matrixGen();		// generating a distances matrix
			setClosest();					// setting list of closest cities
			ROUTE_GEN	= new Algo(this);	// generating an optimal path
			ROUTE 		= setRoute();		// setting the route array
		}else{
			CENTER_POINT= null;
			DIST_MATRIX = null;
			ROUTE_GEN	= null;
			ROUTE		= null;
		}

		COMPUT_TIME		= stopTimer();
	}

	public Map(double[][] lengths, int z){
		startTimer();						// starting the timer

		CITIES		= new City[lengths.length];	// reading the file and init the CITIES array
		for (int i = 0; i < lengths.length; i++) {
			CITIES[i] = new City(i,0d,0d,i,this);
		}

		N_CITIES	= CITIES.length;		// init the number of cities
		FILE_FOUND	= false;			// boolean to check whether cities have been found or not
		VERSIONS	= versionsInit();


		CENTER_POINT= new City(-1, 0d, 0d, N_CITIES, this); // generating the center point of the map
		DIST_MATRIX	= lengths;		// generating a distances matrix
		setClosest();					// setting list of closest cities
		ROUTE_GEN	= new Algo(this);	// generating an optimal path
		ROUTE 		= setRoute();		// setting the route array
		COMPUT_TIME		= stopTimer();
	}


	

	// ..... SETTER METHODs .....


	// generating center point
	private City centerPointGen(){
		double x = 0, y = 0;

		for(City city :CITIES){
			x += city.getX();
			y += city.getY();
		}
		
		return new City(-1, x/N_CITIES, y/N_CITIES, N_CITIES, this);
	}

	// increasing the number of linked cities
	public void increaseLinkedCities(){
		VERSIONS.get(version-1)[LINKED_CITIES] = getLinkedCitiesQty()+1;
	}
	// set route version
	public void setVersion(final int v){
		version = Math.max(v, 1);
	}
	// adding a new version
	public void setNewVersion(){
		final int newNeighbor1 = VERSIONS.get(versionsQty()-1)[NEIGHBOR1] +2;
		final int newNeighbor2 = VERSIONS.get(versionsQty()-1)[NEIGHBOR2] +2;
		setVersion(versionsQty()+1);

		VERSIONS.add(new int[]{0, newNeighbor1, newNeighbor2});
	}

	// initialising first version
	private ArrayList<int[]> versionsInit(){
		final ArrayList<int[]> CONTAINER = new ArrayList<>();
		CONTAINER.add(new int[]{0, 0, 1});
		return CONTAINER;
	}

	// providing the cities with their relevant closest cities
	private void setClosest(){
		// providing a "closest cities array" to every city
		for(City cityA: CITIES)
			cityA.closest = Util.quickSort(CITIES.clone(), (cityB) -> getDistance(cityA, cityB));
	}


	/**
	 * Starting the timer
	 * @return current time
	 */
	private double startTimer(){
		return Util.timeTrack(true);
	}
	/**
	 * Stop the timer
	 * @return total time
	 */
	private double stopTimer(){
		final double timer = Util.timeTrack(false);
		Util.timeReset();
		return timer;
	}


	/**
	 * Converting the cities list to an array of cities
	 * @return converted array
	 */
	private City[] citiesToArray(ArrayList<City> citiesList){
		City[] citiesArray = new City[citiesList.size()];

		for(int i=0; i<citiesList.size(); i++)	citiesArray[i] = citiesList.get(i);
		return citiesArray;
	}

	/**
	 * Txt file reader
	 * @param fileName
	 * @return whether the file has been found or not
	 */
	private City[] fileReader(final String fileName){
		
		ArrayList<City> citiesList = new ArrayList<>();
        try{
			BufferedReader scann = new BufferedReader(new FileReader(fileName));
			String line = "";

			for(int id=0; (line = scann.readLine()) != null;){
				if(line.trim().matches("^\\s*([+-]?\\d+([.]\\d+)?\\s+){2}([+-]?\\d+([.]\\d+)?)\\s*$")){
					// cleaning the line
					final String delimiter = " ";
					line = line.replaceAll("\\s+", delimiter).trim();

					// splitting the line by attributes
					ArrayList<Double> cityAttr = new ArrayList<>();
					for(String strAttr: line.split(delimiter))	cityAttr.add(Double.parseDouble(strAttr));
		
					citiesList.add(new City(cityAttr.get(0), cityAttr.get(1), cityAttr.get(2), id++, this));
				}
			}
			scann.close();

        }catch (IOException e){
            System.out.println(Util.colorText("No file found!", "red"));
			return new City[0];
        }
        
		return citiesToArray(citiesList);
    }

	// setting the route array
	private ArrayList<City> setRoute(){
		final ArrayList<City> route = ROUTE_GEN.getRoute();
		route.add(route.get(0));
		return route;
	}


	/**
     * Euclidean calculation
     * @param a
	 * @param b
     * @return distance
     */
	private double euclidean(final City a, final City b){
		return Math.sqrt( Math.pow((a.getX() - b.getX()),2) + Math.pow((a.getY() - b.getY()),2) );
	}


	/**
	 * Setting the costs (distances) matrix
	 * @return costMatrix
	 */
	private double[][] matrixGen(){
		final City[] CITIES_AND_CENTER = new City[N_CITIES+1];
		for(int i=0; i<CITIES.length; i++)	CITIES_AND_CENTER[i] = CITIES[i];
		CITIES_AND_CENTER[N_CITIES] = CENTER_POINT;
		final int CC_LENGTH = CITIES_AND_CENTER.length;

		final double[][] MATRIX = new double[CC_LENGTH][CC_LENGTH];
		int cityA, cityB;

		for(int i=0; i<CC_LENGTH; i++){
			cityA = CITIES_AND_CENTER[i].getMatrixIndex();
			
			for(int j=0; j<CC_LENGTH; j++){
				cityB = CITIES_AND_CENTER[j].getMatrixIndex();
				MATRIX[cityA][cityB] = euclidean(CITIES_AND_CENTER[i], CITIES_AND_CENTER[j]);
			}
		}
		return MATRIX;
	}










	//.....	GETTER METHODs .....


	// get route
	public ArrayList<Integer> getRoute(){
		ArrayList<Integer> intRoute = new ArrayList<>();

		for(City city: ROUTE){
			intRoute.add((int)city.getName());
		}

		return intRoute;
	}

	// get total distance
	private double getRouteDistance(){
		double distance = 0;

		for(int i=0; i<ROUTE.size()-1; i++){
			distance += getDistance(ROUTE.get(i), ROUTE.get(i+1));
		}

		return distance;
	}

	// getting versions quantity
	private int versionsQty(){
		return VERSIONS==null? 0: VERSIONS.size();
	}
	// get number of linked cities
	public int getLinkedCitiesQty(){
		return VERSIONS.get(version-1)[LINKED_CITIES];
	}
	// getting neighbor 1
	public int indexNeighbor1(){
		return VERSIONS.get(version-1)[NEIGHBOR1];
	}
	// getting neighbor 2
	public int indexNeighbor2(){
		return VERSIONS.get(version-1)[NEIGHBOR2];
	}
	// getting route (first tour) version
	public int getVersion(){
		return version;
	}

	// getting the center of the map
	public City getCenterPoint(){
		return CENTER_POINT;
	}


	/**
	 * Getting the distance between two given cities
	 * @param a first city
	 * @param b second city
	 * @return euclidean distance
	 */
	public double getDistance(final City a, final City b){
		return DIST_MATRIX[a.getMatrixIndex()][b.getMatrixIndex()];
	}

	// get a copy of the cities array
	public City[] getCities(){
		return CITIES.clone();
	}

	// getting size
	public int getQuantity(){
		return N_CITIES;
	}

	// get state
	public boolean fileFound(){
		return FILE_FOUND;
	}

	// get compilation time in milliseconds
	public double getCompTime(){
		return (double)COMPUT_TIME/1000000;
	}

	// print results
	public void printResult(){
		String color = "cyan";

		System.out.println();
		System.out.println(Util.colorText("Cities route:		",	color)	+ getRoute()		);
		System.out.println(Util.colorText("Route distance:		",	color) 	+ getRouteDistance());
		System.out.println(Util.colorText("Computation (ms):	",	color)	+ getCompTime()		);
		System.out.println(Util.colorText("Number of cities:	",	color)	+ getQuantity()		);
		System.out.println();
	}


}