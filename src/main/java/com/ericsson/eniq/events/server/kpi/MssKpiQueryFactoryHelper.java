/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.server.kpi;

import static com.ericsson.eniq.events.server.common.ApplicationConstants.*;
import static com.ericsson.eniq.events.server.kpi.KPIConstants.*;

import java.util.*;

import javax.ejb.*;

import com.ericsson.eniq.events.server.common.EventDataSourceType;
import com.ericsson.eniq.events.server.common.tablesandviews.MssAggregationView;
import com.ericsson.eniq.events.server.common.tablesandviews.TechPackTables;

@Stateless
@LocalBean
public class MssKpiQueryFactoryHelper {

    @EJB
    KpiUtilities kpiUtilities;

    @EJB
    KpiFactory kpiFactory;

    boolean isAggregation;

    Map<String, Object> mssParameters;

    MssAggregationView mssAggregationView;

    protected boolean shouldExcludeExclusiveTacs;

    protected boolean isExcludedTacOrGroup;

    private String queryType;

    private String timeStringToAppend;

    TechPackTables techPackTables;

    private enum TypesOfColumns {
        SUCCESS, DROPPED, BLOCKED, LOC_SERVICE_ERROR, LOC_SERVICE_SUCCESS, SMS_ERROR, SMS_SUCCESS
    }

    /**
     * This method will create an sql query that will get the KPI's for MSS. Decisions about groups, aggregations etc will all be made based on the
     * parameters that we receive in. Velocity macros and templates are used in different stages to build up the final sql.
     * 
     * @param templateParmeters Starting parameters. These are configured outside this class and include selections made in the GUI.
     * @return A string query that can be run into sybase IQ
     */
    public List<String> getMssKPIQuery(final Map<String, Object> templateParmeters) {
        queryType = (String) templateParmeters.get(TYPE_PARAM);
        populateMssParameters(templateParmeters);
        final List<String> allQueries = new ArrayList<String>();
        allQueries.add(getImpactedSubscribers());
        allQueries.add(getMssNonEmergengyVoiceKPI());
        allQueries.add(getMssEmergengyVoiceKPI());
        allQueries.add(getMssSmsKPI());
        allQueries.add(getMssLocationServiceKPI());
        return allQueries;
    }

    private String getMssNonEmergengyVoiceKPI() {
        mssAggregationView = getAggregationViewToUseVoice();
        populateMssParametersForVoiceNonEmergencyCalls();
        final List<MssKPI> allKpis = kpiFactory.getAllMssVoiceNonEmergencyKPIs();
        populateMssVoiceSubTemplates(allKpis);
        return buildMssKpiTable();
    }

    private String getMssEmergengyVoiceKPI() {
        mssAggregationView = getAggregationViewToUseVoice();
        populateMssParametersForVoiceEmergencyCalls();
        final List<MssKPI> allKpis = kpiFactory.getAllMssVoiceEmergencyKPIs();
        populateMssVoiceSubTemplates(allKpis);
        return buildMssKpiTable();
    }

    private String getMssSmsKPI() {
        mssAggregationView = getAggregationViewToUseSms();
        populateMssParametersForSmsAndLocServices();
        final List<MssKPI> allKpis = kpiFactory.getAllMssSmsKPIs();
        populateMssSmsSubTemplates(allKpis);
        return buildMssKpiTable();
    }

    private String getMssLocationServiceKPI() {
        mssAggregationView = getAggregationViewToUseLocService();
        populateMssParametersForSmsAndLocServices();
        final List<MssKPI> allKpis = kpiFactory.getAllMssLocationServiceKPIs();
        populateMssLocServiceSubTemplates(allKpis);
        return buildMssKpiTable();
    }

    private String buildMssKpiTable() {
        final String buildMssKpiTable = getMssKpiTable();
        mssParameters.put(MSS_KPI_TABLE, buildMssKpiTable);
        final String buildFinalSQLFromMasterTemplate = kpiUtilities.getQueryFromTemplate(MSS_COMPLETE_KPI_VM, mssParameters);

        return buildFinalSQLFromMasterTemplate;
    }

    private String getMssKpiTable() {
        String template = null;
        if (isGroup()) {
            template = MSS_KPI_TABLE_GROUP_RAW_AGGREGATION_VM;
        } else {
            template = MSS_KPI_TABLE_RAW_AGGREGATION_VM;
        }
        return kpiUtilities.getQueryFromTemplate(template, mssParameters);
    }

