package projectDLX;

import dancingLinkX.*;

public class NQueens extends ToricLinkedList {
	
	public static class NQueensSolutionFormatter implements QNodeStackFormatter {
		public String qNodeStackFormat(QNode[] stack, int depth){
			boolean[][] board = new boolean[depth][depth];
			for(int d=0; d<depth; d++){
				// System.out.println(d);
				int ind = stack[d].indicator;
				board[ind/depth][ind%depth] = true;
			}
			
			String sfm = "";
			for(int i=0; i<board.length; i++){
				for(int j=0; j<board[i].length; j++){
					sfm = sfm.concat(board[i][j] ? "Q" : ".");
				}sfm = sfm.concat("\n");
			}
			return sfm;
		}
	}
	
	@Override
	public void searchSolution(QNodeStackFormatter qnsfm){
		super.searchSolution(qnsfm);
	}
	
	public static void main(String[] args){
		final int N = 5;
		final boolean T = true;
		// final boolean F = false;
		
		String name = String.format("Test problem : %d Queens", N);
		String[] colNames = new String[6*N-2];
		int ind = 0;
		for(int i=0; i<N; i++){ // 0~3, horizontal
			colNames[ind++] = String.format("R%d", i);
		}for(int i=0; i<N; i++){ // 4~7, vertical
			colNames[ind++] = String.format("C%d", i);
		}for(int i=0; i<2*N-1; i++){ // 8~14, lu-rd diagonal
			colNames[ind++] = String.format("D%d", i);
		}for(int i=0; i<2*N-1; i++){ // 15~21, ru-ld diagonal
			colNames[ind++] = String.format("V%d", i);
		}
		
		boolean[] whichPrimary = new boolean[colNames.length];
		for(int i=0; i<2*N; i++){
			whichPrimary[i] = true;
		}
		
		boolean[][] constraints = new boolean[N*N][];
		for(int i=0; i<constraints.length; i++){
			constraints[i] = new boolean[colNames.length];
			int row = i/N, col = i%N, lurd = N-1-row+col, ruld = row+col;
			constraints[i][row] = T;
			constraints[i][N+col] = T;
			constraints[i][2*N+lurd] = T;
			constraints[i][4*N-1+ruld] = T;
		}
		
		ToricLinkedList nQueens = new ToricLinkedList(name, colNames, whichPrimary, constraints);
		
		// nQueens.testPrint();
		// nQueens.searchSolution();
		nQueens.searchSolution(new NQueensSolutionFormatter());
	}
}
