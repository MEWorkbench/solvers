package pt.uminho.ceb.biosystems.mew.solvers.persistent.listener;

import java.util.EventObject;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPVariable;

public class LPVariableAddedEvent extends EventObject {
	
	private static final long	serialVersionUID	= 6488195872202890798L;
	
	protected LPVariable		_variable;
	protected Integer			_index;
	
	public LPVariableAddedEvent(Object source, LPVariable variable) {
		this(source,variable,null);
	}
	
	public LPVariableAddedEvent(Object source,LPVariable variable, Integer index) {
		super(source);
		_variable = variable;
		_index = index;
	}
	
	/**
	 * @return the _variable
	 */
	public LPVariable get_variable() {
		return _variable;
	}

	public Integer get_index() {
		return _index;
	}
	
}
