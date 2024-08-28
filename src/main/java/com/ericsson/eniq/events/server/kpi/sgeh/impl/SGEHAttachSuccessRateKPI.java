/*
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.server.kpi.sgeh.impl;

import static com.ericsson.eniq.events.server.kpi.KPIConstants.*;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import com.ericsson.eniq.events.server.common.ApplicationConstants;
import com.ericsson.eniq.events.server.kpi.KPI;
import com.ericsson.eniq.events.server.kpi.KpiUtilities;
import com.ericsson.eniq.events.server.templates.exception.ResourceInitializationException;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Local(KPI.class)
@Lock(LockType.WRITE)
public class SGEHAttachSuccessRateKPI implements KPI {

    @EJB
    private KpiUtilities kpiUtilities;

    private Map<String, String> successColumnsAgg = null;

    private Map<String, String> successColumnsRaw = null;

    private Map<String, String> errorColumnsAgg = null;

    private static final Map<String, String> errorColumnsRaw = null;

    private String eventId;

    @PostConstruct
    public void init() {
        // Populate success aggregation columns
        successColumnsAgg = new HashMap<String, String>();
        successColumnsAgg.put(NO_OF_SUCCESSES, NO_OF_SUCCESSES_AGG);

        // Populate success raw columns
        successColumnsRaw = new HashMap<String, String>();
        successColumnsRaw.put(NO_OF_SUCCESSES, NO_OF_SUCCESSES_RAW);

        // Populate error aggregation columns
        errorColumnsAgg = new HashMap<String, String>();
        errorColumnsAgg.put(NO_OF_ERRORS, NO_OF_ERRORS_AGG);

        // Populate error raw columns
        // No columns to be added

        // Populate eventId field
        eventId = kpiUtilities.getEventID(ApplicationConstants.ATTACH);
    }

    @PreDestroy
    public void beanDestroy() {
        successColumnsAgg = null;
        errorColumnsAgg = null;
        eventId = null;
    }

    @Override
    @Lock(LockType.READ)
    public Map<String, String> getSuccessColumnsAgg() {
        return successColumnsAgg;
    }

    @Override
    @Lock(LockType.READ)
    public Map<String, String> getSuccessColumnsRaw() {
        return successColumnsRaw;
    }

    @Override
    @Lock(LockType.READ)
    public Map<String, String> getErrorColumnsAgg() {
        return errorColumnsAgg;
    }

    @Override
    @Lock(LockType.READ)
    public Map<String, String> getErrorColumnsRaw() {
        return errorColumnsRaw;
    }

    @Override
    public String getKPICalculation(final Map<String, Object> parameters) throws ResourceInitializationException {

        parameters.put(EVENT_ID_VARIABLE, getEventID());
        parameters.put(NO_OF_SUCCESSES_VARIABLE, NO_OF_SUCCESSES);
        parameters.put(NO_OF_ERRORS_VARIABLE, NO_OF_ERRORS);
        parameters.put(KPI_NAME_VARIABLE, ATTACH_SUCCESS_RATE);

        return kpiUtilities.getQueryFromTemplate(NETWORK_EVENT_RATE_CALC_VM, parameters);
    }

    @Override
    @Lock(LockType.READ)
    public String getEventID() {
        return eventId;
    }

    /**
     * @param kpiUtilities the kpiUtilities to set
     */
    public void setKpiUtilities(final KpiUtilities kpiUtilities) {
        this.kpiUtilities = kpiUtilities;
    }
}
