package pt.uminho.ceb.biosystems.mew.solvers.persistent.listener;

import java.util.EventObject;
import java.util.List;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPConstraint;

public class LPConstraintRangeRemovedEvent extends EventObject {

	private static final long	serialVersionUID	= 6488195872202890798L;
	
	protected List<LPConstraint> _constraints;
	
	public LPConstraintRangeRemovedEvent(Object source,List<LPConstraint> constraints) {
		super(source);
		_constraints = constraints;
	}

	/**
	 * @return the _variable
	 */
	public List<LPConstraint> get_constraints() {
		return _constraints;
	}


}
