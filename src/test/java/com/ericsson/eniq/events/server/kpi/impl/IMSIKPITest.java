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

public class IMSIKPITest extends KPIBaseTestClass {

    private IMSIKPI objUnderTest;

    @Before
    public void onSetUp(){
        objUnderTest = new IMSIKPI(this.kpiUtilities);
        objUnderTest.kpiUtilities = this.kpiUtilities;
    }
    
    @Test
    public void testgetEventID() {
        final String expected = "15";
        final String actual = objUnderTest.getEventID();

        assertEquals(expected, actual);
    }

    @Test
    public void testgetSuccessColumnsAgg() {
        final Map<String, String> expected = null;

        final Map<String, String> actual = objUnderTest.getSuccessColumnsAgg();

        assertEquals(expected, actual);
    }

    @Test
    public void testgetSuccessColumnsRaw() {
        final Map<String, String> expected = null;

        final Map<String, String> actual = objUnderTest.getSuccessColumnsRaw();

        assertEquals(expected, actual);
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
        final Map<String, String> actual = objUnderTest.getErrorColumnsAgg();

        assertEquals(expected, actual);
    }

    @Test
    public void testgetKPICalculation() {
        final StringBuffer expected = new StringBuffer();
        expected.append("max(isnull(NO_OF_TOTAL_ERR_SUBSCRIBERS,0)) as 'Impacted Subscribers (LTE)'");

        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(SUCCESS_TABLE_ALIAS_VARIABLE, SUC_AS_TABLE);
        parameters.put(ERROR_TABLE_ALIAS_VARIABLE, ERR_AS_TABLE);

        final String actual = objUnderTest.getKPICalculation(parameters);

        Assert.assertThat(actual, Matchers.equalToIgnoringWhiteSpace(expected.toString()));
    }
}
