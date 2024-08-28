/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi;

import static com.ericsson.eniq.events.server.common.ApplicationConstants.*;
import static com.ericsson.eniq.events.server.kpi.KPIConstants.*;
import static org.junit.Assert.*;

import java.util.*;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import com.ericsson.eniq.events.server.kpi.impl.KPIBaseTestClass;
import com.ericsson.eniq.events.server.templates.exception.ResourceInitializationException;

public class KpiUtilitiesTest extends KPIBaseTestClass {

    static String templateFile = "network/KPI_Rate_Template.vm";;

    /**
     * Simplistic test to make sure that the list of strings is converted to a comma seperated string
     */
    @Test
    public void testListToStringManyItems() {
        final List<String> list = new ArrayList<String>();
        list.add("one");
        list.add("two");
        list.add("three");
        final String delim = ",";
        final String expected = "one,two,three";
        assertEquals("Strings are not equal", expected, kpiUtilities.listToString(list, delim));
    }

    /**
     * test to make sure that in the case of only one string we don't add a , at the end
     */
    @Test
    public void testListToStringOneItem() {
        final List<String> list = new ArrayList<String>();
        list.add("one");
        final String delim = ",";
        final String expected = "one";
        assertEquals("Strings are not equal", expected, kpiUtilities.listToString(list, delim));
    }

    /**
     * Make sure an empty list returns and empty string
     */
    @Test
    public void testListToStringNoItems() {
        final List<String> list = new ArrayList<String>();
        final String delim = ",";
        final String expected = "";
        assertEquals("Strings are not equal", expected, kpiUtilities.listToString(list, delim));
    }

    /**
     * For each event name, make sure we get the correct event id back
     */
    @Test
    public void testEventIDs() {
        final Map<String, String> expectedValues = setupExpectedValues();

        for (final String eventType : expectedValues.keySet()) {
            assertEquals("working on " + eventType, expectedValues.get(eventType), kpiUtilities.getEventID(eventType));
        }
    }

    /**
     * For each event name, make sure we get the correct event id back for Mss
     */
    @Test
    public void testMssEventIDs() {
        final Map<String, String> expectedValues = setupExpectedValuesForMss();

        for (final String eventType : expectedValues.keySet()) {
            assertEquals("working on " + eventType, expectedValues.get(eventType), kpiUtilities.getMssEventID(eventType));
        }
    }

    /**
     * Make sure that we get an exception if we try to generate sql from a non existing template file.
     */
    @Test(expected = ResourceInitializationException.class)
    public void testGettingTemplateNonExistingTemplate() {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        final String nonExistingTemplateFile = "/this/dir/should/not/exist/template.vm";

        kpiUtilities.getQueryFromTemplate(nonExistingTemplateFile, parameters);
    }

    /**
     * Making sure that if we don't supply any parameters that no parameter replacement is going on. Probably need to to this for every template to
     * make sure something stupid isn't being done in the code
     */
    @Test
    public void testGettingTemplateNoParameters() {
        final Map<String, Object> parameters = new HashMap<String, Object>();

        final String sqlFragment = kpiUtilities.getQueryFromTemplate(templateFile, parameters);
        final String expected = "max(isnull((case when isnull(${error_table_alias}.EVENT_ID, ${success_table_alias}.EVENT_ID)=$event_id_value then (cast (round((isnull((isnull($no_of_successes,0) )/cast(isnull($no_of_errors,0) + isnull($no_of_successes,0) as DECIMAL(16)),0))*100, 2) as numeric(5,2))) end),0)) as '$kpi_name'";

        Assert.assertThat(sqlFragment, Matchers.equalToIgnoringWhiteSpace(expected));
    }

    /**
     * Making sure that when we try to get the sql from template with correct parameters, that we get the expected result. Probably need to do this
     * for every template.
     */
    @Test
    public void testGettingTemplate() {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(ERROR_TABLE_ALIAS_VARIABLE, "err");
        parameters.put(SUCCESS_TABLE_ALIAS_VARIABLE, "suc");
        parameters.put(KPI_NAME_VARIABLE, "kpi_test");
        parameters.put(NO_OF_SUCCESSES_VARIABLE, "NO_OF_SUCCESSES");
        parameters.put(NO_OF_ERRORS_VARIABLE, "NO_OF_ERRORS");

        final String sqlFragment = kpiUtilities.getQueryFromTemplate(templateFile, parameters);

        final String expected = "max(isnull((case when isnull(err.EVENT_ID, suc.EVENT_ID)=$event_id_value then (cast (round((isnull((isnull(NO_OF_SUCCESSES,0) )/cast(isnull(NO_OF_ERRORS,0) + isnull(NO_OF_SUCCESSES,0) as DECIMAL(16)),0))*100, 2) as numeric(5,2))) end),0)) as 'kpi_test'";

        Assert.assertThat(sqlFragment, Matchers.equalToIgnoringWhiteSpace(expected));
    }

    /**
     * Just a method to set up the event ids and event names. Will need to add something similar for event subtype id's
     */
    private Map<String, String> setupExpectedValues() {
        final Map<String, String> expectedValues = new HashMap<String, String>();

        expectedValues.put(ATTACH, "0");
        expectedValues.put(ACTIVATE, "1");
        expectedValues.put(RAU, "2");
        expectedValues.put(ISRAU, "3");
        expectedValues.put(DEACTIVATE, "4");
        expectedValues.put(L_ATTACH, "5");
        expectedValues.put(L_DETACH, "6");
        expectedValues.put(L_HANDOVER, "7");
        expectedValues.put(L_TAU, "8");
        expectedValues.put(L_DEDICATED_BEARER_ACTIVATE, "9");
        expectedValues.put(L_DEDICATED_BEARER_DEACTIVATE, "10");
        expectedValues.put(L_PDN_CONNECT, "11");
        expectedValues.put(L_PDN_DISCONNECT, "12");
        expectedValues.put(L_SERVICE_REQUEST, "13");
        expectedValues.put(DETACH, "14");
        expectedValues.put(SERVICE_REQUEST, "15");
        return expectedValues;
    }

    /**
     * Just a method to set up the event ids and event names.
     */
    private Map<String, String> setupExpectedValuesForMss() {
        final Map<String, String> expectedValues = new HashMap<String, String>();

        expectedValues.put(MS_ORIGINATING, "0");
        expectedValues.put(MS_TERMINATING, "1");
        expectedValues.put(CALL_FORWARDING, "2");
        expectedValues.put(ROAMING_CALL_FORWARDING, "3");
        expectedValues.put(MS_ORIGINATING_SMS_MSC, "4");
        expectedValues.put(MS_TERMINATING_SMS_MSC, "5");
        expectedValues.put(LOCATION_SERVICES, "6");
        return expectedValues;
    }
}
