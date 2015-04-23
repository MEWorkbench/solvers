package pt.uminho.ceb.biosystems.mew.solvers.lp;

public class SolverException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Class<?> solver;
	
	public SolverException(Class<?> solver, Exception e){
		super(e);
		this.solver = solver;
	}
	
	public SolverException(Class<?> solver) {
		super();
		this.solver = solver;
	}

	public SolverException(Exception e) {
		super(e);
	}


	

}
