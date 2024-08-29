import java.util.ArrayList;
import java.util.Collections;

public class Opt {

    double[][] lengths;

    public Opt(double[][] lengths) {
        this.lengths = lengths;
    }

    private void reverseSegment(ArrayList<Integer> tour, int i, int j) {
        while (i < j) {
            Collections.swap(tour, i, j);
            i++;
            j--;
        }
    }

    public void Opt2(ArrayList<Integer> tour) {
        boolean improved;
        do {
            improved = false;
            for (int i = 1; i < tour.size() - 2; i++) {
                for (int j = i + 1; j < tour.size() - 1; j++) {
                    if (lengths[tour.get(i-1)][tour.get(j)] + lengths[tour.get(i)][tour.get(j+1)] < lengths[tour.get(i-1)][tour.get(i)] + lengths[tour.get(j)][tour.get(j+1)]) {
                        reverseSegment(tour,i,j);
                        improved = true;
                        break;
                    }
                }
                if (improved) {
                    break;
                }
            }
        } while (improved);
    }


    public void Opt3(ArrayList<Integer> tour) {
        boolean improved;
        int n = tour.size();
        do {
            improved = false;
            for (int i = 0; i < n - 3; i++) {
                for (int j = i + 1; j < n - 2; j++) {
                    for (int k = j + 1; k < n - 1; k++) {
                        improved = try3OptSwap(tour, i, j, k) || improved;
                    }
                }
            }
        } while (improved);
    }

    private boolean try3OptSwap(ArrayList<Integer> tour, int i, int j, int k) {
        boolean ret = false;
        double originalDistance = TSPSolver.calcLength(tour,lengths);

        ArrayList<ArrayList<Integer>> newTours = new ArrayList<>();
        for (int l = 1; l < 8; l++) {
            newTours.add(getNewTour(tour, i, j, k, l));
        }

        for (ArrayList<Integer> newTour : newTours) {
            double newDistance = TSPSolver.calcLength(newTour,lengths);
            if (newDistance < originalDistance) {
                for (int idx = 0; idx < tour.size(); idx++) {
                    tour.set(idx, newTour.get(idx));
                }
                originalDistance = newDistance;
                ret =  true;
            }
        }

        return ret;
    }

    private ArrayList<Integer> getNewTour(ArrayList<Integer> tour, int i, int j, int k, int option) {
        ArrayList<Integer> newTour = new ArrayList<>(tour);
        switch (option) {
            case 1: reverseSegment(newTour, i + 1, j); break;
            case 2: reverseSegment(newTour, j + 1, k); break;
            case 3: reverseSegment(newTour, i + 1, k); break;
            case 4: reverseSegment(newTour, i + 1, j); reverseSegment(newTour, j + 1, k); break;
            case 5: reverseSegment(newTour, i + 1, k); reverseSegment(newTour, j + 1, k); break;
            case 6: reverseSegment(newTour, i + 1, j); reverseSegment(newTour, i + 1, k); break;
            case 7: reverseSegment(newTour, i + 1, j); reverseSegment(newTour, j + 1, k); reverseSegment(newTour, i + 1, k); break;
        }
        return newTour;
    }
}
