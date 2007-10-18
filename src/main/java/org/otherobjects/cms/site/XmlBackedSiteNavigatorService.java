package org.otherobjects.cms.site;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

public class XmlBackedSiteNavigatorService implements SiteNavigatorService, InitializingBean
{
    private Resource xmlConfig;
    private Document siteDoc;

    public void setXmlConfig(Resource xmlConfig)
    {
        this.xmlConfig = xmlConfig;
    }

    public List<SiteItem> getSiteItems(SiteItem siteItem, int parentLevel)
    {
        if (siteItem == null || parentLevel == 0)
        {
            if (parentLevel > 0)
                return null;
            else
                return getSiteItems(null); // return direct root children
        }

        int currentLevel = siteItem.getDepth();

        // currentlevel must not be more than one level up from parentLevel
        if (parentLevel - currentLevel > 1)
            return null;

        if (currentLevel == parentLevel)
        {
            if (!siteItem.isFolder()) // if currentlevel is parent of parentLevel it must be a folder for us to be able to return something
                return null;
            else
                return getSiteItems(siteItem);
        }

        //ok currentlevel not above or the same as the requested parentlevel so it is below the requested parentlevel
        // which means we need to walk up the tree from currentlevel until we reach parentlevel and then get the children
        while (siteItem.getDepth() != parentLevel)
        {
            siteItem = new XmlSiteItem(((XmlSiteItem) siteItem).getBackingElement().getParent());
        }

        return getSiteItems(siteItem);
    }

    public List<SiteItem> getSiteItems(SiteItem siteItem)
    {
        if (siteItem == null)
            return getChildElementsAsSiteItems(siteDoc.getRootElement());
        else if (((XmlSiteItem) siteItem).isFolder())
            return getChildElementsAsSiteItems(((XmlSiteItem) siteItem).getBackingElement());
        else
            return null;
    }

    private List<SiteItem> getChildElementsAsSiteItems(Element parent)
    {
        List<SiteItem> result = new ArrayList<SiteItem>();
        for (Element siteItem : (List<Element>) parent.elements())
        {
            result.add(new XmlSiteItem(siteItem));
        }
        return result;
    }

    private int calcDepth(String href)
    {
        int count = 0;
        int startIndex = 0;

        while ((startIndex = href.indexOf('/', startIndex)) > -1)
        {
            count++;
        }
        return count - 1;
    }

    public void afterPropertiesSet() throws Exception
    {
        Assert.notNull(xmlConfig);
        SAXReader reader = new SAXReader();
        siteDoc = reader.read(xmlConfig.getURL());
    }

    public class XmlSiteItem implements SiteItem
    {
        private Element backingElement;
        private int depth = 0;

        public XmlSiteItem(Element siteItem)
        {
            this.backingElement = siteItem;
        }

        public Element getBackingElement()
        {
            return backingElement;
        }

        public void setBackingElement(Element backingElement)
        {
            this.backingElement = backingElement;
        }

        public String getHref()
        {
            return backingElement.getUniquePath();
        }

        public String getTitle()
        {
            return backingElement.attributeValue("title");
        }

        public boolean isFolder()
        {
            return backingElement.hasContent();
        }

        public int getDepth()
        {
            if (depth != 0)
                return depth;
            else
            {
                int count = 0;
                int startIndex = 0;

                while ((startIndex = getHref().indexOf('/', startIndex)) > -1)
                {
                    startIndex++;
                    count++;
                }
                depth = count - 1;
                return depth;
            }
        }

    }

}
