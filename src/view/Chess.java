package view;

import java.util.ArrayList;

import util.Location;
import control.AsciiChess;
import model.Board;
import model.Pawn;
import model.Piece;
import model.Player;

public class Chess implements AsciiChess{
	
	public static void main(String[] args){
		
		Chess chess = new Chess();
		Board game = new Board();
		Player playerWhite = new Player();
		Player playerBlack = new Player();
		
		
		game.loadPlayer(playerWhite);
		game.loadPlayer(playerBlack);
		
		
		game.printBoard();
		
		
		
	}

	@Override
	public boolean play() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean loadMoveFiles() {
		// TODO Auto-generated method stub
		return false;
	}

	//this function will be called any time a user selects a piece. 
	//this function will most likely have a separate version for the king piece.
	public boolean calculateMoves(Board board, Piece piece, int i, int j) {
		
		int[][] vectors = piece.getMoveSet();
		
		//iterate through moveset unit vectors
		for(int index = 0; i < vectors.length; index++){
			
			int moveDist = piece.getMoves();
			
			if(piece instanceof Pawn){
				Pawn temp = (Pawn) piece;
				
				if(temp.atStart()){
					moveDist++;
				}
			}
			
			int iOff = vectors[index][0];
			int jOff = vectors[index][1];
					
			//iterate max length of moves;
			for(int offset = 0; offset < moveDist; offset++){
				
				iOff += vectors[index][0] * offset;
				jOff += vectors[index][1] * offset;
				
				if((iOff >= 0 && iOff <= 7) && (jOff >= 0 && jOff <= 7)){
					
					if(board.isEmpty(iOff, jOff)){
						
						piece.addValidMove(new Location(iOff,jOff));
					}else if(piece.getOwner() != board.getPiece(iOff, jOff).getOwner()){
						
						//enemy piece, break (need to add checking mate checking)
						piece.addValidMove(new Location(iOff,jOff));
						break;
					}else{
						
						//friendly piece, break;
						break;
					}
				}

				
			}
		}
		
		return false;
	}

	public boolean movePiece(Piece piece, Board board, Location toMove, Player player) {
		
		ArrayList<Location> validMoves = piece.getValidMoves();
		
		if(validMoves == null){
			
			return false; //piece has no valid moves
		}
		
		for(Location validMove : validMoves){
			
			if(toMove.equals(validMove)){
				
				//check for enemy piece
				if(!board.isEmpty(toMove.getI(), toMove.getJ())){
					
					player.capturePiece(board.getPiece(toMove.getI(), toMove.getJ()));
					board.getPiece(toMove.getI(), toMove.getJ()).kill();
					board.updateBoard(piece, toMove.getI(), toMove.getJ());
					return true;
				}else{
					
					board.updateBoard(piece, toMove.getI(), toMove.getJ());
					return true;
				}
			}
		}
		
		return false; //move inputed was invalid
			
	}
	
}
