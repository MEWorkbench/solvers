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
package pt.uminho.ceb.biosystems.mew.solvers.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SolutionDataMatrix {
	
	private static final boolean debug = false;
	private Integer colums;
	private Integer rows;
	
	private String[] header;
	private int indexValues;
	
	private int linesInHeader;
	private String regExpData;
	
	private ArrayList<Object> data;
	
	private int initial =0;
	
	public SolutionDataMatrix(Integer dataColuns, Integer dataRows, String regExpData, int numberLinesInHeader, int indexValue, int initialIdx){
		this.colums = dataColuns;
		this.rows = dataRows;
		this.regExpData = regExpData;
		this.linesInHeader = numberLinesInHeader;
		this.indexValues = indexValue;
		this.initial = initialIdx;
		
		header = new String[linesInHeader];
	}
	
	@SuppressWarnings("unchecked")
	public void readerInFile(BufferedReader reader) throws IOException{
		String line = null;
		
		for(int i =0; i < linesInHeader; i++){
			line = reader.readLine();
			header[i] = line;
//			System.out.println(i + "\t"+line);
		}
	
		if(rows>0){
			Pattern p = Pattern.compile(regExpData);
			data = new ArrayList<Object>();
			
			for(int i =0; i < colums; i++){
				if(i == indexValues)
					data.add(new ArrayList<Integer>());
				else
					data.add(new ArrayList<Double>());
					
			}
		
			for(int i =0; i < rows ;i++){
				line = reader.readLine();
				
				Matcher m = p.matcher(line);
				m.matches();
				
				for(int j =0; j < colums; j++){
					if(debug) System.out.print("\t" + m.group(j+1));
					if(j == indexValues){
						if(debug) System.out.print("<= " + j+"=");
						Integer value = Integer.parseInt(m.group(j+1));
						((ArrayList<Integer>) data.get(j)).add(value-initial);
//						System.out.print(value+"\t");
					}else{
						Double value = 0.0;
						try{
							value = Double.parseDouble(m.group(j+1));
						}catch (Exception e) {
							value = 0.0;
						}
						if(value!=null)
							((ArrayList<Double>) data.get(j)).add(value);
						else
							((ArrayList<Double>) data.get(j)).add(0.0);
						if(debug) System.out.print(" [" + value +"]");
					}
					
				}
				if(debug) System.out.println();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Integer> getIndexValues(){
		return ((ArrayList<Integer>) data.get(indexValues));
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Double> getValues(int index){
		return ((ArrayList<Double>) data.get(index));
	}
	
	public String getLineOfHeader(int index){
		return header[index];
	}
	
	public void setInitialIdx(int inicitalIdx) {
		this.initial = inicitalIdx;
	}

	public static void main(String...strings){
		
//		String line = "**     778 L778      4.2763485e-07                      0";
//		String reg = "\\**\\s+(\\d+)\\s+([^\\s]+)\\s+([^\\s]+)\\s+([^\\s]+)";
//		Pattern p = Pattern.compile(reg);
//		Matcher m = p.matcher(line);
//		boolean b = m.matches();
//		
//		
////		System.out.println("###############################");
////		Pattern p1 = Pattern.compile("(< eps|[^\\s]+)?");
////		Matcher m1 = p1.matcher("");
////		
////		if(m1.find())
////			System.out.println("->"+m1.group(1));
////		System.out.println("###############################");
//		
//		int num = m.groupCount();
//		for(int i =0; i <= num; i++)
//			System.out.println(i + " ===="+m.group(i)+"=====");
		
	}
}
