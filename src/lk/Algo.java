package lk;

import java.util.ArrayList;

public class Algo {

	private ArrayList<City>	 finalRoute;		// Array storing the final improved path
	private City[] 		 		 cities;		// Array of Cities
	private City baseCity;		// Starting point
    private final Map MAP;		// Map object, container of cities
	private final int		   N_CITIES;		// Total number of cities
	private final int			MAX_OPT;		// Maximum optimization levels (representing the number of "K-OPT" to carry out)
	private final int 			MAX_LKH;		// Maximum optimization attempts of each "K-OPT" level (Lin-Kernighan Heuristic)
	private final double MIN_GAIN = 0.00001;	// Optimiser (Lin-Kernighan) minimum acceptable gain

	public Algo(final Map M, final int K_OPT, final int LKH){
		MAP 		= M;
		cities 		= MAP.getCities();
		N_CITIES 	= MAP.getQuantity();
		MAX_OPT 	= K_OPT;
		MAX_LKH	 	= N_CITIES > (LKH+2)? LKH: N_CITIES > 3? N_CITIES-3: 0;

		// ......... starting the algorithms .........
        kruskal();		// starting the greedy algorithm for the first tour
		optimiser();	// running the optimiser (Chained Lin-Kernighan Heuristic)
		settingRoute();	// setting the array of the final improved path
    }
    public Algo(final Map M){
		// first parameter is the cities array
		// second one is the number of k-OPT
		// third one is the number of lkh cycles per level
		this(M, 3, 5);	// calling the main contructos
    }


	//................setters..............

	private void settingRoute(){
		this.finalRoute	= new ArrayList<>();
		this.baseCity 	= this.cities[0];
		City city		= this.baseCity, prevCity;

		// copying every city into the "finalRoute" array
		while(this.finalRoute.size() < this.N_CITIES){
			this.finalRoute.add(city);

			prevCity = this.finalRoute.size() > 1? this.finalRoute.get(this.finalRoute.size()-2): city;
			city = city.getNextCity(prevCity);
		}
	}




	// ............algorithms...........


	private void kruskal(){
		final City[] v = this.cities.clone();
		for(int j=0; !v[j].routeComplete(); j = j==this.N_CITIES-1? 0: j+1) {
			v[j].linkClosest();
		}
		MAP.setNewVersion();
		for(int j=0; !v[j].routeComplete(); j = j==this.N_CITIES-1? 0: j+1) {
			v[j].linkClosest();
		}

	}


	private void optimiser(){
		final short START_LEVEL = 1, START_SCORE = 0;	// setting strting parameters for LKH
		this.baseCity = this.cities[0];					// getting the first city for LKH

		if(this.N_CITIES > 3 && this.MAX_OPT > 0){
			for(int i=0; i<this.N_CITIES-1;){
				// condition for Chaining (attempting to improve again)
				if(linKernighan(START_LEVEL, START_SCORE));
				else this.baseCity = this.cities[++i];
			}
		}
	}


	private boolean linKernighan(final int CURRENT_OPT, final double PREV_GAIN){
		final Score[] SCORED= new Score[this.N_CITIES-3];

		// ------------------getting best candidates by LKH------------------
		final City PREV_BASE= this.baseCity.getNeighbour2();
		final City NEXT_BASE= this.baseCity.getNeighbour1();
		City prevCity		= NEXT_BASE;
		City tempCity		= prevCity.getNextCity(this.baseCity);
		City prevHolder;

		// getting scores of all the cities
        for(int i=0;	tempCity != PREV_BASE; i++){
			SCORED[i] = new Score(tempCity, prevCity, MAP.getDistance(prevCity,tempCity)-MAP.getDistance(NEXT_BASE,tempCity));
			
			prevHolder	= prevCity;
			prevCity	= tempCity;
			tempCity	= tempCity.getNextCity(prevHolder);
        }
		
		// sorting by score in a decreasing order (the last two paramenters are respectively "left start" and "right end")
        scoreSort(SCORED, 0, SCORED.length-1);


		//-----------------running the K-opt-------------------
		for(int i=0; i<this.MAX_LKH; i++){
			final City ELECTED_CITY = SCORED[i].CITY;
			final City PREV_ELECTED = SCORED[i].PREV_CITY;
	
			final double old1 = MAP.getDistance(this.baseCity,	NEXT_BASE   );
			final double old2 = MAP.getDistance(PREV_ELECTED ,	ELECTED_CITY);
			final double new1 = MAP.getDistance(this.baseCity,	PREV_ELECTED);
			final double new2 = MAP.getDistance(NEXT_BASE	 ,	ELECTED_CITY);
	
			final double GAIN = old1 + old2 - new1 - new2 + PREV_GAIN;

			// flipping the cities
			flip(this.baseCity, NEXT_BASE, PREV_ELECTED, ELECTED_CITY);
			
			// condition to immediately exit the "LKH" function or generate a new "OPT" level (recursion)
			if(GAIN > this.MIN_GAIN || CURRENT_OPT < this.MAX_OPT && linKernighan(CURRENT_OPT+1, GAIN))	return true;
			else flip(this.baseCity, PREV_ELECTED, NEXT_BASE, ELECTED_CITY); // go back to previous state
		}

		return false;
	}


	private void flip(final City prevA, final City A, final City B, final City nextB){
		prevA.replaceNeighbour(A, B);		// exchange the link between prevA and A with prevA and B
		A.replaceNeighbour(prevA, nextB);	// exchange the link between A and prevA with A and prevB

		nextB.replaceNeighbour(B, A);		// exchange the link between nextB and B with nextB and A
		B.replaceNeighbour(nextB, prevA);	// exchange the link between B and nextB with B and prevA
	}


	private void scoreSort(final Score[] arr, final int LEFT, final int RIGHT){
		int l	= LEFT, r = RIGHT;

		// getting the pivot (LKH score) from a calculated mid point
		final double PIVOT = arr[(l + r) / 2].SCORE;

		// partition 
		while (l <= r) {
			// loop left index if the current score is greater than the pivot one
			while (arr[l].SCORE > PIVOT) l++;
			// loop right index if the current score is smaller than the pivot one
			while (arr[r].SCORE < PIVOT) r--;

			if (l <= r) {
				final Score TMP_NODE= arr[l];
				arr[l++]			= arr[r];
				arr[r--]			= TMP_NODE;
			}
		}

		// sorting from right to left
		if (LEFT < r )						scoreSort(arr, LEFT,  r);
		// sorting from left to right and terminate the sorting algorithm at the "MAX_LKH"nth sorted element
		if (l < this.MAX_LKH && l < RIGHT)	scoreSort(arr, l, RIGHT);
	}



    //...........getter methods...........


	// get total distance
	private double getRouteDistance(){
		double distance = 0;

		this.baseCity	= this.cities[0];
		City city		= this.baseCity;
		City prevCity	= this.baseCity.getNeighbour2();
		City prevHolder;

		// copying every city into the "finalRoute" array
		for(int i=0; i< this.N_CITIES; i++){
			distance	+= MAP.getDistance(city, prevCity);

			prevHolder	= prevCity;
			prevCity	= city;
			city		= city.getNextCity(prevHolder);
		}

		return distance;
	}

    public ArrayList<City> getRoute(){
        return new ArrayList<>(finalRoute);
    }
}