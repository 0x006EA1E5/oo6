package org.otherobjects.cms.jcr;

import org.apache.jackrabbit.ocm.manager.atomictypeconverter.AtomicTypeConverterProvider;
import org.apache.jackrabbit.ocm.manager.cache.ObjectCache;
import org.apache.jackrabbit.ocm.manager.objectconverter.ProxyManager;
import org.apache.jackrabbit.ocm.manager.objectconverter.impl.ObjectConverterImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;

public class DynaNodeObjectConverter extends ObjectConverterImpl {

	public DynaNodeObjectConverter() {
		super();
	}

	public DynaNodeObjectConverter(Mapper mapper,
			AtomicTypeConverterProvider converterProvider,
			ProxyManager proxyManager, ObjectCache requestObjectCache) {
		super(mapper, converterProvider, proxyManager, requestObjectCache);
	}

	public DynaNodeObjectConverter(Mapper mapper,
			AtomicTypeConverterProvider converterProvider) {
		super(mapper, converterProvider);
	}
	
}
