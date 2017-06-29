package pt.uminho.ceb.biosystems.mew.solvers;

import org.junit.Assert;
import org.junit.Test;

import junit.framework.TestCase;
import pt.uminho.ceb.biosystems.mew.solvers.builders.CLPSolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.builders.GLPKBinSolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.lp.CLPLPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.GLPKSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.ILPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolution;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.InfeasibleProblemException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.LinearProgrammingTermAlreadyPresentException;

public class SolversTests extends TestCase {

	
	@Test
	public void testCLPLPSolverDefinition(){
		assertLPProblem(CLPSolverBuilder.ID);
	}
	
	@Test
	public void testGLPKSolverDefinition(){
		assertLPProblem(GLPKBinSolverBuilder.ID);
	}
	
	
	@Test
	public void testCLPLPInfeasible(){
		assertLPInfeasible(CLPSolverBuilder.ID);
	}
	
	@Test
	public void testGLPKLPInfeasible(){
		assertLPInfeasible(GLPKBinSolverBuilder.ID);
	}

	public static void assertLPProblem(String id){
		ILPSolver a = SolverFactory.getInstance().lpSolver(id, Problems.getLPProblem());
		LPSolution res = a.solve();
		Assert.assertNotNull(res);
	}
	
	public static void assertLPInfeasible(String id){
		ILPSolver a = SolverFactory.getInstance().lpSolver(id, Problems.getInfeasibleLPProblem());
		LPSolution res = null;
		try {
			res = a.solve();
		} catch (InfeasibleProblemException e) {
			e.printStackTrace();
			return;
		}
		
		Assert.assertNotNull(res);
		Assert.assertTrue(res.getSolutionType().equals(LPSolutionType.INFEASIBLE));
	}
}