    /**
     * Based on the
     * 
     * @param templateParmeters
     */
    void populateMssParameters(final Map<String, Object> templateParmeters) throws UnsupportedOperationException {

        mssParameters = new HashMap<String, Object>();
        mssParameters.putAll(templateParmeters);
        isAggregation = false;
        shouldExcludeExclusiveTacs = false;
        techPackTables = (TechPackTables) mssParameters.get(TECH_PACK_TABLES);
        final String timeRangeForInterval = (String) mssParameters.get(TIMERANGE_PARAM);
        String timeRangeForTableType = timeRangeForInterval;
        isExcludedTacOrGroup = (Boolean) mssParameters.get(IS_EXCULDED_TAC_OR_TACGROUP);
        if (isExcludedTacOrGroup) {
            timeRangeForTableType = EventDataSourceType.RAW.getValue();
        }
        timeStringToAppend = returnAggregateViewTypeWithOutOneMinute(timeRangeForTableType);
        isAggregation = toUseAggregationTables(timeRangeForTableType);
        if (!isExcludedTacOrGroup) {
            shouldExcludeExclusiveTacs = true;
        }
        // Figure out the extra columns we need based on the type
        final List<String> columnsForType = getColumnsForType();
        //if we don't support the type then throw unsupported exception
        if ((columnsForType == null) || columnsForType.isEmpty()) {
            throw new UnsupportedOperationException();
        }
        mssParameters.put(SUCCESS_TABLE_ALIAS_VARIABLE, SUC_AS_TABLE);
        mssParameters.put(TABLE_COLUMNS_VARIABLE, columnsForType);
        mssParameters.put(INTERVAL_PARAM, getInterval(timeRangeForInterval));
        mssParameters.put("isAgg", isAggregation);
        mssParameters.put("event_source_table_alias", "total_sub");
        mssParameters.put(USE_TAC_EXCLUSION_BOOLEAN, shouldExcludeExclusiveTacs);

    }

    /**
     * Based on the
     * 
     * @param templateParmeters
     */
    void populateMssParametersForVoiceEmergencyCalls() {
        mssParameters.put(EXTRA_WHERE_CLAUSE, "and TELE_SERVICE_CODE = 18");
        mssParameters.put(TO_CALC_TOTAL_SUBSCRIBERS, true);
        mssParameters.put(IS_MSS_VOICE_REPORT_VAR, true);
        mssParameters.put(IS_MSS_VOICE_EMERGENCY_CALL, true);
    }

    /**
     * Based on the
     * 
     * @param templateParmeters
     */
    void populateMssParametersForVoiceNonEmergencyCalls() {
        mssParameters.put(TO_CALC_TOTAL_SUBSCRIBERS, true);
        mssParameters.put(EXTRA_WHERE_CLAUSE, "");
        mssParameters.put(IS_MSS_VOICE_REPORT_VAR, true);
    }

    /**
     * Based on the
     * 
     * @param templateParmeters
     */
    void populateMssParametersForSmsAndLocServices() {
        mssParameters.put(EXTRA_WHERE_CLAUSE, "");
        mssParameters.put(IS_MSS_VOICE_REPORT_VAR, false);
    }

    private MssAggregationView getAggregationViewToUseVoice() {
        if (queryType.equals(TYPE_TAC) || queryType.equals(TYPE_MAN)) {
            return MssAggregationView.TAC_MANUFACTURER_SUMMARY;
        } else if (queryType.equals(TYPE_BSC) || queryType.equals(TYPE_CELL)) {
            return MssAggregationView.CONTROLLER_CELL_SUMMARY;
        } else {
            return MssAggregationView.EVENTSRC_SUMMARY;
        }
    }

    private MssAggregationView getAggregationViewToUseSms() {
        if (queryType.equals(TYPE_TAC) || queryType.equals(TYPE_MAN)) {
            return MssAggregationView.SMS_TAC_MANUFACTURER_SUMMARY;
        } else if (queryType.equals(TYPE_BSC) || queryType.equals(TYPE_CELL)) {
            return MssAggregationView.SMS_CONTROLLER_CELL_SUMMARY;
        } else {
            return MssAggregationView.SMS_EVENTSRC_SUMMARY;
        }
    }

