/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi.impl;

import static com.ericsson.eniq.events.server.kpi.KPIConstants.*;

import java.util.Map;

import javax.ejb.EJB;

import com.ericsson.eniq.events.server.kpi.KPI;
import com.ericsson.eniq.events.server.kpi.KpiUtilities;
import com.ericsson.eniq.events.server.templates.exception.ResourceInitializationException;

public class IMSIKPI implements KPI {

    @EJB
    KpiUtilities kpiUtilities;

    /**
     * @param kpiUtilities
     */
    public IMSIKPI(final KpiUtilities kpiUtils) {
        this.kpiUtilities = kpiUtils;
    }

    @Override
    public Map<String, String> getSuccessColumnsAgg() {
        return null;
    }

    @Override
    public Map<String, String> getSuccessColumnsRaw() {
        return null;
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

        parameters.put(NO_ERROR_SUBSCRIBERS_VARIABLE, NO_OF_TOTAL_ERR_SUBSCRIBERS);

        final String queryFragment = kpiUtilities.getQueryFromTemplate(NETWORK_EVENT_IMSI_CALC_VM, parameters);

        return queryFragment;
    }

    @Override
    public String getEventID() {
        return "15";
    }

    /**
     * @param kpiUtilities the kpiUtilities to set
     */
    public void setKpiUtilities(final KpiUtilities kpiUtilities) {
        this.kpiUtilities = kpiUtilities;
    }
}
