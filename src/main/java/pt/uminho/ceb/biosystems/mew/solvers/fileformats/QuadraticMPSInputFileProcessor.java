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
package pt.uminho.ceb.biosystems.mew.solvers.fileformats;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

import pt.uminho.ceb.biosystems.mew.solvers.qp.QPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPProblemRow;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPTerm;

public class QuadraticMPSInputFileProcessor extends MPSInputFormat
{
	
	public QuadraticMPSInputFileProcessor(QPProblem problem)
	{
		super(problem);
	}
	
	public void generateMPSFile(String fileName)throws IOException 
	{
		FileWriter fileDescriptor = new FileWriter(fileName);
		BufferedWriter fileWriter = new BufferedWriter(fileDescriptor);
		String objectiveFunctionName = "COST";

		writeProblemName(fileWriter, "QP");
		fileWriter.write("ROWS\n");
		writeObjectiveFunctionName(fileWriter, objectiveFunctionName);

		writeRows(fileWriter); // the same

		writeColumns(fileWriter, objectiveFunctionName);

		writeRHS(fileWriter);

		writeVariableBounds(fileWriter);
		
		writeQuadObj(fileWriter);
		
		fileWriter.write("ENDATA\n");

		fileWriter.flush();
		fileWriter.close();
		fileDescriptor.close();

	}
	
	public void writeQuadObj(BufferedWriter fileWriter) throws IOException
	{
		fileWriter.write("QUADOBJ\n");
		
		QPProblemRow row = ((QPProblem)problem).getQPObjectiveFunction().getQpRow();
		int numberTerms = row.getNumberOfTerms();
				
		TreeMap<Integer, TreeMap<Integer, Double>> values = new TreeMap<Integer, TreeMap<Integer,Double>>();
		for(int i=0; i < numberTerms; i++)
		{
			QPTerm term = row.getTerm(i);
			double value = ((problem.getObjectiveFunction().isMaximization()) ? (-1.0*term.getCoefficientValue()) : term.getCoefficientValue());
			
			
			if(value!=0){
				
				int var1 = term.getVariableIndex1();
				int var2 = term.getVariableIndex2();
				
				TreeMap<Integer, Double> secondPart = values.get(var1);
				if(secondPart == null)
					secondPart = new TreeMap<Integer, Double>();
				
				secondPart.put(var2, value);
				values.put(var1, secondPart);
				
			}	
		}
		
		for(int var1 : values.keySet()){
			TreeMap<Integer, Double> secondPart = values.get(var1);
			for(int var2 : secondPart.keySet()){
				double value = secondPart.get(var2);
				fileWriter.write("    ");
				String s1 = "X"+ var1;
				fileWriter.write(s1);
				for(int k=0; k < 10-s1.length(); k++)
					fileWriter.write(" ");
				String s2 = "X" + var2;
				fileWriter.write(s2);
				for(int k=0; k < 10-s2.length(); k++)
					fileWriter.write(" ");
				fileWriter.write(value+ "\n");
			}
		}
		
	}
	
//	protected void writeColumns(BufferedWriter fileWriter, String objectiveFunctionName) throws IOException {
//
//		fileWriter.write("COLUMNS\n");
//
//		ArrayList<LPVariable> variables = this.problem.getVariables();
//
//		LPObjectiveFunction objectiveFunction = this.problem.getObjectiveFunction();
//
//		ArrayList<LPConstraint> constraints = this.problem.getConstraints();
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
//	
//		for (int i = 0; i < numberOfVariables; i++) {
//
//			for (int j = 0; j < numberOfConstraints; j++) {
//				double value = problem.getConstraint(j).getLeftSide().getTermCoefficient(i);
//				if (value != 0) {
//					writeColumnData(fileWriter, value, i, j);
//				}
//			}
//			
//			double value = objectiveFunction.getRow().getTerm(i).getCoefficientValue();
//			if(value!=0){
//				int index = objectiveFunction.getRow().getTerm(i).getVariableIndex();
//				writeObjectiveFunctionInfo(fileWriter, problem.getObjectiveFunction().isMaximization(),
//						index, value, objectiveFunctionName);
//			}
//			
//			
//			
//			//fileWriter.write("\n");
//			//writeInsertLineFeedColumn(fileWriter, currentNumberOfLineElements);
//
//		}
//
//	}
}
