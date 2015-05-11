/*
 * Copyright 2010
 * IBB-CEB - Institute for Biotechnology and Bioengineering - Centre of
 * Biological Engineering
 * CCTC - Computer Science and Technology Center
 * University of Minho
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Public License for more details.
 * You should have received a copy of the GNU Public License
 * along with this code. If not, see http://www.gnu.org/licenses/
 * Created inside the SysBioPseg Research Group (http://sysbio.di.uminho.pt)
 */
package pt.uminho.ceb.biosystems.mew.solvers.lp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import pt.uminho.ceb.biosystems.mew.solvers.SolverType;
import pt.uminho.ceb.biosystems.mew.solvers.fileformats.LPFormatTypes;
import pt.uminho.ceb.biosystems.mew.solvers.fileformats.LPInputFileFormat;
import pt.uminho.ceb.biosystems.mew.solvers.fileformats.MPSInputFormat;
import pt.uminho.ceb.biosystems.mew.solvers.fileformats.OutputFileUtils;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.InfeasibleProblemException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverDefinitionException;
import pt.uminho.ceb.biosystems.mew.solvers.parser.GeneralOutputSolverFile;
import pt.uminho.ceb.biosystems.mew.solvers.parser.GeneralOutputUtils;
import pt.uminho.ceb.biosystems.mew.utilities.io.FileUtils;
import pt.uminho.ceb.biosystems.mew.utilities.java.TimeUtils;

public class GLPKSolver implements ILPSolver {
	
	public final static String	MPS_INPUT_FILE		= "inputFile";
	public final static String	MPS_OUTPUT_FILE		= "outputFile";
	public final static String	MPS_BOUNDS_FILE		= "bounds";
	public final static String	NO_PRESOLVER		= "--nopresol ";
	public final static String	NO_SCALING			= "--noscale ";
	
	protected boolean			debug				= false;
	
	protected LPProblem			problem;
	
	protected LPFormatTypes		formatType			= LPFormatTypes.FreeMPS;
	
	protected String			solverOutput;
	protected long				totalTime;
	
	boolean						computeShadowPrices	= true;
	
	boolean						usePreSolver		= false;
	boolean						useAutoScaling		= false;
	
	public GLPKSolver() {
		totalTime = 100000;
	}
	
	public GLPKSolver(LPProblem problem) {
		this.problem = problem;
		this.totalTime = 100000;
	}
	
	public GLPKSolver(LPProblem problem, long totalTime) {
		this.problem = problem;
		this.totalTime = totalTime;
	}
	
	public List<LPFormatTypes> getListSupportedFormats() {
		ArrayList<LPFormatTypes> res = new ArrayList<LPFormatTypes>();
		res.add(LPFormatTypes.FixedMPS);
		res.add(LPFormatTypes.FreeMPS);
		return res;
	}
	
	@Override
	public LPSolution solve() throws SolverDefinitionException, InfeasibleProblemException {
		if (debug) System.out.println("\t\t\tGLPKSolver.solve BEGIN");
		
		String mpsInputFile = MPS_INPUT_FILE + "." + Math.random() + "." + GregorianCalendar.getInstance().getTimeInMillis();
		String mpsOutputFile = MPS_OUTPUT_FILE + "." + Math.random() + "." + GregorianCalendar.getInstance().getTimeInMillis();
		
		LPInputFileFormat inputFileProcessor = null;
		
		LPSolution lpSolution = null;
		
		inputFileProcessor = new MPSInputFormat(problem);
		
		if (debug) System.out.println("\t\t\t\tGLPKSolver.solve.writeLPFile BEGIN");
		
		try {
			inputFileProcessor.writeLPFile(mpsInputFile);
		} catch (Exception e) {
			
		}
		
		try {
			solverOutput = runGlpk(mpsInputFile, mpsOutputFile);
		} catch (SolverDefinitionException e) {
			GeneralOutputUtils.deleteFile(mpsInputFile);
			throw new SolverDefinitionException(GLPKSolver.class);
		}
		
		if (debug) {
			System.out.println("\t\t\t\tGLPKSolver.solve.writeLPFile END");
			System.out.println("\t\t\t\tGLPKSolver.solve.MPSOutputFileParser BEGIN");
		}
		
		GeneralOutputSolverFile mps_out = OutputFileUtils.createParserGLPK(problem);
		
		try {
			mps_out.parserFile(mpsOutputFile);
		} catch (Exception e) {
			GeneralOutputUtils.deleteFile(mpsInputFile);
			throw new InfeasibleProblemException(GLPKSolver.class);
		}
		
		LPMapVariableValues emptyVars = new LPMapVariableValues();
		
		int valueIdx = (MILPProblem.class.isAssignableFrom(problem.getClass())) ? 2 : 3;
		
		if (computeShadowPrices) {
			LPMapVariableValues emptyReduced = new LPMapVariableValues();
			LPMapVariableValues emptyShadow = new LPMapVariableValues();
			
			GeneralOutputUtils.createLPListVariablesAndReducedCostsAndShadowPrices(valueIdx, 6, 5, mps_out, emptyVars, emptyReduced, emptyShadow);
			lpSolution = new LPSolution(problem, emptyVars, mps_out.getSolutionType(), mps_out.getObjectiveValue());
			lpSolution.addPerVariableMetric("ReducedCosts", emptyReduced);
			lpSolution.addPerConstraintMetric("ShadowPrices", emptyShadow);
			
		} else {
			GeneralOutputUtils.createLPListVariables(valueIdx, mps_out, emptyVars);
			lpSolution = new LPSolution(problem, emptyVars, mps_out.getSolutionType(), mps_out.getObjectiveValue());
		}
		
		GeneralOutputUtils.deleteFile(mpsInputFile);
		GeneralOutputUtils.deleteFile(mpsOutputFile);
		
		if (debug) {
			System.out.println("\t\t\t\tGLPKSolver.solve.MPSOutputFileParser end");
			System.out.println("\t\t\tGLPKSolver.solve END");
		}
		
		lpSolution.setSolverOutput(solverOutput);
		lpSolution.setSolverType(SolverType.GLPK);
		return lpSolution;
	}
	
