package pt.uminho.ceb.biosystems.mew.solvers;

import pt.uminho.ceb.biosystems.mew.solvers.SolverType;
import pt.uminho.ceb.biosystems.mew.solvers.lp.CLPLPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.CPLEXSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.GLPKSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.ILPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.CPLEXSolver3;

public class ValidationUtilities {
	
	public static boolean canRunCPLEX3Solver(){
		CPLEXSolver3 cplex = new CPLEXSolver3(new LPProblem());
		return canRunSolver(cplex);
	}
	
	public static boolean canRunCPLEXSolver(){
		CPLEXSolver cplex = new CPLEXSolver(new LPProblem());
		return canRunSolver(cplex);
	}
	
	public static boolean canRunCLPLPSolver(){
		CLPLPSolver cplex = new CLPLPSolver(new LPProblem());
		return canRunSolver(cplex);
	}
	
	public static boolean canRunGLPKSolver(){
		GLPKSolver cplex = new GLPKSolver(new LPProblem());
		return canRunSolver(cplex);
	}
	
	public static boolean canRunSolver(ILPSolver solver){
		try{
			solver.solve();
		}catch(Error | Exception e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static boolean canRunSolver(SolverType solverType) {
		switch (solverType) {
		case CLP:
			return canRunCPLEX3Solver();
		case GLPK:
			return canRunGLPKSolver();
		case CPLEX:
			return canRunCPLEXSolver();
		case CPLEX3:
			return canRunCPLEX3Solver();
		default:
//			throw new Exception("Unknown solver");
			return false;
		}
	}
	
}
