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
package pt.uminho.ceb.biosystems.mew.solvers.qp;

import java.io.Serializable;

public class QPTerm implements Serializable{
	
	private static final long serialVersionUID = 1L;

	protected double coefficientValue;
	protected int variableIndex1;
	protected int variableIndex2;
	
	
	public QPTerm(int variableIndex, double coefficientValue){
		this.coefficientValue = coefficientValue;
		this.variableIndex1 = variableIndex;
		this.variableIndex2 = variableIndex;
	}
	
	
	public QPTerm(int variableIndex1, int variableIndex2, double coefficientValue) 
	{
		this.coefficientValue = coefficientValue;
		this.variableIndex1 = variableIndex1;
		this.variableIndex2 = variableIndex2;
	}


	public double getCoefficientValue() {
		return coefficientValue;
	}

	public void setCoefficientValue(double coefficientValue) {
		this.coefficientValue = coefficientValue;
	}

	public int getVariableIndex1() {
		return variableIndex1;
	}

	public void setVariableIndex1(int variableIndex1) {
		this.variableIndex1 = variableIndex1;
	}

	public int getVariableIndex2() {
		return variableIndex2;
	}

	public void setVariableIndex2(int variableIndex2) {
		this.variableIndex2 = variableIndex2;
	}

	
}
