package pt.uminho.ceb.biosystems.mew.solvers.fileformats;

import pt.uminho.ceb.biosystems.mew.solvers.lp.LPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.lp.MILPProblem;
import pt.uminho.ceb.biosystems.mew.solvers.parser.GeneralOutputSolverFile;

public class OutputFileUtils {

	public static GeneralOutputSolverFile createOutputParserCLP(LPProblem problem){
		GeneralOutputSolverFile ret = new GeneralOutputSolverFile(problem);
		
		ret.setLineObjectiveValue(1);
		ret.setRegExpObjectiveValue("\\s*Objective value\\s*([^ (]+).*$");
		
		return ret;
	}
	
	public static GeneralOutputSolverFile createParserGLPK(LPProblem problem){
		GeneralOutputSolverFile ret = new GeneralOutputSolverFile(problem);
		
		ret.setRegexpOptimalSolutionType("Status:\\s+(OPTIMAL|INTEGER OPTIMAL)\\s*");
		ret.setLineIndexSolutionType(4);
		
		ret.setHeaderRowInfo(10);
		ret.setColumnsInRowInfo(6);
		ret.setInitialIdxRows(2);
		ret.setRegExpDataRowInfo("\\s+(\\d+)\\s+([^\\s]+)\\s+([^\\s]+)?\\s+([^\\s]+)\\s+([^\\s]+)\\s+=?\\s*(< eps|[^\\s]+)?\\s*");
		
		ret.setInitialIdxColumns(1);
		ret.setHeaderColumnInfo(3);
		ret.setColumnsInColumnInfo(7);
		if(MILPProblem.class.isAssignableFrom(problem.getClass())){
			ret.setRegExpDataColumnsInfo("\\s+(\\d+)\\s+([^\\s]+)\\s+([^\\s]+)\\s+([^\\s]+)?\\s+([^\\s]+)?\\s+(< eps|[^\\s]+)?\\s*");
		}else
			ret.setRegExpDataColumnsInfo("\\s+(\\d+)\\s+([^\\s]+)\\s+([^\\s]+)?\\s+([^\\s]+)\\s+([^\\s]+)?\\s+([^\\s]+)?\\s+(< eps|[^\\s]+)?\\s*");
		
		//FIXME Verify this line in other versions of glpk
		ret.setLineObjectiveValue(5);
		ret.setRegExpObjectiveValue("\\s*Objective:\\s*COST\\s*=\\s*([^ (]+).*");
		
		return ret;
	}
}
