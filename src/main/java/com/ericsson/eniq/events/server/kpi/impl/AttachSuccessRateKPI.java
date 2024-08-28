package com.ericsson.eniq.events.server.kpi.impl;

import com.ericsson.eniq.events.server.common.ApplicationConstants;
import com.ericsson.eniq.events.server.kpi.KPI;
import com.ericsson.eniq.events.server.kpi.KpiUtilities;
import com.ericsson.eniq.events.server.templates.exception.ResourceInitializationException;

import javax.ejb.EJB;
import java.util.HashMap;
import java.util.Map;

import static com.ericsson.eniq.events.server.kpi.KPIConstants.*;

public class AttachSuccessRateKPI implements KPI {

    @EJB
    private KpiUtilities kpiUtilities;

    /**
     * @param KpiUtilities
     */
    public AttachSuccessRateKPI(final KpiUtilities kpiUtils) {
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
        parameters.put(KPI_NAME_VARIABLE, ATTACH_SUCCESS_RATE);

        final String queryFragment = kpiUtilities.getQueryFromTemplate(NETWORK_EVENT_RATE_CALC_VM,
                parameters);

        return queryFragment;
    }

    @Override
    public String getEventID() {
        return kpiUtilities.getEventID(ApplicationConstants.L_ATTACH);
    }

    /**
     * @param kpiUtilities the kpiUtilities to set
     */
    public void setKpiUtilities(final KpiUtilities kpiUtilities) {
        this.kpiUtilities = kpiUtilities;
    }
}