    private MssAggregationView getAggregationViewToUseLocService() {
        if (queryType.equals(TYPE_TAC) || queryType.equals(TYPE_MAN)) {
            return MssAggregationView.LOC_SERVICE_TAC_MANUFACTURER_SUMMARY;
        } else if (queryType.equals(TYPE_BSC) || queryType.equals(TYPE_CELL)) {
            return MssAggregationView.LOC_SERVICE_CONTROLLER_CELL_SUMMARY;
        } else {
            return MssAggregationView.LOC_SERVICE_EVENTSRC_SUMMARY;
        }
    }

    private String getImpactedSubscribers() {
        mssParameters.put(EXTRA_WHERE_CLAUSE, "");
        mssParameters.put(IS_MSS_VOICE_REPORT_VAR, false);
        String template = "";
        String template_total = "";
        final String isTypeMSC = "isTypeMSC";
        if (isGroup()) {
            template = MSS_IMSI_TEMPLATE_GROUP_RAW;
            if (queryType.equals(TYPE_MSC)) {
                mssParameters.put(isTypeMSC, true);
            }
        } else if (queryType.equals(TYPE_MSC)) {
            template = MSC_IMSI_TEMPLATE_RAW;
            mssParameters.put(isTypeMSC, true);
        } else {
            template = MSS_IMSI_TEMPLATE_RAW;
        }
        final List<String> allErrTables = new ArrayList<String>();
        allErrTables.addAll(getRawTableList(TypesOfColumns.BLOCKED));
        allErrTables.addAll(getRawTableList(TypesOfColumns.DROPPED));
        allErrTables.addAll(getRawTableList(TypesOfColumns.SMS_ERROR));
        allErrTables.addAll(getRawTableList(TypesOfColumns.LOC_SERVICE_ERROR));
        mssParameters.put(RAW_TABLES_KPI, allErrTables);
        mssParameters.put(RAW_TABLES_ALIAS_KPI, IMPACTED_SUBSCRIBERS);
        final String imsiTable = kpiUtilities.getQueryFromTemplate(template, mssParameters);
        mssParameters.put(IMSI_TOTAL_VAR, imsiTable);
        if (isGroup()) {
            template_total = MSS_IMPACTED_SUBSCRIBERS;
        } else {
            template_total = MSS_IMPACTED_SUBSCRIBERS_TEMPL;
        }
        return kpiUtilities.getQueryFromTemplate(template_total, mssParameters);
    }

    /**
     * @param allKpis
     */
    void populateMssVoiceSubTemplates(final List<MssKPI> allKpis) {
        // Populate sub templates
        final String success = getSuccessString(allKpis, TypesOfColumns.SUCCESS);
        mssParameters.put(SUCCESS_EVENTS_VARIABLE, success);

        final String error = getErrorViewString(allKpis);
        mssParameters.put(ERROR_EVENTS_VARIABLE, error);

        final String calculations = getCalculation(allKpis);
        mssParameters.put(SELECTED_KPIS_VARIABLE, calculations);

        final String eventImsi = getVoiceIMSI();
        mssParameters.put(EVENT_IMSI, eventImsi);
    }

    /**
     * @param allKpis
     */
    void populateMssSmsSubTemplates(final List<MssKPI> allKpis) {
        // Populate sub templates
        final String success = getSuccessString(allKpis, TypesOfColumns.SMS_SUCCESS);
        mssParameters.put(SUCCESS_EVENTS_VARIABLE, success);

        final String error = getErrorViewStringForSmsOrLocService(allKpis, TypesOfColumns.SMS_ERROR);
        mssParameters.put(ERROR_EVENTS_VARIABLE, error);

        final String calculations = getCalculation(allKpis);
        mssParameters.put(SELECTED_KPIS_VARIABLE, calculations);

        //final String eventImsi = getSmsIMSI();
        mssParameters.put(EVENT_IMSI, "");
    }

    /**
     * @param allKpis
     */
    void populateMssLocServiceSubTemplates(final List<MssKPI> allKpis) {
        // Populate sub templates
        final String success = getSuccessString(allKpis, TypesOfColumns.LOC_SERVICE_SUCCESS);
        mssParameters.put(SUCCESS_EVENTS_VARIABLE, success);

        final String error = getErrorViewStringForSmsOrLocService(allKpis, TypesOfColumns.LOC_SERVICE_ERROR);
        mssParameters.put(ERROR_EVENTS_VARIABLE, error);

        final String calculations = getCalculation(allKpis);
        mssParameters.put(SELECTED_KPIS_VARIABLE, calculations);

        //final String eventImsi = getLocServiceIMSI();
        mssParameters.put(EVENT_IMSI, "");
    }

