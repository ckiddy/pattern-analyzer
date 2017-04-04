/**
 * 
 */
package com.myweb.patternanalyzer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.myweb.patternanalyzer.Analyze;

/**
 * @author Chandru
 * Created On : 19-Mar-2017
 * 
 */
public class AnalyzeTest {
	
	Analyze analyze = new Analyze();
	String[] args = null;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		args = new String[5];
		args[0] = "-F" + analyze.getClass().getClassLoader().getResource("test.csv").getPath();
		args[1] = "-S" + "First Name,Last Name,Email";
		args[2] = "-G" + "Email,EID,UID1,UID2,UID3";
		args[3] = "-s" + "Employee Type";
		args[4] = "-f";
	}

	/**
	 * Test method for {@link com.myweb.patternanalyzer.Analyze#analyze(java.lang.String[])}.
	 */
	@Test
	public void testAnalyze() {
		try {
			assertTrue(analyze.analyze(args));
		} catch (Exception e) {
			fail("Failed with exception. - " + e.getMessage());
		}
	}

}
