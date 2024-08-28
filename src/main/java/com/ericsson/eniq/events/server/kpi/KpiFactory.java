/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import com.ericsson.eniq.events.server.kpi.impl.AttachSuccessRateKPI;
import com.ericsson.eniq.events.server.kpi.impl.BearerActivationSuccessRateKPI;
import com.ericsson.eniq.events.server.kpi.impl.IMSIKPI;
import com.ericsson.eniq.events.server.kpi.impl.InterTrackingAreaUpdateSuccessRateKPI;
import com.ericsson.eniq.events.server.kpi.impl.IntraTrackingAreaUpdateSuccessRateKPI;
import com.ericsson.eniq.events.server.kpi.impl.PDNConnectionSuccessRateKPI;
import com.ericsson.eniq.events.server.kpi.impl.PagingFailureRatioKPI;
import com.ericsson.eniq.events.server.kpi.impl.S1BasedHandoverNoSGWWithMMERelocationSuccessRateKPI;
import com.ericsson.eniq.events.server.kpi.impl.S1BasedHandoverSuccessRateKPI;
import com.ericsson.eniq.events.server.kpi.impl.S1BasedHandoverWithSGWAndWithMMERelocationSuccessRateKPI;
import com.ericsson.eniq.events.server.kpi.impl.S1BasedHandoverWithSGWAndWithoutMMERelocationSuccessRateKPI;
import com.ericsson.eniq.events.server.kpi.impl.S1BasedHandoverWithoutSGWAndWithoutMMERelocationSuccessRateKPI;
import com.ericsson.eniq.events.server.kpi.impl.UEInitiatedServiceRequestFailureRatioKPI;
import com.ericsson.eniq.events.server.kpi.impl.X2BasedHandoverSuccessRateKPI;
import com.ericsson.eniq.events.server.kpi.impl.X2BasedHandoverWithSGWRelocationSuccessRateKPI;
import com.ericsson.eniq.events.server.kpi.impl.X2BasedHandoverWithoutSGWRelocationSuccessRateKPI;
import com.ericsson.eniq.events.server.kpi.mss.impl.CallForwardingDropRatio;
import com.ericsson.eniq.events.server.kpi.mss.impl.CallForwardingSuccessRatio;
import com.ericsson.eniq.events.server.kpi.mss.impl.LocationRequestsSuccessRatio;
import com.ericsson.eniq.events.server.kpi.mss.impl.MsOriginatingCallAttemptsIntensity;
import com.ericsson.eniq.events.server.kpi.mss.impl.MsOriginatingCallCompletionRatio;
import com.ericsson.eniq.events.server.kpi.mss.impl.MsOriginatingCallDropIntensity;
import com.ericsson.eniq.events.server.kpi.mss.impl.MsOriginatingCallDropRatio;
import com.ericsson.eniq.events.server.kpi.mss.impl.MsOriginatingEmergencyCallCompletionRatio;
import com.ericsson.eniq.events.server.kpi.mss.impl.MsOriginatingEmergencyCallDropRatio;
import com.ericsson.eniq.events.server.kpi.mss.impl.MsTerminatingCallAttemptsIntensity;
import com.ericsson.eniq.events.server.kpi.mss.impl.MsTerminatingCallCompletionRatio;
import com.ericsson.eniq.events.server.kpi.mss.impl.MsTerminatingCallDropIntensity;
import com.ericsson.eniq.events.server.kpi.mss.impl.MsTerminatingCallDropRatio;
import com.ericsson.eniq.events.server.kpi.mss.impl.RoamingCallDropRatio;
import com.ericsson.eniq.events.server.kpi.mss.impl.RoamingCallSuccessRatio;
import com.ericsson.eniq.events.server.kpi.mss.impl.SmsOriginatingSuccessRatio;
import com.ericsson.eniq.events.server.kpi.mss.impl.SmsTerminatingSuccessRatio;
import com.ericsson.eniq.events.server.kpi.sgeh.impl.SGEHAttachSuccessRateKPI;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.WRITE)
public class KpiFactory {

    @EJB
    private KpiUtilities kpiUtilities;

    @EJB(beanName = "SGEHAttachSuccessRateKPI")
    private KPI sgehAttachSuccessRateKPI;

    @EJB(beanName = "PDPContextActivationSuccessRateKPI")
    private KPI pdpContextActivationSuccessRateKPI;

    @EJB(beanName = "SGEHPagingFailureRatioKPI")
    private KPI sgehPagingFailureRatioKPI;

    @EJB(beanName = "PDPContextCutoffRatioKPI")
    private KPI pdpContextCutoffRatioKPI;

