import java.util.ArrayList;
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

    public ACOAlgorithm(double[][] distanceMatrix, double antRate, double alpha, double beta, double evaporationRate, double initialPheromone) {
        this.distanceMatrix = distanceMatrix;
        this.numAnts = (int) (distanceMatrix.length * antRate);
        this.alpha = alpha;
        this.beta = beta;
        this.evaporationRate = evaporationRate;
        this.numCities = distanceMatrix.length;
        this.random = new Random();

        this.pheromoneMatrix = new double[numCities][numCities];
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                if (i != j) {
                    pheromoneMatrix[i][j] = initialPheromone;
                }
            }
        }
    }

    public ArrayList<Integer> solve(int maxIterations) {
        ArrayList<Integer> bestTour = null;
        double bestTourLength = Double.POSITIVE_INFINITY;

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            if((double) iteration / maxIterations % 0.1 == 0) System.out.println((double) iteration /maxIterations);
            ArrayList<ArrayList<Integer>> antTours = new ArrayList<>();
            for (int ant = 0; ant < numAnts; ant++) {
                ArrayList<Integer> tour = constructSolution();
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

    private ArrayList<Integer> constructSolution() {
        ArrayList<Integer> tour = new ArrayList<>();
        boolean[] visited = new boolean[numCities];
        int startCity = random.nextInt(numCities);
        tour.add(startCity);
        visited[startCity] = true;

        for (int i = 1; i < numCities; i++) {
            int currentCity = tour.get(i - 1);
            int nextCity = selectNextCity(currentCity, visited);
            tour.add(nextCity);
            visited[nextCity] = true;
        }

        return tour;
    }

    private int selectNextCity(int currentCity, boolean[] visited) {
        double totalProbability = 0;
        double[] probabilities = new double[numCities];

        for (int i = 0; i < numCities; i++) {
            if (!visited[i]) {
                double pheromone = pheromoneMatrix[currentCity][i];
                double distance = distanceMatrix[currentCity][i];
                probabilities[i] = Math.pow(pheromone, alpha) * Math.pow(1.0 / distance, beta);
                totalProbability += probabilities[i];
            }
        }

        double rand = random.nextDouble() * totalProbability;
        double sum = 0;
        for (int i = 0; i < numCities; i++) {
            if (!visited[i]) {
                sum += probabilities[i];
                if (rand <= sum) {
                    return i;
                }
            }
        }
        return -1;
    }

    private double calculateTourLength(ArrayList<Integer> tour) {
        double length = 0;
        for (int i = 0; i < numCities - 1; i++) {
            length += distanceMatrix[tour.get(i)][tour.get(i + 1)];
        }
        length += distanceMatrix[tour.get(numCities - 1)][tour.get(0)]; // Return to start
        return length;
    }

    private void updatePheromones(ArrayList<ArrayList<Integer>> antTours) {
        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                if (i != j) {
                    pheromoneMatrix[i][j] *= (1 - evaporationRate);
                }
            }
        }

        for (ArrayList<Integer> tour : antTours) {
            double tourLength = calculateTourLength(tour);
            double pheromoneToAdd = 1 / tourLength;
            for (int i = 0; i < numCities - 1; i++) {
                int city1 = tour.get(i);
                int city2 = tour.get(i + 1);
                pheromoneMatrix[city1][city2] += pheromoneToAdd;
                pheromoneMatrix[city2][city1] += pheromoneToAdd;
            }
            int lastCity = tour.get(numCities - 1);
            int firstCity = tour.get(0);
            pheromoneMatrix[lastCity][firstCity] += pheromoneToAdd;
            pheromoneMatrix[firstCity][lastCity] += pheromoneToAdd;
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