package pp.pi;

import java.util.*;
import acm.program.*;

public class PISolver extends ConsoleProgram {

	private static final int DEFAULT_SMOOTH_FACTOR = 2;
	private List<Double> grayValue;

	// Pre: throws an IllegalArgumentException if the list is null or empty
	// Post: construct a patch index solver with the data given as a X-Y correlation
	public PISolver(List<Double> data) {
		checkNull(data);
		grayValue = new ArrayList<Double>(data);
	}

	// Post: return SM
	public double getSM() {
		return grayValue.size();
	}

	// Post: return SI
	public double getSI() {
		double sum = 0;
		int lastX = 0;
		double lastY = grayValue.get(lastX);
		for (int x = 0; x < grayValue.size(); x++) {
			int xDiff = x - lastX;
			double yDiff = grayValue.get(x) - lastY;
			sum += Math.sqrt(xDiff * xDiff + yDiff * yDiff);
			lastX = x;
			lastY = grayValue.get(x);
		}				
		return sum;
	}

	// Post: return Patch Index = 1 - (SM/SI)
	public double doPICalculation() {
		double sm = getSM();
		double si = getSI();
		double pi = 1 - (sm / si);
		return 	pi;
	}

	// Method chosen for smoothing: Moving Average (MA)
	// given n% of data per mov. avg. (here called smooth index), smooth the curve to avoid noise
	// Pre:  0 < n < 100, no exceptions
	public List<Double> smooth(int n){
		n = (int) (n * getSM() / 100);
		List<Double> temp = new ArrayList<Double>();
		for (int index = grayValue.size() - 1; index >= n; index--) {
			double sum = 0;
			for (int i = index - n; i < index; i++) {
				sum += grayValue.get(index);
			}
			temp.add(0, sum / n);
		}
		for (int i = 0; i < n; i++) {
			grayValue.remove(0);
		}
		return grayValue;
	}

	// Pre: given an object as an input
	// Post: if it is null or empty, throw an IllegalArgumentException
	private void checkNull(Object o) {
		if (o == null) {  
			throw new IllegalArgumentException();
		}
	}

}
