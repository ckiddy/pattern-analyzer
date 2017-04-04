/**
 * 
 */
package com.myweb.patternanalyzer.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import com.myweb.patternanalyzer.exceptions.PatternAnalyzerException;
import com.myweb.patternanalyzer.utils.Constants.Options;

/**
 * @author Chandru Created On : 18-Mar-2017
 * 
 */
public class PatternAnalyzerUtil {

	private final static Logger logger = Logger.getLogger(PatternAnalyzerUtil.class);
	/**
	 * Validates input data, if the input parameters.
	 * 
	 * @param args
	 * @param className
	 * @throws PatternAnalyzerException 
	 */
	public static void validateInput(String[] args, String className) throws PatternAnalyzerException {
		logger.debug("Validating Input");
		if (args.length < 3) {
			logger.debug("Please provide Input File, Source and Generated field list.");
			printUsage(className);
		}

	}

	/**
	 * To check if some argument is present in the input arguments.
	 * 
	 * @param args
	 * @param string
	 * @return
	 */
	public static int findArgs(String[] args, String string) {
		logger.debug("Finding argument for "+ string);
		for (int i = 0; i < args.length; i++) {
			if (args[i].contains(string)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * To find the suffix to append it to field name when it occurs multiple time.
	 * 
	 * @param count
	 * @param field
	 * @param additionalFields
	 * @param record
	 * @return
	 * @throws PatternAnalyzerException
	 */
	public static String findSuffix(int count, String field, Map<String, Integer> additionalFields,
			CSVRecord record) {
		logger.debug("Finding suffix");
		String suffix = Constants.UNDERSCORE + String.valueOf(count);
		if (field == null) {
			return suffix;
		} else {
			return Constants.SPACE + record.get(additionalFields.get(field));
		}
	}

	/**
	 * To get the total number of rows to be processed/analyzed during execution.
	 * 
	 * @param args
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static int getTotalRows(String[] args, String fileName) throws Exception {
		logger.debug("Get total rows "+ fileName);
		int totalRowsToAnalyze = 0;
		if (findArgs(args, Options.PARTIAL.value()) >= 0) {
			try {
				totalRowsToAnalyze = Integer.valueOf(getValueFromInput(args, Options.PARTIAL.value()));
				int size = CSVUtil.getRowsCount(fileName);
				totalRowsToAnalyze = totalRowsToAnalyze > size ? size : totalRowsToAnalyze;
			} catch (NumberFormatException e) {
				throw new NumberFormatException("Invalid number provided for Partial Analyze option.");
			}
		} else if (findArgs(args, Options.FULL.value()) >= 0) {
			totalRowsToAnalyze = CSVUtil.getRowsCount(fileName);
		} else {
			totalRowsToAnalyze = Constants.DEFAULT_TOTAL_ROWS_TO_ANALYZE;
			int size = CSVUtil.getRowsCount(fileName);
			totalRowsToAnalyze = totalRowsToAnalyze > size ? size : totalRowsToAnalyze;
		}
		return totalRowsToAnalyze;
	}

	public static void printUsage(String className) throws PatternAnalyzerException {
		System.out
				.println("Usage : " + className + " -FcsvFilePath -SsourceFieldsList -GgeneratedFieldsList <optional>");
		System.out.println("-FcsvFilePath : (-F *Mandatory) CSV File to be analyzed for associated Rules/Patterns.");
		System.out.println(
				"-SsourceFieldsList : (-S *Mandatory) List of comma seperated source fields. Ex. \"First Name,Last Name,Email\"");
		System.out.println(
				"-GgeneratedFieldsList : (-G *Mandatory) List of comma seperated generated fields. Ex. \"Email, UID1, UID2\"");
		System.out.println("<optional> : -f = Full Analysis.");
		System.out.println("<optional> : -p10 = Partial Analysis - Analyse first 10 records.");
		System.out.println("<optional> : -sDept = To find the field suffix for duplicate pattern.");
		throw new PatternAnalyzerException("Invalid input");
	}

	/**
	 * To build the set of fields from input comma seperated array
	 * 
	 * @param input
	 * @param setOfFields
	 * @return
	 */
	public static Set<String> buildFieldsSet(String[] input, Set<String> setOfFields) {
		for (String s : input) {
			setOfFields.add(s.trim());
		}
		return setOfFields;
	}

	/**
	 * To validate all the fields against the fields in csv
	 * 
	 * @param setOfFields
	 * @param parser
	 * @throws Exception
	 */
	public static void validateFields(Set<String> setOfFields, CSVParser parser) throws Exception {
		for (String s : setOfFields) {
			boolean isFieldAvaliable = parser.getHeaderMap().containsKey(s.trim());
			if (!isFieldAvaliable) {
				CSVUtil.cleanup(parser);
				throw new Exception("Field not available " + s);
			}
		}
	}

	/**
	 * To extract the additional fields which are in csv but not in source or generated fields
	 * 
	 * @param setOfFields
	 * @param parser
	 * @return
	 */
	public static Map<String, Integer> extractAdditionalFields(Set<String> setOfFields, CSVParser parser) {
		Map<String, Integer> additionalFields = new LinkedHashMap<>();
		for (Entry<String, Integer> e : parser.getHeaderMap().entrySet()) {
			if (!setOfFields.contains(e.getKey().trim())) {
				additionalFields.put(e.getKey(), e.getValue());
			}
		}
		return additionalFields;
	}

	/**
	 * To get the value for specific option
	 * 
	 * @param args
	 * @param className
	 * @return
	 * @throws PatternAnalyzerException
	 */
	public static String getValueFromInput(String[] args, String option) throws PatternAnalyzerException {
		int i = findArgs(args, option);
		if (i == -1) {
			throw new PatternAnalyzerException(option + " Option not found in input.");
		}
		
		String value = args[i].substring(option.length()).trim();
		if(value == null || value.equals("") || value.length()==0) {
			throw new PatternAnalyzerException("Value is null/empty for option "+ option);
		}
		return value;
	}
}
