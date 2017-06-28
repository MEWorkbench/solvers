package pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions;

import pt.uminho.ceb.biosystems.mew.solvers.lp.SolverException;

public class UnregisteredSolverIDException extends SolverException	{

	
	private static final long serialVersionUID = 1L;
	public UnregisteredSolverIDException(String solver) {
		super(solver);
	}
}
