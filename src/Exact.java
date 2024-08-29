import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Exact {

    double[][] lengths;

    ArrayList<Integer> solution = new ArrayList<>();

    public Exact(double[][] lengths) {
        this.lengths = lengths;
    }

    public ArrayList<Integer> bruteForce(){
        ArrayList<Integer> visited = new ArrayList<>();
        visited.add(0);
        ArrayList<Integer> notVisited = new ArrayList<>();
        for (int i = 0; i < lengths.length; i++) {
            notVisited.add(i);
        }
        SimpleHeuristics sh = new SimpleHeuristics(lengths);
        double[] minVal = {TSPSolver.calcLength(sh.randomInsert(),lengths)};
        bfRec(visited, notVisited, 0, minVal);
        return solution;
    }

    private void bfRec(ArrayList<Integer> visited, ArrayList<Integer> notVisited, double thisLen, double[] currentMin){
        if(thisLen > currentMin[0])return;
        if(notVisited.isEmpty()){
            solution = visited;
            currentMin[0] = thisLen;
            return;
        }
        for (int n : notVisited) {
            if(n == 0 && !(notVisited.size() == 1))continue;
            ArrayList<Integer> newVisited = new ArrayList<>(visited);
            newVisited.add(n);
            ArrayList<Integer> newNotVisited = new ArrayList<>(notVisited);
            newNotVisited.remove((Integer) n);
            bfRec(newVisited, newNotVisited, thisLen + lengths[n][visited.get(visited.size()-1)],currentMin);
        }
    }



    public ArrayList<Integer> heldKarp() {
        int n = lengths.length;
        double[][] dp = new double[n][1 << n];
        int[][] parent = new int[n][1 << n];
        for (double[] row : dp) {
            Arrays.fill(row, Double.POSITIVE_INFINITY);
        }
        dp[0][1] = 0;

        for (int mask = 1; mask < (1 << n); mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) == 0) continue;
                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) == 0 || u == v) continue;
                    double newDistance = dp[u][mask ^ (1 << v)] + lengths[u][v];
                    if (newDistance < dp[v][mask]) {
                        dp[v][mask] = newDistance;
                        parent[v][mask] = u;
                    }
                }
            }
        }

        double minDistance = Double.POSITIVE_INFINITY;
        int minIdx = -1;
        for (int v = 1; v < n; v++) {
            double distance = dp[v][(1 << n) - 1] + lengths[v][0];
            if (distance < minDistance) {
                minDistance = distance;
                minIdx = v;
            }
        }

        int[] tourIndices = new int[n+1];
        int mask = (1 << n) - 1;
        int idx = minIdx;
        for (int i = n - 1; i >= 0; i--) {
            tourIndices[i] = idx;
            idx = parent[idx][mask];
            mask ^= (1 << tourIndices[i]);
        }

        return Arrays.stream(tourIndices).boxed().collect(Collectors.toCollection(ArrayList::new));
    }

}
