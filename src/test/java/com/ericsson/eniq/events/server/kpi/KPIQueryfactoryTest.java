/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi;

import static com.ericsson.eniq.events.server.common.ApplicationConstants.*;
import static com.ericsson.eniq.events.server.kpi.KPIConstants.*;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

import com.ericsson.eniq.events.server.common.ApplicationConstants;
import com.ericsson.eniq.events.server.common.EventDataSourceType;
import com.ericsson.eniq.events.server.common.Group;
import com.ericsson.eniq.events.server.common.TechPackList;
import com.ericsson.eniq.events.server.common.TechPackRepresentation;
import com.ericsson.eniq.events.server.kpi.impl.KPIBaseTestClass;
import com.ericsson.eniq.events.server.kpi.sgeh.impl.SGEHAttachSuccessRateKPI;
import com.ericsson.eniq.events.server.templates.utils.TemplateUtils;

@SuppressWarnings("PMD.ExcessiveClassLength")
public class KPIQueryfactoryTest extends KPIBaseTestClass {
    KPIQueryfactory objUnderTest;

    private Map<String, Object> params;

    private TechPackList techPackList;

    private TechPackRepresentation techPackRepresentation;

    private List<String> aggregationViews;

    private final boolean useDataTiering = false;

    protected Mockery mockery = new JUnit4Mockery();

    {
        mockery.setImposteriser(ClassImposteriser.INSTANCE);
    }

    @Before
    public void setUp() {
        setUpSGEH();

        objUnderTest = new KPIQueryfactory();
        objUnderTest.kpiFactory = this.kpiFactory;
        objUnderTest.kpiUtilities = this.kpiUtilities;

        final List<String> errTables = new ArrayList<String>();
        final List<String> sucTables = new ArrayList<String>();

        errTables.add("EVENT_E_LTE_ERR_RAW");
        sucTables.add("EVENT_E_LTE_SUC_RAW");

        params = new HashMap<String, Object>();
        params.put(RAW_LTE_ERR_TABLES, errTables);
        params.put(RAW_LTE_SUC_TABLES, sucTables);
        params.put(START_TIME, "'2011-03-03 14:00:00'");
        params.put(END_TIME, "'2011-03-03 15:00:00'");
        params.put(TIMERANGE_PARAM, "TR_1");
        params.put(TYPE_PARAM, "APN");
        params.put(USE_TAC_EXCLUSION_BOOLEAN, true);

    }

    private void setUpSGEH() {
        techPackList = mockery.mock(TechPackList.class);
        techPackRepresentation = mockery.mock(TechPackRepresentation.class);
        aggregationViews = new ArrayList<String>();
        aggregationViews.add(EVNTSRC_EVENTID);
        mockery.checking(new Expectations() {
            {
                allowing(techPackList).shouldQueryUseAggregationTables();
                will(returnValue(true));
                allowing(techPackList).getTechPack(with(EVENT_E_SGEH_TPNAME));
                will(returnValue(techPackRepresentation));
                allowing(techPackList).getAllSucAggregationViews();
                will(returnValue(aggregationViews));
                allowing(techPackList).getAllErrAggregationViews();
                will(returnValue(aggregationViews));
                allowing(techPackList).getAllRawErrTables();
                will(returnValue(aggregationViews));
            }
        });
    }

