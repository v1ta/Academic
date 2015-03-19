package model;

import model.Player.PlayerType;

public class King extends Piece{

	public King(PlayerType owner){
		
		if(owner == PlayerType.BLACK){
			
			this.asciiModel = "bK";
		}else{
			
			this.asciiModel = "wK";
		}
		
		this.owner = owner;
		this.moveset = new int[][] {{1,1}, {-1,1}, {-1,-1}, {1, -1}, {1,0}, {0,1}, {-1,0}, {0,-1}};
		this.numMoves = 1;
	}

}