    private String getVoiceIMSI() {
        final String successImsiStr = getSuccessImsiTable(TypesOfColumns.SUCCESS);
        mssParameters.put(IMSI_SUCCESSES_TABLE, successImsiStr);

        final String blockedImsiStr = getErrorImsiTable(TypesOfColumns.BLOCKED);
        mssParameters.put(IMSI_BLOCKED_TABLE, blockedImsiStr);

        final String droppedImsiStr = getErrorImsiTable(TypesOfColumns.DROPPED);
        mssParameters.put(IMSI_DROPPED_TABLE, droppedImsiStr);

        return getIMSI();
    }

    private String getIMSI() {
        String totalImsi = "";
        if (isGroup()) {
            totalImsi = MSS_TOTAL_IMSI_GROUP_RAW;
        } else if (queryType.equals(TYPE_MSC)) {
            totalImsi = MSC_TOTAL_IMSI_RAW;
        } else {
            totalImsi = MSS_TOTAL_IMSI_RAW;
        }
        return kpiUtilities.getQueryFromTemplate(totalImsi, mssParameters);
    }

    private String getSuccessImsiTable(final TypesOfColumns typeOfCol) {
        String template = "";
        if (isGroup()) {
            template = MSS_IMSI_TEMPLATE_GROUP_RAW;
        } else if (queryType.equals(TYPE_MSC)) {
            template = MSC_IMSI_TEMPLATE_RAW;
        } else {
            template = MSS_IMSI_TEMPLATE_RAW;
        }
        mssParameters.put(RAW_TABLES_KPI, getRawTableList(typeOfCol));
        mssParameters.put(RAW_TABLES_ALIAS_KPI, "suc_subscriber");
        return kpiUtilities.getQueryFromTemplate(template, mssParameters);
    }

    private String getErrorImsiTable(final TypesOfColumns columnType) {
        String template = "";
        List<String> tableNames = null;
        if (isGroup()) {
            template = MSS_IMSI_TEMPLATE_GROUP_RAW;
            tableNames = getRawTableForImsi(columnType);
        } else if (queryType.equals(TYPE_MSC)) {
            template = MSC_IMSI_TEMPLATE_RAW;
            tableNames = getRawTableForImsi(columnType);
        } else {
            template = MSS_IMSI_TEMPLATE_RAW;
            tableNames = getRawTableForImsi(columnType);
        }
        mssParameters.put(RAW_TABLES_KPI, tableNames);
        return kpiUtilities.getQueryFromTemplate(template, mssParameters);
    }

    private List<String> getRawTableForImsi(final TypesOfColumns columnType) {
        List<String> tableNames = new ArrayList<String>();
        if (columnType == TypesOfColumns.BLOCKED) {
            tableNames = getRawTableList(columnType);
            mssParameters.put(RAW_TABLES_ALIAS_KPI, "blocked_subscriber");
        } else if (columnType == TypesOfColumns.DROPPED) {
            tableNames = getRawTableList(columnType);
            mssParameters.put(RAW_TABLES_ALIAS_KPI, "dropped_subscriber");
        }
        return tableNames;
    }

    /**
     * Gets the interval for the specified time range, where the interval is the number of minutes between one date time and the next.
     * 
     * @param timeRange enum @see EventDataSourceType
     * @return 1 of the time range is 1 Min, if it is 15 Min then the interval is 15 and if the time range is 1 day, then the interval is 1440.
     */
    protected String getInterval(final String timeRange) {
        if (timeRange != null) {
            if (timeRange.equals(EventDataSourceType.AGGREGATED_15MIN.getValue())) {
                return FIFTEEN_MINUTE_STR;
            } else if (timeRange.equals(EventDataSourceType.AGGREGATED_DAY.getValue())) {
                return DAY_MINUTES_STR;
            }
        }
        return ONE_MINUTE_STR;
    }

    String getCalculation(final List<MssKPI> allKpis) {

        final String delimiter = ",\n";
        final StringBuilder equations = new StringBuilder();
        final Iterator<MssKPI> iter = allKpis.iterator();

        while (iter.hasNext()) {
            final MssKPI kpi = iter.next();
            if (iter.hasNext()) {
                equations.append(kpi.getKPICalculation(mssParameters));
                equations.append(delimiter);
            } else {
                equations.append(kpi.getKPICalculation(mssParameters));
            }
        }

        return equations.toString();
    }

