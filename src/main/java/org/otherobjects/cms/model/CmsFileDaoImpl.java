package org.otherobjects.cms.model;

import javax.activation.MimeType;
import javax.activation.MimetypesFileTypeMap;

import org.apache.commons.io.FileUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.io.OoResourcePathPrefix;
import org.otherobjects.cms.jcr.GenericJcrDaoJackrabbit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmsFileDaoImpl extends GenericJcrDaoJackrabbit<CmsFile> implements CmsFileDao
{
    private final Logger logger = LoggerFactory.getLogger(CmsFileDaoImpl.class);

    private final DataFileDao dataFileDao = new DataFileDaoFileSystem();
    private OoResourceLoader ooResourceLoader;

    private MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();

    public void setOoResourceLoader(OoResourceLoader ooResourceLoader)
    {
        this.ooResourceLoader = ooResourceLoader;
    }

    public CmsFileDaoImpl()
    {
        super(CmsFile.class);
    }

    @Override
    public CmsFile save(CmsFile cmsFile, boolean validate)
    {
        if (cmsFile.getFile().getPrefix().equals(OoResourcePathPrefix.UPLOAD))
        {
            // just uploaded file, move it to its final destination
            //store in data path
            StringBuffer path = new StringBuffer();
            path.append(OoResourcePathPrefix.DATA.pathPrefix());

            MimeType mimeType = getMimeType(cmsFile.getOriginalFileName());
            if (mimeType != null)
                path.append(formatPath(mimeType.toString()));
            else
                path.append(formatPath("general"));

            path.append(cmsFile.getOriginalFileName()); // TODO need to make sure this file gets a filename unique to that path

            cmsFile.setMimeType(mimeType.toString());
            try
            {
                OoResource source = cmsFile.getFile();
                System.out.println(path.toString());
                OoResource target = ooResourceLoader.getResource(path.toString());

                FileUtils.copyFile(source.getFile(), target.getFile());
                cmsFile.setFile(target);
                source.getFile().delete();
            }
            catch (Exception e)
            {
                throw new OtherObjectsException("Couldn't copy cmsFile resource to " + path.toString(), e);
            }
        }
        return super.save(cmsFile, validate);
    }

    private String formatPath(String path)
    {
        return "/" + path.trim() + "/";
    }

    public MimeType getMimeType(String filename)
    {
        try
        {
            return new MimeType(mimetypesFileTypeMap.getContentType(filename));
        }
        catch (Exception e)
        {
            return null;
        }
    }

}
