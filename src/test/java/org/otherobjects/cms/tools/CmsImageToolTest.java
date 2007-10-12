package org.otherobjects.cms.tools;

import java.io.File;

import org.otherobjects.cms.model.CmsImage;
import org.otherobjects.cms.model.CmsImageDao;
import org.otherobjects.cms.model.CmsImageSize;
import org.otherobjects.cms.test.BaseJcrTestCase;

public class CmsImageToolTest extends BaseJcrTestCase
{
    private CmsImageDao cmsImageDao;
    private CmsImage sampleImage;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        this.cmsImageDao = (CmsImageDao) getApplicationContext().getBean("cmsImageDao");
        File dog = new File("src/test/java/org/otherobjects/cms/util/dog.jpg");
        CmsImage im1 = new CmsImage();
        im1.setPath("/");
        im1.setCode(dog.getName());
        im1.setNewFile(dog);
        this.sampleImage = (CmsImage) this.cmsImageDao.save(im1);
    }

    public void testGetOriginal()
    {
        CmsImageTool imageTool = new CmsImageTool();
        CmsImageSize original = imageTool.getOriginal(this.sampleImage);
        assertEquals(new Long(800), original.getWidth());
        assertEquals(new Long(600), original.getHeight());
        File file = original.getDataFile().getFile();
        assertNotNull(file);
        assertTrue(file.getAbsolutePath().contains(CmsImage.DATA_FILE_COLLECTION_NAME));
        assertTrue(file.getAbsolutePath().contains(CmsImage.ORIGINALS_PATH));

        // Check that getting the same size as the original return original
        original = imageTool.getSize(this.sampleImage, 800, 600);
        file = original.getDataFile().getFile();
        assertNotNull(file);
        assertTrue(file.getAbsolutePath().contains(CmsImage.DATA_FILE_COLLECTION_NAME));
        assertTrue(file.getAbsolutePath().contains(CmsImage.ORIGINALS_PATH));

    }

    public void testGetThumbnail()
    {
        CmsImageTool imageTool = new CmsImageTool();
        CmsImageSize thumbnail = imageTool.getThumbnail(this.sampleImage);
        assertEquals(new Long(100), thumbnail.getWidth());
        assertEquals(new Long(100), thumbnail.getHeight());
        File file = thumbnail.getDataFile().getFile();
        assertNotNull(file);
        assertTrue(file.getAbsolutePath().contains("100x100"));

        // Check for redundant resizing
        long created = file.lastModified();
        thumbnail = imageTool.getThumbnail(this.sampleImage);
        file = thumbnail.getDataFile().getFile();
        long lastModified = file.lastModified();
        assertEquals(created, lastModified);
    }

    public void testGetSize()
    {
        CmsImageTool imageTool = new CmsImageTool();

        // Test fixed width
        CmsImageSize sizeW = imageTool.getSize(this.sampleImage, 400);
        assertEquals(new Long(400), sizeW.getWidth());
        assertEquals(new Long(300), sizeW.getHeight());
        File file = sizeW.getDataFile().getFile();
        assertNotNull(file);
        assertTrue(file.getAbsolutePath().contains("/400x300/"));
        assertNotNull(sizeW.getDataFile().getExternalUrl());

        CmsImageSize sizeH = imageTool.getSize(this.sampleImage, null, 200);
        assertEquals(new Long(267), sizeH.getWidth());
        assertEquals(new Long(200), sizeH.getHeight());
        file = sizeH.getDataFile().getFile();
        assertNotNull(file);
        assertTrue(file.getAbsolutePath().contains("/267x200/"));

        CmsImageSize sizeWH = imageTool.getSize(this.sampleImage, 150, 150);
        assertEquals(new Long(150), sizeWH.getWidth());
        assertEquals(new Long(150), sizeWH.getHeight());
        file = sizeWH.getDataFile().getFile();
        assertNotNull(file);
        assertTrue(file.getAbsolutePath().contains("/150x150/"));

        CmsImageSize sizeWHC = imageTool.getSize(this.sampleImage, 133, 133, "#FF0000");
        assertEquals(new Long(133), sizeWHC.getWidth());
        assertEquals(new Long(133), sizeWHC.getHeight());
        file = sizeWHC.getDataFile().getFile();
        assertNotNull(file);
        assertTrue(file.getAbsolutePath().contains("/133x133#FF0000/"));
    }

}
