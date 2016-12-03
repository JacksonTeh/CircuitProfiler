package Main;


import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class Graph {
	
	private int widthGuard;
	private int currentWidthSize;
	private int currentRowSize;
	private ArrayList<ArrayList<Nodes>> placementList;
	private int calculatedRowSize;
	public static final int rowSeperation = 36;
	public Graph(NodeList nodeList)
	{
		
		this.currentWidthSize = 0;
		this.currentRowSize =0;
		this.calculatedRowSize = ((int)Math.ceil(Math.sqrt(nodeList.gettotalNonTerminalWidth()/rowSeperation))); 
		this.widthGuard = this.calculatedRowSize * rowSeperation;
		this.placementList = new ArrayList<ArrayList<Nodes>>();
		ArrayList<Nodes> tempNodeList = nodeList.getNonTerminalNodeList();
		try
		{
			for (Iterator<Nodes> i = tempNodeList.iterator(); i.hasNext();)
			{
				Nodes tempNode = i.next();
				this.addNode(tempNode);
			}
		}catch (ConcurrentModificationException e)
		{
			System.out.println(e.getMessage());
			System.out.println("failed");
		}
		
	}
	
	public Nodes nodeInThisLocation (int x, int y)
	{
		Nodes tempNode;
		NodeCoordinate tempCoordinate;
		for (int k =0; k < this.placementList.size() ;k++)
		{
			for (Iterator<Nodes> i = this.placementList.get(k).iterator(); i.hasNext();)
			{
				tempNode = i.next();
				tempCoordinate = tempNode.getNodeCoordinate();
				if (tempCoordinate.getNodeXCoordinate()+tempNode.getNodeWidth()>= x &&
					tempCoordinate.getNodeXCoordinate() <= x &&
					tempCoordinate.getNodeYCoordinate() == y	)
				{
					return tempNode;
				}
			}
		}
		return null;
	}
	public void swapNodes (Nodes node1, Nodes node2)
	{
		int yIndex1 = node1.getNodeCoordinate().getNodeYCoordinate()/rowSeperation;
		int yIndex2 = node2.getNodeCoordinate().getNodeYCoordinate()/rowSeperation;
		int xIndex1 = this.placementList.get(yIndex1).indexOf(node1);
		int xIndex2 = this.placementList.get(yIndex2).indexOf(node2);
		Nodes tempNode1 = this.placementList.get(yIndex1).get(xIndex1);
		Nodes tempNode2 = this.placementList.get(yIndex2).get(xIndex2);
		NodeCoordinate tempCoordinate1 = tempNode1.getNodeCoordinate();
		NodeCoordinate tempCoordinate2 = tempNode2.getNodeCoordinate();
		tempNode1.setNodeCoordinate(tempCoordinate2.getNodeXCoordinate(), tempCoordinate2.getNodeYCoordinate());
		tempNode2.setNodeCoordinate(tempCoordinate1.getNodeXCoordinate(), tempCoordinate1.getNodeYCoordinate());
		
		
	}
	
	public void changeCoordinate (Nodes node, int x, int y)
	{
		NodeCoordinate newCoordinate = node.getNodeCoordinate();
		newCoordinate.setNodeXCoordinate(x);
		newCoordinate.setNodeYCoordinate(y);
	}
	
	
	public boolean moveNodeByWidth (Nodes node, int width)
	{
		NodeCoordinate coordinateOfTheNode = node.getNodeCoordinate();
		int rowNumber = coordinateOfTheNode.getNodeYCoordinate()/rowSeperation;
		int locationInPlacementList = this.placementList.get(rowNumber).indexOf(node);
		Nodes tempNode = this.placementList.get(rowNumber).get(locationInPlacementList);
		if (coordinateOfTheNode.getNodeYCoordinate() + width > this.widthGuard)
		{
			return false; //This node at the end, cant switch
		}
		else
		{
			tempNode.setNodeCoordinate(coordinateOfTheNode.getNodeXCoordinate(), coordinateOfTheNode.getNodeYCoordinate() + width);
			return true; //This node can be switch.
		}
		
		
	}
	
	public boolean addNode(Nodes newNode)
	{
		if (this.calculateTheWidthGuard(newNode))
		{
			this.updateNodeCoordinate (newNode,this.currentWidthSize,this.currentRowSize);
			this.currentWidthSize += newNode.getNodeWidth();
			if (this.placementList.size() < (this.currentRowSize/rowSeperation) + 1 )
			{
				this.placementList.add(new ArrayList<Nodes>());
			}
			this.placementList.get(this.currentRowSize/rowSeperation).add(newNode);
			return true;
		}
		else
		{
			return false; //Fulled and cant place d
		}
	}
	
	private boolean calculateTheWidthGuard(Nodes newNode) 
	{
		if (this.currentWidthSize + newNode.getNodeWidth() <= this.widthGuard)
		{
			return true;
		}
		else
		{
			this.currentRowSize += rowSeperation;
			this.currentWidthSize = 0;
			if (this.currentRowSize/rowSeperation > this.calculatedRowSize)
			{
				return false; //Fulled.
			}
			else
			{
				return true;
			}
		}
	}

	public void updateNodeCoordinate(Nodes newNode, int x, int y) 
	{
		
		newNode.setNodeCoordinate(x, y);
		
	}

	public void legalizeNodes ()
	{
		for (int row = 0; row <= this.placementList.size(); row++)
		{
			ArrayList<Nodes> tempList = this.placementList.get(row);
			//tempList.so;
			Nodes leftNode = null;
			Nodes rightNode = null;
			NodeCoordinate leftNodeCoordinate,rightNodeCoordinate;
			int lengthToShift,accumulatedWidthSize = 0;
			for (Iterator<Nodes> i = tempList.iterator(); i.hasNext();)
			{
				rightNode = i.next();
				accumulatedWidthSize += rightNode.getNodeWidth();
				if (leftNode != null) //First iteration, first node.
				{
					leftNodeCoordinate = leftNode.getNodeCoordinate();
					rightNodeCoordinate = rightNode.getNodeCoordinate();
					lengthToShift = leftNodeCoordinate.getNodeXCoordinate() + leftNode.getNodeWidth() - rightNodeCoordinate.getNodeXCoordinate();
					if (leftNodeCoordinate.getNodeXCoordinate() + leftNode.getNodeWidth() >= rightNodeCoordinate.getNodeXCoordinate())
						//Overlapped, shift the right cell
					{
						if (!this.moveNodeByWidth(rightNode, lengthToShift))
						{
							//Reached the end of the boundary.
							if (accumulatedWidthSize <= this.widthGuard)
							{
								//This row can fit all the nodes
								//Add node to the end of the last line
								this.reduceTheWidthSpacing (tempList,lengthToShift);
							}
							else
							{
								//This row can not fit all the nodes.
								//Change the coordinate of the node and add to the next row.
								rightNode.setNodeCoordinate(rightNodeCoordinate.getNodeXCoordinate(), rightNodeCoordinate.getNodeYCoordinate());
								this.placementList.get(row+1).add(rightNode);
							}
						}
					}
				}
				leftNode = rightNode;
			}
			
		}
	}

	private void reduceTheWidthSpacing(ArrayList<Nodes> tempList, int lengthToShift) 
	{
		Nodes rightNode,leftNode =null ;
		NodeCoordinate rightCoordinate,leftCoordinate;
		for (Iterator<Nodes> i = tempList.iterator(); i.hasNext()|lengthToShift <= 0;)
		{
			rightNode = i.next();
			rightCoordinate = rightNode.getNodeCoordinate();
			if (leftNode == null)
			{
				//First Iteration
				if (rightCoordinate.getNodeXCoordinate() !=0)
				{
					rightNode.setNodeCoordinate(rightCoordinate.getNodeXCoordinate()-1,rightCoordinate.getNodeYCoordinate());
					lengthToShift --;
				}
			}
			else
			{
				leftCoordinate = leftNode.getNodeCoordinate();
				if (leftCoordinate.getNodeXCoordinate()+rightNode.getNodeWidth()< rightCoordinate.getNodeXCoordinate())
				{
					rightNode.setNodeCoordinate(rightCoordinate.getNodeXCoordinate()-1, rightCoordinate.getNodeYCoordinate());
					lengthToShift--;
				}
			}
			leftNode = rightNode;
		}
		if (lengthToShift > 0)
		{
			this.reduceTheWidthSpacing(tempList, lengthToShift);
		}
	}

}
