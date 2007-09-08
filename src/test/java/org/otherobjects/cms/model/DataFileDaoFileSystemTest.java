package org.otherobjects.cms.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.TestCase;

public class DataFileDaoFileSystemTest extends TestCase
{

    public void testSave() throws IOException
    {
        DataFileDaoFileSystem dataFileDao = new DataFileDaoFileSystem();

        File test = new File("text.txt");
        FileWriter writer = new FileWriter(test);
        writer.write("Test file.");
        writer.close();

        DataFile testDataFile = new DataFile(test);
        testDataFile.setCollection("images");
        testDataFile.setInternalOnly(false);

        assertFalse(dataFileDao.exists(testDataFile.getId()));
        DataFile savedFile = dataFileDao.save(testDataFile);

        assertEquals("/images/text.txt", savedFile.getId());
        assertEquals("text/plain", savedFile.getMimeType().toString());
        assertEquals(test.length(), savedFile.getFileSize());

        assertTrue(savedFile.getFile().exists());
        assertFalse("File should have been moved on save", test.getAbsolutePath().equals(savedFile.getFile().getAbsolutePath()));

        // Test get
        assertTrue(dataFileDao.exists(testDataFile.getId()));
        DataFile dataFile = dataFileDao.get("/images/text.txt");
        assertEquals(savedFile.getFile(), dataFile.getFile());
        assertEquals(savedFile.getId(), dataFile.getId());
        assertEquals(savedFile.getCollection(), dataFile.getCollection());
        assertEquals(savedFile.getPath(), dataFile.getPath());
        assertEquals(savedFile.getFileName(), dataFile.getFileName());
        assertEquals(savedFile.getFileSize(), dataFile.getFileSize());
        assertTrue(savedFile.getExternalUrl().contains(dataFile.getId()));

        test.delete();
        savedFile.getFile().delete();
    }

}
