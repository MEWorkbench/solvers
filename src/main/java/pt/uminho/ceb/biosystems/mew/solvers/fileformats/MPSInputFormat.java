package pt.uminho.ceb.biosystems.mew.solvers.fileformats;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPConstraint;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPConstraintType;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPObjectiveFunction;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.lp.LPVariable;


public class MPSInputFormat implements LPInputFileFormat
{

	protected LPProblem problem;

	public MPSInputFormat(LPProblem problem)
	{
		this.problem = problem;
	}

	@Override
	public LPFormatTypes getFormat ()
	{
		return LPFormatTypes.FixedMPS;
	}
	
	@Override
	public void writeLPFile(String filename) throws IOException 
	{
		generateMPSFile(filename);
	}

	public void generateMPSFile(String fileName) throws IOException 
	{
		
		FileWriter fileDescriptor = new FileWriter(fileName);
		
		BufferedWriter fileWriter = new BufferedWriter(fileDescriptor);
		String objectiveFunctionName = "COST";

		writeProblemName(fileWriter, "LP");
		fileWriter.write("ROWS\n");
		writeObjectiveFunctionName(fileWriter, objectiveFunctionName);

		writeRows(fileWriter);

		writeColumns(fileWriter, objectiveFunctionName);

		writeRHS(fileWriter);

		writeVariableBounds(fileWriter);
		fileWriter.write("ENDATA\n");

		fileWriter.flush();
		fileWriter.close();
		fileDescriptor.close();
	}

	protected void writeProblemName(BufferedWriter fileWritter, String problemName) 
	throws IOException 
	{
		fileWritter.write("NAME          " + problemName + "\n");
	}

	protected void writeObjectiveFunctionName(BufferedWriter fileWriter,
			String objectiveFunction) throws IOException {

		fileWriter.write(" N  " + objectiveFunction + "\n");
	}

	protected void writeRows(BufferedWriter fileWritter) throws IOException {

		List<LPConstraint> constraints = this.problem.getConstraints();
		for (int i = 0; i < constraints.size(); i++)
		{			
			LPConstraint c = constraints.get(i);
			if (c.getType()==LPConstraintType.EQUALITY)
			{
				fileWritter.write(" E  L" + i + "\n");
			}
			else if (c.getType() == LPConstraintType.GREATER_THAN)
			{
				fileWritter.write(" G  L" + i + "\n");
			}
			else if (c.getType()==LPConstraintType.LESS_THAN)
			{
				fileWritter.write(" L  L" + i + "\n");
			}
		}
	}

	protected void writeColumns(BufferedWriter fileWriter, String objectiveFunctionName) throws IOException {

		fileWriter.write("COLUMNS\n");

		LPObjectiveFunction objectiveFunction = this.problem.getObjectiveFunction();
		List<LPConstraint> constraints = this.problem.getConstraints();
		
		
		Map<Integer, Set<Integer>> varIdxToConstIdx = new TreeMap<Integer, Set<Integer>>();
		
		for(int cidx=0; cidx < constraints.size(); cidx++){
			LPConstraint c = constraints.get(cidx);
			Set<Integer> idxVars = c.getLeftSide().getVarIdxs();
			for(int v : idxVars){
				Set<Integer> cIdxs = varIdxToConstIdx.get(v);
				if(cIdxs==null){
					cIdxs = new TreeSet<Integer>();
					varIdxToConstIdx.put(v, cIdxs);
				}
				cIdxs.add(cidx);
			}
		}
		
		Set<Integer> objVarIdx = objectiveFunction.getRow().getVarIdxs();
		
		for(int v : varIdxToConstIdx.keySet()){
			Set<Integer> cIdxs = varIdxToConstIdx.get(v);
			
			if(objVarIdx.contains(v)){
				double value = objectiveFunction.getRow().getTermCoefficient(v);
				writeObjectiveFunctionInfo(fileWriter, problem.getObjectiveFunction().isMaximization(),
						 v, value, objectiveFunctionName);
			}
			
			for(int c : cIdxs){
				LPConstraint cData = constraints.get(c);
				double value = cData.getLeftSide().getTermCoefficient(v);
				writeColumnData(fileWriter, value, v, c);	
			}
			
			
		}

	}

