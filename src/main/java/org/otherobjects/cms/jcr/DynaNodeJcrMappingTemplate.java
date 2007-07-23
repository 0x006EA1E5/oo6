package org.otherobjects.cms.jcr;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.ocm.exception.JcrMappingException;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.atomictypeconverter.impl.DefaultAtomicTypeConverterProvider;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.spring.JcrMappingTemplate;
import org.springmodules.jcr.SessionFactory;

public class DynaNodeJcrMappingTemplate extends JcrMappingTemplate {
	
	public DynaNodeJcrMappingTemplate() {
		super();

	}

	public DynaNodeJcrMappingTemplate(SessionFactory sessionFactory,
			Mapper mapper) {
		super(sessionFactory, mapper);
	}

	@Override
	protected ObjectContentManager createObjectContentManager(Session session)
			throws RepositoryException, JcrMappingException {
		ObjectContentManagerImpl objectContentManager = new ObjectContentManagerImpl(this.getMapper(), createQueryManager(), session);
		objectContentManager.setObjectConverter(new DynaNodeObjectConverter(this.getMapper(), new DefaultAtomicTypeConverterProvider()));
		return objectContentManager;
	}
	
	
	
}
