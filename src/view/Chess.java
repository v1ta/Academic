package view;

import java.util.ArrayList;
import java.util.StringTokenizer;

import util.Location;
import control.AsciiChess;
import model.Bishop;
import model.Board;
import model.Enpassant;
import model.King;
import model.Knight;
import model.Pawn;
import model.Piece;
import model.Player;
import model.Queen;
import model.Rook;

public class Chess implements AsciiChess{
	
	private static int turns = 0;
	private static boolean whiteCheck = false;
	private static boolean blackCheck = false;
	private static boolean canMove = true;
	private boolean drawFlag = false;
	
	public static void main(String[] args){
		
		Chess chess = new Chess();
		Board game = new Board(new Piece[8][8]);
		Player playerWhite = new Player();
		Player playerBlack = new Player();
		String input = "";
		ArrayList<Location> moves = null;
		
		game.loadPlayer(playerWhite);
		game.loadPlayer(playerBlack);
		
		chess.calculateMoves(game);
		
		while(chess.play(game)){
			
			if(Chess.turns % 2 == 0){
				System.out.print("White's move: ");
			}else{
				System.out.print("Black's move: ");
			}
			
			input = game.input().nextLine();
			System.out.println("\n");
			
			moves = chess.parseInput(input);
			
			if(moves == null){
				
				continue;
			}
			
			if(moves.size() == 1){
				if(moves.get(0).getI() == 0){
					break;
				}else if(moves.get(0).getI() == 1 && chess.drawFlag()){
					System.out.println("Draw\n");
					break;
				}else{
					System.out.println("Illegal input\n");
					continue;
				}
			}
			
			if(Chess.turns % 2 == 0){
				
				if(chess.movePiece(game.getPiece(moves.get(0)), game, moves.get(1), playerWhite)){
					Chess.turns++;
				}else{
					continue;
				}
				
				
			}else{
				
				if(chess.movePiece(game.getPiece(moves.get(0)), game, moves.get(1), playerBlack)){
					Chess.turns++;
				}else{
					continue;
				}
				
			}
			
			
			chess.calculateMoves(game);
				
				if(chess.blackCheck()){
					if(chess.checkMate(game, playerBlack)){
						game.printBoard();
						System.out.println("White wins\n");
						break;
					}
					System.out.println("Check");
				}else if(chess.whiteCheck()){
					if(chess.checkMate(game, playerWhite)){
						game.printBoard();
						System.out.println("Black wins\n");
						break;
					}
					System.out.println("Check\n");
				}
				
			
			
		}
			if(chess.drawFlag()){
				
				chess.toggleDrawFlag();
			}
			
			if(moves.size() == 3){
				
				chess.toggleDrawFlag();
			}
			
			
		
				game.closeInput();
	}
	
	
	public boolean whiteCheck(){
		return whiteCheck;
	}
	
	
	public boolean blackCheck(){
		return blackCheck;
	}
		
	
	public boolean drawFlag(){
		
		return this.drawFlag;
	}
	
	
	public void toggleDrawFlag(){
		
		if(this.drawFlag){
			this.drawFlag = false;
		}else{
			this.drawFlag = true;
		}
		
	}

	
	public boolean play(Board board) {
		
		if(canMove){
			board.printBoard();
			return true;
		}else{
			
			return false;
		}
	}


