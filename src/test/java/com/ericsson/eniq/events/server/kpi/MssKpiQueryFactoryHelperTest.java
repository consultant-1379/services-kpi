/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi;

import static com.ericsson.eniq.events.server.common.ApplicationConstants.*;
import static com.ericsson.eniq.events.server.common.TechPackData.*;
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

import org.junit.Before;
import org.junit.Test;

import com.distocraft.dc5000.repository.cache.GroupTypeDef;
import com.distocraft.dc5000.repository.cache.GroupTypeKeyDef;
import com.distocraft.dc5000.repository.dwhrep.Grouptypes;
import com.ericsson.eniq.events.server.common.EventDataSourceType;
import com.ericsson.eniq.events.server.common.GroupHashId;
import com.ericsson.eniq.events.server.common.tablesandviews.TableType;
import com.ericsson.eniq.events.server.common.tablesandviews.TechPack;
import com.ericsson.eniq.events.server.common.tablesandviews.TechPackTables;
import com.ericsson.eniq.events.server.kpi.impl.KPIBaseTestClass;

public class MssKpiQueryFactoryHelperTest extends KPIBaseTestClass {

    MssKpiQueryFactoryHelper testObj;

    private Map<String, Object> params;

    private Map<String, GroupHashId> groups = new HashMap<String, GroupHashId>();

    @Before
    public void setUp() {
        testObj = new MssKpiQueryFactoryHelper();
        testObj.kpiFactory = this.kpiFactory;
        testObj.kpiUtilities = this.kpiUtilities;
        populateGropDef();

        final List<String> sucTables = new ArrayList<String>();
        sucTables.add(EVENT_E_MSS_VOICE_CDR_SUC_RAW);
        params = new HashMap<String, Object>();
        params.put(GROUP_DEFINITIONS, groups);
        params.put(RAW_SUC_TABLES, sucTables);
        params.put(START_TIME, "'2011-03-15 00:00:00'");
        params.put(END_TIME, "'2011-03-16 00:00:00'");
        params.put(TECH_PACK_TABLES, populateTechPackTablesData());
        params.put(IS_EXCULDED_TAC_OR_TACGROUP, false);

    }

    private TechPackTables populateTechPackTablesData() {
        final TechPackTables techPackTables = new TechPackTables(TableType.RAW);
        final TechPack techPack = new TechPack("EVENT_E_MSS", TableType.RAW, "DIM_E_MSS");
        techPackTables.addTechPack(techPack);
        final List<String> sucTablesRaw = new ArrayList<String>();
        sucTablesRaw.add(EVENT_E_MSS_VOICE_CDR_SUC_RAW);
        final List<String> errTablesRawDropped = new ArrayList<String>();
        errTablesRawDropped.add(EVENT_E_MSS_VOICE_CDR_DROP_CALL_RAW);
        final List<String> errTablesRawBlocked = new ArrayList<String>();
        errTablesRawBlocked.add(EVENT_E_MSS_VOICE_CDR_ERR_RAW);

        final List<String> errTablesRawSms = new ArrayList<String>();
        errTablesRawSms.add(EVENT_E_MSS_SMS_CDR_ERR_RAW);
        final List<String> errTablesRawLocService = new ArrayList<String>();
        errTablesRawLocService.add(EVENT_E_MSS_LOC_SERVICE_CDR_ERR_RAW);
        final List<String> sucTablesRawSMS = new ArrayList<String>();
        sucTablesRawSMS.add(EVENT_E_MSS_SMS_CDR_SUC_RAW);
        final List<String> sucTablesRawLocService = new ArrayList<String>();
        sucTablesRawLocService.add(EVENT_E_MSS_LOC_SERVICE_CDR_SUC_RAW);

        techPack.setErrRawTables(KEY_TYPE_ERR, errTablesRawBlocked);
        techPack.setErrRawTables(KEY_TYPE_DROP_CALL, errTablesRawDropped);

        techPack.setErrRawTables(KEY_TYPE_SMS_ERR, errTablesRawSms);
        techPack.setSucRawTables(KEY_TYPE_SMS_SUC, sucTablesRawSMS);

        techPack.setErrRawTables(KEY_TYPE_LOC_SERVICE_ERR, errTablesRawLocService);
        techPack.setSucRawTables(KEY_TYPE_LOC_SERVICE_SUC, sucTablesRawLocService);

        techPack.setSucRawTables(sucTablesRaw);
        return techPackTables;
    }

