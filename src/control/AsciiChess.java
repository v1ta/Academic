package control;

import util.Location;
import model.Board;
import model.Piece;
import model.Player;

public interface AsciiChess {
	
	boolean play();
	
	boolean loadMoveFiles();
	
	boolean calculateMoves(Board board, Piece piece, int i, int j);
	
	boolean movePiece(Piece piece, Board board, Location toMove, Player player);

}
