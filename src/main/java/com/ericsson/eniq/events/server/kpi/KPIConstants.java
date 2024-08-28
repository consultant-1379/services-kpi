/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi;

public class KPIConstants {
    // templates 
    public static final String NETWORK_EVENT_AGGREGATION_ERRORS_VM = "network/event_aggregation_errors.vm";

    public static final String SGEH_NETWORK_EVENT_AGGREGATION_ERRORS_VM = "sgeh/network/event_aggregation_errors.vm";

    public static final String NETWORK_EVENT_AGGREGATION_ERRORS_VM_MAN = "network/event_aggregation_errors_man.vm";

    public static final String SGEH_NETWORK_EVENT_AGGREGATION_ERRORS_VM_MAN = "sgeh_network/event_aggregation_errors_man.vm";

    public static final String NETWORK_EVENT_RAW_ERRORS_VM = "network/event_raw_errors.vm";

    public static final String NETWORK_EVENT_RAW_ERRORS_VM_MAN = "network/event_raw_errors_man.vm";

    public static final String NETWORK_EVENT_GROUP_AGGREGATION_ERRORS_VM = "network/event_group_aggregation_errors.vm";

    public static final String SGEH_NETWORK_EVENT_GROUP_AGGREGATION_ERRORS_VM = "sgeh/network/event_group_aggregation_errors.vm";

    public static final String NETWORK_EVENT_GROUP_RAW_ERRORS_VM = "network/event_group_raw_errors.vm";

    public static final String NETWORK_EVENT_AGGREGATION_SUCCESSES_VM_MAN = "network/event_aggregation_successes_man.vm";

    public static final String SGEH_NETWORK_EVENT_AGGREGATION_SUCCESSES_VM_MAN = "sgeh/network/event_aggregation_successes_man.vm";

    public static final String NETWORK_EVENT_AGGREGATION_SUCCESSES_VM = "network/event_aggregation_successes.vm";

    public static final String SGEH_NETWORK_EVENT_AGGREGATION_SUCCESSES_VM = "sgeh/network/event_aggregation_successes.vm";

    public static final String NETWORK_EVENT_RAW_SUCCESSES_VM_MAN = "network/event_raw_successes_man.vm";

    public static final String NETWORK_EVENT_RAW_SUCCESSES_VM_MAN_DATATIERING = "datatiering/event_raw_successes_man_tiered.vm";

    public static final String NETWORK_EVENT_RAW_SUCCESSES_VM = "network/event_raw_successes.vm";

    public static final String NETWORK_EVENT_RAW_SUCCESSES_DATATIERING_VM = "datatiering/event_raw_successes_tiered.vm";

    public static final String NETWORK_EVENT_GROUP_AGGREGATION_SUCCESSES_VM = "network/event_group_aggregation_successes.vm";

    public static final String SGEH_NETWORK_EVENT_GROUP_AGGREGATION_SUCCESSES_VM = "sgeh/network/event_group_aggregation_successes.vm";

    public static final String NETWORK_EVENT_GROUP_RAW_SUCCESSES_VM = "network/event_group_raw_successes.vm";

    public static final String NETWORK_EVENT_GROUP_RAW_SUCCESSES_DATATIERING_VM = "datatiering/event_group_raw_successes_tiered.vm";

    public static final String NETWORK_EVENT_IMSI_VM = "network/event_imsi.vm";

    public static final String SGEH_NETWORK_EVENT_IMSI_VM = "sgeh/network/event_imsi.vm";

    public static final String NETWORK_EVENT_GROUP_AGGREGATION_IMSI_VM = "network/event_group_aggregation_imsi.vm";

    public static final String NETWORK_EVENT_GROUP_RAW_AGGREGATION_IMSI_VM = "network/event_group_raw_imsi.vm";

    public static final String SGEH_NETWORK_EVENT_GROUP_RAW_AGGREGATION_IMSI_VM = "sgeh/network/event_group_raw_imsi.vm";

    public static final String SUCCESSES_TEMPLATE = "successesTemplate";

    public static final String ERRORS_TEMPLATE = "errorsTemplate";

    public static final String IMSI_TEMPLATE = "imsiTemplate";

    // KPI Templates
    public static final String NETWORK_EVENT_KPIS_VM = "network/FullKPI.vm";

    public static final String NETWORK_EVENT_KPIS_NO_TIME_SERIES_VM = "network/FullKPINoTimeSeries.vm";

    public static final String NETWORK_EVENT_AVG_CALC_VM = "network/KPI_Average_Template.vm";

    public static final String NETWORK_EVENT_AVG_EXTRA_CONDITION_CALC_VM = "network/KPI_Average_Extra_Condition_Template.vm";

