/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi.impl;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;

import static com.ericsson.eniq.events.server.kpi.KPIConstants.*;
import com.ericsson.eniq.events.server.common.ApplicationConstants;
import com.ericsson.eniq.events.server.kpi.KPI;
import com.ericsson.eniq.events.server.kpi.KpiUtilities;
import com.ericsson.eniq.events.server.templates.exception.ResourceInitializationException;

public class UEInitiatedServiceRequestMaxTimeKPI implements KPI {

    @EJB
    KpiUtilities kpiUtilities;

    @Override
    public Map<String, String> getSuccessColumnsAgg() {
        final Map<String, String> successColumns = new HashMap<String, String>();
        successColumns.put(MAX_DURATION, MAX_DURATION);
        successColumns.put(NO_OF_PAGING_ATTEMPTS, NO_OF_PAGING_ATTEMPTS);

        return successColumns;
    }

    @Override
    public Map<String, String> getSuccessColumnsRaw() {
        final Map<String, String> successColumns = new HashMap<String, String>();
        successColumns.put(NO_OF_PAGING_ATTEMPTS, NO_OF_PAGING_ATTEMPTS_RAW);
        successColumns.put(MAX_DURATION, MAX_DURATION_RAW);
        return successColumns;
    }

    @Override
    public Map<String, String> getErrorColumnsAgg() {
    	final Map<String, String> errorColumns = new HashMap<String, String>();
        errorColumns.put(NO_OF_PAGING_ATTEMPTS, NO_OF_PAGING_ATTEMPTS_AGG);
        return errorColumns;
    }

    @Override
    public Map<String, String> getErrorColumnsRaw() {
        final Map<String, String> errorColumns = new HashMap<String, String>();
        errorColumns.put(NO_OF_PAGING_ATTEMPTS, NO_OF_PAGING_ATTEMPTS_RAW);
        errorColumns.put(NO_OF_ERRORS, NO_OF_ERRORS_RAW);
        return errorColumns;
    }

    @Override
    public String getKPICalculation(final Map<String, Object> parameters) throws ResourceInitializationException {
        final String condition = "= 0";
        parameters.put(EVENT_ID_VARIABLE, getEventID());
        parameters.put(MAX_DURATION_VARIABLE, MAX_DURATION);
        parameters.put(CONDITION_COLUMN, NO_OF_PAGING_ATTEMPTS);
        parameters.put(CONDITION, condition);
        parameters.put(KPI_NAME_VARIABLE, UI_INITIATED_SERVICE_REQUEST_MAX_TIME);

        final String queryFragment = kpiUtilities.getQueryFromTemplate(
                NETWORK_EVENT_MAX_EXTRA_CONDITION_CALC_VM, parameters);

        return queryFragment;
    }

    @Override
    public String getEventID() {
        return kpiUtilities.getEventID(ApplicationConstants.L_SERVICE_REQUEST);
    }

    /**
     * @param kpiUtilities the kpiUtilities to set
     */
    public void setKpiUtilities(final KpiUtilities kpiUtilities) {
        this.kpiUtilities = kpiUtilities;
    }
}
