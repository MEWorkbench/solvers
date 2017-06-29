package utils.test;

import org.junit.Assert;

import pt.uminho.ceb.biosystems.mew.solvers.SolverFactory;
import pt.uminho.ceb.biosystems.mew.solvers.lp.ILPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolution;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.InfeasibleProblemException;

public class AssertProblemUtils {
	
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
			Assert.assertTrue("Runned throw an exeption", true);
		}
		
		
		Assert.assertNotNull(res);
		Assert.assertTrue("Runned and returned a Infisible solution", res.getSolutionType().equals(LPSolutionType.INFEASIBLE));
	}

}
