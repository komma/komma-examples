package net.enilink.komma.example.objectmapping.util;

import net.enilink.komma.core.IUnitOfWork;
import net.enilink.komma.core.KommaModule;
import net.enilink.komma.dm.IDataManager;
import net.enilink.komma.dm.IDataManagerFactory;
import net.enilink.komma.em.CacheModule;
import net.enilink.komma.em.CachingEntityManagerModule;
import net.enilink.komma.em.EntityManagerFactoryModule;
import net.enilink.komma.em.util.KommaUtil;
import net.enilink.komma.em.util.UnitOfWork;
import net.enilink.komma.example.objectmapping.model.Book;
import net.enilink.komma.example.objectmapping.model.Person;
import net.enilink.komma.sesame.SesameModule;

import org.openrdf.repository.Repository;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ExampleModule extends AbstractModule {

	private Repository dataRepository;
	private KommaModule kommaModule;

	public ExampleModule(Repository repo, KommaModule module) {
		this.dataRepository = repo;
		this.kommaModule = module;
	}

	@Override
	protected void configure() {
		install(new SesameModule());
		install(new EntityManagerFactoryModule(kommaModule, null,
				new CachingEntityManagerModule()));
		install(new CacheModule("myCache"));

		UnitOfWork uow = new UnitOfWork();
		uow.begin();

		bind(UnitOfWork.class).toInstance(uow);
		bind(IUnitOfWork.class).toInstance(uow);
		bind(Repository.class).toInstance(dataRepository);
	}

	@Provides
	protected IDataManager provideDataManager(IDataManagerFactory dmFactory) {
		return dmFactory.get();
	}
}