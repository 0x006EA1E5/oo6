package org.otherobjects.cms.io;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.core.io.Resource;

public interface OoResource extends Resource
{
    public OutputStream getOutputStream() throws IOException;
}