    public static final String NETWORK_EVENT_MAX_CALC_VM = "network/KPI_Max_Success_Template.vm";

    public static final String NETWORK_EVENT_MAX_EXTRA_CONDITION_CALC_VM = "network/KPI_Max_Success_Extra_Condition_Template.vm";

    public static final String NETWORK_EVENT_RATE_CALC_VM = "network/KPI_Rate_Template.vm";

    public static final String NETWORK_SUM_OVER_TOTAL_RATE_CALC_VM = "network/KPI_Sum_Over_Total_Rate_Calculation.vm";

    public static final String NETWORK_EVENT_FAILURE_RATIO_EXTRA_CONDITION_CALC_VM = "network/KPI_Failure_Rate_Extra_Condition_Template.vm";

    public static final String NETWORK_EVENT_IMSI_CALC_VM = "network/IMSI_Calculation_Template.vm";

    //Mss templates

    public static final String MSS_NETWORK_EVENT_AGGREGATION_ERRORS_VM = "mss/network/aggregation/event_aggregation_errors.vm";

    public static final String MSS_NETWORK_EVENT_RAW_ERRORS_VM = "mss/network/raw/event_raw_errors.vm";

    public static final String MSS_NETWORK_EVENT_RAW_AGGREGATION_VIEW_VM = "mss/network/raw/event_raw_view.vm";

    public static final String MSS_NETWORK_EVENT_GROUP_AGGREGATION_ERRORS_VM = "mss/network/aggregationGroup/event_group_aggregation_errors.vm";

    public static final String MSS_NETWORK_EVENT_GROUP_RAW_ERRORS_VM = "mss/network/rawGroup/event_group_raw_errors.vm";

    public static final String MSS_NETWORK_EVENT_GROUP_RAW_AGGREGATION_VIEW_VM = "mss/network/rawGroup/event_group_raw_view.vm";

    public static final String MSS_NETWORK_EVENT_AGGREGATION_SUCCESSES_VM = "mss/network/aggregation/event_aggregation_successes.vm";

    public static final String MSS_NETWORK_EVENT_RAW_SUCCESSES_VM = "mss/network/raw/event_raw_successes.vm";

    public static final String MSS_NETWORK_EVENT_GROUP_AGGREGATION_SUCCESSES_VM = "mss/network/aggregationGroup/event_group_aggregation_successes.vm";

    public static final String MSS_NETWORK_EVENT_GROUP_RAW_SUCCESSES_VM = "mss/network/rawGroup/event_group_raw_successes.vm";

    public static final String MSS_TOTAL_IMSI_RAW = "mss/network/raw/Total_IMSI.vm";

    public static final String MSC_TOTAL_IMSI_RAW = "mss/network/raw/Total_IMSI_msc.vm";

    public static final String MSS_TOTAL_IMSI_GROUP_RAW = "mss/network/rawGroup/Total_Group_Raw_IMSI.vm";

    public static final String MSS_IMSI_TEMPLATE_RAW = "mss/network/raw/IMSI_Template.vm";

    public static final String MSC_IMSI_TEMPLATE_RAW = "mss/network/raw/IMSI_Template_msc.vm";

    public static final String MSS_IMSI_TEMPLATE_GROUP_RAW = "mss/network/rawGroup/IMSI_Group_Raw_Template.vm";

    public static final String MSS_COMPLETE_KPI_VM = "mss/network/FullMssKPI.vm";

    public static final String MSS_KPI_TABLE_RAW_AGGREGATION_VM = "mss/network/raw/MssKpiTable.vm";

    public static final String MSS_KPI_TABLE_GROUP_RAW_AGGREGATION_VM = "mss/network/rawGroup/GroupMssKpiTable.vm";

    public static final String MSS_NETWORK_EVENT_GROUP_ON_CLAUSE_VM = "mss/network/on_clause_error_suc_group_table.vm";

    public static final String MSS_NETWORK_EVENT_NON_GROUP_ON_CLAUSE_VM = "mss/network/on_clause_error_suc_non_group_table.vm";

    public static final String MSS_IMPACTED_SUBSCRIBERS = "mss/network/impacted_subscriber.vm";

    public static final String MSS_IMPACTED_SUBSCRIBERS_TEMPL = "mss/network/impacted_subscriber_Templ.vm";

    //Mss Kpi templates
    public static final String MSS_CALL_COMPLETION_RATIO_VM = "mss/network/kpi/Call_Completion_Kpi.vm";

    public static final String MSS_CALL_COMPLETION_RATIO_SMS_LOC_SERVICE_VM = "mss/network/kpi/Call_Completion_Kpi_Sms_Loc_Service.vm";

