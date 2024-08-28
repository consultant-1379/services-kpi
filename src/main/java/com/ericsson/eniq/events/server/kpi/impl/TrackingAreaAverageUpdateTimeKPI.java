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

public class TrackingAreaAverageUpdateTimeKPI implements KPI {

    @EJB
    KpiUtilities kpiUtilities;

    @Override
    public Map<String, String> getSuccessColumnsAgg() {
        final Map<String, String> successColumns = new HashMap<String, String>();
        successColumns.put(NO_OF_SUCCESSES, NO_OF_SUCCESSES_AGG);

        return successColumns;
    }

    @Override
    public Map<String, String> getSuccessColumnsRaw() {
        return null;
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
        parameters.put(KPI_NAME_VARIABLE, TRACKING_AREA_AVERAGE_UPDATE_TIME);
        parameters.put(TOTAL_DURATION_VARIABLE, TOTAL_DURATION);

        final String queryFragment = kpiUtilities.getQueryFromTemplate(NETWORK_EVENT_AVG_CALC_VM,
                parameters);

        return queryFragment;
    }

    @Override
    public String getEventID() {
        return kpiUtilities.getEventID(ApplicationConstants.L_TAU);
    }

    /**
     * @param kpiUtilities the kpiUtilities to set
     */
    public void setKpiUtilities(final KpiUtilities kpiUtilities) {
        this.kpiUtilities = kpiUtilities;
    }
}
