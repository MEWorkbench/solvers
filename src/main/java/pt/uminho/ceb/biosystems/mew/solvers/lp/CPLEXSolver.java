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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Set;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjective;
import ilog.concert.IloObjectiveSense;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.InfeasibleProblemException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverConstructionException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverDefinitionException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverParametersException;
import pt.uminho.ceb.biosystems.mew.solvers.qp.IQPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPProblem;
import pt.uminho.ceb.biosystems.mew.utilities.io.FileUtils;
import pt.uminho.ceb.biosystems.mew.utilities.java.TimeUtils;

/**
 * 
 * @author pmaia May 24, 2010
 */
public class CPLEXSolver implements ILPSolver,IQPSolver{

	
	protected LPProblem lpProblem;
	protected QPProblem qpProblem;
	protected IloNumVar[] variables = null;
	protected IloRange[] constraints = null;
	
	protected String solverOutput;
	
	boolean computeShadowPrices = true;
	boolean computeReducedCosts = true;
	boolean isMILPproblem = false;
	boolean isQPproblem = false;

	
	public CPLEXSolver(LPProblem problem){
		if(MILPProblem.class.isAssignableFrom(problem.getClass())){
			this.lpProblem = problem;
			isMILPproblem = true;
			isQPproblem = false;
		}
		else if(QPProblem.class.isAssignableFrom(problem.getClass())){
			this.qpProblem = (QPProblem) problem;
			isQPproblem = true;
			isMILPproblem = false;
		}else{
			this.lpProblem = problem;
			isQPproblem = false;
			isMILPproblem = false;
		}
	}
	
	@Override
	public boolean getComputeShadowPrices() {
		return computeShadowPrices;
	}

	public boolean getComputeReducedCosts() {
		return computeReducedCosts;
	}
	

	@Override
	public String getSolverOutput() {
		return solverOutput;
	}

	@Override
	public void resetSolver() {
		solverOutput = "";		
	}

	@Override
	public void setComputeShadowPrices(boolean computeShadowPrices) {
		this.computeShadowPrices = computeShadowPrices;
	}
	
	public void setComputeReducedCosts(boolean computeReducedCosts) {
		this.computeReducedCosts = computeReducedCosts;
	}

	@Override
	public void setProblem(LPProblem problem) {
		this.lpProblem = problem;		
	}

	private void setParam(IloCplex cplex) throws SolverParametersException{
		CplexParamConfiguration.setParameters(cplex);
	}

