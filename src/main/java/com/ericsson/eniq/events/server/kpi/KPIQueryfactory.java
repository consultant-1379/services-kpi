/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi;

import static com.ericsson.eniq.events.server.common.ApplicationConstants.*;
import static com.ericsson.eniq.events.server.kpi.KPIConstants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import com.ericsson.eniq.events.server.common.EventDataSourceType;
import com.ericsson.eniq.events.server.common.TechPackList;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@LocalBean
@SuppressWarnings("PMD.CyclomaticComplexity")
public class KPIQueryfactory {

    /**
     * @param kpiUtilities the kpiUtilities to set
     */
    public void setKpiUtilities(final KpiUtilities kpiUtilities) {
        this.kpiUtilities = kpiUtilities;
    }

    @EJB
    KpiUtilities kpiUtilities;

    @EJB
    KpiFactory kpiFactory;

    boolean aggregation;

    Map<String, Object> parameters;

    /**
     * This method will create an sql query that will get the KPI's for LTE. Decisions about groups, aggregations etc
     * will all be made based on the parameters that we receive in. Velocity macros and templates are used in
     * different stages to build up the final sql.
     *
     * @param templateParameters Starting parameters. These are configured outside this class and include selections
     *                           made in the GUI.
     * @param useDataTiering  true will enforce to use 15MIN Success table rather than Success raw table if 0 < timerange <= 2hour.
     *                        this doesn't apply to error table selection. 
     * @return A string query that can be run into sybase IQ
     */
    public String getLteKPIQuery(final Map<String, Object> templateParameters, final boolean useDataTiering) {
        final List<KPI> allKpis = kpiFactory.getAllKPIs();
        populateParameters(templateParameters, null, useDataTiering);
        populateSubTemplates(allKpis, new TechPackList(), useDataTiering);

        final String buildFinalSQLFromMasterTemplate = kpiUtilities.getQueryFromTemplate(NETWORK_EVENT_KPIS_VM,
                parameters);

        return buildFinalSQLFromMasterTemplate;
    }

    // TODO: COMMENT THIS
    public String getSGEHKPIQuery(final Map<String, Object> templateParameters, final List<KPI> kpiList,
            final TechPackList techPackList) {
        final boolean useDataTiering = false;
        populateParameters(templateParameters, techPackList, useDataTiering);

        populateSubTemplates(kpiList, techPackList, useDataTiering);

        final String buildFinalSQLFromMasterTemplate = kpiUtilities.getQueryFromTemplate(NETWORK_EVENT_KPIS_VM,
                parameters);

        return buildFinalSQLFromMasterTemplate;
    }

    /**
     * Based on the
     *
     * @param templateParmeters
     */
    void populateParameters(final Map<String, Object> templateParmeters, final TechPackList techPackList,
            final boolean useDataTiering) {

        parameters = new HashMap<String, Object>();

        // Get the type
        final String type = (String) templateParmeters.get(TYPE_PARAM);

        // Figure out the extra columns we need based on the type
        final List<String> columnsForType = getColumnsForType(type);

        // Get the time range. Based on this we will have either aggregate or
        // raw tables. (if the exclusive tac group is chosen or a tac in the 
        //exclusive tac group is drilled on, then we must also go to raw)
        final String timeRange = (String) templateParmeters.get(TIMERANGE_PARAM);
        final boolean useTacExclusion = (Boolean) templateParmeters.get(USE_TAC_EXCLUSION_BOOLEAN);

        parameters.putAll(getTableNames(timeRange, type, useTacExclusion, useDataTiering));

        parameters.put(ERROR_TABLE_ALIAS_VARIABLE, ERR_AS_TABLE);
        parameters.put(SUCCESS_TABLE_ALIAS_VARIABLE, SUC_AS_TABLE);
        parameters.put(TABLE_COLUMNS_VARIABLE, columnsForType);
        parameters.put(INTERVAL_PARAM, getInterval(timeRange));
        //replace time interval if data tiering is on
        if (useDataTiering) {
            parameters.put(INTERVAL_PARAM, getIntervalForDataTiering(timeRange));
        }

        parameters.putAll(templateParmeters);
        aggregation = (techPackList == null) ? aggregation : techPackList.shouldQueryUseAggregationTables();
        parameters.put("isAgg", aggregation);
        parameters.put(TECH_PACK_LIST, techPackList);

        parameters.put("event_source_table_alias", "total_sub");

    }

