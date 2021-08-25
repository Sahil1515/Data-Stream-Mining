package ca.pfv.spmf.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.net.URL;
import ca.pfv.spmf.test.A_PRPSTable;

public class A2_my_class {

  public A2_my_class () {}
  
  
  public static void Inititial_Window_creation(int currentTid, List<List> DS,int  WS, List<A_PRPSTable> PRPSTable, A_PRPTidSTree PRPSTidTree)
  {
	  int tCount=0;
	  
	  A_PRP_Node currentRoot=new A_PRP_Node();
	  currentRoot.Label=-1;
	  currentRoot.Parent=null;
	  currentRoot.childs=new ArrayList<A_PRP_Node>();;
	  currentRoot.tailNodeLink=null;
	  
	  A_PRPSTable row=new A_PRPSTable();
	  row.Label=0;
	  row.Support=0;
	  row.prevTid=0;
	  row.maxPrd=0;
	  row.nodeLink=null;
	  PRPSTable.add(0,row); 
	  
	  printTable(PRPSTable);
	  
	  
	  for(List<Integer> T : DS)
	  {
		  if(tCount<WS)
		  {
			  Insert_PRPSTidTree(currentTid,T, currentRoot, PRPSTable);
			  currentTid++;
			  tCount++;
		  }
		  if(tCount<WS)
		  {
			  continue;
		  }
		  else break;
	  }
	  
	  RefreshTable(PRPSTable,WS);
	  System.out.print("\n\nPreorderTraversal of tree is: ");
      printTree(currentRoot);
      System.out.print("\n\n");
  }
  
  public static int c=0;
  
  public static void Insert_PRPSTidTree(int currentTid,List<Integer> transaction, A_PRP_Node currentRoot, List<A_PRPSTable> PRPSTable) {
	  c++;
	  
	  boolean flag=false;
	  boolean table_flag=false;
	  
	  for(Integer p :transaction ) 
	  {
		  
		  A_PRP_Node node=null;// added
		  
		  if(currentRoot!=null)
		  { 
			  //Checking if the path already exists
			  for (A_PRP_Node child : currentRoot.childs)
			  {
				  if(child.Label==p) 
				  {//node exists
					  
					  node=child;
					  flag=true;
					  
					//modify the PRPS Table
					  for(A_PRPSTable R : PRPSTable)
					  {
						  if(R.Label==p)
						  {
							  int currentMaxPrd=currentTid-R.prevTid;
							  if(R.maxPrd<currentMaxPrd)
							  {
								  R.maxPrd=currentMaxPrd;
							  }
							  R.prevTid=currentTid;
							  R.Support++;
						  }
					  }// Inner For loop ends
				  }
				  if(flag==true)
				  {
					  break;
				  }
			  }
		  }
		  
		  
		  //If the path is totally new
		  if(flag==false)
		  {
			  node=new A_PRP_Node();
			  node.Label=p;
			  node.Parent=currentRoot;
			  node.childs=new ArrayList<A_PRP_Node>();;
			  node.tailNodeLink=null;
			  
			  //Update PRPS table
			  if(currentRoot!=null)
			  {  
				  currentRoot.childs.add(node);
			  }
			  
			  //Checking if the node entry is made already in the table then we will simply modify it
			  for(A_PRPSTable R : PRPSTable)
			  {
				  if(R.Label==p)
				  {
					  table_flag=true;
					  int currentMaxPrd=currentTid-R.prevTid;
					  if(R.maxPrd<currentMaxPrd)
					  {
						  R.maxPrd=currentMaxPrd;
					  }
					  node.nodeLink=R.nodeLink;
					  R.nodeLink=node;
					  R.Support++;//added
					  R.prevTid=currentTid;
					  
				  }
			  }// Inner For loop ends
			  //printTable(PRPSTable);
			  
			  //If node entry is not made in the table then we will make a new row for this node in the table
			  if(table_flag==false)// label1 ->
			  {
				  A_PRPSTable row=new A_PRPSTable();
				  row.Label=p;
				  row.Support=1;
				  row.prevTid=currentTid;
				  row.maxPrd=currentTid;
				  row.nodeLink=node;// doubt
				  
				  PRPSTable.add(row);

			  }
		  }
		  
		  transaction.remove(0);
		  
		  // If the transaction is finished then update the tail ids
		  if(transaction.size()<=0)
		  {
			  node.TIDs.add(currentTid);
			  if(node.TIDs.size()==0)
			  {	
				  System.out.println("\n\nHey\n\n");
				  PRPSTable.get(0).nodeLink=node; //check about index 0 
			  }
			  else {
				  A_PRP_Node getcurrentTail= PRPSTable.get(0).nodeLink;
				  
				  if(getcurrentTail!=null)
				  while(getcurrentTail.tailNodeLink!=null)
				  {
					  getcurrentTail=getcurrentTail.tailNodeLink;
				  }
				  if(getcurrentTail!=null)
				  getcurrentTail.tailNodeLink=node;
			  }
			  printTable(PRPSTable);
		  }
		  else if(node!=null)
		  {
			  Insert_PRPSTidTree(currentTid,transaction,node,PRPSTable);//what to pass as node
		  }
		  
		  if(transaction.size()<=0)
			  break;
		  
	  }  //outer for loop ends
  }
  