    String getSuccessString(final List<MssKPI> allKpis, final TypesOfColumns typeOfCol) {
        final QueryHelperData queryHelperData = new QueryHelperData();
        if (isAggregation) {
            queryHelperData.columnVar = SUCCESS_AGGREGATION_COLUMNS_VARIABLE;
            if (isGroup()) {
                queryHelperData.template = MSS_NETWORK_EVENT_GROUP_AGGREGATION_SUCCESSES_VM;
            } else {
                queryHelperData.template = MSS_NETWORK_EVENT_AGGREGATION_SUCCESSES_VM;
            }
        } else {
            queryHelperData.columnVar = SUCCESS_RAW_COLUMNS_VARIABLE;
            if (isGroup()) {
                queryHelperData.template = MSS_NETWORK_EVENT_GROUP_RAW_SUCCESSES_VM;
            } else {
                queryHelperData.template = MSS_NETWORK_EVENT_RAW_SUCCESSES_VM;
            }
        }
        queryHelperData.cloumnAliasVar = SUCCESS_COLUMN_ALIAS_VARIABLE;
        final List<String> sucTablesRaw = getRawTableList(typeOfCol);
        mssParameters.put(RAW_SUC_TABLES, sucTablesRaw);
        mssParameters.put(SUCCESS_VIEW_VARIABLE, getAggTableList(typeOfCol));
        return getColumnAsStringForSuccess(queryHelperData, allKpis);
    }

    private String getColumnAsStringForSuccess(final QueryHelperData queryHelperData, final List<MssKPI> allKpis) {
        final List<String> columns = new ArrayList<String>();
        final List<String> columnsAlias = new ArrayList<String>();
        final String template = queryHelperData.template;
        for (final MssKPI kpi : allKpis) {
            final Map<String, String> map = getMapOfColumnsForSuccess(kpi);
            if (map != null) {
                for (final String suc : map.keySet()) {
                    // If the column is already in the list
                    if (!columnsAlias.contains(suc)) {
                        columnsAlias.add(suc);
                        columns.add(map.get(suc));
                    }
                }
            }
        }
        populateColumnParameters(columns, columnsAlias, queryHelperData, TypesOfColumns.SUCCESS);
        return kpiUtilities.getQueryFromTemplate(template, mssParameters);
    }

    private void populateColumnParameters(final List<String> column, final List<String> columnAlias, final QueryHelperData queryHelperData,
                                          final TypesOfColumns columnType) {
        final String aliasedColumns = kpiUtilities.listToString(columnAlias, DELIMITER);
        mssParameters.put(queryHelperData.columnVar, kpiUtilities.listToString(column, DELIMITER));
        mssParameters.put(queryHelperData.cloumnAliasVar, aliasedColumns);
        if (columnType == TypesOfColumns.BLOCKED) {
            mssParameters.put(TOTAL_BLOCKED_COLUMNS_ALIAS_VARIABLE, aliasedColumns);
        } else if (columnType == TypesOfColumns.DROPPED) {
            mssParameters.put(TOTAL_DROPPED_COLUMNS_ALIAS_VARIABLE, aliasedColumns);
        } else if ((columnType == TypesOfColumns.SMS_ERROR) || (columnType == TypesOfColumns.LOC_SERVICE_ERROR)) {
            mssParameters.put(TOTAL_ERROR_COLUMNS_ALIAS_VARIABLE, aliasedColumns);
        }
    }

    private Map<String, String> getMapOfColumnsForSuccess(final MssKPI kpi) {
        if (isAggregation) {
            return kpi.getSuccessColumnsAgg();
        }
        return kpi.getSuccessColumnsRaw();

    }

