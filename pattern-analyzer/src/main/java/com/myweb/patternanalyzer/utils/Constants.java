/**
 * 
 */
package com.myweb.patternanalyzer.utils;

/**
 * @author Chandru Created On : 18-Mar-2017
 * 
 */
public class Constants {

	public static final int OPTIONAL_ARG_START_LENGTH = 2;
	public static final String UNDERSCORE = "_";
	public static final String SPACE = " ";
	public static final int DEFAULT_TOTAL_ROWS_TO_ANALYZE = 5;
	public static final int DEFAULT_VALUE_LENGTH = 25;
	public static final String FIELDS_SEPERATOR = ", ";
	public static final String PATTERN_SEPERATOR = " followed by ";

	// list of supported options for input
	public static enum Options {
		PATH("-F"), SOURCE_FIELDS("-S"), GENERATED_FIELDS("-G"), PARTIAL("-p"), FULL("-f"), SUFFIX("-s");

		private String value;

		Options(String value) {
			this.value = value;
		}

		public String value() {
			return value;
		}

	}

	// list of field types can be identified during the pattern building
	public static enum FieldTypes {
		MAIN("Main"), SUBSTRING("Substring"), SPECIAL("Special Character"), NUMBER("Number");

		private String value;

		FieldTypes(String value) {
			this.value = value;
		}

		public String value() {
			return value;
		}
	}
}