	public LPSolution solve() throws SolverDefinitionException, InfeasibleProblemException, SolverParametersException, SolverConstructionException {
		
		LPSolution solution =null;
		IloCplex cplex = null;
		try{
			cplex = new IloCplex();
		} catch (Exception e) {
			throw new SolverDefinitionException(CPLEXSolver.class,e);
		} catch (Error e) {
			throw new SolverDefinitionException(CPLEXSolver.class);
		}
		
		setParam(cplex);

			
//		try {
			
			if(isQPproblem)
				populateCPLEXmemory4QP(cplex);
			else
				populateCPLEXmemory(cplex);
			
//		} catch (SolverConstructionException e) {			
//			throw new SolverConstructionException(CPLEXSolver.class);
//		}
			
			
		
		//the solver output
		PrintStream out = cplex.output(); 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream aux = new PrintStream(baos);
		cplex.setOut(aux);
		
		
		//solve the problem
		try {
			cplex.solve();
		} catch (Exception e) {	
			throw new InfeasibleProblemException(CPLEXSolver.class);
		}
		
		
		//flush, close, re-link
		try {
			baos.flush();
		} catch (IOException e) {
			throw new SolverConstructionException(CPLEXSolver.class);
		}
	
		aux.flush();
		solverOutput = baos.toString();

		try {
			baos.close();
		} catch (IOException e) {
			throw new SolverConstructionException(CPLEXSolver.class);
		}
		
		aux.close();
		cplex.setOut(out);		
//	
		//get the status
		IloCplex.Status status;
		try {
			status = cplex.getStatus();
		} catch (Exception e) {	
			cplex.end();
			throw new SolverConstructionException(CPLEXSolver.class);
		}		

		//build the variable values list 		
		LPMapVariableValues valuesList = new LPMapVariableValues();
		double[] solutionValues = new double[variables.length];
		
		try {
			solutionValues = cplex.getValues(variables);
		} catch (Exception e) {
			cplex.end();
//			e.printStackTrace();
			throw new InfeasibleProblemException(CPLEXSolver.class, e);

		}
		
				
		
		for(int i=0;i< solutionValues.length;i++){
			valuesList.addVariableValue(i, solutionValues[i]);
		}
	
		
		//final solution
		try {
			solution = new LPSolution(lpProblem, valuesList, convertCPLexStatus2LPSolutionType(status), cplex.getObjValue());
		} catch (Exception e) {
			cplex.end();
			throw new InfeasibleProblemException(CPLEXSolver.class);
		}
		
		if(computeShadowPrices && !isMILPproblem){
			LPMapVariableValues shadowPrices = new LPMapVariableValues();
			
			for(int i=0;i<constraints.length;i++){
				try {
					shadowPrices.addVariableValue(i, cplex.getDual(constraints[i]));
				} catch (Exception e) {
					cplex.end();
					e.printStackTrace();
					throw new InfeasibleProblemException(CPLEXSolver.class);
				}
			}
				
			// NEED TO CHECK THE NAME !!!
			solution.addPerConstraintMetric("ShadowPrices", shadowPrices);
			
		}
		
		// THIS NEEDS TO BE CHANGED !!!!!!!!!
		if(computeReducedCosts && !isMILPproblem) {
			LPMapVariableValues reducedCosts = new LPMapVariableValues();
			
//			for(int i=0;i<constraints.length;i++){
//				reducedCosts.addShadowValue(i, cplex.getDual(constraints[i]));
//			}

			for(int i=0;i<variables.length;i++){
				try {
					reducedCosts.addVariableValue(i, cplex.getReducedCost(variables[i]));
				} catch (Exception e) {
					cplex.end();
					throw new InfeasibleProblemException(CPLEXSolver.class);
				}
			}
			solution.addPerVariableMetric("ReducedCosts", reducedCosts);
		}
		
//		
		
		//explicitly free CPLEX memory
		cplex.end();
		solution.setSolverType(SolverType.CPLEX);
		solution.setSolverOutput(solverOutput);

		
		return solution;
	}
	