    public static final String MSS_CALL_COMPLETION_RATIO_EXTRA_CONDITION_VM = "mss/network/kpi/Call_Completion_Extra_Condition_Kpi.vm";

    public static final String MSS_CALL_DROP_RATIO_VM = "mss/network/kpi/Call_Drop_Kpi.vm";

    public static final String MSS_CALL_DROP_RATIO_EXTRA_CONDITION_VM = "mss/network/kpi/Call_Drop_Extra_Condition_Kpi.vm";

    public static final String MSS_CALL_ATTEMPTS_INTENSITY_VM = "mss/network/kpi/Call_Attempts_Intensity_Kpi.vm";

    public static final String MSS_CALL_DROP_INTENSITY_VM = "mss/network/kpi/Call_Drop_Intensity_Kpi.vm";

    // columns for queries and calculations
    public static final String NO_OF_SUCCESSES = "NO_OF_SUCCESSES";

    public static final String NO_OF_ERRORS = "NO_OF_ERRORS";

    public static final String TOTAL_DURATION = "TOTAL_DURATION";

    public static final String MAX_DURATION = "MAX_DURATION";

    public static final String PAGING_ATTEMPTS = "PAGING_ATTEMPTS";

    public static final String NO_OF_ERRORS_RAW = "count(*)";

    public static final String NO_OF_ERRORS_AGG = NO_OF_ERRORS;

    public static final String SUM_NO_OF_NET_INIT_DEAC = "sum(NO_OF_NET_INIT_DEACTIVATES)";

    public static final String NO_OF_SUCCESSES_RAW = "count(*)";

    public static final String NO_OF_SUCCESSES_AGG = NO_OF_SUCCESSES;

    public static final String NO_OF_PAGING_ATTEMPTS_RAW = "sum(PAGING_ATTEMPTS)";

    public static final String NO_OF_PAGING_ATTEMPTS_AGG = "NO_OF_PAGING_ATTEMPTS";

    public static final String NO_OF_TOTAL_ERR_SUBSCRIBERS_RAW = "count(distinct(IMSI))";

    public static final String NO_OF_TOTAL_ERR_SUBSCRIBERS = "NO_OF_TOTAL_ERR_SUBSCRIBERS";

    public static final String NO_OF_NET_INIT_DEACTIVATES_RAW = "sum(case when (DEACTIVATION_TRIGGER != 1) then 1 else 0 end)";

    public static final String NO_OF_NET_INIT_DEACTIVATES_AGG = "NO_OF_NET_INIT_DEACTIVATES";

    public static final String NO_OF_NET_INIT_DEACTIVATES = "NO_OF_NET_INIT_DEACTIVATES";

    public static final String TOTAL_DURATION_RAW = "sum(DURATION)";

    public static final String TOTAL_DURATION_AGG = "DURATION";

    public static final String MAX_DURATION_RAW = "max(DURATION)";

    // columns for queries and calculations for MSS

    public static final String NO_OF_DROPPED_CALLS = "NO_OF_DROPPED_CALLS";

    public static final String NO_OF_BLOCKED_CALLS = "NO_OF_BLOCKED_CALLS";

    // Variables for Substitution in templates
    public static final String EVENT_IMSI = "event_imsi";

    public static final String KPI_NAME_VARIABLE = "kpi_name";

    public static final String TOTAL_DURATION_VARIABLE = "total_duration";

    public static final String MAX_DURATION_VARIABLE = "max_duration";

    public static final String EVENT_ID_VARIABLE = "event_id_value";

    public static final String NO_OF_ERRORS_VARIABLE = "no_of_errors";

    public static final String NO_OF_SUCCESSES_VARIABLE = "no_of_successes";

    public static final String SUCCESS_AND_ERROR_SUM_VARIABLE = "suc_err_sum_variable";

    public static final String TABLE_COLUMNS_VARIABLE = "columns";

    public static final String SELECTED_KPIS_VARIABLE = "selected_KPIs";

    public static final String SUC_AS_TABLE = "suc";

    public static final String TEMP_SUC_AS_TABLE = "temp_suc";

    public static final String ERR_AS_TABLE = "err";

    public static final String TEMP_ERR_AS_TABLE = "temp_err";

    public static final String CONDITION_COLUMN = "condition_column";

    public static final String CONDITION = "condition";

    public static final String SGEH_TAC_TABLE = ", dc.DIM_E_SGEH_TAC tac";

    // Variables for Substitution in templates in MSS

    public static final String RAW_TABLES_KPI = "rawTables";

    public static final String RAW_TABLES_ALIAS_KPI = "rawTableAlias";

