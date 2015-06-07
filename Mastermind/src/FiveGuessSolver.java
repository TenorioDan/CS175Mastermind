import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//Author: Joey Shepard 58868407
public class FiveGuessSolver {
	
	public int numColors, numSlots, numIterations, totalGuesses, bestEval, shortestGame, longestGame, currentEval;
	public boolean spectateGame, firstGuess;
	public ArrayList<Color> answer, bestGuess;
	public ArrayList<Color> colorChoices;
	public ArrayList<ArrayList<Color>> historyOfGuesses;
	private ArrayList<ArrayList<Color>> masterSet, sSet;
	private Map<Integer, Integer> evalToHitCount;
	private Map<ArrayList<Color>, Integer> codeToEval;
	private Map<ArrayList<Color>, Integer> guessScores;
	
	public FiveGuessSolver(int numSlots, int numColors, boolean spectateGame, int numIterations)
	{
		this.numColors = numColors;
		this.numIterations = numIterations;
		this.spectateGame = spectateGame;
		this.numSlots = numSlots;	
		totalGuesses = 0;
		bestEval = -1;
		colorChoices = new ArrayList<Color>();
		answer = new ArrayList<Color>();
		historyOfGuesses = new ArrayList<ArrayList<Color>>();
		guessScores = new HashMap<ArrayList<Color>, Integer>();
		codeToEval =  new HashMap<ArrayList<Color>, Integer>();
		evalToHitCount = new HashMap<Integer, Integer>();
	}
	
