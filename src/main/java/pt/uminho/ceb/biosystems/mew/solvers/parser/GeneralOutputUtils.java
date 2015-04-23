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
package pt.uminho.ceb.biosystems.mew.solvers.parser;

import java.io.File;
import java.util.ArrayList;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPMapVariableValues;


public class GeneralOutputUtils {
	
	static boolean debug = false;
	static public LPMapVariableValues createListVariablesValues(int numVars, ArrayList<Integer> valuesIndex, ArrayList<Double> values){
		LPMapVariableValues ret = new LPMapVariableValues();
		
		if(numVars == valuesIndex.size()){
			for(int i =0; i < numVars; i++){
				ret.addVariableValue(valuesIndex.get(i), values.get(i));
			}
		}else{
			for(int i =0; i < numVars; i++){
				int index = valuesIndex.indexOf(i);
				if(index<0)
					ret.addVariableValue(i, 0.0);
				else
					ret.addVariableValue(valuesIndex.get(index), values.get(index));
			}
		}
		
		return ret;		
	}
	
	static public void createLPListVariablesAndReducedCosts(int idxOfLPListVars, int idxReduced, 
			GeneralOutputSolverFile data, LPMapVariableValues emptyVars,
			LPMapVariableValues emptyReduced){
		
		ArrayList<Integer> indexs = data.getIndexColumsnInfo();
		ArrayList<Double> values = data.getValuesColumns(idxOfLPListVars);
		ArrayList<Double> reduced = data.getValuesColumns(idxReduced);
		
		if(data.getNumberVariables() == indexs.size()){
			for(int i =0; i < data.getNumberVariables(); i++){
				emptyVars.addVariableValue(indexs.get(i), values.get(i));
				emptyReduced.addVariableValue(indexs.get(i), reduced.get(i));
			}
		}else{
			for(int i =0; i < data.getNumberVariables(); i++){
				int index = indexs.indexOf(i);
				if(index<0){
					emptyVars.addVariableValue(i, 0.0);
					emptyReduced.addVariableValue(i, 0.0);
				}
				else{
					emptyVars.addVariableValue(indexs.get(index), values.get(index));
					emptyReduced.addVariableValue(indexs.get(index), reduced.get(index));
				}
			}
		}
	}

	static public void createLPListVariablesAndReducedCostsAndShadowPrices(int idxOfLPListVars, int idxReduced,
			int idxShadow, GeneralOutputSolverFile data, LPMapVariableValues emptyVars,
			LPMapVariableValues emptyReduced, LPMapVariableValues emptyShadow){
		
		ArrayList<Integer> indexs = data.getIndexColumsnInfo();
		ArrayList<Double> values = data.getValuesColumns(idxOfLPListVars);
		ArrayList<Double> shadow = data.getValuesRows(idxShadow);
		ArrayList<Double> reduced = data.getValuesColumns(idxReduced);
		
		ArrayList<Integer> indc = data.getIndexRowsValues();
		if(debug){
			System.out.println("#########################");
			System.out.println("all info");
			for(int i =0; i < data.getColumnsInColumnInfo(); i++)
				System.out.println(i + "\t"+ data.getValuesColumns(i));
			System.out.println("#########################");
			System.out.println(idxOfLPListVars+"ROW IDX:\t"+ indc);
			System.out.println("ROW VAL:\t"+ values);
			System.out.println("ROW Shadow:\t"+ shadow);
			System.out.println("COL IDX:\t"+ indexs);
			System.out.println("COL reduced:\t"+ reduced);
		}
		for(int i =0; i < data.getNumberConstraints(); i++){
			emptyShadow.addVariableValue(indc.get(i), shadow.get(i));
		}
		
		if(data.getNumberVariables() == indexs.size()){
			for(int i =0; i < data.getNumberVariables(); i++){
				emptyVars.addVariableValue(indexs.get(i), values.get(i));
				emptyReduced.addVariableValue(indexs.get(i), reduced.get(i));
			}
		}else{
			for(int i =0; i < data.getNumberVariables(); i++){
				int index = indexs.indexOf(i);
				if(index<0){
					emptyVars.addVariableValue(i, 0.0);
					emptyReduced.addVariableValue(i, 0.0);
				}
				else{
					emptyVars.addVariableValue(indexs.get(index), values.get(index));
					emptyReduced.addVariableValue(indexs.get(index), reduced.get(index));
				}
			}
		}
	}

	static public void createLPListVariables(int idxOfLPListVars, 
		GeneralOutputSolverFile data, LPMapVariableValues emptyVars){
		
		ArrayList<Integer> indexs = data.getIndexColumsnInfo();
		ArrayList<Double> values = data.getValuesColumns(idxOfLPListVars);
		
		if(data.getNumberVariables() == indexs.size()){
			for(int i =0; i < data.getNumberVariables(); i++){
				emptyVars.addVariableValue(indexs.get(i), values.get(i));
			}
		}else{
			for(int i =0; i < data.getNumberVariables(); i++){
				int index = indexs.indexOf(i);
				if(index<0){
					emptyVars.addVariableValue(i, 0.0);
				}
				else{
					emptyVars.addVariableValue(indexs.get(index), values.get(index));
				}
			}
		}
	}
	
	static public void deleteFile(String fileName)
	{
		
		//System.out.println("FILE\t" + fileName);
		 File f = new File(fileName);

//		    if (!f.exists())
//		      throw new IllegalArgumentException(
//		          "Delete: no such file or directory: " + fileName);
//		    if (!f.canWrite())
//		      throw new IllegalArgumentException("Delete: write protected: "
//		          + fileName);
//
//		    if (f.isDirectory()) {
//		      String[] files = f.list();
//		      if (files.length > 0)
//		        throw new IllegalArgumentException(
//		            "Delete: directory not empty: " + fileName);
//		    }

		 	if(f.exists())
		 		f.delete();

//		    if (!success)
//		      throw new IllegalArgumentException("Delete: deletion failed");
	}

}
