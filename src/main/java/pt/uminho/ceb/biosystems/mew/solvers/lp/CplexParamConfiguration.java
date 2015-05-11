package pt.uminho.ceb.biosystems.mew.solvers.lp;

import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.BooleanParam;
import ilog.cplex.IloCplex.DoubleParam;
import ilog.cplex.IloCplex.IntParam;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions.SolverParametersException;

public class CplexParamConfiguration {
	
	static public boolean				_debug			= false;
	static public String				defConf			= "conf/cplex.conf";
	static public Map<String, Double>	doubleParam;
	static public Map<String, Integer>	intParam;
	static public Map<String, Boolean>	booleanParam;
	static protected OutputStream		warningStream	= System.err;
	
	public static OutputStream getWarningStream() {
		return warningStream;
	}
	
	public static void setWarningStream(OutputStream warningStream) {
		CplexParamConfiguration.warningStream = warningStream;
	}
	
	static {
		initParam();
		
		try {
			addParams(defConf);
			
		} catch (SolverParametersException e) {
			e.printStackTrace();
		}
	}
	
	static public void initParam() {
		doubleParam = new HashMap<String, Double>();
		intParam = new HashMap<String, Integer>();
		booleanParam = new HashMap<String, Boolean>();
	}
	
	static public void addParams(String fileParamPath) throws SolverParametersException {
		FileReader file = null;
		try {
			file = new FileReader(fileParamPath);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			//			e1.printStackTrace();
		}
		
		Properties prop = new Properties();
		
		try {
			if (file != null) prop.load(file);
		} catch (IOException e) {
			throw new SolverParametersException(CPLEXSolver.class, "file", fileParamPath, "Properties");
		}
		
		addParams(prop);
	}
	
	static public void setDoubleParam(String param, Double value) {
		doubleParam.put(param, value);
	}
	
	static public void setIntegerParam(String param, Integer value) {
		intParam.put(param, value);
	}
	
	static public void setBooleanParam(String param, Boolean value) {
		booleanParam.put(param, value);
	}
	
	static public void addParam(String param, String value) throws SolverParametersException {
		Object obj = null;
		param = param.trim();
		value = value.trim();
		
		try {
			obj = Integer.parseInt(value);
			setIntegerParam(param, (Integer) obj);
		} catch (Exception e) {
			try {
				obj = Double.parseDouble(value);
				setDoubleParam(param, (Double) obj);
				
			} catch (Exception e2) {
				String valueToComp = value.toUpperCase();
				if (valueToComp.matches("TRUE|FALSE")) {
					obj = Boolean.parseBoolean(value);
					setBooleanParam(param, (Boolean) obj);
				} else {
					throw new SolverParametersException(CPLEXSolver.class, param, value, "Double, Integer, Boolean");
				}
			}
		}
	}
	
	public static void addParams(Properties prop) throws SolverParametersException {
		for (Object varId : prop.keySet()) {
			addParam(varId.toString(), prop.getProperty(varId.toString()));
		}
	}
	
	public static String getDefaultFileConfiguration() {
		return defConf;
	}
	
	public static Map<String, Double> getDoubleParam() {
		return doubleParam;
	}
	
	public static Map<String, Integer> getIntParam() {
		return intParam;
	}
	
	public static Map<String, Boolean> getBooleanParam() {
		return booleanParam;
	}
	
	public static void setParameters(IloCplex cplex) throws SolverParametersException {
		
		try {
			cplex.setDefaults();
		} catch (Exception e) {
			throw new SolverParametersException(CPLEXSolver.class, "DEFAULT", "DEFAULT", "DEFAULT");
		}
		
		setDoubleParameters(cplex);
		setIntParameters(cplex);
		setBooleanParameters(cplex);
		
		cplex.setWarning(warningStream);
	}
	
	private static void setDoubleParameters(IloCplex cplex) throws SolverParametersException {
		
		for (String id : doubleParam.keySet()) {
			Double objValue = doubleParam.get(id);
			String value = null;
			if (_debug) 
				System.out.println("setting CPLEX param ["+id+"]="+objValue);
			try {
				DoubleParam param = (DoubleParam) DoubleParam.class.getDeclaredField(id).get(null);
				cplex.setParam(param, objValue);
			} catch (IllegalArgumentException e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Double");
			} catch (SecurityException e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Double");
			} catch (IllegalAccessException e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Double");
			} catch (NoSuchFieldException e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Double");
			} catch (Exception e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Double");
			}
		}
	}
	
	private static void setIntParameters(IloCplex cplex) throws SolverParametersException {
		for (String id : intParam.keySet()) {
			Integer objValue = intParam.get(id);
			String value = null;
			if(_debug)System.out.println("setting CPLEX param ["+id+"]="+objValue);
			try {
				IntParam param = (IntParam) IntParam.class.getDeclaredField(id).get(null);
				cplex.setParam(param, objValue);
			} catch (IllegalArgumentException e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Integer");
			} catch (SecurityException e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Integer");
			} catch (IllegalAccessException e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Integer");
			} catch (NoSuchFieldException e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Integer");
			} catch (Exception e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Integer");
			}
		}
	}
	
	private static void setBooleanParameters(IloCplex cplex) throws SolverParametersException {
		for (String id : booleanParam.keySet()) {
			Boolean objValue = booleanParam.get(id);			
			String value = null;

			if(_debug) System.out.println("setting CPLEX param ["+id+"]="+objValue);

			try {
				BooleanParam param = (BooleanParam) BooleanParam.class.getDeclaredField(id).get(null);
				try {
					cplex.setParam(param, objValue);
				} catch (Exception e) {
					if (objValue != null) value = objValue.toString();
					throw new SolverParametersException(CPLEXSolver.class, id, value, "Boolean");
				}
			} catch (IllegalArgumentException e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Boolean");
			} catch (SecurityException e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Boolean");
			} catch (IllegalAccessException e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Boolean");
			} catch (NoSuchFieldException e) {
				if (objValue != null) value = objValue.toString();
				throw new SolverParametersException(CPLEXSolver.class, id, value, "Boolean");
			}
		}
	}
	
	public static void main(String... args) throws SolverParametersException, IloException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, SecurityException, NoSuchFieldException,
			SolverParametersException {
		
		//		System.out.println(Boolean.parseBoolean("true"));
		//		System.out.println(Boolean.parseBoolean("True"));
		//		System.out.println(Boolean.parseBoolean("TRUE"));
		//		System.out.println(Boolean.parseBoolean("trUe"));
		//		
		//		System.out.println(CplexParamConfiguration.getDoubleParam());
		//		System.out.println(CplexParamConfiguration.getIntParam());
		//		System.out.println(CplexParamConfiguration.getBooleanParam());
		
		IloCplex cplex = new IloCplex();
		
		CplexParamConfiguration.setParameters(cplex);
	}
	
}
