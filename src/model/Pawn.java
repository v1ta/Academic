package model;

import util.Location;
import model.Player.PlayerType;

public class Pawn extends Piece{
	
	public Pawn(PlayerType owner, Location currentPos){
		
		super(currentPos);
		if(owner == PlayerType.Black){
			
			this.asciiModel = "bp";
			this.upgradeLoc = 7;
			this.moveset = new int[][]{{1, 0}};
			this.sMoveset = new int[][]{{1, -1} , {1 , 1}};
		}else{
			
			this.asciiModel = "wp";
			this.upgradeLoc = 0;
			this.moveset = new int[][]{{-1, 0}};
			this.sMoveset = new int[][]{{-1, -1} , {-1 , 1}};
		}
		
		this.owner = owner;
		this.numMoves = 1;
	}
	
	public Pawn(Piece piece){
		super(piece);
	}
	

}
