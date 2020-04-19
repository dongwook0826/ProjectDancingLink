package test.dlx;

import dancingLinkX.*;

public class DancingLinksTest {
	public static void main(String[] args){
		final boolean T = true;
		final boolean F = false;
		
		String name = "Test problem : exact cover";
		String[] colNames = {"A","B","C","D","E","F","G"};
		boolean[] whichPrimary = {T,T,T,F,F,T,F};
		// String[] rowNames = {"0","1","2","3","4","5","6","7"};
		boolean[][] constraints = {
			{F,F,T,F,F,T,F},
			{F,F,T,F,T,T,F},
			{T,T,F,F,F,F,F},
			{T,F,F,T,F,F,T},
			{F,T,T,F,F,T,F},
			{T,F,F,T,F,F,F},
			{F,T,F,F,F,F,T},
			{F,F,F,T,T,F,T}
		};
		
		ToricLinkedList problem = new ToricLinkedList(name, colNames, whichPrimary, constraints);
		
		// problem.testPrint();
		problem.searchSolution();
	}
}