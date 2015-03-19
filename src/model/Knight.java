package model;

import model.Player.PlayerType;

public class Knight extends Piece{

	public Knight(PlayerType owner){
		
		if(owner == PlayerType.BLACK){
			
			this.asciiModel = "bN";
		}else{
			
			this.asciiModel = "wN";
		}
		
		this.owner = owner;
		this.moveset = new int[][]{{2,3}, {-2,3}, {-2,-3}, {2,-3}, {3,2}, {-3,2}, {-3,-2}, {3,-2}};
		this.numMoves = 1;
	}
}
