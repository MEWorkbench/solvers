package pt.uminho.ceb.biosystems.mew.solvers.persistent.listener;

import java.util.EventObject;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPConstraint;

public class LPConstraintRemovedEvent extends EventObject{

	private static final long	serialVersionUID	= -4095466611636285630L;
	
	protected LPConstraint _constraint;
	
	public LPConstraintRemovedEvent(Object source, LPConstraint constraint) {
		super(source);
		_constraint = constraint;	
	}

	/**
	 * @return the _constraint
	 */
	public LPConstraint get_constraint() {
		return _constraint;
	}
	
	
	
	
}
