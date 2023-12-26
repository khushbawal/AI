package environment;

/**
 * A simple object representing the Tiles in the
 * environment.
 * @author Khush Bawal
 */
public class Tile {
	private TileStatus status;
	
	public Tile(TileStatus status) {
		this.status = status;
	}
	
	public TileStatus getStatus() { return status; }
	public String toString() { return ""+status.toString().charAt(0); }
}
