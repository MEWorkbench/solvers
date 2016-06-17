package pt.uminho.ceb.biosystems.mew.solvers;

import org.junit.Test;

import junit.framework.TestCase;
import pt.uminho.ceb.biosystems.mew.solvers.lp.CLPLPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.CPLEXSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.GLPKSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.lp.SolverException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverDefinitionException;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.CPLEXSolver3;

public class SolversTests extends TestCase {

	@Test
	public void testCPLEX3SolverDefinition(){
		CPLEXSolver3 cplex = new CPLEXSolver3(new LPProblem());
		try{
			cplex.solve();
		}catch(Error e){
			throw new SolverDefinitionException(CPLEXSolver3.class);
		}catch(Exception e){
			throw new SolverException(CPLEXSolver3.class, e);
		}
	}
	
	@Test
	public void testCPLEXSolverDefinition(){
		CPLEXSolver cplex = new CPLEXSolver(new LPProblem());
		try{
			cplex.solve();
		}catch(Error e){
			throw new SolverDefinitionException(CPLEXSolver.class);
		}catch(Exception e){
			if(SolverDefinitionException.class.isAssignableFrom(e.getClass()))
				throw e;
			throw new SolverException(CPLEXSolver.class, e);
		}
	}
	
	@Test
	public void testCLPLPSolverDefinition(){
		CLPLPSolver clplp = new CLPLPSolver(new LPProblem());
		try{
			clplp.solve();
		}catch(Error e){
			throw new SolverDefinitionException(CLPLPSolver.class);
		}catch(Exception e){
			if(SolverDefinitionException.class.isAssignableFrom(e.getClass()))
				throw e;
			throw new SolverException(CLPLPSolver.class, e);
		}
	}
	
	@Test
	public void testGLPKSolverDefinition(){
		GLPKSolver glpk = new GLPKSolver(new LPProblem());
		try{
			glpk.solve();
		}catch(Error e){
			throw new SolverDefinitionException(GLPKSolver.class);
		}catch(Exception e){
			if(SolverDefinitionException.class.isAssignableFrom(e.getClass()))
				throw e;
			throw new SolverException(GLPKSolver.class, e);
		}
	}

}
