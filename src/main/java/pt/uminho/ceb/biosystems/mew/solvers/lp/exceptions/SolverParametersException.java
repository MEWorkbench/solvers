package pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions;

import pt.uminho.ceb.biosystems.mew.solvers.lp.SolverException;

/**
 * Exception given when the solver parameters are incorrectly defined 
 * @author hgiesteira
 *
 */
public class SolverParametersException extends SolverException {

	private static final long	serialVersionUID	= 1L;
	
	public String param;
	public String value;
	public String kclass;
	
	public SolverParametersException(String solver, String param, String value, String kclass) {
		super(solver);
		this.param = param;
		this.value = value;
		this.kclass = kclass;
	}
	
	public SolverParametersException(String solver, Exception e, String param, String value, String kclass) {
		super(solver,e);
		this.param = param;
		this.value = value;
		this.kclass = kclass;
	}

	public String getSolver() {
		return solver;
	}
	
	@Override
	public String getMessage() {
		return "[" + getSolver() + "] Unkown solver parameter " + param + " - " + kclass;
	}
}
