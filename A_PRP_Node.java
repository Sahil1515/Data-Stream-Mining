package ca.pfv.spmf.test;

import java.util.ArrayList;
import java.util.List;

public class A_PRP_Node {
	public Integer  Label =0;// Integer or character
	public List<A_PRP_Node> childs = new ArrayList<A_PRP_Node>();
	public A_PRP_Node Parent = null;
	public List<Integer> TIDs = new ArrayList<Integer>();
	public A_PRP_Node nodeLink = null;
	public A_PRP_Node tailNodeLink = null;
	
	public A_PRP_Node()
	{
		
	}
	
}

