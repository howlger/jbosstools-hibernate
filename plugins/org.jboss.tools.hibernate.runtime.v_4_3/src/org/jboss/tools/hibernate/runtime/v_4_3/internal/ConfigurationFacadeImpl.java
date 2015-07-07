package org.jboss.tools.hibernate.runtime.v_4_3.internal;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.jdbc.dialect.spi.DialectFactory;
import org.hibernate.service.ServiceRegistry;
import org.jboss.tools.hibernate.runtime.common.AbstractConfigurationFacade;
import org.jboss.tools.hibernate.runtime.common.IFacadeFactory;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;

public class ConfigurationFacadeImpl extends AbstractConfigurationFacade {
	
	ServiceRegistry serviceRegistry = null;

	public ConfigurationFacadeImpl(
			IFacadeFactory facadeFactory,
			Configuration configuration) {
		super(facadeFactory, configuration);
	}
	
	@Override
	public IConfiguration configure() {
		((Configuration)getTarget()).configure();
		return this;
	}

	protected Object buildTargetSessionFactory() {
		if (serviceRegistry == null) {
			buildServiceRegistry();
		}
		return ((Configuration)getTarget()).buildSessionFactory(serviceRegistry);
	}

	protected Object buildTargetSettings() {
		if (serviceRegistry == null) {
			buildServiceRegistry();
		}
		return ((Configuration)getTarget()).buildSettings(serviceRegistry);
	}
	
	protected Object buildTargetDialect() {
		if (serviceRegistry == null) {
			buildServiceRegistry();
		}
		return serviceRegistry.getService(DialectFactory.class).buildDialect(
				getProperties(), null);
	}
	
	void buildServiceRegistry() {
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(getProperties());
		serviceRegistry = builder.build();		
	}

}
