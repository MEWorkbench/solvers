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

import java.util.ArrayList;

public class MILPProblem extends LPProblem {

	private static final long serialVersionUID = 1L;
		
	public MILPProblem()
	{
		super();
	}

	//API simplified method added by Joao
	public void addIntVariable(String variableName,	int lowerBound, int upperBound) {
		LPVariableType variableType = LPVariableType.INTEGER;
		if(lowerBound == 0 && upperBound == 1)
			variableType = LPVariableType.BINARY;
		
		LPVariable intVar = new LPVariable(variableName, lowerBound, upperBound, variableType);
		addVariable(intVar);
	}

	public ArrayList<LPVariable> getIntVariables() {
		ArrayList<LPVariable> intVariables = new ArrayList<LPVariable>();
		for(LPVariable var:_variables)
			if(var.isInteger()) intVariables.add(var);
		return intVariables;
	}
	
	public ArrayList<LPVariable> getLinearVariables() {
		ArrayList<LPVariable> linearVariables = new ArrayList<LPVariable>();
		for(LPVariable var:_variables)
			if(!var.isInteger()) linearVariables.add(var);
		return linearVariables;
	}

//	public void addIntVariable(IntegerVariable var) {
//		LPVariable intVar = new LPVariable(var.getVariableName(), var.getLowerBound(), var.getUpperBound(), LPVariableType.BINARY);
//		
//	}
//	
}
