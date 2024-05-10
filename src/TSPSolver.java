import java.util.*;
import java.util.stream.Collectors;

public class TSPSolver {

    private final float[][] lengths;
    ArrayList<Integer> solution = new ArrayList<>();

    public TSPSolver(float[][] lengths) {
        this.lengths = lengths;
    }

    public ArrayList<Integer> bruteForce(){
        ArrayList<Integer> visited = new ArrayList<>();
        visited.add(0);
        float[] minVal = {Integer.MAX_VALUE};
        bfRec(visited, minVal);
        return solution;
    }

    private void bfRec(ArrayList<Integer> visited, float[] currentMin){
        if(visited.size() == lengths.length){
            visited.add(0);
            if(calcLength(visited)<currentMin[0]){
                currentMin[0] = calcLength(visited);
                solution = visited;
                return;
            }
        }
        for (int i = 0; i < lengths.length; i++) {
            if(!visited.contains(i)){
                ArrayList<Integer> newVisited = new ArrayList<>(visited);
                newVisited.add(i);
                bfRec(newVisited, currentMin);
            }
        }
    }



    public ArrayList<Integer> tspHeldKarp() {
        int n = lengths.length;
        double[][] dp = new double[n][1 << n]; // Dynamic programming table
        int[][] parent = new int[n][1 << n];   // To store parent cities
        for (double[] row : dp) {
            Arrays.fill(row, Double.POSITIVE_INFINITY);
        }
        dp[0][1] = 0; // Base case: Starting city with no other cities visited

        for (int mask = 1; mask < (1 << n); mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) != 0) { // Check if city u is included in the subset represented by mask
                    for (int v = 0; v < n; v++) {
                        if ((mask & (1 << v)) != 0 && u != v) { // Check if city v is also included and is not the same as u
                            double newDistance = dp[u][mask ^ (1 << v)] + lengths[u][v];
                            if (newDistance < dp[v][mask]) {
                                dp[v][mask] = newDistance;
                                parent[v][mask] = u;
                            }
                        }
                    }
                }
            }
        }

        double minDistance = Double.POSITIVE_INFINITY;
        int minIdx = -1;
        for (int v = 1; v < n; v++) {
            double distance = dp[v][(1 << n) - 1] + lengths[v][0]; // Complete tour
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

    public ArrayList<Integer> nn(){
        ArrayList<Integer> ret = new ArrayList<>(lengths.length);
        ret.add(0);
        while(ret.size()<lengths.length){
            int lastIdx = ret.get(ret.size()-1);
            int minIdx = -1;
            for (int i = 0; i < lengths.length; i++) {
                if(ret.contains(i))continue;
                if(minIdx == -1 || lengths[lastIdx][i]<lengths[lastIdx][minIdx]){
                    minIdx = i;
                }
            }
            ret.add(minIdx);
        }

        ret.add(0);
        return ret;
    }

    public ArrayList<Integer> nn2(){
        int l = lengths.length;
        ArrayList<Integer> ret = new ArrayList<>(l+1);
        ret.add(0);
        ret.add(0);
        for (int i = 1; i < l; i++) {
            float minVal = Float.MAX_VALUE;
            int minIdx = -1;
            for (int j = 1; j <= i; j++) {
                ret.add(j,i);
                float val = calcLength(ret);
                ret.remove(j);
                if(val<minVal){
                    minVal = val;
                    minIdx = j;
                }
            }
            ret.add(minIdx,i);
        }

        return ret;
    }

    public ArrayList<Integer> ant(){
        int l = lengths.length;
        float[][] weights = new float[l][l];

        for (int i = 0; i < l; i++) {
            for (int j = 0; j < l; j++) {
                weights[i][j] = i==j?0:1;
            }
        }


        ArrayList<Integer> currentBestRoute = randomRoute();
        float currentBestLen = calcLength(currentBestRoute);
        for (double i = 1; i > 0; i -= 0.0001) {
            ArrayList<Integer> currentRoute = new ArrayList<>(l+1);
            currentRoute.add(0);
            for (int j = 0; j < l-1; j++) {
                ArrayList<Integer> candidates = new ArrayList<>(l);
                for (int k = 0; k < l; k++) {
                    if(!currentRoute.contains(k))candidates.add(k);
                }
                float rowSum = 0;
                for (int c: candidates) {
                    rowSum += (float) (weights[currentRoute.get(currentRoute.size()-1)][c] / (lengths[currentRoute.get(currentRoute.size()-1)][c]*1.5));
                }
                double rand = Math.random()*rowSum;
                int newPoint = -1;
                for (int c : candidates) {
                    rand -= weights[currentRoute.get(currentRoute.size() - 1)][c] / (lengths[currentRoute.get(currentRoute.size()-1)][c]*1.5);
                    if (rand <= 0.0f) {
                        newPoint = c;
                        break;
                    }
                }
                currentRoute.add(newPoint);
            }

            currentRoute.add(0);

            float thisLen = calcLength(currentRoute);
            for (int j = 0; j < l-1; j++) {
                weights[currentRoute.get(j)][currentRoute.get(j+1)] += 1/thisLen*100;
            }

            if(thisLen<currentBestLen){
                currentBestLen = thisLen;
                currentBestRoute = new ArrayList<>(currentRoute);
            }


        }

        return currentBestRoute;
    }


    public ArrayList<Integer> randomRoute(){
        int l = lengths.length;
        ArrayList<Integer> route = new ArrayList<>(l+1);
        route.add(0);
        for (int i = 0; i < l-1; i++) {
            ArrayList<Integer> candidates = new ArrayList<>(l);
            for (int j = 0; j < l; j++) {
               if(!route.contains(j))candidates.add(j);
            }
            route.add(candidates.get((int) (Math.random()*candidates.size())));
        }
        route.add(0);
        return route;
    }



    public float calcLength(ArrayList<Integer> l){
        float ret = 0;
        for (int i = 0; i < l.size()-1; i++) {
            ret += lengths[l.get(i)][l.get(i+1)];
        }
        return ret;
    }


    
    public ArrayList<Integer> opt(ArrayList<Integer> list, int z){
        ArrayDeque<ArrayList<Integer>> permQ = new ArrayDeque<>((int) Math.pow(list.size(),z));
        int l = list.size();

        for (int i = 1; i < l-1; i++) {
            ArrayList<Integer> a = new ArrayList<>();
            a.add(i);
            permQ.add(a);
        }

        while(permQ.peek() != null && permQ.peek().size() < z){
            ArrayList<Integer> a = permQ.pop();
            for (int i = 1; i < l-1; i++) {
                ArrayList<Integer> b = new ArrayList<>(a);
                b.add(i);
                permQ.add(b);
            }
        }

        ArrayList<Integer> minRoute = new ArrayList<>(list);
        float minLen = calcLength(minRoute);

        while(!permQ.isEmpty()){
            ArrayList<Integer> a = permQ.pop();
            ArrayList<Integer> thisRoute = new ArrayList<>(list);

            int temp = thisRoute.get(a.get(0));
            for (int i = 0; i < a.size()-1; i++) {
                thisRoute.set(a.get(i),thisRoute.get(a.get(i+1)));
            }
            thisRoute.set(a.get(a.size()-1),temp);

            float thisLen = calcLength(thisRoute);

            if(thisLen<minLen){
                minRoute = thisRoute;
                minLen = thisLen;
            }



        }

        return minRoute;
    }

    public int fak(int n){
        return n==1 ? 1:n*fak(n-1);
    }

    public void changePos(ArrayList<Integer> list, int a, int b){
        int temp = list.get(b);
        list.set(b,list.get(a));
        list.set(a,temp);
    }








































}
