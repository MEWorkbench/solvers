package pt.uminho.ceb.biosystems.mew.solvers.persistent;

import pt.uminho.ceb.biosystems.mew.solvers.qp.QPObjectiveFunction;

public interface IQPObjectiveFunctionPersistent {

	void changeQPObjectiveFunction(QPObjectiveFunction lpObjectiveFunction);
	
}