    /**
     * @param allKpis
     */
    void populateSubTemplates(final List<KPI> allKpis, final TechPackList techPackList, final boolean useDataTiering) {
        // Populate sub templates
        final String success = getSuccessString(allKpis, techPackList, useDataTiering);
        parameters.put(SUCCESS_EVENTS_VARIABLE, success);

        final String error = getErrorString(allKpis, techPackList);
        parameters.put(ERROR_EVENTS_VARIABLE, error);

        final String calculations = getCalculation(allKpis, techPackList);
        parameters.put(SELECTED_KPIS_VARIABLE, calculations);

        final String eventImsi = getIMSI(techPackList);
        parameters.put(EVENT_IMSI, eventImsi);
    }

    private String getIMSI(final TechPackList techPackList) {
        String template = "";
        if (isSGEHQuery(techPackList)) {
            if (isGroup()) {
                template = SGEH_NETWORK_EVENT_GROUP_RAW_AGGREGATION_IMSI_VM;
            } else {
                template = SGEH_NETWORK_EVENT_IMSI_VM;
            }
        } else { // is LTE
            if (isGroup()) {
                template = NETWORK_EVENT_GROUP_RAW_AGGREGATION_IMSI_VM;
            } else {
                template = NETWORK_EVENT_IMSI_VM;
            }
        }

        return kpiUtilities.getQueryFromTemplate(template, parameters);
    }

    /**
     * Gets the interval for the specified time range, where the interval is the number of minutes between
     * one date time and the next.
     *
     * @param timeRange enum @see EventDataSourceType
     * @return 1 of the time range is 1 Min, if it is 15 Min then the interval is 15
     *         and if the time range is 1 day, then the interval is 1440.
     */
    protected String getInterval(final String timeRange) {
        if (timeRange != null) {
            if (timeRange.equals(EventDataSourceType.AGGREGATED_1MIN.getValue())) {
                return ONE_MINUTE_STR;
            } else if (timeRange.equals(EventDataSourceType.AGGREGATED_15MIN.getValue())) {
                return FIFTEEN_MINUTE_STR;
            } else if (timeRange.equals(EventDataSourceType.AGGREGATED_DAY.getValue())) {
                return DAY_MINUTES_STR;
            }
        }
        return ONE_MINUTE_STR;
    }

    /**
     * Gets the interval for the data tiering, where the interval is the number of minutes between
     * one date time and the next.
     *
     * @param timeRange enum @see EventDataSourceType
     * @return 15 of the time range is 1 Min, if it is 15 Min then the interval is 15
     *         and if the time range is 1 day, then the interval is 1440.
     */
    protected String getIntervalForDataTiering(final String timeRange) {
        if (timeRange != null && timeRange.equals(EventDataSourceType.AGGREGATED_DAY.getValue())) {
            return DAY_MINUTES_STR;
        }
        return FIFTEEN_MINUTE_STR;
    }

    /**
     * Gets the
     *
     * @param timeRange
     * @param type
     * @return
     */
    protected Map<String, String> getTableNames(final String timeRange, final String type,
            final boolean useTacExclusion, final boolean useDataTiering) {
        final Map<String, String> tableNames = new HashMap<String, String>();

        final String errorTable;
        String successTable;
        final String subscriberTable;

        if (shouldUseRawTables(type, timeRange, useTacExclusion)) {
            errorTable = "dc.EVENT_E_LTE_ERR_RAW";
            subscriberTable = "dc.EVENT_E_LTE_ERR_RAW";
            aggregation = false;

            if (useDataTiering) {
                final String tableName = getTablePrefix(type);
                successTable = "dc." + tableName + "_SUC" + FIFTEEN_MINUTES_VIEW;
            } else {
                successTable = "dc.EVENT_E_LTE_SUC_RAW";
            }

        } else {
            //Get the table name and view type
            final String tableName = getTablePrefix(type);
            final String returnAggregateViewType = returnAggregateViewType(timeRange);

            errorTable = "dc." + tableName + "_ERR" + returnAggregateViewType;
            successTable = "dc." + tableName + "_SUC" + returnAggregateViewType;
            subscriberTable = "dc." + tableName + "_ERR" + returnAggregateViewType;
            aggregation = true;
        }

        tableNames.put(ERROR_VIEW_VARIABLE, errorTable);
        tableNames.put(SUCCESS_VIEW_VARIABLE, successTable);
        tableNames.put(SUB_VIEW_VARIABLE, subscriberTable);

        return tableNames;
    }