	/**
	 * Populates the CPLEX memory for Linear Programming (LP) and Mixed Integer Linear Programming (MILP) problems.
	 * 
	 * @param cplex the <code>IloCplex</code> instance to be populated.
	 * @throws SolverConstructionException 
	 * @throws Exception 
	 */
	private void populateCPLEXmemory(IloCplex cplex) throws SolverParametersException, SolverConstructionException {
		//variables
		variables = new IloNumVar[lpProblem._variables.size()];
		//constraints
		constraints = new IloRange[lpProblem._constraints.size()];
		//populate the variables
		for(int i=0; i<lpProblem._variables.size(); i++) {
			LPVariable var = lpProblem._variables.get(i);
			IloNumVar cplexVar;
			try {
				
				if(var.isBinary())
					cplexVar = cplex.boolVar(var.variableName);
				else if(var.isInteger())
					cplexVar = cplex.intVar((int)var.lowerBound, (int)var.upperBound, var.variableName);
				else{
	//				cplexVar = cplex.numVar(var.lowerBound, var.upperBound, IloNumVarType.Float, var.variableName);
					cplexVar = cplex.numVar(var.lowerBound, var.upperBound, IloNumVarType.Float, "X"+i);
//					System.out.println("X"+i+"\t=\t"+var.getVariableName());
				}
				
			} catch (Exception e) {
				throw new SolverConstructionException(CPLEXSolver.class);
			}
				
			variables[i] = cplexVar;
		}
		
		//populate the constraints
		for(int i=0;i<lpProblem._constraints.size();i++){
			LPConstraint constraint = lpProblem._constraints.get(i);
			IloLinearNumExpr expression;
			
			try {
				expression = cplex.linearNumExpr();
			} catch (Exception e) {
				throw new SolverConstructionException(CPLEXSolver.class);
			}
			
			Set<Integer> idxs = constraint.getLeftSide().getVarIdxs();
			
			try {
				for(int idx : idxs)
					expression.addTerm(constraint.getLeftSide().getTermCoefficient(idx), variables[idx]);
			} catch (Exception e) {
				throw new SolverConstructionException(CPLEXSolver.class);
			}
			
			try {
				switch (constraint.getType()) {
				case EQUALITY:
						constraints[i] = cplex.addEq(expression, constraint.getRightSide(),"L"+i);
					break;
				case LESS_THAN:
						constraints[i] = cplex.addLe(expression, constraint.getRightSide(), "L"+i);
					break;
				case GREATER_THAN:
						constraints[i] = cplex.addGe(expression, constraint.getRightSide(), "L"+i);
					break;
				}
			} catch (Exception e) {
				throw new SolverConstructionException(CPLEXSolver.class);
			}
		}
		
		
		//the objective function expression
		IloLinearNumExpr ofExpression;
		try {
			ofExpression = cplex.linearNumExpr();
		} catch (Exception e) {
			throw new SolverConstructionException(CPLEXSolver.class);
		}
		Set<Integer> idxs = lpProblem.getObjectiveFunction().getRow().getVarIdxs();
		for(Integer idx : idxs){
			try {
				ofExpression.addTerm(
					lpProblem.getObjectiveFunction().getRow().getTermCoefficient(idx), 
					variables[idx]
				);
			} catch (Exception e) {
				throw new SolverConstructionException(CPLEXSolver.class);
			}
		}
		
		//objective function sense (max/min)
		IloObjectiveSense objectiveSense = (lpProblem.getObjectiveFunction().isMaximization()) 	?	IloObjectiveSense.Maximize : 
																									IloObjectiveSense.Minimize;
		
		//the objective function itself
		IloObjective objectiveFunction;
		try {
			objectiveFunction = cplex.objective(objectiveSense,ofExpression,"objectiveFunction");
		} catch (Exception e) {
			throw new SolverConstructionException(CPLEXSolver.class);
		}
		
		//add everything to CPLEX memory
		try {
			cplex.add(variables);

			cplex.add(constraints);

			cplex.add(objectiveFunction);
		} catch (Exception e) {
			throw new SolverConstructionException(CPLEXSolver.class);
		}
		
//		cplex.exportModel("cplex.mps");
	}

