package org.otherobjects.cms.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores performance related information.
 * 
 * @author rich
 */
public class PerformanceInfoImpl implements PerformanceInfo
{
    private final List<PerformanceEvent> events = new ArrayList<PerformanceEvent>();

    /* (non-Javadoc)
     * @see org.otherobjects.cms.util.PerformanceInfo#registerEvent(java.lang.String, java.lang.String, long)
     */
    public void registerEvent(String type, String details, long time)
    {
        events.add(new PerformanceEvent(type,details,time));
    }

    /* (non-Javadoc)
     * @see org.otherobjects.cms.util.PerformanceInfo#getEvents()
     */
    public List<PerformanceEvent> getEvents()
    {
        return events;
    }

    /**
     * Represents a single time event.
     * 
     * @author rich
     */
    public class PerformanceEvent
    {
        private String type;
        private String details;
        private long time;

        public PerformanceEvent(String type, String details, long time)
        {
            this.type = type;
            this.details = details;
            this.time = time;
        }

        public String getType()
        {
            return type;
        }

        public void setType(String type)
        {
            this.type = type;
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
