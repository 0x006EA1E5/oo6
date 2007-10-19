package org.otherobjects.cms.model;

import java.util.Map;

import junit.framework.TestCase;

public class SyndicationFeedResourceTest extends TestCase
{
    public void testgetMappings()
    {
        SyndicationFeedResource sfr = new SyndicationFeedResource();
        sfr.setMapping("title:testtitle\ndescription:desc link:linkprop");
        Map<String, String> mappings = sfr.getMappingsMap();
        assertEquals(3, mappings.size());
        assertEquals("testtitle", mappings.get("title"));
        assertEquals("desc", mappings.get("description"));
        assertEquals("linkprop", mappings.get("link"));
    }
}
