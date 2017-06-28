package pt.uminho.ceb.biosystems.mew.solvers.builders;

import pt.uminho.ceb.biosystems.mew.solvers.ISolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.lp.CLPLPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.GLPKSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.ILPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.qp.CLPQPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.IQPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPProblem;

public class GLPKBinSolverBuilder implements ISolverBuilder{

	public static String ID = "GLPK";
	
	@Override
	public boolean isLP() {
		return true;
	}

	@Override
	public boolean isMIP() {
		return true;
	}

	@Override
	public boolean isQP() {
		return false;
	}

	@Override
	public boolean supportsPersistentModel() {
		return false;
	}

	@Override
	public ILPSolver lpSolver(LPProblem problem) {
		return new GLPKSolver(problem, 300);
	}

	@Override
	public ILPSolver lpSolver(LPProblem problem, int totalTime) {
		return new GLPKSolver(problem, totalTime);
	}

	@Override
	public IQPSolver qpSolver(QPProblem problem) {
		throw new RuntimeException("GLPK not supports QP problems!");
	}

	@Override
	public String getId() {
		return ID;
	}

}
