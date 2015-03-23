package model;

import util.Location;
import model.Player.PlayerType;

public class Knight extends Piece{

	public Knight(PlayerType owner, Location currentPos){
		
		super(currentPos);
		if(owner == PlayerType.Black){
			
			this.asciiModel = "bN";
		}else{
			
			this.asciiModel = "wN";
		}
		
		this.owner = owner;
		this.moveset = new int[][]{{2,1}, {-2,1}, {-2,-1}, {2,-1}, {1,2}, {-1,2}, {-1,-2}, {1,-2}};
		this.numMoves = 1;
	}
	
	public Knight(Piece piece){
		super(piece);
	}
}
