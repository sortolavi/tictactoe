import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Tictactoe main class
 * @author Timo Sorakivi
 * @version 1.01
 */

public class Tictactoe implements ActionListener {
	
	private static boolean DEBUG 			= false; 	// For testing purposes only
	private static boolean COMPUTERSTARTS;  			// Computer starts the game if true, defined later
	private static boolean CHANGESTARTER	= true; 	// When playing more than one game, we alternate starting player if true
	
	// evaluatePosition function return values, showing who wins or loses
	private static final int PLAYER1WIN 	= 4; // smallest value
	private static final int PLAYER2WIN 	= 7; // biggest value
	private static final int DRAW 			= 5; // draw 
	private static final int GOAHEAD	 	= 6; // continue
	
	// other constants, possible values in game table
	private static final int EMPTY    		= 0;
	private static final int PLAYER1 		= 1;
	private static final int PLAYER2 		=-1;
	
	// game table storing player moves
	private static final int TABLESIZE     	= 9;
	private int[] game = new int[TABLESIZE];

	// winning combinations
	private final int[][] WINNINGCOMBINATIONS = new int[][] {
			{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // horizontal rows
			{0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // vertical rows
			{0, 4, 8}, {2, 4, 6}			 // diagonal rows
	};

	// UI components
	private JFrame window 					= new JFrame("Tic-tac-toe");
	private JButton buttons[] 				= new JButton[9];
	
	private int turn=0, crosses=0, naughts=0, draws=0; 	// player turn counter, win and draw counters
	private int playedGames 				= 0;		// amount of played games
	private String info1 = "", info2 = "";				// strings for showing game data
	private String mark 					= "";		// player mark, X or 0, cross or naught
	private int playerId 					= 0; 		// player, 1 or -1, PLAYER1 or PLAYER2
	private boolean win 					= false;	// true if player wins
	private boolean firstRound 				= true;		// used at first round when playing against computer
	
	
	// The ui pictures (cross and naught) are at images folder that is under the folder where *.java files are
	// This way jGrasp or Oracle java compiler are able to find them 
	//
	// If using Eclipse, the images folder must be placed side by side with bin and src folders
	
	private ImageIcon crossIcon = new ImageIcon("images/cross.gif");
	private ImageIcon naughtIcon = new ImageIcon("images/naught.gif");
	private ImageIcon thisIcon;
	
	// class constructor function
	public Tictactoe() {
		// make a window
		window.setSize(300,300);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setLayout(new GridLayout(3,3));
		window.setLocation(300, 200);
		window.setResizable(false);
		
		// add buttons to window, set names and event listeners
		for(int i = 0 ; i < TABLESIZE ; i++){
			buttons[i] = new JButton();
			buttons[i].setBackground(Color.white);
			buttons[i].setName(Integer.toString(i));
			window.add(buttons[i]);
			buttons[i].addActionListener(this);
		}
		// show the window
		window.setVisible(true);
	}
	
	
	// handling events
	public void actionPerformed(ActionEvent evt) {
		firstRound = false;
		changeTurn();
		
		// mark the clicked button and prevent it having any more events
		JButton pressedButton = (JButton)evt.getSource(); 
		pressedButton.setIcon(thisIcon);
		pressedButton.setEnabled(false);
		
		// add the player move to game table (1 or -1)
		int num = Integer.parseInt(pressedButton.getName());
		updateTable(num, playerId);
		
		// is there a winner?
		int eval = evaluatePosition(); 
		if (DEBUG) System.out.println("eval : " + eval);

		if (eval == GOAHEAD) {
			computerPlaying();
		}
		if (eval == PLAYER1WIN || eval == PLAYER2WIN) {
			showWinningRow();
			if(mark.equals("X")) crosses++;else naughts++;
			JOptionPane.showMessageDialog(null, mark + " wins!", "Results" , JOptionPane.PLAIN_MESSAGE);
			showResults();
		}
		if (eval == DRAW) {
			draws++;
			JOptionPane.showMessageDialog(null, "Draw!", "Results" , JOptionPane.PLAIN_MESSAGE);
			showResults();
		}

	}
	
	
	// show results and options
	private void showResults() {
		playedGames ++;
		if(crosses > 0 || naughts > 0)info1 = "Wins: ";
		if(crosses > 0)info1 += "Cross: " + crosses + " ";
		if(naughts > 0)info1 += "Naught: " + naughts + "\n";
		if(draws > 0)info2 = "Draw: " + draws;
		int vastaus = JOptionPane.showConfirmDialog(null, info1 + info2 + "\nPlay again?", "Results", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if(vastaus == JOptionPane.YES_OPTION)reset(); else System.exit(0);
	}
	
	
	// reset current game table and start a new game
	private void reset() {
		resetTable();
		playGame();
	}
	
	
	// paints winning row with new color
	private void showWinningRow() {
		for(int i = 0; i < 8 ; i++){
			if(game[WINNINGCOMBINATIONS[i][0]] == playerId &&  game[WINNINGCOMBINATIONS[i][1]] == playerId && game[WINNINGCOMBINATIONS[i][2]] == playerId){ 
				for(int j = 0; j < 3; j++) {
					int k = WINNINGCOMBINATIONS[i][j];
					buttons[k].setBackground(new Color(20, 20, 20));
				}
			}
		}
	}


	// checking if any winning combinations has been reached, called from evaluatePosition()
	private boolean winnerFound(int id){
		for(int i = 0; i < 8 ; i++){
			if(game[WINNINGCOMBINATIONS[i][0]] == id &&  game[WINNINGCOMBINATIONS[i][1]] == id && game[WINNINGCOMBINATIONS[i][2]] == id){ 
				return true;
			}
		}
		return false;
	}
	
	

	// reset table and set all ready for a new game
	private void resetTable(){
	   for(int i = 0; i < 9; i++){
			buttons[i].setText("");
			buttons[i].setIcon(null);
			buttons[i].setBackground(Color.white);
			buttons[i].setEnabled(true);
			game[i] = EMPTY;
		}
	   	win = false;
	   	firstRound = true;
	   	if(CHANGESTARTER){
		   if(playedGames % 2 == 0){turn =  0; COMPUTERSTARTS = !COMPUTERSTARTS;}
		   else {				     turn = -1; COMPUTERSTARTS = !COMPUTERSTARTS;}
	   } else turn = 0;
	}
	
	
	// coordinate player turns and ids and icons
	private void changeTurn(){
		turn++;
		mark = (turn % 2 == 0)? "O" : "X" ;
		playerId = (turn % 2 == 0)? PLAYER2 : PLAYER1 ;
		thisIcon = (turn % 2 == 0)? naughtIcon: crossIcon ;
	}
	
	
	// if playing against computer
	private void computerPlaying(){
		changeTurn();
		
		// at first round choose center, later use algorithm to find best move
		BestMove bm = firstRound? new BestMove(0,4): bMove(playerId);
		
		if (DEBUG) System.out.println("comp play " + playerName(playerId) + " index: "+ bm.index + " , val: " + bm.val);

		updateTable(bm.index, playerId);
		buttons[bm.index].setIcon(thisIcon);
		buttons[bm.index].setEnabled(false);
		
		int eval = evaluatePosition(); 

		if (DEBUG) System.out.println("comp eval : " + eval);

		if (eval == PLAYER1WIN || eval == PLAYER2WIN) {
			showWinningRow();
			if(mark.equals("X")) crosses++;else naughts++;
			JOptionPane.showMessageDialog(null, mark + " wins!", "Results" , JOptionPane.PLAIN_MESSAGE);
			showResults();
		}
		if (eval == DRAW) {
			draws++;
			JOptionPane.showMessageDialog(null, "Draw!", "Results" , JOptionPane.PLAIN_MESSAGE);
			showResults();
		}


	}
	
	
	// starting the game
	private void playGame(){
		if(COMPUTERSTARTS)computerPlaying();
	}

	
	// check if game table is empty at given index
  	private boolean isEmpty (int i) {
  		return game [i] == EMPTY;
	}
	
  	
	// check if game table is full
	private boolean tableFull(){
		for (int i = 0; i < TABLESIZE; i++) if (game[i] == EMPTY) return false;
		return true;
	}
	
	
	// evaluate current game situation, return int value
	private int evaluatePosition (){
		return 	winnerFound (PLAYER2)? PLAYER2WIN :
				winnerFound (PLAYER1)? PLAYER1WIN :
	   			tableFull() 	     ? DRAW : 
	   			GOAHEAD;
	}
	
	
	// fill in the game table with the move of current playerId in turn (1 or -1)
	private void updateTable (int i,  int id){
		game [i] = id;
	}
	
	
	// return name of player, used in debugging only
	private String playerName (int i){
		return 	(i == PLAYER1)? "Player 1 ":"Player 2 ";
	}
	
	
  
  	/* 	
  	 * AI main function
  	 * Parameters:	int player, the player in turn (1 tai -1, PLAYER1 or PLAYER2)
  	 * Returns: 	instance of BestMove class 
  	 * 
  	 * The principle is to traverse down all possible moves recursively, that is, making a move and calling the function again while alternating players until
  	 * at the end a solution is reached (win, lose or draw) that now has a numerical value. Recursion backs to next upper level and keeps going until all solutions
  	 * have been checked and evaluated with index (0 - 8) and value (4,5 or 7) that is used only inside the recursion.
  	 * 
  	 * More about the algorithm: http://en.wikipedia.org/wiki/Minimax
  	 */
  	private BestMove bMove (int player) {
	
		int opponent;	// PLAYER1 or PLAYER2
		BestMove move;  // Opponents best choice
		int eval;		// win, lose or draw
		int best = 0;	// 
		int value;		// 
		
		eval = evaluatePosition(); 								// did we found a solution
		if (eval != GOAHEAD) return new BestMove (eval);		// if yes then let's unwind the recursion
		
		opponent 	= (player==PLAYER1)?PLAYER2:PLAYER1;
		value 		= (player==PLAYER1)?PLAYER2WIN:PLAYER1WIN; 	// first give the worst possible value that can only get better
		
		for (int i = 0; i < TABLESIZE; i++){
			if (isEmpty(i)){
				updateTable (i, player);						// make a move
				move = bMove (opponent);						// opponent move, going on recurcively until solution is reached
				updateTable (i, EMPTY);							// cancel the move
				
				// if move value is better, then keep it
				if (player == PLAYER2 && move.val > value || player == PLAYER1 && move.val < value) {
					value = move.val;
					best = i;
				}
		    }
		}
		if (DEBUG) System.out.println("value : " + value + ", best : " + best);
		return new BestMove (value, best);
  	}
  
  	
  	// Main function, create Tictactoe class instance, decide who starts the game and play
	public static void main (String [] args){
	
		Tictactoe ttt = new Tictactoe();
		
		int whoStarts = JOptionPane.showConfirmDialog(null, "Computer starts?", "Start",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		COMPUTERSTARTS = (whoStarts == JOptionPane.YES_OPTION)? true:false;
		
		ttt.playGame();
	}
}

