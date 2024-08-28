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
public class SGEHPagingFailureRatioKPI implements KPI {

    @EJB
    KpiUtilities kpiUtilities;

    private Map<String, String> successColumnsAgg = null;

    private static final Map<String, String> successColumnsRaw = null;

    private Map<String, String> errorColumnsAgg = null;

    private static final Map<String, String> errorColumnsRaw = null;

    private String eventId;

    @PostConstruct
    public void init() {
        // Populate success aggregation columns
        successColumnsAgg = new HashMap<String, String>();
        successColumnsAgg.put(NO_OF_SUCCESSES, NO_OF_SUCCESSES_AGG);
        successColumnsAgg.put(NO_OF_PAGING_ATTEMPTS, NO_OF_PAGING_ATTEMPTS_AGG);

        // Populate error aggregation columns
        errorColumnsAgg = new HashMap<String, String>();
        errorColumnsAgg.put(NO_OF_ERRORS, NO_OF_ERRORS_AGG);
        errorColumnsAgg.put(NO_OF_PAGING_ATTEMPTS, NO_OF_PAGING_ATTEMPTS_AGG);

        // Populate eventId field
        eventId = kpiUtilities.getEventID(ApplicationConstants.ACTIVATE);
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

        final String condition = " > 0";
        parameters.put(EVENT_ID_VARIABLE, getEventID());
        parameters.put(NO_OF_SUCCESSES_VARIABLE, NO_OF_SUCCESSES);
        parameters.put(NO_OF_ERRORS_VARIABLE, NO_OF_ERRORS);
        parameters.put(CONDITION_COLUMN, NO_OF_PAGING_ATTEMPTS);
        parameters.put(CONDITION, condition);
        parameters.put(KPI_NAME_VARIABLE, "Paging Failure Ratio");

        final String queryFragment = kpiUtilities.getQueryFromTemplate(
                NETWORK_EVENT_FAILURE_RATIO_EXTRA_CONDITION_CALC_VM, parameters);

        return queryFragment;
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
