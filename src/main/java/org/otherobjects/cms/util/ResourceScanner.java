package org.otherobjects.cms.util;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.TemplateBlock;
import org.otherobjects.cms.model.TemplateLayout;

/**
 * Scans resources on disk and updates their meta data in the data store.
 * 
 * <p>Currently, this only supports templates.
 * 
 * <p>TODO This may be better implemented in Groovy?
 * 
 * @author rich
 *
 */
public class ResourceScanner
{
    @Resource
    private DaoService daoService;

    @Resource
    private OoResourceLoader ooResourceLoader;

    public void updateResources()
    {
        try
        {
            // TODO Make missing items from disk deleted/inactive/warned`
            
            // Process blocks
            List<OoResource> resources = ooResourceLoader.getResources("/site/templates/blocks/");
            String path = "/designer/blocks/";
            for (OoResource r : resources)
            {
                UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");
                String code = StringUtils.substringBefore(r.getFilename(), ".");
                TemplateBlock block = (TemplateBlock) dao.getByPath(path + code);

                if (block == null)
                {
                    block = new TemplateBlock();
                    block.setPath(path);
                }
                block.setCode(code);
                block.setLabel(code);
                if (r.getMetaData() != null)
                {
                    if (r.getMetaData().getLabel() != null)
                        block.setLabel(r.getMetaData().getLabel());
                    else
                        block.setDescription(r.getMetaData().getDescription());
                }
                dao.save(block);
                dao.publish(block, null);
            }

            // Process layouts
            resources = ooResourceLoader.getResources("/site/templates/layouts/");
            path = "/designer/layouts/";
            for (OoResource r : resources)
            {
                UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");
                String code = StringUtils.substringBefore(r.getFilename(), ".");
                TemplateLayout layout = (TemplateLayout) dao.getByPath(path + code);

                if (layout == null)
                {
                    layout = new TemplateLayout();
                    layout.setPath(path);
                }
                layout.setCode(code);
                layout.setLabel(code);
                if (r.getMetaData() != null)
                {
                    if (r.getMetaData().getLabel() != null)
                        layout.setLabel(r.getMetaData().getLabel());
                    else
                        layout.setDescription(r.getMetaData().getDescription());
                }
                dao.save(layout);
                dao.publish(layout, null);
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
