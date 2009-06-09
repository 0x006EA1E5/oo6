package org.otherobjects.cms.monitoring;

import java.util.List;

import org.otherobjects.cms.monitoring.PerformanceInfoImpl.PerformanceEvent;

public interface PerformanceInfo
{
    void registerEvent(String details, long time);

    List<PerformanceEvent> getEvents();
}
