/*
 * Copyright 2010
 * IBB-CEB - Institute for Biotechnology and Bioengineering - Centre of
 * Biological Engineering
 * CCTC - Computer Science and Technology Center
 * University of Minho
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Public License for more details.
 * You should have received a copy of the GNU Public License
 * along with this code. If not, see http://www.gnu.org/licenses/
 * Created inside the SysBioPseg Research Group (http://sysbio.di.uminho.pt)
 */
package pt.uminho.ceb.biosystems.mew.solvers.lp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.LinearProgrammingTermAlreadyPresentException;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPConstraintAddedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPConstraintChangedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPConstraintRangeRemovedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPConstraintRemovedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPConstraintReplacedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPProblemListener;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPVariableAddedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPVariableChangedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPVariableRangeRemovedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPVariableRemovedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.ObjectiveFunctionChangedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.ObjectiveSenseChangedEvent;

/**
 * Class to handle a generic purpose linear programming problem.
 * 
 * @author mrocha, pmaia (updates)
 * @date May 5, 2014 (updated)
 * @version 1.2
 * @since metabolic3 persistent
 */
public class LPProblem implements Serializable {
	
	private static final long								serialVersionUID	= 1L;
	
	/** Debug flag */
	protected static final boolean							_debug				= false;
	
	/** Listeners of this <code>LPProblem</code> instance */
	protected final CopyOnWriteArrayList<LPProblemListener>	_listeners;
	
	/** The list of <code>LPVarible</code> in the problem */
	public List<LPVariable>									_variables;
	
	/** The list of <code>LPConstraint</code> in the problem */
	public List<LPConstraint>								_constraints;
	
	/** The <code>LPObjectiveFunction</code> defined for this problem */
	public LPObjectiveFunction								_objectiveFunction	= null;
	
	/**
	 * Default empty constructor
	 */
	public LPProblem() {
		_variables = new ArrayList<LPVariable>();
		_constraints = new ArrayList<LPConstraint>();
		_listeners = new CopyOnWriteArrayList<LPProblemListener>();
	}
	
	public synchronized void addVariable(LPVariable variable) {
		_variables.add(variable);
		fireVariableAddedEvent(variable);
	}
	
	public void addVariable(String variableName, double lowerBound, double upperBound) {
		LPVariable lpVar = new LPVariable(variableName, lowerBound, upperBound);
		addVariable(lpVar);
	}
	
	public void addVariableAt(int index, LPVariable variable) {
		_variables.add(index, variable);
		fireVariableAddedAtEvent(variable, index);
	}
	
	public synchronized void removeVariable(LPVariable variable) {
		_variables.remove(variable);
		fireVariableRemovedEvent(variable);
	}
	
	public synchronized void removeVariableRange(List<LPVariable> variables) {
		_variables.removeAll(variables);
		fireVariableRangeRemoved(variables);
	}
	
	public synchronized void addConstraint(LPConstraint constraint) {
		_constraints.add(constraint);
		fireConstraintAddedEvent(constraint);
	}
	
	
	public void addConstraint(LPProblemRow row, LPConstraintType type, double rigthValue) {
		LPConstraint constraint = new LPConstraint(type, row, rigthValue);
		addConstraint(constraint);
	}
	
	public void addConstraint(int[] indexes, double[] coefficients, LPConstraintType type, double rightValue) throws LinearProgrammingTermAlreadyPresentException {
		
		LPProblemRow row = new LPProblemRow();
		for (int i = 0; i < indexes.length; i++)
			row.addTerm(indexes[i], coefficients[i]);
		
		addConstraint(row, type, rightValue);
	}
	
	
	public synchronized void removeConstraint(LPConstraint constraint) {
		_constraints.remove(constraint);
		fireConstraintRemovedEvent(constraint);
	}
	
	public synchronized void removeConstraintRange(List<LPConstraint> constraints) {
		_constraints.removeAll(constraints);
		fireConstraintRangeRemoved(constraints);
	}
	
	public void replaceConstraint(LPConstraint oldConstraint, LPConstraint newConstraint){
		int index = _constraints.indexOf(oldConstraint);
		_constraints.set(index, newConstraint);
		fireConstraintReplacedEvent(oldConstraint,newConstraint);
	}
	
	public LPObjectiveFunction getObjectiveFunction() {
		return _objectiveFunction;
	}
	
	public void setObjectiveFunction(int[] indexes, double[] coeficients, boolean isMaximization) throws LinearProgrammingTermAlreadyPresentException {
		
		LPProblemRow ofrow = new LPProblemRow();
		for (int i = 0; i < indexes.length; i++)
			ofrow.addTerm(indexes[i], coeficients[i]);
		
		LPObjectiveFunction oF = new LPObjectiveFunction(ofrow, isMaximization);
		setObjectiveFunction(oF);
	}
	
	public void setObjectiveFunction(LPProblemRow row, boolean isMaximization) {
		LPObjectiveFunction oF = new LPObjectiveFunction(row, isMaximization);
		setObjectiveFunction(oF);
	}
	
