package com.myweb.patternanalyzer;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;

import com.myweb.patternanalyzer.pojos.AssociatedPattern;
import com.myweb.patternanalyzer.utils.CSVUtil;
import com.myweb.patternanalyzer.utils.Constants.Options;
import com.myweb.patternanalyzer.utils.PatternAnalyzerUtil;

public class Analyze {

	private final static Logger logger = Logger.getLogger(Analyze.class);
	private String className = this.getClass().getSimpleName();

	// Main Program/class to run the tool
	public static void main(String[] args) throws Exception {

		Analyze analyze = new Analyze();
		// Main method to be executed to perform operation - includes input
		// validation and calls core pattern analyzer.
		analyze.analyze(args);
	}

	/**
	 * This is just a dummy method to validate the input data, initialize all
	 * the inputs required for analysis. Finally this method calls pattern
	 * analyzer.
	 * 
	 * @param args
	 * @return
	 * @throws Exception
	 */
	public boolean analyze(String[] args) throws Exception {
		Long start = System.currentTimeMillis();
		logger.info("Starting Program.");
		boolean isCompleted = false;

		PatternAnalyzer pa = new PatternAnalyzer();
		// validate if the program has enough inputs.
		PatternAnalyzerUtil.validateInput(args, className);

		// Input file name
		String fileName = null;
		// Source fields to be used for building the patterns.
		String[] sourceFields = null;
		// Generated fields have the pattern that matching the values of source
		// fields.
		String[] generatedFields = null;
		// This is required to append at the end of field name when same pattern
		// found multiple time. Default is count. (Option example: -sEmployee
		// Type)
		String suffixField = null;

		// Mandatory Fields
		// set/initialize the mandatory variables using the input command line
		// parameters data
		try {
			fileName = PatternAnalyzerUtil.getValueFromInput(args, Options.PATH.value());
			sourceFields = PatternAnalyzerUtil.getValueFromInput(args, Options.SOURCE_FIELDS.value()).split(",");
			generatedFields = PatternAnalyzerUtil.getValueFromInput(args, Options.GENERATED_FIELDS.value()).split(",");
		} catch (Exception e) {
			logger.error("Error while parsing mandatory input : ", e);
			PatternAnalyzerUtil.printUsage(className);
		}

		// Optional Fields
		// set/initialize the optional variables
		try {
			suffixField = PatternAnalyzerUtil.getValueFromInput(args, Options.SUFFIX.value());
		} catch (Exception e) {
			logger.error("Error while parsing optional input : ", e);
		}

		// contains all the fields from SourceFields and GeneratedFields
		Set<String> setOfFields = new LinkedHashSet<>();
		Set<String> setOfSourceFields = new LinkedHashSet<>();
		Set<String> setOfGeneratedFields = new LinkedHashSet<>();
		// contains the fields which are in file but not in setOfFields (Ex.
		// Employee Type, Dept), used for identifying suffix to append.
		Map<String, Integer> additionalFields = null;

		setOfFields = PatternAnalyzerUtil.buildFieldsSet(sourceFields, setOfFields);
		setOfSourceFields.addAll(setOfFields);
		setOfFields = PatternAnalyzerUtil.buildFieldsSet(generatedFields, setOfFields);
		setOfGeneratedFields = PatternAnalyzerUtil.buildFieldsSet(generatedFields, setOfGeneratedFields);

		// gets the total rows to be analyzed - default = 5, Partial option
		// (-p10 for 10 rows)or Full (-f)options available.
		int totalRowsToAnalyze = PatternAnalyzerUtil.getTotalRows(args, fileName);

		CSVParser parser = CSVUtil.getParser(fileName);
		CSVPrinter printer = null;

		// Validating all input fields and the fields from csv
		PatternAnalyzerUtil.validateFields(setOfFields, parser);
		// building the list of additional fields which will be used for
		// identifying suffix
		additionalFields = PatternAnalyzerUtil.extractAdditionalFields(setOfFields, parser);

		try {
			// main pattern analysis call
			Map<String, AssociatedPattern> results = pa.analyzePattern(parser, totalRowsToAnalyze, setOfFields,
					setOfSourceFields, setOfGeneratedFields, additionalFields, suffixField);

			// initialize the output writer
			printer = CSVUtil.getDefaultWriter();
			// print the output
			pa.printPattern(printer, results);
			isCompleted = true;
			Long end = System.currentTimeMillis();
			logger.debug("Pattern Analysis completed in " + (end - start) + " ms");
			return isCompleted;

		} catch (Exception e) {
			logger.error("Error while analyzing or printing. ", e);
		} finally {
			// cleanup the resource
			CSVUtil.cleanup(parser);
			CSVUtil.cleanup(printer);
		}
		return isCompleted;
	}
}