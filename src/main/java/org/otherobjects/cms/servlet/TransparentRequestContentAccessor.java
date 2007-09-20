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

/**
 * This is an HttpServletRequest Wrapper that allows you to read the request body multiple times, which is needed if
 * you want to read the request body in a filter. It achieves this by storing the request body in an instance variable, so
 * beware of memory issues if you use this on requests that carry large bodies.
 * 
 * @author joerg
 *
 */
public class TransparentRequestContentAccessor extends HttpServletRequestWrapper
{
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
            encoding = getDefaultEncoding();
        }
		
		InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()), encoding);
		return new BufferedReader(isr);
	}
	
	public static String getDefaultEncoding()
	{
		return new InputStreamReader(
				new ByteArrayInputStream(new
						byte[0])).getEncoding();
	}

	public class ServletInputStreamWrapper extends ServletInputStream {
		
		InputStream in;
		
		public ServletInputStreamWrapper(InputStream inputStream) {
			super();
			this.in = inputStream;
		}

		@Override
		public int read() throws IOException {
			return in.read();
		}
	}

}
