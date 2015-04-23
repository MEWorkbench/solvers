package pt.uminho.ceb.biosystems.mew.solvers.persistent.listener;

import java.util.EventObject;

public class ObjectiveSenseChangedEvent extends EventObject{

	private static final long	serialVersionUID	= -3554165997031264932L;
	
	protected boolean _maximization = false;
	
	public ObjectiveSenseChangedEvent(Object source,boolean maximization) {
		super(source);
		_maximization = maximization;
	}
	
	public boolean isMaximization(){
		return _maximization;
	}

	
}
