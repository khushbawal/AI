package simulation;

import utils.MapManager;
import environment.Environment;

/**
 * A non-GUI version of the simulation. This will allow
 * us to quickly test out your implementations without
 * having to wait for each time-step to occur (there is a
 * 200 millisecond delay between time-steps in the visualization).
 * @author Khush Bawal
 */
public class RunSimulation {
	private Environment env;
	private int interations;
	private boolean displaySimErrors;
	
	public RunSimulation(String mapFile, int iterations) {
		String[] map = MapManager.loadMap(mapFile);
		this.env = new Environment(map);
		this.interations = iterations;
		this.displaySimErrors = true;
	}
	

	public void run() {
		for (int i = 1; i <= this.interations; i++) {
			try {
				// Wrapped in try/catch in case the Robot's decision results
				// in a crash; we'll treat that the same as Action.DO_NOTHING
				env.updateEnvironment();
			} catch (Exception ex) {
				if (this.displaySimErrors) {
					String error = "[ERROR AGENT CRASH AT TIME STEP %03d] %s\n";
					System.out.printf(error, i, ex);
				}
			}
			
			// Quit the simulation early if the goal condition is met
			if (this.env.goalConditionMet()) {
				break;
			}
		}
	}
	
	public boolean goalConditionMet() {
		return this.env.goalConditionMet();
	}

	public static void main(String[] args) {
		// Currently uses the first public test case
		String mapFile = "maps/public/map01.txt";
		int iterations = 200;
		RunSimulation sim = new RunSimulation(mapFile, iterations);
		sim.run();
    }
}