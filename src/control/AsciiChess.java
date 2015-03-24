package control;

import java.util.ArrayList;

import util.Location;
import model.Board;
import model.Piece;
import model.Player;

/**
 * control for chess
 * @author Joseph
 *
 */
public interface AsciiChess {
	
	boolean play(Board baord);
	
	void calculateMoves(Board board);
	
	boolean movePiece(Piece piece, Board board, Location toMove, Player player);
	
	boolean checkMate(Board board, Player player);
	
	ArrayList<Location> parseInput(String input);

}