	protected void writeObjectiveFunctionInfo(BufferedWriter fileWriter, boolean isMaximization, 
			int variablePosition, Double variableValue, String objectiveFunction)	
			throws IOException {

		if(variableValue.floatValue()!=0){
			String x = "X" + variablePosition;
	//		if (currentNumberOfRows % 2 == 0)
			fileWriter.write("    " + x);
			
			for(int i=0; i < 10-x.length(); i++)
				fileWriter.write(" ");
			
			if (isMaximization)
			{
				fileWriter.write("" + objectiveFunction);
				for(int i=0; i < 10-objectiveFunction.length(); i++)
					fileWriter.write(" ");
				fileWriter.write(""+ (-1*variableValue) );
			}
			else
			{
				fileWriter.write("" + objectiveFunction);
				for(int i=0; i < 10-objectiveFunction.length(); i++)
					fileWriter.write(" ");
				fileWriter.write("" + variableValue);
			}
			fileWriter.write("\n");
		}
		//writeInsertLineFeedColumn(fileWriter, currentNumberOfRows);
	}

//	protected void writeInsertLineFeedColumn(BufferedWriter fileWriter,	int currentNumberOfRows) throws IOException {
//		if (currentNumberOfRows % 2 != 0) fileWriter.write("\n");
//	}


	protected void writeColumnData(BufferedWriter fileWriter, double value, int currentVariable, 
			int currentConstraint) throws IOException {

		String x = "X" + currentVariable;
		fileWriter.write("    " + x);
		
		for(int i=0; i < 10-x.length(); i++)
			fileWriter.write(" ");
		
//		if (currentNumberOfLineElements % 2 != 0)
//			fileWriter.write(" X" + currentVariable + " ");

		String cc = "" + currentConstraint;
		fileWriter.write("L" + cc);
		for(int i=0; i < 10-cc.length(); i++)
			fileWriter.write(" ");
		fileWriter.write("" + value);

//		if (currentNumberOfLineElements % 2 == 0)
			fileWriter.write("\n");
//		else
//			fileWriter.write(" ");
	}

	protected void writeRHS(BufferedWriter fileWriter) throws IOException 
	{

		List<LPConstraint> constraints = problem.getConstraints();

		fileWriter.write("RHS\n");

		for(int i=0;i<constraints.size();i++)
		{
			double rhs = constraints.get(i).getRightSide();
			if (rhs != 0)
			{
				fileWriter.write("    RHS1"+" ");
				fileWriter.write("       ");
				String l = "L"+i;
				
				fileWriter.write(l);
				for(int k=0; k < 10-l.length(); k++)
					fileWriter.write(" ");
				fileWriter.write(""+rhs);	
				fileWriter.write("\n");
			}
		}
	}

	protected void writeVariableBounds(BufferedWriter fileWriter) throws IOException {

		fileWriter.write("RANGES\nBOUNDS\n");
		int numberOfVariables = problem.getVariables().size();

		for (int i = 0; i < numberOfVariables; i++) {
			LPVariable var = problem.getVariable(i);
			//String variableId = var.getVariableName();
			// Usar nome da variavel ??
			if(var.isInteger())
				writeIntBoundConstraint(fileWriter, "X" + i, var.isBinary(),
					(int)var.getLowerBound(), (int)var.getUpperBound());
			else
			writeBoundConstraint(fileWriter, "X" + i, var.getLowerBound(), var.getUpperBound());
		}

	}

	protected void writeBoundConstraint(BufferedWriter fileWriter,String variableName, 
			double lowerLimit, double upperLimit) throws IOException {

		if (lowerLimit == upperLimit) {
			fileWriter.write(" FX BND1      ");
			fileWriter.write(variableName);
			for(int k=0; k < 10-variableName.length(); k++)
				fileWriter.write(" ");
			fileWriter.write(upperLimit +"\n");
		}
		else {
//			if(lowerLimit!=0){
				fileWriter.write(" LO BND1      ");
				fileWriter.write(variableName);
				for(int k=0; k < 10-variableName.length(); k++)
					fileWriter.write(" ");
				fileWriter.write(lowerLimit + "\n");
//			}
			
//			if(upperLimit!=0){
				fileWriter.write(" UP BND1      ");
				fileWriter.write(variableName);
				for(int k=0; k < 10-variableName.length(); k++)
					fileWriter.write(" ");
				fileWriter.write(upperLimit + "\n");
//			}
		}
	}

	protected void writeIntBoundConstraint(BufferedWriter fileWriter,String variableName, 
			boolean isBinary, int lowerLimit, int upperLimit) throws IOException
	{
		if (isBinary) {
			fileWriter.write(" BV BND1       ");
			fileWriter.write(variableName);
			fileWriter.write("\n"); 	
		}
		else {
			fileWriter.write(" LI BND1      ");
			fileWriter.write(variableName);
			for(int k=0; k < 10-variableName.length(); k++)
				fileWriter.write(" ");
			fileWriter.write(lowerLimit + "\n");
			fileWriter.write(" UI BND1      ");
			fileWriter.write(variableName);
			for(int k=0; k < 10-variableName.length(); k++)
				fileWriter.write(" ");
			fileWriter.write(upperLimit + "\n");
		}
	}

}
