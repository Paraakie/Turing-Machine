/*
 * Sven Gerhards, 15031719
 * A2, 159331
 * 
 * This program simulates a Turing Machine.
 */

//Libaries

import java.io.*; //for files
import java.util.ArrayList; // Array Lists are very useful when working with infinite lists
import java.util.Scanner;

public class UniTuringMachine {
	/*
	 *  Main Function
	 */
	public static void main(String[] args) {
		
		System.out.println("----------------------------------------"); 
		System.out.println(" 159.331, Assignment 2, Semester 1 2018 "); 
		System.out.println(" Submitted by: Sven Gerhards, 15031719 "); 
		System.out.println("----------------------------------------"); 
		System.out.println("                                        ");
		System.out.println("-------------Turing Machine-------------");
		System.out.println("Please Enter the full filepath of your .tm file:");
		
		//Run the TuringMachine
		new UniTuringMachine().TuringMachine(); 
	
	}
	
	/*
	 * Private Values
	 */
	private ArrayList<Character> States = new ArrayList<>(); 			//Q – is the states of M (a fInite set) 
	private ArrayList<Character> Alphabet = new ArrayList<>();		//E – is the alphabet of M (a fInite set with at least two symbols) 
	private ArrayList<Character> InputWord = new ArrayList<>(); 	//I – is the input alphabet of M (a non-empty subset of E ) 
	private ArrayList<String> Instructions = new ArrayList<>(); 	//o – is the instruction table of M 
	private ArrayList<Character> Tape = new ArrayList<>();			//Tape, write stuff on here
	
	private char initState;  //q0 – is the Initial state of M (a member of Q )  
	private char finalState; //F – is the final states of M 
	
	
	//Constructor
	public UniTuringMachine(){ }
	
	/*
	 * Methods
	 */

	
	// This functions is the center piece of all other Methods
	void TuringMachine() {
		//Read userInput using Scanner
		Scanner userInput = new Scanner(System.in);
		String fname = userInput.nextLine();
		
		//Pass filename to readFile method
		readFile(fname);
		
		//Close Scanner to avoid memory leaks
		userInput.close();
		
		//Print out all stored information
		System.out.println("States (Q) : " + getStates());
		System.out.println("Symbols (S) : " + getAlphabet());
		System.out.println("Input Word (I) : " + getInputWord());
		System.out.println("Initial State (q0) : " + getInitState());
		System.out.println("Final State (F) : " + getFinalState());
		
		displayInsturctions();
		
		// Inialize the Tape, if required the ArrayList will be updated in length later on
		setTape((ArrayList<Character>) getInputWord());
		
		//Then Simulate the Machine
		simulateMachine();
		
	}
		

	//Read in file
	void readFile(String filename) {
		
		//Testfile -> C:\Users\Paraakie\Documents\Uni\Y3S1 159331\binary_increment.tm
		int countReadLines = 0;
		String line;
        		
		try (BufferedReader readFile = new BufferedReader(new FileReader(filename))) {
			
            //While loop stops once it reaches the end of the file
    		while ((line = readFile.readLine()) != null) {
    			
    			//System.out.println(line); //For testing
			    			
            	//Ignores lines starting with # or empty lines
    			if(line.length() == 0 || line.charAt(0) == '#') {
					
					continue; //Skips line
				}
    			
    			line = line + '\0'; // This isn't added at the end of any of the read in Strings, so I did this manually
    			countReadLines = processFileInput(countReadLines, line);
    			
    			// Exception Handling when countRL isn't valid
    			if(countReadLines == -1) {
    				System.out.println("Error: Invalid countRL value");
    				System.exit(2);
    			}
            }
    		readFile.close();
		}
		
		/*
		 * The following catches were heavily inspired by Derek Banas from his video:
		 * "Java Video Tutorial 32", Link:  http://goo.gl/QAv0q
		 */
	
		// Can be thrown by FileInputStream
		catch (FileNotFoundException e) {
			
			System.out.println("Error(0): File not found.");
			System.exit(0);
		}
		
		catch(IOException e){
			
			System.out.println("Error(1): An I/O Error Occurred");
			System.exit(1);
		}
	}
	
