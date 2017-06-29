package pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions;

import pt.uminho.ceb.biosystems.mew.solvers.lp.SolverException;

public class ParsingMPSFileException extends SolverException{

	private static final long serialVersionUID = 1L;
	
	public ParsingMPSFileException(String solver, Exception e) {
		super(solver, e);
	}


}
