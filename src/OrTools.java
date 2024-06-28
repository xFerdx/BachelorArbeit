
import com.google.ortools.Loader;
import com.google.ortools.constraintsolver.Assignment;
import com.google.ortools.constraintsolver.FirstSolutionStrategy;
import com.google.ortools.constraintsolver.RoutingIndexManager;
import com.google.ortools.constraintsolver.RoutingModel;
import com.google.ortools.constraintsolver.RoutingSearchParameters;
import com.google.ortools.constraintsolver.main;

import java.util.ArrayList;
import java.util.logging.Logger;

public class OrTools {

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
        RoutingSearchParameters searchParameters =
                main.defaultRoutingSearchParameters()
                        .toBuilder()
                        .setFirstSolutionStrategy(FirstSolutionStrategy.Value.PATH_CHEAPEST_ARC)
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
