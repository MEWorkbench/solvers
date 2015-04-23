/*
 * Copyright 2010
 * IBB-CEB - Institute for Biotechnology and Bioengineering - Centre of Biological Engineering
 * CCTC - Computer Science and Technology Center
 *
 * University of Minho 
 * 
 * This is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This code is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Public License for more details. 
 * 
 * You should have received a copy of the GNU Public License 
 * along with this code. If not, see http://www.gnu.org/licenses/ 
 * 
 * Created inside the SysBioPseg Research Group (http://sysbio.di.uminho.pt)
 */
package pt.uminho.ceb.biosystems.mew.solvers.lp;

import java.io.Serializable;

public class LPConstraint implements Serializable{
	
	private static final long serialVersionUID = 1L;

	LPConstraintType type;
	
	LPProblemRow leftSide;
	
	double rightSide;
	
	public String name;
	
	public LPConstraint(LPConstraintType type, LPProblemRow left, double right)
	{
		this.type = type;
		this.leftSide = left;
		this.rightSide = right;
	}

	public LPConstraintType getType() {
		return type;
	}

	public void setType(LPConstraintType type) {
		this.type = type;
	}

	public LPProblemRow getLeftSide() {
		return leftSide;
	}

	public void setLeftSide(LPProblemRow leftSide) {
		this.leftSide = leftSide;
	}

	public double getRightSide() {
		return rightSide;
	}

	public void setRightSide(double rightSide) {
		this.rightSide = rightSide;
	}
	
	public String toString(){		
		String operator ="";
		switch (type) {
			case EQUALITY:
				operator = " = ";
				break;
			case GREATER_THAN:
				operator = " >= ";
				break;
			case LESS_THAN:
				operator = " <= ";
				break;
			default:
				break;
		}
		return leftSide.toString() + operator + rightSide;
	}
	
	public LPConstraint clone(){
		LPProblemRow row = leftSide.clone();
		LPConstraint toret = new LPConstraint(type, row, rightSide);
		toret.name = name;
		return toret;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		LPConstraint other = (LPConstraint) obj;
		if (leftSide == null) {
			if (other.leftSide != null) return false;
		} else if (!leftSide.equals(other.leftSide)) return false;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		if (Double.doubleToLongBits(rightSide) != Double.doubleToLongBits(other.rightSide)) return false;
		if (type != other.type) return false;
		return true;
	}
	
	
	
}
