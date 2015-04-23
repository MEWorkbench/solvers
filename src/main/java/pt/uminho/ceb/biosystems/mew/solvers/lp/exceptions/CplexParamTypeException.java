package pt.uminho.ceb.biosystems.mew.solvers.lp.exceptions;

public class CplexParamTypeException extends Exception {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String param;
	public String value;
	public String kclass;
	
	public CplexParamTypeException(String param, String value, String kclass) {
		this.param = param;
		this.value = value;
		this.kclass = kclass;
	}
	
	public String getMessage(){
		return "Cplex parameter exception, possible reasons:\n" +
				"*Cplex does not recognize the parameter " + "[" + param + "]\n" +
				"*The value ["+value+"] is not castable to class ["+kclass+"]"; 
	}
	
}