    public static final String MSS_KPI_TABLE = "mssKpiTable";

    // success query
    public static final String SUCCESS_AGGREGATION_WHERE_CONDITIONS_VARIABLE = "success_aggregation_where_conditions";

    public static final String SUCCESS_AGGREGATION_COLUMNS_VARIABLE = "success_aggregation_columns";

    public static final String SUCCESS_AGGREGATION_COLUMNS_ALIAS_VARIABLE = "success_aggregation_columns_alias";

    public static final String SUCCESS_RAW_COLUMNS_VARIABLE = "success_raw_columns";

    public static final String SUCCESS_RAW_COLUMNS_ALIAS_VARIABLE = "success_raw_columns_alias";

    public static final String SUCCESS_COLUMN_ALIAS_VARIABLE = "success_column_alias";

    public static final String SUCCESS_VIEW_VARIABLE = "sucview";

    public static final String SUB_VIEW_VARIABLE = "subview";

    public static final String SUCCESS_EVENTS_VARIABLE = "event_successes";

    public static final String SUCCESS_JOIN_CONDITIONS_VARIABLE = "success_join_conditions";

    public static final String SUCCESS_TABLE_ALIAS_VARIABLE = "success_table_alias";

    public static final String GROUP_SUCCESS_AGGREGATION_COLUMNS_VARIABLE = "group_success_aggregation_columns";

    public static final String GROUP_SUCCESS_RAW_COLUMNS_VARIABLE = "group_success_raw_columns";

    public static final String GROUP_SUCCESS_AGGREGATION_COLUMNS_ALIAS_VARIABLE = "group_success_aggregation_columns_alias";

    public static final String GROUP_SUCCESS_RAW_COLUMNS_ALIAS_VARIABLE = "group_success_raw_columns_alias";

    public static final String GROUP_NAME_COLUMN_VARIABLE = "groupnameColumn";

    // error query
    public static final String ERROR_AGGREGATION_COLUMNS_VARIABLE = "error_aggregation_columns";

    public static final String ERROR_AGGREGATION_COLUMNS_ALIAS_VARIABLE = "error_aggregation_columns_alias";

    public static final String ERROR_AGGREGATION_WHERE_CONDITIONS_VARIABLE = "error_aggregation_where_conditions";

    public static final String ERROR_VIEW_VARIABLE = "errview";

    public static final String ERROR_AGGREGATION_TABLES_VARIABLE = "errAggTables";

    public static final String ERROR_EVENTS_VARIABLE = "event_errors";

    public static final String EXTRA_COLUMN_CONDITION_VARIABLE = "extra_column_Condition_String";

    public static final String ERROR_TABLE_ALIAS_VARIABLE = "error_table_alias";

    public static final String DROPPED_TABLE_ALIAS_VARIABLE = "dropped_table_alias";

    public static final String BLOCKED_TABLE_ALIAS_VARIABLE = "blocked_table_alias";

    public static final String GROUP_ERROR_AGGREGATION_COLUMNS_ALIAS_VARIABLE = "group_error_aggregation_columns_alias";

    public static final String GROUP_ERROR_RAW_COLUMNS_ALIAS_VARIABLE = "group_error_raw_columns_alias";

    public static final String GROUP_ERROR_AGGREGATION_COLUMNS_VARIABLE = "group_error_aggregation_columns";

    public static final String GROUP_ERROR_RAW_COLUMNS_VARIABLE = "group_error_raw_columns";

    public static final String ERROR_RAW_COLUMNS_VARIABLE = "error_raw_columns";

    public static final String ERROR_RAW_COLUMNS_ALIAS_VARIABLE = "error_raw_columns_alias";

    public static final String TOTAL_BLOCKED_COLUMNS_ALIAS_VARIABLE = "blocked_columns_alias";

    public static final String TOTAL_DROPPED_COLUMNS_ALIAS_VARIABLE = "dropped_columns_alias";

    public static final String TOTAL_ERROR_COLUMNS_ALIAS_VARIABLE = "error_columns_alias";

    // IMSI Query
    public static final String NO_ERROR_SUBSCRIBERS_VARIABLE = "no_error_subscribers";

    public static final String NO_OF_PAGING_ATTEMPTS = "NO_OF_PAGING_ATTEMPTS";

    public static final String TELE_SERVICE_CODE = "TELE_SERVICE_CODE";

    public static final String NO_OF_TOTAL_SUBSCRIBERS = "NO_OF_TOTAL_SUBSCRIBERS";

    // KPI Names
    public static final String PDN_AVERAGE_CONN_TIME = "PDN Average Connection Time";

