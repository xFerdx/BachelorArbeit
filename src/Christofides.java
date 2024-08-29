import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

public class Christofides {

    double[][] lengths;

    public Christofides(double[][] lengths) {
        this.lengths = lengths;
    }




    public ArrayList<Integer> getOddPoints(ArrayList<Integer>[] graph){
        ArrayList<Integer> ret = new ArrayList<>();
        if(graph[0].size() % 2 == 1)ret.add(0);
        for (int i = 1; i < graph.length; i++) {
            if(graph[i].size() % 2 == 1)ret.add(i);
        }

        return ret;
    }


    public ArrayList<int[]> getMinWeightPerfectMatching(ArrayList<Integer> vertices) {
        ArrayList<int[]> minMatching = new ArrayList<>();
        if(vertices.size() % 2 == 1)throw new RuntimeException("not even number");
        while(!vertices.isEmpty()) {
            double minWeight = Double.MAX_VALUE;
            int[] bestPair = new int[2];
            for (int i = 0; i < vertices.size(); i++) {
                for (int j = i + 1; j < vertices.size(); j++) {
                    double weight = lengths[vertices.get(i)][vertices.get(j)];
                    if (weight < minWeight) {
                        minWeight = weight;
                        bestPair[0] = vertices.get(i);
                        bestPair[1] = vertices.get(j);
                    }
                }
            }
            minMatching.add(bestPair);
            vertices.remove((Integer) bestPair[0]);
            vertices.remove((Integer) bestPair[1]);
        }
        return minMatching;
    }

    public static ArrayList<Integer> eulerCircuit(List<Integer>[] graph) {
        ArrayList<Integer> eulerTour = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();

        stack.push(0);
        while (!stack.isEmpty()) {
            int v = stack.peek();
            if (graph[v].isEmpty()) {
                eulerTour.add(v);
                stack.pop();
            } else {
                int u = graph[v].iterator().next();
                stack.push(u);
                graph[v].remove((Integer)u);
                graph[u].remove((Integer)v);
            }
        }

        eulerTour.add(eulerTour.get(0));
        return eulerTour;
    }

    private void hamiltonCircuit(ArrayList<Integer> eulerCir){
        HashSet<Integer> containing = new HashSet<>();
        for (int i = 0; i < eulerCir.size(); i++) {
            if(!containing.add(eulerCir.get(i))){
                eulerCir.remove(i);
                i--;
            }
        }
        eulerCir.add(eulerCir.get(0));
    }

    public ArrayList<Integer> solve() {
        TSPSolver tsp = new TSPSolver(lengths);
        ArrayList<Integer>[] graph = tsp.MST(null,true);
        ArrayList<Integer> oddPoints = getOddPoints(graph);
        ArrayList<int[]> s = getMinWeightPerfectMatching(oddPoints);
        for (int[] e : s) {
            graph[e[0]].add(e[1]);
            graph[e[1]].add(e[0]);
        }
        ArrayList<Integer> ec = eulerCircuit(graph);
        hamiltonCircuit(ec);
        return ec;
    }


}
