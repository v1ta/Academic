package model;

import model.Player.PlayerType;

public class Bishop extends Piece{

	public Bishop(PlayerType owner){
		
		if(owner == PlayerType.BLACK){
			
			this.asciiModel = "bB";
		}else{
			
			this.asciiModel = "wB";
		}
		
		this.owner = owner;
		this.moveset = new int[][]{{1,1}, {-1,1}, {-1,-1}, {1, -1}};
		this.numMoves = 7;
	}
}
