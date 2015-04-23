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
import java.util.ArrayList;
import java.util.List;

import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.NonExistentLPVariableException;


public class LPListVariableValues implements Serializable{
	
		private static final long serialVersionUID = 1L;

		protected List<LPVariableValue> valuesList;

	    public LPListVariableValues() {
	        valuesList = new ArrayList<LPVariableValue>();
	    }

	    public int size() {
	        return valuesList.size();
	    }

	    public LPVariableValue get(int i) {
	        return valuesList.get(i);
	    }

	    public void addVariableValue(int varIndex, double varValue) 
	    	//throws AlreadyPresentFluxMeasureException 
	    {
	        LPVariableValue cell = null;
	        cell = getVariableValue(varIndex);
	        if (cell == null)
	        	valuesList.add(new LPVariableValue(varIndex, varValue));
	        else
	        	valuesList.add(varIndex, new LPVariableValue(varIndex, varValue));
	        //    throw new AlreadyPresentFluxMeasureException();
	    }

	    public void setVariableValue(int varIndex, double varValue) throws NonExistentLPVariableException {
	        LPVariableValue cell = getVariableValue(varIndex);
	        if (cell != null)
	            cell.setVariableValue(varValue);
	        else
	            throw new NonExistentLPVariableException();
	    }


	    protected LPVariableValue getVariableValue (int varIndex) {

	        for (LPVariableValue cell : valuesList)
	            if (cell.getVariableIndex() == varIndex)
	                return cell;

	        return null;
	    }
}
