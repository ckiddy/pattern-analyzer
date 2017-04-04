/**
 * 
 */
package com.myweb.patternanalyzer.pojos;

import java.util.List;

/**
 * @author Chandru Created On : 18-Mar-2017
 * 
 */
public class AssociatedPattern {
	private String fieldName;
	private int patternMatchLength;
	private String fieldsUsed;
	private String patternMatched;
	private String lengthConstraint;
	private String duplicateHandling;
	private List<BasePattern> patterns;
	private int fieldMatchedCount;
	private boolean isFullyMatched;
	private String type;

	/**
	 * 
	 */
	public AssociatedPattern() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param fieldName
	 * @param patternMatchLength
	 * @param fieldsUsed
	 * @param patternMatched
	 * @param lengthConstraint
	 * @param duplicateHandling
	 * @param patterns
	 * @param fieldMatchedCount
	 * @param isFullyMatched
	 */
	public AssociatedPattern(String fieldName, int patternMatchLength, String fieldsUsed, String patternMatched,
			String lengthConstraint, String duplicateHandling, List<BasePattern> patterns, int fieldMatchedCount,
			boolean isFullyMatched, String type) {
		super();
		this.fieldName = fieldName;
		this.patternMatchLength = patternMatchLength;
		this.fieldsUsed = fieldsUsed;
		this.patternMatched = patternMatched;
		this.lengthConstraint = lengthConstraint;
		this.duplicateHandling = duplicateHandling;
		this.patterns = patterns;
		this.fieldMatchedCount = fieldMatchedCount;
		this.isFullyMatched = isFullyMatched;
		this.type = type;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the patternMatchLength
	 */
	public int getPatternMatchLength() {
		return patternMatchLength;
	}

	/**
	 * @param patternMatchLength
	 *            the patternMatchLength to set
	 */
	public void setPatternMatchLength(int patternMatchLength) {
		this.patternMatchLength = patternMatchLength;
	}

	/**
	 * @return the fieldsUsed
	 */
	public String getFieldsUsed() {
		return fieldsUsed;
	}

	/**
	 * @param fieldsUsed
	 *            the fieldsUsed to set
	 */
	public void setFieldsUsed(String fieldsUsed) {
		this.fieldsUsed = fieldsUsed;
	}

	/**
	 * @return the patternMatched
	 */
	public String getPatternMatched() {
		return patternMatched;
	}

	/**
	 * @param patternMatched
	 *            the patternMatched to set
	 */
	public void setPatternMatched(String patternMatched) {
		this.patternMatched = patternMatched;
	}

	/**
	 * @return the lengthConstraint
	 */
	public String getLengthConstraint() {
		return lengthConstraint;
	}

	/**
	 * @param lengthConstraint
	 *            the lengthConstraint to set
	 */
	public void setLengthConstraint(String lengthConstraint) {
		this.lengthConstraint = lengthConstraint;
	}

	/**
	 * @return the duplicateHandling
	 */
	public String getDuplicateHandling() {
		return duplicateHandling;
	}

	/**
	 * @param duplicateHandling
	 *            the duplicateHandling to set
	 */
	public void setDuplicateHandling(String duplicateHandling) {
		this.duplicateHandling = duplicateHandling;
	}

	/**
	 * @return the patterns
	 */
	public List<BasePattern> getPatterns() {
		return patterns;
	}

	/**
	 * @param patterns
	 *            the patterns to set
	 */
	public void setPatterns(List<BasePattern> patterns) {
		this.patterns = patterns;
	}

	/**
	 * @return the fieldMatchedCount
	 */
	public int getFieldMatchedCount() {
		return fieldMatchedCount;
	}

	/**
	 * @param fieldMatchedCount
	 *            the fieldMatchedCount to set
	 */
	public void setFieldMatchedCount(int fieldMatchedCount) {
		this.fieldMatchedCount = fieldMatchedCount;
	}

	/**
	 * @return the isFullyMatched
	 */
	public boolean isFullyMatched() {
		return isFullyMatched;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AssociatedPattern [fieldName=" + fieldName + ", patternMatchLength=" + patternMatchLength
				+ ", fieldsUsed=" + fieldsUsed + ", patternMatched=" + patternMatched + ", lengthConstraint="
				+ lengthConstraint + ", duplicateHandling=" + duplicateHandling + ", patterns=" + patterns
				+ ", fieldMatchedCount=" + fieldMatchedCount + ", isFullyMatched=" + isFullyMatched + "]";
	}

	/**
	 * @param isFullyMatched
	 *            the isFullyMatched to set
	 */
	public void setFullyMatched(boolean isFullyMatched) {
		this.isFullyMatched = isFullyMatched;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
