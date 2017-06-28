package pt.uminho.ceb.biosystems.mew.solvers.builders;

import pt.uminho.ceb.biosystems.mew.solvers.ISolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.lp.CLPLPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.ILPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.qp.CLPQPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.IQPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPProblem;

public class CLPSolverBuilder implements ISolverBuilder{

	public static String ID = "CLP";
	
	@Override
	public boolean isLP() {
		return true;
	}

	@Override
	public boolean isMIP() {
		return false;
	}

	@Override
	public boolean isQP() {
		return true;
	}

	@Override
	public boolean supportsPersistentModel() {
		return false;
	}

	@Override
	public ILPSolver lpSolver(LPProblem problem) {
		return new CLPLPSolver(problem);
	}

	@Override
	public ILPSolver lpSolver(LPProblem problem, int totalTime) {
		return new CLPLPSolver(problem, totalTime);
	}

	@Override
	public IQPSolver qpSolver(QPProblem problem) {
		return new CLPQPSolver(problem);
	}

	@Override
	public String getId() {
		return ID;
	}

}