	/**
	 * Populates the CPLEX memory for Quadratic Programming (QP) problems.
	 * 
	 * @param cplex the <code>IloCplex</code> instance to be populated
	 * @throws SolverConstructionException 
	 * @throws SolverParametersException 
	 */
	private void populateCPLEXmemory4QP(IloCplex cplex) throws SolverConstructionException, SolverParametersException  {
		//variables
		variables = new IloNumVar[qpProblem.getVariables().size()];
		//constraints
		constraints = new IloRange[qpProblem.getConstraints().size()];
		
		//populate the variables
		for(int i=0; i<qpProblem.getVariables().size(); i++){
			LPVariable var = qpProblem.getVariables().get(i);
			try {
				variables[i] = cplex.numVar(var.lowerBound, var.upperBound,IloNumVarType.Float);
//				System.out.println("X"+i+"\t=\t"+var.getVariableName());
			} catch (Exception e) {
				throw new SolverConstructionException(CPLEXSolver.class);
			}
		}
		
		//populate the constraints
		for(int i=0;i<qpProblem.getNumberConstraints();i++){
			LPConstraint constraint = qpProblem.getConstraint(i);
			IloLinearNumExpr expression;
			try {
				expression = cplex.linearNumExpr();
			} catch (Exception e) {
				throw new SolverConstructionException(CPLEXSolver.class);
			}
			Set<Integer> idxs = constraint.getLeftSide().getVarIdxs();
			
			for(int idx : idxs)
				try {
					expression.addTerm(constraint.getLeftSide().getTermCoefficient(idx), variables[idx]);
				} catch (Exception e) {
					throw new SolverConstructionException(CPLEXSolver.class);
				}
			
			try {
				if(constraint.getType().equals(LPConstraintType.EQUALITY))
					constraints[i] = cplex.eq(expression, constraint.getRightSide());
				else if(constraint.getType().equals(LPConstraintType.GREATER_THAN))
					constraints[i] = cplex.ge(expression, constraint.getRightSide());
				else if(constraint.getType().equals(LPConstraintType.LESS_THAN))
					constraints[i] = cplex.le(expression, constraint.getRightSide());
			} catch (Exception e) {
				throw new SolverConstructionException(CPLEXSolver.class);
			}
		}
		
//		//the linear part of the objective function
		
		
		Set<Integer> idxs = qpProblem.getObjectiveFunction().getRow().getVarIdxs();
		double[] coeffs = new double[idxs.size()];
		IloNumVar[] varInOF = new IloNumVar[idxs.size()];
		int j =0;
		
		for(Integer i : idxs){
			varInOF[j]=variables[i];
			coeffs[j] = (qpProblem.getObjectiveFunction().getRow().getTermCoefficient(i));
			j++;
		}


		
		//the quadratic part of the objective function
		IloNumExpr[] exprs = new IloNumExpr[qpProblem.getQPObjectiveFunction().getQpRow().getNumberOfTerms()];		
		for(int i=0;i<qpProblem.getQPObjectiveFunction().getQpRow().getNumberOfTerms();i++){ 			
			try {
				exprs[i] = cplex.prod(
						qpProblem.getQPObjectiveFunction().getQpRow().getTerm(i).getCoefficientValue(),
						variables[qpProblem.getQPObjectiveFunction().getQpRow().getTerm(i).getVariableIndex1()],
						variables[qpProblem.getQPObjectiveFunction().getQpRow().getTerm(i).getVariableIndex2()]
						);
			} catch (Exception e) {
				throw new SolverConstructionException(CPLEXSolver.class);
			}			
		}
		
		IloNumExpr quadExpression;
		try {
			quadExpression = cplex.prod(0.5, cplex.sum(exprs));
		} catch (Exception e) {
			throw new SolverConstructionException(CPLEXSolver.class);
		}
		
		IloNumExpr finalExpression;
		try {
			finalExpression = cplex.sum(cplex.scalProd(varInOF, coeffs),quadExpression);
		} catch (Exception e) {
			throw new SolverConstructionException(CPLEXSolver.class);
		}
		

		
		//add everything to CPLEX memory
		try {
			
			cplex.add(variables);
			cplex.add(constraints);
			cplex.addMinimize(finalExpression);
			
		} catch (Exception e) {
			throw new SolverConstructionException(CPLEXSolver.class);
		}
		
	
	}

	public LPSolutionType convertCPLexStatus2LPSolutionType(IloCplex.Status status){
		
		if(status.equals(IloCplex.Status.Bounded))
			return LPSolutionType.BOUND;
		
		else if(status.equals(IloCplex.Status.Error))
			return LPSolutionType.ERROR;
		
		else if(status.equals(IloCplex.Status.Feasible))
			return LPSolutionType.FEASIBLE;
		
		else if(status.equals(IloCplex.Status.Infeasible))
			return LPSolutionType.INFEASIBLE;
		
		else if(status.equals(IloCplex.Status.InfeasibleOrUnbounded))
			return LPSolutionType.INFEASIBLE_OR_UNBOUND;
		
		else if(status.equals(IloCplex.Status.Optimal))
			return LPSolutionType.OPTIMAL;
		
		else if(status.equals(IloCplex.Status.Unbounded))
			return LPSolutionType.UNBOUND;
		
		else return LPSolutionType.UNKNOWN;
	}

	@Override
	public void setQPProblem(QPProblem problem) {
		this.isMILPproblem = false;
		this.isQPproblem = true;
		this.qpProblem = problem;
	}
	
	@Override
	public void saveModelToMPS(String file, boolean includeTime) {
		
		if (includeTime) {
			String extension = FileUtils.getFileExtension(file);
			String time = TimeUtils.formatMillis(System.currentTimeMillis());
			file = file.replaceFirst(extension, "") + "_" + time + "." + extension;
		}
		
		try {
			IloCplex cplex = null;
			try{
				cplex = new IloCplex();
			} catch (Exception e) {
				throw new SolverDefinitionException(CPLEXSolver.class,e);
			} catch (Error e) {
				throw new SolverDefinitionException(CPLEXSolver.class);
			}
			
			setParam(cplex);
						
			if(isQPproblem)
				populateCPLEXmemory4QP(cplex);
			else
				populateCPLEXmemory(cplex);
			cplex.exportModel(file);
		} catch (IloException e1) {
			throw new SolverException(getClass(),e1);
		}
		
	}

}