    /**
     * Figure out if we should use raw tables for the queries. The following criteria determines if the raw tables
     * should be used:
     * 1) The Time Range says we should use Raw Tables (timeRange will be TR_1)
     * 2) No Time Range is specified, so if it is null for assume Raw Tables
     * 3) If the Time Range is 1Min (TR_2) but the type is a type that doesn't have 1Min aggregation tables, in that
     * case we use the Raw Tables.
     * 4) The useTacExclusion boolean says whether or not TACs in the EXCLUSIVE_TAC group should be excluded. Usually
     * they should.  However if not then we must use the raw tables (they are already excluded in the aggregations).
     * This case only arises if a.) the Exclusive Tac group itself is requested, or b.) a Tac in the Exclusive Tac group
     * is requested. (this should only arise on a drilldown from the Exclusive Tac group itself).
     *
     * @return True if any of the above cases are true
     */
    private boolean shouldUseRawTables(final String type, final String timeRange, final boolean useTacExclusion) {
        boolean rawTable = false;

        final List<String> typesThatUseRawTablesForOneMinutesQueries = new ArrayList<String>();

        typesThatUseRawTablesForOneMinutesQueries.add(TYPE_CELL);
        typesThatUseRawTablesForOneMinutesQueries.add(TYPE_ECELL);
        typesThatUseRawTablesForOneMinutesQueries.add(TYPE_IMSI);
        typesThatUseRawTablesForOneMinutesQueries.add(TYPE_APN);
        typesThatUseRawTablesForOneMinutesQueries.add(TYPE_MAN);
        typesThatUseRawTablesForOneMinutesQueries.add(TYPE_TAC);

        if (timeRange == null
                || (!useTacExclusion)
                || (timeRange.equalsIgnoreCase(EventDataSourceType.AGGREGATED_1MIN.getValue()) && typesThatUseRawTablesForOneMinutesQueries
                        .contains(type)) || timeRange.equalsIgnoreCase(EventDataSourceType.RAW.getValue())) {
            rawTable = true;
        }
        return rawTable;
    }

    /**
     * Get the table name prefix based on the type.
     *
     * @param type
     * @return
     */
    private String getTablePrefix(final String type) {

        final String tableName;
        if (type.equals(TYPE_APN)) {
            tableName = "EVENT_E_LTE_APN_EVENTID";
        } else if (type.equals(TYPE_TAC) || type.equals(TYPE_MAN)) {
            tableName = "EVENT_E_LTE_MANUF_TAC_EVENTID";
        } else if (type.equals(TYPE_SGSN)) {
            tableName = "EVENT_E_LTE_EVNTSRC_EVENTID";
        } else if (type.equals(TYPE_BSC)) {
            tableName = "EVENT_E_LTE_VEND_HIER3_EVENTID";
        } else if (type.equals(TYPE_CELL)) {
            tableName = "EVENT_E_LTE_VEND_HIER321_EVENTID";
        } else {
            tableName = "EVENT_E_LTE";
        }
        return tableName;
    }

