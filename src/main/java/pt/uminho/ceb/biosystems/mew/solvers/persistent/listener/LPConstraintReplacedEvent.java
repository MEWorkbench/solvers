package pt.uminho.ceb.biosystems.mew.solvers.persistent.listener;

import java.util.EventObject;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPConstraint;

public class LPConstraintReplacedEvent extends EventObject{

	private static final long	serialVersionUID	= -4095466611636285630L;
	
	protected LPConstraint _oldConstraint;
	protected LPConstraint _newConstraint;
	
	public LPConstraintReplacedEvent(Object source, LPConstraint oldConstraint,LPConstraint newConstraint) {
		super(source);
		_oldConstraint = oldConstraint;
		_newConstraint = newConstraint;
	}

	public LPConstraint get_oldConstraint() {
		return _oldConstraint;
	}

	public LPConstraint get_newConstraint() {
		return _newConstraint;
	}	
}
