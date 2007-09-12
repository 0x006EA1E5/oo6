package org.otherobjects.cms.servlet;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class TransparentRequestContentAccessor extends HttpServletRequestWrapper
{
	public static final String DEFAULT_CHAR_ENCODING = "8859_1";
	
	private ByteArrayOutputStream baos;
	private boolean getReaderCalled = false;
	private boolean getInpuStreamCalled = false;

	public TransparentRequestContentAccessor(HttpServletRequest request) throws IOException {
		super(request);
		baos = new ByteArrayOutputStream();
		InputStream in = request.getInputStream();

		try
		{
			byte buffer[] = new byte[2048];
			int len = buffer.length;
			while (true)
			{
				len = in.read(buffer);
				if (len == -1)
					break;
				baos.write(buffer, 0, len);
			}
		}
		catch (Exception e)
		{
			throw new IOException("Couldn't copy inputStream bytes to byteArrayOutputStream");
		}
		finally
		{
			if (in != null)
				in.close();
		}
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if(getReaderCalled)
			throw new IllegalStateException("getInputStream must not be called if getReader was already called");
		getInpuStreamCalled = true;

		return new ServletInputStreamWrapper(new ByteArrayInputStream(baos.toByteArray()));
	}

	@Override
	public BufferedReader getReader() throws IOException {
		if(getInpuStreamCalled)
			throw new IllegalStateException("getReader must not be called if getInputStream was already called");
		getReaderCalled = true;
		
		String encoding = super.getCharacterEncoding();
		if (encoding == null) {
            encoding = DEFAULT_CHAR_ENCODING;
        }
		
		InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()), encoding);
		return new BufferedReader(isr);
	}



}
