package model;

import model.Player.PlayerType;

public class Rook extends Piece{

	public Rook(PlayerType owner){
		
		if(owner == PlayerType.BLACK){
			
			this.asciiModel = "bR";
		}else{
			
			this.asciiModel = "wR";
		}
		
		this.owner = owner;
		this.moveset = new int[][]{{1,0}, {0,1}, {-1,0}, {0,-1}};
		this.numMoves = 7;
	}
	
}
