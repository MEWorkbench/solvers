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
import java.util.ArrayList;
import java.util.List;

public class QPProblemRow implements Serializable{
	
	private static final long serialVersionUID = 1L;

	protected List<QPTerm> quadTermList;
	
	public QPProblemRow(){
		quadTermList = new ArrayList<QPTerm>();
	}
	
	public void addTerm(int variableIndex, double coefficientValue) 
	{
		QPTerm term = new QPTerm(variableIndex,coefficientValue);
		quadTermList.add(term);
	}

	public void addTerm(int variableIndex1, int variableIndex2, double coefficientValue) 
	{
		QPTerm term = new QPTerm(variableIndex1, variableIndex2, coefficientValue);
		quadTermList.add(term);
	}
	
	public QPTerm getTerm(int index) {
		return quadTermList.get(index);
	}

	public int getNumberOfTerms(){
		return quadTermList.size();
	}

	public List<QPTerm> getQuadTermList() {
		return quadTermList;
	}
}
