package agent;


import java.util.Map;
import java.util.PriorityQueue;
import java.util.HashMap;

import environment.TileStatus;
import environment.Action;
import environment.Environment;
import environment.Position;

/**
	Represents an intelligent agent moving through a particular room.	
	The robot only has two sensors - the ability to retrieve the 
	the status of all its neighboring tiles, including itself, and the
	ability to retrieve to location of the TARGET tile.
*/

public class Robot {
	private Environment env;

	/** Initializes a Robot on a specific tile in the environment. */
	public Robot (Environment env) {
		this.env = env;
	}
	
	/**
		I used the A* alogrithm's puesdo code provided on https://www.redblobgames.com/pathfinding/a-star/introduction.html#astar
		The code function below considers different tiles as locations to reaching final destination.
		So each path is stored in the order of lowest distance to be covered in the priority queue.
		These paths are stored as nodes and heuristic functions are used to determine the best possible path to move to reach the destination in less number of steps.
		
	 */
	public Action getAction () {
		
		Position selfPos = env.getRobotPosition(this);

		Map<String, Position> neighbors = env.getNeighborPositions(selfPos);
		Position abovePos = neighbors.get("above"); // Either a Tile or null
		Position belowPos = neighbors.get("below"); // Either a Tile or null
		Position leftPos = neighbors.get("left");   // Either a Tile or null
		Position rightPos = neighbors.get("right"); // Either a Tile or null



		Position targetPos = env.getTarget();

		if (selfPos.equals(targetPos)) {

			return Action.DO_NOTHING;
		}
		else {

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