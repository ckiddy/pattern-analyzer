/**
 * 
 */
package com.myweb.patternanalyzer.exceptions;

import org.apache.log4j.Logger;

/**
 * @author Chandru Created On : 18-Mar-2017
 * 
 */
public class PatternAnalyzerException extends Exception {

	private final static Logger logger = Logger.getLogger(PatternAnalyzerException.class);
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public PatternAnalyzerException(String message) {
		super(message);
		logger.error("Exception : " + message);
	}

}
