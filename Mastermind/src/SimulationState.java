import java.util.*;
//gitignore test 2
//Author: Joey Shepard 58868407
public class SimulationState {
	
	public int numColors, numSlots, numIterations, totalGuesses, bestEval, shortestGame, longestGame;
	public boolean spectateGame, guessAgain;
	public ArrayList<Color> answer, bestGuess;
	public ArrayList<Integer> slotChoices;
	public ArrayList<Color> colorChoices;
	public ArrayList<ArrayList<Color>> historyOfGuesses, alreadyVisitedStates;
	
	public SimulationState(int numSlots, int numColors, boolean spectateGame, int numIterations)
	{
		this.numColors = numColors;
		this.numIterations = numIterations;
		this.spectateGame = spectateGame;
		this.numSlots = numSlots;	
		totalGuesses = 0;
		bestEval = 0;
		slotChoices = new ArrayList<Integer>();
		colorChoices = new ArrayList<Color>();
		answer = new ArrayList<Color>();
		bestGuess = new ArrayList<Color>();
		historyOfGuesses = new ArrayList<ArrayList<Color>>();
		alreadyVisitedStates = new ArrayList<ArrayList<Color>>();
	}
	
	public void runSimulation()
	{
		for (int i = 0; i < numIterations; i++)
		{			
			if (spectateGame)
				System.out.println("Game #:" + (i + 1) + "\n----------------------------------------------");
			
			resetColorChoices();
			//get new answer for new game
			answer = getNewAnswer();

			//clear history in between games
			historyOfGuesses.clear();			
			bestGuess.clear();
			alreadyVisitedStates.clear();
			bestEval = 0;
			resetSlotChoices();			
			guessAgain = true;
			
			//game loop
			while (true)
			{
				ArrayList<Color> computerGuess = calculateGuess();		
				historyOfGuesses.add(computerGuess);
				alreadyVisitedStates.add(computerGuess);
				
				if (spectateGame)
					System.out.println("Guess #" + historyOfGuesses.size() + ": " + computerGuess 
							+ " ANSWER: " + answer + " Best Eval: " + bestEval);
				
				
				//game is over if true
				if (computerGuess.equals(answer))
					break;
			}
			
			if (i == 0)
			{
				shortestGame = historyOfGuesses.size();
				longestGame = historyOfGuesses.size();
			}
			
			if (historyOfGuesses.size() < shortestGame)
				shortestGame = historyOfGuesses.size();
			
			if (historyOfGuesses.size() > longestGame)
				longestGame = historyOfGuesses.size();
			
			totalGuesses += historyOfGuesses.size();
			if (spectateGame)
				System.out.println();
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Color> calculateGuess()
	{
		if (guessAgain)
		{
			guessAgain = false;
			bestGuess =  getNewAnswer();
			bestEval = evaluateGuess(bestGuess, answer);
			if (bestEval == 0)
			{
				for (Color c : bestGuess)
					colorChoices.remove(c);
				guessAgain = true;						
			}
			return bestGuess;
		}
		
		else
		{			
			while (true)
			{
				ArrayList<Color> bestGuessTest = (ArrayList<Color>)bestGuess.clone();
				int randomIndex = slotChoices.get((int)(Math.random() * slotChoices.size()));
				Color randomColor = colorChoices.get((int)(Math.random() * colorChoices.size()));
				bestGuessTest.set(randomIndex, randomColor); 
				
				if (!alreadyVisitedStates.contains(bestGuessTest))
				{
					alreadyVisitedStates.add(bestGuessTest);
				
					int tempEval = evaluateGuess(bestGuessTest, answer);
						
					if (tempEval - bestEval == -10)
						slotChoices.remove(slotChoices.indexOf(randomIndex));
					
					if (tempEval > bestEval)
					{
						if (tempEval - bestEval == 9 || tempEval - bestEval == 10)
							slotChoices.remove(slotChoices.indexOf(randomIndex));
						
						bestEval = tempEval;
						bestGuess = bestGuessTest;							
					}	
					
					
					
					return bestGuessTest;
				}				
			}
		}
		
	}
	
	public int evaluateGuess(ArrayList<Color> guess, ArrayList<Color> answer)
	{
		int blackPegs = 0;
		int whitePegs = 0;
		
		for (int i = 0; i < numSlots; i++)
		{
			//Right Color, Right Position
			if (guess.get(i) == answer.get(i))
			{
				blackPegs++;
			}
			
			//Right Color, Wrong Position
			else if (answer.contains(guess.get(i)))
			{				
				whitePegs++;
			}
		}
		
		return 10 * blackPegs + 1 * whitePegs;
	}
	
	public ArrayList<Color> getNewAnswer()
	{
		ArrayList<Color> answerToReturn = new ArrayList<Color>();
		
		for (int i = 0; i < numSlots; i++)
			answerToReturn.add(colorChoices.get((int)(Math.random() * colorChoices.size())));
		return answerToReturn;
	}
	
	public void resetSlotChoices()
	{
		slotChoices.clear();
		for (int i = 0; i < numSlots; i++)
			slotChoices.add(i);
	}
	
	public void resetColorChoices()
	{
		colorChoices.clear();
		for (int i = 0; i < numColors; i++)
			colorChoices.add(Color.values()[i]);
	}
	
	public void printOutput()
	{
		System.out.println("-----------------------------------------------------");
		System.out.println("Number of Slots: " + numSlots);
		System.out.println("Number of Colors: " + numColors);
		System.out.println("Total Guesses: " + totalGuesses);
		System.out.println("Total Games Played: " + numIterations);
		System.out.println("Average Guesses Per Game: " + (double)((double)totalGuesses / (double) numIterations));
		System.out.println("Shortest Game: " + shortestGame);
		System.out.println("Longest Game: " + longestGame);
	}
}
