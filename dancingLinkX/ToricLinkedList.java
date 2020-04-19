package dancingLinkX;

/*
dlx algorithm interface for solving general exact cover problems
source : https://arxiv.org/pdf/cs/0011047.pdf
*/

public class ToricLinkedList {
	
	public HeaderQNode root;
	public int solutionCnt = 0;
	
	public ToricLinkedList(){
		root = new HeaderQNode();
		
		root.leftQNode = root;
		root.rightQNode = root;
		root.upperQNode = root;
		root.lowerQNode = root;
		root.headerQNode = root;
		root.primary = true;
	}
	
	public ToricLinkedList(String name){
		root = new HeaderQNode(name, -1, true);
		
		root.leftQNode = root;
		root.rightQNode = root;
		root.upperQNode = root;
		root.lowerQNode = root;
		root.headerQNode = root;
	}
	
	public ToricLinkedList(String name, String[] columnNames, boolean[] whichPrimary){
		this(name);
		if(columnNames.length == whichPrimary.length){
			for(int i=0; i<columnNames.length; i++){
				addColumn(columnNames[i], i, whichPrimary[i]);
			}
		}
	}
	
	public ToricLinkedList(String name, String[] columnNames, boolean[] whichPrimary, boolean[][] constraints){
		this(name, columnNames, whichPrimary);
		for(int i=0; i<constraints.length; i++){
			addRow(i, constraints[i]);
		}
	}
	
	public void addColumn(String name, int indicator, boolean primary){
		HeaderQNode newCol = new HeaderQNode(name, indicator, primary);
		
		newCol.leftQNode = root.leftQNode;
		newCol.rightQNode = root;
		newCol.upperQNode = newCol;
		newCol.lowerQNode = newCol;
		newCol.headerQNode = root;
		
		root.leftQNode.rightQNode = newCol;
		root.leftQNode = newCol;
		
		root.cnt++;
	}
	
	public void addRow(int indicator, boolean[] boolNodesRow){
		if(boolNodesRow.length != root.cnt) return;
		
		QNode first = null;
		HeaderQNode colNd = root.rightQNode;
		
		for(int i=0; i<boolNodesRow.length; i++, colNd = colNd.rightQNode){
			if(boolNodesRow[i]){
				QNode qnd = new QNode(indicator);
				
				qnd.upperQNode = colNd.upperQNode;
				qnd.lowerQNode = colNd;
				qnd.headerQNode = colNd;
				
				colNd.upperQNode.lowerQNode = qnd;
				colNd.upperQNode = qnd;
				colNd.cnt++;
				
				if(first == null){
					qnd.rightQNode = qnd;
					qnd.leftQNode = qnd;
					
					first = qnd;
				}else{
					qnd.leftQNode = first.leftQNode;
					qnd.rightQNode = first;
					
					first.leftQNode.rightQNode = qnd;
					first.leftQNode = qnd;
				}
			}
		}
	}
	
	public void cover(HeaderQNode colNd){
		
		colNd.leftQNode.rightQNode = colNd.rightQNode;
		colNd.rightQNode.leftQNode = colNd.leftQNode;
		
		QNode cnd = colNd.lowerQNode;
		while(cnd != colNd){
			QNode rnd = cnd.rightQNode;
			while(rnd != cnd){
				rnd.upperQNode.lowerQNode = rnd.lowerQNode;
				rnd.lowerQNode.upperQNode = rnd.upperQNode;
				rnd.headerQNode.cnt--;
				
				rnd = rnd.rightQNode;
			}
			cnd = cnd.lowerQNode;
		}
		root.cnt--;
	}
	
	public void uncover(HeaderQNode colNd){
		
		QNode cnd = colNd.upperQNode;
		while(cnd != colNd){
			QNode rnd = cnd.leftQNode;
			while(rnd != cnd){
				rnd.upperQNode.lowerQNode = rnd;
				rnd.lowerQNode.upperQNode = rnd;
				rnd.headerQNode.cnt++;
				
				rnd = rnd.leftQNode;
			}
			cnd = cnd.upperQNode;
		}
		root.cnt++;
		
		colNd.leftQNode.rightQNode = colNd;
		colNd.rightQNode.leftQNode = colNd;
	}
	
