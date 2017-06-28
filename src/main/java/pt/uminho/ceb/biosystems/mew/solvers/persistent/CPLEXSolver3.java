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
package pt.uminho.ceb.biosystems.mew.solvers.persistent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ilog.concert.IloAddable;
import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.concert.IloObjective;
import ilog.concert.IloObjectiveSense;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import pt.uminho.ceb.biosystems.mew.solvers.lp.ILPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPConstraint;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPConstraintType;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPMapVariableValues;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolution;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPSolutionType;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPVariable;
import pt.uminho.ceb.biosystems.mew.solvers.lp.MILPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.InfeasibleProblemException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverConstructionException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverDefinitionException;
import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverParametersException;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPConstraintAddedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPConstraintChangedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPConstraintRangeAddedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPConstraintRangeRemovedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPConstraintRemovedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPConstraintReplacedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPProblemListener;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPVariableAddedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPVariableChangedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPVariableRangeAddedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPVariableRangeRemovedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.LPVariableRemovedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.ObjectiveFunctionChangedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.persistent.listener.ObjectiveSenseChangedEvent;
import pt.uminho.ceb.biosystems.mew.solvers.qp.IQPSolver;
import pt.uminho.ceb.biosystems.mew.solvers.qp.QPProblem;
import pt.uminho.ceb.biosystems.mew.utilities.io.FileUtils;
import pt.uminho.ceb.biosystems.mew.utilities.java.TimeUtils;
// import solvers.lp.exceptions.CplexParamTypeException;

/**
 * CPLEX connector using a persistent model for improved performance.
 * 
 * @author pmaia
 * @date Apr 21, 2014
 * @version 0.2
 * @since solvers2
 */
public class CPLEXSolver3 implements LPProblemListener, ILPSolver, IQPSolver {
	
	/** Debug flag */
	public static final boolean				_debug					= false;
	
	/** Debug flag */
	public static final boolean				_debug_time				= false;
	
	/** The initial time */
	public static long						initTime				= System.currentTimeMillis();
	
	/** This variable is assigned when the problem is either a LP or a MILP */
	protected LPProblem						_lpProblem;
	
	/** This variable is assigned when the problem is a QP */
	protected QPProblem						_qpProblem;
	
	/** Pointers for CPLEX internal variables */
	protected List<IloNumVar>				_variables				= null;
	
	/** Pointers for CPLEX internal constraints */
	protected List<IloRange>				_constraints			= null;
	
	/** Pointer for CPLEX internal objective function */
	protected IloObjective					_objectiveFunction		= null;
	
	/** Internal CPLEX model */
	protected IloCplex						_cplex					= null;
	
	/** Mappings between LPProblem variables and CPLEX variables */
	protected Map<LPVariable, IloNumVar>	_mapLPvarToCPLEXvar		= null;
	
	/** Mappings between LPProblem constraints and CPLEX constraints */
	protected Map<LPConstraint, IloRange>	_mapLPconstToCPLEXconst	= null;
	
	/** Solver output string */
	protected String						_solverOutput;
	
	/** Flag controlling computation of shadow prices (variables) */
	protected boolean						_computeShadowPrices	= false;
	
	/** Flag controlling computation of reduced costs (constraints) */
	protected boolean						_computeReducedCosts	= true;
	
	/** Counter for CPLEX number of (re)initializations */
	protected int							_initializations		= 0;
	
	/** Flag for MILP problems */
	protected boolean						_isMILPproblem			= false;
	
	/** Flag for QP problems */
	protected boolean						_isQPproblem			= false;
	
	/**
	 * Default constructor
	 * 
	 * @param problem the <code>LPProblem</code> (can be an LP, MILP or QP)
	 */
	public CPLEXSolver3(LPProblem problem, boolean warmup) {
		if (MILPProblem.class.isAssignableFrom(problem.getClass())) {
			_lpProblem = problem;
			_isMILPproblem = true;
			_isQPproblem = false;
			if (_debug) System.out.println("Problem is MILP");
		} else if (QPProblem.class.isAssignableFrom(problem.getClass())) {
			_qpProblem = (QPProblem) problem;
			_isQPproblem = true;
			_isMILPproblem = false;
			if (_debug) System.out.println("Problem is QP");
		} else {
			_lpProblem = problem;
			_isQPproblem = false;
			_isMILPproblem = false;
			if (_debug) System.out.println("Problem is LP");
		}
		
		if (_isQPproblem) {
			((QPProblem) _qpProblem).addLPProblemListener(this);
			if (_debug) System.out.println("Added QPProblemListener");
		} else {
			((LPProblem) _lpProblem).addLPProblemListener(this);
			if (_debug) System.out.println("Added LPProblemListener");
		}
		
		if (warmup) initializeSolver();
	}
	
