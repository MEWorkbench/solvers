package pt.uminho.ceb.biosystems.mew.solvers.lp;

public class SolverException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String solver;
	
	public SolverException(String solver, Exception e){
		super(e);
		this.solver = solver;
	}
	
	public SolverException(String solver) {
		super();
		this.solver = solver;
	}

	public SolverException(Exception e) {
		super(e);
	}


	

}