    private void populateGropDef() {
        final String tpName = "dc_e_abc";
        final String testVersionId = tpName + ":((123))";
        final int isNullable = 0;
        final String tac = "TAC";
        final String imsi = "IMSI";
        final String eventSrc = "EVNTSRC_CS";
        final String controller = "RAT_VEND_HIER3";
        final String cell = "RAT_VEND_HIER321";
        final Grouptypes key1 = getKey(tac, tac, "varchar", 64, 0, testVersionId, isNullable);
        final List<Grouptypes> keys1 = Arrays.asList(key1);
        final GroupTypeDef def1 = new GroupTypeDef(keys1, tac, testVersionId);
        final GroupHashId velocityGroupDefinition1 = getCreateVelocityGroup(def1);
        final Grouptypes key2 = getKey(imsi, imsi, "varchar", 64, 0, testVersionId, isNullable);
        final List<Grouptypes> keys2 = Arrays.asList(key2);
        final GroupTypeDef def2 = new GroupTypeDef(keys2, imsi, testVersionId);
        final GroupHashId velocityGroupDefinition2 = getCreateVelocityGroup(def2);
        final Grouptypes key3 = getKey(eventSrc, "EVNTSRC_ID", "varchar", 64, 0, testVersionId, isNullable);
        final List<Grouptypes> keys3 = Arrays.asList(key3);
        final GroupTypeDef def3 = new GroupTypeDef(keys3, eventSrc, testVersionId);
        final GroupHashId velocityGroupDefinition3 = getCreateVelocityGroup(def3);
        final Grouptypes key4 = getKey(controller, "HIERARCHY_3", "varchar", 64, 0, testVersionId, isNullable);
        final Grouptypes key41 = getKey(controller, "VENDOR", "varchar", 64, 0, testVersionId, isNullable);
        final Grouptypes key42 = getKey(controller, "RAT", "tinyint", 64, 0, testVersionId, isNullable);
        final Grouptypes key43 = getKey(controller, "HIER3_ID", "varchar", 64, 0, testVersionId, isNullable);
        final List<Grouptypes> keys4 = Arrays.asList(key4, key41, key42, key43);
        final GroupTypeDef def4 = new GroupTypeDef(keys4, controller, testVersionId);
        final GroupHashId velocityGroupDefinition4 = getCreateVelocityGroup(def4);
        final Grouptypes key5 = getKey(cell, "HIERARCHY_321", "varchar", 64, 0, testVersionId, isNullable);
        final Grouptypes key51 = getKey(cell, "HIERARCHY_3", "varchar", 64, 0, testVersionId, isNullable);
        final Grouptypes key52 = getKey(cell, "VENDOR", "varchar", 64, 0, testVersionId, isNullable);
        final Grouptypes key53 = getKey(cell, "RAT", "tinyint", 64, 0, testVersionId, isNullable);
        final Grouptypes key54 = getKey(cell, "HIER321_ID", "varchar", 64, 0, testVersionId, isNullable);
        final List<Grouptypes> keys5 = Arrays.asList(key5, key51, key52, key53, key54);
        final GroupTypeDef def5 = new GroupTypeDef(keys5, cell, testVersionId);
        final GroupHashId velocityGroupDefinition5 = getCreateVelocityGroup(def5);
        groups.put(def1.getGroupType(), velocityGroupDefinition1);
        groups.put(def2.getGroupType(), velocityGroupDefinition2);
        groups.put(def3.getGroupType(), velocityGroupDefinition3);
        groups.put(def4.getGroupType(), velocityGroupDefinition4);
        groups.put(def5.getGroupType(), velocityGroupDefinition5);
    }

    private Grouptypes getKey(final String gpType, final String name, final String type, final int size,
            final int scale, final String vId, int nullable) {
        final Grouptypes key = new Grouptypes(null);
        key.setGrouptype(gpType);
        key.setDataname(name);
        key.setDatatype(type);
        key.setDatasize(size);
        key.setDatascale(scale);
        key.setVersionid(vId);
        key.setNullable(nullable);
        return key;
    }

    private GroupHashId getCreateVelocityGroup(final GroupTypeDef aGroupDefinition) {
        final List<String> dataKeyNames = getDataNames(aGroupDefinition);
        return new GroupHashId(aGroupDefinition.getGroupType(), aGroupDefinition.getTableName(),
                GroupTypeDef.KEY_NAME_GROUP_NAME, dataKeyNames);
    }

    private List<String> getDataNames(final GroupTypeDef aGroupDefinition) {
        // I'm still creating them in a loop......
        final List<String> dataKeyNames = new ArrayList<String>();
        for (final GroupTypeKeyDef groupKey : aGroupDefinition.getDataKeys()) {
            //Ignore HIERARCHY_2 for 3G
            if (!groupKey.getKeyName().equals(HIER2)) {
                dataKeyNames.add(groupKey.getKeyName());
            }
        }
        return dataKeyNames;
    }