    String getCalculation(final List<KPI> allKpis, final TechPackList techPackList) {

        final String delimiter = ",\n";
        final StringBuilder equations = new StringBuilder();
        final Iterator<KPI> iter = allKpis.iterator();

        while (iter.hasNext()) {
            final KPI kpi = iter.next();
            if (iter.hasNext()) {
                equations.append(kpi.getKPICalculation(parameters));
                equations.append(delimiter);
            } else {
                equations.append(kpi.getKPICalculation(parameters));
            }
        }

        return equations.toString();
    }

    String getSuccessString(final List<KPI> allKpis, final TechPackList techPackList, final boolean useDataTiering) {
        final List<String> success = new ArrayList<String>();
        final List<String> successAlias = new ArrayList<String>();
        final String type = (String) parameters.get(TYPE_PARAM);
        String template = "";

        for (final KPI kpi : allKpis) {
            if (aggregation) {
                populateSuccessParameters(success, successAlias, kpi.getSuccessColumnsAgg());

                if (isSGEHQuery(techPackList)) {
                    template = putParametersAndGetTemplateForSGEHAggSuccess(success, successAlias, type);
                } else {
                    template = putParametersAndGetTemplateForLTEAggSuccess(success, successAlias, type);
                }
            } else if (useDataTiering) {
                populateSuccessParameters(success, successAlias, kpi.getSuccessColumnsAgg());
                template = putParametersAndGetTemplateForLTEDataTieringSuccess(success, successAlias, type);
            } else {
                populateSuccessParameters(success, successAlias, kpi.getSuccessColumnsRaw());
                template = putParametersAndGetTemplateForLTERawSuccess(success, successAlias, type);
            }
        }

        return kpiUtilities.getQueryFromTemplate(template, parameters);
    }

    private String putParametersAndGetTemplateForLTEDataTieringSuccess(final List<String> success,
            final List<String> successAlias, final String type) {
        String template;
        if (isGroup()) {
            parameters.put(SUCCESS_AGGREGATION_COLUMNS_VARIABLE, success);
            parameters.put(SUCCESS_AGGREGATION_COLUMNS_ALIAS_VARIABLE,
                    kpiUtilities.listToString(successAlias, DELIMITER));

            template = NETWORK_EVENT_GROUP_RAW_SUCCESSES_DATATIERING_VM;
        } else {
            parameters.put(SUCCESS_AGGREGATION_COLUMNS_VARIABLE, success);
            parameters.put(SUCCESS_AGGREGATION_COLUMNS_ALIAS_VARIABLE,
                    kpiUtilities.listToString(successAlias, DELIMITER));

            if (type.equals(TYPE_MAN)) {
                template = NETWORK_EVENT_RAW_SUCCESSES_VM_MAN_DATATIERING;
            } else {
                template = NETWORK_EVENT_RAW_SUCCESSES_DATATIERING_VM;
            }
        }
        return template;
    }

    private void populateSuccessParameters(final List<String> success, final List<String> successAlias,
            final Map<String, String> map) {
        if (map != null) {
            for (final String suc : map.keySet()) {
                // If the success column is already in the list
                if (!successAlias.contains(suc)) {
                    successAlias.add(suc);
                    success.add(map.get(suc));
                }
            }
        }
    }

    private boolean isSGEHQuery(final TechPackList techPackList) {
        return techPackList.getTechPack(EVENT_E_SGEH_TPNAME) != null;
    }

    private String putParametersAndGetTemplateForLTERawSuccess(final List<String> success,
            final List<String> successAlias, final String type) {
        String template;
        if (isGroup()) {
            parameters.put(SUCCESS_RAW_COLUMNS_VARIABLE, success);
            parameters.put(SUCCESS_RAW_COLUMNS_ALIAS_VARIABLE, kpiUtilities.listToString(successAlias, DELIMITER));

            template = NETWORK_EVENT_GROUP_RAW_SUCCESSES_VM;
        } else {

            parameters.put(SUCCESS_RAW_COLUMNS_VARIABLE, kpiUtilities.listToString(success, DELIMITER));
            parameters.put(SUCCESS_RAW_COLUMNS_ALIAS_VARIABLE, kpiUtilities.listToString(successAlias, DELIMITER));
            if (type.equals(TYPE_MAN)) {
                template = NETWORK_EVENT_RAW_SUCCESSES_VM_MAN;
            } else {
                template = NETWORK_EVENT_RAW_SUCCESSES_VM;
            }
        }
        return template;
    }

