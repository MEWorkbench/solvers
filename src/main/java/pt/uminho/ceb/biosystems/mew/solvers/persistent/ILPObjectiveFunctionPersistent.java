package pt.uminho.ceb.biosystems.mew.solvers.persistent;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPObjectiveFunction;

public interface ILPObjectiveFunctionPersistent {
	
	void changeLPObjectiveFunction(LPObjectiveFunction lpObjectiveFunction);
}
