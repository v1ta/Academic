package model;

import util.Location;
import model.Player.PlayerType;

public class Rook extends Piece{

	public Rook(PlayerType owner, Location currentPos){
		
		super(currentPos);
		if(owner == PlayerType.Black){
			
			this.asciiModel = "bR";
		}else{
			
			this.asciiModel = "wR";
		}
		
		this.owner = owner;
		this.moveset = new int[][]{{1,0}, {0,1}, {-1,0}, {0,-1}};
		this.numMoves = 7;
	}
	
	public Rook(Piece piece){
		super(piece);
	}
	
}
