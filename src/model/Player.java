package model;

import java.util.ArrayList;

public class Player {
	
	public static enum PlayerType { Black , White };
	private static int playerCount;
	private PlayerType player;
	private ArrayList<Piece> captured;
	
	
	public Player(){
		
		if(playerCount == 0)
			this.player = PlayerType.White;
		else if(playerCount == 1)
			this.player = PlayerType.Black;
		else
			throw new IllegalArgumentException("Chess only supports two players.");
		
		this.captured = new ArrayList<Piece>();
		playerCount++;

	}
	
	public PlayerType getType(){
		return this.player;
	}
	
	public void capturePiece(Piece piece){
		
		captured.add(piece);
	}
	
	public String toString(){
		if(this.player == PlayerType.White){
			return "White";
		}else{
			return "Black";
		}
	}
}