	public List<LPConstraint> getConstraints() {
		return _constraints;
	}
	
	public LPConstraint getConstraint(int constraintIndex) {
		return _constraints.get(constraintIndex);
	}
	
	public int getNumberConstraints() {
		return _constraints.size();
	}
	
	public List<LPVariable> getVariables() {
		return _variables;
	}
	
	public LPVariable getVariable(int variableIndex) {
		return _variables.get(variableIndex);
	}
	
	public int getNumberVariables() {
		return _variables.size();
	}
	
	public void setConstraints(List<LPConstraint> constraints) {
		this._constraints = constraints;
	}
	
	public void setVariables(List<LPVariable> variables) {
		this._variables = variables;
	}
	
	public void setObjectiveFunction(LPObjectiveFunction objectiveFunction) {
		this._objectiveFunction = objectiveFunction;
	}
	
	public void addLPProblemListener(LPProblemListener listener) {
		_listeners.add(listener);
	}
	
	public void removeLPProblemListener(LPProblemListener listener) {
		_listeners.remove(listener);
	}
	
	public synchronized void changeVariableBounds(int index, double lower, double upper) {
		this._variables.get(index).setLowerBound(lower);
		this._variables.get(index).setUpperBound(upper);
		fireVariableChanged(_variables.get(index));
	}
	
	public synchronized void changeConstraintBound(int index, double value) {
		this._constraints.get(index).setRightSide(value);
		fireConstraintChanged(_constraints.get(index));
	}
	
	public synchronized void updateLPObjectiveFunction() {
		fireLPObjectiveFunctionChanged();
	}
	
	public synchronized void changeObjectiveSense(boolean isMaximization) {
		_objectiveFunction.setMaximization(isMaximization);
		fireObjectiveSenseChanged(isMaximization);
	}
	
	private void fireVariableChanged(LPVariable var) {
		LPVariableChangedEvent evt = new LPVariableChangedEvent(this, var);
		for (LPProblemListener listener : _listeners)
			listener.updateLPProblemVariable(evt);
	}
	
	private void fireConstraintChanged(LPConstraint lpConstraint) {
		LPConstraintChangedEvent evt = new LPConstraintChangedEvent(this, lpConstraint);
		for (LPProblemListener listener : _listeners)
			listener.updateLPProblemConstraint(evt);
	}
	
	private void fireLPObjectiveFunctionChanged() {
		ObjectiveFunctionChangedEvent evt = new ObjectiveFunctionChangedEvent(this);
		for (LPProblemListener listener : _listeners)
			listener.updateObjectiveFunction(evt);
	}
	
	private void fireObjectiveSenseChanged(boolean isMaximization) {
		ObjectiveSenseChangedEvent evt = new ObjectiveSenseChangedEvent(this, isMaximization);
		for (LPProblemListener listener : _listeners)
			listener.updateObjectiveSense(evt);
	}
	
	private void fireVariableAddedEvent(LPVariable variable) {
		LPVariableAddedEvent evt = new LPVariableAddedEvent(this, variable);
		for (LPProblemListener listener : _listeners)
			listener.addLPVariable(evt);
	}
	
	private void fireVariableAddedAtEvent(LPVariable variable, Integer index) {
		LPVariableAddedEvent evt = new LPVariableAddedEvent(this, variable, index);
		for (LPProblemListener listener : _listeners)
			listener.addLPVariable(evt);
	}
	
	private void fireConstraintAddedEvent(LPConstraint constraint) {
		LPConstraintAddedEvent evt = new LPConstraintAddedEvent(this, constraint);
		for (LPProblemListener listener : _listeners)
			listener.addLPConstraint(evt);
	}
	
	private void fireVariableRemovedEvent(LPVariable variable) {
		LPVariableRemovedEvent evt = new LPVariableRemovedEvent(this, variable);
		for (LPProblemListener listener : _listeners)
			listener.removeLPVariable(evt);
	}
	
	private void fireConstraintRemovedEvent(LPConstraint constraint) {
		LPConstraintRemovedEvent evt = new LPConstraintRemovedEvent(this, constraint);
		for (LPProblemListener listener : _listeners)
			listener.removeLPConstraint(evt);
	}
	
	private void fireConstraintReplacedEvent(LPConstraint oldConstraint, LPConstraint newConstraint){
		LPConstraintReplacedEvent evt = new LPConstraintReplacedEvent(this, oldConstraint, newConstraint);
		for(LPProblemListener listener : _listeners)
			listener.replaceLPConstraint(evt);
	}
	
	private void fireVariableRangeRemoved(List<LPVariable> variables) {
		LPVariableRangeRemovedEvent evt = new LPVariableRangeRemovedEvent(this, variables);
		for (LPProblemListener listener : _listeners)
			listener.removeLPVariableRange(evt);
	}
	
	private void fireConstraintRangeRemoved(List<LPConstraint> constraints) {
		LPConstraintRangeRemovedEvent evt = new LPConstraintRangeRemovedEvent(this, constraints);
		for (LPProblemListener listener : _listeners)
			listener.removeLPConstraintRange(evt);
	}
	
}