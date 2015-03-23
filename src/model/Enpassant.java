package model;

import model.Player.PlayerType;
import util.Location;

public class Enpassant extends Piece{

	
	public Enpassant(PlayerType owner, Location location, Location ghost){
		
		super(location);
		this.owner = owner;
		this.ghost = ghost;
		this.turnsAlive = 0;
		this.asciiModel = "";
	}
	
	public Enpassant(Piece piece){
		super(piece);
	}
	
}
