/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi.impl;

import static com.ericsson.eniq.events.server.kpi.KPIConstants.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UEInitiatedServiceRequestFailureRatioKPITest extends KPIBaseTestClass {

    private UEInitiatedServiceRequestFailureRatioKPI objUnderTest;

    @Before
    public void onSetUp() {
        objUnderTest = new UEInitiatedServiceRequestFailureRatioKPI(this.kpiUtilities);
        objUnderTest.kpiUtilities = this.kpiUtilities;
    }

    @Test
    public void testgetEventID() {
        final String expected = "13";
        final String actual = objUnderTest.getEventID();

        assertEquals(expected, actual);
    }

    @Test
    public void testgetSuccessColumnsAgg() {
        final Map<String, String> successColumns = new HashMap<String, String>();
        successColumns.put(NO_OF_PAGING_ATTEMPTS, NO_OF_PAGING_ATTEMPTS_AGG);
        successColumns.put(NO_OF_SUCCESSES, NO_OF_SUCCESSES_AGG);

        final Map<String, String> actual = objUnderTest.getSuccessColumnsAgg();

        assertEquals(successColumns, actual);
    }

    @Test
    public void testgetSuccessColumnsRaw() {
        final Map<String, String> expected = new HashMap<String, String>();
        expected.put(NO_OF_SUCCESSES, NO_OF_SUCCESSES_RAW);
        expected.put(NO_OF_PAGING_ATTEMPTS, NO_OF_PAGING_ATTEMPTS_RAW);

        final Map<String, String> actual = objUnderTest.getSuccessColumnsRaw();

        assertEquals(expected, actual);
    }

    @Test
    public void testgetErrorColumnsAgg() {
        final Map<String, String> expected = new HashMap<String, String>();
        expected.put(NO_OF_ERRORS, NO_OF_ERRORS_AGG);
        expected.put(NO_OF_PAGING_ATTEMPTS, NO_OF_PAGING_ATTEMPTS_AGG);
        final Map<String, String> actual = objUnderTest.getErrorColumnsAgg();

        assertEquals(expected, actual);
    }

    @Test
    public void testgetErrorColumnsRaw() {
        final Map<String, String> errorColumns = new HashMap<String, String>();
        errorColumns.put(NO_OF_PAGING_ATTEMPTS, NO_OF_PAGING_ATTEMPTS_RAW);
        errorColumns.put(NO_OF_ERRORS, NO_OF_ERRORS_RAW);
        final Map<String, String> actual = objUnderTest.getErrorColumnsRaw();

        assertEquals(errorColumns, actual);
    }

    @Test
    public void testgetKPICalculation() {
        final String expected = "max(isnull((case when isnull(err.EVENT_ID, suc.EVENT_ID)=13 and suc.NO_OF_PAGING_ATTEMPTS = 0 and err.NO_OF_PAGING_ATTEMPTS = 0 then (cast (round((isnull((isnull(NO_OF_ERRORS,0) )/cast(isnull(NO_OF_ERRORS,0) + isnull(NO_OF_SUCCESSES,0) as DECIMAL(16)),0))*100, 2) as numeric(5,2))) end),0)) as 'UE Initiated Service Request Failure Ratio'";
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(SUCCESS_TABLE_ALIAS_VARIABLE, SUC_AS_TABLE);
        parameters.put(ERROR_TABLE_ALIAS_VARIABLE, ERR_AS_TABLE);

        final String actual = objUnderTest.getKPICalculation(parameters);

        Assert.assertThat(actual, Matchers.equalToIgnoringWhiteSpace(expected));
    }
}