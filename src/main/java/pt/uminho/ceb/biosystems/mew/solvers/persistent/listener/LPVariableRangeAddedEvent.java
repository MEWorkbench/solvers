package pt.uminho.ceb.biosystems.mew.solvers.persistent.listener;

import java.util.EventObject;
import java.util.List;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPVariable;

public class LPVariableRangeAddedEvent extends EventObject {

	private static final long	serialVersionUID	= 6488195872202890798L;
	
	protected List<LPVariable> _variables;
	
	public LPVariableRangeAddedEvent(Object source,List<LPVariable> variables) {
		super(source);
		_variables = variables;
	}

	/**
	 * @return the _variable
	 */
	public List<LPVariable> get_variables() {
		return _variables;
	}


}
