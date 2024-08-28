/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi.mss.impl;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;

import static com.ericsson.eniq.events.server.common.ApplicationConstants.*;

import com.ericsson.eniq.events.server.kpi.KPIConstants;
import com.ericsson.eniq.events.server.kpi.KpiUtilities;
import com.ericsson.eniq.events.server.kpi.MssKPI;
import com.ericsson.eniq.events.server.templates.exception.ResourceInitializationException;

public class MsOriginatingCallDropRatio implements MssKPI {

	@EJB
	private KpiUtilities kpiUtilities;

	private final static String KPI_NAME = "MS Originating call drop ratio";

	/**
	 * @param kpiUtils
	 */
	public MsOriginatingCallDropRatio(final KpiUtilities kpiUtils) {
		this.kpiUtilities = kpiUtils;
	}

	@Override
	public Map<String, String> getSuccessColumnsAgg() {
		final Map<String, String> successColumns = new HashMap<String, String>();
		successColumns.put(KPIConstants.NO_OF_SUCCESSES, KPIConstants.NO_OF_SUCCESSES);

		return successColumns;
	}

	@Override
	public Map<String, String> getSuccessColumnsRaw() {
		final Map<String, String> successColumns = new HashMap<String, String>();
		successColumns.put(KPIConstants.NO_OF_SUCCESSES, KPIConstants.NO_OF_SUCCESSES_RAW);

		return successColumns;
	}

	@Override
	public Map<String, String> getErrorColumnsAggForBlocked() {
		return null;
	}

	@Override
	public Map<String, String> getErrorColumnsAggForDropped() {
		final Map<String, String> errorColumns = new HashMap<String, String>();
		errorColumns.put(KPIConstants.NO_OF_DROPPED_CALLS, KPIConstants.NO_OF_ERRORS);
		return errorColumns;
	}
	
	@Override
	public Map<String, String> getErrorColumnsRawForBlocked() {
		return null;
	}

	@Override
	public Map<String, String> getErrorColumnsRawForDropped() {
		final Map<String, String> errorColumns = new HashMap<String, String>();
		errorColumns.put(KPIConstants.NO_OF_DROPPED_CALLS, KPIConstants.NO_OF_ERRORS_RAW);
		return errorColumns;
	}
	
	@Override
	public String getKPICalculation(final Map<String, Object> parameters)
	throws ResourceInitializationException {
		
		parameters.put(KPIConstants.EVENT_ID_VARIABLE, getEventID());
		parameters.put(KPIConstants.NO_OF_SUCCESSES_VARIABLE, KPIConstants.NO_OF_SUCCESSES);
		parameters.put(NO_OF_DROPPED_CALLS_VAR, KPIConstants.NO_OF_DROPPED_CALLS);
		parameters.put(KPIConstants.KPI_NAME_VARIABLE,KPI_NAME);
		final String queryFragment = kpiUtilities.getQueryFromTemplate(KPIConstants.MSS_CALL_DROP_RATIO_VM,
				parameters);

		return queryFragment;
	}

	@Override
	public String getEventID() {
		// TODO Auto-generated method stub
		return kpiUtilities.getMssEventID(MS_ORIGINATING);
	}
	
    /**
     * @param kpiUtilities the kpiUtilities to set
     */
    public void setKpiUtilities(final KpiUtilities kpiUtilities) {
        this.kpiUtilities = kpiUtilities;
    }
}
