package pt.uminho.ceb.biosystems.mew.solvers.builders;

import pt.uminho.ceb.biosystems.mew.solvers.ISolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.lp.CLPLPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.ILPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.qp.CLPQPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.IQPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPGenSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPProblem;

public class QPGenSolverBuilder implements ISolverBuilder{

	public static String ID = "QPGEN";
	
	@Override
	public boolean isLP() {
		return false;
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
		throw new RuntimeException("QPGEN not supports LP problems!");
	}

	@Override
	public ILPSolver lpSolver(LPProblem problem, int totalTime) {
		throw new RuntimeException("QPGEN not supports LP problems!");
	}

	@Override
	public IQPSolver qpSolver(QPProblem problem) {
		return new QPGenSolver(problem);
	}

	@Override
	public String getId() {
		return ID;
	}

}
