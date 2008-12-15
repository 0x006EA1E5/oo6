package org.otherobjects.cms.monitoring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Stores performance related information.
 * 
 * @author rich
 */
@Component("performanceInfo")
@Scope("request")
public class PerformanceInfoImpl implements PerformanceInfo
{
    private final List<PerformanceEvent> events = new ArrayList<PerformanceEvent>();

    public PerformanceInfoImpl()
    {
        System.err.println("Creating Performance Info");
    }
    
    public void registerEvent(String details, long time)
    {
        events.add(new PerformanceEvent(details, time));
    }

    public List<PerformanceEvent> getEvents()
    {
        return events;
    }

    /**
     * Represents a single timed event.
     * 
     * @author rich
     */
    public class PerformanceEvent
    {
        private String details;
        private long time;

        public PerformanceEvent(String details, long time)
        {
            this.details = details;
            this.time = time;
        }

        public String getDetails()
        {
            return details;
        }

        public void setDetails(String details)
        {
            this.details = details;
        }

        public long getTime()
        {
            return this.time;
        }

        public void setTime(long time)
        {
            this.time = time;
        }
    }
}
