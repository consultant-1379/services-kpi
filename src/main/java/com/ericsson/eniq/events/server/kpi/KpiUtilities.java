/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import com.ericsson.eniq.events.server.common.ApplicationConstants;
import com.ericsson.eniq.events.server.templates.exception.ResourceInitializationException;
import com.ericsson.eniq.events.server.templates.utils.TemplateUtils;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Lock(LockType.WRITE)
public class KpiUtilities {

    Map<String, String> cacheFor2G3G4G = new HashMap<String, String>();

    Map<String, String> cacheForMss = new HashMap<String, String>();

    @EJB
    TemplateUtils templateUtils;

    /**
     * Creates a String with all the elements of the supplied list seperated by the supplied delimiter.
     * @param list The list of Strings to convert to a String
     * @param delimiter what to put between the strings.
     * @return A String with each element of the list seperated by delimiter.
     */
    public String listToString(final List<String> list, final String delimiter) {
        if (list.isEmpty()) {
            return "";
        }

        final StringBuilder sb = new StringBuilder();

        final Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            final String string = it.next();
            sb.append(string);
            if (it.hasNext()) {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    /**
     * Uses TemplateUtils to generate a query based on the given Template file and the parameters 
     * @param templateFileName
     * @param parameters
     * @return a query that can be run into the database, or combined with other templates to build up a query
     */
    @Lock(LockType.READ)
    public String getQueryFromTemplate(final String templateName, final Map<String, Object> parameters)
            throws ResourceInitializationException {
        return templateUtils.getQueryFromTemplate(templateName, parameters);
    }

    /**
     * Get the Event_ID for the supplied event_type
     * @param eventType
     * @return the Event_ID for the Event_TYPE
     */
    @Lock(LockType.READ)
    public String getEventID(final String eventType) {
        return cacheFor2G3G4G.get(eventType);

    }

    /**
     * Get the Event_ID for the supplied event_type
     * @param eventType
     * @return the Event_ID for the Event_TYPE
     */
    @Lock(LockType.READ)
    public String getMssEventID(final String eventType) {
        return cacheForMss.get(eventType);

    }

    /**
     * Method "possibly" required by the SpringFramework to inject templateUtils into this class.
     * For sure this is needed for the Integration tests, but is it needed in production code.
     * @param templateUtilities
     */
    public void setTemplateUtils(final TemplateUtils templateUtilities) {
        templateUtils = templateUtilities;
    }

    /**
     * Another method that is required by the integration tests, but I wonder is it needed for production code.
     * Basically it sets up the cacheFor2G3G4G
     * Will be removed when we move to getting the Event_IDs from the Db
     */
    @PostConstruct
    public void applicationStartup() {
        populate2G3G4GEvents();
        populateMssEvents();
    }

    private void populate2G3G4GEvents() {
        cacheFor2G3G4G.put(ApplicationConstants.ATTACH, "0");
        cacheFor2G3G4G.put(ApplicationConstants.ACTIVATE, "1");
        cacheFor2G3G4G.put(ApplicationConstants.RAU, "2");
        cacheFor2G3G4G.put(ApplicationConstants.ISRAU, "3");
        cacheFor2G3G4G.put(ApplicationConstants.DEACTIVATE, "4");
        cacheFor2G3G4G.put(ApplicationConstants.L_ATTACH, "5");
        cacheFor2G3G4G.put(ApplicationConstants.L_DETACH, "6");
        cacheFor2G3G4G.put(ApplicationConstants.L_HANDOVER, "7");
        cacheFor2G3G4G.put(ApplicationConstants.L_TAU, "8");
        cacheFor2G3G4G.put(ApplicationConstants.L_DEDICATED_BEARER_ACTIVATE, "9");
        cacheFor2G3G4G.put(ApplicationConstants.L_DEDICATED_BEARER_DEACTIVATE, "10");
        cacheFor2G3G4G.put(ApplicationConstants.L_PDN_CONNECT, "11");
        cacheFor2G3G4G.put(ApplicationConstants.L_PDN_DISCONNECT, "12");
        cacheFor2G3G4G.put(ApplicationConstants.L_SERVICE_REQUEST, "13");
        cacheFor2G3G4G.put(ApplicationConstants.DETACH, "14");
        cacheFor2G3G4G.put(ApplicationConstants.SERVICE_REQUEST, "15");
    }

    private void populateMssEvents() {
        cacheForMss.put(ApplicationConstants.MS_ORIGINATING, "0");
        cacheForMss.put(ApplicationConstants.MS_TERMINATING, "1");
        cacheForMss.put(ApplicationConstants.CALL_FORWARDING, "2");
        cacheForMss.put(ApplicationConstants.ROAMING_CALL_FORWARDING, "3");
        cacheForMss.put(ApplicationConstants.MS_ORIGINATING_SMS_MSC, "4");
        cacheForMss.put(ApplicationConstants.MS_TERMINATING_SMS_MSC, "5");
        cacheForMss.put(ApplicationConstants.LOCATION_SERVICES, "6");
    }

    /**
     * Another method that is required by the integration tests, but I wonder is it needed for production code.
     * Basically it sets up the cacheFor2G3G4G
     * Will be removed when we move to getting the Event_IDs from the Db
     */
    @PreDestroy
    public void applicationShutdown() {
        cacheFor2G3G4G.clear();
        cacheForMss.clear();
    }

}
