/*
 * Copyright 2010
 * IBB-CEB - Institute for Biotechnology and Bioengineering - Centre of Biological Engineering
 * CCTC - Computer Science and Technology Center
 *
 * University of Minho 
 * 
 * This is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version. 
 * 
 * This code is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
 * GNU Public License for more details. 
 * 
 * You should have received a copy of the GNU Public License 
 * along with this code. If not, see http://www.gnu.org/licenses/ 
 * 
 * Created inside the SysBioPseg Research Group (http://sysbio.di.uminho.pt)
 */
package pt.uminho.ceb.biosystems.mew.solvers.lp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.ceb.biosystems.mew.solvers.SolverType;

public class LPSolution implements Serializable {

	private static final long serialVersionUID = 1L;

	protected LPProblem problem;
	protected LPMapVariableValues values;
	protected LPSolutionType solutionType;
	protected String solverOutput;
	protected SolverType solverType;

	protected Map<String, LPMapVariableValues> perVariableMetrics = null;
	protected Map<String, LPMapVariableValues> perConstraintMetrics = null;

	protected double ofValue;

	public LPSolution(LPProblem problem, LPMapVariableValues values,
			LPSolutionType solutionType, double objectiveValue) {
		this.problem = problem;
		this.values = values;
		this.solutionType = solutionType;
		perVariableMetrics = new HashMap<String, LPMapVariableValues>();
		perConstraintMetrics = new HashMap<String, LPMapVariableValues>();
		this.ofValue = objectiveValue;
	}

	public LPProblem getProblem() {
		return problem;
	}

	public void setProblem(LPProblem problem) {
		this.problem = problem;
	}

	public LPMapVariableValues getValues() {
		return values;
	}

	public void setValuesList(LPMapVariableValues values) {
		this.values = values;
	}

	public LPSolutionType getSolutionType() {
		return solutionType;
	}

	public void setSolutionType(LPSolutionType solutionType) {
		this.solutionType = solutionType;
	}

	public String getSolverOutput() {
		return solverOutput;
	}

	public void setSolverOutput(String solverOutput) {
		this.solverOutput = solverOutput;
	}

	public SolverType getSolverType() {
		return solverType;
	}

	public void setSolverType(SolverType solverType) {
		this.solverType = solverType;
	}

	public LPMapVariableValues getPerVariableMetric(String id) {
		return this.perVariableMetrics.get(id);
	}

	public void addPerVariableMetric(String id, LPMapVariableValues values) {
		this.perVariableMetrics.put(id, values);
	}

	public LPMapVariableValues getPerConstraintMetric(String id) {
		return this.perConstraintMetrics.get(id);
	}

	public void addPerConstraintMetric(String id, LPMapVariableValues values) {
		this.perConstraintMetrics.put(id, values);
	}

	public double getOfValue() {
		return ofValue;
	}

	public void setOfValue(double ofValue) {
		this.ofValue = ofValue;
	}
	
	public Set<String> getVariableMetricsIds(){
		return (perVariableMetrics!=null)?perVariableMetrics.keySet():new HashSet<String>();
	}
	
	public Set<String> getConstraintMetricsIds(){
		
		return (perConstraintMetrics!=null)?perConstraintMetrics.keySet():new HashSet<String>();
	}
	
	
}
