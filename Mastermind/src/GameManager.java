//Author: Joey Shepard 58868407

import java.util.Scanner;

//GameManager handles the user input (# games, algorithm, slot #, color) and runs the simulation
public class GameManager {
	
	public static void main(String[] args)
	{
		//Scanner is used for keyboard input from the user
		Scanner s = new Scanner(System.in);
		
		//default values, these will be overwritten by user when prompted in console
		int numSlots = 5;
		int numColors = 5;
		int numIterations = 1000;
		int algorithmIndex = 1;
		boolean spectateGame = false;
		
		//BEGIN algorithm prompt
		for(;;)
		{
			System.out.println("Enter the index of the desired algorithm: ");
			System.out.println("1) 5-guess Algorithm");
			System.out.println("2) Human Behavior Algorithm");
			System.out.println("3) Genetic Algorithm");
			
			try{
				algorithmIndex = s.nextInt();
			if (algorithmIndex < 1 || algorithmIndex > 3)
				System.out.println("ERROR: Please enter an INTEGER value between 1-3.");
			else
				break;
			}catch(Exception e)
			{
				s.nextLine();
				System.out.println("ERROR: Please enter an INTEGER value between 1-3.");
			}
		}
		//END algorithm prompt
		
		//the human algorithm has a fixed slot and color count (4slots x 6colors)
		if (algorithmIndex != 2)
		{
			//BEGIN slot count prompt
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
			// END slot count prompt
			
		    //BEGIN color count prompt
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
			//END color count prompt
		}//END human if
		
		if (algorithmIndex != 2)
		{
			//BEGIN 'print log during simulation' prompt
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
			//END 'print log during simulation' prompt
		}//END human if
		
		
		//BEGIN game loop count prompt
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
		//END game loop count prompt
		
		//close scanner for safe execution
		s.close();
		
		//genetic
		if (algorithmIndex == 3)
		{
			SimulationStateGenetic simStateGenetic = new SimulationStateGenetic(numColors, numSlots, spectateGame, numIterations);
		 	simStateGenetic.runSimulation();
		 	simStateGenetic.printOutput();
		}
		
		//human
		else if (algorithmIndex == 2)
		{
			GridSolver gridSolver = new GridSolver(numIterations);
			gridSolver.RunSimulation();
		}
		
		//five guess
		else if (algorithmIndex == 1)
		{
			//for (int slot = 2; slot < 10; slot++)
			//{
			//	for (int color = 2; color <= 10; color++)
			//	{
			//		if (color + slot < 15)
			//		{
			//			FiveGuessSolver solver = new FiveGuessSolver(slot, color, spectateGame, numIterations);
			//			solver.runSimulation();
			//			solver.printAvgOnly();
			///			System.out.print(" ");		
			//		}
			//	}
			//	System.out.println();
			//}
			
			FiveGuessSolver solver = new FiveGuessSolver(numSlots, numColors, spectateGame, numIterations);			
			solver.runSimulation();
			solver.printOutput();	
		}
			

	}
}