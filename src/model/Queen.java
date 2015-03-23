package model;

import util.Location;
import model.Player.PlayerType;

public class Queen extends Piece{

	public Queen(PlayerType owner, Location currentPos){
		
		super(currentPos);
		if(owner == PlayerType.Black){
			
			this.asciiModel = "bQ";
		}else{
			
			this.asciiModel = "wQ";
		}
		
		this.owner = owner;
		this.moveset = new int[][] {{1,1}, {-1,1}, {-1,-1}, {1, -1}, {1,0}, {0,1}, {-1,0}, {0,-1}};
		this.numMoves = 7;
	}
	
	public Queen(Piece piece){
		super(piece);
	}

}