    String getErrorViewString(final List<MssKPI> allKpis) {
        final QueryHelperData queryHelperData = new QueryHelperData();
        if (isAggregation) {
            queryHelperData.columnVar = ERROR_AGGREGATION_COLUMNS_VARIABLE;
            queryHelperData.cloumnAliasVar = ERROR_AGGREGATION_COLUMNS_ALIAS_VARIABLE;
            if (isGroup()) {
                queryHelperData.template = MSS_NETWORK_EVENT_GROUP_AGGREGATION_ERRORS_VM;
                queryHelperData.errorViewTemplate = MSS_NETWORK_EVENT_GROUP_RAW_AGGREGATION_VIEW_VM;
            } else {
                queryHelperData.template = MSS_NETWORK_EVENT_AGGREGATION_ERRORS_VM;
                queryHelperData.errorViewTemplate = MSS_NETWORK_EVENT_RAW_AGGREGATION_VIEW_VM;
            }
        } else {
            queryHelperData.columnVar = ERROR_RAW_COLUMNS_VARIABLE;
            queryHelperData.cloumnAliasVar = ERROR_RAW_COLUMNS_ALIAS_VARIABLE;
            if (isGroup()) {
                queryHelperData.template = MSS_NETWORK_EVENT_GROUP_RAW_ERRORS_VM;
                queryHelperData.errorViewTemplate = MSS_NETWORK_EVENT_GROUP_RAW_AGGREGATION_VIEW_VM;
            } else {
                queryHelperData.template = MSS_NETWORK_EVENT_RAW_ERRORS_VM;
                queryHelperData.errorViewTemplate = MSS_NETWORK_EVENT_RAW_AGGREGATION_VIEW_VM;
            }
        }
        mssParameters.put(BLOCKED_TABLE_VAR, getErrorStringForBlocked(queryHelperData, allKpis));
        mssParameters.put(DROPPED_TABLE_VAR, getErrorStringForDropped(queryHelperData, allKpis));
        mssParameters.put(ERROR_VIEW, ERROR_VIEW);
        return kpiUtilities.getQueryFromTemplate(queryHelperData.errorViewTemplate, mssParameters);
    }

    String getErrorStringForDropped(final QueryHelperData queryHelperData, final List<MssKPI> allKpis) {
        if (isAggregation) {
            mssParameters.put(ERROR_AGGREGATION_TABLES_VARIABLE, getAggTableList(TypesOfColumns.DROPPED));
        } else {
            mssParameters.put(RAW_ERR_TABLES, techPackTables.getErrTables(KEY_TYPE_DROP_CALL));
        }
        mssParameters.put(ERROR_TABLE_ALIAS_VARIABLE, ERR_AS_TABLE_DROPPED);
        mssParameters.put(DROPPED_TABLE_ALIAS_VARIABLE, ERR_AS_TABLE_DROPPED);
        return getColumnAsStringForErrors(queryHelperData, allKpis, TypesOfColumns.DROPPED);
    }

    String getErrorStringForBlocked(final QueryHelperData queryHelperData, final List<MssKPI> allKpis) {
        if (isAggregation) {
            mssParameters.put(ERROR_AGGREGATION_TABLES_VARIABLE, getAggTableList(TypesOfColumns.BLOCKED));
        } else {
            mssParameters.put(RAW_ERR_TABLES, techPackTables.getErrTables(KEY_TYPE_ERR));
        }
        mssParameters.put(ERROR_TABLE_ALIAS_VARIABLE, ERR_AS_TABLE_BLOCKED);
        mssParameters.put(BLOCKED_TABLE_ALIAS_VARIABLE, ERR_AS_TABLE_BLOCKED);
        return getColumnAsStringForErrors(queryHelperData, allKpis, TypesOfColumns.BLOCKED);
    }

    String getColumnAsStringForErrors(final QueryHelperData queryHelperData, final List<MssKPI> allKpis, final TypesOfColumns columnType) {
        final List<String> columns = new ArrayList<String>();
        final List<String> columnsAlias = new ArrayList<String>();
        final String template = queryHelperData.template;
        for (final MssKPI kpi : allKpis) {
            final Map<String, String> map = getMapOfColumnsForErrors(kpi, columnType);
            if (map != null) {
                for (final String suc : map.keySet()) {
                    // If the column is already in the list
                    if (!columnsAlias.contains(suc)) {
                        columnsAlias.add(suc);
                        columns.add(map.get(suc));
                    }
                }
            }
        }
        populateColumnParameters(columns, columnsAlias, queryHelperData, columnType);
        return kpiUtilities.getQueryFromTemplate(template, mssParameters);
    }

