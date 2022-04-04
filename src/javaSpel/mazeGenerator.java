package javaSpel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class mazeGenerator {

	int width;
	int height;
	
	private static final Random rand = new Random();
	
	int [][] mazeMatrix;

	public mazeGenerator(int width, int height) {
		this.width = width;
		this.height = height;
		
		this.mazeMatrix = new int[height * 2 + 1][width * 2 + 1];
	}
	
	public void generate() {
		List<Integer> visited = new ArrayList<>();
		List<Vector2I> toVisit = new ArrayList<>();
			
		for(int i = 0;i<height;i++) {
			
			
			int [] row = new int[width * 2 + 1]; 
			for(int j = 0;j<width * 2 + 1;j++) {
				row[j] = 1;
				
		}
		
			mazeMatrix[i * 2] = row;
			
			int [] row2 = new int[width * 2 + 1]; 
			
			for(int j = 0;j<width * 2 + 1;j++) {
				if(j % 2 == 0) {
				row2[j] = 1;
				}
				else
				{
					row2[j] = 0;
				}
		}
			mazeMatrix[i * 2 + 1] = row2; 
		}
		
		int [] row3 = new int[width * 2 + 1]; 
		for(int j = 0;j<width * 2 + 1;j++) {
			row3[j] = 1;
			
	}
	
		mazeMatrix[height * 2 ] = row3;
		
		
		visited.add(0);
		toVisit.add(new Vector2I(0,1));
		toVisit.add(new Vector2I(0,width));
		
		while(toVisit.size() > 0)
		{
		int randomIndex	= rand.nextInt(toVisit.size());
		Vector2I nextPath = toVisit.remove(randomIndex);
		
		
		
		
		if(visited.contains(nextPath.end))
		{
			continue;
		}
		
		int pathDifference = nextPath.end-nextPath.start;
		
		if (pathDifference == 1) { // Rechts
			mazeMatrix[(int)( nextPath.start / width) * 2 + 1][(nextPath.start % width + 1) * 2] = 0;
		} else if (pathDifference == -1) { // Links
			mazeMatrix[(int)( nextPath.start / width) * 2 + 1][(nextPath.start % width - 1) * 2] = 0;
		} else if (pathDifference == width) { // Onder
			mazeMatrix[(int)( nextPath.start / width) * 2 + 2][(nextPath.start % width) * 2 + 1] = 0;
		} else if (pathDifference == -width) { // Boven
			mazeMatrix[(int)( nextPath.start / width) * 2][(nextPath.start % width) * 2 + 1] = 0;
		}
		
		visited.add(nextPath.end);
		
		
		int above = nextPath.end - width;
		if(above > 0 && !visited.contains(above))
		{
				toVisit.add(new Vector2I(nextPath.end, above));
		}
		
		
		
		
		int left = nextPath.end - 1;
		if(left % width != width-1&& !visited.contains(left))
		{
				toVisit.add(new Vector2I(nextPath.end, left));
		}
		
		
		
		
		int right = nextPath.end + 1;
		if(right % width != 0 && !visited.contains(right))
		{
				toVisit.add(new Vector2I(nextPath.end, right));
		}
		
		
		
		int below = nextPath.end + width;
		if( below < width * height && !visited.contains(below))
		{
				toVisit.add(new Vector2I(nextPath.end, below));
		}
		
		
		
		}
		
		
		}
	
	public int[][] getMatrix() {
		return mazeMatrix;
	}
	
	public void printMazeMatrix() {
		for (int i = 0; i < mazeMatrix.length; i++) { //this equals to the row in our matrix.
	         for (int j = 0; j < mazeMatrix[i].length; j++) { //this equals to the column in each row.
	            System.out.print(mazeMatrix[i][j] + " ");
	         }
	         System.out.println(); //change line on console as row comes to end in the matrix.
	      }
	}
	
	
}
