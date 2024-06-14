import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GeneticAlgorithm {

    int populationSize;
    int generations;
    double mutationRate;
    double[][] lengths;

    ArrayList<Integer> bestRoute;
    double bestDist = Double.MAX_VALUE;

    ArrayList<Integer>[] routes;
    double[] distances;
    double[] fitness;
    Random rand = new Random();
    TSPSolver tsp;

    public GeneticAlgorithm(double[][] lengths, int populationSize, int generations, double mutationRate) {
        this.lengths = lengths;
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
        this.routes = new ArrayList[populationSize];
        this.distances = new double[populationSize];
        this.fitness = new double[populationSize];
        this.tsp = new TSPSolver(lengths);
    }

    public ArrayList<Integer> solve() {
        for (int i = 0; i < populationSize; i++) {
            routes[i] = tsp.randomRoute();
        }

        for (int i = 0; i < generations; i++) {
            checkBestDist();
            calcFitness();
            ArrayList<Integer>[] newRoutes = new ArrayList[populationSize];
            newRoutes[0] = bestRoute;
            for (int j = 1; j < populationSize; j++) {
                ArrayList<Integer> parent1 = selectParent();
                ArrayList<Integer> parent2 = selectParent();
                newRoutes[j] = crossover(parent1, parent2);
            }
            routes = newRoutes;
            mutate();
        }

        checkBestDist();
        return bestRoute;
    }

    private void checkBestDist() {
        for (int i = 0; i < populationSize; i++) {
            ArrayList<Integer> route = routes[i];
            double len = tsp.calcLength(route);
            distances[i] = len;
            if (len < bestDist) {
                bestDist = len;
                bestRoute = new ArrayList<>(route);
            }
        }
    }

    private void calcFitness() {
        double fitnessSum = 0;
        for (int i = 0; i < populationSize; i++) {
            fitness[i] = 1 / distances[i];
            fitnessSum += fitness[i];
        }

        for (int i = 0; i < populationSize; i++) {
            fitness[i] /= fitnessSum;
        }
    }

    private ArrayList<Integer> selectParent() {
        double rand = Math.random();
        for (int i = 0; i < populationSize; i++) {
            rand -= fitness[i];
            if (rand <= 0) {
                return routes[i];
            }
        }
        return routes[populationSize - 1];
    }

    private ArrayList<Integer> crossover(ArrayList<Integer> parent1, ArrayList<Integer> parent2) {
        int size = parent1.size();
        ArrayList<Integer> child = new ArrayList<>(Collections.nCopies(size, -1));

        // Fixiere das erste und das letzte Element
        child.set(0, parent1.get(0));
        child.set(size - 1, parent1.get(size - 1));

        // Wähle zufällige Start- und Endpositionen für den Crossover
        int startPos = rand.nextInt(size - 2) + 1;  // Von 1 bis size-2
        int endPos = rand.nextInt(size - 2) + 1;

        if (startPos > endPos) {
            int temp = startPos;
            startPos = endPos;
            endPos = temp;
        }

        // Kopiere das Segment von parent1 zu child
        for (int i = startPos; i <= endPos; i++) {
            child.set(i, parent1.get(i));
        }

        // Fülle die restlichen Plätze von parent2 auf
        int childIndex = (endPos + 1) % (size - 1);
        for (int i = 1; i < size - 1; i++) {  // Beginne ab 1, um das erste und letzte Element zu ignorieren
            int parent2Index = (endPos + 1 + i) % (size - 1);
            int candidate = parent2.get(parent2Index);
            if (!child.contains(candidate)) {
                while (child.get(childIndex) != -1) {
                    childIndex = (childIndex + 1) % (size - 1);
                    if (childIndex == 0) childIndex++;
                }
                child.set(childIndex, candidate);
            }
        }

        return child;
    }

    private void mutate() {
        for (int i = 0; i < populationSize; i++) {
            if (Math.random() < mutationRate) {
                int rand1 = rand.nextInt(lengths.length - 1) + 1;
                int rand2 = rand.nextInt(lengths.length - 1) + 1;
                Collections.swap(routes[i], rand1, rand2);
            }
        }
    }


}