    private String putParametersAndGetTemplateForLTEAggSuccess(final List<String> success,
            final List<String> successAlias, final String type) {
        String template;
        if (isGroup()) {
            parameters.put(SUCCESS_AGGREGATION_COLUMNS_VARIABLE, success);
            parameters.put(SUCCESS_AGGREGATION_COLUMNS_ALIAS_VARIABLE,
                    kpiUtilities.listToString(successAlias, DELIMITER));

            template = NETWORK_EVENT_GROUP_AGGREGATION_SUCCESSES_VM;
        } else {
            parameters.put(SUCCESS_AGGREGATION_COLUMNS_VARIABLE, success);
            parameters.put(SUCCESS_AGGREGATION_COLUMNS_ALIAS_VARIABLE,
                    kpiUtilities.listToString(successAlias, DELIMITER));

            if (type.equals(TYPE_MAN)) {
                template = NETWORK_EVENT_AGGREGATION_SUCCESSES_VM_MAN;
            } else {
                template = NETWORK_EVENT_AGGREGATION_SUCCESSES_VM;
            }
        }
        return template;
    }

    private String putParametersAndGetTemplateForSGEHAggSuccess(final List<String> success,
            final List<String> successAlias, final String type) {
        String template;
        if (isGroup()) {
            parameters.put(SUCCESS_AGGREGATION_COLUMNS_VARIABLE, success);
            parameters.put(SUCCESS_AGGREGATION_COLUMNS_ALIAS_VARIABLE,
                    kpiUtilities.listToString(successAlias, DELIMITER));

            template = SGEH_NETWORK_EVENT_GROUP_AGGREGATION_SUCCESSES_VM;
        } else {
            parameters.put(SUCCESS_AGGREGATION_COLUMNS_VARIABLE, success);
            parameters.put(SUCCESS_AGGREGATION_COLUMNS_ALIAS_VARIABLE,
                    kpiUtilities.listToString(successAlias, DELIMITER));

            if (type.equals(TYPE_MAN)) {
                template = SGEH_NETWORK_EVENT_AGGREGATION_SUCCESSES_VM_MAN;
            } else {
                template = SGEH_NETWORK_EVENT_AGGREGATION_SUCCESSES_VM;
            }
        }
        return template;
    }

    String getErrorString(final List<KPI> allKpis, final TechPackList techPackList) {
        final List<String> error = new ArrayList<String>();
        final List<String> errorAlias = new ArrayList<String>();
        String template = "";
        final String type = (String) parameters.get(TYPE_PARAM);

        for (final KPI kpi : allKpis) {
            if (aggregation) {
                final Map<String, String> map = kpi.getErrorColumnsAgg();
                populateSuccessParameters(error, errorAlias, map);

                if (isSGEHQuery(techPackList)) {
                    template = putParametersAndGetTemplateForSGEHAggError(error, errorAlias, type);
                } else {
                    template = putParametersAndGetTemplateForLTEAggErrors(error, errorAlias, type);
                }

            } else {
                final Map<String, String> map = kpi.getErrorColumnsRaw();
                if (map != null) {
                    for (final String err : map.keySet()) {
                        // If the error column is already in the list
                        if (!errorAlias.contains(err) && !err.isEmpty()) {
                            errorAlias.add(err);
                            error.add(map.get(err));
                        }
                    }
                }

                template = putParametersAndGetTemplateForLTERawErrors(error, errorAlias, type);
            }
        }

        return kpiUtilities.getQueryFromTemplate(template, parameters);
    }

