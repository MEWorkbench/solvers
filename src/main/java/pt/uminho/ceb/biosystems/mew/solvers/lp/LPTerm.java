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

// This class keeps a term constant*LPvariable
public class LPTerm implements Serializable{
	
	private static final long serialVersionUID = 1L;

	protected double coefficientValue;
	protected int variableIndex;
	
	public LPTerm(int variableIndex, double coefficientValue){
		this.coefficientValue = coefficientValue;
		this.variableIndex = variableIndex;
	}

	public double getCoefficientValue(){
		return coefficientValue;
	}

	public int getVariableIndex(){
		return variableIndex;
	}

	public void setCoefficientValue(double coefficientValue){
		this.coefficientValue = coefficientValue;
	}
	
	public String toString(){
		return variableIndex + "*" + coefficientValue; 
	}
	
	public LPTerm clone(){
		return new LPTerm(variableIndex,coefficientValue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		LPTerm other = (LPTerm) obj;
		if (Double.doubleToLongBits(coefficientValue) != Double.doubleToLongBits(other.coefficientValue)) return false;
		if (variableIndex != other.variableIndex) return false;
		return true;
	}

	
}
