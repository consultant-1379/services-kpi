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

public class PDNAverageConnectionTimeKPI implements KPI {

    @EJB
    KpiUtilities kpiUtilities;

    @Override
    public Map<String, String> getSuccessColumnsAgg() {
        final Map<String, String> successColumns = new HashMap<String, String>();
        successColumns.put(TOTAL_DURATION, TOTAL_DURATION_AGG);
        successColumns.put(NO_OF_SUCCESSES, NO_OF_SUCCESSES_AGG);
        successColumns.put("AVG_DURATION", "AVG_DURATION");

        return successColumns;
    }

    @Override
    public Map<String, String> getSuccessColumnsRaw() {
        final Map<String, String> successColumns = new HashMap<String, String>();
        successColumns.put(TOTAL_DURATION, TOTAL_DURATION_RAW);
        return successColumns;
    }

    @Override
    public Map<String, String> getErrorColumnsAgg() {
    	return null;
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
        parameters.put(KPI_NAME_VARIABLE, PDN_AVERAGE_CONN_TIME);
        parameters.put(TOTAL_DURATION_VARIABLE, TOTAL_DURATION);
        final String queryFragment = kpiUtilities.getQueryFromTemplate(NETWORK_EVENT_AVG_CALC_VM,
                parameters);

        return queryFragment;
    }

    @Override
    public String getEventID() {
        return kpiUtilities.getEventID(ApplicationConstants.L_PDN_CONNECT);
    }

    /**
     * @param kpiUtilities the kpiUtilities to set
     */
    public void setKpiUtilities(final KpiUtilities kpiUtilities) {
        this.kpiUtilities = kpiUtilities;
    }
}
