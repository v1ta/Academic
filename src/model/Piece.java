package model;

import java.util.ArrayList;

import util.Location;
import model.Player.PlayerType;

public abstract class Piece{
	
	protected Boolean alive;
	protected String asciiModel;
	protected PlayerType owner;
	protected ArrayList<Location> validMoves;
	protected int[][] moveset;
	protected int numMoves;
	/*
	
	
	 * all chess piece move-set (minus pawn) by Juilian Garnier https://github.com/juliangarnier
	 * each number corresponds to a certain move set, particular pieces are an aggregate of move sets.
	 *
	protected static int[][] attacks = {
		{ 20, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0, 0,20},
		{ 0,20, 0, 0, 0, 0, 0, 24,  0, 0, 0, 0, 0,20, 0},
		{ 0, 0,20, 0, 0, 0, 0, 24,  0, 0, 0, 0,20, 0, 0},
		{ 0, 0, 0,20, 0, 0, 0, 24,  0, 0, 0,20, 0, 0, 0},
		{ 0, 0, 0, 0,20, 0, 0, 24,  0, 0,20, 0, 0, 0, 0},
		{ 0, 0, 0, 0, 0,20, 2, 24,  2,20, 0, 0, 0, 0, 0},
		{ 0, 0, 0, 0, 0, 2,53, 56, 53, 2, 0, 0, 0, 0, 0},
	    { 24,24,24,24,24,24,56,  0, 56,24,24,24,24,24,24}, //7,7 = piece
	    { 0, 0, 0, 0, 0, 2,53, 56, 53, 2, 0, 0, 0, 0, 0},
	    { 0, 0, 0, 0, 0,20, 2, 24,  2,20, 0, 0, 0, 0, 0},
	    { 0, 0, 0, 0,20, 0, 0, 24,  0, 0,20, 0, 0, 0, 0},
	    { 0, 0, 0,20, 0, 0, 0, 24,  0, 0, 0,20, 0, 0, 0},
	    { 0, 0,20, 0, 0, 0, 0, 24,  0, 0, 0, 0,20, 0, 0},
	    { 0,20, 0, 0, 0, 0, 0, 24,  0, 0, 0, 0, 0,20, 0},
	    { 20, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 20}
	};
	
	*/
	
	public Piece(){
		
		this.alive = true;
	}
	
	public void kill(){
		
		this.alive = false;
	}
	
	public String toString(){
		
		return this.asciiModel;
	}
	
	public int[][] getMoveSet(){
		
		return this.moveset;
	}
	
	public int getMoves(){
		
		return this.numMoves;
	}
	
	public void addValidMove(Location location){
		
		this.validMoves.add(location);
	}
	
	public PlayerType getOwner(){
		
		return this.owner;
	}
	
	public ArrayList<Location> getValidMoves(){
		
		return this.validMoves;
	}
	
	
	
	
	
	

}
