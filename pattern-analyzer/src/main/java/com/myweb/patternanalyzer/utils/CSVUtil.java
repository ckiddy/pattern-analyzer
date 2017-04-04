/**
 * 
 */
package com.myweb.patternanalyzer.utils;

import java.io.FileReader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;

import com.myweb.patternanalyzer.exceptions.PatternAnalyzerException;

/**
 * @author Chandru Created On : 18-Mar-2017
 * 
 */
public class CSVUtil {

	private final static Logger logger = Logger.getLogger(CSVUtil.class);

	// Create the CSVFormat object
	private static CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');

	public static CSVParser getParser(String fileName) throws PatternAnalyzerException {
		logger.debug("Getting parser for fileName: " +fileName);
		CSVParser parser = null;
		try {
			parser = new CSVParser(new FileReader(fileName), format);
		} catch (Exception e) {
			throw new PatternAnalyzerException("Error while initializing CSV Parser. : " + e.getMessage());
		}
		return parser;

	} 
	
	public static CSVPrinter getDefaultWriter() throws PatternAnalyzerException {
		CSVPrinter printer = null;
		try {
			printer = new CSVPrinter(System.out, format.withDelimiter('#'));
		} catch (Exception e) {
			throw new PatternAnalyzerException("Error while initializing CSV Printer. : " + e.getMessage());

		}
		return printer;
	}

	public static int getRowsCount(String fileName) throws PatternAnalyzerException {
		logger.debug("Getting rowsCount for fileName: " +fileName);
		CSVParser parser = null;
		try {
			parser = getParser(fileName);
			return parser.getRecords().size();
		} catch (Exception e) {
			throw new PatternAnalyzerException("Error while fetching row count. : " + e.getMessage());
		} finally {
			cleanup(parser);
		}

	}

	public static void cleanup(CSVParser parser) throws PatternAnalyzerException {
		logger.debug("Cleaning up parser");
		try {
			if (parser != null)
				parser.close();
		} catch (Exception e) {
			throw new PatternAnalyzerException("Error while closing parser. : " + e.getMessage());
		}
	}

	/**
	 * @param printer
	 * @throws PatternAnalyzerException 
	 */
	public static void cleanup(CSVPrinter printer) throws PatternAnalyzerException {
		logger.debug("Cleaning up printer");
		try {
			if (printer != null)
				printer.close();
		} catch (Exception e) {
			throw new PatternAnalyzerException("Error while closing printer. : " + e.getMessage());
		}	
	}

}
