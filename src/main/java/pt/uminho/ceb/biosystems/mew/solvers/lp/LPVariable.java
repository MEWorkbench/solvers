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

public class LPVariable implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public String variableName; 
	
	public double lowerBound;
	
	public double upperBound;
	
	LPVariableType variableType = LPVariableType.LINEAR;
	
	public LPVariable (String name, double lowerBound, double upperBound)
	{
		this.variableName = name;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	public LPVariable (String name, double lowerBound, double upperBound, LPVariableType variableType)
	{
		this.variableName = name;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.variableType = variableType;
	}
	
	public LPVariable(String variableName, double variableValue){
		this.variableName = variableName;
		this.lowerBound = variableValue;
		this.upperBound = variableValue;
	}
	
	public LPVariable(String variableName, double variableValue, LPVariableType variableType) {
		this.variableName = variableName;
		this.lowerBound = variableValue;
		this.upperBound = variableValue;
		this.variableType = variableType;
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}
	
	public boolean isInteger() {
		return (variableType.equals(LPVariableType.INTEGER) || variableType.equals(LPVariableType.BINARY));
	}
	
	public boolean isBinary() {
		return (variableType.equals(LPVariableType.BINARY));
	}
	
	public String toString(){
		return variableName + "\t["+lowerBound+","+upperBound+"]";
	}
	
	public LPVariableType getVariableType() {
		return variableType;
	}
	
	public LPVariable clone(){
		return new LPVariable(variableName,lowerBound,upperBound,variableType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		LPVariable other = (LPVariable) obj;
		if (Double.doubleToLongBits(lowerBound) != Double.doubleToLongBits(other.lowerBound)) return false;
		if (Double.doubleToLongBits(upperBound) != Double.doubleToLongBits(other.upperBound)) return false;
		if (variableName == null) {
			if (other.variableName != null) return false;
		} else if (!variableName.equals(other.variableName)) return false;
		if (variableType != other.variableType) return false;
		return true;
	}
	
	

	
}