    @Lock(LockType.READ)
    public List<KPI> getAllKPIs() {
        final List<KPI> allKPIS = new ArrayList<KPI>();
        allKPIS.add(new AttachSuccessRateKPI(kpiUtilities));
        allKPIS.add(new PDNConnectionSuccessRateKPI(kpiUtilities));
        allKPIS.add(new IMSIKPI(kpiUtilities));
        allKPIS.add(new BearerActivationSuccessRateKPI(kpiUtilities));
        allKPIS.add(new UEInitiatedServiceRequestFailureRatioKPI(kpiUtilities));
        allKPIS.add(new PagingFailureRatioKPI(kpiUtilities));
        allKPIS.add(new IntraTrackingAreaUpdateSuccessRateKPI(kpiUtilities));
        allKPIS.add(new InterTrackingAreaUpdateSuccessRateKPI(kpiUtilities));
        allKPIS.add(new X2BasedHandoverSuccessRateKPI(kpiUtilities));
        allKPIS.add(new X2BasedHandoverWithoutSGWRelocationSuccessRateKPI(kpiUtilities));
        allKPIS.add(new X2BasedHandoverWithSGWRelocationSuccessRateKPI(kpiUtilities));
        allKPIS.add(new S1BasedHandoverSuccessRateKPI(kpiUtilities));
        allKPIS.add(new S1BasedHandoverNoSGWWithMMERelocationSuccessRateKPI(kpiUtilities));
        allKPIS.add(new S1BasedHandoverWithoutSGWAndWithoutMMERelocationSuccessRateKPI(kpiUtilities));
        allKPIS.add(new S1BasedHandoverWithSGWAndWithoutMMERelocationSuccessRateKPI(kpiUtilities));
        allKPIS.add(new S1BasedHandoverWithSGWAndWithMMERelocationSuccessRateKPI(kpiUtilities));

        return allKPIS;
    }

    @Lock(LockType.READ)
    public List<KPI> getCoreNetworkKPIs() {
        final List<KPI> coreNetworkKPIs = new ArrayList<KPI>();
        coreNetworkKPIs.add(sgehAttachSuccessRateKPI);
        coreNetworkKPIs.add(pdpContextActivationSuccessRateKPI);
        coreNetworkKPIs.add(sgehPagingFailureRatioKPI);
        coreNetworkKPIs.add(pdpContextCutoffRatioKPI);
        return coreNetworkKPIs;
    }

    @Lock(LockType.READ)
    public List<MssKPI> getAllMssVoiceNonEmergencyKPIs() {
        final List<MssKPI> allMssVoiceNonEmergencyKPIS = new ArrayList<MssKPI>();
        allMssVoiceNonEmergencyKPIS.add(new MsOriginatingCallCompletionRatio(kpiUtilities));
        allMssVoiceNonEmergencyKPIS.add(new MsTerminatingCallCompletionRatio(kpiUtilities));
        allMssVoiceNonEmergencyKPIS.add(new MsOriginatingCallDropRatio(kpiUtilities));
        allMssVoiceNonEmergencyKPIS.add(new MsTerminatingCallDropRatio(kpiUtilities));
        allMssVoiceNonEmergencyKPIS.add(new MsOriginatingCallAttemptsIntensity(kpiUtilities));
        allMssVoiceNonEmergencyKPIS.add(new MsTerminatingCallAttemptsIntensity(kpiUtilities));
        allMssVoiceNonEmergencyKPIS.add(new MsOriginatingCallDropIntensity(kpiUtilities));
        allMssVoiceNonEmergencyKPIS.add(new MsTerminatingCallDropIntensity(kpiUtilities));
        allMssVoiceNonEmergencyKPIS.add(new CallForwardingSuccessRatio(kpiUtilities));
        allMssVoiceNonEmergencyKPIS.add(new RoamingCallSuccessRatio(kpiUtilities));
        allMssVoiceNonEmergencyKPIS.add(new CallForwardingDropRatio(kpiUtilities));
        allMssVoiceNonEmergencyKPIS.add(new RoamingCallDropRatio(kpiUtilities));
        return allMssVoiceNonEmergencyKPIS;
    }

    @Lock(LockType.READ)
    public List<MssKPI> getAllMssVoiceEmergencyKPIs() {
        final List<MssKPI> allMssVoiceEmergencyKPIS = new ArrayList<MssKPI>();
        allMssVoiceEmergencyKPIS.add(new MsOriginatingEmergencyCallCompletionRatio(kpiUtilities));
        allMssVoiceEmergencyKPIS.add(new MsOriginatingEmergencyCallDropRatio(kpiUtilities));
        return allMssVoiceEmergencyKPIS;
    }

    @Lock(LockType.READ)
    public List<MssKPI> getAllMssSmsKPIs() {
        final List<MssKPI> allMssSmsKPIS = new ArrayList<MssKPI>();
        allMssSmsKPIS.add(new SmsOriginatingSuccessRatio(kpiUtilities));
        allMssSmsKPIS.add(new SmsTerminatingSuccessRatio(kpiUtilities));
        return allMssSmsKPIS;
    }

    @Lock(LockType.READ)
    public List<MssKPI> getAllMssLocationServiceKPIs() {
        final List<MssKPI> allMssSmsKPIS = new ArrayList<MssKPI>();
        allMssSmsKPIS.add(new LocationRequestsSuccessRatio(kpiUtilities));
        return allMssSmsKPIS;
    }

    /**
     * @param kpiUtilities the kpiUtilities to set
     */
    public void setKpiUtilities(final KpiUtilities kpiUtilities) {
        this.kpiUtilities = kpiUtilities;
    }

    public void setSgehAttachSuccessRateKPI(final SGEHAttachSuccessRateKPI sgehAttachSuccessRateKPI) {
        this.sgehAttachSuccessRateKPI = sgehAttachSuccessRateKPI;
    }
}
