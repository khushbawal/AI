package edu.ncsu.csc411.ps02.agent;


import java.util.Map;
import java.util.PriorityQueue;
import java.util.HashMap;

//import edu.ncsu.csc411.ps02.environment.Tile;
import edu.ncsu.csc411.ps02.environment.TileStatus;
import edu.ncsu.csc411.ps02.environment.Action;
import edu.ncsu.csc411.ps02.environment.Environment;
import edu.ncsu.csc411.ps02.environment.Position;

/**
	Represents an intelligent agent moving through a particular room.	
	The robot only has two sensors - the ability to retrieve the 
	the status of all its neighboring tiles, including itself, and the
	ability to retrieve to location of the TARGET tile.
	
	Your task is to modify the getAction method below so that it reaches
	TARGET with a minimal number of steps.
*/

public class Robot {
	private Environment env;

	/** Initializes a Robot on a specific tile in the environment. */
	public Robot (Environment env) {
		this.env = env;
	}
	
	/**
    Problem Set 02 - Modify the getAction method below in order to simulate
    the passage of a single time-step. At each time-step, the Robot decides
    which tile to move to.
    
    Your task for this Problem Set is to modify the method below such that
    the Robot agent is able to reach the TARGET tile on a given Environment. 
    5 out of the 10 graded test cases, with explanations on how to create new
    Environments, are available under the test package.
    
    This method should return a single Action from the Action class.
    	- Action.DO_NOTHING
    	- Action.MOVE_UP
    	- Action.MOVE_DOWN
    	- Action.MOVE_LEFT
    	- Action.MOVE_RIGHT
	 */

