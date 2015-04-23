package pt.uminho.ceb.biosystems.mew.solvers.persistent.listener;

import java.util.EventObject;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPConstraint;

public class LPConstraintAddedEvent extends EventObject{

	private static final long	serialVersionUID	= -4095466611636285630L;
	
	protected LPConstraint _constraint;
	protected Integer			_index;
	
	public LPConstraintAddedEvent(Object source, LPConstraint constraint) {
		this(source,constraint,null);
	}
	
	public LPConstraintAddedEvent(Object source,LPConstraint constraint, Integer index) {
		super(source);
		_constraint = constraint;
		_index = index;
	}

	/**
	 * @return the _constraint
	 */
	public LPConstraint get_constraint() {
		return _constraint;
	}

	
	public Integer get_index() {
		return _index;
	}
	
	
	
	
}
