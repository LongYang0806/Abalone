package com.longyang.abalone.api;

/**
 * Class used to represent a 2D (x, y) position.
 *
 * @author Long Yang (ly603@nyu.edu)
 *
 */
public class Position implements Comparable<Position>{
	private final int x;
	private final int y;
	
	public Position(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
	
	@Override
	public String toString(){
		if(this != null){
			return "(" + x + ", " + y + ")";
		}else {
			return null;
		}
	}
	
	@Override
	public boolean equals(Object other){
		if(other == null){
			return false;
		}
		if(other == this) {
			return true;
		}
		if(other instanceof Position){
			Position otherPosition = (Position) other;
			return x == otherPosition.getX() && y == otherPosition.getY();
		}else{
			return false;
		}
	}

	@Override
	public int compareTo(Position o) {
		if(o == null){
			return 1;
		}else{
			if(x > o.getX()){
				return 1;
			}else if(x == o.getX()){
				if(y > o.getY()){
					return 1;
				}else if(y == o.getY()){
					return 0;
				}else{
					return -1;
				}
			}else{
				return -1;
			}
		}
	}
	
}