	// Take Read-In information and put it into the ArrayLists 
	int processFileInput(int countRL, String lineCopy){

		// Read in States, character by character (separated by spaces)
		if(countRL == 0) {
			
			readInStates(lineCopy);				
			countRL++;
			return countRL;
			
		} else
		
		// Read in Alphabet, character by character (separated by spaces)
		if(countRL == 1) {

			readInAlphabet(lineCopy);
			countRL++;
			return countRL;
		} else
		
		// Read in Input-Word I, character by character (separated by spaces)
		if(countRL == 2) {
			
			readInInputWord(lineCopy);
			countRL++;
			return countRL;
		} else
		
		if(countRL == 3) {
			
			setInitState(lineCopy.charAt(0)); //using setter
			countRL++;
			return countRL;
		} else
		
		// Read in finalState, single character
		if(countRL == 4) {
			
			setFinalState(lineCopy.charAt(0)); //using setter
			countRL++;
			return countRL;
		} else

		// Read in InstructionTables, read line by line
		if(countRL == 5) {
			
			ArrayList<String> tempAL = new ArrayList<>();
			tempAL = (ArrayList<String>) getInstructions();
			tempAL.add(lineCopy);
			setInstructions(tempAL);
			
			// No need to increase count, Since the rest of the program is just lines of the InstructionTable
			return countRL;
		}	
		
		return -1;
	}

	// States are put into their ArrayList
	void readInStates(String allStates) {
		ArrayList<Character> tempAL = new ArrayList<>(); 	// Temporary ArrayList
		int numStates = 0; //Keep count of the number of States
		
		//read in state, has to be one char
		for(int i = 0; allStates.charAt(i) != '\0'; i++) {
			
			//Skip Spaces 
			if(allStates.charAt(i) == ' ') { continue; }
			
			//Add Letter to TempArrayList
			tempAL.add(allStates.charAt(i));
			numStates++;
		}
		
		
		//Check if there is a minimum amount of states
		if(numStates < 2) {
			
			System.out.println("Error 3: There need to be at least 2 States");
			System.exit(3);
		}
		
		setStates(tempAL); //Set States
}

	// Alphabet is put into its ArrayList
	void readInAlphabet(String allLetters) {
		ArrayList<Character> tempAL = new ArrayList<>(); 	// Temporary ArrayList

		//read in letters, has to be one char
		for(int i = 0; allLetters.charAt(i) != '\0'; i++) {
			
			//Skip Spaces 
			if(allLetters.charAt(i) == ' ') { continue; }
			
			//Add Letter to TempArrayList
			tempAL.add(allLetters.charAt(i));
		}
		
		setAlphabet(tempAL); //Set Alphabet		
	}
	
	// InputWord is put into its ArrayList
	void readInInputWord(String inputWord) {
		ArrayList<Character> tempAL = new ArrayList<>(); 	// Temporary ArrayList
		
		//read in inputWord, made up of singular chars
		for(int i = 0; inputWord.charAt(i) != '\0'; i++) {
			
			//Skip Lines
			if(inputWord.charAt(i) == ' ') { continue; }
			
			//Add Letter to TempArrayList
			tempAL.add(inputWord.charAt(i));
		}
		
		setInputWord(tempAL); //Set InputWord			
	}
	
	//Prints out Instructions in a more readable way
	void displayInsturctions() {
		
		ArrayList<?> tempAL = getInstructions();
		String tempS = "Empty";
		
		System.out.println("Instruction Table (delta) : ");
		
		// Using loop & subString to efficiently print out Instructions
		for(int index = 0; tempAL.size() > index; index++) {
		
			tempS = tempAL.get(index).toString();
			System.out.println(tempS.substring(0, 3) + " -> " + tempS.substring(4, 10));
		}
		
	}
	
	//Process the Instructions
	void simulateMachine() {
		
		int currentPos = 0;
		char currentState = getInitState();
		
		int newPos = 0;
		char newState = ' ';
		
		System.out.println("Simulating Machine");
		
		
		while(currentState != getFinalState()) { 
			
			//Print Line
			printTape(currentPos, currentState);
			
			//Update Tape based on State and Position
			newState = updateTape(currentPos, currentState);
			newPos = updateTapePos(currentPos, currentState);
			
			currentPos = newPos;
			currentState = newState;
			
			//Check if currentPos is valid
			if(currentPos < 0) {
				System.out.println("Error 6: TapePointer went out of bounderies.");
				System.exit(6);				
			}
			
		}
		
		//Once arrived at Halting State
		if(currentState == getFinalState()) { System.out.println("Program Halted (Success)"); }
		else { 
			System.out.println("Error(4): Program stopped, but is not in a Halt State.");
			System.exit(4);
		}
		
	}
	
