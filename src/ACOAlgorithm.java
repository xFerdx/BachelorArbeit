import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class ACOAlgorithm {
    private final double[][] distanceMatrix;
    private final double[][] pheromoneMatrix;
    private final int numAnts;
    private final double alpha;
    private final double beta;
    private final double evaporationRate;
    private final int numCities;
    private final Random random;

    public ACOAlgorithm(double[][] distanceMatrix, double antRate, double alpha, double beta, double evaporationRate) {
        this.distanceMatrix = distanceMatrix;
        this.numAnts = (int) Math.max(1,distanceMatrix.length * antRate);
        this.alpha = alpha;
        this.beta = beta;
        this.evaporationRate = evaporationRate;
        this.numCities = distanceMatrix.length;
        this.random = new Random();
        this.pheromoneMatrix = new double[numCities][numCities];
        for (int i = 0; i < numCities; i++) {
            Arrays.fill(pheromoneMatrix[i], 1);
            pheromoneMatrix[i][i] = 0;
        }
    }

    public ArrayList<Integer> solve(int maxIterations) {
        ArrayList<Integer> bestTour = null;
        double bestTourLength = Double.POSITIVE_INFINITY;

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            //if(((double)(iteration * 100) / maxIterations) % 10 == 0) System.out.println((double)iteration/ maxIterations);

            ArrayList<ArrayList<Integer>> antTours = new ArrayList<>(numAnts);
            for (int ant = 0; ant < numAnts; ant++) {
                ArrayList<Integer> tour = constructTour();
                antTours.add(tour);
                double tourLength = calculateTourLength(tour);
                if (tourLength < bestTourLength) {
                    bestTourLength = tourLength;
                    bestTour = new ArrayList<>(tour);
                }
            }
            updatePheromones(antTours);
            evaporatePheromones();
        }
        Objects.requireNonNull(bestTour).add(bestTour.get(0));
        return bestTour;
    }

    private ArrayList<Integer> constructTour() {
        ArrayList<Integer> tour = new ArrayList<>(numCities);
        boolean[] visited = new boolean[numCities];
        int startCity = random.nextInt(numCities);
        tour.add(startCity);
        visited[startCity] = true;
        for (int i = 1; i < numCities; i++) {
            int nextCity = selectNextCity(tour.get(i - 1), visited);
            tour.add(nextCity);
            visited[nextCity] = true;
        }
        //tspSolver.Opt2(tour);
        return tour;
    }

    private int selectNextCity(int currentCity, boolean[] visited) {
        double totalProbability = 0;
        double[] probabilities = new double[numCities];
        for (int i = 0; i < numCities; i++) {
            if (visited[i]) continue;
            double pheromone = pheromoneMatrix[currentCity][i];
            double distance = distanceMatrix[currentCity][i];
            probabilities[i] = Math.pow(pheromone, alpha) * Math.pow(1.0 / distance, beta);
            totalProbability += probabilities[i];
        }
        double rand = random.nextDouble() * totalProbability;
        double sum = 0;
        for (int i = 0; i < numCities; i++) {
            sum += probabilities[i];
            if (rand <= sum)
                return i;
        }
        return -1;
    }

    private double calculateTourLength(ArrayList<Integer> tour) {
        double length = 0;
        for (int i = 0; i < numCities; i++)
            length += distanceMatrix[tour.get(i)][tour.get((i+1) % numCities)];
        return length;
    }

    private void updatePheromones(ArrayList<ArrayList<Integer>> antTours) {
        for (ArrayList<Integer> tour : antTours) {
            double pheromoneToAdd = 1 / calculateTourLength(tour);
            for (int i = 0; i < numCities; i++) {
                pheromoneMatrix[tour.get(i)][tour.get((i+1) % numCities)] += pheromoneToAdd;
                pheromoneMatrix[tour.get((i+1) % numCities)][tour.get(i)] += pheromoneToAdd;
            }
        }
    }

    private void evaporatePheromones() {
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                if (i != j) {
                    pheromoneMatrix[i][j] *= (1 - evaporationRate);
                }
            }
        }
    }

}