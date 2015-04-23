package pt.uminho.ceb.biosystems.mew.solvers.persistent.listener;

import java.util.EventObject;

public class ObjectiveFunctionChangedEvent extends EventObject{

	private static final long	serialVersionUID	= -3554165997031264932L;
	
	public ObjectiveFunctionChangedEvent(Object source) {
		super(source);
	}

	
}
