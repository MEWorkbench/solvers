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

import pt.uminho.ceb.biosystems.mew.solvers.lp.CLPLPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.CPLEXSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.GLPKSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.ILPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverDefinitionException;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.CPLEXSolver3;
import pt.uminho.ceb.biosystems.mew.solvers.qp.CLPQPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.IQPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPGenSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPProblem;
/* 
 * LP SolverType was moved to here.
 * Some methods were created to facilitate the call of
 * solvers and to distinguish between LP, MILP, QP and ODE
 * solvers. 
 * 
 * Changed by Joao-
 */ 
@SuppressWarnings("deprecation")
public enum SolverType {

	GLPK {
		@Override
		public boolean isLP() { return true; }
		
		@Override
		public boolean isMIP() { return true; }

		@Override
		public ILPSolver lpSolver(LPProblem problem) {
			return new GLPKSolver(problem, timeOut);
		}
		
		@Override
		public ILPSolver lpSolver(LPProblem problem, int totalTime) {
			return new GLPKSolver(problem, totalTime);
		}
	},
	CLP {
		@Override
		public boolean isLP() { return true; }
		
		@Override
		public boolean isQP() { return true; }
		
		@Override
		public IQPSolver qpSolver(QPProblem problem) {
			return new CLPQPSolver(problem, timeOut);
		}
		
		@Override
		public ILPSolver lpSolver(LPProblem problem) {
			return new CLPLPSolver(problem, timeOut);
		}
		
		@Override
		public ILPSolver lpSolver(LPProblem problem, int totalTime) {
			return new CLPLPSolver(problem, totalTime);
		}
	},
	@Deprecated
	QPGEN {
		@Override
		public boolean isQP() { return true; }
		
		@Override
		public IQPSolver qpSolver(QPProblem problem) {
			return new QPGenSolver(problem);
		}
	},
	
	CPLEX {
		@Override
		public boolean isLP() { return true; }

		@Override
		public boolean isMIP() { return true; }

		@Override
		public boolean isQP() { return true; }
		
		@Override
		public ILPSolver lpSolver(LPProblem problem) throws SolverDefinitionException{
			try {
				return new CPLEXSolver(problem);
			}catch (Error e) {
				throw new SolverDefinitionException(CPLEXSolver.class);
			}  catch (Exception e) {
				throw new SolverDefinitionException(CPLEXSolver.class);
			}
		}
		
		@Override
		public IQPSolver qpSolver(QPProblem problem) throws SolverDefinitionException{
			try {
				return new CPLEXSolver(problem);
			}catch (Error e) {
				throw new SolverDefinitionException(CPLEXSolver.class);
			}  catch (Exception e) {
				throw new SolverDefinitionException(CPLEXSolver.class);
			}
		}
		
	},
	
	CPLEX3 {
		@Override
		public boolean isLP() { return true; }

		@Override
		public boolean isMIP() { return true; }

		@Override
		public boolean isQP() { return true; }
		
		@Override
		public boolean supportsPersistentModel(){ return true;}
		
		@Override
		public ILPSolver lpSolver(LPProblem problem) throws SolverDefinitionException{			
				return new CPLEXSolver3(problem,true);			
		}
	};
	
	public boolean isLP(){ return false; }

	public boolean isMIP(){	return false; }

	public boolean isQP(){ return false; }
	
	public boolean supportsPersistentModel(){ return false; }
	
	public ILPSolver lpSolver(LPProblem problem) throws SolverDefinitionException{
		throw new UnsupportedOperationException();
	};
	
	public ILPSolver lpSolver(LPProblem problem, int totalTime){
		throw new UnsupportedOperationException();
	};
	
	public IQPSolver qpSolver(QPProblem problem) throws SolverDefinitionException {
		throw new UnsupportedOperationException();
	}
	
	protected static long timeOut = 300;
	
	public static long getTimeOut() {
		return SolverType.timeOut;
	}
	
	public static void getTimeOut(long timeOut) {
		SolverType.timeOut = timeOut;
	}

	private static final ArrayList<SolverType> lpSolvers =
		new ArrayList<SolverType>();
	
	static {
		for(SolverType solver : SolverType.values())
			if(solver.isLP()) lpSolvers.add(solver);
	}
	
	public static ArrayList<SolverType> lpSolvers() { return lpSolvers; }
	
	private static final ArrayList<SolverType> milpSolvers =
		new ArrayList<SolverType>();
	
	static {
		for(SolverType solver : SolverType.values())
			if(solver.isMIP()) milpSolvers.add(solver);
	}
	
	public static ArrayList<SolverType> milpSolvers() { return milpSolvers; }
	
	private static final ArrayList<SolverType> qpSolvers =
		new ArrayList<SolverType>();
	
	static {
		for(SolverType solver : SolverType.values())
			if(solver.isQP()) qpSolvers.add(solver);
	}
	
	public static ArrayList<SolverType> qpSolvers() { return qpSolvers; }
}