	public void calculateMoves(Board board) {
		
		whiteCheck = false;
		blackCheck = false;
		
		Piece[][] pieces= board.getBoard();
		int iOff = 0;
		int jOff = 0;
		
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				
			if(pieces[i][j] == null){
				
				continue;
			}
			
			Piece piece = pieces[i][j];
			
			if(piece instanceof Enpassant){
				if(piece.getTurns() == 1){
					board.nukeCell(piece.getPos());
				}else{
					
					piece.incrementTurn();
				}
				continue;
			}
			
			piece.resetValidMoves();
			
			
			int[][] vectors = piece.getMoveSet();
			//System.out.println("CALCULATING MOVESET FOR: " + piece);
			
			//iterate through moveset unit vectors
			for(int index = 0; index < vectors.length; index++){
				
				int moveDist = piece.getMoves();
				
				if(piece instanceof Pawn){
					if(piece.atStart()){
						moveDist++;
					}
				}
				
	
						
				iOff = piece.getPos().getI();
				jOff = piece.getPos().getJ();
				//iterate max length of moves;
				for(int offset = 0; offset < moveDist; offset++){
					//System.out.println("INNER LOOP, OFFSET: " + offset + "  INDEX: " + index);
					iOff += vectors[index][0];
					jOff += vectors[index][1];
					
					if((iOff >= 0 && iOff <= 7) && (jOff >= 0 && jOff <= 7)){
						
						//if(piece instanceof Queen && piece.getOwner().toString().equals("Black")){
						//	System.out.println("COORDS: "+ iOff + "   " + jOff + "  CALLING ISEMPTY: " + board.isEmpty(iOff, jOff));
						//}
						
						if(board.isEmpty(iOff, jOff)){
							piece.addValidMove(new Location(iOff,jOff));
							
								
						}else if(piece.getOwner() != board.getPiece(new Location(iOff, jOff)).getOwner()){
							

							piece.addValidMove(new Location(iOff,jOff));
							if(board.getPiece(new Location(iOff, jOff)) instanceof King && !(piece instanceof King)){
								if(board.getPiece(new Location(iOff, jOff)).getOwner().toString().equals("White")){
									whiteCheck = true;
								}else{
									blackCheck = true;
								}
							}
							break;
						}else{
							//System.out.println("CAL MOVE SET FRIENDLY PIECE: "+ iOff + " " + jOff);
							//friendly piece, break;
							break;
						}
					}
	
					
				}
			}
			
			
			if(piece instanceof Pawn){
				
				int [][] sVectors = piece.getSMoveSet();
				int sMoveDist = piece.getMoves();
				
				
				
				for(int index = 0; index < sVectors.length; index++){
					
					iOff = piece.getPos().getI();
					jOff = piece.getPos().getJ();
					
					


					
					for(int offset = 0; offset < sMoveDist; offset++){
						
						//System.out.println("INNER LOOP, OFFSET: " + offset + "  INDEX: " + index);
						iOff += sVectors[index][0];
						jOff += sVectors[index][1];
						
						if((iOff >= 0 && iOff <= 7) && (jOff >= 0 && jOff <= 7)){
							
	
							
							if(!board.isEmpty(iOff, jOff) || board.isEnpassant(iOff, jOff)){
								if(piece.getOwner() != board.getPiece(new Location(iOff, jOff)).getOwner()){
									
										
									
						
									//enemy piece, break (need to add checking mate checking)
									//System.out.println("CAL MOVE SET TEST FINAL MOVE: "+ iOff + " " + jOff);
									//System.out.println("added move for: " + piece + " at " + piece.getPos() + " with move " + iOff + " " + jOff);
									piece.addValidMove(new Location(iOff,jOff));
									
									if(board.getPiece(new Location(iOff, jOff)) instanceof King){
										if(board.getPiece(new Location(iOff, jOff)).getOwner().toString().equals("White")){
											whiteCheck = true;
										}else{
											blackCheck = true;
										}
									}
									break;
								}else{
									//	System.out.println("CAL MOVE SET FRIENDLY PIECE: "+ iOff + " " + jOff);
									//friendly piece, break;
									break;
								}
							}
						}
		
						
					}
				}
			}
			
			if(piece.atStart()){
				if(piece instanceof King){
					
				
					int [][] sVectors = piece.getSMoveSet();
					int sMoveDist = piece.getMoves() + 1;
					
					
					
					for(int index = 0; index < sVectors.length; index++){
						
						iOff = piece.getPos().getI();
						jOff = piece.getPos().getJ();
						
						for(int offset = 0; offset < sMoveDist; offset++){
							
							
							iOff += sVectors[index][0];
							jOff += sVectors[index][1];
							
							
							if((iOff >= 0 && iOff <= 7) && (jOff >= 0 && jOff <= 6)){
								
								if(board.isEmpty(iOff, jOff)){

									if(offset == 1){
										
										//right
										if(!board.isEmpty(iOff, jOff + sVectors[index][1])){
											
											
											if((piece.getOwner() == board.getPiece(new Location(iOff, jOff + sVectors[index][1])).getOwner()) && (board.getPiece(new Location(iOff, jOff + sVectors[index][1])) instanceof Rook) && (board.getPiece(new Location(iOff, jOff + sVectors[index][1])).atStart())){
												
												piece.addValidMove(new Location(iOff,jOff));
												
											}
										}
											
											//left
										if((iOff >= 0 && iOff <= 7) && ((jOff + 2*sVectors[index][1])	 >= 0 && (jOff + 2*sVectors[index][1])	 <= 7))
											if(!board.isEmpty(iOff, jOff + 2*sVectors[index][1])){
												

												if((piece.getOwner() == board.getPiece(new Location(iOff, jOff + 2*sVectors[index][1])).getOwner()) && (board.getPiece(new Location(iOff, jOff + 2*sVectors[index][1])) instanceof Rook) && (board.getPiece(new Location(iOff, jOff + 2*sVectors[index][1])).atStart()) && (board.isEmpty(iOff, jOff + sVectors[index][1]))) {
													
													piece.addValidMove(new Location(iOff,jOff));
													break;
													
												}
											}
										}
									}
									
								}else{
									
									break;
								}
							}
			
							
						}
					}
				}
			
		
			}
		}
		
		return;
	}

	
	public boolean movePiece(Piece piece, Board board, Location toMove, Player player) {
		
		
		if(piece == null){
			System.out.println("Illegal selection: there is no piece at that locaiton\n");
			return false;
		}
		
		Board tempB = new Board(board);
		Piece temp = null;
		
		if(piece instanceof Pawn){
			temp = new Pawn(piece);
		}else if(piece instanceof Rook){
			temp = new Rook(piece);
		}else if(piece instanceof Knight){
			temp = new Knight(piece);
		}else if(piece instanceof Bishop){
			temp = new Bishop(piece);
		}else if(piece instanceof Queen){
			temp = new Queen(piece);
		}else{
			temp = new King(piece);
		}
		
		boolean validMoveFlag = false;
		ArrayList<Location> validMoves = piece.getValidMoves();
		
		
		if(validMoves == null){
			
			System.out.println("Illegal move, no valid moves for this piece, try again\n");
			return false; //piece has no valid moves
		}
		
		for(Location validMove : validMoves){
			
			if(toMove.equals(validMove)){
				validMoveFlag = true;
				break;
			}
		}
				
		if(!validMoveFlag){
			System.out.println("Illegal move, invalid move selected, try again\n");
			
			for(Location validMove : validMoves){
				System.out.println(validMove);
			}
			return false;
		}
				if(tempB != null && temp != null){
					tempB.updateBoard(temp, toMove);
				
					calculateMoves(tempB);
					
					if(player.toString().equals("White") && whiteCheck){
						
						System.out.println("Illegal move, in check, try again\n");
						return false;
					}else if(blackCheck){
						
						System.out.println("Illegal move, in check try again\n");
						return false;
					}
				}
				//check for enemy piece
				if(!board.isEmpty(toMove.getI(), toMove.getJ()) || board.isEnpassant(toMove.getI(), toMove.getJ())){	
					

					if(board.getPiece(toMove) instanceof Enpassant && piece instanceof Pawn){
						Location temp1 = board.getPiece(toMove).getGhost();
						player.capturePiece(board.getPiece(temp1));
						board.getPiece(temp1).kill();
						board.updateBoard(piece, toMove);
						board.nukeCell(temp1);
						return true;
					}else{
						player.capturePiece(board.getPiece(toMove));
						board.getPiece(toMove).kill();
						board.updateBoard(piece, toMove);
						return true;
					}
				}else{
					board.updateBoard(piece, toMove);
					return true;
				}
				
			
	}


	
	public boolean checkMate(Board board, Player player) {
		
		
		String target = "";
		
		if(player.toString().equals("White")){
			target = "wK";
		}else{
			target = "bK";
		}
		
		Piece kingInCheck = null;
		
		outerloop:
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				kingInCheck = board.getPiece(new Location(i,j));
				
				if(kingInCheck != null){
					if(kingInCheck.toString().equals(target)){
						break outerloop;
					}
				}
			}
		}
	

		
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				
				Piece piece = board.getPiece(new Location(i,j));
				if(piece == null){
					continue;
					
				}else if(piece.getOwner() == kingInCheck.getOwner()){
					
					continue;
						
				}
			
				ArrayList<Location> kingMoves = kingInCheck.getValidMoves();
				ArrayList<Location> pieceMoves = piece.getValidMoves();
				
				if(pieceMoves.size() == 0){
					continue;
				}
				
				for(int x = 0; x < kingMoves.size(); x++){
	
						
						if(pieceMoves.contains(kingMoves.get(x))){
							
							kingInCheck.delMove(kingMoves.get(x));
						}
					
				}
	
						
						
					
				
				
			}
		}
		
		
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				
				Piece piece = board.getPiece(new Location(i,j));
				
				if(piece == null){
					continue;
					
				}else if(piece.getOwner() != kingInCheck.getOwner()){
					
					continue;
						
				}
				
				
				Piece temp = null;
				
				if(piece instanceof Pawn){
					temp = new Pawn(piece);
				}else if(piece instanceof Rook){
					temp = new Rook(piece);
				}else if(piece instanceof Knight){
					temp = new Knight(piece);
				}else if(piece instanceof Bishop){
					temp = new Bishop(piece);
				}else if(piece instanceof Queen){
					temp = new Queen(piece);
				}else{
					temp = new King(piece);
				}
				
				ArrayList<Location> moves = temp.getValidMoves();
				
				if(moves.size() == 0)
					continue;
				
				for( int e = 0; e < moves.size(); e++){
					
					
					
					Board tempBoard = new Board(board);
					
					tempBoard.updateBoard(temp, moves.get(e));
					
					calculateMoves(tempBoard);
					
		
					if(player.toString().equals("White")){
						
						if(!whiteCheck){
							
							return false;
						}
						
					}else{
						
						if(!blackCheck){
							
							return false;
						}
					}
				}
				
				
				
			}
		}
		
		if(kingInCheck.getValidMoves().size() == 0){
			return true;
		}else{
			return false;
		}
	}


	public boolean staleMate(Board board) {
		// TODO Auto-generated method stub
		return false;
	}


	public ArrayList<Location> parseInput(String input) {
		
		ArrayList<String> userCommands = new ArrayList<String>();
		ArrayList<Location> moves = new ArrayList<Location>();
		StringTokenizer tk = new StringTokenizer(input);
		
		for(int i = 0; tk.hasMoreTokens(); i++)
		{
			userCommands.add(tk.nextToken());
			if(userCommands.get(i).length() == 2){
				if(!Character.isLetter(userCommands.get(i).charAt(1))){
					userCommands.add(userCommands.get(i).substring(1));
					userCommands.set(i, userCommands.get(i).substring(0, 1));
					i++;
				}
			}
		}

		if(userCommands.size() == 4 || userCommands.size() == 5){
			
			
			for(int i = 0; i < 4; i += 2){
				
				if(!((int)userCommands.get(i).charAt(0) - 97 >= 0 && (int)userCommands.get(i).charAt(0) - 97 < 8)){

					System.out.println("Error: please enter a valid board location, i.e. \"e5\"\n");
					return null;
				}
				
				moves.add(new Location(8 - Integer.parseInt(userCommands.get(i+1)), ((int)userCommands.get(i).charAt(0) - 97)));
			}
	
			if(userCommands.size() == 5){
				if(!userCommands.get(4).equals("draw?")){
					
					System.out.println("Error: The only accepted command following board locations is \"draw?\"");
					return null;
				}else{
						
					moves.add(new Location(0,0));
				}
					
			}
				
			return moves;
		}else if(userCommands.size() == 1){
			

			if(userCommands.get(0).equals("resign")){

				moves.add(new Location(0, 0));
				return moves;
			}else if(userCommands.get(0).equals("draw")){

				moves.add(new Location(1,1));
				return moves;
			}
					
		}
		
		System.out.println("Error: Improper input, please use <piece location> <location to move> [\"draw?\"]\n");
		return null;
		
	}


	
}
