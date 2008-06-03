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
        // FIXME Need to test for rollback on partial failure eg bad file or bad data
        // FIXME Test overwriting images/sizes when replaced
        
        File dog = new File("src/test/java/org/otherobjects/cms/util/dog.jpg");

        CmsImage im1 = new CmsImage();
        im1.setPath("/");
        im1.setLabel("Test dog");
        im1.setNewFile(dog);
        im1.setOriginalFileName(dog.getName());

        CmsImage im1saved = (CmsImage) this.cmsImageDao.save(im1);
        
        new CmsImageDaoImpl();
        assertNotNull(im1saved.getId());
        assertNotNull(im1saved.getOriginalFileName());
        assertNull(im1saved.getNewFile());
        assertEquals(new Long(800), im1saved.getOriginalWidth());
        assertEquals(new Long(600), im1saved.getOriginalHeight());

        assertTrue(this.dataFileDao.exists(im1saved.getOriginalFileName()));

    }
}
