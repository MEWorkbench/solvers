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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.GregorianCalendar;

import pt.uminho.ceb.biosystems.mew.solvers.SolverType;
import pt.uminho.ceb.biosystems.mew.solvers.fileformats.OutputFileUtils;
import pt.uminho.ceb.biosystems.mew.solvers.fileformats.QuadraticMPSInputFileProcessor;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPMapVariableValues;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolution;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.InfeasibleProblemException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverConstructionException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverDefinitionException;
import pt.uminho.ceb.biosystems.mew.solvers.parser.GeneralOutputSolverFile;
import pt.uminho.ceb.biosystems.mew.solvers.parser.GeneralOutputUtils;

public class CLPQPSolver implements IQPSolver {

	public final static String MPS_INPUT_FILE = "inputFile";
	public final static String MPS_OUTPUT_FILE = "outputFile";

	
	
	protected QPProblem qpproblem;
	
	protected String solverOutput;
	
	protected boolean usePreSolver = true;
	protected boolean useAutoScaling = true;
	
	boolean computeShadowPrices = true;
	
	protected long timeOut = 10000;
	
	
	public CLPQPSolver(QPProblem problem){
		this.qpproblem = problem;
		this.timeOut = 10000;
	}

	public CLPQPSolver(QPProblem problem, long timeOut) {
		this.qpproblem = problem;
		this.timeOut = timeOut;
	}

	public String getSolverOutput() {
		return solverOutput;
	}

	public void resetSolver() {
		solverOutput = "";
		
	}

	public LPSolution solve() throws SolverConstructionException, SolverDefinitionException, InfeasibleProblemException {
		
		String mpsInputFile = MPS_INPUT_FILE +"."+Math.random()+"."+GregorianCalendar.getInstance().getTimeInMillis();
		String mpsOutputFile = MPS_OUTPUT_FILE +"."+Math.random()+"."+ GregorianCalendar.getInstance().getTimeInMillis();

		QuadraticMPSInputFileProcessor mps = new QuadraticMPSInputFileProcessor(qpproblem);
		
		
		try {
			mps.generateMPSFile(mpsInputFile);
		} catch (IOException e) {
			throw new SolverConstructionException(CLPQPSolver.class);
		}
		
		try {
			solverOutput = runQPCLP(mpsInputFile, mpsOutputFile);
		} catch (SolverDefinitionException e) {
			GeneralOutputUtils.deleteFile(mpsInputFile);
			throw new SolverDefinitionException(CLPQPSolver.class);
		}
		
		GeneralOutputSolverFile mps_out = OutputFileUtils.createOutputParserCLP(qpproblem);

		try {
			mps_out.parserFile(mpsOutputFile);
		} catch (IOException e) {
			GeneralOutputUtils.deleteFile(mpsInputFile);
			throw new InfeasibleProblemException(CLPQPSolver.class);
		}
		
		LPMapVariableValues valuesList = new LPMapVariableValues();
		LPMapVariableValues emptyShadow = new LPMapVariableValues();
		LPMapVariableValues emptyReduced = new LPMapVariableValues();
		
		
		GeneralOutputUtils.createLPListVariablesAndReducedCostsAndShadowPrices(2, 3, 3, mps_out, valuesList, emptyReduced, emptyShadow);
		LPSolution lpSolution = new LPSolution(qpproblem, valuesList, mps_out.getSolutionType(), mps_out.getObjectiveValue());
		lpSolution.addPerVariableMetric("ReducedCosts", emptyReduced);
		lpSolution.addPerConstraintMetric("ShadowPrices", emptyShadow);
		
		//GeneralOutputUtils.createLPListVariablesAndReducedCosts(2, 3, mps_out, valuesList, reduced);
		
		//LPSolution lpSolution = new LPSolution(qpproblem, valuesList, mps_out.getSolutionType(), mps_out.getObjectiveValue());
		//lpSolution.addPerVariableMetric("ReducedCosts", reduced);

		GeneralOutputUtils.deleteFile(mpsInputFile);
		GeneralOutputUtils.deleteFile(mpsOutputFile);
		
		lpSolution.setSolverOutput(solverOutput);
		lpSolution.setSolverType(SolverType.CLP);
		
		return lpSolution;
	}
	
	protected String runQPCLP(String mpsInputFile, String mpsOutputFile) throws SolverDefinitionException  
	{
		String solverCommand = 
				"clp " + mpsInputFile
			+ " -primalS" 
			+ " -printi all" 
			+ " -sec " + timeOut
			+ " -solution " + mpsOutputFile;
		
		try{
			Process child = Runtime.getRuntime().exec(solverCommand);
	      	InputStream in = child.getInputStream();
	      	BufferedReader dataInputStream = new BufferedReader(new InputStreamReader(in));
	
			String childOutput;
			String solverOutput = "";
			
			while (((childOutput = dataInputStream.readLine()) != null))
				solverOutput += childOutput+"\n";
	
			child.destroy();
		}catch(IOException e){
			throw new SolverDefinitionException(CLPQPSolver.class);
		}
		return solverOutput;
	}

	@Override
	public void setQPProblem(QPProblem problem) {
		this.qpproblem = problem;	
	}
}
