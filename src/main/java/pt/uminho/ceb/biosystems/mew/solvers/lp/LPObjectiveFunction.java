/*
 * Copyright 2010
 * IBB-CEB - Institute for Biotechnology and Bioengineering - Centre of
 * Biological Engineering
 * CCTC - Computer Science and Technology Center
 * University of Minho
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Public License for more details.
 * You should have received a copy of the GNU Public License
 * along with this code. If not, see http://www.gnu.org/licenses/
 * Created inside the SysBioPseg Research Group (http://sysbio.di.uminho.pt)
 */
package pt.uminho.ceb.biosystems.mew.solvers.lp;

import java.io.Serializable;

import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.LinearProgrammingTermAlreadyPresentException;

public class LPObjectiveFunction implements Serializable {
	
	private static final long	serialVersionUID	= 1L;
	
	LPProblemRow				row;
	
	boolean						maximization		= true;
	
	public LPObjectiveFunction(LPProblemRow row) {
		this.row = row;
	}
	
	public LPObjectiveFunction(LPProblemRow row, boolean maximize) {
		this.row = row;
		this.maximization = maximize;
	}
	
	public LPProblemRow getRow() {
		return row;
	}
	
	public void setRow(LPProblemRow row) {
		this.row = row;
	}
	
	public boolean isMaximization() {
		return maximization;
	}
	
	public void setMaximization(boolean maximization) {
		this.maximization = maximization;
	}
	
	public void addRow(int variableIndex, double coefficientValue) throws LinearProgrammingTermAlreadyPresentException {
		row.addTerm(variableIndex, coefficientValue);
	}
	
	public LPObjectiveFunction clone() {
		LPProblemRow newrow = row.clone();
		return new LPObjectiveFunction(newrow, maximization);
	}
	
	public String toString(){
		return ((maximization) ? "maximize" : "minimize") + "\t" + row.toString() ;
	}
}
