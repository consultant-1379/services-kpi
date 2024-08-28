/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi;

import java.util.Map;

public interface KPITemplate {
	void setKpiUtilities(final KpiUtilities kpiUtilities);
	Map<String, Object> getTemplateParameters();
	String getTemplate();
}
