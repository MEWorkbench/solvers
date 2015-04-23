package pt.uminho.ceb.biosystems.mew.solvers.lp;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Set;

public class LPMapVariableValues extends LinkedHashMap<Integer,Double> implements Serializable {

	private static final long serialVersionUID = 1L;

	
	public LPMapVariableValues ()
	{
		super();
	}
	
    
    public void addVariableValue(int varIndex, double varValue) 
    {
    	put(varIndex, varValue);
       	//if (mapValues.containsKey(varIndex)) 
    	//  throw AlreadyPresentFluxMeasureException();
    	//else ...
    }
    

    public void setVariableValue(int varIndex, double varValue) 
    {
    	put(varIndex, varValue);
       	//if (mapValues.containsKey(varIndex)) ...
    	//else throw new NonExistentLPVariableException();
    }

    protected Double getVariableValue (int varIndex) {

    	if (containsKey(varIndex))
    		return get(varIndex);
    	else
    		return null;
    }
    
    public Set<Integer> getIndexes ()
    {
    	return keySet();
    }
}
