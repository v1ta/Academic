package model;

import model.Player.PlayerType;

public class Pawn extends Piece{
	
	private int upgradeLoc;
	private boolean atStart;
	
	public Pawn(PlayerType owner){
		
		if(owner == PlayerType.BLACK){
			
			this.asciiModel = "bp";
			this.upgradeLoc = 7;
			this.moveset = new int[][]{{0,-1}};
		}else{
			
			this.asciiModel = "wp";
			this.upgradeLoc = 0;
			this.moveset = new int[][]{{0, 1}};
		}
		
		this.atStart = true;
		this.owner = owner;
		this.numMoves = 1;
	}
	
	public boolean atStart(){
		
		return this.atStart;
	}
	
}
