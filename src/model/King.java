package model;

import util.Location;
import model.Player.PlayerType;

public class King extends Piece{

	public King(PlayerType owner, Location currentPos){
		
		super(currentPos);
		if(owner == PlayerType.Black){
			
			this.asciiModel = "bK";
			
		}else{
			
			this.asciiModel = "wK";
		}
		
		this.owner = owner;
		this.moveset = new int[][] {{1,1}, {-1,1}, {-1,-1}, {1, -1}, {1,0}, {0,1}, {-1,0}, {0,-1}};
		this.sMoveset = new int[][] {{0,1} , {0,-1}};
		this.numMoves = 1;
	}
	
	public King(Piece piece){
		super(piece);
	}

}
