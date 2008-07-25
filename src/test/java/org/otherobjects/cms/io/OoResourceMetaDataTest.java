package org.otherobjects.cms.io;

import java.text.ParseException;

import junit.framework.TestCase;

import org.json.JSONException;

public class OoResourceMetaDataTest extends TestCase
{

    public static final String TEST_JSON_METADATA = "{\n\"metaData\":{\"author\":\"author value\",\"creationDate\":1199142000000,\"description\":\"description value\",\"modificationTimestamp\":1199142000000,\"title\":\"title value\",\"userdId\":5}\n}";

    public void testSerialise() throws ParseException
    {
        OoResourceMetaData md = new OoResourceMetaData();

        md.setTitle("title value");
        md.setDescription("description value");
        md.setKeywords("keyword value");
        md.setAuthor("author value");

        String json = md.toJSON();
        System.out.println(json);

        assertTrue(json.contains("\"title\":\"title value\""));
        assertTrue(json.contains("\"description\":\"description value\""));
        assertTrue(json.contains("\"keywords\":\"keyword value\""));
        assertTrue(json.contains("\"author\":\"author value\""));

    }

    public void testDeserialize() throws JSONException
    {
        // FIXME Fix this test
//        OoResourceMetaData o = new OoResourceMetaData(TEST_JSON_METADATA);
//        assertEquals("title value", o.getTitle());
//        assertEquals("description value", o.getDescription());
//        assertEquals("keyword value", o.getKeywords());
//        assertEquals("author value", o.getAuthor());
//        
//        TypeDefImpl typeDef = o.getTypeDef();
//        assertEquals("ProductsBlock",typeDef.getName());
//        assertEquals("title",typeDef.getLabelProperty());
//        assertEquals("title",typeDef.getProperty("title").getName());
    }
}
