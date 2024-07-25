
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.*;
import com.google.protobuf.Duration;

import java.util.ArrayList;

public class OrTools {

    public double getLB(double[][] distanceMatrix){
        Loader.loadNativeLibraries();
        return 0;
    }

    public static ArrayList<Integer> solve(double[][] distanceMatrix, int vehicleNumber, int depot) {
        Loader.loadNativeLibraries();
        long[][] newMat = new long[distanceMatrix.length][distanceMatrix[0].length];
        for (int i = 0; i < newMat.length; i++) {
            for (int j = 0; j < newMat[0].length; j++) {
                newMat[i][j] = (long) (distanceMatrix[i][j] * 100000);
            }
        }
        RoutingIndexManager manager = new RoutingIndexManager(newMat.length, vehicleNumber, depot);
        RoutingModel routing = new RoutingModel(manager);
        final int transitCallbackIndex =
                routing.registerTransitCallback((long fromIndex, long toIndex) -> {
                    int fromNode = manager.indexToNode(fromIndex);
                    int toNode = manager.indexToNode(toIndex);
                    return newMat[fromNode][toNode];
                });
        routing.setArcCostEvaluatorOfAllVehicles(transitCallbackIndex);
//        RoutingSearchParameters searchParameters =
//                main.defaultRoutingSearchParameters()
//                        .toBuilder()
//                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
//                        .build();

        RoutingSearchParameters searchParameters = main.defaultRoutingSearchParameters().toBuilder()
                .setFirstSolutionStrategy(FirstSolutionStrategy.Value.CHRISTOFIDES)  // Let the solver choose the best initial solution strategy
                .setLocalSearchMetaheuristic(LocalSearchMetaheuristic.Value.GUIDED_LOCAL_SEARCH)  // Use Guided Local Search to escape local optima
                .setTimeLimit(Duration.newBuilder().setSeconds(5).build())  // Set a time limit for the search
                .setSavingsNeighborsRatio(1.0)  // Increase the number of neighbors considered in the Savings heuristic
                .setSolutionLimit(1000)  // Limit the number of solutions to consider
                .build();


        Assignment solution = routing.solveWithParameters(searchParameters);
        return getSolution(routing, manager, solution);
    }

    private static ArrayList<Integer> getSolution(RoutingModel routing, RoutingIndexManager manager, Assignment solution) {
        ArrayList<Integer> route = new ArrayList<>();
        long index = routing.start(0);
        while (!routing.isEnd(index)) {
            route.add(manager.indexToNode(index));
            index = solution.value(routing.nextVar(index));
        }
        route.add(manager.indexToNode(routing.end(0)));
        return route;
    }
}
