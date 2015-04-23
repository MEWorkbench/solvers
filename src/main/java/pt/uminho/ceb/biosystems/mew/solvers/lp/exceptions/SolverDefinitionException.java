package pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions;

import pt.uminho.ceb.biosystems.mew.solvers.lp.SolverException;

/**
 * Exception given when the path of the solver is set incorrectly
 * 
 */
public class SolverDefinitionException extends SolverException {
	
	private static final long	serialVersionUID	= 1L;
	
	
	public SolverDefinitionException(Class<?> solver) {
		super(solver);
	}
	
	public SolverDefinitionException(Class<?> solver, Exception e) {
		super(solver,e);
	}
	
	public String getMessage() {
		return solver.getSimpleName() + " - Wrong Path Definition";
	}
	
	public String getSolver() {
		// TODO Auto-generated method stub
		return solver.getSimpleName();
	}
	
}
