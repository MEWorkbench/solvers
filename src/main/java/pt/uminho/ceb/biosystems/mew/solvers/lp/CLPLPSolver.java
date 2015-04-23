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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.GregorianCalendar;

import pt.uminho.ceb.biosystems.mew.utilities.io.FileUtils;
import pt.uminho.ceb.biosystems.mew.utilities.java.TimeUtils;

import pt.uminho.ceb.biosystems.mew.solvers.SolverType;
import pt.uminho.ceb.biosystems.mew.solvers.fileformats.LPInputFileFormat;
import pt.uminho.ceb.biosystems.mew.solvers.fileformats.MPSInputFormat;
import pt.uminho.ceb.biosystems.mew.solvers.fileformats.OutputFileUtils;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.InfeasibleProblemException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverConstructionException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverDefinitionException;
import pt.uminho.ceb.biosystems.mew.solvers.parser.GeneralOutputSolverFile;
import pt.uminho.ceb.biosystems.mew.solvers.parser.GeneralOutputUtils;

public class CLPLPSolver implements ILPSolver{

	public final static String MPS_INPUT_FILE = "clp_inputFile";
	public final static String MPS_OUTPUT_FILE = "clp_outputFile";
	

	protected LPProblem lpProblem;
	
	protected String solverOutput;
	
	protected boolean usePreSolver = true;
	protected boolean useAutoScaling = true;
	
	boolean computeShadowPrices = true;
	
	protected long timeOut = 10000;
	
	public CLPLPSolver(){
		this.timeOut = 10000; //NOTE: MAKE THIS A VARIABLE
	}
	
	public CLPLPSolver(LPProblem problem){
		this.lpProblem = problem;
		this.timeOut = 10000;
	}
	
	public CLPLPSolver(LPProblem problem, int totalTime) {
		this.lpProblem = problem;
		this.timeOut = totalTime;
	}

	public CLPLPSolver(LPProblem problem, long timeOut) {
		this.lpProblem = problem;
		this.timeOut = timeOut;
	}

	@Override
	public boolean getComputeShadowPrices() {
		return false;
	}

	@Override
	public String getSolverOutput() {
		return solverOutput;
	}

	@Override
	public void resetSolver() {
		solverOutput = "";
		
	}

	@Override
	public void setComputeShadowPrices(boolean computeShadowPrices) {
		this.computeShadowPrices = computeShadowPrices;
		
	}

	@Override
	public void setProblem(LPProblem problem) {
		this.lpProblem = problem;
		
	}


	public LPSolution solve() throws SolverDefinitionException, InfeasibleProblemException, SolverConstructionException  {
		
		String mpsInputFile = MPS_INPUT_FILE +"."+Math.random()+"."+GregorianCalendar.getInstance().getTimeInMillis();
		String mpsOutputFile = MPS_OUTPUT_FILE +"."+Math.random()+"."+ GregorianCalendar.getInstance().getTimeInMillis();

		
		LPInputFileFormat inputFileProcessor = null;
		
		LPSolution lpSolution = null;

		inputFileProcessor = new MPSInputFormat(lpProblem);
		
		try {
			inputFileProcessor.writeLPFile(mpsInputFile);
		} catch (IOException e) {
			throw new SolverConstructionException(CLPLPSolver.class);
		}
		
		try {
			solverOutput = runCLP(mpsInputFile, mpsOutputFile);
		} catch (SolverDefinitionException e) {
			GeneralOutputUtils.deleteFile(mpsInputFile);
			throw new SolverDefinitionException(CLPLPSolver.class);
		}
		
		GeneralOutputSolverFile mps_out = OutputFileUtils.createOutputParserCLP(lpProblem);
		
		try {
			mps_out.parserFile(mpsOutputFile);
		} catch (IOException e) {
			GeneralOutputUtils.deleteFile(mpsInputFile);
			throw new InfeasibleProblemException(CLPLPSolver.class);
		}
				
		LPMapVariableValues valuesList = new LPMapVariableValues();
		if(computeShadowPrices){
			LPMapVariableValues emptyShadow = new LPMapVariableValues();
			LPMapVariableValues emptyReduced = new LPMapVariableValues();

			GeneralOutputUtils.createLPListVariablesAndReducedCostsAndShadowPrices(2, 3, 3, mps_out, valuesList, emptyReduced, emptyShadow);
			lpSolution = new LPSolution(lpProblem, valuesList, mps_out.getSolutionType(), mps_out.getObjectiveValue());
			lpSolution.addPerVariableMetric("ReducedCosts", emptyReduced);
			lpSolution.addPerConstraintMetric("ShadowPrices", emptyShadow);
		}
		else{
			GeneralOutputUtils.createLPListVariables(2, mps_out, valuesList);
			lpSolution = new LPSolution(lpProblem, valuesList, mps_out.getSolutionType(), mps_out.getObjectiveValue());
		}
		
		
		GeneralOutputUtils.deleteFile(mpsInputFile);
		GeneralOutputUtils.deleteFile(mpsOutputFile);
		
		lpSolution.setSolverOutput(solverOutput);
		lpSolution.setSolverType(SolverType.CLP);
		return lpSolution;
	}
	
	
	protected String runCLP(String mpsInputFile, String mpsOutputFile) throws SolverDefinitionException  {
		String solverCommand="";

		solverCommand = "clp "+ mpsInputFile 
				+ " -sec " + timeOut 
				+ " -primalS"
				+ " -printi all"
				+ " -solution " +mpsOutputFile;
		
		String childOutput;
		String solverOutput = "";
		
		try {
			Process child = Runtime.getRuntime().exec(solverCommand);
	      	InputStream in = child.getInputStream();
	      	BufferedReader dataInputStream = new BufferedReader(new InputStreamReader(in));
	      	
			while (((childOutput = dataInputStream.readLine()) != null))
				solverOutput += childOutput+"\n";
			
			child.destroy();
		} catch (IOException e) {
			throw new SolverDefinitionException(CLPLPSolver.class);
		}
		return solverOutput;
	}

	@Override
	public void saveModelToMPS(String file, boolean includeTime) {
		if (lpProblem != null) {
			if (includeTime) {
				String extension = FileUtils.getFileExtension(file);
				String time = TimeUtils.formatMillis(System.currentTimeMillis());
				file = file.replaceFirst(extension, "") + "_" + time + "." + extension;
			}
			
			LPInputFileFormat inputFileProcessor = new MPSInputFormat(lpProblem);
			
			try {
				inputFileProcessor.writeLPFile(file);
			} catch (IOException e) {
				throw new SolverException(getClass(), e);
			}
		} else {
			throw new SolverException(getClass(), new Exception("Problem is null. Impossible to write MPS file."));
		}
	}
}