  public static void RefreshTable(List<A_PRPSTable> PRPSTable,int WS)
  {
	  for(A_PRPSTable row :PRPSTable)
	  {
		  if(row.prevTid!=WS)
		  {
			  int currentMaxPrd=WS-row.prevTid;
			  if(row.maxPrd<currentMaxPrd)
			  {
				  row.maxPrd=currentMaxPrd;
			  }
		  }
	  }
  }
  
  
  public static void main(String[] args) throws IOException {

      // the Input and output files
      String inputFile = fileToPath("contextSPPGrowth.txt");
      BufferedReader reader = new BufferedReader(new FileReader(inputFile));

      String line;

      List<List> DS = new ArrayList<List>();

      
      while (((line = reader.readLine()) != null)) {
          String[] lineSplited = line.split(" ");
          List<Integer> transaction = new ArrayList<Integer>();
          
          for (String itemString : lineSplited) {
              Integer item_name = Integer.parseInt(itemString);
             transaction.add(item_name);   
          }
          DS.add(transaction);
      }
      reader.close();
      
      int currentTid=1;
      int WS=6;
      List<A_PRPSTable> PRPSTable= new ArrayList<A_PRPSTable>();
      
      A_PRPTidSTree tree=null;
      
      Inititial_Window_creation(currentTid, DS, WS, PRPSTable, tree);  
      
      System.out.println("\n"+c);
      
  }
  
  
  public static String fileToPath(String filename) throws UnsupportedEncodingException{
		URL url = MainTestSPPGrowth.class.getResource(filename);
		 return java.net.URLDecoder.decode(url.getPath(),"UTF-8");
	}
  
  
  public static void  printChildren(A_PRP_Node node)
  {
	  System.out.print("Children of "+node.Label+":");
	  for(A_PRP_Node e:node.childs)
		  System.out.print(e.Label+" ");
	  System.out.println();
  }
  public static void printDS(List<List> DS)
  {
	  for(List<Integer> lst:DS)
      {
    	  for(Integer ele: lst)
    	  {
    		  System.out.print(ele);
    		  System.out.print(" ");
    	  }
    	  System.out.println();
      }
  }
  public static void printTree(A_PRP_Node Root) 
  {
	 if(Root==null)
		 return ;
	 
	 for(A_PRP_Node e: Root.childs)
	 {
		 System.out.print(e.Label+ " ");
		 printTree(e);
		 
	 }
  }

  
  public static void printTable(List<A_PRPSTable> PRPSTable) {
	  
//	  Collections.reverse(PRPSTable);
	  System.out.println("\nPrinting Table ");
	  
	  int flag=0;
	  
	  
	  for(A_PRPSTable row: PRPSTable)
	  {
		  if(flag==0)
		  {
			  A_PRP_Node temp_node=row.nodeLink;
			  while(temp_node!=null)
			  {
				  System.out.print(temp_node);
				  System.out.print(" ");
				  if(row.nodeLink!=null)
					  System.out.print(row.nodeLink.Label);
				  
				  System.out.println();
//				  System.out.print(" ");
				 
				  temp_node=temp_node.tailNodeLink;
			  }
			  System.out.print("\n\nLabel\tSupport\tmaxPrd\tprevTid\tnodeLink\t\n");
		  }
		  flag++;
		  System.out.print(row.Label);
		  System.out.print("\t");
		  System.out.print(row.Support);
		  System.out.print("\t");
		  System.out.print(row.maxPrd);
		  System.out.print("\t");
		  System.out.print(row.prevTid);
		  System.out.print("\t");
		  System.out.print(row.nodeLink);
		  System.out.print("\t");
		  if(row.nodeLink!=null)
			  System.out.print(row.nodeLink.Label);
		  System.out.print("  t");
		  
		  if(row.nodeLink!=null)
			  if(row.nodeLink.tailNodeLink!=null)
				  System.out.print(row.nodeLink.tailNodeLink.Label);
		  System.out.print("  p");
		  
		  if(row.nodeLink!=null)
			  if(row.nodeLink.Parent!=null)
				  System.out.print(row.nodeLink.Parent.Label);
		  System.out.print("  c");
		  
		  if(row.nodeLink!=null)
			  if(row.nodeLink.childs!=null) 
			  {
				  System.out.print(row.nodeLink.childs.size()+"  ");
				  
				  for(A_PRP_Node n:row.nodeLink.childs)
				  {
					  System.out.print(n.Label);
					  System.out.print(" ");
				  }
			  }
		  
		  
		  System.out.println();
	  }
	 
  }
  
}




//1 2 3 6
//1 2 3 4 6
//2 3 4 5
//1 2 3 4 5
//1 3 5
//2 3 5
//1 3 4
//1 3 5 


