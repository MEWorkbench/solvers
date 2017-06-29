package utils.test;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPConstraintType;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;

public class Problems {
		
	//	Minimize z = -.5 * x1 + .5 * x2 - x3 + 1
    //
    //	subject to
    //	0.0 <= x1 - .5 * x2 <= 0.2
    //  -x2 + x3 <= 0.4
    //	where,
    //	0.0 <= x1 <= 0.5
    //	0.0 <= x2 <= 0.5
    //  0.0 <= x3 <= 0.5
	public static LPProblem getLPProblem(){
		LPProblem p = new LPProblem();
		p.addVariable("v1", 0.0, 0.5);
		p.addVariable("v2", 0.0, 0.5);
		p.addVariable("v3", 0.0, 0.5);
		
		p.addConstraint(new int[]{0,1}, new double[]{1, -0.5}, LPConstraintType.GREATER_THAN, 0.0);
		p.addConstraint(new int[]{0,1}, new double[]{1, -0.5}, LPConstraintType.LESS_THAN, 0.2);
		
		p.addConstraint(new int[]{1,2}, new double[]{-1, 1}, LPConstraintType.LESS_THAN, 0.4);
		
		p.setObjectiveFunction(new int[]{0,1,2}, new double[]{-0.5,0.5,-1}, false);
		
		return p;
	}
	
	
//	min: x + y;
//	c1: x >= 6;
//	c2: y >= 6;
//	c3: x + y <= 11;
	public static LPProblem getInfeasibleLPProblem(){
		
		double infinity = 10000;
		LPProblem p = new LPProblem();
		p.addVariable("x", 6, infinity);
		p.addVariable("y", 6, infinity);
		
		p.addConstraint(new int[]{0,1}, new double[]{1, 1}, LPConstraintType.LESS_THAN, 11);
		
		p.setObjectiveFunction(new int[]{0,1}, new double[]{1,1}, false);
		return p;
	}

}
