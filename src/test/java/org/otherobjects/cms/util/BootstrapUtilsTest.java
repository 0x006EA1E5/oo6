package org.otherobjects.cms.util;

import java.io.FileInputStream;
import java.io.IOException;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.test.BaseJcrTestCase;

public class BootstrapUtilsTest extends BaseJcrTestCase
{
    protected DaoService daoService;
    protected BootstrapUtils bootstrapUtils;
    
    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }
    
    @Override
    protected void onSetUp() throws Exception
    {
        bootstrapUtils = new BootstrapUtils();
        bootstrapUtils.setDaoService(daoService);
    }

    public void testBootstrap() throws IOException
    {
        FileInputStream script = new FileInputStream("/Java/workspace/www.maureenmichaelson.com/bootstrap-data/setup.groovy");
        bootstrapUtils.runScript(script);
    }

}
