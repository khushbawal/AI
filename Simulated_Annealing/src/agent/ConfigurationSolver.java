package agent;

//import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
import java.util.Random;

import environment.Environment;

/**
 * Represents a linear assignment problem where N workers must be assigned
 * to N tasks. Each worker/task combination is further associated with some
 * value. The goal of this task is the produce an optimal configuration that
 * maximizes (or minimizes) the sum of the assigned worker/task values.
 * @author Khush Bawal	
*/

public class ConfigurationSolver {
	private Environment env;
	private int[] configuration;
	private int[] bestConfiguration;
	double temp;
	Random rand;
	int numWorkers;
	
	/** Initializes a Configuration Solver for a specific environment. */
	public ConfigurationSolver (Environment env) { 
		this.env = env;
		this.configuration = new int[this.env.getNumWorkers()];
		// Initializing by assigning work to an arbitrary task
		for(int i = 0; i < this.configuration.length; i++) {
			this.configuration[i] = i;
		}
		// Need to clone because of how Java handles assigning arrays
		// as values (relative referencing).
		// Cloning the array resolves this issue.
		this.bestConfiguration = this.configuration.clone();
		this.temp = 100;
		this.rand = new Random();
		this.numWorkers = this.env.getNumWorkers();
		System.out.println("Current :- " + Arrays.toString(this.configuration) + " = " + env.calcScore(this.configuration));
		System.out.println("Best :- " + Arrays.toString(this.bestConfiguration) + " = " + env.calcScore(this.bestConfiguration));

	}


	/**
		Basically the same step are used as swapping two tasks of two different workers each time they are random
		while keeping the other similar to previous. Also basically implementing the Simulated Annealing code
		provided in lecture slides. Basically if E >= 0 then accept as current configuration and if E < 0 then
		run it through a probability of e^(E/t) where t reduces after each iteration. If probability passes then
		current state is updated again.
	 */
	public int[] updateSearch () {
		temp *= 0.95;
		if (temp < 0.000001) {
			return configuration;
		}
		int[] intList = this.configuration.clone();
		int nextVal = 0;
//		Collections.shuffle(intList);
		//Swapping Workers
		int randSwapA = rand.nextInt(numWorkers);
		int randSwapB = rand.nextInt(numWorkers);
		//Swap here
		Integer tempRandSwapHold = intList[randSwapA];
		intList[randSwapA] = intList[randSwapB];
		intList[randSwapB] = tempRandSwapHold;
		nextVal = env.calcScore(intList);
		int E = nextVal - env.calcScore(this.configuration);
		double prob = Math.exp(E/temp);
//		System.out.println("E = " + E + " = " + nextVal + " + " + env.calcScore(this.configuration));
		if (E >= 0) {
			this.configuration = intList.clone();
			if (env.calcScore(this.configuration) > env.calcScore(bestConfiguration))
				this.bestConfiguration = this.configuration.clone();
		}
		else if (E < 0) {
//			System.out.println("Probability" + prob);
			if (rand.nextInt() < prob) {
				this.configuration = intList.clone();
			}
		}
//		System.out.println("Current :- " + Arrays.toString(this.configuration) + " = " + env.calcScore(this.configuration));
//		System.out.println("Best :- " + Arrays.toString(this.bestConfiguration) + " = " + env.calcScore(this.bestConfiguration));
		return this.configuration;
	}
	
	/**
	 * In addition to updateSearch, we should also track you BEST OBSERVED CONFIGURATION.
	 * While our search may move to worse configurations (since that's what the algorithm
	 * needs to do), getBestConfiguration will return the best observed configuration by our
	 * agent.
	*/
	public int[] getBestConfiguration() {
		return this.bestConfiguration;
	}
	
}
