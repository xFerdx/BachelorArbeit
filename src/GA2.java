import java.util.*;
import java.util.stream.Collectors;

public class GA2{
    private double[][] distances;
    private int cityCount;
    private int populationSize;
    private int generations;
    private double mutationRate;
    private Random random;

    public GA2(double[][] distances, int populationSize, int generations, double mutationRate) {
        this.distances = distances;
        this.cityCount = distances.length;
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
        this.random = new Random();
    }

    public ArrayList<Integer> solve() {
        List<int[]> population = initializePopulation();

        for (int gen = 0; gen < generations; gen++) {
            population = evolvePopulation(population);
        }

        return Arrays.stream(getBestTour(population))
                .boxed()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<int[]> initializePopulation() {
        List<int[]> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            population.add(generateRandomTour());
        }
        return population;
    }

    private int[] generateRandomTour() {
        List<Integer> tour = new ArrayList<>();
        for (int i = 0; i < cityCount; i++) {
            tour.add(i);
        }
        Collections.shuffle(tour);
        return tour.stream().mapToInt(Integer::intValue).toArray();
    }

    private List<int[]> evolvePopulation(List<int[]> population) {
        List<int[]> newPopulation = new ArrayList<>();

        for (int i = 0; i < populationSize; i++) {
            int[] parent1 = selectParent(population);
            int[] parent2 = selectParent(population);
            int[] child = crossover(parent1, parent2);
            mutate(child);
            newPopulation.add(child);
        }

        return newPopulation;
    }

    private int[] selectParent(List<int[]> population) {
        int tournamentSize = 5;
        int[] best = null;
        double bestFitness = Double.POSITIVE_INFINITY;

        for (int i = 0; i < tournamentSize; i++) {
            int[] candidate = population.get(random.nextInt(populationSize));
            double fitness = calculateFitness(candidate);
            if (fitness < bestFitness) {
                best = candidate;
                bestFitness = fitness;
            }
        }

        return best;
    }

    private int[] crossover(int[] parent1, int[] parent2) {
        int[] child = new int[cityCount];
        Arrays.fill(child, -1);

        int start = random.nextInt(cityCount);
        int end = random.nextInt(cityCount);

        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }

        for (int i = start; i <= end; i++) {
            child[i] = parent1[i];
        }

        for (int i = 0; i < cityCount; i++) {
            if (!contains(child, parent2[i])) {
                for (int j = 0; j < cityCount; j++) {
                    if (child[j] == -1) {
                        child[j] = parent2[i];
                        break;
                    }
                }
            }
        }

        return child;
    }

    private boolean contains(int[] array, int value) {
        for (int i : array) {
            if (i == value) return true;
        }
        return false;
    }

    private void mutate(int[] tour) {
        for (int i = 0; i < cityCount; i++) {
            if (random.nextDouble() < mutationRate) {
                int j = random.nextInt(cityCount);
                int temp = tour[i];
                tour[i] = tour[j];
                tour[j] = temp;
            }
        }
    }

    private double calculateFitness(int[] tour) {
        double distance = 0;
        for (int i = 0; i < cityCount - 1; i++) {
            distance += distances[tour[i]][tour[i + 1]];
        }
        distance += distances[tour[cityCount - 1]][tour[0]];
        return distance;
    }

    private int[] getBestTour(List<int[]> population) {
        int[] best = null;
        double bestFitness = Double.POSITIVE_INFINITY;

        for (int[] tour : population) {
            double fitness = calculateFitness(tour);
            if (fitness < bestFitness) {
                best = tour;
                bestFitness = fitness;
            }
        }

        return best;
    }
}