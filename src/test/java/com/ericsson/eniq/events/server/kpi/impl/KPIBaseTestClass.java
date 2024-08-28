/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi.impl;

import com.ericsson.eniq.events.server.kpi.KPI;
import com.ericsson.eniq.events.server.kpi.KpiFactory;
import com.ericsson.eniq.events.server.kpi.KpiUtilities;
import com.ericsson.eniq.events.server.templates.utils.TemplateUtils;
import org.junit.Ignore;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import javax.ejb.EJB;

// specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations = {"classpath:com/ericsson/eniq/events/server/kpi/impl/KPIBaseTestClass-context.xml"})
@Ignore
public class KPIBaseTestClass extends AbstractJUnit4SpringContextTests {

    @EJB
    protected TemplateUtils templateUtils;

    @EJB
    protected KpiUtilities kpiUtilities;

    @EJB
    protected KpiFactory kpiFactory;

    @EJB
    protected KPI sgehAttachSuccessRateKPI;

}