    @Test
    public void testGetMssKPIQueryForRawTAC() {
        params.put(TIMERANGE_PARAM, "TR_2");
        params.put(TYPE_PARAM, TAC_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawMan() {
        params.put(TIMERANGE_PARAM, "TR_2");
        params.put(TYPE_PARAM, MAN_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawController() {
        params.put(TIMERANGE_PARAM, "TR_2");
        params.put(TYPE_PARAM, BSC_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawAccessArea() {
        params.put(TIMERANGE_PARAM, "TR_2");
        params.put(TYPE_PARAM, CELL_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawEventSrc() {
        params.put(TIMERANGE_PARAM, "TR_2");
        params.put(TYPE_PARAM, MSC_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForGroupRawEventSrc() {
        params.put(TIMERANGE_PARAM, "TR_2");
        params.put(TYPE_PARAM, MSC_PARAM.toUpperCase());
        params.put(GROUP_NAME_KEY, "mscGroup");
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForGroupRawTAC() {
        params.put(TIMERANGE_PARAM, "TR_2");
        params.put(TYPE_PARAM, TAC_PARAM.toUpperCase());
        params.put(GROUP_NAME_KEY, "tacGroup");
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForGroupRawController() {
        params.put(TIMERANGE_PARAM, "TR_2");
        params.put(TYPE_PARAM, BSC_PARAM.toUpperCase());
        params.put(GROUP_NAME_KEY, "bscGroup");
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForGroupRawAccessArea() {
        params.put(TIMERANGE_PARAM, "TR_2");
        params.put(TYPE_PARAM, CELL_PARAM.toUpperCase());
        params.put(GROUP_NAME_KEY, "cellGroup");
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawTAC_Day() {
        params.put(TIMERANGE_PARAM, "TR_3");
        params.put(TYPE_PARAM, TAC_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawMan_Day() {
        params.put(TIMERANGE_PARAM, "TR_3");
        params.put(TYPE_PARAM, MAN_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawController_Day() {
        params.put(TIMERANGE_PARAM, "TR_3");
        params.put(TYPE_PARAM, BSC_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawAccessArea_Day() {
        params.put(TIMERANGE_PARAM, "TR_3");
        params.put(TYPE_PARAM, CELL_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawEventSrc_Day() {
        params.put(TIMERANGE_PARAM, "TR_3");
        params.put(TYPE_PARAM, MSC_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForGroupRawEventSrc_Day() {
        params.put(TIMERANGE_PARAM, "TR_3");
        params.put(TYPE_PARAM, MSC_PARAM.toUpperCase());
        params.put(GROUP_NAME_KEY, "mscGroup");
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForGroupRawTAC_Day() {
        params.put(TIMERANGE_PARAM, "TR_3");
        params.put(TYPE_PARAM, TAC_PARAM.toUpperCase());
        params.put(GROUP_NAME_KEY, "tacGroup");
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForGroupRawController_Day() {
        params.put(TIMERANGE_PARAM, "TR_3");
        params.put(TYPE_PARAM, BSC_PARAM.toUpperCase());
        params.put(GROUP_NAME_KEY, "bscGroup");
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForGroupRawAccessArea_Day() {
        params.put(TIMERANGE_PARAM, "TR_3");
        params.put(TYPE_PARAM, CELL_PARAM.toUpperCase());
        params.put(GROUP_NAME_KEY, "cellGroup");
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawTAC_Week() {
        params.put(TIMERANGE_PARAM, "TR_4");
        params.put(TYPE_PARAM, TAC_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawMan_Week() {
        params.put(TIMERANGE_PARAM, "TR_4");
        params.put(TYPE_PARAM, MAN_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawController_Week() {
        params.put(TIMERANGE_PARAM, "TR_4");
        params.put(TYPE_PARAM, BSC_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawAccessArea_Week() {
        params.put(TIMERANGE_PARAM, "TR_4");
        params.put(TYPE_PARAM, CELL_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForRawEventSrc_Week() {
        params.put(TIMERANGE_PARAM, "TR_4");
        params.put(TYPE_PARAM, MSC_PARAM.toUpperCase());
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForGroupRawEventSrc_Week() {
        params.put(TIMERANGE_PARAM, "TR_4");
        params.put(TYPE_PARAM, MSC_PARAM.toUpperCase());
        params.put(GROUP_NAME_KEY, "mscGroup");
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForGroupRawTAC_Week() {
        params.put(TIMERANGE_PARAM, "TR_4");
        params.put(TYPE_PARAM, TAC_PARAM.toUpperCase());
        params.put(GROUP_NAME_KEY, "tacGroup");
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForGroupRawController_Week() {
        params.put(TIMERANGE_PARAM, "TR_4");
        params.put(TYPE_PARAM, BSC_PARAM.toUpperCase());
        params.put(GROUP_NAME_KEY, "bscGroup");
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test
    public void testGetMssKPIQueryForGroupRawAccessArea_Week() {
        params.put(TIMERANGE_PARAM, "TR_4");
        params.put(TYPE_PARAM, CELL_PARAM.toUpperCase());
        params.put(GROUP_NAME_KEY, "cellGroup");
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testGetMssKPIQueryForTypeNotSupported() {
        params.put(TIMERANGE_PARAM, "TR_2");
        params.put(TYPE_PARAM, "Junk");
        params.remove(GROUP_NAME_KEY);
        final List<String> queries = testObj.getMssKPIQuery(params);
        checkNotNull(queries);
    }

    private void checkNotNull(final List<String> queries) {
        for (final String query : queries) {
            assertNotNull(query);
        }
    }

    @Test
    public void testGetColumnTypeTAC() throws Exception {
        final List<String> expectedValue = new ArrayList<String>();
        expectedValue.add(TAC_PARAM.toUpperCase());
        testObj.setType(TYPE_TAC);
        final List<String> actualValue = testObj.getColumnsForType();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetColumnTypeMSC() throws Exception {
        final List<String> expectedValue = new ArrayList<String>();
        expectedValue.add(EVENT_SOURCE_SQL_ID.toUpperCase());
        testObj.mssParameters = params;
        testObj.setType(TYPE_MSC);
        final List<String> actualValue = testObj.getColumnsForType();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetColumnTypeMAN() throws Exception {
        final List<String> expectedValue = new ArrayList<String>();
        expectedValue.add(MAN_PARAM.toUpperCase());
        testObj.mssParameters = params;
        testObj.setType(TYPE_MAN);
        final List<String> actualValue = testObj.getColumnsForType();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetColumnTypeBSC() throws Exception {
        final List<String> expectedValue = new ArrayList<String>();
        expectedValue.add(CONTROLLER_SQL_ID);
        testObj.mssParameters = params;
        testObj.setType(TYPE_BSC);
        final List<String> actualValue = testObj.getColumnsForType();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetColumnTypeCELL() throws Exception {
        final List<String> expectedValue = new ArrayList<String>();
        expectedValue.add(CELL_SQL_ID.toUpperCase());
        testObj.mssParameters = params;
        testObj.setType(TYPE_CELL);
        final List<String> actualValue = testObj.getColumnsForType();
        assertEquals(expectedValue, actualValue);
    }

    @Test
    public void testGetIntervalForNoTR() {
        final String expectedValue = ONE_MINUTE_STR;
        final String actualValue = testObj.getInterval(null);
        assertEquals(expectedValue, actualValue);

    }

    @Test
    public void testGetIntervalForTR1() {
        final String expectedValue = ONE_MINUTE_STR;
        final String actualValue = testObj.getInterval(EventDataSourceType.RAW.getValue());
        assertEquals(expectedValue, actualValue);

    }

    @Test
    public void testGetIntervalForTR2() {
        final String expectedValue = ONE_MINUTE_STR;
        final String actualValue = testObj.getInterval(EventDataSourceType.AGGREGATED_1MIN.getValue());
        assertEquals(expectedValue, actualValue);

    }

    @Test
    public void testGetIntervalForTR3() {
        final String expectedValue = FIFTEEN_MINUTE_STR;
        final String actualValue = testObj.getInterval(EventDataSourceType.AGGREGATED_15MIN.getValue());
        assertEquals(expectedValue, actualValue);

    }

    @Test
    public void testGetIntervalForTR4() {
        final String expectedValue = DAY_MINUTES_STR;
        final String actualValue = testObj.getInterval(EventDataSourceType.AGGREGATED_DAY.getValue());
        assertEquals(expectedValue, actualValue);

    }

    /**
     * Tests if the group boolean is true
     */
    @Test
    public void testisGroupTrue() {
        final Class<? extends MssKpiQueryFactoryHelper> factoryClass = testObj.getClass();
        final String methodIsGroup = "isGroup";

        final boolean expected = true;
        boolean actual = false;

        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(GROUP_NAME_KEY, "test");

        try {
            final Field parametersField = factoryClass.getDeclaredField("mssParameters");
            parametersField.setAccessible(true);
            parametersField.set(testObj, parameters);

            final Method method = factoryClass.getDeclaredMethod(methodIsGroup, new Class[] {});
            method.setAccessible(true);
            actual = (Boolean) method.invoke(testObj, new Object[] {});

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
        final Class<? extends MssKpiQueryFactoryHelper> factoryClass = testObj.getClass();
        testObj.mssParameters = new HashMap<String, Object>();
        final String methodIsGroup = "isGroup";

        final boolean expected = false;
        boolean actual = true;

        try {

            final Method method = factoryClass.getDeclaredMethod(methodIsGroup, new Class[] {});
            method.setAccessible(true);
            actual = (Boolean) method.invoke(testObj, new Object[] {});

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
}
