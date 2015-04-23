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


public class QPObjectiveFunction implements Serializable
{
	
	private static final long serialVersionUID = 1L;
	
	protected QPProblemRow qpRow; // quadratic part of the Obj function
	
	public QPObjectiveFunction (QPProblemRow qprow)
	{
		this.qpRow = qprow;
	}

	public QPObjectiveFunction() {
		qpRow = new QPProblemRow();
	}

	public QPProblemRow getQpRow() {
		return qpRow;
	}

	public void setQpRow(QPProblemRow qpRow) {
		this.qpRow = qpRow;
	}
	
	public void addQPTerm(int idxvar1, int idxvar2, double coef){
		qpRow.addTerm(idxvar1, idxvar2, coef);
	}

	public void addQPTerm(int varIndex, double coef) {
		qpRow.addTerm(varIndex, coef);
		
	}
	
	
}
