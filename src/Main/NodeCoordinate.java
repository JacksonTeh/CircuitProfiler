package Main;

public class NodeCoordinate {
	private int nodeXCoordinate;
	private int nodeYCoordinate;
	
	public NodeCoordinate()
	{
		this.nodeXCoordinate = 0;
		this.nodeYCoordinate = 0;
	}
	
	public void setNodeXCoordinate(int nodeXCoordinate) {
		this.nodeXCoordinate = nodeXCoordinate;
	}
	
	public void setNodeYCoordinate(int nodeYCoordinate) {
		this.nodeYCoordinate = nodeYCoordinate;
	}
	
	public int getNodeXCoordinate() {
		return this.nodeXCoordinate;
	}
	
	public int getNodeYCoordinate() {
		return this.nodeYCoordinate;
	}
}