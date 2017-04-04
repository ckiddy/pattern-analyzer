/**
 * 
 */
package com.myweb.patternanalyzer.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import com.myweb.patternanalyzer.pojos.AssociatedPattern;
import com.myweb.patternanalyzer.pojos.BasePattern;
import com.myweb.patternanalyzer.utils.Constants.FieldTypes;

/**
 * @author Chandru Created On : 18-Mar-2017
 * 
 */
public class PatternBuilder {

	private final static Logger logger = Logger.getLogger(PatternBuilder.class);

	/**
	 * To build reference/primary patterns - This will be called only once for
	 * first row of the file.
	 * 
	 * @param sourceFields
	 * @param record
	 * @param setOfGeneratedFields
	 * @param associatedPatterns
	 * @return
	 */
	public static Map<String, AssociatedPattern> buildPatterns(Set<String> sourceFields, CSVRecord record,
			Set<String> setOfGeneratedFields, Map<String, AssociatedPattern> associatedPatterns) {
		if (associatedPatterns == null) {
			associatedPatterns = new LinkedHashMap<>();
		}

		for (String fname : setOfGeneratedFields) {
			String fieldName = fname.trim();
			AssociatedPattern associatedPattern = new AssociatedPattern();
			associatedPattern.setType("primary");
			buildPattern(sourceFields, record, fieldName, record.get(fieldName).trim(), associatedPattern,
					associatedPatterns);

			associatedPatterns.put(fieldName, associatedPattern);
		}
		return associatedPatterns;
	}

	/**
	 * To build the pattern for the field specified by "fieldName"
	 * 
	 * @param sourceFields
	 * @param record
	 * @param fieldName
	 * @param fieldValue
	 * @param associatedPattern
	 * @param associatedPatterns
	 * @return
	 */
	public static AssociatedPattern buildPattern(Set<String> sourceFields, CSVRecord record, String fieldName,
			String fieldValue, AssociatedPattern associatedPattern, Map<String, AssociatedPattern> associatedPatterns) {
		logger.debug("Building Pattern for " + fieldName);
		if (associatedPattern == null) {
			associatedPattern = new AssociatedPattern();
		}
		List<BasePattern> patterns = new ArrayList<>();
		String tempFieldValue = fieldValue.trim().toLowerCase();
		int patternMatchLength = 0;
		for (String field : sourceFields) {
			String currentFieldName = field.trim();
			if (!currentFieldName.equals(fieldName)) {
				String value = record.get(currentFieldName).trim().toLowerCase();
				int tempLength = value.length();

				// to check if the complete string is present
				if (tempFieldValue.contains(value)) {
					tempFieldValue = tempFieldValue.replace(value, "");
					patterns.add(new BasePattern(currentFieldName, fieldValue.indexOf(value), currentFieldName,
							FieldTypes.MAIN, value.length()));
					patternMatchLength += value.length();
					continue;
				}

				// to check what substring is present in input
				while (tempLength >= 1) {
					String token = value.substring(0, tempLength);
					if (tempFieldValue.startsWith(token)) {

						String fName = "Substring(0," + tempLength + ") of " + currentFieldName;
						tempFieldValue = tempFieldValue.replace(token, "");
						patterns.add(new BasePattern(fName, fieldValue.indexOf(token), currentFieldName,
								FieldTypes.SUBSTRING, tempLength));
						patternMatchLength += token.length();
						break;
					} else if (tempLength > 2 && tempFieldValue.contains(token)) {
						String fName = "Substring(0," + tempLength + ") of " + currentFieldName;
						tempFieldValue = tempFieldValue.replace(token, "");
						patterns.add(new BasePattern(fName, fieldValue.indexOf(token), currentFieldName,
								FieldTypes.SUBSTRING, tempLength));
						patternMatchLength += token.length();
						break;
					}
					tempLength--;
					continue;
				}
			}
		}

		int charCount = 0;
		// to check special cases like special characters, numbers.
		while (!tempFieldValue.equals("")) {
			charCount++;
			// for special characters like "@" or "_" etc.
			String sc = checkSpecialCharacter(tempFieldValue);
			if (sc != null) {
				if (sc.equals("@")) {
					String token = tempFieldValue.substring(tempFieldValue.indexOf("@"));
					tempFieldValue = tempFieldValue.replace(token, "");
					patterns.add(new BasePattern(token, fieldValue.indexOf(token), token, FieldTypes.SPECIAL,
							token.length()));
				} else {
					tempFieldValue = tempFieldValue.replaceFirst(sc, "");
					patterns.add(new BasePattern(sc, fieldValue.indexOf(sc), sc, FieldTypes.SPECIAL, sc.length()));
				}
				continue;
			}
			// for checking numbers
			int count = checkNumber(tempFieldValue);
			if (count > 0) {
				String token = tempFieldValue.substring(0, count);
				String fName = "Number " + count;
				tempFieldValue = tempFieldValue.replace(token, "");
				patterns.add(
						new BasePattern(fName, fieldValue.indexOf(token), "number", FieldTypes.NUMBER, token.length()));
				patternMatchLength += count;
				continue;
			}

			if (charCount == Constants.DEFAULT_VALUE_LENGTH) {
				break;
			}
		}

		// if this is for secondary pattern, add all the existing one to build
		// master list.
		if (associatedPattern.getFieldMatchedCount() > 0 && patterns.size() == 1) {
			patterns.get(0).setIndex(record.get(fieldName).trim().toLowerCase().indexOf(fieldValue));
			patterns.addAll(associatedPattern.getPatterns());
		} else {
			associatedPattern.setFieldName(fieldName);
			associatedPattern.setPatternMatchLength(patternMatchLength);
		}
		// sort the patterns based on the index value to maintain the order in
		// final result.
		sortPatterns(patterns);
		// to build all the input fields used in the pattern
		buildFieldsUsed(associatedPattern, patterns, sourceFields, record, associatedPatterns);
		// to build all the patterns used - final string
		buildPatternUsed(associatedPattern, patterns, sourceFields, record, associatedPatterns);

		return associatedPattern;
	}