	public CPLEXSolver3(LPProblem problem) {
		this(problem, false);
	}
	
	/**
	 * Populates the CPLEX memory for Linear Programming (LP) and Mixed Integer
	 * Linear Programming (MILP) problems.
	 * 
	 * @param cplex
	 *            the <code>IloCplex</code> instance to be populated.
	 * @throws Exception
	 */
	private void populateCPLEXmemory(IloCplex cplex) throws SolverConstructionException {
		
		// variables 
		_variables = new ArrayList<IloNumVar>();
		
		// constraints 
		_constraints = new ArrayList<IloRange>();
		
		// mappings to cplex variables
		_mapLPvarToCPLEXvar = new HashMap<LPVariable, IloNumVar>();
		// mappings to cplex constraints
		_mapLPconstToCPLEXconst = new HashMap<LPConstraint, IloRange>();
		
		// populate the variables
		if (_debug_time) initTime = System.currentTimeMillis();
		for (int i = 0; i < _lpProblem._variables.size(); i++) {
			LPVariable var = _lpProblem._variables.get(i);
			// System.out.println("var [" + i + "] = " + var.getVariableName());
			IloNumVar cplexVar;
			try {
				
				if (var.isBinary())
					cplexVar = cplex.boolVar(var.variableName);
				else if (var.isInteger())
					cplexVar = cplex.intVar((int) var.lowerBound, (int) var.upperBound, var.variableName);
				else{
					cplexVar = cplex.numVar(var.lowerBound, var.upperBound, IloNumVarType.Float, "X" + i);
//					System.out.println("X"+i+"\t=\t"+var.getVariableName());
				}
				
			} catch (Exception e) {
				throw new SolverConstructionException(getClass(), e);
			}
			
			_variables.add(i, cplexVar);
			_mapLPvarToCPLEXvar.put(var, _variables.get(i));
		}
		if (_debug_time) System.out.println("[" + getClass().getSimpleName() + "]: populateCPLEXMemory - variables: \t" + TimeUtils.formatMillis(System.currentTimeMillis() - initTime));
		
		// populate the constraints
		if (_debug_time) initTime = System.currentTimeMillis();
		for (int i = 0; i < _lpProblem._constraints.size(); i++) {
			long subtime = System.currentTimeMillis();
			LPConstraint constraint = _lpProblem._constraints.get(i);
			// System.out.println("const [" + i + "] = " + constraint.name);
			IloLinearNumExpr expression;
			try {
				expression = cplex.linearNumExpr();
			} catch (Exception e) {
				throw new SolverConstructionException(getClass(), e);
			}
			Set<Integer> idxs = constraint.getLeftSide().getVarIdxs();
			
			try {
				for (int idx : idxs)
					expression.addTerm(constraint.getLeftSide().getTermCoefficient(idx), _variables.get(idx));
			} catch (Exception e) {
				throw new SolverConstructionException(getClass(), e);
			}
			
			try {
				switch (constraint.getType()) {
					case EQUALITY:
						_constraints.add(i, cplex.addEq(expression, constraint.getRightSide(), "L" + i));
						break;
					case LESS_THAN:
						_constraints.add(i, cplex.addLe(expression, constraint.getRightSide(), "L" + i));
						break;
					case GREATER_THAN:
						_constraints.add(i, cplex.addGe(expression, constraint.getRightSide(), "L" + i));
						break;
				}
			} catch (Exception e) {
				throw new SolverConstructionException(getClass(), e);
			}
			if (_debug_time) System.out.println("\tconstraint [" + constraint.toString() + "]: " + TimeUtils.formatMillis(System.currentTimeMillis() - subtime));
			
			_mapLPconstToCPLEXconst.put(constraint, _constraints.get(i));
		}
		if (_debug_time) System.out.println("[" + getClass().getSimpleName() + "]: populateCPLEXMemory - constraints: \t" + TimeUtils.formatMillis(System.currentTimeMillis() - initTime));
		
		if (_debug) System.out.println("update LP objective function");
		IloLinearNumExpr ofExpression;
		try {
			ofExpression = cplex.linearNumExpr();
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		Set<Integer> idxs = _lpProblem.getObjectiveFunction().getRow().getVarIdxs();
		for (Integer idx : idxs) {
			try {
				ofExpression.addTerm(_lpProblem.getObjectiveFunction().getRow().getTermCoefficient(idx), _variables.get(idx));
			} catch (Exception e) {
				throw new SolverConstructionException(getClass(), e);
			}
		}
		
		// objective function sense (max/min)
		IloObjectiveSense objectiveSense = (_lpProblem.getObjectiveFunction().isMaximization()) ? IloObjectiveSense.Maximize : IloObjectiveSense.Minimize;
		
		// the objective function itself
		try {
			_objectiveFunction = cplex.objective(objectiveSense, ofExpression, "objectiveFunction");
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		// add everything to CPLEX memory
		try {
			
			IloAddable[] vars = new IloAddable[_variables.size()];
			vars = _variables.toArray(vars);
			
			IloAddable[] cons = new IloAddable[_constraints.size()];
			cons = _constraints.toArray(cons);
			
			cplex.add(vars);
			
			cplex.add(cons);
			
			cplex.add(_objectiveFunction);
			
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
			
		}
	}
	
	/**
	 * Populates the CPLEX memory for Quadratic Programming (QP) problems.
	 * 
	 * @param cplex
	 *            the <code>IloCplex</code> instance to be populated
	 * @throws SolverConstructionException
	 */
	private void populateCPLEXmemory4QP(IloCplex cplex) throws SolverConstructionException {
		// variables
		_variables = new ArrayList<IloNumVar>();
		// constraints
		_constraints = new ArrayList<IloRange>();
		// mappings to cplex variables
		_mapLPvarToCPLEXvar = new HashMap<LPVariable, IloNumVar>();
		// mappings to cplex constraints
		_mapLPconstToCPLEXconst = new HashMap<LPConstraint, IloRange>();
		
		// populate the variables
		for (int i = 0; i < _qpProblem.getVariables().size(); i++) {
			LPVariable var = _qpProblem.getVariables().get(i);
			try {
				_variables.add(i, cplex.numVar(var.lowerBound, var.upperBound, IloNumVarType.Float));
//				System.out.println("X"+i+"\t=\t"+var.getVariableName());
			} catch (Exception e) {
				throw new SolverConstructionException(getClass(), e);
			}
			_mapLPvarToCPLEXvar.put(var, _variables.get(i));
		}
		
		// populate the constraints
		for (int i = 0; i < _qpProblem.getNumberConstraints(); i++) {
			LPConstraint constraint = _qpProblem.getConstraint(i);
			IloLinearNumExpr expression;
			try {
				expression = cplex.linearNumExpr();
			} catch (Exception e) {
				throw new SolverConstructionException(getClass(), e);
			}
			Set<Integer> idxs = constraint.getLeftSide().getVarIdxs();
			
			try {
				for (int idx : idxs)
					expression.addTerm(constraint.getLeftSide().getTermCoefficient(idx), _variables.get(idx));
				
			} catch (Exception e) {
				throw new SolverConstructionException(getClass(), e);
			}
			
			try {
				if (constraint.getType().equals(LPConstraintType.EQUALITY))
					_constraints.add(i, cplex.eq(expression, constraint.getRightSide()));
				else if (constraint.getType().equals(LPConstraintType.GREATER_THAN))
					_constraints.add(i, cplex.ge(expression, constraint.getRightSide()));
				else if (constraint.getType().equals(LPConstraintType.LESS_THAN)) _constraints.add(i, cplex.le(expression, constraint.getRightSide()));
			} catch (Exception e) {
				throw new SolverConstructionException(getClass(), e);
			}
			_mapLPconstToCPLEXconst.put(constraint, _constraints.get(i));
		}
		
		// updateObjectiveFunctionQP(cplex);
		
		// the linear part of the objective function
		Set<Integer> idxs = _qpProblem.getObjectiveFunction().getRow().getVarIdxs();
		double[] coeffs = new double[idxs.size()];
		IloNumVar[] varsInOF = new IloNumVar[idxs.size()];
		int j = 0;
		
		for (Integer i : idxs) {
			varsInOF[j] = _variables.get(i);
			coeffs[j] = (_qpProblem.getObjectiveFunction().getRow().getTermCoefficient(i));
			j++;
		}
		
		// the quadratic part of the objective function
		IloNumExpr[] exprs = new IloNumExpr[_qpProblem.getQPObjectiveFunction().getQpRow().getNumberOfTerms()];
		for (int i = 0; i < _qpProblem.getQPObjectiveFunction().getQpRow().getNumberOfTerms(); i++) {
			try {
				exprs[i] = cplex.prod(_qpProblem.getQPObjectiveFunction().getQpRow().getTerm(i).getCoefficientValue(), _variables.get(_qpProblem.getQPObjectiveFunction().getQpRow().getTerm(i).getVariableIndex1()),
						_variables.get(_qpProblem.getQPObjectiveFunction().getQpRow().getTerm(i).getVariableIndex2()));
			} catch (Exception e) {
				throw new SolverConstructionException(getClass(), e);
			}
		}
		
		IloNumExpr quadExpression;
		try {
			quadExpression = cplex.prod(0.5, cplex.sum(exprs));
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		IloNumExpr finalExpression;
		try {
			finalExpression = cplex.sum(cplex.scalProd(varsInOF, coeffs), quadExpression);
			_objectiveFunction = cplex.objective(IloObjectiveSense.Minimize, finalExpression);
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		// add everything to CPLEX memory
		try {
			
			IloAddable[] vars = new IloAddable[_variables.size()];
			vars = _variables.toArray(vars);
			
			IloAddable[] cons = new IloAddable[_constraints.size()];
			cons = _constraints.toArray(cons);
			
			cplex.add(vars);
			
			cplex.add(cons);
			
			cplex.add(_objectiveFunction);
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
	}
	
	/**
	 * Re-assign the cplex objective function from the LPProblem
	 * 
	 * @param cplex
	 * @throws IloException
	 */
	private void updateObjectiveFunctionLP() throws IloException {
		_objectiveFunction.clearExpr();
		IloLinearNumExpr ofExpression;
		try {
			ofExpression = _cplex.linearNumExpr();
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		Set<Integer> idxs = _lpProblem.getObjectiveFunction().getRow().getVarIdxs();
		for (Integer idx : idxs) {
			try {
				ofExpression.addTerm(_lpProblem.getObjectiveFunction().getRow().getTermCoefficient(idx), _variables.get(idx));
			} catch (Exception e) {
				throw new SolverConstructionException(getClass(), e);
			}
		}
		
		// objective function sense (max/min)
		IloObjectiveSense objectiveSense = (_lpProblem.getObjectiveFunction().isMaximization()) ? IloObjectiveSense.Maximize : IloObjectiveSense.Minimize;
		
		_objectiveFunction.setName("objectiveFunction");
		_objectiveFunction.setSense(objectiveSense);
		_objectiveFunction.setExpr(ofExpression);
	}
	
	/**
	 * Re-assign the cplex objective function from the QPProblem
	 * 
	 * @param cplex
	 * @throws IloException
	 */
	public void updateObjectiveFunctionQP() throws IloException {
		
		_objectiveFunction.clearExpr();
		// the linear part of the objective function
		Set<Integer> idxs = _qpProblem.getObjectiveFunction().getRow().getVarIdxs();
		double[] coeffs = new double[idxs.size()];
		IloNumVar[] varsInOF = new IloNumVar[idxs.size()];
		int j = 0;
		
		for (Integer i : idxs) {
			varsInOF[j] = _variables.get(i);
			coeffs[j] = (_qpProblem.getObjectiveFunction().getRow().getTermCoefficient(i));
			j++;
		}
		
		// the quadratic part of the objective function
		IloNumExpr[] exprs = new IloNumExpr[_qpProblem.getQPObjectiveFunction().getQpRow().getNumberOfTerms()];
		for (int i = 0; i < _qpProblem.getQPObjectiveFunction().getQpRow().getNumberOfTerms(); i++) {
			try {
				exprs[i] = _cplex.prod(_qpProblem.getQPObjectiveFunction().getQpRow().getTerm(i).getCoefficientValue(), _variables.get(_qpProblem.getQPObjectiveFunction().getQpRow().getTerm(i).getVariableIndex1()),
						_variables.get(_qpProblem.getQPObjectiveFunction().getQpRow().getTerm(i).getVariableIndex2()));
			} catch (Exception e) {
				throw new SolverConstructionException(getClass(), e);
			}
		}
		
		IloNumExpr quadExpression;
		try {
			quadExpression = _cplex.prod(0.5, _cplex.sum(exprs));
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		IloNumExpr finalExpression;
		try {
			finalExpression = _cplex.sum(_cplex.scalProd(varsInOF, coeffs), quadExpression);
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		_objectiveFunction.setName("objectiveFunction");
		_objectiveFunction.setSense(IloObjectiveSense.Minimize);
		_objectiveFunction.setExpr(finalExpression);
	}
	
	private void initializeSolver() throws SolverDefinitionException, SolverConstructionException {
		try {
			_cplex = new IloCplex();
			_cplex.setDefaults();
			//			_cplex.setDeleteMode(DeleteMode.FixBasis); 	// pivots and fixes the basis in order to guarantee that a valid basis is still available upon removal of variables 
			setParam(_cplex);
			
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		try {
			if (_isQPproblem)
				populateCPLEXmemory4QP(_cplex);
			else
				populateCPLEXmemory(_cplex);
		} catch (SolverConstructionException e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		_initializations++;
		if (_debug) System.out.println("Solver initialization #" + _initializations);
	}
	
	public LPSolution solve() throws SolverDefinitionException, SolverConstructionException, InfeasibleProblemException {
		
		LPSolution solution = null;
		
		if (_cplex == null) try {
			initializeSolver();
		} catch (SolverDefinitionException e) {
			throw new SolverDefinitionException(getClass(), e);
		} catch (SolverConstructionException e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		// the solver output
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream aux = new PrintStream(baos);
		_cplex.setOut(aux);
		
		
		// solve the problem
		try {
			_cplex.solve();
		} catch (Exception e) {
			throw new InfeasibleProblemException(getClass(), e);
		}
		
		// flush, close, re-link
		try {
			baos.flush();
		} catch (IOException e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		aux.flush();
		_solverOutput = baos.toString();
		
		try {
			baos.close();
		} catch (IOException e) {
			throw new SolverConstructionException(getClass(), e);
		}
		aux.close();
		
		// get the status
		IloCplex.Status status;
		try {
			status = _cplex.getStatus();
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		// build the variable values list
		LPMapVariableValues valuesList = new LPMapVariableValues();
		double[] solutionValues = new double[_variables.size()];
		
		try {
			IloNumVar[] vars = new IloNumVar[_variables.size()];
			vars = _variables.toArray(vars);
			solutionValues = _cplex.getValues(vars);
		} catch (Exception e) {
			//			e.printStackTrace();
			throw new SolverConstructionException(getClass(), e);
		}
		
		for (int i = 0; i < solutionValues.length; i++) {
			valuesList.addVariableValue(i, solutionValues[i]);
		}
		
		// final solution
		try {
			solution = new LPSolution(_lpProblem, valuesList, convertCPLexStatus2LPSolutionType(status), _cplex.getObjValue());
		} catch (Exception e) {
			new InfeasibleProblemException(getClass(), e);
		}
		
		if (_computeShadowPrices && !_isMILPproblem) {
			LPMapVariableValues shadowPrices = new LPMapVariableValues();
			
			try {
				for (int i = 0; i < _constraints.size(); i++) {
					shadowPrices.addVariableValue(i, _cplex.getDual(_constraints.get(i)));
				}
			} catch (Exception e) {
				throw new SolverConstructionException(CPLEXSolver3.class, e);
			}
			
			solution.addPerConstraintMetric("ShadowPrices", shadowPrices);
			
		}
		
		if (_computeReducedCosts && !_isMILPproblem) {
			LPMapVariableValues reducedCosts = new LPMapVariableValues();
			try {
				for (int i = 0; i < _variables.size(); i++) {
					reducedCosts.addVariableValue(i, _cplex.getReducedCost(_variables.get(i)));
				}
			} catch (Exception e) {
				throw new SolverConstructionException(CPLEXSolver3.class, e);
			}
			solution.addPerVariableMetric("ReducedCosts", reducedCosts);
		}
		
		solution.setSolverType(SolverType.CPLEX);
		solution.setSolverOutput(_solverOutput);
		
		return solution;
	}
	
	public LPSolutionType convertCPLexStatus2LPSolutionType(IloCplex.Status status) {
		
		if (status.equals(IloCplex.Status.Bounded))
			return LPSolutionType.BOUND;
		
		else if (status.equals(IloCplex.Status.Error))
			return LPSolutionType.ERROR;
		
		else if (status.equals(IloCplex.Status.Feasible))
			return LPSolutionType.FEASIBLE;
		
		else if (status.equals(IloCplex.Status.Infeasible))
			return LPSolutionType.INFEASIBLE;
		
		else if (status.equals(IloCplex.Status.InfeasibleOrUnbounded))
			return LPSolutionType.INFEASIBLE_OR_UNBOUND;
		
		else if (status.equals(IloCplex.Status.Optimal))
			return LPSolutionType.OPTIMAL;
		
		else if (status.equals(IloCplex.Status.Unbounded))
			return LPSolutionType.UNBOUND;
		
		else
			return LPSolutionType.UNKNOWN;
	}
	
	private void addLPConstraint(LPConstraint constraint) {
		IloLinearNumExpr expression;
		try {
			expression = _cplex.linearNumExpr();
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		Set<Integer> idxs = constraint.getLeftSide().getVarIdxs();
		
		try {
			for (int idx : idxs)
				expression.addTerm(constraint.getLeftSide().getTermCoefficient(idx), _variables.get(idx));
			
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		int index = _constraints.size();
		try {
			if (constraint.getType().equals(LPConstraintType.EQUALITY))
				_constraints.add(index, _cplex.eq(expression, constraint.getRightSide()));
			else if (constraint.getType().equals(LPConstraintType.GREATER_THAN))
				_constraints.add(index, _cplex.ge(expression, constraint.getRightSide()));
			else if (constraint.getType().equals(LPConstraintType.LESS_THAN)) _constraints.add(index, _cplex.le(expression, constraint.getRightSide()));
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		_mapLPconstToCPLEXconst.put(constraint, _constraints.get(index));
		try {
			_cplex.add(_constraints.get(index));
		} catch (IloException e) {
			throw new SolverConstructionException(getClass(), e);
		}
	}
	
	private void replaceLPConstraint(LPConstraint oldConstraint, LPConstraint newConstraint) {
		IloLinearNumExpr expression ;
		try {
			expression = _cplex.linearNumExpr();
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		Set<Integer> idxs = newConstraint.getLeftSide().getVarIdxs();
		
		try {
			double bound = newConstraint.getRightSide();
//			System.out.println("BEFORE = "+(_mapLPconstToCPLEXconst.get(oldConstraint)).toString());
//			((IloLinearNumExpr) _mapLPconstToCPLEXconst.get(oldConstraint).getExpr()).clear();
			try {
				for (int idx : idxs) {
//					((IloLinearNumExpr) _mapLPconstToCPLEXconst.get(oldConstraint).getExpr()).addTerm(newConstraint.getLeftSide().getTermCoefficient(idx), _variables.get(idx));
											expression.addTerm(newConstraint.getLeftSide().getTermCoefficient(idx), _variables.get(idx));
				}
				_mapLPconstToCPLEXconst.get(oldConstraint).setExpr(expression);
//				System.out.println("AFTER = "+(_mapLPconstToCPLEXconst.get(oldConstraint)).toString());	
			} catch (Exception e) {
				e.printStackTrace();
				//					throw new SolverConstructionException(getClass(), e);
			}
			_mapLPconstToCPLEXconst.put(newConstraint, _mapLPconstToCPLEXconst.get(oldConstraint));
			_mapLPconstToCPLEXconst.remove(oldConstraint);
			if (newConstraint.getType().equals(LPConstraintType.EQUALITY)) {
				_mapLPconstToCPLEXconst.get(newConstraint).setBounds(bound, bound);
			} else if (newConstraint.getType().equals(LPConstraintType.GREATER_THAN)) {
				_mapLPconstToCPLEXconst.get(newConstraint).setBounds(bound, Double.MAX_VALUE);
			} else if (newConstraint.getType().equals(LPConstraintType.LESS_THAN)) {
				_mapLPconstToCPLEXconst.get(newConstraint).setBounds(-Double.MAX_VALUE, bound);
			}
			
		} catch (Exception e) {			
			throw new SolverConstructionException(getClass(), e);
		}
		
	}
	
	private void addLPConstraintAt(int index, LPConstraint constraint) {
		IloLinearNumExpr expression;
		try {
			expression = _cplex.linearNumExpr();
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		Set<Integer> idxs = constraint.getLeftSide().getVarIdxs();
		
		try {
			for (int idx : idxs)
				expression.addTerm(constraint.getLeftSide().getTermCoefficient(idx), _variables.get(idx));
			
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		try {
			if (constraint.getType().equals(LPConstraintType.EQUALITY))
				_constraints.add(index, _cplex.eq(expression, constraint.getRightSide()));
			else if (constraint.getType().equals(LPConstraintType.GREATER_THAN))
				_constraints.add(index, _cplex.ge(expression, constraint.getRightSide()));
			else if (constraint.getType().equals(LPConstraintType.LESS_THAN)) _constraints.add(index, _cplex.le(expression, constraint.getRightSide()));
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		_mapLPconstToCPLEXconst.put(constraint, _constraints.get(index));
		try {
			_cplex.add(_constraints.get(index));
		} catch (IloException e) {
			throw new SolverConstructionException(getClass(), e);
		}
	}
	
	private void addLPVariable(LPVariable variable) {
		IloNumVar cplexVar;
		int index = _variables.size();
		try {
			if (variable.isBinary())
				cplexVar = _cplex.boolVar(variable.variableName);
			else if (variable.isInteger())
				cplexVar = _cplex.intVar((int) variable.lowerBound, (int) variable.upperBound, variable.variableName);
			else
				cplexVar = _cplex.numVar(variable.lowerBound, variable.upperBound, IloNumVarType.Float, "X" + index);
			
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		_variables.add(index, cplexVar);
		_mapLPvarToCPLEXvar.put(variable, _variables.get(index));
		try {
			_cplex.add(_variables.get(index));
		} catch (IloException e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
	}
	
	private void addLPVariableAt(int index, LPVariable variable) {
		IloNumVar cplexVar;
		try {
			if (variable.isBinary())
				cplexVar = _cplex.boolVar(variable.variableName);
			else if (variable.isInteger())
				cplexVar = _cplex.intVar((int) variable.lowerBound, (int) variable.upperBound, variable.variableName);
			else
				cplexVar = _cplex.numVar(variable.lowerBound, variable.upperBound, IloNumVarType.Float, "X" + index);
			
		} catch (Exception e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
		_variables.add(index, cplexVar);
		_mapLPvarToCPLEXvar.put(variable, _variables.get(index));
		try {
			_cplex.add(_variables.get(index));
		} catch (IloException e) {
			throw new SolverConstructionException(getClass(), e);
		}
		
	}
	
	private void removeLPVariable(LPVariable variable) {
		try {
			IloNumVar cplexVar = _mapLPvarToCPLEXvar.get(variable);
			_variables.remove(cplexVar);
			_mapLPvarToCPLEXvar.remove(variable);
			_cplex.end(cplexVar);
			_cplex.remove(cplexVar);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}
	
	private void removeLPConstraint(LPConstraint constraint) {
		try {
			IloRange cplexConst = _mapLPconstToCPLEXconst.get(constraint);
			_constraints.remove(cplexConst);
			_mapLPconstToCPLEXconst.remove(constraint);
			_cplex.end(cplexConst);
			_cplex.remove(cplexConst);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void updateLPProblemVariable(LPVariableChangedEvent event) {
		if (_cplex != null && event != null) {
			LPVariable var = event.get_variable();
			double lb = var.getLowerBound();
			double ub = var.getUpperBound();
			if (_debug) System.out.println("CPLEX RECEIVED VARIABLE CHANGED " + var.variableName + " / L=" + lb + " / U=" + ub);
			try {
				_mapLPvarToCPLEXvar.get(var).setLB(lb);
				_mapLPvarToCPLEXvar.get(var).setUB(ub);
			} catch (IloException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void updateLPProblemConstraint(LPConstraintChangedEvent event) {
		if (_cplex != null && event != null) {
			LPConstraint constraint = event.get_constraint();
			double value = constraint.getRightSide();
			if (_debug) System.out.println("CPLEX RECEIVED CONSTRAINT CHANGED " + constraint.name + " / RHS = " + value);
			try {
				_mapLPconstToCPLEXconst.get(constraint).setBounds(value, value);
			} catch (IloException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void updateObjectiveFunction(ObjectiveFunctionChangedEvent event) {
		if (_cplex != null && event != null) {
			if (_isQPproblem) {
				try {
					if (_debug) System.out.println("CPLEX RECEIVED UPDATE OBJECTIVE FUNCTION QP");
					updateObjectiveFunctionQP();
				} catch (IloException e) {
					e.printStackTrace();
				}
			} else {
				try {
					if (_debug) System.out.println("CPLEX RECEIVED UPDATE OBJECTIVE FUNCTION LP");
					updateObjectiveFunctionLP();
				} catch (IloException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void updateObjectiveSense(ObjectiveSenseChangedEvent event) {
		if (_cplex != null && event != null) {
			boolean objectiveSense = event.isMaximization();
			IloObjectiveSense cplexObjectiveSense = (objectiveSense) ? IloObjectiveSense.Maximize : IloObjectiveSense.Minimize;
			if (_debug) System.out.println("CPLEX RECEIVED OBJECTIVE SENSE CHANGED " + (objectiveSense ? "maximize" : "minimize"));
			try {
				_objectiveFunction.setSense(cplexObjectiveSense);
			} catch (IloException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void addLPVariable(LPVariableAddedEvent event) {
		if (_cplex != null && event != null) {
			LPVariable variable = event.get_variable();
			Integer index = event.get_index();
			if (index != null) {
				if (_debug) System.out.println("CPLEX RECEIVED ADD LP VARIABLE " + variable.toString() + " AT INDEX " + index);
				addLPVariableAt(index, variable);
			} else {
				if (_debug) System.out.println("CPLEX RECEIVED ADD LP VARIABLE " + variable.toString());
				addLPVariable(variable);
			}
		}
	}
	
	@Override
	public void addLPConstraint(LPConstraintAddedEvent event) {
		if (_cplex != null && event != null) {
			LPConstraint constraint = event.get_constraint();
			Integer index = event.get_index();
			if (index != null) {
				if (_debug) System.out.println("CPLEX RECEIVED ADD LP CONSTRAINT " + constraint.toString());
				addLPConstraintAt(index, constraint);
			} else {
				if (_debug) System.out.println("CPLEX RECEIVED ADD LP CONSTRAINT " + constraint.toString());
				addLPConstraint(constraint);
			}
		}
	}
	
	@Override
	public void replaceLPConstraint(LPConstraintReplacedEvent event) {
		if (_cplex != null && event != null) {
			LPConstraint oldConstraint = event.get_oldConstraint();
			LPConstraint newConstraint = event.get_newConstraint();
			if (_debug) System.out.println("CPLEX RECEIVED REPLACE LP CONSTRAINT " + oldConstraint.toString() + " BY " + newConstraint.toString());
			replaceLPConstraint(oldConstraint, newConstraint);
		}
	}
	
	@Override
	public void addLPVariableRange(LPVariableRangeAddedEvent event) {
		if (_cplex != null && event != null) {
			if (_debug) System.out.println("CPLEX RECEIVED ADD LP VARIABLE RANGE [" + event.get_variables() + "]");
			for (LPVariable variable : event.get_variables())
				addLPVariable(variable);
		}
	}
	
	@Override
	public void addLPConstraintRange(LPConstraintRangeAddedEvent event) {
		if (_cplex != null && event != null) {
			if (_debug) System.out.println("CPLEX RECEIVED ADD LP CONSTRAINT RANGE [" + event.get_constraints() + "]");
			for (LPConstraint constraint : event.get_constraints())
				addLPConstraint(constraint);
		}
	}
	
	@Override
	public void removeLPVariable(LPVariableRemovedEvent event) {
		if (_cplex != null && event != null) {
			LPVariable variable = event.get_variable();
			if (_debug) System.out.println("CPLEX RECEIVED OBJECTIVE REMOVE LP VARIABLE " + variable.toString());
			removeLPVariable(variable);
		}
	}
	
	@Override
	public void removeLPConstraint(LPConstraintRemovedEvent event) {
		if (_cplex != null && event != null) {
			LPConstraint constraint = event.get_constraint();
			if (_debug) System.out.println("CPLEX RECEIVED OBJECTIVE REMOVE LP CONSTRAINT " + constraint.toString());
			removeLPConstraint(constraint);
		}
	}
	
	@Override
	public void removeLPVariableRange(LPVariableRangeRemovedEvent event) {
		if (_cplex != null && event != null) {
			if (_debug) System.out.println("CPLEX RECEIVED REMOVE LP VARIABLE RANGE [" + event.get_variables() + "]");
			for (LPVariable variable : event.get_variables())
				removeLPVariable(variable);
		}
	}
	
	@Override
	public void removeLPConstraintRange(LPConstraintRangeRemovedEvent event) {
		if (_cplex != null && event != null) {
			if (_debug) System.out.println("CPLEX RECEIVED REMOVE LP CONSTRAINT RANGE [" + event.get_constraints() + "]");
			for (LPConstraint constraint : event.get_constraints())
				removeLPConstraint(constraint);
		}
	}
	
	public void finalize() throws Throwable {
		
		if (_cplex != null) {
			if(_lpProblem!=null){
				_lpProblem.removeLPProblemListener(this);
				_lpProblem = null;
			}
			if(_qpProblem!=null){
				_qpProblem.removeLPProblemListener(this);
				_qpProblem = null;
			}			
			_cplex.end();
		}
		super.finalize();
	}
	
	public boolean getComputeShadowPrices() {
		return _computeShadowPrices;
	}
	
	public boolean getComputeReducedCosts() {
		return _computeReducedCosts;
	}
	
	public String getSolverOutput() {
		return _solverOutput;
	}
	
	public void resetSolver() {
		try {
			finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		_solverOutput = "";
	}
	
	@Override
	public void setProblem(LPProblem problem) {
		this._lpProblem = (LPProblem) problem;
	}
	
	public void setComputeShadowPrices(boolean computeShadowPrices) {
		this._computeShadowPrices = computeShadowPrices;
	}
	
	public void setComputeReducedCosts(boolean computeReducedCosts) {
		this._computeReducedCosts = computeReducedCosts;
	}
	
	private void setParam(IloCplex cplex) throws SolverParametersException {
		CplexParamConfiguration.setParameters(cplex);
	}
	
	@Override
	public <T extends QPProblem> void setQPProblem(T problem) {
		_qpProblem = problem;
	}
	
	@Override
	public void saveModelToMPS(String file, boolean includeTime) {
		
		if (includeTime) {
			String extension = FileUtils.getFileExtension(file);
			String time = TimeUtils.formatMillis(System.currentTimeMillis());
			file = file.replaceFirst(extension, "") + "_" + time + "." + extension;
		}
		
		try {
			_cplex.exportModel(file);
		} catch (IloException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
}
