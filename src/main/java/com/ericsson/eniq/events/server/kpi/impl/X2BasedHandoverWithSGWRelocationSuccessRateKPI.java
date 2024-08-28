/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi.impl;

import static com.ericsson.eniq.events.server.kpi.KPIConstants.*;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;

import com.ericsson.eniq.events.server.common.ApplicationConstants;
import com.ericsson.eniq.events.server.kpi.KPI;
import com.ericsson.eniq.events.server.kpi.KpiUtilities;
import com.ericsson.eniq.events.server.templates.exception.ResourceInitializationException;

public class X2BasedHandoverWithSGWRelocationSuccessRateKPI implements KPI {

    @EJB
    KpiUtilities kpiUtilities;

    /**
     * @param kpiUtilities
     */
    public X2BasedHandoverWithSGWRelocationSuccessRateKPI(final KpiUtilities kpiUtils) {
        this.kpiUtilities = kpiUtils;
    }

    @Override
    public Map<String, String> getSuccessColumnsAgg() {
        final Map<String, String> successColumns = new HashMap<String, String>();
        successColumns.put(NO_OF_SUCCESSES, NO_OF_SUCCESSES_AGG);

        return successColumns;
    }

    @Override
    public Map<String, String> getSuccessColumnsRaw() {
        final Map<String, String> successColumns = new HashMap<String, String>();
        successColumns.put(NO_OF_SUCCESSES, NO_OF_SUCCESSES_RAW);

        return successColumns;
    }

    @Override
    public Map<String, String> getErrorColumnsAgg() {
        final Map<String, String> errorColumns = new HashMap<String, String>();
        errorColumns.put(NO_OF_ERRORS, NO_OF_ERRORS_AGG);

        return errorColumns;
    }

    @Override
    public Map<String, String> getErrorColumnsRaw() {
        return null;
    }

    @Override
    public String getKPICalculation(final Map<String, Object> parameters) throws ResourceInitializationException {

        parameters.put(EVENT_ID_VARIABLE, getEventID());
        parameters.put(NO_OF_SUCCESSES_VARIABLE, NO_OF_SUCCESSES);
        parameters.put(NO_OF_ERRORS_VARIABLE, NO_OF_ERRORS);
        parameters.put(KPI_NAME_VARIABLE, X2_BASED_HO_WITH_SQW_RELOCATION_SUCCESS_RATIO);
        parameters.put(CONDITION_COLUMN, EVENT_SUBTYPE_ID);
        final String condition = " = 0";
        parameters.put(CONDITION, condition);

        final String queryFragment = kpiUtilities.getQueryFromTemplate(
                NETWORK_EVENT_SUCCESS_RATIO_EXTRA_CONDITION_CALC_VM, parameters);

        return queryFragment;
    }

    @Override
    public String getEventID() {
        return kpiUtilities.getEventID(ApplicationConstants.L_HANDOVER);
    }

    /**
     * @param kpiUtilities the kpiUtilities to set
     */
    public void setKpiUtilities(final KpiUtilities kpiUtilities) {
        this.kpiUtilities = kpiUtilities;
    }
}
