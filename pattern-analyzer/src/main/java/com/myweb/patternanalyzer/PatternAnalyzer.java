/**
 * 
 */
package com.myweb.patternanalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import com.myweb.patternanalyzer.pojos.AssociatedPattern;
import com.myweb.patternanalyzer.pojos.BasePattern;
import com.myweb.patternanalyzer.utils.Constants.FieldTypes;
import com.myweb.patternanalyzer.utils.PatternAnalyzerUtil;
import com.myweb.patternanalyzer.utils.PatternBuilder;

/**
 * @author Chandru Created On : 19-Mar-2017
 * 
 */
public class PatternAnalyzer {

	private final static Logger logger = Logger.getLogger(PatternAnalyzer.class);

	/**
	 * Main method to analyze the pattern based on input data
	 * 
	 * @param parser
	 * @param totalRowsToAnalyze
	 * @param setOfFields
	 * @param setOfSourceFields
	 * @param setOfGeneratedFields
	 * @param additionalFields
	 * @param suffixField
	 * @return
	 * @throws Exception
	 */
	public Map<String, AssociatedPattern> analyzePattern(CSVParser parser, int totalRowsToAnalyze,
			Set<String> setOfFields, Set<String> setOfSourceFields, Set<String> setOfGeneratedFields,
			Map<String, Integer> additionalFields, String suffixField) throws Exception {

		int rowCount = 0;
		Map<String, AssociatedPattern> associatedPatterns = new LinkedHashMap<>();

		// performing analysis for every record in the file
		for (CSVRecord record : parser) {
			// when the number of iteration become total rows to be analyzed,
			// quit the execution
			if (totalRowsToAnalyze == rowCount) {
				logger.debug("TotalRowsToAnalyze matched rowCount" + rowCount);
				break;
			}
			rowCount++;
			logger.debug("Current row number : " + rowCount);

			// Building list of pattern for the first time - These will be used
			// for further rows. To avoid building the same pattern again and
			// again we will consider this list as reference patterns, if any
			// field does not satisfy for these patterns, then we will build new
			// one.
			//
			// This is one time activity to have reference patterns. Hence to
			// improve performance.
			if (rowCount == 1) {
				logger.debug("Building patterns for first row");
				associatedPatterns = PatternBuilder.buildPatterns(setOfSourceFields, record, setOfGeneratedFields,
						associatedPatterns);
				// Check the patterns and find if any source and generated or
				// same. For Ex: Email (Source Field/Generated field) and UID2
				// (Generated Field) are same as per the input data.
				checkMatchingPatterns(associatedPatterns, setOfFields, setOfSourceFields);
				logger.debug("Built patterns.");
				logger.debug("---------------");
				logger.debug(associatedPatterns);
			}

			// validating each generated field against the patterns specific to
			// that field
			for (String currentField : setOfGeneratedFields) {
				int fieldMatchedCount = 0;
				String fieldName = currentField.trim();
				if (associatedPatterns.containsKey(fieldName)) {
					AssociatedPattern associatedPattern = associatedPatterns.get(fieldName);
					fieldMatchedCount = associatedPattern.getFieldMatchedCount() + 1;
					associatedPattern.setFieldMatchedCount(fieldMatchedCount);
					List<BasePattern> basePatterns = associatedPattern.getPatterns();
					String mainFieldValue = record.get(fieldName).trim().toLowerCase();
					logger.debug("Current Field value: " + mainFieldValue);
					// validation against the first set of patterns(i.e.
					// reference patterns).
					mainFieldValue = validatePattern(basePatterns, mainFieldValue, fieldName, setOfSourceFields, record,
							associatedPattern.getType());
					while (!mainFieldValue.equals("")) {
						// validation against the second set of patterns, i.e.
						// secondary or duplicate patterns.
						List<BasePattern> basePatterns2 = null;
						for (AssociatedPattern ap : associatedPatterns.values()) {
							if (ap.getFieldName().equals(fieldName) && ap.getType().equals("secondary")) {
								basePatterns2 = ap.getPatterns();
								if (basePatterns2 != null) {
									mainFieldValue = validatePattern(basePatterns2, mainFieldValue, fieldName,
											setOfSourceFields, record, ap.getType());
								}
							}
						}

						if (mainFieldValue.equals("")) {
							break;
						} else {
							AssociatedPattern newAssociatedPattern = new AssociatedPattern(
									associatedPattern.getFieldName(), associatedPattern.getPatternMatchLength(),
									associatedPattern.getFieldsUsed(), associatedPattern.getPatternMatched(),
									associatedPattern.getLengthConstraint(), associatedPattern.getDuplicateHandling(),
									associatedPattern.getPatterns(), fieldMatchedCount,
									associatedPattern.isFullyMatched(), "secondary");

							// build new pattern when it failed to be validated
							// against the primary/reference patterns.
							newAssociatedPattern = PatternBuilder.buildPattern(setOfSourceFields, record, fieldName,
									mainFieldValue, newAssociatedPattern, associatedPatterns);

							if (newAssociatedPattern.getFieldMatchedCount() == fieldMatchedCount) {
								associatedPatterns.put(fieldName + PatternAnalyzerUtil.findSuffix(fieldMatchedCount,
										suffixField, additionalFields, record), newAssociatedPattern);
							} else {
								// newAssociatedPattern.setType("primary");
								associatedPatterns.put(fieldName, newAssociatedPattern);
							}
						}
					}
				}
			}
		}

		return associatedPatterns;

	}

