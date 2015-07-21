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
package pt.uminho.ceb.biosystems.mew.solvers.qp;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.IQPObjectiveFunctionPersistent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPProblemListener;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.ObjectiveFunctionChangedEvent;

public class QPProblem extends LPProblem implements IQPObjectiveFunctionPersistent {
	
	private static final long	serialVersionUID	= 1L;
	
	public QPObjectiveFunction	qpObjFunction;
	public double				offset				= 0.0;
	
	public QPProblem() {
		super();
		qpObjFunction = new QPObjectiveFunction();
	}
	
	public QPObjectiveFunction getQPObjectiveFunction() {
		return qpObjFunction;
	}
	
	public void setQPObjectiveFunction(QPObjectiveFunction qpObjFunction) {
		this.qpObjFunction = qpObjFunction;
	}
	
	public void setQPObjectiveFunction(QPProblemRow qpRow) {
		this.qpObjFunction = new QPObjectiveFunction(qpRow);
	}
	
	public double getOffset() {
		return offset;
	}
	
	public void setOffset(double offset) {
		this.offset = offset;
	}
	
	@Override
	public void changeQPObjectiveFunction(QPObjectiveFunction lpObjectiveFunction) {
		this.qpObjFunction = lpObjectiveFunction;
		fireQPObjectiveFunctionChanged();
	}
	
	private void fireQPObjectiveFunctionChanged() {
		ObjectiveFunctionChangedEvent evt = new ObjectiveFunctionChangedEvent(this);
		for (LPProblemListener listener : _listeners)
			listener.updateObjectiveFunction(evt);
	}
}
