package model;

import model.Player.PlayerType;

public class Queen extends Piece{

	public Queen(PlayerType owner){
		
		if(owner == PlayerType.BLACK){
			
			this.asciiModel = "bQ";
		}else{
			
			this.asciiModel = "wQ";
		}
		
		this.owner = owner;
		this.moveset = new int[][] {{1,1}, {-1,1}, {-1,-1}, {1, -1}, {1,0}, {0,1}, {-1,0}, {0,-1}};
		this.numMoves = 7;
	}

}
