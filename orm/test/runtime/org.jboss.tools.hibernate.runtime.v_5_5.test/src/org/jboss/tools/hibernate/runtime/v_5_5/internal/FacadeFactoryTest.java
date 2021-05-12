package org.jboss.tools.hibernate.runtime.v_5_5.internal;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.hibernate.Criteria;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.DefaultNamingStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringSettings;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableFilter;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Cfg2HbmTool;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.GenericExporter;
import org.hibernate.tool.hbm2x.Hbm2DDLExporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.QueryExporter;
import org.hibernate.tool.ide.completion.HQLCodeAssist;
import org.hibernate.tool.ide.completion.HQLCompletionProposal;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.jboss.tools.hibernate.runtime.common.IFacade;
import org.jboss.tools.hibernate.runtime.spi.IArtifactCollector;
import org.jboss.tools.hibernate.runtime.spi.ICfg2HbmTool;
import org.jboss.tools.hibernate.runtime.spi.IClassMetadata;
import org.jboss.tools.hibernate.runtime.spi.ICollectionMetadata;
import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IConfiguration;
import org.jboss.tools.hibernate.runtime.spi.ICriteria;
import org.jboss.tools.hibernate.runtime.spi.IEntityMetamodel;
import org.jboss.tools.hibernate.runtime.spi.IEnvironment;
import org.jboss.tools.hibernate.runtime.spi.IExporter;
import org.jboss.tools.hibernate.runtime.spi.IForeignKey;
import org.jboss.tools.hibernate.runtime.spi.IGenericExporter;
import org.jboss.tools.hibernate.runtime.spi.IHQLCodeAssist;
import org.jboss.tools.hibernate.runtime.spi.IHQLCompletionProposal;
import org.jboss.tools.hibernate.runtime.spi.IHbm2DDLExporter;
import org.jboss.tools.hibernate.runtime.spi.IHibernateMappingExporter;
import org.jboss.tools.hibernate.runtime.spi.INamingStrategy;
import org.jboss.tools.hibernate.runtime.spi.IOverrideRepository;
import org.jboss.tools.hibernate.runtime.spi.IQueryExporter;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringSettings;
import org.jboss.tools.hibernate.runtime.spi.IReverseEngineeringStrategy;
import org.jboss.tools.hibernate.runtime.spi.ISchemaExport;
import org.jboss.tools.hibernate.runtime.spi.ITableFilter;
import org.jboss.tools.hibernate.runtime.v_5_5.internal.util.MetadataHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FacadeFactoryTest {

	private FacadeFactoryImpl facadeFactory;

	@BeforeEach
	public void beforeEach() throws Exception {
		facadeFactory = new FacadeFactoryImpl();
	}
	
	@Test
	public void testFacadeFactoryCreation() {
		assertNotNull(facadeFactory);
	}
	
	@Test
	public void testGetClassLoader() {
		assertSame(
				FacadeFactoryImpl.class.getClassLoader(), 
				facadeFactory.getClassLoader());
	}
	
	@Test
	public void testCreateArtifactCollector() {
		ArtifactCollector artifactCollector = new ArtifactCollector();
		IArtifactCollector facade = facadeFactory.createArtifactCollector(artifactCollector);
		assertSame(artifactCollector, ((IFacade)facade).getTarget());
	}
		
	@Test
	public void testCreateCfg2HbmTool() {
		Cfg2HbmTool cfg2HbmTool = new Cfg2HbmTool();
		ICfg2HbmTool facade = facadeFactory.createCfg2HbmTool(cfg2HbmTool);
		assertSame(cfg2HbmTool,  ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateNamingStrategy() {
		DefaultNamingStrategy namingStrategy = new DefaultNamingStrategy();
		INamingStrategy facade = facadeFactory.createNamingStrategy(namingStrategy);
		assertSame(namingStrategy, ((IFacade)facade).getTarget());
	}
	
	@Test
	public void testCreateReverseEngineeringSettings() {
		ReverseEngineeringSettings res = new ReverseEngineeringSettings(null);
		IReverseEngineeringSettings facade = facadeFactory.createReverseEngineeringSettings(res);
		assertSame(res, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateReverseEngineeringStrategy() {
		ReverseEngineeringStrategy res = (ReverseEngineeringStrategy)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { ReverseEngineeringStrategy.class }, 
				new TestInvocationHandler());
		IReverseEngineeringStrategy facade = facadeFactory.createReverseEngineeringStrategy(res);
		assertSame(res, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateOverrideRepository() {
		OverrideRepository overrideRepository = new OverrideRepository();
		IOverrideRepository facade = facadeFactory.createOverrideRepository(overrideRepository);
		assertSame(overrideRepository, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateSchemaExport() {
		SchemaExport schemaExport = new SchemaExport();
		ISchemaExport facade = facadeFactory.createSchemaExport(schemaExport);
		assertTrue(facade instanceof SchemaExportFacadeImpl);
		assertSame(schemaExport, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateGenericExporter() {
		GenericExporter genericExporter = new GenericExporter();
		IGenericExporter facade = facadeFactory.createGenericExporter(genericExporter);
		assertSame(genericExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHbm2DDLExporter() {
		Hbm2DDLExporter hbm2ddlExporter = new Hbm2DDLExporter();
		IHbm2DDLExporter facade = facadeFactory.createHbm2DDLExporter(hbm2ddlExporter);
		assertSame(hbm2ddlExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateQueryExporter() {
		QueryExporter queryExporter = new QueryExporter();
		IQueryExporter facade = facadeFactory.createQueryExporter(queryExporter);
		assertSame(queryExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateTableFilter() {
		TableFilter tableFilter = new TableFilter();
		ITableFilter facade = facadeFactory.createTableFilter(tableFilter);
		assertSame(tableFilter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateExporter() {
		Exporter exporter = (Exporter)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Exporter.class }, 
				new TestInvocationHandler());
		IExporter facade = facadeFactory.createExporter(exporter);
		assertTrue(facade instanceof ExporterFacadeImpl);
		assertSame(exporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateClassMetadata() {
		ClassMetadata classMetadata = (ClassMetadata)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { ClassMetadata.class }, 
				new TestInvocationHandler());
		IClassMetadata facade = facadeFactory.createClassMetadata(classMetadata);
		assertSame(classMetadata, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateCollectionMetadata() {
		CollectionMetadata collectionMetadata = (CollectionMetadata)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { CollectionMetadata.class }, 
				new TestInvocationHandler());
		ICollectionMetadata facade = facadeFactory.createCollectionMetadata(collectionMetadata);
		assertSame(collectionMetadata, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateColumn() {
		Column column = new Column();
		IColumn facade = facadeFactory.createColumn(column);
		assertSame(column, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateConfiguration() {
		Configuration configuration = new Configuration();
		IConfiguration facade = facadeFactory.createConfiguration(configuration);
		assertSame(configuration, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateCriteria() {
		Criteria criteria = (Criteria)Proxy.newProxyInstance(
				facadeFactory.getClassLoader(), 
				new Class[] { Criteria.class }, 
				new TestInvocationHandler());
		ICriteria facade = facadeFactory.createCriteria(criteria);
		assertSame(criteria, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateEntityMetamodel() {
		Configuration configuration = new Configuration();
		configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
		builder.applySettings(configuration.getProperties());
		ServiceRegistry serviceRegistry = builder.build();		
		SessionFactoryImplementor sfi = (SessionFactoryImplementor)configuration.buildSessionFactory(serviceRegistry);
		RootClass rc = new RootClass(null);
		MetadataImplementor m = (MetadataImplementor)MetadataHelper.getMetadata(configuration);
		@SuppressWarnings("deprecation")
		SimpleValue sv = new SimpleValue(m);
		sv.setNullValue("null");
		sv.setTypeName(Integer.class.getName());
		rc.setIdentifier(sv);
		rc.setOptimisticLockStyle(OptimisticLockStyle.NONE);
		PersisterCreationContext pcc = new PersisterCreationContext() {		
			@Override
			public SessionFactoryImplementor getSessionFactory() {
				return sfi;
			}		
			@Override
			public MetadataImplementor getMetadata() {
				return m;
			}
		};
		EntityMetamodel entityMetamodel = new EntityMetamodel(rc, null, pcc);
		IEntityMetamodel facade = facadeFactory.createEntityMetamodel(entityMetamodel);
		assertSame(entityMetamodel, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateEnvironment() {
		IEnvironment environment = facadeFactory.createEnvironment();
		assertNotNull(environment);
		assertTrue(environment instanceof EnvironmentFacadeImpl);
	}
	
	@Test
	public void testCreateForeignKey() {
		ForeignKey foreignKey = new ForeignKey();
		IForeignKey facade = facadeFactory.createForeignKey(foreignKey);
		assertSame(foreignKey, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHibernateMappingExporter() {
		HibernateMappingExporter hibernateMappingExporter = new HibernateMappingExporter();
		IHibernateMappingExporter facade = facadeFactory.createHibernateMappingExporter(hibernateMappingExporter);
		assertSame(hibernateMappingExporter, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHQLCodeAssist() {
		StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
		ssrb.applySetting("hibernate.dialect", TestDialect.class.getName());
		HQLCodeAssist hqlCodeAssist = new HQLCodeAssist(new MetadataSources().buildMetadata(ssrb.build()));
		IHQLCodeAssist facade = facadeFactory.createHQLCodeAssist(hqlCodeAssist);
		assertSame(hqlCodeAssist, ((IFacade)facade).getTarget());		
	}
	
	@Test
	public void testCreateHQLCompletionProposal() {
		HQLCompletionProposal hqlCompletionProposal = new HQLCompletionProposal(0, 0);
		IHQLCompletionProposal facade = facadeFactory.createHQLCompletionProposal(hqlCompletionProposal);
		assertSame(hqlCompletionProposal, ((IFacade)facade).getTarget());		
	}
	
	private class TestInvocationHandler implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return null;
		}	
	}
	
	public static class TestDialect extends Dialect {};
	
}
