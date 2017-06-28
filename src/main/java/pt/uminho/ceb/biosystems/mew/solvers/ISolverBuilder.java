package pt.uminho.ceb.biosystems.mew.solvers;

import pt.uminho.ceb.biosystems.mew.solvers.lp.ILPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.qp.IQPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPProblem;

public interface ISolverBuilder {

	public String getId();
	
	public boolean isLP();
	
	public boolean isMIP();

	public boolean isQP();
	
	public boolean supportsPersistentModel();
	
	public ILPSolver lpSolver(LPProblem problem);
	
	public ILPSolver lpSolver(LPProblem problem, int totalTime);
	
	public IQPSolver qpSolver(QPProblem problem);
}