    private String putParametersAndGetTemplateForLTERawErrors(final List<String> error, final List<String> errorAlias,
            final String type) {
        String template;

        if (isGroup()) {
            parameters.put(ERROR_RAW_COLUMNS_VARIABLE, error);
            parameters.put(ERROR_RAW_COLUMNS_ALIAS_VARIABLE, kpiUtilities.listToString(errorAlias, DELIMITER));
            template = NETWORK_EVENT_GROUP_RAW_ERRORS_VM;

        } else if (type.equals(TYPE_MAN)) {
            parameters.put(ERROR_RAW_COLUMNS_VARIABLE, kpiUtilities.listToString(error, DELIMITER));
            parameters.put(ERROR_RAW_COLUMNS_ALIAS_VARIABLE, kpiUtilities.listToString(errorAlias, DELIMITER));
            template = NETWORK_EVENT_RAW_ERRORS_VM_MAN;
        } else {
            parameters.put(ERROR_RAW_COLUMNS_VARIABLE, kpiUtilities.listToString(error, DELIMITER));
            parameters.put(ERROR_RAW_COLUMNS_ALIAS_VARIABLE, kpiUtilities.listToString(errorAlias, DELIMITER));
            template = NETWORK_EVENT_RAW_ERRORS_VM;

        }
        return template;
    }

    private String putParametersAndGetTemplateForLTEAggErrors(final List<String> error, final List<String> errorAlias,
            final String type) {
        String template;
        parameters.put(ERROR_AGGREGATION_COLUMNS_VARIABLE, error);
        parameters.put(ERROR_AGGREGATION_COLUMNS_ALIAS_VARIABLE, kpiUtilities.listToString(errorAlias, DELIMITER));

        if (isGroup()) {
            template = NETWORK_EVENT_GROUP_AGGREGATION_ERRORS_VM;
        } else if (type.equals(TYPE_MAN)) {
            template = NETWORK_EVENT_AGGREGATION_ERRORS_VM_MAN;
        } else {
            template = NETWORK_EVENT_AGGREGATION_ERRORS_VM;
        }
        return template;
    }

    private String putParametersAndGetTemplateForSGEHAggError(final List<String> error, final List<String> errorAlias,
            final String type) {
        String template;

        parameters.put(ERROR_AGGREGATION_COLUMNS_VARIABLE, error);
        parameters.put(ERROR_AGGREGATION_COLUMNS_ALIAS_VARIABLE, kpiUtilities.listToString(errorAlias, DELIMITER));

        if (isGroup()) {
            template = SGEH_NETWORK_EVENT_GROUP_AGGREGATION_ERRORS_VM;
        } else if (type.equals(TYPE_MAN)) {
            template = SGEH_NETWORK_EVENT_AGGREGATION_ERRORS_VM_MAN;
        } else {
            template = SGEH_NETWORK_EVENT_AGGREGATION_ERRORS_VM;
        }
        return template;
    }

    protected List<String> getColumnsForType(final String type) {

        final List<String> tableColumns = new ArrayList<String>();

        if (type.equals(TYPE_APN)) {
            tableColumns.add(APN_PARAM.toUpperCase());
        } else if (type.equals(TYPE_TAC)) {
            tableColumns.add(TAC_PARAM.toUpperCase());
        } else if (type.equals(TYPE_SGSN)) {
            tableColumns.add(SGSN_SQL_NAME.toUpperCase());
        } else if (type.equals(TYPE_BSC)) {
            tableColumns.add(RAT_PARAM.toUpperCase());
            tableColumns.add(VENDOR_PARAM.toUpperCase());
            tableColumns.add(BSC_SQL_NAME.toUpperCase());
        } else if (type.equals(TYPE_CELL)) {
            tableColumns.add(RAT_PARAM.toUpperCase());
            tableColumns.add(VENDOR_PARAM.toUpperCase());
            tableColumns.add(BSC_SQL_NAME.toUpperCase());
            tableColumns.add(CELL_SQL_NAME.toUpperCase());
        }

        return tableColumns;
    }

    /**
     * Determines if this is a Group KPI
     *
     * @return true if its a group
     */
    private boolean isGroup() {

        return parameters.containsKey(GROUP_NAME_PARAM);
    }

    /**
     * @param kpiFactory the kpiFactory to set
     */
    public void setKpiFactory(final KpiFactory kpiFactory) {
        this.kpiFactory = kpiFactory;
    }
}