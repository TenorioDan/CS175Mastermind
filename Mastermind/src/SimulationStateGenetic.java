import java.util.*;

//Author: Joey Shepard 58868407
public class SimulationStateGenetic {
	
	public int numColors;
	public static int numSlots;
	public int numIterations;
	public int totalGuesses;
	public int bestEval;
	public int shortestGame;
	public int longestGame;
	public boolean spectateGame, guessAgain;
	public static ArrayList<Color> answer;
	public ArrayList<Color> bestGuess;
	public ArrayList<Integer> slotChoices;
	public ArrayList<Color> colorChoices;
	public ArrayList<ArrayList<Color>> historyOfGuesses, alreadyVisitedStates;
	
	public SimulationStateGenetic(int numColors, int numSlots, boolean spectateGame, int numIterations)
	{
		this.numColors = numColors;
		this.numIterations = numIterations;
		this.spectateGame = spectateGame;
		SimulationStateGenetic.numSlots = numSlots;	
		totalGuesses = 0;
		bestEval = 0;
		slotChoices = new ArrayList<Integer>();
		colorChoices = new ArrayList<Color>();
		answer = new ArrayList<Color>();
		bestGuess = new ArrayList<Color>();
		historyOfGuesses = new ArrayList<ArrayList<Color>>();
		alreadyVisitedStates = new ArrayList<ArrayList<Color>>();
	}
	
	public static class Population {
		ArrayList<ArrayList<Color>> pop;
		int maxFitness = numSlots * 10;
		public Population(ArrayList<ArrayList<Color>> pop) {
			this.pop = pop;
			
		}
		
		//get the fittest individual
		public ArrayList<Color> getFittest() {
			int maxFitness = 0;
			for( int i = 0; i < pop.size(); i++ )
				if(evaluateGuess(pop.get(i),answer) > evaluateGuess(pop.get(maxFitness),answer))
					maxFitness = i;
			return pop.get(maxFitness);
		}
		
		//get the max fitness score of the fittest individual
		public int getMaxFitness() {
			int maxFitness = 0;
			for( int i = 0; i < pop.size(); i++ ) {
				int guess = evaluateGuess(pop.get(i),answer);
				if( guess > maxFitness)
					maxFitness = guess;
			}
			return maxFitness;
		}
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
			
			//population
			ArrayList<ArrayList<Color>> pop = new ArrayList<ArrayList<Color>>();
			Population population = new Population( pop );
			
			
			//game loop
			while (true)
			{
				//ArrayList<Color> computerGuess = calculateGuess();		
				ArrayList<Color> computerGuess = guess(population);
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
	
	//choose a random individual in our population
	public ArrayList<Color> chooseIndiv(Population population) {
		return population.pop.get((int)(Math.random() * population.pop.size()));
	}
	
	//mutate a random individual - change all of their ball colors
	public Population mutate(Population population) {
		ArrayList<Color> fittest = population.getFittest(), indiv = fittest;
			
		while(indiv == fittest)
			indiv = chooseIndiv(population);
		
		for( int i = 0; i < numSlots; i++ ) {
			indiv.set(i,colorChoices.get((int) (Math.random() * colorChoices.size())));
		}
		
		return population;
	}
	
	//create a new individual
	public Population crossover(Population population) {
		
		ArrayList<Color> newSol = new ArrayList<Color>(), indiv1 = population.getFittest(), indiv2 = chooseIndiv(population);
		for( int i = 0; i < numSlots; i++ ) {
			if(Math.random() <= 0.5)
				newSol.add(indiv1.get(i));
			else
				newSol.add(indiv2.get(i));
		}
		population.pop.add(newSol);
		return population;
	}

	//guess combination using genetic algorithm
	public ArrayList<Color> guess(Population population) {
		if (guessAgain)
		{
			guessAgain = false;
			for( int i = 0; i < 2; i++ ) {
				bestGuess =  getNewAnswer();
				bestEval = evaluateGuess(bestGuess, answer);
				if (bestEval == 0)
				{
					for (Color c : bestGuess)
						colorChoices.remove(c);
					guessAgain = true;						
				}
				population.pop.add(bestGuess);
			}
			return population.getFittest();
		}
		
		else
		{			
			while (population.getMaxFitness() < population.maxFitness)
			{
				
				population = crossover(population);
				population = mutate(population);
				
				ArrayList<Color> bestGuessTest = population.getFittest(); 
				
				if (!alreadyVisitedStates.contains(bestGuessTest) /*&& evaluateGuess(bestGuessTest,answer) > bestEval*/)
				{
					alreadyVisitedStates.add(bestGuessTest);
				
					int tempEval = evaluateGuess(bestGuessTest, answer);

					if(tempEval > bestEval) {
						bestEval = tempEval;
						bestGuess = bestGuessTest;
					}
					
					
					
					return bestGuessTest;
				}				
			}
			
			if(population.getMaxFitness() == population.maxFitness)
				return population.getFittest();
		
		}
		return null;
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
	
	public static int evaluateGuess(ArrayList<Color> guess, ArrayList<Color> answer)
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
