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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;


public class GeneralOutputSolverFile {
	
	private LPProblem problem;
	
	private SolutionDataMatrix rowInfo;
	private SolutionDataMatrix columnsInfo;
	
	private boolean hasRowInfo = true;
	
	private Integer lineIndexSolutionType = 0;
	
	private String regexpOptimalSolutionType = "\\s*optimal\\s*";
	
	private Integer lineObjectiveValue = 1;
	
	private String regExpDataRowInfo = "\\**\\s+(\\d+)\\s+([^\\s]+)\\s+([^\\s]+)\\s+([^\\s]+)";
	private String regExpDataColumnsInfo = "\\**\\s+(\\d+)\\s+([^\\s]+)\\s+([^\\s]+)\\s+([^\\s]+)";
	private String regExpObjectiveValue = "Objective value\\s+(.*)";
	
	private int headerRowInfo = 2;
	private int headerColumnInfo=0;
	
	private int columnsInRowInfo = 4;
	private int columnsInColumnInfo = 4;
	
	private int indexValuesRowInfo = 0;
	private int indexValuesColumnInfo = 0;
	
	private int initialIdxColumns = 0;
	private int initialIdxRows = 0;
	
	public GeneralOutputSolverFile (LPProblem problem)
	{
		this.problem = problem;
	}
	
	public boolean isHasRowInfo() {
		return hasRowInfo;
	}

	public void setHasRowInfo(boolean hasRowInfo) {
		this.hasRowInfo = hasRowInfo;
	}

	public Integer getLineIndexSolutionType() {
		return lineIndexSolutionType;
	}

	public void setLineIndexSolutionType(Integer lineIndexSolutionType) {
		this.lineIndexSolutionType = lineIndexSolutionType;
	}

	public String getRegexpOptimalSolutionType() {
		return regexpOptimalSolutionType;
	}

	public void setRegexpOptimalSolutionType(String regexpOptimalSolutionType) {
		this.regexpOptimalSolutionType = regexpOptimalSolutionType;
	}

	public Integer getLineObjectiveValue() {
		return lineObjectiveValue;
	}

	public void setLineObjectiveValue(Integer lineObjectiveValue) {
		this.lineObjectiveValue = lineObjectiveValue;
	}

	public String getRegExpDataRowInfo() {
		return regExpDataRowInfo;
	}

	public void setRegExpDataRowInfo(String regExpDataRowInfo) {
		this.regExpDataRowInfo = regExpDataRowInfo;
	}

	public String getRegExpDataColumnsInfo() {
		return regExpDataColumnsInfo;
	}

	public void setRegExpDataColumnsInfo(String regExpDataColumnsInfo) {
		this.regExpDataColumnsInfo = regExpDataColumnsInfo;
	}

	public String getRegExpObjectiveValue() {
		return regExpObjectiveValue;
	}

	public void setRegExpObjectiveValue(String regExpObjectiveValue) {
		this.regExpObjectiveValue = regExpObjectiveValue;
	}

	public int getHeaderRowInfo() {
		return headerRowInfo;
	}

	public void setHeaderRowInfo(int headerRowInfo) {
		this.headerRowInfo = headerRowInfo;
	}

	public int getHeaderColumnInfo() {
		return headerColumnInfo;
	}

	public void setHeaderColumnInfo(int headerColumnInfo) {
		this.headerColumnInfo = headerColumnInfo;
	}

	public int getColumnsInRowInfo() {
		return columnsInRowInfo;
	}

	public void setColumnsInRowInfo(int columnsInRowInfo) {
		this.columnsInRowInfo = columnsInRowInfo;
	}

	public int getColumnsInColumnInfo() {
		return columnsInColumnInfo;
	}

	public void setColumnsInColumnInfo(int columnsInColumnInfo) {
		this.columnsInColumnInfo = columnsInColumnInfo;
	}

	public int getIndexValuesRowInfo() {
		return indexValuesRowInfo;
	}

	public void setIndexValuesRowInfo(int indexValuesRowInfo) {
		this.indexValuesRowInfo = indexValuesRowInfo;
	}

	public int getIndexValuesColumnInfo() {
		return indexValuesColumnInfo;
	}

	public void setIndexValuesColumnInfo(int indexValuesColumnInfo) {
		this.indexValuesColumnInfo = indexValuesColumnInfo;
	}

	public void parserFile(String file) throws IOException{
		
		if(hasRowInfo){
			rowInfo = new SolutionDataMatrix(columnsInRowInfo, problem.getNumberConstraints(), regExpDataRowInfo, headerRowInfo, indexValuesRowInfo,initialIdxRows);
		}else{
			rowInfo = new SolutionDataMatrix(0, 0, regExpDataRowInfo, headerRowInfo, indexValuesRowInfo,initialIdxRows);
		}
		
		
		
		columnsInfo = new SolutionDataMatrix(columnsInColumnInfo, problem.getNumberVariables(), regExpDataColumnsInfo, headerColumnInfo, indexValuesColumnInfo,initialIdxColumns);
		
		FileReader filer = new FileReader(file);
		BufferedReader reader = new BufferedReader(filer);
		
		rowInfo.readerInFile(reader);
		columnsInfo.readerInFile(reader);
		
		reader.close();
		filer.close();
				
//		String ofString = rowInfo.getLineOfHeader(idxObjectiveLine);
//		Matcher m = objectiveFunctionValue.matcher(ofString);
//		
//		if(m.matches()){
//			String v = m.group(1);
//			solution = Double.parseDouble(v);
//			
//			if(problem.getObjectiveFunction().isMaximization()) this.solution = -solution;
//		}
//		
//		
		
	}
	
	public ArrayList<Integer> getIndexColumsnInfo(){
		return columnsInfo.getIndexValues();
	}
	
	public ArrayList<Double> getValuesColumns(int index){
		return columnsInfo.getValues(index);
	}
	
	public ArrayList<Integer> getIndexRowsValues(){
		return rowInfo.getIndexValues();
	}
	
	public ArrayList<Double> getValuesRows(int index){
		return rowInfo.getValues(index);
	}
	
	public Double getObjectiveValue(){
		String line = rowInfo.getLineOfHeader(lineObjectiveValue);
		
		Pattern p =  Pattern.compile(regExpObjectiveValue); 
		Matcher m = p.matcher(line);
		m.matches();
		
		double ret = Double.parseDouble(m.group(1));
		
		if(problem.getObjectiveFunction().isMaximization()) ret=-ret;
		return ret;
	}
	
	public LPSolutionType getSolutionType(){
		String line = rowInfo.getLineOfHeader(lineIndexSolutionType);
		Pattern p = Pattern.compile(regexpOptimalSolutionType);
		Matcher m = p.matcher(line);
		if(m.matches())
			return LPSolutionType.OPTIMAL;
		
		return LPSolutionType.UNDEFINED;
	}
	
	public Integer getNumberVariables(){
		return problem.getNumberVariables();
	}
	
	public Integer getNumberConstraints(){
		return problem.getNumberConstraints();
	}

	public int getInitialIdxColumns() {
		return initialIdxColumns;
	}

	public void setInitialIdxColumns(int initialIdxColumns) {
		this.initialIdxColumns = initialIdxColumns;
	}

	public int getInitialIdxRows() {
		return initialIdxRows;
	}

	public void setInitialIdxRows(int initialIdxRows) {
		this.initialIdxRows = initialIdxRows;
	}

//	public void setOFValueRegularExpresion(int i, Pattern p) {
//		this.idxObjectiveLine = i;
//		this.objectiveFunctionValue = p;
//	}
//
//	public double getSolution(){
//		return solution;
//	}
}
