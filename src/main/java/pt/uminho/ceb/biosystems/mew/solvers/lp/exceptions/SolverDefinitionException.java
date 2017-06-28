package pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions;

import pt.uminho.ceb.biosystems.mew.solvers.lp.SolverException;

/**
 * Exception given when the path of the solver is set incorrectly
 * 
 */
public class SolverDefinitionException extends SolverException {
	
	private static final long	serialVersionUID	= 1L;
	
	
	public SolverDefinitionException(String solver) {
		super(solver);
	}
	
	public SolverDefinitionException(String solver, Exception e) {
		super(solver,e);
	}
	
	public String getMessage() {
		return solver + " - Wrong Path Definition";
	}
	
	public String getSolver() {
		return solver;
	}
	
}
