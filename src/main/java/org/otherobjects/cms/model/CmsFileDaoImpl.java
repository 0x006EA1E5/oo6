package org.otherobjects.cms.model;

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
            file.setFileName("");
            file.setPath("");
            cmsFile.setMimeType(file.getMimeType().toString());
            dataFileDao.save(file);
        }
        return super.save(cmsFile, validate);
    }

}
