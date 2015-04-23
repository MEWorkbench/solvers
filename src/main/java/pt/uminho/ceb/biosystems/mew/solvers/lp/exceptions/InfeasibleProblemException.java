package pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions;

import pt.uminho.ceb.biosystems.mew.solvers.lp.SolverException;

/**
 * Exception given when the solver cannot return a valid solution
 * @author hgiesteira
 *
 */
public class InfeasibleProblemException extends SolverException {

	private static final long	serialVersionUID	= 1L;

	
	public InfeasibleProblemException(Class<?> solver) {
		super(solver);
	}
	
	public InfeasibleProblemException(Class<?> solver, Exception e){
		super(solver,e);
	}

	public String getSolver() {
		return solver.getSimpleName();
	}

}
