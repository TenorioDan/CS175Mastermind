//Author: Joey Shepard 58868407

import java.util.Scanner;

public class GameManager {
	
	public static void main(String[] args)
	{
		Scanner s = new Scanner(System.in);
		int numSlots = 5;
		int numColors = 5;
		int numIterations = 1000;
		boolean spectateGame = false;
		
		for(;;)
		{
			System.out.println("Enter the number of slots for the game: ");
			try{
				numSlots = s.nextInt();
				if (numSlots < 1)
					System.out.println("ERROR: Please enter an INTEGER value greater than 0.");
				else
					break;
			}catch(Exception e)
			{
				s.nextLine();
				System.out.println("ERROR: Please enter an INTEGER value.");
			}
		}
		
		for(;;)
		{
			System.out.println("Enter the number of colors for the game: (MAX COLORS = 10)");
			try{
				numColors = s.nextInt();
				if (numColors < 1 || numColors > 10)
					System.out.println("ERROR: Please enter an INTEGER value Between 1-10.");
				else
					break;
			}catch(Exception e)
			{
				s.nextLine();
				System.out.println("ERROR: Please enter an INTEGER value Between 1-10.");
			}
		}
		
		for(;;)
		{
			System.out.println("Enter \"true\" to print the guess and feedback each turn, \"false\" otherwise: ");
			try{
			spectateGame = s.nextBoolean();
			break;
			}catch(Exception e)
			{
				s.nextLine();
				System.out.println("ERROR: Please enter a BOOLEAN value (true or false).");
			}
		}
		
		for(;;)
		{
			System.out.println("Enter the number of game loop iterations: ");
			try{
			numIterations = s.nextInt();
			if (numIterations < 1)
				System.out.println("ERROR: Please enter an INTEGER value greater than 0.");
			else
				break;
			}catch(Exception e)
			{
				s.nextLine();
				System.out.println("ERROR: Please enter an INTEGER value greater than 0.");
			}
		}
		s.close();
		
		SimulationState simState = new SimulationState(numColors, numSlots, spectateGame, numIterations);
		simState.runSimulation();
		simState.printOutput();		

	}
}
