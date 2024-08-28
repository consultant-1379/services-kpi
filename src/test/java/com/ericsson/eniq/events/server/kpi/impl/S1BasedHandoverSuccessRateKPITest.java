/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi.impl;

import static com.ericsson.eniq.events.server.kpi.KPIConstants.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.*;

/**
 * @author eeikonl
 * @since 2011
 *
 */
public class S1BasedHandoverSuccessRateKPITest extends KPIBaseTestClass {

    private S1BasedHandoverSuccessRateKPI objUnderTest;

    @Before
    public void onSetUp() {
        objUnderTest = new S1BasedHandoverSuccessRateKPI(this.kpiUtilities);
        objUnderTest.kpiUtilities = this.kpiUtilities;
    }

    @Test
    public void testgetEventID() {
        final String expected = "7";
        final String actual = objUnderTest.getEventID();

        assertEquals(expected, actual);
    }

    @Test
    public void testgetSuccessColumnsAgg() {
        final Map<String, String> expected = new HashMap<String, String>();
        expected.put(NO_OF_SUCCESSES, NO_OF_SUCCESSES_AGG);

        final Map<String, String> actual = objUnderTest.getSuccessColumnsAgg();

        assertEquals(expected, actual);
    }

    @Test
    public void testgetSuccessColumnsRaw() {
        final Map<String, String> expected = new HashMap<String, String>();
        expected.put(NO_OF_SUCCESSES, NO_OF_SUCCESSES_RAW);

        final Map<String, String> actual = objUnderTest.getSuccessColumnsRaw();

        assertEquals(expected, actual);
    }

    @Test
    public void testgetErrorColumnsAgg() {
        final Map<String, String> expected = new HashMap<String, String>();
        expected.put(NO_OF_ERRORS, NO_OF_ERRORS_AGG);
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
        final String expected = "max(isnull((case when isnull(err.EVENT_ID, suc.EVENT_ID)=7 and (isnull(suc.EVENT_SUBTYPE_ID, err.EVENT_SUBTYPE_ID)  = 7) then (cast (round((isnull((isnull(NO_OF_SUCCESSES,0) )/cast(isnull(NO_OF_ERRORS,0) + isnull(NO_OF_SUCCESSES,0) as DECIMAL(16)),0))*100, 2) as numeric(5,2))) end),0)) as 'S1 Based Handover Success Rate'";
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(SUCCESS_TABLE_ALIAS_VARIABLE, SUC_AS_TABLE);
        parameters.put(ERROR_TABLE_ALIAS_VARIABLE, ERR_AS_TABLE);

        final String actual = objUnderTest.getKPICalculation(parameters);

        Assert.assertThat(actual, Matchers.equalToIgnoringWhiteSpace(expected));
    }
}
