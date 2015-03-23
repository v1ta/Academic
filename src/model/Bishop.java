package model;

import util.Location;
import model.Player.PlayerType;

public class Bishop extends Piece{

	public Bishop(PlayerType owner, Location currentPos){
		
		super(currentPos);
		if(owner == PlayerType.Black){
			
			this.asciiModel = "bB";
		}else{
			
			this.asciiModel = "wB";
		}
		
		this.owner = owner;
		this.moveset = new int[][]{{1,1}, {-1,1}, {-1,-1}, {1, -1}};
		this.numMoves = 7;
	}
	
	public Bishop(Piece piece)
	{
		super(piece);
	}
}
