/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2010 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.server.kpi;

import java.util.Map;

import com.ericsson.eniq.events.server.templates.exception.ResourceInitializationException;

public interface KPI {
    /**
     * These are the columns needed from the aggregated success tables 
     * @return A map of strings with the key = alias and value = column name 
     */
    Map<String, String> getSuccessColumnsAgg();

    /**
     * These are the columns needed from the raw success tables 
     * @return A map of strings with the key = alias and value = column name 
     */
    Map<String, String> getSuccessColumnsRaw();


    /**
     * These are the columns needed from the aggregated error tables 
     * @return A map of strings with the key = alias and value = column name 
     */
    Map<String, String>  getErrorColumnsAgg();

    /**
     * These are the columns needed from the raw error tables 
     * @return A map of strings with the key = alias and value = column name 
     */
    Map<String, String> getErrorColumnsRaw();


    /**
     * This is the calculation string used the the final KPI query.
     * You need to make sure that any column alias or table alias that is used in this calculation is also in the 
     * correct getColumns method  
     * @return A string with the calculation query  
     */
    String getKPICalculation(Map<String, Object> parameters) throws ResourceInitializationException;

    /**
     * Return the event id for this KPI.
     * @return String representation of the Event ID
     */
    String getEventID();
}
