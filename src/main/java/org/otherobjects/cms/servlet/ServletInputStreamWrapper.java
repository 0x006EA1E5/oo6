package org.otherobjects.cms.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

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
