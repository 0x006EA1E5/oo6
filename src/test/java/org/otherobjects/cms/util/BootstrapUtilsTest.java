package org.otherobjects.cms.util;

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
        this.bootstrapUtils = new BootstrapUtils();
        this.bootstrapUtils.setDaoService(this.daoService);
    }

    public void testBootstrap() throws IOException
    {
        // FIXME Need a proper test here
        //        FileInputStream script = new FileInputStream("src/main/resources/otherobjects.resources/bootstrap-data/setup.script");
        //        this.bootstrapUtils.runScript(script);
    }

}
