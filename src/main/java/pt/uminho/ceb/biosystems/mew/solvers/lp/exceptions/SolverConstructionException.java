package pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions;

import pt.uminho.ceb.biosystems.mew.solvers.lp.SolverException;

/**
 * Exception given when the solver throws an intern exception
 * @author hgiesteira
 *
 */
public class SolverConstructionException extends SolverException{

	public SolverConstructionException(Class<?> solver) {
		super(solver);
	}

	public SolverConstructionException(Class<?> solver, Exception e) {
		super(solver, e);
	}

	public String getSolver() {
		return solver.getSimpleName();
	}

}
