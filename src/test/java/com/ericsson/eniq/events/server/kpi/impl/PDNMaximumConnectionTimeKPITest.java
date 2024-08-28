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

public class PDNMaximumConnectionTimeKPITest extends KPIBaseTestClass {

    private PDNMaximumConnectionTimeKPI objUnderTest;

    @Before
    public void onSetUp(){
        objUnderTest = new PDNMaximumConnectionTimeKPI();
        objUnderTest.kpiUtilities = this.kpiUtilities;
    }
    @Test
    public void testgetEventID() {
        final String expected = "11";
        final String actual = objUnderTest.getEventID();

        assertEquals(expected, actual);
    }

    @Test
    public void testgetSuccessColumnsAgg() {
        final Map<String, String> successColumns = new HashMap<String, String>();
        successColumns.put(MAX_DURATION, MAX_DURATION);

        final Map<String, String> actual = objUnderTest.getSuccessColumnsAgg();

        assertEquals(successColumns, actual);
    }

    @Test
    public void testgetSuccessColumnsRaw() {
        final Map<String, String> successColumns = new HashMap<String, String>();
        successColumns.put(MAX_DURATION, MAX_DURATION_RAW);

        final Map<String, String> actual = objUnderTest.getSuccessColumnsRaw();

        assertEquals(successColumns, actual);
    }

    @Test
    public void testgetErrorColumnsAgg() {
    	final Map<String, String> expected = null;
        final Map<String, String> actual = objUnderTest.getErrorColumnsAgg();

        assertEquals(expected, actual);
    }

    @Test
    public void testgetErrorColumnsRaw() {
        final Map<String, String> expected = null;
        final Map<String, String> actual = objUnderTest.getErrorColumnsRaw();

        assertEquals(expected, actual);
    }

    @Test
    public void testgetKPICalculation() {
        final String expected = "max(isnull((case when isnull(suc.EVENT_ID, err.EVENT_ID)=11 then isnull(MAX_DURATION, 0) end), 0)) as 'PDN Maximum Connection Time'";
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(SUCCESS_TABLE_ALIAS_VARIABLE, SUC_AS_TABLE);
        parameters.put(ERROR_TABLE_ALIAS_VARIABLE, ERR_AS_TABLE);
        final String actual = objUnderTest.getKPICalculation(parameters);

        Assert.assertThat(actual, Matchers.equalToIgnoringWhiteSpace(expected));
    }
}
