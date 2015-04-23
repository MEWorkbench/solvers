package pt.uminho.ceb.biosystems.mew.solvers.fileformats;
///*
// * Copyright 2010
// * IBB-CEB - Institute for Biotechnology and Bioengineering - Centre of Biological Engineering
// * CCTC - Computer Science and Technology Center
// *
// * University of Minho 
// * 
// * This is free software: you can redistribute it and/or modify 
// * it under the terms of the GNU Public License as published by 
// * the Free Software Foundation, either version 3 of the License, or 
// * (at your option) any later version. 
// * 
// * This code is distributed in the hope that it will be useful, 
// * but WITHOUT ANY WARRANTY; without even the implied warranty of 
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
// * GNU Public License for more details. 
// * 
// * You should have received a copy of the GNU Public License 
// * along with this code. If not, see http://www.gnu.org/licenses/ 
// * 
// * Created inside the SysBioPseg Research Group (http://sysbio.di.uminho.pt)
// */
//package solvers.fileformats;
//
//import java.io.BufferedWriter;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.optflux.solvers.lp.LPConstraint;
//import org.optflux.solvers.lp.LPConstraintType;
//import org.optflux.solvers.lp.LPObjectiveFunction;
//import org.optflux.solvers.lp.LPProblem;
//import org.optflux.solvers.lp.LPTerm;
//import org.optflux.solvers.lp.LPVariable;
//import org.optflux.solvers.lp.MILPProblem;
//
//
//
//public class FreeMPSInputFileProcessor implements LPInputFileFormat
//{
//
//	protected LPProblem problem;
//
//	public FreeMPSInputFileProcessor(LPProblem problem)
//	{
//		this.problem = problem;
//	}
//
//	@Override
//	public LPFormatTypes getFormat ()
//	{
//		return LPFormatTypes.FreeMPS;
//	}
//	
//	@Override
//	public void writeLPFile(String filename) throws Exception 
//	{
//		generateMPSFile(filename);
//	}
//
//	public void generateMPSFile(String fileName)throws IOException 
//	{
//		FileWriter fileDescriptor = new FileWriter(fileName);
//		BufferedWriter fileWriter = new BufferedWriter(fileDescriptor);
//		String objectiveFunctionName = "COST";
//
//		writeProblemName(fileWriter, "LP");
//		fileWriter.write("ROWS\n");
//		writeObjectiveFunctionName(fileWriter, objectiveFunctionName);
//
//		writeRows(fileWriter);
//
//		writeColumns(fileWriter, objectiveFunctionName);
//
//		writeRHS(fileWriter);
//
//		writeVariableBounds(fileWriter);
//		fileWriter.write("ENDATA");
//
//		fileWriter.flush();
//		fileWriter.close();
//		fileDescriptor.close();
//
//	}
//
//	protected void writeProblemName(BufferedWriter fileWritter, String problemName) 
//	throws IOException 
//	{
//		fileWritter.write("NAME " + problemName + "\n");
//	}
//
//	protected void writeObjectiveFunctionName(BufferedWriter fileWriter,
//			String objectiveFunction) throws IOException {
//
//		fileWriter.write(" N  " + objectiveFunction + "\n");
//	}
//
//	protected void writeRows(BufferedWriter fileWritter) throws IOException {
//
//		List<LPConstraint> constraints = this.problem.getConstraints();
//		for (int i = 0; i < constraints.size(); i++)
//		{			
//			LPConstraint c = constraints.get(i);
//			if (c.getType()==LPConstraintType.EQUALITY)
//			{
//				fileWritter.write(" E  L" + i + "\n");
//			}
//			else if (c.getType() == LPConstraintType.GREATER_THAN)
//			{
//				fileWritter.write(" G  L" + i + "\n");
//			}
//			else if (c.getType()==LPConstraintType.LESS_THAN)
//			{
//				fileWritter.write(" L  L" + i + "\n");
//			}
//		}
//	}
//
//	protected void writeColumns(BufferedWriter fileWriter, String objectiveFunctionName) throws IOException {
//
//		fileWriter.write("COLUMNS\n");
//
//		List<LPVariable> variables = this.problem.getVariables();
//
//		LPObjectiveFunction objectiveFunction = this.problem.getObjectiveFunction();
//
//		List<LPConstraint> constraints = this.problem.getConstraints();
//
//		int numberOfVariables = variables.size();
//		int numberOfConstraints = constraints.size();
//
//		if (MILPProblem.class.isAssignableFrom(problem.getClass()) )
//		{
//			int numberIntVariables = ((MILPProblem)problem).getIntVariables().size();
//			numberOfVariables += numberIntVariables;
//		}
//		
//		int currentObjectiveIndex = 0;
//		int numberOfObjectiveTerms = objectiveFunction.getRow().getNumberOfTerms();
//
//		for (int i = 0; i < numberOfVariables; i++) {
//			int currentNumberOfLineElements = 0;
//
//			for (int j = 0; j < numberOfConstraints; j++) {
//				double value = problem.getConstraint(j).getLeftSide().getTermCoefficient(i);
//				if (value != 0) {
//					currentNumberOfLineElements++;
//					writeColumnData(fileWriter, value, i, j, currentNumberOfLineElements);
//				}
//			}
//
//			if (currentObjectiveIndex <= (numberOfObjectiveTerms - 1)) {
//				LPTerm term = objectiveFunction.getRow().getTerm(currentObjectiveIndex);
//				Double coefficientValue = term.getCoefficientValue();
//				int variableIndex = term.getVariableIndex();
//
//				if (coefficientValue != 0 && (i == variableIndex)) {
//					writeObjectiveFunctionInfo(fileWriter, problem.getObjectiveFunction().isMaximization(),
//							currentNumberOfLineElements, i, coefficientValue, objectiveFunctionName);
//					currentNumberOfLineElements++;
//					currentObjectiveIndex++;
//				}
//			}
//			writeInsertLineFeedColumn(fileWriter, currentNumberOfLineElements);
//
//		}
//
//	}
//
//	protected void writeObjectiveFunctionInfo(BufferedWriter fileWriter, boolean isMaximization, 
//			int currentNumberOfRows, int variablePosition, Double variableValue, String objectiveFunction)	
//			throws IOException {
//
//		if (currentNumberOfRows % 2 == 0)
//			fileWriter.write(" X" + variablePosition);
//
//		if (isMaximization)
//			fileWriter.write(" " + objectiveFunction + " " + -1*variableValue);
//		else
//			fileWriter.write(" " + objectiveFunction + " " +  variableValue);
//
//		writeInsertLineFeedColumn(fileWriter, currentNumberOfRows);
//	}
//
//	protected void writeInsertLineFeedColumn(BufferedWriter fileWriter,	int currentNumberOfRows) throws IOException {
//		if (currentNumberOfRows % 2 != 0) fileWriter.write("\n");
//	}
//
//
//	protected void writeColumnData(BufferedWriter fileWriter, double value, int currentVariable, 
//			int currentConstraint, int currentNumberOfLineElements) throws IOException {
//
//		if (currentNumberOfLineElements % 2 != 0)
//			fileWriter.write(" X" + currentVariable + " ");
//
//		fileWriter.write("L" + currentConstraint);
//		fileWriter.write(" " + value);
//
//		if (currentNumberOfLineElements % 2 == 0)
//			fileWriter.write("\n");
//		else
//			fileWriter.write(" ");
//	}
//
//	protected void writeRHS(BufferedWriter fileWriter) throws IOException 
//	{
//
//		List<LPConstraint> constraints = this.problem.getConstraints();
//
//		fileWriter.write("RHS\n");
//
//		for(int i=0;i<constraints.size();i++)
//		{
//			double rhs = constraints.get(i).getRightSide();
//			if (rhs != 0)
//			{
//				fileWriter.write(" RHS1"+" ");
//				fileWriter.write("L"+i+" "+rhs);
//				fileWriter.write("\n");
//			}
//		}
//	}
//
//	protected void writeVariableBounds(BufferedWriter fileWriter) throws IOException {
//
//		fileWriter.write("RANGES\nBOUNDS\n");
//		int numberOfVariables = problem.getVariables().size();
//
//		for (int i = 0; i < numberOfVariables; i++) {
//			LPVariable var = problem.getVariable(i);
//			//String variableId = var.getVariableName();
//			// Usar nome da variavel ??
//			if(var.isInteger())
//				writeIntBoundConstraint(fileWriter, "X" + i, var.isBinary(), (int)var.getLowerBound(), (int)var.getUpperBound()); 
//			else
//				writeBoundConstraint(fileWriter, "X" + i, var.getLowerBound(), var.getUpperBound());
//		}
//		
//	}
//		
//	protected void writeBoundConstraint(BufferedWriter fileWriter,String variableName, 
//			double lowerLimit, double upperLimit) throws IOException {
//
//		if (lowerLimit == upperLimit) {
//			fileWriter.write(" FX BND1     ");
//			fileWriter.write(variableName);
//			fileWriter.write(" "+ upperLimit +"\n");
//		}
////		else if (lowerLimit == ReactionConstraint.MINUS_INFINITY //TODO tirar isto- Gera sempre bounds !!!!
////		&& upperLimit == ReactionConstraint.PLUS_INFINITY) {
////		fileWriter.write(" FR BND1     ");
////		fileWriter.write(variableName + "\n");
//		else {
//			fileWriter.write(" LO BND1     ");
//			fileWriter.write(variableName);
//			fileWriter.write(" " + lowerLimit + "\n");
//			fileWriter.write(" UP BND1     ");
//			fileWriter.write(variableName);
//			fileWriter.write(" " + upperLimit + "\n");
//		}
//	}
//
//	protected void writeIntBoundConstraint(BufferedWriter fileWriter,String variableName, 
//			boolean isBinary, int lowerLimit, int upperLimit) throws IOException
//	{
//		if (isBinary) {
//			fileWriter.write(" BV BND1     ");
//			fileWriter.write(variableName);
//			fileWriter.write("\n"); 	
//		}
//		else {
//			fileWriter.write(" LI BND1     ");
//			fileWriter.write(variableName);
//			fileWriter.write(" " + lowerLimit + "\n");
//			fileWriter.write(" UI BND1     ");
//			fileWriter.write(variableName);
//			fileWriter.write(" " + upperLimit + "\n");
//		}
//	}
//
//
//	
//
//}