	protected String runGlpk(String mpsInputFile, String mpsOutputFile) throws SolverDefinitionException {
		String solverCommand = "";
		if (this.formatType.equals(LPFormatTypes.FreeMPS)) {
			solverCommand = "glpsol " + mpsInputFile + " -o " + mpsOutputFile + " --tmlim " + totalTime;
		} else if (this.formatType.equals(LPFormatTypes.FixedMPS)) {
			solverCommand = "glpsol --mps " + mpsInputFile + " -o " + mpsOutputFile + " --tmlim " + totalTime;
		}
		
		String solverOutput = "";
		try {
			Process child = Runtime.getRuntime().exec(solverCommand);
			InputStream in = child.getInputStream();
			BufferedReader dataInputStream = new BufferedReader(new InputStreamReader(in));
			
			String childOutput;
			
			while (((childOutput = dataInputStream.readLine()) != null))
				solverOutput += childOutput + "\n";
			
			child.destroy();
			
		} catch (IOException e) {
			throw new SolverDefinitionException(GLPKSolver.class);
		}
		
		return solverOutput;
	}
	
	@Override
	public String getSolverOutput() {
		return solverOutput;
	}
	
	@Override
	public void resetSolver() {
		solverOutput = "";
	}
	
	public LPProblem getProblem() {
		return problem;
	}
	
	public void setProblem(LPProblem problem) {
		this.problem = problem;
	}
	
	/**
	 * @return the computeShadowPrices
	 */
	public boolean getComputeShadowPrices() {
		return computeShadowPrices;
	}
	
	/**
	 * @param computeShadowPrices the computeShadowPrices to set
	 */
	public void setComputeShadowPrices(boolean computeShadowPrices) {
		this.computeShadowPrices = computeShadowPrices;
	}
	
	public LPFormatTypes getFormatType() {
		return formatType;
	}
	
	public void setFormatType(LPFormatTypes formatType) {
		this.formatType = formatType;
	}
	
	public void setDebug(boolean b) {
		debug = b;
	}
	
	public boolean isUsePreSolver() {
		return usePreSolver;
	}
	
	public void setUsePreSolver(boolean usePreSolver) {
		this.usePreSolver = usePreSolver;
	}
	
	public boolean isUseAutoScaling() {
		return useAutoScaling;
	}
	
	public void setUseAutoScaling(boolean useAutoScaling) {
		this.useAutoScaling = useAutoScaling;
	}
	
	@Override
	public void saveModelToMPS(String file, boolean includeTime) {
		if (problem != null) {
			if (includeTime) {
				String extension = FileUtils.getFileExtension(file);
				String time = TimeUtils.formatMillis(System.currentTimeMillis());
				file = file.replaceFirst(extension, "") + "_" + time + "." + extension;
			}
			
			LPInputFileFormat inputFileProcessor = new MPSInputFormat(problem);
			
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
