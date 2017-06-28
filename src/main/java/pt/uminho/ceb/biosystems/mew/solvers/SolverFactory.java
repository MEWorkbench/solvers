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
package pt.uminho.ceb.biosystems.mew.solvers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.uminho.ceb.biosystems.mew.solvers.builders.CLPSolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.builders.GLPKBinSolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.lp.ILPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.UnregisteredSolverIDException;
import pt.uminho.ceb.biosystems.mew.solvers.qp.IQPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPProblem;


public class SolverFactory {

	private static SolverFactory instance;

	static public SolverFactory getInstance(){
		if(instance == null){
			instance = new SolverFactory();
			instance.registSolver(new GLPKBinSolverBuilder());
			instance.registSolver(new CLPSolverBuilder());
		}
		
		return instance;
	}

	private Map<String, ISolverBuilder> solvers;
	private List<String> lpSolvers;
	private List<String> milpSolvers;
	private List<String> qpSolvers;


	private SolverFactory(){
		solvers = new HashMap<>();
		lpSolvers = new ArrayList<>();
		milpSolvers = new ArrayList<>();
		qpSolvers = new ArrayList<>();
	}
	
	public void registSolver(ISolverBuilder solver){
		
		String id = solver.getId();
		solvers.put(id, solver);
		
		if(solver.isLP())
			lpSolvers.add(id);
		if(solver.isQP())
			qpSolvers.add(id);
		if(solver.isMIP())
			milpSolvers.add(id);
	}
	
	public List<String> lpSolvers() { return lpSolvers; }

	public List<String> milpSolvers() { return milpSolvers; }

	public List<String> qpSolvers() { return qpSolvers; }

	
	private ISolverBuilder get(String id) {
		ISolverBuilder builder = solvers.get(id);
		if(builder == null) throw new UnregisteredSolverIDException(id);
		
		return builder;

	}

	public boolean supportsPersistentModel(String id){
		return get(id).supportsPersistentModel();
	}
	
	public ILPSolver lpSolver(String id, LPProblem problem){
		return get(id).lpSolver(problem);
	}
	
	public ILPSolver lpSolver(String id, LPProblem problem, int totalTime){
		return get(id).lpSolver(problem, totalTime);
	}
	
	public IQPSolver qpSolver(String id, QPProblem problem){
		return get(id).qpSolver(problem);
	}
}