	public void runSimulation()
	{		
		for (int i = 0; i < numIterations; i++)
		{			
			masterSet = new ArrayList<ArrayList<Color>>();
					
			if (spectateGame)
				System.out.println("Game #" + (i + 1) + ":\n----------------------------------------------");
			
			dynamicSetAllocation();
			setColors();
			//get new answer for new game
			answer = getNewAnswer();
			bestEval = 0;
			//clear history in between games
			historyOfGuesses.clear();			
		
			firstGuess = true;			
			
			//game loop
			while (true)
			{
				ArrayList<Color> computerGuess = new ArrayList<Color>();
				
				if (firstGuess)
				{
					//Color one = colorChoices.get(0);
					//Color two = colorChoices.get(1);					
					//computerGuess.add(one); computerGuess.add(one);
					//computerGuess.add(two); computerGuess.add(two);
					int slotNum = numSlots / 2 == 0 ? numSlots / 2 : (numSlots + 1) / 2;
					
					for (int y = 0; y < numSlots; y++)
					{
						if (y < slotNum)
							computerGuess.add(colorChoices.get(0));
						else
							computerGuess.add(colorChoices.get(1));
							
					}					
					firstGuess = false;					
				}
				
				else
				{
					guessScores.clear();
					codeToEval.clear();
					evalToHitCount.clear();
					
					//////////////BEGIN STEP 5///////////////
					//remove pattern c from master set if its code when compared to the guess returns the evaluation
					ArrayList<ArrayList<Color>> toBeRemoved = new ArrayList<ArrayList<Color>>();
					for (ArrayList<Color> c : masterSet)
						if (evaluateGuess(c, historyOfGuesses.get(historyOfGuesses.size() - 1)) != currentEval)
							toBeRemoved.add(c);
					
					for (ArrayList<Color> c : toBeRemoved)
						sSet.remove(c);
					
					//////////////END STEP 5///////////////
					
					
					//////////BEGIN STEP 6/////////////////
					//Find the number of possible eliminations in S for each code in masterSet			
					for (ArrayList<Color> codeInMS: sSet)					
					{
						int tempEvalMS = evaluateGuess(codeInMS, historyOfGuesses.get(historyOfGuesses.size() - 1));
						int hitCountinS = 0;
						codeToEval.put(codeInMS, tempEvalMS);
						if (!evalToHitCount.keySet().contains(tempEvalMS))
						{
							//if the code would eliminate a code in s
							for (ArrayList<Color> codeInS: sSet)					
							{		
								int tempEvalS = evaluateGuess(codeInS, historyOfGuesses.get(historyOfGuesses.size() - 1));
								if (tempEvalS == tempEvalMS)
									hitCountinS++;														
							}
							evalToHitCount.put(tempEvalMS, hitCountinS);	
						}
						
						
					}
					//int numElim = 0;
					for (ArrayList<Color> codeInS: masterSet)					
					{
//						for(ArrayList<Color> c: sSet)					
//						{
//							if (evalToHitCount.get(codeToEval.get(codeInS)) != null
//									&& codeToEval.get(codeInS) == codeToEval.get(c))
//							{
//								numElim++;
//							}
//						}
						if (evalToHitCount.get(codeToEval.get(codeInS)) == null)
							guessScores.put(codeInS, 0);
						else
							guessScores.put(codeInS, sSet.size() - evalToHitCount.get(codeToEval.get(codeInS)));
						//guessScores.put(codeInS, sSet.size() - numElim);
					}
					
					ArrayList<ArrayList<Color>> nextGuesses = new ArrayList<ArrayList<Color>>();
					
					int maxScore = -1;
					for (ArrayList<Color> code: guessScores.keySet())
					{
						int score = guessScores.get(code);
						if (score > maxScore)
						{
							maxScore = score;
						}
					}
					
					for (ArrayList<Color> code: guessScores.keySet())
					{
						if (guessScores.get(code) == maxScore)
							nextGuesses.add(code);
					}
					
					ArrayList<ArrayList<Color>> bestGuesses = new ArrayList<ArrayList<Color>>();
					for (ArrayList<Color> c: nextGuesses)
					{
						if (sSet.contains(c))
							bestGuesses.add(c);
					}
					
					if (!bestGuesses.isEmpty())
					{	
						for (ArrayList<Color> codeInS: sSet)	
						{					
							if (bestGuesses.contains(codeInS))
							{
								computerGuess = codeInS;
								break;
							}
						}
						//computerGuess = bestGuesses.get(0);
					}
					else
					{
						for (ArrayList<Color> codeInMS: masterSet)	
						{					
							if (nextGuesses.contains(codeInMS))
							{
								computerGuess = codeInMS;
								break;
							}
						}
						//computerGuess = nextGuesses.get(0);	
					}
					///////////END STEP 6/////////////////
				}
				
				currentEval = evaluateGuess(computerGuess, answer);
				masterSet.remove(computerGuess);
				sSet.remove(computerGuess);
				
				historyOfGuesses.add(computerGuess);
				
				if (currentEval > bestEval)
					bestEval = currentEval;
			
				if (spectateGame)
					System.out.println("Guess #" + historyOfGuesses.size() + ": " + computerGuess 
							+ " ANSWER: " + answer + " Current Eval: " + currentEval + " || Best Eval: " + bestEval);
				
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
	
	public void setColors()
	{
		colorChoices.clear();
		for (int i = 0; i < numColors; i++)
			colorChoices.add(Color.values()[i]);
	}
	
	//private void populateMasterSets()
	//{		
	//	for (int i = 0; i < numColors; i++)
	//	{			
	//		for (int j = 0; j < numColors; j++)
	//		{
	//			for (int k = 0; k < numColors; k++)
	//			{
	//				for (int l = 0; l < numColors; l++)
	//				{
	//					ArrayList<Color> temp = new ArrayList<Color>();
	//					temp.add(Color.values()[i]);
	//					temp.add(Color.values()[j]);
	//					temp.add(Color.values()[k]);
	//					temp.add(Color.values()[l]);
	//					masterSet.add(temp);
	//				}
	//			}
	//		}
	//	}		
	//	sSet = new ArrayList<ArrayList<Color>>(masterSet);
	//}
	
	public void dynamicSetAllocation()
	{
		masterSet = new ArrayList<ArrayList<Color>>();
		setColors();
		int cardinality = (int)Math.pow(numColors, numSlots);
		for (int i = 0; i < cardinality; i++)
		{
			masterSet.add(new ArrayList<Color>());
		}
		populateMasterSets(0, cardinality);
		
		sSet = new ArrayList<ArrayList<Color>>(masterSet);
		
		//for (ArrayList<Color> a : masterSet)
		//{
		//	for (Color c : a)
		//	{
		//		System.out.print(c + " ");
		//	}
		//	System.out.println();
		//}
		//System.out.println(masterSet.size());
	}
	
	private void populateMasterSets(int startIndex, int cardinality)
	{		
		if (cardinality > 0)
		{
			//add the next color to each code		
			
				for (int q = 0; q < numColors; q++)
				{
					for (int i = startIndex + q * cardinality / numColors; i < startIndex + q * cardinality / numColors + cardinality / numColors; i++)
					{
						//System.out.println(i);
						//System.out.println(startIndex + q * cardinality / numColors + cardinality / numColors);
						masterSet.get(i).add(Color.values()[q]);				
					}
				}
			
			
			//recurse
			for (int j = 0; j < numColors; j++)
			{
				//int starter = j * cardinality / numColors == 0 ? j * cardinality / numColors : j * cardinality / numColors - 1;
				populateMasterSets(startIndex + j * cardinality / numColors, cardinality / numColors);
			}			
		}
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
	
	public void printAvgOnly()
	{
		System.out.print((double)((double)totalGuesses / (double) numIterations));
	}
}
