import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GridSolver {
	
	public int numColors;
	public static int numSlots;
	public int numIterations;
	public int totalGuesses;
	public int bestEval;
	public boolean spectateGame, guessAgain;
	public static ArrayList<Color> answer;
	public ArrayList<Color> colorChoices;
	
	private ArrayList<Color> notInAnswer;
	private int blackPegs;
	private int whitePegs;
	private int trialPos;
	private int fillInCode;
	private boolean isPlayer = false;
	private Color fill;
	private Color trial;
	private Grid grid;
	
	public GridSolver(boolean spectateGame, int numIterations, int numColors, int numSlots){
		this.numColors = numColors;
		this.numSlots = numSlots;
		this.numIterations = numIterations;
		this.spectateGame = spectateGame;
		
	}
	
	public void RunSimulation(){
		float iterations = 0;
		float average = 0;
		int fewestGuesses = 9999;
		int mostGuesses = 0;
		while (iterations < numIterations){
			//uncomment for debugging
			//iterations = numIterations;
			
			blackPegs = 0;
			whitePegs = 0;
			trialPos = 0;
			fillInCode = 0;
			colorChoices = new ArrayList<Color>();
			answer = new ArrayList<Color>();
			grid = new Grid(numColors);
			notInAnswer = new ArrayList<Color>();		
			boolean solved = false;
			boolean firstPosSolved = false, secondPosSolved = false, thirdPosSolved = false, fourthPosSolved = false;
			int currentTrial = 0;
			int currentFill = 0;
			
			ArrayList<Color> currentGuess = new ArrayList<Color>();
			blackPegs = 0;
			whitePegs = 0;
			
			resetColorChoices();
			answer = getNewAnswer();
			
			//first guess will be a fill of the first color choice
			for (int i = 0; i < numSlots; i++){
				currentGuess.add(colorChoices.get(currentFill));
			}
			
			blackPegs = 0;
			whitePegs = 0;
			checkGuess(currentGuess);
			
			if (blackPegs > 0){
				isPlayer = true;
				for (int i = 0; i < blackPegs; i++){
					grid.knowColor(colorChoices.get(currentFill));
				}
			}
			else{
				notInAnswer.add(colorChoices.get(currentFill));
			}
			
			//second guess will always FirstColor, SecondColor, SecondColor, SecondColor (1222)
			
			int guess = 1;
			
			while(!solved){
				guess++;
				//clear the guess list add the fill and trial colors appropriately
				
				if (!grid.row1Solved && grid.first != null)
					trial = grid.first;
				else if (!grid.row2Solved && grid.second != null)
					trial = grid.second;
				else if (!grid.row3Solved && grid.third != null)
					trial = grid.third;
				else if (!grid.row4Solved && grid.fourth != null)
					trial = grid.fourth;
				else if (!notInAnswer.isEmpty())
					trial = notInAnswer.get((int)Math.random() * notInAnswer.size());
				else
					trial = colorChoices.get(currentTrial);
				
				while (true){
					currentFill++;
					
					if (currentFill == 6){
						if (!notInAnswer.isEmpty())
							fill = notInAnswer.get((int)Math.random() * notInAnswer.size());
						currentFill = 0;
					}
					
					fill = colorChoices.get(currentFill);
					
					if (trial != fill)
						break;
				}
				
				//System.out.println(trial + " " + fill);
				
				isPlayer = grid.isInSecretCode(trial);
				
				if (!isPlayer){
					if (!firstPosSolved)
						trialPos = 0;
					else if (!secondPosSolved)
						trialPos = 1;
					else if (!thirdPosSolved)
						trialPos = 2;
					else if (!fourthPosSolved)
						trialPos = 3;
				}
				else{
					if (trial == grid.first && !grid.row1Solved){
						for (int i = 0; i < numSlots; i++){
							if (grid.rows[0][i] == Grid.PositionState.MA)
								trialPos = i;
						}
					}
					else if (trial == grid.second && !grid.row2Solved){
						for (int i = 0; i < numSlots; i++){
							if (grid.rows[1][i] == Grid.PositionState.MA)
								trialPos = i;
						}
					}
					else if (trial == grid.third && !grid.row3Solved){
						for (int i = 0; i < numSlots; i++){
							if (grid.rows[2][i] == Grid.PositionState.MA)
								trialPos = i;
						}
					}
					else if (trial == grid.fourth && !grid.row4Solved){
						for (int i = 0; i < numSlots; i++){
							if (grid.rows[3][i] == Grid.PositionState.MA)
								trialPos = i;
						}
					}
				}
				
				currentGuess.clear();
	
				for (int i = 0; i < numSlots; i++){
					if (i == trialPos){
						currentGuess.add(i, trial);
					}
					else{
						currentGuess.add(i, fill);
					}
				}
				
				System.out.println("\n" + guess + " " + currentGuess.get(0)
									+ " " + currentGuess.get(1)
									+ " " + currentGuess.get(2)
									+ " " + currentGuess.get(3)
									+ " ---- ANSWER---- " + answer.get(0)
									+ " " + answer.get(1)
									+ " " + answer.get(2)
									+ " " + answer.get(3));
				
				if (guess > 15)
					break;
				
				blackPegs = 0;
				whitePegs = 0;
				
				checkGuess(currentGuess);
				
				if (blackPegs + whitePegs == 0){
					if (!notInAnswer.contains(fill))
						notInAnswer.add(fill);
					if (!notInAnswer.contains(trial))
						notInAnswer.add(trial);
				}
				
				fillInCode = blackPegs + whitePegs;
				
				//The first question is simply a matter of counting the total
				//number of pegs and subtracting 1 if the Trial colour is a
				//player.
				if (isPlayer)
					fillInCode--;
				
				for (int i = 0; i < fillInCode; i++){
					grid.knowColor(fill);
				}
				
				
				//follow the the table about the white pegs
				if (whitePegs == 0){
					if (isPlayer){
						notInAnswer.add(fill);
						//trial in trialpos
						if (trial == grid.first && !grid.row1Solved){
							
							grid.row1Solved = true;
							
							for (int i = 0; i < numSlots; i++){
								if (i == trialPos){
									grid.rows[0][i] = Grid.PositionState.YES;
									grid.rows[1][i] = Grid.PositionState.NO;
									grid.rows[2][i] = Grid.PositionState.NO;
									grid.rows[3][i] = Grid.PositionState.NO;
								}
								else{
									grid.rows[0][i] = Grid.PositionState.NO;
								}
							}
						}
						else if (trial == grid.second && !grid.row2Solved){
							
							grid.row2Solved = true;
							
							for (int i = 0; i < numSlots; i++){
								if (i == trialPos){
									grid.rows[0][i] = Grid.PositionState.NO;
									grid.rows[1][i] = Grid.PositionState.YES;
									grid.rows[2][i] = Grid.PositionState.NO;
									grid.rows[3][i] = Grid.PositionState.NO;
								}
								else
									grid.rows[1][i] = Grid.PositionState.NO;
							}
						}
						else if (trial == grid.third && !grid.row3Solved){
							
							grid.row3Solved = true;
							
							for (int i = 0; i < numSlots; i++){
								if (i == trialPos){
									grid.rows[0][i] = Grid.PositionState.NO;
									grid.rows[1][i] = Grid.PositionState.NO;
									grid.rows[2][i] = Grid.PositionState.YES;
									grid.rows[3][i] = Grid.PositionState.NO;
								}
								else
									grid.rows[2][i] = Grid.PositionState.NO;
							}
						}
						else if (trial == grid.fourth && !grid.row4Solved){
							
							grid.row4Solved = true;
							
							for (int i = 0; i < numSlots; i++){
								if (i == trialPos){
									grid.rows[0][i] = Grid.PositionState.NO;
									grid.rows[1][i] = Grid.PositionState.NO;
									grid.rows[2][i] = Grid.PositionState.NO;
									grid.rows[3][i] = Grid.PositionState.YES;
								}
								else
									grid.rows[3][i] = Grid.PositionState.NO;
							}
						}
					}
					else{
						//fill not in trialpos
						if (grid.isInSecretCode(fill)){
							if (fill == grid.first){
								grid.rows[0][trialPos] = Grid.PositionState.NO;
							}
							else if (fill == grid.second){
								grid.rows[1][trialPos] = Grid.PositionState.NO;
							}
							else if (fill == grid.third){
								grid.rows[2][trialPos] = Grid.PositionState.NO;
							}
							else if (fill == grid.fourth){
								grid.rows[3][trialPos] = Grid.PositionState.NO;
							}
						}
					}
				}
				else if (whitePegs == 1){
					if (isPlayer){
						notInAnswer.add(fill);
						//neither in trialpos
						if (trial == grid.first && !grid.row1Solved){
							grid.rows[0][trialPos] = Grid.PositionState.NO;
						}
						else if (trial == grid.second && !grid.row2Solved){
							grid.rows[1][trialPos] = Grid.PositionState.NO;
						}
						else if (trial == grid.third && !grid.row3Solved){
							grid.rows[2][trialPos] = Grid.PositionState.NO;
						}
						else if (trial == grid.fourth && !grid.row4Solved){
							grid.rows[3][trialPos] = Grid.PositionState.NO;
						}
					}
					else{
						notInAnswer.add(trial);
						if (!grid.isInSecretCode(fill)){
							grid.knowColor(fill);
						}
						
						if (fill == grid.first && !grid.row1Solved){
							
							grid.row1Solved = true;
							
							for (int i = 0; i < numSlots; i++){
								if (i == trialPos){
									grid.rows[0][i] = Grid.PositionState.YES;
									grid.rows[1][i] = Grid.PositionState.NO;
									grid.rows[2][i] = Grid.PositionState.NO;
									grid.rows[3][i] = Grid.PositionState.NO;
								}
								else{
									grid.rows[0][i] = Grid.PositionState.NO;
								}
							}
						}
						else if (fill == grid.second && !grid.row2Solved){
							
							grid.row2Solved = true;
							
							for (int i = 0; i < numSlots; i++){
								if (i == trialPos){
									grid.rows[0][i] = Grid.PositionState.NO;
									grid.rows[1][i] = Grid.PositionState.YES;
									grid.rows[2][i] = Grid.PositionState.NO;
									grid.rows[3][i] = Grid.PositionState.NO;
								}
								else{
									grid.rows[1][i] = Grid.PositionState.NO;
								}
							}
						}
						else if (fill == grid.third && !grid.row3Solved){
							
							grid.row3Solved = true;
							
							for (int i = 0; i < numSlots; i++){
								if (i == trialPos){
									grid.rows[0][i] = Grid.PositionState.NO;
									grid.rows[1][i] = Grid.PositionState.NO;
									grid.rows[2][i] = Grid.PositionState.YES;
									grid.rows[3][i] = Grid.PositionState.NO;
								}
								else{
									grid.rows[2][i] = Grid.PositionState.NO;
								}
							}
						}
						else if (fill == grid.fourth && !grid.row4Solved){
							
							grid.row4Solved = true;
							
							for (int i = 0; i < numSlots; i++){
								if (i == trialPos){
									grid.rows[0][i] = Grid.PositionState.NO;
									grid.rows[1][i] = Grid.PositionState.NO;
									grid.rows[2][i] = Grid.PositionState.NO;
									grid.rows[3][i] = Grid.PositionState.YES;
								}
								else{
									grid.rows[3][i] = Grid.PositionState.NO;
								}
							}
						}
					}
				}
				else if (whitePegs == 2){
					if (isPlayer){
						//fill in trialpos
						if (!grid.isInSecretCode(fill)){
							grid.knowColor(fill);
						}
						
						if (fill == grid.first && !grid.row1Solved){
							
							grid.row1Solved = true;
							
							for (int i = 0; i < numSlots; i++){
								if (i == trialPos){
									grid.rows[0][i] = Grid.PositionState.YES;
									grid.rows[1][i] = Grid.PositionState.NO;
									grid.rows[2][i] = Grid.PositionState.NO;
									grid.rows[3][i] = Grid.PositionState.NO;
								}
								else{
									grid.rows[0][i] = Grid.PositionState.NO;
								}
							}
						}
						else if (fill == grid.second && !grid.row2Solved){
							
							grid.row2Solved	= true;
							
							for (int i = 0; i < numSlots; i++){
								if (i == trialPos){
									grid.rows[0][i] = Grid.PositionState.NO;
									grid.rows[1][i] = Grid.PositionState.YES;
									grid.rows[2][i] = Grid.PositionState.NO;
									grid.rows[3][i] = Grid.PositionState.NO;
								}
								else{
									grid.rows[1][i] = Grid.PositionState.NO;
								}
							}
						}
						else if (fill == grid.third && !grid.row3Solved){
							
							grid.row3Solved  = true;
							
							for (int i = 0; i < numSlots; i++){
								if (i == trialPos){
									grid.rows[0][i] = Grid.PositionState.NO;
									grid.rows[1][i] = Grid.PositionState.NO;
									grid.rows[2][i] = Grid.PositionState.YES;
									grid.rows[3][i] = Grid.PositionState.NO;
								}
								else{
									grid.rows[2][i] = Grid.PositionState.NO;
								}
							}
						}
						else if (fill == grid.fourth && !grid.row4Solved){
							
							grid.row4Solved = true;
							
							for (int i = 0; i < numSlots; i++){
								if (i == trialPos){
									grid.rows[0][i] = Grid.PositionState.NO;
									grid.rows[1][i] = Grid.PositionState.NO;
									grid.rows[2][i] = Grid.PositionState.NO;
									grid.rows[3][i] = Grid.PositionState.YES;
								}
								else{
									grid.rows[3][i] = Grid.PositionState.NO;
								}
							}
						}
					}
				}
				
				if (guess == numColors - 1){
					if (grid.totalKnownColors < numSlots){
						while (grid.totalKnownColors < numSlots){
							grid.knowColor(colorChoices.get(colorChoices.size()-1));
						}
					}
				}
				
				grid.checkRowsAndColumns();
				grid.checkRowsAndColumns();
				grid.checkRowsAndColumns();
				grid.checkRowsAndColumns();
	
				trialPos++;
				currentTrial++;
				
				if (trialPos == numSlots)
					trialPos = 0;
				
				
				if (currentTrial == colorChoices.size())
					currentTrial = 0;
				
				System.out.println("\n| " + grid.rows[0][0] + " | " 
									+ grid.rows[0][1] + " | "
									+ grid.rows[0][2] + " | "
									+ grid.rows[0][3] + " | " + ((grid.first == null) ? "" : grid.first));
				
				System.out.println("| " + grid.rows[1][0] + " | " 
						+ grid.rows[1][1] + " | "
						+ grid.rows[1][2] + " | "
						+ grid.rows[1][3] + " | " + ((grid.first == null) ? "" : grid.second));
				
				System.out.println("| " + grid.rows[2][0] + " | " 
						+ grid.rows[2][1] + " | "
						+ grid.rows[2][2] + " | "
						+ grid.rows[2][3] + " | " + ((grid.first == null) ? "" : grid.third));
				
				System.out.println("| " + grid.rows[3][0] + " | " 
						+ grid.rows[3][1] + " | "
						+ grid.rows[3][2] + " | "
						+ grid.rows[3][3] + " | " + ((grid.first == null) ? "" : grid.fourth));
				
				boolean incomplete = false;
				
				for (int i = 0; i < numSlots; i++){
					for(int j = 0; j < numSlots; j++){
						if (grid.rows[i][j] == Grid.PositionState.MA)
							incomplete = true;
						
						if (grid.rows[i][j] == Grid.PositionState.YES){
							
							//System.out.println("i " + i + " j " + j);
							
							if (j == 0)
								firstPosSolved = true;
							else if (j == 1)
								secondPosSolved = true;
							else if (j == 2)
								thirdPosSolved = true;
							else if (j == 3)
								fourthPosSolved = true;
						}
						
						if (!incomplete && i == numSlots - 1 && j == numSlots - 1 
								&& (grid.row1Solved && grid.row2Solved && grid.row3Solved && grid.row4Solved)
								&& grid.first != null && grid.second != null && grid.third != null && grid.fourth != null){
							solved = true;
							System.out.println("SOLVED");
						}
					}
				}
			}
			
			iterations++;
			guess++;
			average+=guess;
			
			if (guess < fewestGuesses)
				fewestGuesses = guess;
			if (guess > mostGuesses)
				mostGuesses = guess;
				
		}
		
		System.out.println("AVERAGE: " + average/numIterations);
		System.out.println("FEWEST GEUSSES: " + fewestGuesses);
		System.out.println("MOST GUESSES: " + mostGuesses);
	}
	
	
	//get a new answer
	public ArrayList<Color> getNewAnswer(){
		ArrayList<Color> answerToReturn = new ArrayList<Color>();
		
		for (int i = 0; i < numSlots; i++)
			answerToReturn.add(colorChoices.get((int)(Math.random() * colorChoices.size())));
		return answerToReturn;
	}
	
	public boolean checkGuess(ArrayList<Color> guess){
		
		ArrayList<Color> guessCopy = new ArrayList<Color>(guess);
		ArrayList<Color> answerCopy = new ArrayList<Color>(answer);
		int numberOfMatches = 0;
		for (int i = 0; i < 4; i++){
			//right color, correct position
			if (guess.get(i) == answer.get(i)){
				guessCopy.remove(i - numberOfMatches);
				answerCopy.remove(i - numberOfMatches);
				numberOfMatches++;
				blackPegs++;
			}
		}

		
		for (int i = 0; i < answerCopy.size(); i++){
			//Right Color, incorrect Position
			if (guessCopy.contains(answerCopy.get(i))){
				guessCopy.remove(answerCopy.get(i));
				whitePegs++;			
			}
		}
		
		System.out.println("BLACK (" + blackPegs + ")   ---    WHITE (" + whitePegs + ")");
		
		
		return true;
	}
	
	
	public void resetColorChoices(){
		colorChoices.clear();
		for (int i = 0; i < numColors; i++)
			colorChoices.add(Color.values()[i]);
	}
	
	
	//Down the left of the grid is the color that we know is in the code
	//each color has a row that contains info about where in the code the color is
	//
	static class Grid{
		
		public enum PositionState{ MA, YES, NO }
		
		public Color first, second, third, fourth;
		public int totalKnownColors = 0;
		
		public PositionState[][] rows;
		public Color[] colors;
		public boolean row1Solved = false;
		public boolean row2Solved = false;
		public boolean row3Solved = false;
		public boolean row4Solved = false;
		
		private int numberOfColors = 4;
		
		public Grid(int numberOfColors){
			
			this.numberOfColors = numberOfColors;
			rows = new PositionState[numberOfColors][numberOfColors];
			colors = new Color[numberOfColors];
			
			for (int i = 0; i < numberOfColors; i++){
				for (int j = 0; j < numberOfColors; j++){
					rows[i][j] = PositionState.MA;
				}
			}
		}
		
		public void knowColor(Color c){
			
			if (totalKnownColors < numberOfColors){
				totalKnownColors++;
				
				if (first == null){
					first = c;
					colors[0] = c;
				}
				else if (second == null){
					second = c;
					colors[1] = c;
				}
				else if (third == null){
					third = c;
					colors[2] = c;
				}
				else if (fourth == null){
					fourth = c;
					colors[3] = c;
				}
			}
		}
		
		public boolean isInSecretCode(Color c){
			if (c == first || c == second || c == third || c == fourth)
				return true;
				
			return false;
		
		}
		
		public void checkRowsAndColumns(){
			//check if there are three checks in a column, if there are fill in the missing space with and solved!
			
			if (first != null && second != null && third != null && fourth != null){
				for (int i = 0; i < numberOfColors; i++){
					
					int emptyPositions = 0;
					int emptyPosition = 0;
					
					if (rows[i][0] == PositionState.MA){
						emptyPositions++;
						emptyPosition = 0;
					}
					if (rows[i][1] == PositionState.MA){
						emptyPositions++;
						emptyPosition = 1;
					}
					if (rows[i][2] == PositionState.MA){
						emptyPositions++;
						emptyPosition = 2;
					}
					if (rows[i][3] == PositionState.MA){
						emptyPositions++;
						emptyPosition = 3;
					}
					
					if (emptyPositions == 1){
						rows[i][emptyPosition] = PositionState.YES;
						if (i == 0)
							row1Solved = true;
						else if (i == 1)
							row2Solved = true;
						else if(i == 2)
							row3Solved = true;
						else if (i == 3)
							row4Solved = true;
						
						for (int h = 0; h < numberOfColors; h++){
							if (h != i){
								rows[h][emptyPosition] = PositionState.NO;
							}
						}
					}
				}
			}
			
			for (int i = 0; i < totalKnownColors; i++){
				
				int emptyPositions = 0;
				int emptyPosition = 0;
				
				if (rows[0][i] == PositionState.MA){
					emptyPositions++;
					emptyPosition = 0;
				}
				if (rows[1][i] == PositionState.MA){
					emptyPositions++;
					emptyPosition = 1;
				}
				if (rows[2][i] == PositionState.MA){
					emptyPositions++;
					emptyPosition = 2;
				}
				if (rows[3][i] == PositionState.MA){
					emptyPositions++;
					emptyPosition = 3;
				}
				
				if (emptyPositions == 1){
					rows[emptyPosition][i] = PositionState.YES;
					
					if (emptyPosition == 0)
						row1Solved = true;
					if (emptyPosition == 1)
						row2Solved = true;
					if (emptyPosition == 2)
						row3Solved = true;
					if (emptyPosition == 3)
						row4Solved = true;
					
					for (int h = 0; h < numberOfColors; h++){
						if (h != i){
							rows[emptyPosition][h] = PositionState.NO;
						}
					}
				}
			}
			
		}
	}	
}