    @Test
    public void testGetColumnTypeAPN() throws Exception {

        final List<String> expectedValue = new ArrayList<String>();

        expectedValue.add(APN_PARAM.toUpperCase());

        final List<String> actualValue = objUnderTest.getColumnsForType(TYPE_APN);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetColumnTypeTAC() throws Exception {

        final List<String> expectedValue = new ArrayList<String>();

        expectedValue.add(TAC_PARAM.toUpperCase());

        final List<String> actualValue = objUnderTest.getColumnsForType(TYPE_TAC);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetColumnTypeSGSN() throws Exception {

        final List<String> expectedValue = new ArrayList<String>();

        expectedValue.add(SGSN_SQL_NAME.toUpperCase());

        final List<String> actualValue = objUnderTest.getColumnsForType(TYPE_SGSN);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetColumnTypeBSC() throws Exception {

        final List<String> expectedValue = new ArrayList<String>();

        expectedValue.add(RAT_PARAM.toUpperCase());
        expectedValue.add(VENDOR_PARAM.toUpperCase());
        expectedValue.add(BSC_SQL_NAME.toUpperCase());

        final List<String> actualValue = objUnderTest.getColumnsForType(TYPE_BSC);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetColumnTypeCELL() throws Exception {

        final List<String> expectedValue = new ArrayList<String>();

        expectedValue.add(RAT_PARAM.toUpperCase());
        expectedValue.add(VENDOR_PARAM.toUpperCase());
        expectedValue.add(BSC_SQL_NAME.toUpperCase());
        expectedValue.add(CELL_SQL_NAME.toUpperCase());

        final List<String> actualValue = objUnderTest.getColumnsForType(TYPE_CELL);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetIntervalForNoTR() {
        final String expectedValue = ONE_MINUTE_STR;
        final String actualValue = objUnderTest.getInterval(null);
        assertEquals(expectedValue, actualValue);

    }

    @Test
    public void testGetIntervalForTR1() {
        final String expectedValue = ONE_MINUTE_STR;
        final String actualValue = objUnderTest.getInterval(EventDataSourceType.RAW.getValue());
        assertEquals(expectedValue, actualValue);

    }

    @Test
    public void testGetIntervalForTR2() {
        final String expectedValue = ONE_MINUTE_STR;
        final String actualValue = objUnderTest.getInterval(EventDataSourceType.AGGREGATED_1MIN.getValue());
        assertEquals(expectedValue, actualValue);

    }

    @Test
    public void testGetIntervalForTR3() {
        final String expectedValue = FIFTEEN_MINUTE_STR;
        final String actualValue = objUnderTest.getInterval(EventDataSourceType.AGGREGATED_15MIN.getValue());
        assertEquals(expectedValue, actualValue);

    }

    @Test
    public void testGetIntervalForTR4() {
        final String expectedValue = DAY_MINUTES_STR;
        final String actualValue = objUnderTest.getInterval(EventDataSourceType.AGGREGATED_DAY.getValue());
        assertEquals(expectedValue, actualValue);

    }

    @Test
    public void testGetTableNamesApnTR1() {
        final String tableName = "EVENT_E_LTE";
        final String type = TYPE_APN;
        final String timeRange = "TR_1";
        final String suffix = "_RAW";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesApnTR2() {
        final String tableName = "EVENT_E_LTE";
        final String type = TYPE_TAC;
        final String timeRange = "TR_2";
        final String suffix = "_RAW";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesApnTR3() {
        final String tableName = "EVENT_E_LTE_APN_EVENTID";
        final String type = TYPE_APN;
        final String timeRange = "TR_3";
        final String suffix = "_15MIN";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesApnTR4() {
        final String tableName = "EVENT_E_LTE_APN_EVENTID";
        final String type = TYPE_APN;
        final String timeRange = "TR_4";
        final String suffix = "_DAY";

        testTableNames(tableName, type, timeRange, suffix);
    }

    /**
     * TR1 go to the raw tables
     */
    @Test
    public void testGetTableNamesTacTR1() {
        final String tableName = "EVENT_E_LTE";
        final String type = TYPE_TAC;
        final String timeRange = "TR_1";
        final String suffix = "_RAW";

        testTableNames(tableName, type, timeRange, suffix);
    }

    /**
     * TR2 go to the raw tables
     */
    @Test
    public void testGetTableNamesTacTR2() {
        final String tableName = "EVENT_E_LTE";
        final String type = TYPE_TAC;
        final String timeRange = "TR_2";
        final String suffix = "_RAW";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesTacTR3() {
        final String tableName = "EVENT_E_LTE_MANUF_TAC_EVENTID";
        final String type = TYPE_TAC;
        final String timeRange = "TR_3";
        final String suffix = "_15MIN";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesTacTR4() {
        final String tableName = "EVENT_E_LTE_MANUF_TAC_EVENTID";
        final String type = TYPE_TAC;
        final String timeRange = "TR_4";
        final String suffix = "_DAY";

        testTableNames(tableName, type, timeRange, suffix);
    }

    /**
     * Both TR1 and TR2 go to the raw tables
     */
    @Test
    public void testGetTableNamesManTR1() {
        final String tableName = "EVENT_E_LTE";
        final String type = TYPE_MAN;
        final String timeRange = "TR_1";
        final String suffix = "_RAW";

        testTableNames(tableName, type, timeRange, suffix);
    }

    /**
     * Both TR1 and TR2 go to the raw tables
     */
    @Test
    public void testGetTableNamesManTR2() {
        final String tableName = "EVENT_E_LTE";
        final String type = TYPE_TAC;
        final String timeRange = "TR_2";
        final String suffix = "_RAW";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesManTR3() {
        final String tableName = "EVENT_E_LTE_MANUF_TAC_EVENTID";
        final String type = TYPE_MAN;
        final String timeRange = "TR_3";
        final String suffix = "_15MIN";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesManTR4() {
        final String tableName = "EVENT_E_LTE_MANUF_TAC_EVENTID";
        final String type = TYPE_MAN;
        final String timeRange = "TR_4";
        final String suffix = "_DAY";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesSgsnTR1() {
        final String tableName = "EVENT_E_LTE";
        final String type = TYPE_SGSN;
        final String timeRange = "TR_1";
        final String suffix = "_RAW";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesSgsnTR2() {
        final String tableName = "EVENT_E_LTE_EVNTSRC_EVENTID";
        final String type = TYPE_SGSN;
        final String timeRange = "TR_2";
        final String suffix = "_1MIN";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesSgsnTR3() {
        final String tableName = "EVENT_E_LTE_EVNTSRC_EVENTID";
        final String type = TYPE_SGSN;
        final String timeRange = "TR_3";
        final String suffix = "_15MIN";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesSgsnTR4() {
        final String tableName = "EVENT_E_LTE_EVNTSRC_EVENTID";
        final String type = TYPE_SGSN;
        final String timeRange = "TR_4";
        final String suffix = "_DAY";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesBscTR1() {
        final String tableName = "EVENT_E_LTE";
        final String type = TYPE_BSC;
        final String timeRange = "TR_1";
        final String suffix = "_RAW";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesBscTR2() {
        final String tableName = "EVENT_E_LTE_VEND_HIER3_EVENTID";
        final String type = TYPE_BSC;
        final String timeRange = "TR_2";
        final String suffix = "_1MIN";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesBscTR3() {
        final String tableName = "EVENT_E_LTE_VEND_HIER3_EVENTID";
        final String type = TYPE_BSC;
        final String timeRange = "TR_3";
        final String suffix = "_15MIN";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesBscTR4() {
        final String tableName = "EVENT_E_LTE_VEND_HIER3_EVENTID";
        final String type = TYPE_BSC;
        final String timeRange = "TR_4";
        final String suffix = "_DAY";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesCellTR1andTR2() {
        final String tableName = "EVENT_E_LTE";
        final String type = TYPE_CELL;
        final String timeRange = "TR_1";
        final String suffix = "_RAW";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesCellTR2() {
        final String tableName = "EVENT_E_LTE";
        final String type = TYPE_TAC;
        final String timeRange = "TR_2";
        final String suffix = "_RAW";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesCellTR3() {
        final String tableName = "EVENT_E_LTE_VEND_HIER321_EVENTID";
        final String type = TYPE_CELL;
        final String timeRange = "TR_3";
        final String suffix = "_15MIN";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesCellTR4() {
        final String tableName = "EVENT_E_LTE_VEND_HIER321_EVENTID";
        final String type = TYPE_CELL;
        final String timeRange = "TR_4";
        final String suffix = "_DAY";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesNoTypeTR1() {
        final String tableName = "EVENT_E_LTE";
        final String type = "";
        final String timeRange = "TR_1";
        final String suffix = "_RAW";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesNoTypeTR2() {
        final String tableName = "EVENT_E_LTE";
        final String type = "";
        final String timeRange = "TR_2";
        final String suffix = "_1MIN";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesNoTypeTR3() {
        final String tableName = "EVENT_E_LTE";
        final String type = "";
        final String timeRange = "TR_3";
        final String suffix = "_15MIN";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesNoTypeTR4() {
        final String tableName = "EVENT_E_LTE";
        final String type = "";
        final String timeRange = "TR_4";
        final String suffix = "_DAY";

        testTableNames(tableName, type, timeRange, suffix);
    }

    @Test
    public void testGetTableNamesNullTimeRange() {
        final Map<String, String> expectedValue = new HashMap<String, String>();

        final String tableName = "EVENT_E_LTE";
        expectedValue.put("subview", "dc." + tableName + "_ERR_RAW");
        expectedValue.put("errview", "dc." + tableName + "_ERR_RAW");
        expectedValue.put("sucview", "dc." + tableName + "_SUC_RAW");

        final String type = TYPE_CELL;
        final String timeRange = null;
        final Map<String, String> actualValue = objUnderTest.getTableNames(timeRange, type, true, useDataTiering);
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetCalculation() {

        objUnderTest.parameters = new HashMap<String, Object>();
        objUnderTest.parameters.put(ERROR_TABLE_ALIAS_VARIABLE, "err");
        objUnderTest.parameters.put(SUCCESS_TABLE_ALIAS_VARIABLE, "suc");

        final List<KPI> allKPIs = kpiFactory.getAllKPIs();

        final String actual = objUnderTest.getCalculation(allKPIs, new TechPackList());

        //Make sure that there is no template variable that is not replaced
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(actual);
        assertNull("Un-Substituted variables found => " + unsubstitutedVariable, unsubstitutedVariable);

        //Make sure we have the correct number of KPI's (ie one for each entry in the allKpis list)
        final int numOfKpis = allKPIs.size();
        final StringTokenizer tokenizer = new StringTokenizer(actual, "\n");
        assertEquals("Counting number of KPI calculations", numOfKpis, tokenizer.countTokens());

        //for each calculation make sure that it is syntactically correct. Simply count number of ( and make sure it is
        //equal to ) Same for '. It may not be a great test, but at least if we forget to close a bracket somewhere
        //we can pick it up here

        String calc;
        while (tokenizer.hasMoreTokens()) {
            calc = tokenizer.nextToken();
            final int openBracket = StringUtils.countOccurrencesOf(calc, "(");
            final int closeBracket = StringUtils.countOccurrencesOf(calc, ")");

            //Make sure there are equal numbers of ( and ), ie that all brackets that are open are closed
            assertEquals("comparing " + calc, openBracket, closeBracket);
            final int apostrophe = StringUtils.countOccurrencesOf(calc, "'");

            //Make sure that there are 2 ' per calc string.
            assertTrue("comparing " + calc, apostrophe / 2 == 1);
        }

    }

    /**
     * The point of this simple test is to make sure that we don't have any $ in the generated sql, ie that there
     * are no unreplaced variables
     */
    @Test
    public void kpiQueryFactorySimpleTestSGEHAgg() {
        final List<KPI> kpiList = new ArrayList<KPI>();
        kpiList.add(sgehAttachSuccessRateKPI);
        params.put(TIMERANGE_PARAM, "TR_4");
        final String actual = objUnderTest.getSGEHKPIQuery(params, kpiList, techPackList);

        //Make sure that there are no unsubstituted variables
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(actual);
        System.out.println(actual);
        //We expect a null result. Probably not the best logic
        assertNull("Un-Substituted variables found => " + unsubstitutedVariable, unsubstitutedVariable);
    }

    /**
     * The point of this simple test is to make sure that we don't have any $ in the generated sql, ie that there
     * are no unreplaced variables
     */
    @Test
    public void kpiQueryFactorySimpleTestRaw() {
        params.put(TIMERANGE_PARAM, "TR_1");
        final String actual = objUnderTest.getLteKPIQuery(params, useDataTiering);

        //Make sure that there are no unsubstituted variables
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(actual);
        System.out.println(actual);
        //We expect a null result. Probably not the best logic
        assertNull("Un-Substituted variables found => " + unsubstitutedVariable, unsubstitutedVariable);
    }

    /**
     * The point of this simple test is to make sure that we don't have any $ in the generated sql, ie that there
     * are no unreplaced variables
     */
    @Test
    public void kpiQueryFactorySimpleTest1MinAggregation() {
        params.put(TIMERANGE_PARAM, "TR_2");
        final String actual = objUnderTest.getLteKPIQuery(params, useDataTiering);

        //Make sure that there are no unsubstituted variables
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(actual);
        System.out.println(actual);
        //We expect a null result. Probably not the best logic
        assertNull("Un-Substituted variables found => " + unsubstitutedVariable, unsubstitutedVariable);
    }

    /**
     * The point of this simple test is to make sure that we don't have any $ in the generated sql, ie that there
     * are no unreplaced variables
     */
    @Test
    public void kpiQueryFactorySimpleTest15MinAggregation() {
        params.put(TIMERANGE_PARAM, "TR_3");
        final String actual = objUnderTest.getLteKPIQuery(params, useDataTiering);

        //Make sure that there are no unsubstituted variables
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(actual);
        System.out.println(actual);
        //We expect a null result. Probably not the best logic
        assertNull("Un-Substituted variables found => " + unsubstitutedVariable, unsubstitutedVariable);
    }

    /**
     * The point of this simple test is to make sure that we don't have any $ in the generated sql, ie that there
     * are no unreplaced variables
     */
    @Test
    public void kpiQueryFactorySimpleTest15MinAggregationMAN() {
        params.put(TIMERANGE_PARAM, "TR_3");
        params.put(TYPE_PARAM, TYPE_MAN);
        final String actual = objUnderTest.getLteKPIQuery(params, useDataTiering);

        //Make sure that there are no unsubstituted variables
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(actual);
        System.out.println(actual);
        //We expect a null result. Probably not the best logic
        assertNull("Un-Substituted variables found => " + unsubstitutedVariable, unsubstitutedVariable);
    }

    /**
     * The point of this simple test is to make sure that we don't have any $ in the generated sql, ie that there
     * are no unreplaced variables
     */
    @Test
    public void kpiQueryFactorySimpleTestRawMAN() {
        params.put(TIMERANGE_PARAM, "TR_1");
        params.put(TYPE_PARAM, TYPE_MAN);
        final String actual = objUnderTest.getLteKPIQuery(params, useDataTiering);

        //Make sure that there are no unsubstituted variables
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(actual);
        System.out.println(actual);
        //We expect a null result. Probably not the best logic
        assertNull("Un-Substituted variables found => " + unsubstitutedVariable, unsubstitutedVariable);
    }

    /**
     * The point of this simple test is to make sure that we don't have any $ in the generated sql, ie that there
     * are no unreplaced variables
     */
    @Test
    public void kpiQueryFactorySimpleTestGroupsRaw() {
        final Map<String, Group> templateGroupDefs = getMockGroupDefs();
        params.put(GROUP_DEFINITIONS, templateGroupDefs);
        params.put(TIMERANGE_PARAM, "TR_1");
        params.put(GROUP_NAME_PARAM, "APN");
        params.put(GROUP_NAME_COLUMN_VARIABLE, ApplicationConstants.GROUP_TYPE_APN);
        params.put("groupTable", ApplicationConstants.GROUP_TABLE);
        final String actual = objUnderTest.getLteKPIQuery(params, useDataTiering);

        //Make sure that there are no unsubstituted variables
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(actual);
        System.out.println(actual);
        //We expect a null result. Probably not the best logic
        assertNull("Un-Substituted variables found => " + unsubstitutedVariable, unsubstitutedVariable);
    }

    private Group createMockedGroup(final String groupType, final String groupNameColumn, final String tableName,
            final String... groupKeysArray) {
        final List<String> groupKeysList = Arrays.asList(groupKeysArray);
        return new Group(groupType, tableName, groupNameColumn, groupKeysList);
    }

    private Map<String, Group> getMockGroupDefs() {
        final Map<String, Group> templateGroupDefs = new HashMap<String, Group>();

        templateGroupDefs.put("TAC", createMockedGroup("TAC", "GROUP_NAME", "GROUP_TYPE_E_TAC", "TAC"));
        templateGroupDefs.put("APN", createMockedGroup("APN", "GROUP_NAME", "GROUP_TYPE_E_APN", "APN"));
        return templateGroupDefs;
    }

    /**
     * The point of this simple test is to make sure that we don't have any $ in the generated sql, ie that there
     * are no unreplaced variables
     */
    @Test
    public void kpiQueryFactorySimpleTestGroups15MinAggregation() {

        final Map<String, Group> templateGroupDefs = getMockGroupDefs();

        params.put(GROUP_DEFINITIONS, templateGroupDefs);
        params.put(TIMERANGE_PARAM, "TR_3");
        params.put(GROUP_NAME_PARAM, "DG_GroupNameTAC_250");
        params.put(GROUP_NAME_COLUMN_VARIABLE, ApplicationConstants.GROUP_TYPE_TAC);
        params.put("groupTable", ApplicationConstants.GROUP_TABLE);
        final String actual = objUnderTest.getLteKPIQuery(params, useDataTiering);

        //Make sure that there are no unsubstituted variables
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(actual);
        System.out.println(actual);
        //We expect a null result. Probably not the best logic
        assertNull("Un-Substituted variables found => " + unsubstitutedVariable, unsubstitutedVariable);
    }

    /**
     * Make sure we have setup all our parameters
     */
    @Test
    public void areParametersCorrect() {
        objUnderTest.populateParameters(params, null, useDataTiering);
        final Map<String, Object> expected = setUpExpected();
        assertEquals(expected, objUnderTest.parameters);

    }

    /**
     * Bit of a misnomer of a name. We can't actually test that the string is correct without running it into a DB
     * and that is an integration test. All we are doing here is making sure that there are no unSubstituted variables
     */
    @Test
    public void isSuccessStringCorrect() {
        objUnderTest.parameters = new HashMap<String, Object>();
        objUnderTest.parameters.put("subview", "dc.EVENT_E_LTE_ERR_RAW");
        objUnderTest.parameters.put("sucview", "dc.EVENT_E_LTE_SUC_RAW");
        objUnderTest.parameters.put("event_source_table_alias", "total_sub");
        objUnderTest.parameters.put("success_table_alias", "suc");
        objUnderTest.parameters.put("errview", "dc.EVENT_E_LTE_ERR_RAW");
        objUnderTest.parameters.put("error_table_alias", "err");
        objUnderTest.parameters.put("sucview", "dc.EVENT_E_LTE_SUC_RAW");
        objUnderTest.parameters.put(TYPE_PARAM, "test");
        objUnderTest.parameters.put(USE_TAC_EXCLUSION_BOOLEAN, true);

        final List<KPI> allKPIs = kpiFactory.getAllKPIs();
        final String successString = objUnderTest.getSuccessString(allKPIs, new TechPackList(), useDataTiering);

        //Make sure that there is no template variable that is not replaced
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(successString);
        if (unsubstitutedVariable == null) {
            assertNull("Un-Substituted variables found => " + unsubstitutedVariable, unsubstitutedVariable);
        } else {
            assertTrue(true);
        }

    }

    /**
     * Bit of a misnomer of a name. We can't actually test that the string is correct without running it into a DB
     * and that is an integration test. All we are doing here is making sure that there are no unSubstituted variables
     */
    @Test
    public void isSuccessStringCorrectMan() {
        objUnderTest.parameters = new HashMap<String, Object>();
        objUnderTest.parameters.put("subview", "dc.EVENT_E_LTE_ERR_RAW");
        objUnderTest.parameters.put("sucview", "dc.EVENT_E_LTE_SUC_RAW");
        objUnderTest.parameters.put("event_source_table_alias", "total_sub");
        objUnderTest.parameters.put("success_table_alias", "suc");
        objUnderTest.parameters.put("errview", "dc.EVENT_E_LTE_ERR_RAW");
        objUnderTest.parameters.put("error_table_alias", "err");
        objUnderTest.parameters.put("sucview", "dc.EVENT_E_LTE_SUC_RAW");
        objUnderTest.parameters.put(TYPE_PARAM, "MANUFACTURER");

        final List<KPI> allKPIs = kpiFactory.getAllKPIs();
        final String successString = objUnderTest.getSuccessString(allKPIs, new TechPackList(), useDataTiering);

        //Make sure that there is no template variable that is not replaced
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(successString);
        if (unsubstitutedVariable == null) {
            assertNull("Un-Substituted variables found => " + unsubstitutedVariable, unsubstitutedVariable);
        } else {
            assertTrue(true);
        }

    }

    /**
     * Bit of a misnomer of a name. We can't actually test that the string is correct without running it into a DB
     * and that is an integration test. All we are doing here is making sure that there are no unSubstituted variables
     */
    @Test
    public void isErrorStringCorrect() {
        objUnderTest.parameters = new HashMap<String, Object>();
        objUnderTest.parameters.put("subview", "dc.EVENT_E_LTE_ERR_RAW");
        objUnderTest.parameters.put("sucview", "dc.EVENT_E_LTE_SUC_RAW");
        objUnderTest.parameters.put("event_source_table_alias", "total_sub");
        objUnderTest.parameters.put("success_table_alias", "suc");
        objUnderTest.parameters.put("errview", "dc.EVENT_E_LTE_ERR_RAW");
        objUnderTest.parameters.put("error_table_alias", "err");
        objUnderTest.parameters.put("sucview", "dc.EVENT_E_LTE_SUC_RAW");
        objUnderTest.parameters.put(TYPE_PARAM, "test");

        final List<KPI> allKPIs = kpiFactory.getAllKPIs();

        final String errorString = objUnderTest.getErrorString(allKPIs, new TechPackList());

        //Make sure that there is no template variable that is not replaced
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(errorString);

        if (unsubstitutedVariable == null) {
            assertNull("Un-Substituted variables found => " + unsubstitutedVariable, unsubstitutedVariable);
        } else {
            assertTrue(true);
        }

    }

    /**
     * Bit of a misnomer of a name. We can't actually test that the string is correct without running it into a DB
     * and that is an integration test. All we are doing here is making sure that there are no unSubstituted variables
     */
    @Test
    public void isSuccessStringAggregationCorrect() {
        objUnderTest.parameters = new HashMap<String, Object>();
        objUnderTest.parameters.put("subview", "dc.EVENT_E_LTE_ERR_RAW");
        objUnderTest.parameters.put("sucview", "dc.EVENT_E_LTE_SUC_RAW");
        objUnderTest.parameters.put("event_source_table_alias", "total_sub");
        objUnderTest.parameters.put("success_table_alias", "suc");
        objUnderTest.parameters.put("errview", "dc.EVENT_E_LTE_ERR_RAW");
        objUnderTest.parameters.put("error_table_alias", "err");
        objUnderTest.parameters.put("sucview", "dc.EVENT_E_LTE_SUC_RAW");
        objUnderTest.parameters.put("starttime", "2013 01-01 12:00");
        objUnderTest.parameters.put("endtime", "2013 01-01 12:30");
        objUnderTest.parameters.put("interval", "1");
        objUnderTest.parameters.put(TYPE_PARAM, "");
        objUnderTest.aggregation = true;

        final List<KPI> allKPIs = kpiFactory.getAllKPIs();
        final String successString = objUnderTest.getSuccessString(allKPIs, new TechPackList(), useDataTiering);

        //Make sure that there is no template variable that is not replaced
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(successString);
        assertNull("Un-Substituted variables found => " + unsubstitutedVariable, unsubstitutedVariable);

    }

    /**
     * Bit of a misnomer of a name. We can't actually test that the string is correct without running it into a DB
     * and that is an integration test. All we are doing here is making sure that there are no unSubstituted variables
     */
    @Test
    public void isErrorStringAggregationCorrect() {
        objUnderTest.parameters = new HashMap<String, Object>();
        objUnderTest.parameters.put("subview", "dc.EVENT_E_LTE_ERR_RAW");
        objUnderTest.parameters.put("sucview", "dc.EVENT_E_LTE_SUC_RAW");
        objUnderTest.parameters.put("event_source_table_alias", "total_sub");
        objUnderTest.parameters.put("success_table_alias", "suc");
        objUnderTest.parameters.put("errview", "dc.EVENT_E_LTE_ERR_RAW");
        objUnderTest.parameters.put("error_table_alias", "err");
        objUnderTest.parameters.put("sucview", "dc.EVENT_E_LTE_SUC_RAW");
        objUnderTest.parameters.put("starttime", "2013 01-01 12:00");
        objUnderTest.parameters.put("endtime", "2013 01-01 12:30");
        objUnderTest.parameters.put("interval", "1");
        objUnderTest.parameters.put(TYPE_PARAM, "");
        objUnderTest.aggregation = true;

        final List<KPI> allKPIs = kpiFactory.getAllKPIs();
        final String errorString = objUnderTest.getErrorString(allKPIs, new TechPackList());
        //Make sure that there is no template variable that is not replaced
        final String unsubstitutedVariable = checkForUnsubstitutedVariables(errorString);
        assertNull("Un-Substituted variables found => " + unsubstitutedVariable + "\n\n\n" + errorString,
                unsubstitutedVariable);

    }

    /**
     * Setup a map of all the parameters that we think we need.
     *
     * @return
     */
    private Map<String, Object> setUpExpected() {
        final Map<String, Object> expected = new HashMap<String, Object>();
        final List<String> columns = new ArrayList<String>();
        final List<String> errTables = new ArrayList<String>();
        final List<String> sucTables = new ArrayList<String>();

        columns.add("APN");
        errTables.add("EVENT_E_LTE_ERR_RAW");
        sucTables.add("EVENT_E_LTE_SUC_RAW");

        expected.put("endtime", "'2011-03-03 15:00:00'");
        expected.put("isAgg", false);
        expected.put("starttime", "'2011-03-03 14:00:00'");
        expected.put("interval", "1");
        expected.put("columns", columns);
        expected.put("event_source_table_alias", "total_sub");
        expected.put("timerange", "TR_1");
        expected.put("type", "APN");
        expected.put("success_table_alias", "suc");
        expected.put("rawLteErrTables", errTables);
        expected.put("errview", "dc.EVENT_E_LTE_ERR_RAW");
        expected.put("error_table_alias", "err");
        expected.put("sucview", "dc.EVENT_E_LTE_SUC_RAW");
        expected.put("subview", "dc.EVENT_E_LTE_ERR_RAW");
        expected.put("rawLteSucTables", sucTables);
        expected.put(USE_TAC_EXCLUSION_BOOLEAN, true);
        expected.put(TECH_PACK_LIST, null);

        return expected;
    }

    /**
     * Method that will search the given string for a $. Basically we are checking to make sure that there are no
     * unsubstituted variables in the generated sql.
     * It will only search for the first instance, but it is better than nothing
     * In English the regex is
     * a $ followed possibly by a { followed by a number of letters up to the next non letter.
     * So in the following string on(#foo.tmp_st = isnull(${success_table_alias}.DATETIME_ID, ${error_table_alias}.DATETIME_ID))
     * it will match ${success_table_alias}
     *
     * @param sqlStatement The string to search for $
     * @return if there is no $ then return null, otherwise return everything from $ up to the next non alpha numeric
     *         character, ie return the variable that wasn't substituted.
     */
    private String checkForUnsubstitutedVariables(final String sqlStatement) {
        String result = null;
        //This pattern is basically everything from $ up to the next non alphanumeric character. IE it is picking the
        //unreplaced variable from the sql. 
        final String regex = "\\$[{]*(\\w*)\\W";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher match = pattern.matcher(sqlStatement);

        //If we find $****** then fail and print out the variable. This is to help tracking down the offender
        if (match.find()) {
            result = match.group();
        }
        return result;
    }

    /**
     * Here for dependancy injection in Spring Framework. Not sure if this is needed.
     *
     * @param templateUtils
     */
    public void setTemplateUtils(final TemplateUtils templateUtils) {
        this.templateUtils = templateUtils;
    }

    public void setSGEHAttachSuccessRateKPI(final SGEHAttachSuccessRateKPI sgehAttachSuccessRateKPI) {
        this.sgehAttachSuccessRateKPI = sgehAttachSuccessRateKPI;
    }

    /**
     * @param tableName
     * @param type
     * @param timeRange
     * @param suffix
     */
    private void testTableNames(final String tableName, final String type, final String timeRange, final String suffix) {
        final Map<String, String> expectedValue = new HashMap<String, String>();
        expectedValue.put("errview", "dc." + tableName + "_ERR" + suffix);
        expectedValue.put("sucview", "dc." + tableName + "_SUC" + suffix);
        expectedValue.put("subview", "dc." + tableName + "_ERR" + suffix);

        final Map<String, String> actualValue = objUnderTest.getTableNames(timeRange, type, true, useDataTiering);
        assertEquals(expectedValue, actualValue);

    }

    /**
     * Tests if the group boolean is true
     */
    @Test
    public void testisGroupTrue() {
        final Class<? extends KPIQueryfactory> factoryClass = objUnderTest.getClass();
        final String methodIsGroup = "isGroup";

        final boolean expected = true;
        boolean actual = false;

        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(GROUP_NAME_PARAM, "test");

        try {
            final Field parametersField = factoryClass.getDeclaredField("parameters");
            parametersField.setAccessible(true);
            parametersField.set(objUnderTest, parameters);

            final Method method = factoryClass.getDeclaredMethod(methodIsGroup, new Class[] {});
            method.setAccessible(true);
            actual = (Boolean) method.invoke(objUnderTest, new Object[] {});

        } catch (final IllegalArgumentException e) {
        } catch (final IllegalAccessException e) {
        } catch (final SecurityException e1) {
            e1.printStackTrace();
        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
        } catch (final InvocationTargetException e) {
            e.printStackTrace();
        } catch (final NoSuchFieldException e) {
            e.printStackTrace();
        }

        assertEquals(expected, actual);
    }

    /**
     * Tests if the group boolean is false
     */
    @Test
    public void testisGroupFalse() {
        final Class<? extends KPIQueryfactory> factoryClass = objUnderTest.getClass();
        objUnderTest.parameters = new HashMap<String, Object>();
        final String methodIsGroup = "isGroup";

        final boolean expected = false;
        boolean actual = true;

        try {

            final Method method = factoryClass.getDeclaredMethod(methodIsGroup, new Class[] {});
            method.setAccessible(true);
            actual = (Boolean) method.invoke(objUnderTest, new Object[] {});

        } catch (final IllegalArgumentException e) {
        } catch (final IllegalAccessException e) {
        } catch (final SecurityException e1) {
            e1.printStackTrace();
        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
        } catch (final InvocationTargetException e) {
            e.printStackTrace();
        }

        assertEquals(expected, actual);
    }
    /**
     * Determines what template will be returned
     */
    //    @Test
    //    public void testgetIMSIAggregationGroup(){
    //    	Class<? extends KPIQueryfactory> factoryClass = objUnderTest.getClass();
    //    	final String methodGetIMSI = "getIMSI";
    //    	
    //    	final Map<String, String> parameters = new HashMap<String, String>();    	
    //    	parameters.put(GROUP_NAME_PARAM, "test");
    //    	
    //    	final String expected = "\n\nfull outer join \n" +
    //    	"    (select\n" +
    //    	"        #foo.tmp_st,\n" +
    //    	"        $groupnameColumn,\n" +        
    //    	"        count(distinct(IMSI))\n" +
    //    	"    from\n" +
    //    	"		$subview temp_sub,\n" +		
    //    	"        #foo,\n" +
    //    	"        $groupTable temp_group\n" +
    //    	"    where\n" +
    //    	"        temp_group.$groupnameColumn = :groupname\n" +
    //    	"        and DATETIME_ID >= :dateFrom and DATETIME_ID < :dateTo\n" +
    //    	"        and             and DATETIME_ID >= tmp_st\n" +
    //    	"        and DATETIME_ID < tmp_et\n" +
    //    	"    group by\n" + 
    //    	"        tmp_st,\n" +
    //    	"        $groupnameColumn\n" +        
    //    	"    )as sub(\n" +
    //    	"        DATETIME_ID,\n" +
    //    	"        $groupnameColumn,\n" +        
    //    	"        NO_OF_TOTAL_ERR_SUBSCRIBERS\n" +
    //    	"    )\n" +
    //    	"    on(\n" +
    //    	"                            isnull(\n" +
    //    	"                    $error_table_alias.DATETIME_ID\n" +
    //    	"                        ,\n" +
    //    	"                                $success_table_alias.DATETIME_ID\n" +
    //    	"                            )= sub.DATETIME_ID\n" +
    //    	"            and                     isnull(\n" +
    //    	"                    $error_table_alias.DATETIME_ID\n" +
    //    	"                        ,\n" +
    //    	"                                $success_table_alias.DATETIME_ID\n" +
    //    	"                            )= sub.DATETIME_ID\n" +
    //    	"        )\n" +
    //    	"\n" +	
    //    	"	full outer join\n" +
    //    	"    (select\n" +
    //    	"        #foo.tmp_st,\n" +
    //    	"        $groupnameColumn,\n" +
    //    	"        count(distinct(IMSI))\n" +
    //    	"     from\n" +
    //    	"        (select\n" +
    //    	"            DATETIME_ID,\n" +        
    //    	"            $groupnameColumn,\n" +
    //    	"            IMSI\n" +
    //    	"        from\n" +
    //    	"            	(\n" +
    //    	"			) as temp_suc\n" +
    //    	",\n" +
    //    	"            $groupTable temp_group\n" +
    //    	"        where\n" +
    //    	"            temp_group.$groupnameColumn = :groupname\n" +
    //    	"			and DATETIME_ID >= :dateFrom and DATETIME_ID < :dateTo\n" +
    //    	"			and                 and PAGING_ATTEMPTS != 0\n" +            
    //    	"        union all\n" +
    //    	"        select\n" +
    //    	"            DATETIME_ID,\n" + 
    //    	"            $groupnameColumn,\n" +
    //    	"            IMSI\n" +
    //    	"        from\n" +
    //    	"           	(\n" +
    //    	"			) as temp_err\n" +
    //    	",\n" +    
    //    	"            $groupTable temp_group\n" +  
    //    	"        where\n" +
    //    	"            temp_group.$groupnameColumn = :groupname\n" +
    //    	"			and DATETIME_ID >= :dateFrom and DATETIME_ID < :dateTo\n" +
    //    	"			and                 and PAGING_ATTEMPTS != 0\n" +
    //    	"        ) as tmp(\n" +
    //    	"            DATETIME_ID,\n" +
    //    	"            $groupnameColumn,\n" +
    //    	"            IMSI\n" +
    //    	"        ),\n" +
    //    	"        #foo\n" +
    //    	"    where\n" +
    //    	"        DATETIME_ID >= tmp_st\n" +
    //    	"        and DATETIME_ID < tmp_et\n" +
    //    	"    group by\n" +
    //    	"        tmp_st,\n" +
    //    	"        $groupnameColumn\n" +
    //    	"                ) as total_sub (\n" +
    //    	"        DATETIME_ID,\n" +
    //    	"        $groupnameColumn,\n" +
    //    	"        NO_OF_TOTAL_SUBSCRIBERS)\n" +
    //    	"    on(\n" +
    //    	"                              isnull(\n" +
    //    	"                    $error_table_alias.DATETIME_ID\n" +
    //    	"                        ,\n" +
    //    	"                                $success_table_alias.DATETIME_ID\n" +
    //    	"                            )= total_sub.DATETIME_ID\n" +
    //    	"    		  and                     isnull(\n" +
    //    	"                    $error_table_alias.DATETIME_ID\n" +
    //    	"                        ,\n" +
    //    	"                                $success_table_alias.DATETIME_ID\n" +
    //    	"                            )= total_sub.DATETIME_ID\n" +
    //    	"            )";
    //    	
    //    	String actual = "";
    //    	try{ 
    //    		final Field parametersField = factoryClass.getDeclaredField("parameters");
    //    		parametersField.setAccessible(true);
    //    		parametersField.set(objUnderTest, parameters); 
    //    		
    //    		final Field aggregationField = factoryClass.getDeclaredField("aggregation");
    //    		aggregationField.setAccessible(true);
    //    		aggregationField.set(objUnderTest, true); 
    //    		
    //    		final Method method = factoryClass.getDeclaredMethod(methodGetIMSI, new Class[] {});
    //			method.setAccessible(true);
    //			actual = (String)method.invoke(objUnderTest, new Object[] {});
    //        	
    //    	} catch (IllegalArgumentException e) {
    //		} catch (IllegalAccessException e) {
    //		} catch (final SecurityException e1) {
    //			e1.printStackTrace();
    //		} catch (NoSuchMethodException e) {
    //			e.printStackTrace();
    //		} catch (InvocationTargetException e) {
    //			e.printStackTrace();
    //		} catch (NoSuchFieldException e) {
    //			e.printStackTrace();
    //		}  	
    //		
    //		assertEquals(expected, actual.toString()); 
    //    }
}