	// Prints the current Tape
	void printTape(int Position, char State) {
		
		System.out.print(State + " : ");
		
		for(int index = 0; getInputWord().size() > index; index++) {
			
			System.out.print(getTape().get(index));
			
			//Print Visual Pointer for
			if(index == Position) {	System.out.print("<"); } 
			else { System.out.print(" "); }
		}
		System.out.println("");
	}
	
	// Update the current Tape
	char updateTape (int currentPos, char currentState){
		
		String currentLine = "";
		ArrayList<Character> copyTape = (ArrayList<Character>) getTape();
		char tapeSymbol = (char) copyTape.get(currentPos); //The symbol the Tape points at currently
		
		for(int index = 0;  currentLine != null; index++) {
			currentLine = (String) getInstructions().get(index);
			//System.out.println(tempS + " -> " + index); //for testing
			
			// Find the currentLine that has a matching State and Symbol
			if(currentLine.charAt(0) == currentState && currentLine.charAt(2) == tapeSymbol) {
				
				// Modify Tape 
				// Only necessary if tapeSymbol is different
				if(currentLine.charAt(6) != tapeSymbol) {
					copyTape.set(currentPos, currentLine.charAt(6));
					
					setTape(copyTape);
				}
				
				// Update currentState
				currentState = currentLine.charAt(4);
				
				break;
			}
			
		}
		return currentState;
	}
	
	//Update Current Position on Tape
	int updateTapePos(int currentPos, char currentState) {
		String currentLine = "";
		char tapeSymbol = (char) getTape().get(currentPos); //The symbol the Tape points at currently
		char upOrDown = ' '; //Used to later update Position
		
		for(int index = 0;  currentLine != null; index++) {
			currentLine = (String) getInstructions().get(index);
			//System.out.println(tempS + " -> " + index); //for testing
			
			// Find the currentLine that has a matching State and Symbol
			if(currentLine.charAt(0) == currentState && currentLine.charAt(2) == tapeSymbol) {
								
				// Update Position
				upOrDown = currentLine.charAt(8);
				if(upOrDown == '+') {
					currentPos++;
				} else if(upOrDown == '-') {
					currentPos--;
				} else {
					System.out.println("Error 5: Invalid symbol for changing Tapes Position");
					System.exit(5);
				}
				break;
			}
			
		}
		return currentPos;
	}
	
	/***********************
	 * Generated           *
	 * Getters and Setters *
	 ***********************/

	/**
	 * @return the tape
	 */
	public ArrayList<?> getTape() {
		return Tape;
	}

	/**
	 * @param tape the tape to set
	 */
	public void setTape(ArrayList<Character> tape) {
		Tape = tape;
	}

	/**
	 * @return the states
	 */
	public ArrayList<?> getStates() {
		return States;
	}

	/**
	 * @param states the states to set
	 */
	public void setStates(ArrayList<Character> states) {
		States = states;
	}

	/**
	 * @return the alphabet
	 */
	public ArrayList<?> getAlphabet() {
		return Alphabet;
	}

	/**
	 * @param alphabet the alphabet to set
	 */
	public void setAlphabet(ArrayList<Character> alphabet) {
		Alphabet = alphabet;
	}

	/**
	 * @return the inputWord
	 */
	public ArrayList<?> getInputWord() {
		return InputWord;
	}

	/**
	 * @param inputWord the inputWord to set
	 */
	public void setInputWord(ArrayList<Character> inputWord) {
		InputWord = inputWord;
	}

	/**
	 * @return the instructions
	 */
	public ArrayList<?> getInstructions() {
		return Instructions;
	}

	/**
	 * @param instructions the instructions to set
	 */
	public void setInstructions(ArrayList<String> instructions) {
		Instructions = instructions;
	}

	/**
	 * @return the initState
	 */
	public char getInitState() {
		return initState;
	}

	/**
	 * @param initState the initState to set
	 */
	public void setInitState(char InitState) {
		this.initState = InitState;
	}

	/**
	 * @return the finalState
	 */
	public char getFinalState() {
		return finalState;
	}

	/**
	 * @param finalState the finalState to set
	 */
	public void setFinalState(char finalState) {
		this.finalState = finalState;
	}
	
}
//------------------------------------------------------------
