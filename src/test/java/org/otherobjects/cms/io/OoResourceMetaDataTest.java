package org.otherobjects.cms.io;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

public class OoResourceMetaDataTest extends TestCase
{

    public static final String TEST_JSON_METADATA = "{\n\"metaData\":{\"author\":\"author value\",\"creationDate\":1199142000000,"
            + "\"description\":\"description value\",\"modificationTimestamp\":1199142000000,\"title\":\"title value\",\"userdId\":5}\n}";

    public void testSerialise() throws ParseException
    {
        OoResourceMetaData md = new OoResourceMetaData();
        md.setTitle("title value");
        md.setDescription("description value");
        md.setAuthor("author value");
        md.setCreationDate(new SimpleDateFormat("dd MM yyyy").parse("01 01 2008"));
        md.setModificationTimestamp(new SimpleDateFormat("dd MM yyyy").parse("01 01 2008"));
        md.setUserdId(new Long(5));

        System.out.println(md.toString());

    }

    public void testDeserialize()
    {
        String json = TEST_JSON_METADATA;

        XStream xstream = new XStream(new JettisonMappedXmlDriver());
        xstream.registerConverter(new JsonXstreamDateConverter());
        xstream.alias("metaData", OoResourceMetaData.class);
        OoResourceMetaData ooResourceMetaData = (OoResourceMetaData) xstream.fromXML(json);

        System.out.println(ooResourceMetaData.getDescription());
        System.out.println(ooResourceMetaData.getCreationDate());
    }
}
