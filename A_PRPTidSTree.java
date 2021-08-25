package ca.pfv.spmf.test;


//package ca.pfv.spmf.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class A_PRPTidSTree {
	
	// list of items in the header table
	public List<Integer> headerList = null;
	
	// list of pair (item, A_PRP_Node) of the header table
	public Map<Integer, A_PRP_Node> mapItemNodes = new HashMap<Integer, A_PRP_Node>();
	
	// map that indicates the last node for each item using the node links
	// key: item  value: an A_PRP_Node
	Map<Integer, A_PRP_Node> mapItemLastNode = new HashMap<>();
	
	// root of the tree
	public A_PRP_Node root = new A_PRP_Node(); // null node
	
	public int numberOfNode = 0;
	
	public A_PRPTidSTree() {
		
	}
	
	/**
	 * Method for adding a transaction and its timestamps to the spp-tree
	 * (for the initial construction of the spp-tree)
	 */
	
	public void addTransaction(List<Integer> transaction, int TID) {
		
		A_PRP_Node currentNode = root;
		
		// for each item in the transaction
		for(Integer item: transaction) {
			
			// look if there is a node already in the spp-tree
			A_PRP_Node child = currentNode.getChildByID(item);
			
			if (child == null) {
				
				numberOfNode++;
				
				// there is no node, we create a new one
				A_PRP_Node newNode = new A_PRP_Node();
				newNode.itemID = item;
				newNode.parent = currentNode;
				
				// we link the new node to its parent
				currentNode.childs.add(newNode);
				
				// we take this node as the current node for the next loop iteration
				currentNode = newNode;
				
				// we update the header table
				// we check if there is already a node with this id in the header table
				fixNodeLinks(item, newNode);
				
				
			} else {
				
				// there is a node already, we skip it
				currentNode = child;
			}
			
		}
		
		currentNode.TIDs.add(TID);
	}
	
	
	/**
	 *  method to fix the node link for an item after inserting a new node
	 *  
	 */
	
	private void fixNodeLinks(Integer item, A_PRP_Node newNode) {
		
		// get the latest node in the tree with this item
		A_PRP_Node lastNode = mapItemLastNode.get(item);
		if (lastNode != null) {
			
			// if not null, then we add the new node to the node link of the last node
			lastNode.nodeLink = newNode;
			
		}
		
		// finally, we set the new node as the last node
		mapItemLastNode.put(item, newNode);
		
		A_PRP_Node headernode = mapItemNodes.get(item);
		if (headernode == null) {
			mapItemNodes.put(item,  newNode);
		}
	}
	
	
	/**
	 *  method for creating the list of items in the header table
	 *  in descending order of total duration
	 */
	
	public void createHeaderList(List<Integer> lastHeaderList, final Map<Integer, Support_maxla> mapSPP_list) {
		
		if (lastHeaderList == null) {
			
			// this is headerList for the total spp-tree
			
			// create an array to store the header list with
			// all the items stored in the map received as parameter
			headerList = new ArrayList<Integer>(mapItemNodes.keySet());
			
			// sort the header table by decreasing order of total duration
			Collections.sort(headerList, new Comparator<Integer>() {
				
				public int compare(Integer item1, Integer item2) {
					// if the same total duration, we check the lexical ordering
					int compare = mapSPP_list.get(item2).getSupport() - mapSPP_list.get(item1).getSupport();
					
					if (compare == 0) {
						
						return (item1 - item2);
						
					}
					
					// otherwise, just use the total duation
					return compare;
				}
			});
		} else {
			
			headerList = new ArrayList<>();
			for(int item: lastHeaderList) {
				
				if (mapSPP_list.containsKey(item)) {
					 headerList.add(item);
				}
			}
		}
	}
	
	/**
	 *  method for adding a prefix path to a spp-tree
	 *  
	 */
	
	public void addPrefixPath(List<A_PRP_Node> prefixPath, Map<Integer, Support_maxla> mapBetaSPPlist) {
		
		// the first element of the prefix path contains the path TIDs
		List<Integer> pathTIDs = prefixPath.get(0).TIDs;
		
		A_PRP_Node currentNode = root;
		
		// for each item in the transaction (in backward order)
		// (and we ignore the first element of the prefix path)
		for (int i = prefixPath.size()-1; i >= 1; i--) {
			
			A_PRP_Node pathItem = prefixPath.get(i);
			
			if (mapBetaSPPlist.containsKey(pathItem.itemID)) {
				
				// look if there's a node already in the spp-tree
				A_PRP_Node child = currentNode.getChildByID(pathItem.itemID);
				
				if (child == null) {
					
					// there's no node, we create a new one
					A_PRP_Node newNode = new A_PRP_Node();
					
					newNode.itemID = pathItem.itemID;
					newNode.parent = currentNode;
					currentNode.childs.add(newNode);
					currentNode = newNode;
					
					fixNodeLinks(pathItem.itemID, newNode);
					
				} else {
					
					// there's a node already, we update it
					currentNode = child;
				
				}
			}
			
		}
		
		if (currentNode.itemID != -1) {
			currentNode.TIDs.addAll(pathTIDs);
		}
	}
	
	public void removeTailItem() {
		
		// get the index of tail item
		int tail = headerList.size() - 1;
		
		// get the tail path
		A_PRP_Node tailNode = mapItemNodes.get(headerList.get(tail));
		
		// delete the tail path from mapItemNodes
		mapItemNodes.remove(headerList.get(tail));
		
		// delete the tail from headerList
		headerList.remove(tail);
		
		// move each tail node's timestamps to its parent
		while (tailNode != null) {
			
			A_PRP_Node parent = tailNode.parent;
			
			parent.removeChildByID(tailNode.itemID);
			
			if (parent.itemID != -1) {
				
				List<Integer> timestamps = tailNode.TIDs;
				
				parent.TIDs.addAll(new ArrayList<Integer>() {
					{
						addAll(timestamps);
					}
				});
				
			}
			tailNode = tailNode.nodeLink;
			
		}
	}
} 

