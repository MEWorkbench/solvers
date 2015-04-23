package pt.uminho.ceb.biosystems.mew.solvers.persistent;

import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPProblemListener;

public interface IPersistentProblem {
	
	public void addLPProblemListener(LPProblemListener listener);
	
	public void removeLPProblemListener(LPProblemListener listener);
	
	public void changeVariableBounds(int index, double lower, double upper);
	
	public void changeConstraintBound(int index, double value);
	
}
