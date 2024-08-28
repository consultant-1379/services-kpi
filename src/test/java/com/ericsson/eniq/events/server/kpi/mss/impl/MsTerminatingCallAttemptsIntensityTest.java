/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi.mss.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ericsson.eniq.events.server.kpi.KPIConstants;
import com.ericsson.eniq.events.server.kpi.impl.KPIBaseTestClass;

public class MsTerminatingCallAttemptsIntensityTest extends KPIBaseTestClass {



	private MsTerminatingCallAttemptsIntensity objUnderTest;

	@Before
	public void onSetUp(){
		objUnderTest = new MsTerminatingCallAttemptsIntensity(this.kpiUtilities);
		objUnderTest.setKpiUtilities(this.kpiUtilities);
	}

	@Test
	public void testgetEventID() {
		final String expected = "1";
		final String actual = objUnderTest.getEventID();

		assertEquals(expected, actual);
	}

	@Test
	public void testgetSuccessColumnsAgg() {
		final Map<String, String> expected = new HashMap<String, String>();
		expected.put(KPIConstants.NO_OF_SUCCESSES, KPIConstants.NO_OF_SUCCESSES);

		final Map<String, String> actual = objUnderTest.getSuccessColumnsAgg();

		assertEquals(expected, actual);
	}

	@Test
	public void testgetSuccessColumnsRaw() {
		final Map<String, String> expected = new HashMap<String, String>();
		expected.put(KPIConstants.NO_OF_SUCCESSES, KPIConstants.NO_OF_SUCCESSES_RAW);

		final Map<String, String> actual = objUnderTest.getSuccessColumnsRaw();

		assertEquals(expected, actual);
	}

	@Test
	public void testgetErrorColumnsAggForBlocked() {
		final Map<String,String>  expected = new HashMap<String, String>();
		expected.put(KPIConstants.NO_OF_BLOCKED_CALLS,KPIConstants.NO_OF_ERRORS);

		final Map<String,String> actual = objUnderTest.getErrorColumnsAggForBlocked();

		assertEquals(expected, actual);
	}

	@Test
	public void testgetErrorColumnsRawForBlocked() {
		final Map<String,String>  expected = new HashMap<String, String>();
		expected.put(KPIConstants.NO_OF_BLOCKED_CALLS,KPIConstants.NO_OF_ERRORS_RAW);

		final Map<String,String> actual = objUnderTest.getErrorColumnsRawForBlocked();

		assertEquals(expected, actual);
	}

	@Test
	public void testgetErrorColumnsAggForDropped() {
		final Map<String,String>  expected = new HashMap<String, String>();
		expected.put(KPIConstants.NO_OF_DROPPED_CALLS,KPIConstants.NO_OF_ERRORS);

		final Map<String,String> actual = objUnderTest.getErrorColumnsAggForDropped();

		assertEquals(expected, actual);
	}

	@Test
	public void testgetErrorColumnsRawForDropped() {
		final Map<String,String>  expected = new HashMap<String, String>();
		expected.put(KPIConstants.NO_OF_DROPPED_CALLS,KPIConstants.NO_OF_ERRORS_RAW);

		final Map<String,String> actual = objUnderTest.getErrorColumnsRawForDropped();

		assertEquals(expected, actual);
	}

	@Test
	public void testgetKPICalculation() {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(KPIConstants.SUCCESS_TABLE_ALIAS_VARIABLE, KPIConstants.SUC_AS_TABLE);
		parameters.put(KPIConstants.DROPPED_TABLE_ALIAS_VARIABLE, KPIConstants.NO_OF_DROPPED_CALLS);
		parameters.put(KPIConstants.BLOCKED_TABLE_ALIAS_VARIABLE, KPIConstants.NO_OF_BLOCKED_CALLS);
		final String actual = objUnderTest.getKPICalculation(parameters);
		assertNotNull(actual);
	}

}