    String getErrorViewStringForSmsOrLocService(final List<MssKPI> allKpis, final TypesOfColumns typeOfView) {
        final QueryHelperData queryHelperData = new QueryHelperData();
        if (isAggregation) {
            queryHelperData.columnVar = ERROR_AGGREGATION_COLUMNS_VARIABLE;
            queryHelperData.cloumnAliasVar = ERROR_AGGREGATION_COLUMNS_ALIAS_VARIABLE;
            if (isGroup()) {
                queryHelperData.template = MSS_NETWORK_EVENT_GROUP_AGGREGATION_ERRORS_VM;
                queryHelperData.errorViewTemplate = MSS_NETWORK_EVENT_GROUP_ON_CLAUSE_VM;
            } else {
                queryHelperData.template = MSS_NETWORK_EVENT_AGGREGATION_ERRORS_VM;
                queryHelperData.errorViewTemplate = MSS_NETWORK_EVENT_NON_GROUP_ON_CLAUSE_VM;
            }
        } else {
            queryHelperData.columnVar = ERROR_RAW_COLUMNS_VARIABLE;
            queryHelperData.cloumnAliasVar = ERROR_RAW_COLUMNS_ALIAS_VARIABLE;
            if (isGroup()) {
                queryHelperData.template = MSS_NETWORK_EVENT_GROUP_RAW_ERRORS_VM;
                queryHelperData.errorViewTemplate = MSS_NETWORK_EVENT_GROUP_ON_CLAUSE_VM;
            } else {
                queryHelperData.template = MSS_NETWORK_EVENT_RAW_ERRORS_VM;
                queryHelperData.errorViewTemplate = MSS_NETWORK_EVENT_NON_GROUP_ON_CLAUSE_VM;
            }
        }
        mssParameters.put(ERROR_VIEW, ERROR_VIEW);
        final String errTable = getErrorStringForSmsOrLocService(queryHelperData, allKpis, typeOfView);
        final String onClause = kpiUtilities.getQueryFromTemplate(queryHelperData.errorViewTemplate, mssParameters);
        final StringBuilder errView = new StringBuilder("\n" + FULL_OUTER_JOIN_STRING + "\n");
        errView.append(errTable);
        errView.append(onClause);
        return errView.toString();
    }

    String getErrorStringForSmsOrLocService(final QueryHelperData queryHelperData, final List<MssKPI> allKpis, final TypesOfColumns typeOfView) {
        if (isAggregation) {
            mssParameters.put(ERROR_AGGREGATION_TABLES_VARIABLE, getAggTableList(typeOfView));
        } else {
            mssParameters.put(RAW_ERR_TABLES, getRawTableList(typeOfView));
        }
        mssParameters.put(ERROR_TABLE_ALIAS_VARIABLE, ERROR_VIEW);
        return getColumnAsStringForErrors(queryHelperData, allKpis, typeOfView);
    }

    private Map<String, String> getMapOfColumnsForErrors(final MssKPI kpi, final TypesOfColumns columnType) {
        if (isAggregation) {
            if (columnType == TypesOfColumns.DROPPED) {
                return kpi.getErrorColumnsAggForDropped();
            }
            return kpi.getErrorColumnsAggForBlocked();
        }
        if (columnType == TypesOfColumns.DROPPED) {
            return kpi.getErrorColumnsRawForDropped();
        }
        return kpi.getErrorColumnsRawForBlocked();
    }

    protected List<String> getColumnsForType() {

        final List<String> tableColumns = new ArrayList<String>();

        if (queryType.equals(TYPE_TAC)) {
            tableColumns.add(TAC_PARAM.toUpperCase());
        } else if (queryType.equals(TYPE_MAN)) {
            tableColumns.add(MAN_PARAM.toUpperCase());
        } else if (queryType.equals(TYPE_MSC)) {
            if (isGroup()) {
                mssParameters.put(TYPE_PARAM, GROUP_TYPE_EVENT_SRC_CS);
            }
            tableColumns.add(EVENT_SOURCE_SQL_ID);
        } else if (queryType.equals(TYPE_BSC)) {
            if (isGroup()) {
                mssParameters.put(TYPE_PARAM, "RAT_VEND_HIER3");
            }
            tableColumns.add(CONTROLLER_SQL_ID);
        } else if (queryType.equals(TYPE_CELL)) {
            if (isGroup()) {
                mssParameters.put(TYPE_PARAM, "RAT_VEND_HIER321");
            }
            tableColumns.add(CELL_SQL_ID.toUpperCase());
        }
        return tableColumns;
    }

    /**
     * Determines if this is a Group KPI
     * 
     * @return true if its a group
     */
    private boolean isGroup() {
        return mssParameters.containsKey(GROUP_NAME_KEY);
    }

