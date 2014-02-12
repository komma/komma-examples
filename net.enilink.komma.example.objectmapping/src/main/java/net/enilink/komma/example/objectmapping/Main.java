package net.enilink.komma.example.objectmapping;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import net.enilink.komma.core.IBindings;
import net.enilink.komma.core.IEntityManager;
import net.enilink.komma.core.IEntityManagerFactory;
import net.enilink.komma.core.IQuery;
import net.enilink.komma.core.KommaModule;
import net.enilink.komma.em.util.ISparqlConstants;
import net.enilink.komma.example.objectmapping.model.Book;
import net.enilink.komma.example.objectmapping.model.Library;
import net.enilink.komma.example.objectmapping.model.Person;
import net.enilink.komma.example.objectmapping.util.ExampleModule;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Main {

	public static void main(String[] args)
			throws DatatypeConfigurationException, RepositoryException {

		// create a sesame repository
		SailRepository dataRepository = new SailRepository(new MemoryStore());
		dataRepository.initialize();

		// create an entity manager and register concepts
		IEntityManager manager = createEntityManager(new ExampleModule(
				dataRepository, new KommaModule() {
					{
						addConcept(Book.class);
						addConcept(Person.class);
					}
				}));

		// create a book
		Book book = manager.createNamed(Library.URI.appendFragment("book1"),
				Book.class);
		book.setTitle("Point of No Return");
		// add some authors
		book.getAuthors().add(
				createPerson(manager, "person1", "Clint Eastwood", new Date()));
		book.getAuthors().add(
				createPerson(manager, "person2", "Marty McFly", new Date()));

		// Do some queries

		exampleRawQuery(manager);
		System.out.println(".........");
		exampleMappedQuery(manager);
		System.out.println(".........");
		exampleRemoveObjectAndQuery(manager, book);
		System.out.println(".........");

		System.out.println("Done!");
	}

	private static void exampleRawQuery(IEntityManager manager) {

		System.out.println("Do a raw query:");

		// do a 'raw' query
		IQuery<?> query = manager
				.createQuery(
						"SELECT ?titleValue ?authorName ?authorDateOfBirth WHERE { " //
								+ "?book ?title ?titleValue . " //
								+ "?book ?author ?person . " //
								+ "?person ?name ?authorName . " //
								+ "?person ?dateOfBirth ?authorDateOfBirth " //
								+ "}")
				.setParameter("author", Library.URI.appendLocalPart("author"))
				.setParameter("name", Library.URI.appendLocalPart("name"))
				.setParameter("dateOfBirth",
						Library.URI.appendFragment("dateOfBirth"))
				.setParameter("title", Library.URI.appendLocalPart("title"));

		for (IBindings<?> bindings : query.evaluate(IBindings.class)) {
			System.out.println(bindings);
		}
	}

	private static void exampleMappedQuery(IEntityManager manager) {

		System.out.println("Do a mapped query:");

		IQuery<?> query = manager
				.createQuery(
						ISparqlConstants.PREFIX
								+ "SELECT ?person ?clazz WHERE {?person rdf:type ?clazz}")
				.setParameter("clazz", Library.URI.appendLocalPart("Person"));

		for (IBindings<?> bindings : query.evaluate(IBindings.class)) {
			Person person = (Person) bindings.get("person");
			System.out.println("Name: " + person.getName());
			System.out.println("Place of birth:" + person.getPlaceOfBirth());
		}
	}

	private static void exampleRemoveObjectAndQuery(IEntityManager manager,
			Book book) {

		System.out.println("Select all books");

		IQuery<?> query = manager.createQuery(
				ISparqlConstants.PREFIX
						+ "SELECT ?book WHERE { ?book rdf:type ?clazz .  }")
				.setParameter("clazz", Library.URI.appendLocalPart("Book"));

		for (IBindings<?> bindings : query.evaluate(IBindings.class)) {
			System.out.println(bindings);
		}

		// remove the book
		manager.remove(book);

		System.out.println("Select all books ... again!");
		query = manager.createQuery(
				ISparqlConstants.PREFIX
						+ "SELECT ?book WHERE { ?book rdf:type ?clazz .  }")
				.setParameter("clazz", Library.URI.appendLocalPart("Book"));

		for (IBindings<?> bindings : query.evaluate(IBindings.class)) {
			System.out.println(bindings);
		}
	}

	private static IEntityManager createEntityManager(ExampleModule module) {
		Injector injector = Guice.createInjector(module);
		IEntityManagerFactory factory = injector
				.getInstance(IEntityManagerFactory.class);
		IEntityManager manager = factory.get();
		return manager;
	}

	private static Person createPerson(IEntityManager manager, String id,
			String name, Date date) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar cal = null;
		try {
			cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}

		Person person = manager.createNamed(Library.URI.appendFragment(id),
				Person.class);
		person.setName(name);
		person.setDateOfBirth(cal);

		return person;
	}
}