	/**
	 * validate the pattern by comparing values as per the fields
	 * 
	 * @param basePatterns
	 * @param mainFieldValue
	 * @param fieldName
	 * @param setOfSourceFields
	 * @param record
	 * @param apType
	 * @return
	 */
	private String validatePattern(List<BasePattern> basePatterns, String mainFieldValue, String fieldName,
			Set<String> setOfSourceFields, CSVRecord record, String apType) {
		for (BasePattern basePattern : basePatterns) {
			if (!mainFieldValue.equals("")) {
				String currentFieldValue = getFieldValue(basePattern, setOfSourceFields, record, apType);

				currentFieldValue = currentFieldValue == null
						? getFieldValue(basePattern, fieldName, setOfSourceFields, record) : currentFieldValue;
				if (currentFieldValue != null) {
					currentFieldValue = currentFieldValue.toLowerCase();
					if (mainFieldValue.contains(currentFieldValue)) {
						mainFieldValue = mainFieldValue.replace(currentFieldValue, "");
					}
				}
			}
		}
		return mainFieldValue;
	}

	/**
	 * @param associatedPatterns
	 * @param listOfFields
	 */
	private static void checkMatchingPatterns(Map<String, AssociatedPattern> associatedPatterns,
			Set<String> listOfFields, Set<String> listOfFields1) {
		logger.debug("Checking Matching Patterns");
		for (String s : listOfFields) {
			String fieldName = s.trim();
			if (associatedPatterns.containsKey(fieldName)) {
				String patternUsed = associatedPatterns.get(fieldName).getPatternMatched();
				for (Entry<String, AssociatedPattern> e : associatedPatterns.entrySet()) {
					if (!e.getKey().equals(fieldName) && !listOfFields1.contains(fieldName)) {
						if (e.getValue().getPatternMatched().equals(patternUsed)) {
							logger.debug("Match found for fieldName : \"" + e.getKey() + "\" and pattern - \""
									+ patternUsed + "\"");
							associatedPatterns.get(fieldName).setFieldsUsed(e.getValue().getFieldName());
							associatedPatterns.get(fieldName).setPatternMatched(e.getValue().getFieldName());
							associatedPatterns.get(fieldName).setLengthConstraint(e.getValue().getLengthConstraint());
							associatedPatterns.get(fieldName).setDuplicateHandling(e.getValue().getDuplicateHandling());
						}
					}
				}
			}
		}
	}

	/**
	 * Get to the field value compare against the pattern field value - For substring, special or number type of fields.
	 * 
	 * @param pattern
	 * @param setOfSourceFields
	 * @param record
	 * @return
	 */
	private static String getFieldValue(BasePattern pattern, String fieldName, Set<String> setOfSourceFields,
			CSVRecord record) {
		logger.debug("Getting field value for fieldName: " + fieldName + " and type: " + pattern.getType());
		if (pattern.getType().equals(FieldTypes.SUBSTRING)) {
			return record.get(pattern.getActualField()).trim().substring(0, pattern.getTypeMatchLength());
		} else if (pattern.getType().equals(FieldTypes.SPECIAL)) {
			return pattern.getFieldName();
		} else if (pattern.getType().equals(FieldTypes.NUMBER)) {
			String s = record.get(fieldName);
			s = s.substring(s.length() - pattern.getTypeMatchLength()).trim();
			if (s != null && PatternBuilder.checkNumber(s) > 0)
				return s;
		}

		return null;
	}

	/**
	 * Get to the field value compare against the pattern field value - For main fields
	 * 
	 * @param fieldName
	 * @param setOfSourceFields
	 * @param record
	 * @return
	 */
	private static String getFieldValue(BasePattern pattern, Set<String> setOfSourceFields, CSVRecord record,
			String apType) {
		logger.debug("Getting field value for fieldName: " + pattern.getFieldName());
		if (setOfSourceFields.contains(pattern.getFieldName())) {
			String s = record.get(pattern.getFieldName()).trim();

			if (apType.equals("secondary") && s.length() > pattern.getTypeMatchLength()) {
				return s.substring(0, pattern.getTypeMatchLength());
			} else {
				return s;
			}
		}
		return null;
	}

	/**
	 * For printing out put result
	 * 
	 * @param printer
	 * @param results
	 * @throws IOException
	 */
	public void printPattern(CSVPrinter printer, Map<String, AssociatedPattern> results) throws IOException {
		logger.debug("Printing the result");
		logger.debug("-------------------");
		printer.printRecord("Field", "Pattern", "Length Constraint", "Fields Used", "Duplicated Handled");
		for (Entry<String, AssociatedPattern> result : results.entrySet()) {
			List<String> data = new ArrayList<String>();
			data.add(result.getKey());
			AssociatedPattern associatedPattern = result.getValue();
			data.add(associatedPattern.getPatternMatched());
			data.add(associatedPattern.getLengthConstraint());
			data.add(associatedPattern.getFieldsUsed());
			data.add(associatedPattern.getDuplicateHandling());
			printer.printRecord(data);
		}

	}

}
