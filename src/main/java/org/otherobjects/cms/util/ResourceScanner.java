package org.otherobjects.cms.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.io.OoResourceMetaData;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.Template;
import org.otherobjects.cms.model.TemplateBlock;
import org.otherobjects.cms.model.TemplateBlockReference;
import org.otherobjects.cms.model.TemplateLayout;
import org.otherobjects.cms.model.TemplateRegion;
import org.otherobjects.cms.types.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final Logger logger = LoggerFactory.getLogger(ResourceScanner.class);

    @Resource
    private DaoService daoService;

    @Resource
    private OoResourceLoader ooResourceLoader;

    @Resource
    private TypeService typeService;

    public void updateResources()
    {
        try
        {
            // TODO Make missing items from disk deleted/inactive/warned
            UniversalJcrDao dao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);

            // Process blocks
            List<OoResource> resources = this.ooResourceLoader.getResources("/site/templates/blocks/");
            String jcrPath = "/designer/blocks/";

            for (OoResource r : resources)
            {
                Date fileDate = new Date(r.getFile().lastModified());
                String code = org.apache.commons.lang.StringUtils.substringBefore(r.getFilename(), ".");

                OoResourceMetaData metaData = r.getMetaData();
                if (metaData != null && metaData.getTypeDef() != null)
                {
                    this.typeService.registerType(metaData.getTypeDef());
                }

                TemplateBlock block = (TemplateBlock) dao.getByPath(jcrPath + code);
                if (block == null || block.getModificationTimestamp().before(fileDate))
                {

                    // Don't include private blocks
                    if (metaData != null && metaData.getKeywords() != null && metaData.getKeywords().contains("private"))
                    {
                        this.logger.info("Ignoring private block: " + jcrPath + code);
                        continue;
                    }

                    this.logger.info("Updating metaData for block: " + jcrPath + code);

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
                        {
                            block.setLabel(metaData.getTitle());
                        }
                        else
                        {
                            block.setDescription(metaData.getDescription());
                        }
                        if (metaData.getKeywords() != null && metaData.getKeywords().contains("global"))
                        {
                            block.setGlobal(true);
                        }
                        if (metaData.getKeywords() != null)
                        {
                            block.setKeywords(metaData.getKeywords());
                        }
                    }

                    dao.save(block);
                    dao.publish(block, null);
                }
                else
                {
                    this.logger.info("Found current block: " + jcrPath + code);
                }
            }

            // Process layouts
            resources = this.ooResourceLoader.getResources("/site/templates/layouts/");
            jcrPath = "/designer/layouts/";
            for (OoResource r : resources)
            {
                Date fileDate = new Date(r.getFile().lastModified());

                String code = org.apache.commons.lang.StringUtils.substringBefore(r.getFilename(), ".");
                TemplateLayout layout = (TemplateLayout) dao.getByPath(jcrPath + code);
                if (layout == null || layout.getModificationTimestamp().before(fileDate))
                {
                    this.logger.info("Updating metaData for layout: " + jcrPath + code);

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
                        {
                            layout.setLabel(r.getMetaData().getTitle());
                        }
                        else
                        {
                            layout.setDescription(r.getMetaData().getDescription());
                        }
                    }
                    dao.save(layout);
                    dao.publish(layout, null);
                }
                else
                {
                    this.logger.info("Found current layout: " + jcrPath + code);
                }
            }
        }
        catch (IOException e)
        {
            throw new OtherObjectsException("Error scanning resources.", e);
        }
    }

    /**
     * FIXME Just here till it gets a proper home
     */
    @SuppressWarnings("unchecked")
    public Template createTemplate(String code, String label, String layout, List<Map> regions)
    {
        UniversalJcrDao dao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);

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
                br.setPath("/blocks/");
                br.setBlock((TemplateBlock) dao.getByPath("/designer/blocks/" + b));
                br = (TemplateBlockReference) dao.save(br);
                dao.publish(br, null);
                blockReferenceObjects.add(br);
            }
            tr.setBlocks(blockReferenceObjects);
            regionObjects.add(tr);
        }

        return createTemplate(label, layout, dao, path, regionObjects);
    }

    @SuppressWarnings("unchecked")
    public Template createTemplateWithBlockData(String code, String label, String layout, List<Map<String, ?>> regions)
    {
        UniversalJcrDao dao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);

        String path = "/designer/templates/" + code;
        Template template = (Template) dao.getByPath(path);

        if (template != null)
            return template;

        List<TemplateRegion> regionObjects = new ArrayList<TemplateRegion>();
        for (Map<String, ?> r : regions)
        {
            TemplateRegion tr = new TemplateRegion();
            tr.setCode((String) r.get("code"));
            List<TemplateBlockReference> blockReferenceObjects = new ArrayList<TemplateBlockReference>();
            for (Map<String, ?> b : (List<Map<String, ?>>) r.get("blocks"))
            {
                TemplateBlockReference br = new TemplateBlockReference();
                br.setPath("/blocks/");
                br.setBlock((TemplateBlock) dao.getByPath("/designer/blocks/" + b.get("code")));
                br.setBlockData((DynaNode) b.get("data"));
                br = (TemplateBlockReference) dao.save(br);
                dao.publish(br, null);
                blockReferenceObjects.add(br);
            }
            tr.setBlocks(blockReferenceObjects);
            regionObjects.add(tr);
        }

        return createTemplate(label, layout, dao, path, regionObjects);
    }

    /**
     * @param label
     * @param layout
     * @param dao
     * @param path
     * @param regionObjects
     * @return
     */
    private Template createTemplate(String label, String layout, UniversalJcrDao dao, String path, List<TemplateRegion> regionObjects)
    {
        Template template;
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
