
/**
 * Tictactoe game sub class, registers value from evaluation function and move index
 * @author Timo Sorakivi
 * @version 1.01
 */

public class BestMove {

	int val;
	int index;

	public BestMove (int v, int i){
		val = v;
		index = i;
	}
	public BestMove (int v) {
	    val = v;
	    index = 0;
	}
}