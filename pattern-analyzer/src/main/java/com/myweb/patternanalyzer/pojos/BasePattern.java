/**
 * 
 */
package com.myweb.patternanalyzer.pojos;

import com.myweb.patternanalyzer.utils.Constants.FieldTypes;

/**
 * @author Chandru Created On : 18-Mar-2017
 * 
 */
public class BasePattern {
	private String fieldName;
	private int index;
	private String actualField;
	private FieldTypes type;
	private int typeMatchLength;

	/**
	 * @param fieldName
	 * @param index
	 * @param type
	 * @param typeMatchLength
	 */
	public BasePattern(String fieldName, int index, String actualField, FieldTypes type, int typeMatchLength) {
		super();
		this.fieldName = fieldName;
		this.index = index;
		this.actualField = actualField;
		this.type = type;
		this.typeMatchLength = typeMatchLength;
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
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the actualField
	 */
	public String getActualField() {
		return actualField;
	}

	/**
	 * @param actualField
	 *            the actualField to set
	 */
	public void setActualField(String actualField) {
		this.actualField = actualField;
	}

	/**
	 * @return the type
	 */
	public FieldTypes getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(FieldTypes type) {
		this.type = type;
	}

	/**
	 * @return the typeMatchLength
	 */
	public int getTypeMatchLength() {
		return typeMatchLength;
	}

	/**
	 * @param typeMatchLength
	 *            the typeMatchLength to set
	 */
	public void setTypeMatchLength(int typeMatchLength) {
		this.typeMatchLength = typeMatchLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BasePattern [fieldName=" + fieldName + ", index=" + index + ", actualField=" + actualField + ", type="
				+ type + ", typeMatchLength=" + typeMatchLength + "]";
	}
}
