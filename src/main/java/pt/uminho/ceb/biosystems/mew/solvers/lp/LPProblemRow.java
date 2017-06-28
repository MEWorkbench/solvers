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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.LinearProgrammingTermAlreadyPresentException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.NonExistentLPTermException;


// this class keeps a row in a LP problem (used for constraints and objective function)
public class LPProblemRow implements Serializable{
	
	private static final long serialVersionUID = 1L;

	protected Map<Integer, LPTerm> termList;
	
	public LPProblemRow(){
		termList = new TreeMap<Integer, LPTerm>();
	}
	
	public LPProblemRow(Map<Integer,LPTerm> termsMap){
		termList = termsMap;
	}
	
	public void addTerm(int variableIndex, double coefficientValue) 
		throws LinearProgrammingTermAlreadyPresentException
	{
		if(!termList.containsKey(variableIndex)){
			LPTerm term = new LPTerm(variableIndex,coefficientValue);
			termList.put(variableIndex, term);
		}else
			throw new LinearProgrammingTermAlreadyPresentException("["+variableIndex+"] = "+coefficientValue);
	}
	
	public void removeTerm(int variableIndex) throws NonExistentLPTermException{
		
		if(!termList.containsKey(variableIndex))
			throw new NonExistentLPTermException();
		termList.remove(variableIndex);
	}
	
	public void setTermCoefficient(int variableIndex, double coefficientValue) 
		throws NonExistentLPTermException{
		
		if(!termList.containsKey(variableIndex))
			throw new NonExistentLPTermException();
		
		termList.get(variableIndex).setCoefficientValue(coefficientValue);
	}
	
	
	public double getTermCoefficient(int variableIndex)
	{
		LPTerm term = termList.get(variableIndex);
		double coef = 0.0; 
		
		if(term != null)
			coef = term.getCoefficientValue();
		
		return coef;
	}
	

	public Set<Integer> getVarIdxs(){
		
		return termList.keySet();
		
	}

	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		for(Integer i : termList.keySet()){
			sb.append(termList.get(i));
			if(counter<termList.size())
				sb.append(" + ");
		}
		return sb.toString();
	}
	
	public String toStringWithNames(Map<Integer,String> indexToIdVarMappings){
		StringBuilder sb = new StringBuilder();
		int counter = 0;
		for(Integer i : termList.keySet()){
			sb.append(termList.get(i).toStringWithNames(indexToIdVarMappings));
			if(counter<termList.size())
				sb.append(" + ");
		}
		return sb.toString();
	}
	
	public LPProblemRow clone(){
		Map<Integer, LPTerm> terms = new TreeMap<Integer,LPTerm>();
		for(Integer t: termList.keySet())
			terms.put(t, termList.get(t).clone());
		
		return new LPProblemRow(terms);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		LPProblemRow other = (LPProblemRow) obj;
		if (termList == null) {
			if (other.termList != null) return false;
		} else if (!termList.equals(other.termList)) return false;
		return true;
	}
	
	

	
}
