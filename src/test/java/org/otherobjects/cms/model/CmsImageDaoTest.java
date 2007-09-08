package org.otherobjects.cms.model;

import java.io.File;

import org.otherobjects.cms.beans.BaseDynaNodeTest;

public class CmsImageDaoTest extends BaseDynaNodeTest
{

    private final DataFileDao dataFileDao = new DataFileDaoFileSystem();
    private CmsImageDao cmsImageDao;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        this.cmsImageDao = (CmsImageDao) getApplicationContext().getBean("cmsImageDao");
    }

    public void testSave()
    {
        File dog = new File("src/test/java/org/otherobjects/cms/util/dog.jpg");

        CmsImage im1 = new CmsImage();
        im1.setPath("/");
        im1.setCode(dog.getName());
        im1.setNewFile(dog);

        CmsImage im1saved = (CmsImage) this.cmsImageDao.save(im1);

        assertNotNull(im1saved.getId());
        assertNotNull(im1saved.getOriginalFileId());
        assertNull(im1saved.getNewFile());
        assertEquals(new Long(800), im1saved.getOriginalWidth());
        assertEquals(new Long(600), im1saved.getOriginalHeight());

        assertTrue(this.dataFileDao.exists(im1saved.getOriginalFileId()));

    }

    //    public void setCmsImageDao(CmsImageDao cmsImageDao)
    //    {
    //        this.cmsImageDao = cmsImageDao;
    //    }

    //    public void setDataFileDao(DataFileDao dataFileDao)
    //    {
    //        this.dataFileDao = dataFileDao;
    //    }

}
