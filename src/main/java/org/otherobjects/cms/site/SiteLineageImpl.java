package org.otherobjects.cms.site;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class SiteLineageImpl implements SiteLineage
{

    /**
     * list of siteItems making up the lineage. index 0 is siteItem for which this is the lineage itself, last index is topmost parent
     */
    private List<SiteItem> lineage;
    private List<SiteItem> reverseLineage = null;

    public SiteLineageImpl(List<SiteItem> lineage)
    {
        this.lineage = lineage;
    }

    public Iterator<SiteItem> getCrumbs()
    {
        return getReverseLineage().iterator();
    }

    public String getFullTitle(String separator)
    {
        StringBuffer buf = new StringBuffer();
        for (SiteItem siteItem : getReverseLineage())
        {
            buf.append(siteItem.getNavigationLabel());
            buf.append(separator);
        }
        return buf.toString();
    }

    public Iterator<SiteItem> getLineageParts()
    {
        return lineage.iterator();
    }

    private List<SiteItem> getReverseLineage()
    {
        if (reverseLineage == null)
        {
            reverseLineage = new ArrayList<SiteItem>();
            for (ListIterator<SiteItem> reverseIterator = lineage.listIterator(lineage.size()); reverseIterator.hasPrevious();)
            {
                reverseLineage.add(reverseIterator.previous());
            }
        }
        return reverseLineage;
    }
}
