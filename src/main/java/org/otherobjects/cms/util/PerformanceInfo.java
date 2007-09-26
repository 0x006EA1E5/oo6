package org.otherobjects.cms.util;

import java.util.List;

import org.otherobjects.cms.util.PerformanceInfoImpl.PerformanceEvent;

public interface PerformanceInfo
{

    public final static String JCR = "JCR";
    public final static String JDBC = "JDBC";
    public final static String XHR = "XHR";

    public abstract void registerEvent(String type, String details, long time);

    public abstract List<PerformanceEvent> getEvents();

}