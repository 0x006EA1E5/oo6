package org.otherobjects.cms.site;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SiteTrailImpl implements SiteTrail
{

    /**
     * list of siteItems making up the trail. index 0 is siteItem for which this is the trail itself, last index is topmost parent
     */
    private List<SiteItem> trail;
    private List<SiteItem> reverseTrail = null;

    public SiteTrailImpl(List<SiteItem> trail)
    {
        this.trail = trail;
    }

    public Iterator<SiteItem> getCrumbs()
    {
        return getReverseTrail().iterator();
    }

    public String getFullTitle(String separator)
    {
        StringBuffer buf = new StringBuffer();
        for (Iterator<SiteItem> it = getReverseTrail().iterator(); it.hasNext();)
        {
            buf.append(it.next().getNavigationLabel());
            if (it.hasNext())
                buf.append(separator);
        }
        return buf.toString();
    }

    public Iterator<SiteItem> getTrailParts()
    {
        return trail.iterator();
    }

    private List<SiteItem> getReverseTrail()
    {
        if (reverseTrail == null)
        {
            reverseTrail = new ArrayList<SiteItem>();
            reverseTrail.addAll(trail);
            Collections.reverse(reverseTrail);
        }
        return reverseTrail;
    }
}
