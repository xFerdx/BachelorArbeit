import java.util.ArrayList;

public class SimpleHeuristics {

    double[][] lengths;

    public SimpleHeuristics(double[][] lengths) {
        this.lengths = lengths;
    }

    public ArrayList<Integer> nn(){
        int n = lengths.length;
        ArrayList<Integer> ret = new ArrayList<>(n+1);
        ret.add(0);
        boolean[] used = new boolean[n];
        used[0] = true;
        while(ret.size()<n){
            int lastIdx = ret.get(ret.size()-1);
            int minIdx = -1;
            for (int i = 0; i < lengths.length; i++) {
                if(used[i])continue;
                if(minIdx == -1 || lengths[lastIdx][i]<lengths[lastIdx][minIdx]){
                    minIdx = i;
                }
            }
            ret.add(minIdx);
            used[minIdx] = true;
        }

        ret.add(0);
        return ret;
    }

    public ArrayList<Integer> randomInsert(){
        int l = lengths.length;
        ArrayList<Integer> ret = new ArrayList<>(l+1);
        ret.add(0);
        ret.add(0);
        for (int i = 1; i < l; i++) {
            double minVal = Float.MAX_VALUE;
            int minIdx = -1;
            for (int j = 1; j <= i; j++) {
                double val = lengths[ret.get(j-1)][i]+lengths[ret.get(j)][i]-lengths[ret.get(j-1)][ret.get(j)];
                if(val<minVal){
                    minVal = val;
                    minIdx = j;
                }
            }
            ret.add(minIdx,i);
        }

        return ret;
    }

    public ArrayList<Integer> cheapestInsertion() {
        int n = lengths.length;
        ArrayList<Integer> tour = new ArrayList<>();
        boolean[] visited = new boolean[n];

        int currentCity = 0;
        tour.add(currentCity);
        visited[currentCity] = true;

        while (tour.size() < n) {
            double minIncrease = Double.MAX_VALUE;
            int nextCity = -1;
            int insertPosition = -1;

            for (int city = 0; city < n; city++) {
                if (visited[city]) continue;
                for (int i = 0; i < tour.size(); i++) {
                    int current = tour.get(i);
                    int next = (i + 1) % tour.size();
                    double increase = lengths[current][city] + lengths[city][tour.get(next)] - lengths[current][tour.get(next)];
                    if (increase < minIncrease) {
                        minIncrease = increase;
                        nextCity = city;
                        insertPosition = next;
                    }
                }
            }
            tour.add(insertPosition, nextCity);
            visited[nextCity] = true;
        }
        tour.add(0,0);
        return tour;
    }

    public ArrayList<Integer> farthestInsertTSP() {
        int n = lengths.length;
        ArrayList<Integer> tour = new ArrayList<>();
        boolean[] visited = new boolean[n];

        tour.add(0);
        visited[0] = true;

        while (tour.size() < n) {
            int farthestCity = -1;
            int insertPosition = -1;
            double maxDistance = -1.0;

            for (int i = 0; i < n; i++) {
                if (visited[i]) continue;
                double minDistance = Double.POSITIVE_INFINITY;
                for (int j = 0; j < tour.size(); j++) {
                    int city1 = tour.get(j);
                    double dist = lengths[city1][i];
                    if (dist < minDistance) {
                        minDistance = dist;
                    }
                }

                if (minDistance > maxDistance) {
                    maxDistance = minDistance;
                    farthestCity = i;
                }
            }

            double minIncrease = Double.POSITIVE_INFINITY;
            for (int j = 1; j < tour.size(); j++) {
                int city1 = tour.get(j - 1);
                int city2 = tour.get(j);
                double increase = lengths[city1][farthestCity] + lengths[farthestCity][city2] - lengths[city1][city2];
                if (increase < minIncrease) {
                    minIncrease = increase;
                    insertPosition = j;
                }
            }

            if (insertPosition == -1) {
                tour.add(farthestCity);
            } else {
                tour.add(insertPosition, farthestCity);
            }

            visited[farthestCity] = true;
        }

        tour.add(0);
        return tour;
    }
}
