package org.otherobjects.cms.model;

import javax.activation.MimeType;

import org.otherobjects.cms.jcr.GenericJcrDaoJackrabbit;

public class CmsFileDaoImpl extends GenericJcrDaoJackrabbit<CmsFile> implements CmsFileDao
{

    private final DataFileDao dataFileDao = new DataFileDaoFileSystem();

    public CmsFileDaoImpl()
    {
        super(CmsFile.class);
    }

    @Override
    public CmsFile save(CmsFile cmsFile, boolean validate)
    {
        if (cmsFile.getNewFile() != null)
        {
            DataFile file = new DataFile(cmsFile.getNewFile());
            file.setCollection(CmsFile.DATA_FILE_COLLECTION_NAME);
            file.setFileName(cmsFile.getOriginalFileName());
            MimeType mimeType = file.getMimeType();
            if (mimeType != null)
                file.setPath(formatPath(mimeType.getPrimaryType()));
            else
                file.setPath(formatPath("general"));

            cmsFile.setMimeType(mimeType.toString());
            dataFileDao.save(file);
            cmsFile.setNewFile(null);
        }
        return super.save(cmsFile, validate);
    }

    private String formatPath(String path)
    {
        return "/" + path.trim() + "/";
    }

}
