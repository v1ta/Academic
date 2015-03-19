package model;

import model.Player.PlayerType;

public class Board {

	private Piece[][] cells;
	
	public Board(){
		
		this.cells = new Piece[8][8];
		
	}
	
	public void printBoard(){
		
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				
				if(this.isEmpty(i, j)){
					if(i % 2 == 0){
						if(j % 2 != 0){
							
							System.out.print("##");
						}else{
							
							System.out.print("  ");
						}
					}
					else{
						if(j % 2 == 0){
							
							System.out.print("##");
						}else{
							
							System.out.print("  ");
						}
					}
					
					System.out.print(" ");
				}else{
				
				System.out.print(this.cells[i][j]);
				System.out.print(" ");
				}
					
			}
			System.out.println(8 - i);
		}
		
		
		for(int i = 0; i < 8; i++){
			System.out.print(" " + Character.toString((char) ('a' + i))+ " ");
		}
		
	}
	
	
	public boolean isEmpty(int i, int j){
		
		if(this.cells[i][j] == null)
			return true;
		else
			return false;
	}
	
	public Piece getPiece(int i, int j){
		
		return this.cells[i][j];
	}
	

	public boolean loadPlayer(Player player){
		
		PlayerType owner = player.getType();
		
		if(owner == PlayerType.BLACK){
			
			this.cells[0][0] = new Rook(owner);
			this.cells[0][1] = new Knight(owner);
			this.cells[0][2] = new Bishop(owner);
			this.cells[0][3] = new Queen(owner);
			this.cells[0][4] = new King(owner);
			this.cells[0][5] = new Bishop(owner);
			this.cells[0][6] = new Knight(owner);
			this.cells[0][7] = new Rook(owner);
			
			for(int i = 0; i < 8; i++){
				
				this.cells[1][i] = new Pawn(owner);
			}
			
			return true;
			

		}else if(owner == PlayerType.WHITE){
			
			for(int i = 0; i < 8; i++){
				
				this.cells[6][i] = new Pawn(owner);
			}
				
			this.cells[7][0] = new Rook(owner);
			this.cells[7][1] = new Knight(owner);
			this.cells[7][2] = new Bishop(owner);
			this.cells[7][3] = new Queen(owner);
			this.cells[7][4] = new King(owner);
			this.cells[7][5] = new Bishop(owner);
			this.cells[7][6] = new Knight(owner);
			this.cells[7][7] = new Rook(owner);
				
			return true;
				
		}else{
			
			return false;
		}
	}
	
	public void updateBoard(Piece piece, int i, int j){
		
		this.cells[i][j] = piece;
	}
	
	
}