    public static final String PDN_CONN_SUC_RATE = "PDN Connection Success Rate";

    public static final String PDN_MAX_CONN_TIME = "PDN Maximum Connection Time";

    public static final String ATTACH_SUCCESS_RATE = "Attach Success Rate";

    public static final String PDP_CONTEXT_SUCCESS_RATE = "PDP Context Activation Success Rate";

    public static final String PDP_CONTEXT_CUTOFF_RATIO = "PDP Context Cutoff Ratio";

    public static final String ATTACH_TIME_AVG = "Attach Time Average";

    public static final String ATTACH_TIME_MAX = "Attach Time Max";

    public static final String BEARER_ACTIVATION_SUCCESS_RATE = "Bearer Activation Success Rate";

    public static final String BEARER_ACTIVATION_TIME_AVG = "Bearer Activation Time Average";

    public static final String BEARER_ACTIVATION_TIME_MAX = "Bearer Activation Time Max";

    public static final String UI_INITIATED_SERVICE_REQUEST_FAILURE_RATIO = "UE Initiated Service Request Failure Ratio";

    public static final String UI_INITIATED_SERVICE_REQUEST_AVERAGE_TIME = "UE Initiated Service Request Average Time";

    public static final String UI_INITIATED_SERVICE_REQUEST_MAX_TIME = "UE Initiated Service Request Max Time";

    public static final String TRACKING_AREA_UPDATE_SUCCESS_RATE = "Tracking Area Update Success Rate";

    public static final String TRACKING_AREA_AVERAGE_UPDATE_TIME = "Tracking Area Average Update Time";

    public static final String TRACKING_AREA_MAXIMUM_UPDATE_TIME = "Tracking Area Maximum Update Time";

    public static final String PAGING_ATTEMPTS_PER_SUB = "Paging Attempts Per Subscriber";

    public static final String S1_BASED_HANDOVER_SUCCESS_RATIO = "S1 Based Handover Success Rate";

    public static final String S1_BASED_HANDOVER_WO_SGW_W_MME_RELOC_SUCCESS_RATIO = "S1 Based HO Without SGW And With MME Relocation";

    public static final String S1_BASED_HANDOVER_WO_SGW_WO_MME_SUCCESS_RATIO = "S1 Based HO Without SGW And MME Relocation";

    public static final String S1_BASED_HANDOVER_W_SGW_WO_MME_RELOC_SUCCESS_RATIO = "S1 Based S1 Based HO With SGW And Without MME Relocation";

    public static final String S1_BASED_HANDOVER_W_SGW_W_MME_SUCCESS_RATIO = "S1 Based HO With SGW And MME Relocation";

    public static final String X2_BASED_HANDOVER_SUCCESS_RATIO = "X2 Based Handover Success Rate";

    public static final String X2_BASED_HO_WITHOUT_SQW_RELOCATION_SUCCESS_RATIO = "X2 Based HO Without SGW Relocation";

    public static final String X2_BASED_HO_WITH_SQW_RELOCATION_SUCCESS_RATIO = "X2 Based HO With SGW Relocation";

    public static final String INTER_MME_TAU_SUCCESS_RATE = "Inter MME Tracking Area Update Success Rate";

    public static final String INTRA_MME_TAU_SUCCESS_RATE = "Intra MME Tracking Area Update Success Rate";

    // General Constants
    public static final String DAY_MINUTES_STR = "1440";

    public static final String ONE_MINUTE_STR = "1";

    public static final String FIFTEEN_MINUTE_STR = "15";

    public static final String AVG_DURATION = "AVG_DURATION";

    public static final String EVENT_E_LTE_SUC_RAW = "EVENT_E_LTE_SUC_RAW";

    public static final String EVENT_E_LTE_ERR_RAW = "EVENT_E_LTE_ERR_RAW";

    public static final String TAU_TYPE = "TAU_TYPE";

    public static final String EVENT_SUBTYPE_ID = "EVENT_SUBTYPE_ID";

    public static final String NETWORK_EVENT_SUCCESS_RATIO_EXTRA_CONDITION_CALC_VM = "network/KPI_Success_Rate_Extra_Condition_Template.vm";

    public static final String NETWORK_EVENT_SUCCESS_RATIO_EXTRA_CONDITION_CALC_USE_SUC_AND_ERR_VM = "network/KPI_Suc_and_Fail_Rate_Extra_Condition_Template.vm";

    public static final String HANDOVER_AVERAGE_TIME = "Handover Average Time";

    public static final String HANDOVER_MAX_TIME = "Handover Max Time";

    public static final String HANDOVER_SUCCESS_RATIO = "Handover success Ratio";
}