	/**
	 * This will build the source fields used in the pattern
	 * 
	 * @param associatedPattern
	 * @param patterns
	 * @param sourceFields
	 * @param record
	 * @param associatedPatterns
	 * @return
	 */
	private static String buildFieldsUsed(AssociatedPattern associatedPattern, List<BasePattern> patterns,
			Set<String> sourceFields, CSVRecord record, Map<String, AssociatedPattern> associatedPatterns) {
		StringBuilder fieldsUsed = new StringBuilder();
		// int len = 0;
		String separator = Constants.FIELDS_SEPERATOR;
		for (BasePattern bp : patterns)
			for (String field : sourceFields) {
				if (bp.getFieldName().contains(field.trim())) {
					fieldsUsed.append(separator).append(field);
					// len += record.get(s1.trim()).length();
				}
			}

		String fields = "";
		if (fieldsUsed.toString() != null && !fieldsUsed.toString().equals("")) {
			fields = fieldsUsed.substring(separator.length());
		}
		associatedPattern.setFieldsUsed(fields);
		return fields;
	}

	/**
	 * This will build the patterns used by appending the specific pattern
	 * seperator (Ex: , or "followed by")
	 * 
	 * @param associatedPatterns
	 * @param pattern
	 * @param inputFields
	 * @return
	 */
	private static String buildPatternUsed(AssociatedPattern associatedPattern, List<BasePattern> patterns,
			Set<String> sourceFields, CSVRecord record, Map<String, AssociatedPattern> associatedPatterns) {
		StringBuilder stringPattern = new StringBuilder();
		String separator = Constants.PATTERN_SEPERATOR;
		int len = 0;
		for (BasePattern bp : patterns) {
			// if(bp.getType().equals(FieldTypes.NUMBER) &&
			// bp.getTypeMatchLength() >=3) {
			// stringPattern.append(separator).append(bp.getFieldName());
			// } else if(!bp.getType().equals(FieldTypes.NUMBER)){
			stringPattern.append(separator).append(bp.getFieldName());
			// }
			for (String field : sourceFields) {
				if (bp.getFieldName().contains(field.trim())) {
					len += record.get(field.trim()).trim().length();
				}
			}
		}

		if (associatedPattern.getPatternMatchLength() == len) {
			associatedPattern.setLengthConstraint("None");
		} else {
			associatedPattern.setLengthConstraint(String.valueOf(associatedPattern.getPatternMatchLength()));
		}

		String patternMatched = "";
		if (stringPattern.toString() != null && !stringPattern.toString().equals("")) {
			patternMatched = stringPattern.substring(separator.length());
		}
		String duplicateHandling = "NA";
		if (associatedPattern.getPatterns() != null) {
			duplicateHandling = "Yes";
			if (!associatedPattern.getDuplicateHandling().equals(duplicateHandling)) {
				if (patterns.size() > associatedPattern.getPatterns().size()
						&& patternMatched.contains(associatedPattern.getPatternMatched())) {
					associatedPattern.setDuplicateHandling(duplicateHandling);
					associatedPattern.setFieldMatchedCount(associatedPattern.getFieldMatchedCount() - 1);
					for (Entry<String, AssociatedPattern> entry : associatedPatterns.entrySet()) {
						if (entry.getKey().contains(associatedPattern.getFieldName())) {
							entry.getValue().setDuplicateHandling(duplicateHandling);
							associatedPatterns.put(entry.getKey(), entry.getValue());
						}
					}
				}
			}
		} else {
			associatedPattern.setDuplicateHandling(duplicateHandling);
		}
		associatedPattern.setPatterns(patterns);
		associatedPattern.setPatternMatched(patternMatched);

		return patternMatched;
	}

	/**
	 * sort the patterns based on index 
	 * 
	 * @param patterns
	 */
	private static void sortPatterns(List<BasePattern> patterns) {
		Collections.sort(patterns, new Comparator<BasePattern>() {
			public int compare(BasePattern o1, BasePattern o2) {
				return o1.getIndex() - o2.getIndex();
			}
		});
	}

	/**
	 * To find the numbers present in string.
	 * 
	 * @param str
	 * @return
	 */
	public static int checkNumber(String str) {
		Pattern p = Pattern.compile("\\d"); // "\d" is for digits in regex
		Matcher m = p.matcher(str);
		int count = 0;
		while (m.find()) {
			count++;
		}
		return count;
	}

	/**
	 * To find the special characters present in string.
	 * 
	 * @param str
	 * @return
	 */
	public static String checkSpecialCharacter(String str) {
		Pattern p = Pattern.compile("[^A-Za-z0-9]");
		Matcher m = p.matcher(str);
		// int count = 0;
		// while(m.find()){ count++; } return count;
		if (m.find())
			return m.group();
		return null;
	}

}
