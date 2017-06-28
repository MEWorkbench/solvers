package pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions;

import pt.uminho.ceb.biosystems.mew.solvers.lp.SolverException;

/**
 * Exception given when the solver throws an intern exception
 * @author hgiesteira
 *
 */
public class SolverConstructionException extends SolverException{

	public SolverConstructionException(String solver) {
		super(solver);
	}

	public SolverConstructionException(String solver, Exception e) {
		super(solver, e);
	}

	public String getSolver() {
		return solver;
	}

}
