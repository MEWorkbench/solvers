package pt.uminho.ceb.biosystems.mew.solvers.persistent.listener;


/**
 * <p>
 * The <code>LPProblemListener</code> interface.
 * Defines a set of mandatory functions that a 
 * listener for an <code>LPProblem</code> must implement.
 * <p>
 * Useful for Linear solvers such as CPLEX
 * 
 * @author pmaia
 * @date May 5, 2014
 * @version 0.2
 * @since metabolic3 persistent
 */
public interface LPProblemListener {
	
	void updateLPProblemVariable(LPVariableChangedEvent event);
	
	void updateLPProblemConstraint(LPConstraintChangedEvent event);
	
	void updateObjectiveFunction(ObjectiveFunctionChangedEvent event);
	
	void updateObjectiveSense(ObjectiveSenseChangedEvent event);
	
	void addLPVariable(LPVariableAddedEvent event);
	
	void addLPConstraint(LPConstraintAddedEvent event);
	
	void replaceLPConstraint(LPConstraintReplacedEvent event);
	
	void addLPVariableRange(LPVariableRangeAddedEvent event);
	
	void addLPConstraintRange(LPConstraintRangeAddedEvent event);
	
	void removeLPVariable(LPVariableRemovedEvent event);
	
	void removeLPConstraint(LPConstraintRemovedEvent event);
	
	void removeLPVariableRange(LPVariableRangeRemovedEvent event);
	
	void removeLPConstraintRange(LPConstraintRangeRemovedEvent event);
	

}
