package org.otherobjects.cms.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.io.OoResourceMetaData;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.Template;
import org.otherobjects.cms.model.TemplateBlock;
import org.otherobjects.cms.model.TemplateBlockReference;
import org.otherobjects.cms.model.TemplateLayout;
import org.otherobjects.cms.model.TemplateRegion;
import org.otherobjects.cms.types.TypeService;

/**
 * Scans resources on disk and updates their meta data in the data store.
 * 
 * <p>Currently, this only supports templates.
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

    @Resource
    private TypeService typeService;

    public void updateResources()
    {
        if (true)
            return;

        try
        {
            // TODO Make missing items from disk deleted/inactive/warned
            UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");

            // Process blocks
            List<OoResource> resources = ooResourceLoader.getResources("/site/templates/blocks/");
            String jcrPath = "/designer/blocks/";

            for (OoResource r : resources)
            {
                String code = StringUtils.substringBefore(r.getFilename(), ".");
                TemplateBlock block = (TemplateBlock) dao.getByPath(jcrPath + code);

                OoResourceMetaData metaData = r.getMetaData();

                // Don't include private blocks
                if (metaData != null && metaData.getKeywords() != null && metaData.getKeywords().contains("private"))
                    continue;

                if (block == null)
                {
                    block = new TemplateBlock();
                    block.setPath(jcrPath);
                }
                block.setCode(code);
                block.setLabel(code);
                if (metaData != null)
                {
                    if (metaData.getTitle() != null)
                        block.setLabel(metaData.getTitle());
                    else
                        block.setDescription(metaData.getDescription());
                    if (metaData.getKeywords() != null && metaData.getKeywords().contains("global"))
                        block.setGlobal(true);

                    if (metaData.getTypeDef() != null)
                        typeService.registerType(metaData.getTypeDef());

                }

                dao.save(block);
                dao.publish(block, null);
            }

            // Process layouts
            resources = ooResourceLoader.getResources("/site/templates/layouts/");
            jcrPath = "/designer/layouts/";
            for (OoResource r : resources)
            {
                String code = StringUtils.substringBefore(r.getFilename(), ".");
                TemplateLayout layout = (TemplateLayout) dao.getByPath(jcrPath + code);

                if (layout == null)
                {
                    layout = new TemplateLayout();
                    layout.setPath(jcrPath);
                }
                layout.setCode(code);
                layout.setLabel(code);
                if (r.getMetaData() != null)
                {
                    if (r.getMetaData().getTitle() != null)
                        layout.setLabel(r.getMetaData().getTitle());
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

    /**
     * FIXME Just here till it gets a proper home
     */
    @SuppressWarnings("unchecked")
    public Template createTemplate(String code, String label, String layout, List<Map> regions)
    {
        UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");

        String path = "/designer/templates/" + code;
        Template template = (Template) dao.getByPath(path);

        if (template != null)
            return template;

        List<TemplateRegion> regionObjects = new ArrayList<TemplateRegion>();
        for (Map r : regions)
        {
            TemplateRegion tr = new TemplateRegion();
            tr.setCode((String) r.get("code"));
            List<TemplateBlockReference> blockReferenceObjects = new ArrayList<TemplateBlockReference>();
            for (String b : (List<String>) r.get("blocks"))
            {
                TemplateBlockReference br = new TemplateBlockReference();
                br.setBlock((TemplateBlock) dao.getByPath("/designer/blocks/" + b));
                blockReferenceObjects.add(br);
            }
            tr.setBlocks(blockReferenceObjects);
            regionObjects.add(tr);
        }

        template = new Template();
        template.setJcrPath(path);
        template.setLabel(label);
        template.setLayout((TemplateLayout) dao.getByPath("/designer/layouts/" + layout));
        template.setRegions(regionObjects);
        template = (Template) dao.save(template);
        dao.publish(template, null);
        return template;
    }

}
