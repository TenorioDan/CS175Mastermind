import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

//Author: Joey Shepard 58868407
public class FiveGuessSolver {
	
	public int numColors, numSlots, numIterations, totalGuesses, bestEval, shortestGame, longestGame, currentEval;
	public boolean spectateGame, guessAgain, firstGuess;
	public ArrayList<Color> answer, bestGuess;
	public ArrayList<Integer> slotChoices;
	public ArrayList<Color> colorChoices;
	public ArrayList<ArrayList<Color>> historyOfGuesses, alreadyVisitedStates;
	private Set<ArrayList<Color>> masterSet, sSet;
	
	public FiveGuessSolver(boolean spectateGame, int numIterations)
	{
		masterSet = new HashSet<ArrayList<Color>>();
		this.numColors = 6;
		this.numIterations = numIterations;
		this.spectateGame = spectateGame;
		this.numSlots = 4;	
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
		populateMasterSets();
		
		for (int i = 0; i < numIterations; i++)
		{			
			if (spectateGame)
				System.out.println("Game #:" + (i + 1) + "\n----------------------------------------------");
			
			resetColorChoices();
			//get new answer for new game
			answer = getNewAnswer();

			//clear history in between games
			historyOfGuesses.clear();			
		
			resetSlotChoices();	
			firstGuess = false;
			
			//game loop
			while (true)
			{
				ArrayList<Color> computerGuess = new ArrayList<Color>();
				
				if (firstGuess)
				{
					Color one = colorChoices.get((int)(Math.random() * colorChoices.size()));
					Color two = colorChoices.get((int)(Math.random() * colorChoices.size()));
					while (two == one)
						two = colorChoices.get((int)(Math.random() * colorChoices.size()));
					
						computerGuess.add(one); computerGuess.add(one);
						computerGuess.add(two); computerGuess.add(two);
					firstGuess = false;					
				}
				
				else
				{
					//////////////BEGIN STEP 5///////////////
					//remove set s from master set if its code when compared to the guess returns the evaluation
					ArrayList<ArrayList<Color>> toBeRemoved = new ArrayList<ArrayList<Color>>();
					for (ArrayList<Color> c : masterSet)
						if (evaluateGuess(c, computerGuess) != currentEval)
							toBeRemoved.add(c);
					
					for (ArrayList<Color> c2 : toBeRemoved)
						sSet.remove(c2);
					
					//////////////END STEP 5///////////////
					
					for (ArrayList<Color> c : masterSet)
					{
						
					}
					
					
				}
				
				currentEval = evaluateGuess(computerGuess, answer);
				masterSet.remove(computerGuess);
				
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
		
	public int evaluateGuess(ArrayList<Color> guess, ArrayList<Color> answer)
	{
		ArrayList<Color> guessCopy = new ArrayList<Color>(guess);
		ArrayList<Color> answerCopy = new ArrayList<Color>(answer);
		
		int blackPegs = 0;
		int whitePegs = 0;
		int numMatches = 0;
		
		for (int i = 0; i < numSlots; i++)
		{
			//Right Color, Right Position
			if (guess.get(i) == answer.get(i))
			{
				answerCopy.remove(i - numMatches);
				guessCopy.remove(i - numMatches);
				numMatches++;
				blackPegs++;			
			}
		}	
		
		for (int i = 0; i < answerCopy.size(); i++)
		{
			//Right Color, Wrong Position
			if (guessCopy.contains(answerCopy.get(i)))
			{
				guessCopy.remove(answerCopy.get(i));
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
	
	private void populateMasterSets()
	{		
		for (int i = 0; i < numColors; i++)
		{			
			for (int j = 0; j < numColors; j++)
			{
				for (int k = 0; k < numColors; k++)
				{
					for (int l = 0; l < numColors; l++)
					{
						ArrayList<Color> temp = new ArrayList<Color>();
						temp.add(Color.values()[i]);
						temp.add(Color.values()[j]);
						temp.add(Color.values()[k]);
						temp.add(Color.values()[l]);
						masterSet.add(temp);
					}
				}
			}
		}		
		sSet = new HashSet<ArrayList<Color>>(masterSet);
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
