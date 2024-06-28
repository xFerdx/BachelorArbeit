import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class LinKernighan {
    private final int size;
    public int[] tour;
    private final double[][] distanceTable;

    public LinKernighan(double[][] len) {
        this.distanceTable = len;
        this.size = len.length;
        this.tour = createRandomTour();
    }


    public ArrayList<Integer> runAlgorithm() {
        double oldDistance;
        double newDistance = getDistance();

        do {
            oldDistance = newDistance;
            improve();
            newDistance = getDistance();
        } while(newDistance < oldDistance);

        ArrayList<Integer> ret =  Arrays.stream(tour).boxed().collect(Collectors.toCollection(ArrayList::new));

        while (ret.get(0) != 0){
            ret.add(ret.remove(0));
        }
        ret.add(0);
        return ret;
    }


    private int[] createRandomTour() {
        int[] array = new int[size];
        for(int i = 0; i < size; i++) {
            array[i] = i;
        }
        Random random = new Random();
        for (int i = 0; i < size; ++i) {
            int index = random.nextInt(i + 1);
            int a = array[index];
            array[index] = array[i];
            array[i] = a;
        }

        return array;
    }


    public double getDistance() {
        double sum = 0;

        for (int i = 0; i < this.size; i++) {
            int a = tour[i];
            int b = tour[(i + 1) % this.size];
            sum += this.distanceTable[a][b];
        }

        return sum;
    }

    public void improve() {
        for(int i = 0; i < size; ++i) {
            improve(i);
        }
    }


    public void improve(int x){
        improve(x, false);
    }


    public void improve(int t1, boolean previous) {
        int t2 = previous? getPreviousIdx(t1): getNextIdx(t1);
        int t3 = getNearestNeighbor(t2);

        if(t3 != -1 && getDistance(t2, t3) < getDistance(t1, t2)) {
            startAlgorithm(t1,t2,t3);
        } else if(!previous) {
            improve(t1, true);
        }
    }


    public int getPreviousIdx(int index) {
        return index == 0? size-1: index-1;
    }


    public int getNextIdx(int index) {
        return (index+1)%size;
    }

    public int getNearestNeighbor(int index) {
        double minDistance = Double.MAX_VALUE;
        int nearestNode = -1;
        int actualNode = tour[index];
        for(int i = 0; i < size; ++i) {
            if(i != actualNode) {
                double distance = this.distanceTable[i][actualNode];
                if(distance < minDistance) {
                    nearestNode = getIndex(i);
                    minDistance = distance;
                }
            }
        }
        return nearestNode;
    }


    public double getDistance(int n1, int n2) {
        return distanceTable[tour[n1]][tour[n2]];
    }

    public void startAlgorithm(int t1, int t2, int t3) {
        ArrayList<Integer> tIndex = new ArrayList<>();
        tIndex.add(0, -1);
        tIndex.add(1, t1);
        tIndex.add(2, t2);
        tIndex.add(3, t3);
        double initialGain = getDistance(t2, t1) - getDistance(t3, t2);
        double GStar = 0;
        double Gi = initialGain;
        int k = 3;
        for(int i = 4;; i+=2) {
            int newT = selectNewT(tIndex);
            if(newT == -1) {
                break;
            }
            tIndex.add(i, newT);
            int tiplus1 = getNextPossibleY(tIndex);
            if(tiplus1 == -1) {
                break;
            }

            Gi += getDistance(tIndex.get(tIndex.size()-2), newT);
            if(Gi - getDistance(newT, t1) > GStar) {
                GStar = Gi - getDistance(newT, t1);
                k = i;
            }

            tIndex.add(tiplus1);
            Gi -= getDistance(newT, tiplus1);


        }
        if(GStar > 0) {
            tIndex.set(k+1, tIndex.get(1));
            tour = getTPrime(tIndex, k);
        }

    }

    public int getNextPossibleY(ArrayList<Integer> tIndex) {
        int ti = tIndex.get(tIndex.size() - 1);
        ArrayList<Integer> ys = new ArrayList<>();
        for(int i = 0; i < size; ++i) {
            if(!isDisjunctive(tIndex, i, ti)) {
                continue;
            }

            if(!isPositiveGain(tIndex, i)) {
                continue;
            };
            if(!nextXPossible(tIndex, i)) {
                continue;
            }
            ys.add(i);
        }

        double minDistance = Double.MAX_VALUE;
        int minNode = -1;
        for(int i: ys) {
            if(getDistance(ti, i) < minDistance) {
                minNode = i;
                minDistance = getDistance(ti, i);
            };
        }

        return minNode;

    }

    private boolean nextXPossible(ArrayList<Integer> tIndex, int i) {
        return isConnected(tIndex, i, getNextIdx(i)) || isConnected(tIndex, i, getPreviousIdx(i));
    }

    private boolean isConnected(ArrayList<Integer> tIndex, int x, int y) {
        if(x == y) return false;
        for(int i = 1; i < tIndex.size() -1 ; i+=2) {
            if(tIndex.get(i) == x && tIndex.get(i + 1) == y) return false;
            if(tIndex.get(i) == y && tIndex.get(i + 1) == x) return false;
        }
        return true;
    }


    private boolean isPositiveGain(ArrayList<Integer> tIndex, int ti) {
        double gain = 0;
        for(int i = 1; i < tIndex.size() - 2; ++i) {
            int t1 = tIndex.get(i);
            int t2 = tIndex.get(i+1);
            int t3 = i == tIndex.size()-3? ti :tIndex.get(i+2);

            gain += getDistance(t2, t3) - getDistance(t1,t2);

        }
        return gain > 0;
    }


    public int selectNewT(ArrayList<Integer> tIndex) {
        int option1 = getPreviousIdx(tIndex.get(tIndex.size()-1));
        int option2 = getNextIdx(tIndex.get(tIndex.size()-1));

        int[] tour1 = constructNewTour(tour, tIndex, option1);

        if(isTour(tour1)) {
            return option1;
        } else {
            int[] tour2 = constructNewTour(tour, tIndex, option2);
            if(isTour(tour2)) {
                return option2;
            }
        }
        return -1;
    }

    private int[] constructNewTour(int[] tour2, ArrayList<Integer> tIndex, int newItem) {
        ArrayList<Integer> changes = new ArrayList<>(tIndex);

        changes.add(newItem);
        changes.add(changes.get(1));
        return constructNewTour(tour2, changes);
    }

    public boolean isTour(int[] tour) {
        if(tour.length != size) {
            return false;
        }

        for(int i = 0; i < size-1; ++i) {
            for(int j = i+1; j < size; ++j) {
                if(tour[i] == tour[j]) {
                    return false;
                }
            }
        }

        return true;
    }

    private int[] getTPrime(ArrayList<Integer> tIndex, int k) {
        ArrayList<Integer> al2 = new ArrayList<>(tIndex.subList(0, k + 2 ));
        return constructNewTour(tour, al2);
    }

    public int[] constructNewTour(int[] tour, ArrayList<Integer> changes) {
        ArrayList<Edge> currentEdges = deriveEdgesFromTour(tour);

        ArrayList<Edge> X = deriveX(changes);
        ArrayList<Edge> Y = deriveY(changes);
        int s = currentEdges.size();

        for(Edge e: X) {
            for(int j = 0; j < currentEdges.size(); ++j) {
                Edge m = currentEdges.get(j);
                if(e.equals(m)) {
                    s--;
                    currentEdges.set(j, null);
                    break;
                }
            }
        }

        for(Edge e: Y) {
            s++;
            currentEdges.add(e);
        }
        return createTourFromEdges(currentEdges, s);
    }

    private int[] createTourFromEdges(ArrayList<Edge> currentEdges, int s) {
        int[] tour = new int[s];
        int i = 0;
        int last = -1;

        for(; i < currentEdges.size(); ++i) {
            if(currentEdges.get(i) != null) {
                tour[0] = currentEdges.get(i).get1();
                tour[1] = currentEdges.get(i).get2();
                last = tour[1];
                break;
            }
        }

        currentEdges.set(i, null);

        int k=2;
        while(true) {
            int j = 0;
            for(; j < currentEdges.size(); ++j) {
                Edge e = currentEdges.get(j);
                if(e != null && e.get1() == last) {
                    last = e.get2();
                    break;
                } else if(e != null && e.get2() == last) {
                    last = e.get1();
                    break;
                }
            }
            if(j == currentEdges.size()) break;
            currentEdges.set(j, null);
            if(k >= s) break;
            tour[k] = last;
            k++;
        }

        return tour;
    }

    public ArrayList<Edge> deriveX(ArrayList<Integer> changes) {
        ArrayList<Edge> es = new ArrayList<>();
        for(int i = 1; i < changes.size() - 2; i+=2) {
            Edge e = new Edge(tour[changes.get(i)], tour[changes.get(i+1)]);
            es.add(e);
        }
        return es;
    }

    ArrayList<Edge> deriveY(ArrayList<Integer> changes) {
        ArrayList<Edge> es = new ArrayList<>();
        for(int i = 2; i < changes.size() - 1; i+=2) {
            Edge e = new Edge(tour[changes.get(i)], tour[changes.get(i+1)]);
            es.add(e);
        }
        return es;
    }

    public ArrayList<Edge> deriveEdgesFromTour(int[] tour) {
        ArrayList<Edge> es = new ArrayList<>();
        for(int i = 0; i < tour.length ; ++i) {
            Edge e = new Edge(tour[i], tour[(i+1)%tour.length]);
            es.add(e);
        }

        return es;
    }

    private boolean isDisjunctive(ArrayList<Integer> tIndex, int x, int y) {
        if(x == y) return false;
        for(int i = 0; i < tIndex.size() -1 ; i++) {
            if(tIndex.get(i) == x && tIndex.get(i + 1) == y) return false;
            if(tIndex.get(i) == y && tIndex.get(i + 1) == x) return false;
        }
        return true;
    }

    private int getIndex(int node) {
        int i = 0;
        for(int t: tour) {
            if(node == t) {
                return i;
            }
            i++;
        }
        return -1;
    }
}


class Edge implements Comparable<Edge> {
    private final int endPoint1;
    private final int endPoint2;

    public Edge(int a, int b) {
        this.endPoint1 = Math.max(a, b);
        this.endPoint2 = Math.min(a, b);
    }

    public int get1() {
        return this.endPoint1;
    }

    public int get2() {
        return this.endPoint2;
    }

    public int compareTo(Edge e2) {
        if(this.get1() < e2.get1() || this.get1() == e2.get1() && this.get2() < e2.get2()) {
            return -1;
        } else if (this.equals(e2)) {
            return 0;
        } else {
            return 1;
        }

    }

    public boolean equals(Edge e2) {
        if (e2 == null) return false;
        return (this.get1() == e2.get1()) && (this.get2() == e2.get2());
    }
}
