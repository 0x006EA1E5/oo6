package org.otherobjects.cms.model;

import java.awt.Dimension;

import org.otherobjects.cms.jcr.GenericJcrDaoJackrabbit;
import org.otherobjects.cms.tools.CmsImageTool;
import org.otherobjects.cms.util.ImageUtils;

public class CmsImageDaoImpl extends GenericJcrDaoJackrabbit<CmsImage> implements CmsImageDao
{
    public CmsImageDaoImpl()
    {
        super(CmsImage.class);
    }

    private final DataFileDao dataFileDao = new DataFileDaoFileSystem();
    private final CmsImageTool cmsImageTool = new CmsImageTool();

//    public CmsImage createCmsImage()
//    {
//        /// FIXME This should never be needed -- need better DAOs
//        String typeName = CmsImage.class.getName();
//        TypeDef type = getTypeService().getType(typeName);
//        try
//        {
//            CmsImage n = new CmsImage();
//            n.setTypeDef(type);
//            return n;
//        }
//        catch (Exception e)
//        {
//            //TODO Better exception?
//            throw new OtherObjectsException("Could not create new instance of type: " + typeName, e);
//        }
//    }

//    @Override
    public CmsImage save(CmsImage o)
    {

        CmsImage image = (CmsImage) o;
        if (image.getNewFile() != null)
        {
            Dimension imageDimensions = ImageUtils.getImageDimensions(image.getNewFile());
            image.setOriginalWidth(new Double(imageDimensions.getWidth()).longValue());
            image.setOriginalHeight(new Double(imageDimensions.getHeight()).longValue());
            DataFile original = new DataFile(image.getNewFile());
            original.setFileName(image.getCode());
            original.setCollection(CmsImage.DATA_FILE_COLLECTION_NAME);
            original.setPath(CmsImage.ORIGINALS_PATH);
            original = this.dataFileDao.save(original);
            image.setOriginalFileId(original.getId());
            image.setNewFile(null);

            // Create thumbnail
            this.cmsImageTool.getThumbnail(image);
        }
        return (CmsImage) super.save(image);
    }
}