	/**
		I used the A* alogrithm's puesdo code provided on https://www.redblobgames.com/pathfinding/a-star/introduction.html#astar
		The code function below considers different tiles as locations to reaching final destination.
		So each path is stored in the order of lowest distance to be covered in the priority queue.
		These paths are stored as nodes and heuristic functions are used to determine the best possible path to move to reach the destination in less number of steps.
		
	 */
	public Action getAction () {
		// This example code demonstrates the available methods and actions,
		// such as retrieving its senses (neighbor positions), getting the status of
		// those tiles, and returning the different available Actions
		
		Position selfPos = env.getRobotPosition(this);
		// For this problem set, the Environment class allows for complete
		// observability. You are able to find all neighbor positions for 
		// any given Position object by passing a Position object to
		// getNeighborPositions
		Map<String, Position> neighbors = env.getNeighborPositions(selfPos);
		Position abovePos = neighbors.get("above"); // Either a Tile or null
		Position belowPos = neighbors.get("below"); // Either a Tile or null
		Position leftPos = neighbors.get("left");   // Either a Tile or null
		Position rightPos = neighbors.get("right"); // Either a Tile or null


		// You are able to get their tile using the getTiles method
//		Map<Position, Tile> positions = env.getTiles();
//		Tile selfTile = positions.get(selfPos);
		
		// The Environment now has a getTarget() method which will return
		// the Position of the Target node.
		Position targetPos = env.getTarget();
		// The Position class has also been updated to include an equals method
		if (selfPos.equals(targetPos)) {
//			System.out.println("DO NOTHING");
			return Action.DO_NOTHING;
		}
		else {
			// You are STRONGLY encouraged to design a search tree.
			// NOTE: There are Java.util implementations for many of the data
			// structures taught in 316. You should create Nodes and utilize
			// these data structures.

			PriorityQueue<qNode> paths = new PriorityQueue<>();
		    Map<Position, Position> came_from = new HashMap<>();
		    Map<Position, Integer> cost_so_far = new HashMap<>();
		    Position start;
			start = env.getRobotPosition(this);
			qNode startNode = new qNode(start, 0);
			paths.add(startNode);
			came_from.put(start, null);
			cost_so_far.put(start, 0);

			qNode current = null;
			while (!paths.isEmpty()) {
				current = paths.poll();
//				System.out.println(paths.remove().getPos().toString());
				if (current.getPos().equals(targetPos)) {
					break;
				}
				neighbors = env.getNeighborPositions(current.getPos());
				for (Map.Entry<String, Position> entry : neighbors.entrySet()) {
					if (env.getPositionTile(entry.getValue()).getStatus() == TileStatus.IMPASSABLE) {
						continue;
					}
					int new_cost = cost_so_far.get(current.getPos()) + 1;
					if (!cost_so_far.containsKey(entry.getValue()) ||  new_cost < cost_so_far.get(entry.getValue())) {
						cost_so_far.put(entry.getValue(), new_cost);
						int priority = new_cost + heuristic(targetPos, entry.getValue());
						qNode possiblePath = new qNode(entry.getValue(), priority);
						paths.add(possiblePath);
						came_from.put(entry.getValue(), current.getPos());
					}
				}
			}

//			Trying to get the first step from the optimized path traced and stored in came_from map.
			Position parent = came_from.get(targetPos);
			while (!parent.equals(selfPos)) {
				if (came_from.get(parent).equals(selfPos)) {
					break;
				}
				else {
					parent = came_from.get(parent);
				}
			}
			if (abovePos != null && abovePos.equals(parent)) {
//				System.out.println("UP");
				return Action.MOVE_UP;
			}
			else if (belowPos != null && belowPos.equals(parent)) {
//				System.out.println("DOWN");
				return Action.MOVE_DOWN;
			}
			else if (leftPos != null && leftPos.equals(parent)) {
//				System.out.println("LEFT");
				return Action.MOVE_LEFT;
			}
			else if (rightPos != null && rightPos.equals(parent)) {
//				System.out.println("RIGHT");
				return Action.MOVE_RIGHT;
			}
			else {
//				System.out.println("SUSPICIOUS");
//				The final Move to success.
				Map<String, Position> lastNeighbor = env.getNeighborPositions(parent);
				Position lastAbovePos = lastNeighbor.get("above"); // Either a Tile or null
				Position lastBelowPos = lastNeighbor.get("below"); // Either a Tile or null
				Position lastLeftPos = lastNeighbor.get("left");   // Either a Tile or null
				Position lastRightPos = lastNeighbor.get("right"); // Either a Tile or null
				if (lastAbovePos != null && lastAbovePos.equals(targetPos)) {
//					System.out.println("UP");
					return Action.MOVE_UP;
				}
				else if (lastBelowPos != null && lastBelowPos.equals(targetPos)) {
//					System.out.println("DOWN");
					return Action.MOVE_DOWN;
				}
				else if (lastLeftPos != null && lastLeftPos.equals(targetPos)) {
//					System.out.println("LEFT");
					return Action.MOVE_LEFT;
				}
				else if (lastRightPos != null && lastRightPos.equals(targetPos)) {
//					System.out.println("RIGHT");
					return Action.MOVE_RIGHT;
				}
				else {
//					System.out.println("VERY SUSPICIOUS");
					return Action.DO_NOTHING;
				}
			}
		}
	}

	/**
	 * A class used to determine the distance left to reach the destination using the Manhattan distance method on a 2D grid.
	 * @param targetPos the position every robot wants to reach.
	 * @param next the next considered tile position to calculate the near distance.
	 * @return the distance left from that future tile to the target tile.
	 */
	public int heuristic(Position targetPos, Position next) {
		return (Math.abs(targetPos.getRow() - next.getRow()) + Math.abs(targetPos.getCol() - next.getCol()));
	}

	/**
	 * Inner class which helps us connect tiles with their f = g + h values.
	 * This helps us sort and organize data in the priority queue easy for us to implement the A* algorithm.
	 * Has a comparable interface to sort in priority queue based on ascending values of f.
	 * @author Khush Bawal
	 *
	 */
	class qNode implements Comparable<qNode> {
	    Position pos;
	    int priority;

	    public qNode(Position pos, int priority) {
	        this.pos = pos;
	        this.priority = priority;
	    }
	    public Position getPos() {
	    	return pos;
	    }
	    public int getPriority() {
	    	return priority;
	    }
	    public int compareTo(qNode other) {
	        return Integer.compare(this.priority, other.priority);
	    }
	}
}