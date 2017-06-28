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
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import pt.uminho.ceb.biosystems.mew.solvers.builders.QPGenSolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.fileformats.LPFormatTypes;
import pt.uminho.ceb.biosystems.mew.solvers.fileformats.LPInputFileFormat;
import pt.uminho.ceb.biosystems.mew.solvers.fileformats.QuadraticMPSInputFileProcessor;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPMapVariableValues;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolution;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.mew.solvers.lp.SolverException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.InfeasibleProblemException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverConstructionException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverDefinitionException;
import pt.uminho.ceb.biosystems.mew.solvers.parser.GeneralOutputUtils;
import pt.uminho.ceb.biosystems.mew.utilities.io.FileUtils;
import pt.uminho.ceb.biosystems.mew.utilities.java.TimeUtils;
@Deprecated
public class QPGenSolver implements IQPSolver
{
	public final static String MPS_FILE = "moma-file";
	
//	protected LPFormatTypes formatType = LPFormatTypes.FixedMPS;
//	assuming this format for now !!	
	
	protected String solverOutput;
	protected long totalTime = 10000;
	
	protected QPProblem problem;
	//protected FluxValueList wildtypeValues = null;
	

	public QPGenSolver()
	{
	}
	
	public QPGenSolver(QPProblem problem/*, FluxValueList wildtypeValues*/) {
		super();
		this.problem = problem;
		//this.wildtypeValues = wildtypeValues;
	}

	public List<LPFormatTypes> getListSupportedFormats()
	{
		ArrayList<LPFormatTypes> res = new ArrayList<LPFormatTypes>();
		res.add(LPFormatTypes.FixedMPS);
		return res;
	}
	
	@Override
	public LPSolution solve() throws SolverConstructionException, SolverDefinitionException, InfeasibleProblemException{
		String mpsFile = MPS_FILE +"."+Math.random()+"."+GregorianCalendar.getInstance().getTimeInMillis();
		
		// if more formats are suppoorted generalize this ...
		QuadraticMPSInputFileProcessor mps = new QuadraticMPSInputFileProcessor(problem);
		// and this ...
		try {
			mps.generateMPSFile(mpsFile);
		} catch (IOException e) {
			throw new SolverConstructionException(QPGenSolverBuilder.ID);
		}
		
		try {
			solverOutput = runQpGen(mpsFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new SolverDefinitionException(QPGenSolverBuilder.ID);
		}
		
		LPMapVariableValues varValues=null;
		try {
			varValues = loadValues(mpsFile+".out");
		} catch (IOException e) {
			throw new InfeasibleProblemException(QPGenSolverBuilder.ID);
		}
		
		
		//MPSOutputFileParser mps_out = new MPSOutputFileParser(problem);
		//LPSolution lpSolution = mps_out.loadResultsFromFile(mpsFile+".out");	
		//return lpSolution;
		
		
		GeneralOutputUtils.deleteFile(mpsFile);
		GeneralOutputUtils.deleteFile(mpsFile+".out");
		
//		FIXME: read the objective value
		LPSolution solution =  new LPSolution(problem, varValues, LPSolutionType.OPTIMAL, 0.0);
		solution.setSolverType(QPGenSolverBuilder.ID);
		solution.setSolverOutput(solverOutput);
		return solution;
	}

	
	protected String runQpGen (String mpsFile) throws IOException 
	{
		String solverCommand = "./qpgen-sparse-gondzio.exe " + mpsFile;
	
		System.out.println("running command ... " + solverCommand);
		
		Process child = Runtime.getRuntime().exec(solverCommand);
      	InputStream in = child.getInputStream();
      	DataInputStream dataInputStream = new DataInputStream(in);
      	long currentTime = 0;
      	long time = GregorianCalendar.getInstance().getTimeInMillis();
		String childOutput = null;
		String solverOutput = "";
		
		while (((childOutput = dataInputStream.readLine()) != null) && (currentTime < totalTime)){
			solverOutput += childOutput+"\n";
			long newTime = time - GregorianCalendar.getInstance().getTimeInMillis();
			currentTime += newTime;
		}
			
		child.destroy();
		return solverOutput;
	}
	
	
	LPMapVariableValues loadValues (String outfile) throws IOException
	{
		FileReader f = new FileReader(outfile);
		BufferedReader r = new BufferedReader(f);
		StringTokenizer st;
        
		r.readLine();
		r.readLine();
		r.readLine();
		r.readLine();
		r.readLine();
		r.readLine();
		r.readLine();
		r.readLine();
		
		int i=0;
		LPMapVariableValues varValuesList = new LPMapVariableValues();
		
		while(r.ready() && i < this.problem.getNumberVariables())
		{
			String str = r.readLine();
			st = new StringTokenizer(str," ");
			if (st.hasMoreTokens()) st.nextToken();
			if (st.hasMoreTokens())	st.nextToken();
			if (st.hasMoreTokens())	{
				String stt=st.nextToken();
				varValuesList.addVariableValue(i,Double.parseDouble(stt));
			}
			i++;
		}
		
		return varValuesList;
	}
	
	
	@Override
	public String getSolverOutput() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setQPProblem(QPProblem problem) {
		this.problem = problem;
	}
	
	@Override
	public void saveModelToMPS(String file, boolean includeTime) {
		if (problem != null) {
			if (includeTime) {
				String extension = FileUtils.getFileExtension(file);
				String time = TimeUtils.formatMillis(System.currentTimeMillis());
				file = file.replaceFirst(extension, "") + "_" + time + "." + extension;
			}
			
			LPInputFileFormat inputFileProcessor = new QuadraticMPSInputFileProcessor(problem);
			
			try {
				inputFileProcessor.writeLPFile(file);
			} catch (IOException e) {
				throw new SolverException(QPGenSolverBuilder.ID, e);
			}
		} else {
			throw new SolverException(QPGenSolverBuilder.ID, new Exception("Problem is null. Impossible to write MPS file."));
		}
	}
}
