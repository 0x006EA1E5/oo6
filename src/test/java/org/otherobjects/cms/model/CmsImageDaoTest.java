package org.otherobjects.cms.model;

import java.io.File;

import org.otherobjects.cms.jcr.BaseJcrTestCase;


public class CmsImageDaoTest extends BaseJcrTestCase
{

    private final DataFileDao dataFileDao = new DataFileDaoFileSystem();
    private CmsImageDao cmsImageDao;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        this.cmsImageDao = (CmsImageDao) this.applicationContext.getBean("cmsImageDao");
    }

    public void testSave()
    {
        File dog = new File("src/test/java/org/otherobjects/cms/util/dog.jpg");

        CmsImage im1 = new CmsImage();
        im1.setPath("/");
        im1.setCode(dog.getName());
        im1.setNewFile(dog);

        CmsImage im1saved = (CmsImage) this.cmsImageDao.save(im1);
        
        new CmsImageDaoImpl();
        assertNotNull(im1saved.getId());
        assertNotNull(im1saved.getOriginalFileId());
        assertNull(im1saved.getNewFile());
        assertEquals(new Long(800), im1saved.getOriginalWidth());
        assertEquals(new Long(600), im1saved.getOriginalHeight());

        assertTrue(this.dataFileDao.exists(im1saved.getOriginalFileId()));

    }
}