    /**
     * This method is used to check if raw tables or the aggregation tables should be used
     * 
     * @param timeRange indicated the time range raw, 15 min or day tables
     * @return true if time range specifies the aggregation else false
     */
    private boolean toUseAggregationTables(final String timeRange) {
        if ((timeRange == null) || EventDataSourceType.AGGREGATED_1MIN.getValue().equalsIgnoreCase(timeRange)
                || EventDataSourceType.RAW.getValue().equalsIgnoreCase(timeRange)) {
            return false;
        }
        return true;
    }

    /**
     * This method will return the list of aggregation table to use for given Aggregation view
     * 
     * @param typeOfView represents SUCCESS,BLOCKED, DROPPED aggregation view
     * @return list of aggregation table to use
     */
    private List<String> getAggTableList(final TypesOfColumns typeOfView) {
        final List<String> aggTableView;
        if (TypesOfColumns.BLOCKED.equals(typeOfView)) {
            aggTableView = mssAggregationView.getErrorView();
        } else if (TypesOfColumns.DROPPED.equals(typeOfView)) {
            aggTableView = mssAggregationView.getDroppedView();
        } else if (TypesOfColumns.LOC_SERVICE_ERROR.equals(typeOfView)) {
            aggTableView = mssAggregationView.getErrorView();
        } else if (TypesOfColumns.LOC_SERVICE_SUCCESS.equals(typeOfView)) {
            aggTableView = mssAggregationView.getSucessView();
        } else if (TypesOfColumns.SMS_ERROR.equals(typeOfView)) {
            aggTableView = mssAggregationView.getErrorView();
        } else if (TypesOfColumns.SMS_SUCCESS.equals(typeOfView)) {
            aggTableView = mssAggregationView.getSucessView();
        } else {
            aggTableView = mssAggregationView.getSucessView();
        }
        return updateAggTableWithTimeRange(aggTableView);
    }

    /**
     * This method will return the list of raw table to use for given raw view
     * 
     * @param typeOfView represents SUCCESS,BLOCKED, DROPPED error view
     * @return list of aggregation table to use
     */
    private List<String> getRawTableList(final TypesOfColumns typeOfView) {
        if (TypesOfColumns.BLOCKED.equals(typeOfView)) {
            return techPackTables.getErrTables(KEY_TYPE_ERR);
        } else if (TypesOfColumns.DROPPED.equals(typeOfView)) {
            return techPackTables.getErrTables(KEY_TYPE_DROP_CALL);
        } else if (TypesOfColumns.LOC_SERVICE_ERROR.equals(typeOfView)) {
            return techPackTables.getErrTables(KEY_TYPE_LOC_SERVICE_ERR);
        } else if (TypesOfColumns.LOC_SERVICE_SUCCESS.equals(typeOfView)) {
            return techPackTables.getSucTables(KEY_TYPE_LOC_SERVICE_SUC);
        } else if (TypesOfColumns.SMS_ERROR.equals(typeOfView)) {
            return techPackTables.getErrTables(KEY_TYPE_SMS_ERR);
        } else if (TypesOfColumns.SMS_SUCCESS.equals(typeOfView)) {
            return techPackTables.getSucTables(KEY_TYPE_SMS_SUC);
        } else {
            return techPackTables.getSucTables();
        }
    }

    /**
     * This method is used to append the 15MIN or DAY aggregation time range string to the list of aggregation table to use
     * 
     * @param listOfAggTableView list of aggregation tables
     * @return list of aggregation tables appened with time range string(15MIN or DAY)
     */
    private List<String> updateAggTableWithTimeRange(final List<String> listOfAggTableView) {
        final List<String> aggViewListWithTimeString = new ArrayList<String>();
        for (final String tableView : listOfAggTableView) {
            aggViewListWithTimeString.add(tableView + timeStringToAppend);
        }
        return aggViewListWithTimeString;
    }

    /**
     * for JUNIT
     * 
     * @return the type
     */
    public String getType() {
        return queryType;
    }

    /**
     * for JUNIT
     * 
     * @param type the type to set
     */
    public void setType(final String type) {
        this.queryType = type;
    }

    /**
     * @param kpiUtilities the kpiUtilities to set
     */
    public void setKpiUtilities(final KpiUtilities kpiUtilities) {
        this.kpiUtilities = kpiUtilities;
    }

    /**
     * @param kpiFactory the kpiFactory to set
     */
    public void setKpiFactory(final KpiFactory kpiFactory) {
        this.kpiFactory = kpiFactory;
    }

    private class QueryHelperData {

        private String template;

        private String errorViewTemplate;

        private String columnVar;

        private String cloumnAliasVar;

    }
}
