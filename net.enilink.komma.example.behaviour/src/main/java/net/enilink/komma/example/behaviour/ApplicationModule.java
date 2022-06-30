package net.enilink.komma.example.behaviour;

import org.eclipse.rdf4j.repository.Repository;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import net.enilink.komma.core.IUnitOfWork;
import net.enilink.komma.core.KommaModule;
import net.enilink.komma.dm.IDataManager;
import net.enilink.komma.dm.IDataManagerFactory;
import net.enilink.komma.em.CacheModule;
import net.enilink.komma.em.CachingEntityManagerModule;
import net.enilink.komma.em.EntityManagerFactoryModule;
import net.enilink.komma.em.util.UnitOfWork;
import net.enilink.komma.rdf4j.RDF4JModule;

public class ApplicationModule extends AbstractModule {

	private Repository dataRepository;
	private KommaModule kommaModule;

	public ApplicationModule(Repository repo, KommaModule module) {
		this.dataRepository = repo;
		this.kommaModule = module;
	}

	@Override
	protected void configure() {
		install(new RDF4JModule());
		install(new EntityManagerFactoryModule(kommaModule, null,
				new CachingEntityManagerModule()));
		install(new CacheModule());

		UnitOfWork uow = new UnitOfWork();
		uow.begin();

		bind(UnitOfWork.class).toInstance(uow);
		bind(IUnitOfWork.class).toInstance(uow);
		bind(Repository.class).toInstance(dataRepository);
	}
}