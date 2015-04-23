package pt.uminho.ceb.biosystems.mew.solvers.persistent.listener;

import java.util.EventObject;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPVariable;

public class LPVariableChangedEvent extends EventObject {

	private static final long	serialVersionUID	= 6488195872202890798L;
	
	protected LPVariable _variable;
	
	public LPVariableChangedEvent(Object source,LPVariable variable) {
		super(source);
		_variable = variable;
	}

	/**
	 * @return the _variable
	 */
	public LPVariable get_variable() {
		return _variable;
	}


}
