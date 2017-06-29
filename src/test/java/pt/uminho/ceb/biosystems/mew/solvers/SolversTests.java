package pt.uminho.ceb.biosystems.mew.solvers;

import org.junit.Test;

import junit.framework.TestCase;
import pt.uminho.ceb.biosystems.mew.solvers.builders.CLPSolverBuilder;
import pt.uminho.ceb.biosystems.mew.solvers.builders.GLPKBinSolverBuilder;
import utils.test.AssertProblemUtils;

public class SolversTests extends TestCase {

	
	@Test
	public void testCLPLPSolverDefinition(){
		AssertProblemUtils.assertLPProblem(CLPSolverBuilder.ID);
	}
	
	@Test
	public void testGLPKSolverDefinition(){
		AssertProblemUtils.assertLPProblem(GLPKBinSolverBuilder.ID);
	}
	
	
	@Test
	public void testCLPLPInfeasible(){
		AssertProblemUtils.assertLPInfeasible(CLPSolverBuilder.ID);
	}
	
	@Test
	public void testGLPKLPInfeasible(){
		AssertProblemUtils.assertLPInfeasible(GLPKBinSolverBuilder.ID);
	}
}
