package model;

import java.util.ArrayList;

public class Player {
	
	protected static enum PlayerType { BLACK , WHITE };
	private static int playerCount;
	private PlayerType player;
	private ArrayList<Piece> captured;
	
	
	public Player(){
		
		if(playerCount == 0)
			this.player = PlayerType.WHITE;
		else if(playerCount == 1)
			this.player = PlayerType.BLACK;
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


}