	public void testPrint(){
		HeaderQNode cnd = root.rightQNode;
		while(cnd != root){
			System.out.printf("%5s %5b ", cnd.name, cnd.primary);
			QNode qnd = cnd.lowerQNode;
			while(qnd != cnd){
				System.out.printf("%2d ", qnd.indicator);
				qnd = qnd.lowerQNode;
			}
			cnd = cnd.rightQNode;
			System.out.println();
		}
	}
	
	public void searchSolution(){
		this.searchSolution(new SolutionFormatter());
	}
	
	public void searchSolution(QNodeStackFormatter qnsfm){
		// print every possible solution for exact cover problem
		System.out.println(root.name);
		System.out.println();
		
		solutionCnt = 0;
		QNode[] stack = new QNode[root.cnt];
		int depth = 0;
		boolean currentlyValid = true;
		
		search : while(depth>=0){
			// System.out.printf("depth %d check\n", depth);
			if(currentlyValid){ // proceed
				HeaderQNode colNd = root;
				do{
					colNd = colNd.rightQNode;
				}while(!colNd.primary);
				if(colNd == root){ // solution found
					System.out.printf("solution found : cnt %d\n", ++solutionCnt);
					// printSolution(stack, depth);
					System.out.printf(qnsfm.qNodeStackFormat(stack, depth));
					System.out.println();
					depth--;
					currentlyValid = false;
					continue;
				}
				
				HeaderQNode minColNd = colNd;
				do{
					// System.out.printf("\tcol %s : %d ", colNd.name, colNd.cnt);
					if(colNd.cnt == 0){
						// branch not valid
						// System.out.printf(": branch invalid\n");
						depth--;
						currentlyValid = false;
						continue search;
					}
					if(minColNd.cnt > colNd.cnt){
						// System.out.printf("-> min");
						minColNd = colNd;
					}
					// System.out.println();
					do{
						colNd = colNd.rightQNode;
					}while(!colNd.primary);
				}while(colNd != root);
				cover(minColNd);
				// System.out.println("\tcover complete");
				
				stack[depth] = minColNd.lowerQNode;
				QNode qnd = stack[depth].rightQNode;
				while(qnd != stack[depth]){
					cover(qnd.headerQNode);
					qnd = qnd.rightQNode;
				}
				depth++;
			}else{ // backtrack & change branch
				QNode qnd = stack[depth].leftQNode;
				while(qnd != stack[depth]){
					uncover(qnd.headerQNode);
					qnd = qnd.leftQNode;
				}
				
				HeaderQNode colNd = stack[depth].headerQNode;
				stack[depth] = stack[depth].lowerQNode;
				if(stack[depth] == colNd){
					uncover(colNd);
					stack[depth--] = null;
					continue;
				}
				qnd = stack[depth].rightQNode;
				while(qnd != stack[depth]){
					cover(qnd.headerQNode);
					qnd = qnd.rightQNode;
				}
				depth++;
				currentlyValid = true;
			}
		}
		
		System.out.printf("total solution count : %d\n", solutionCnt);
	}
	
	public class SolutionFormatter implements QNodeStackFormatter {
		public String qNodeStackFormat(QNode[] stack, int depth){
			// System.out.println("stack length : "+stack.length);
			String sfm = "";
			for(int d=0; d<depth; d++){
				QNode solNd = stack[d];
				sfm = sfm.concat(Integer.toString(solNd.indicator) + " -> ");
				do{
					sfm = sfm.concat(solNd.headerQNode.name + " ");
					solNd = solNd.rightQNode;
				}while(solNd != stack[d]);
				sfm = sfm.concat("\n");
			}
			return sfm;
		}
	}
}
