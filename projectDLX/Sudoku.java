package projectDLX;

import dancingLinkX.*;
import java.util.Scanner;

public class Sudoku extends ToricLinkedList {
	
	public static int[][] sdkArray = new int[9][9];
	
	public static class SudokuSolutionFormatter implements QNodeStackFormatter {
		public String qNodeStackFormat(QNode[] stack, int depth){
			if(stack!=null){
				for(int d=0; d<depth; d++){
					int ind = stack[d].indicator;
					sdkArray[(ind%81)/9][ind%9] = ind/81 + 1;
				}
			}
			
			String sfm = "┏━━━┯━━━┯━━━┳━━━┯━━━┯━━━┳━━━┯━━━┯━━━┓\n";
			for(int r=0; r<sdkArray.length; r++){
				for(int c=0; c<sdkArray[r].length; c++){
					String numStr = sdkArray[r][c] == 0 ? " " : Integer.toString(sdkArray[r][c]);
					if(c%3==0){
						sfm = sfm.concat("┃ "+numStr+" ");
					}else{
						sfm = sfm.concat("│ "+numStr+" ");
					}
				}sfm = sfm.concat("┃\n");
				if(r==sdkArray.length-1){
					sfm = sfm.concat("┗━━━┷━━━┷━━━┻━━━┷━━━┷━━━┻━━━┷━━━┷━━━┛\n");
				}else if(r%3==2){
					sfm = sfm.concat("┣━━━┿━━━┿━━━╋━━━┿━━━┿━━━╋━━━┿━━━┿━━━┫\n");
				}else{
					sfm = sfm.concat("┠───┼───┼───╂───┼───┼───╂───┼───┼───┨\n");
				}
			}sfm = sfm.concat("\n");
			return sfm;
		}
	}
	
	@Override
	public void searchSolution(QNodeStackFormatter qnsfm, int maxPrint){
		super.searchSolution(qnsfm, maxPrint);
	}
	
	public static void main(String[] args){
		final boolean T = true;
		// final boolean F = false;
		final int[][] CELL_BOX = {
			{0,0,0,1,1,1,2,2,2},
			{0,0,0,1,1,1,2,2,2},
			{0,0,0,1,1,1,2,2,2},
			{3,3,3,4,4,4,5,5,5},
			{3,3,3,4,4,4,5,5,5},
			{3,3,3,4,4,4,5,5,5},
			{6,6,6,7,7,7,8,8,8},
			{6,6,6,7,7,7,8,8,8},
			{6,6,6,7,7,7,8,8,8}
		};
		
		String name = "Test problem : Sudoku";
		String[] colNames = new String[4*9*9];
		
		for(int r=0; r<9; r++){
			for(int c=0; c<9; c++){
				colNames[9*r+c] = String.format("(%d,%d) filled", r, c);
			}
		}
		for(int n=1; n<=9; n++){
			for(int a=0; a<9; a++){
				colNames[81+9*(n-1)+a] = String.format("%d in row %d", n, a);
				colNames[162+9*(n-1)+a] = String.format("%d in col %d", n, a);
				colNames[243+9*(n-1)+a] = String.format("%d in box %d", n, a);
			}
		}
		
		boolean[][] sdkBoard = new boolean[9*9*9][colNames.length];
		
		int sdkInd = 0;
		for(int n=1; n<=9; n++){
			for(int r=0; r<9; r++){
				for(int c=0; c<9; c++){
					sdkBoard[sdkInd][9*r+c] = T;
					sdkBoard[sdkInd][81+9*(n-1)+r] = T;
					sdkBoard[sdkInd][162+9*(n-1)+c] = T;
					sdkBoard[sdkInd][243+9*(n-1)+CELL_BOX[r][c]] = T;
					sdkInd++;
				}
			}
		}
		
		boolean[] whichPrimary = new boolean[colNames.length];
		for(int i=0; i<whichPrimary.length; i++){
			whichPrimary[i] = T;
		}
		
		
		Scanner sc = new Scanner(System.in);
		
		System.out.println("Input the sudoku in one string of length 81");
		System.out.print(">>> ");
		String rawSudoku = sc.nextLine();
		while(rawSudoku.length() != 81){
			System.out.printf("wrong length : %d; try again\n", rawSudoku.length());
			System.out.print(">>> ");
			rawSudoku = sc.nextLine();
		}
		
		System.out.println("\ninput puzzle : ");
		for(int r=0; r<9; r++){
			for(int c=0; c<9; c++){
				int cellNum = (int)rawSudoku.charAt(9*r+c) - '0';
				if(cellNum < 1 || cellNum > 9){
					sdkArray[r][c] = 0;
					// System.out.print(" ");
				}else{
					sdkArray[r][c] = cellNum;
					// System.out.print(cellNum);
				}
			}// System.out.println();
		}
		
		System.out.printf(new SudokuSolutionFormatter().qNodeStackFormat(null, 0));
		// parse : String -> ToricLinkedList
		
		ToricLinkedList sudoku = new ToricLinkedList(name, colNames, whichPrimary, sdkBoard);
		// sudoku.testPrint();
		
		HeaderQNode colNd = sudoku.root;
		for(int i=0; i<rawSudoku.length(); i++){
			colNd = colNd.rightQNode;
			
			if(sdkArray[i/9][i%9] == 0) continue;
			
			int cellNum = sdkArray[i/9][i%9];
			
			sudoku.cover(colNd);
			
			QNode numNd = colNd.lowerQNode;
			while(numNd.indicator/81 != cellNum-1){
				numNd = numNd.lowerQNode;
				// System.out.println(numNd.indicator);
			}// System.out.println("--------------");
			
			QNode qnd = numNd.rightQNode;
			while(qnd != numNd){
				// System.out.println(qnd.headerQNode.name);
				sudoku.cover(qnd.headerQNode);
				qnd = qnd.rightQNode;
			}// System.out.println("/////////////");
		}
		
		// parse complete
		
		long solveStart = System.currentTimeMillis();
		sudoku.searchSolution(new SudokuSolutionFormatter(), 1);
		long solveFinish = System.currentTimeMillis();
		System.out.printf("total %4.3f taken", (solveFinish-solveStart)/1000.0);
		
		sc.close();
	}
}
