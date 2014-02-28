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

		// Amongst others, access to data can be managed with KOMMA by
		// implementations of IEntityManager. In this tutorial we create an
		// EntityManager on top of Sesame's MemoryStore.
		//
		// We have to tell this manager to use the Book and Person interfaces to
		// encapsulate access to instances and properties of Books or Persons.
		// We have to register them as Concepts.
		//
		// Here we create an entity manager on top of Sesame's MemoryStore

		SailRepository dataRepository = new SailRepository(new MemoryStore());
		dataRepository.initialize();
		IEntityManager manager = createEntityManager(new ExampleModule(
				dataRepository, new KommaModule() {
					{
						addConcept(Book.class);
						addConcept(Person.class);
					}
				}));

		// Create a book and add some authors
		Book book = manager.createNamed(Library.URI.appendFragment("book1"),
				Book.class);
		book.setTitle("Point of No Return");
		book.getAuthors().add(
				createPerson(manager, "person1", "Clint Eastwood", new Date()));
		book.getAuthors().add(
				createPerson(manager, "person2", "Marty McFly", new Date()));

		// This results in the following RDF statements
		// @Prefix om: <http://enilink.net/examples/objectmapping#>
		// om:book1 rdf:type om:Book
		// om:book1 rdf:type om:Document
		// om:book1 om:title "Point of No Return"
		// om:person1 rdf:type om:Person
		// om:person1 om:name "Clint Eastwood"
		// om:person1 om:dateOfBirth "..."
		// om:book1 om:author person1
		// om:person2 rdf:type om:Person
		// om:person2 om:name "Marty McFly"
		// om:person2 om:dateOfBirth "..."
		// om:book1 om:author person2
		//
		// Please note that KOMMA is able to handle sets, as shown by the
		// representation of authors. Sets are represented as repeated
		// properties, i.e. they are represented by multiple
		// statements in the form of (book, author, person)

		// Do some queries!
		exampleRawQuery(manager);
		System.out.println(".........");
		exampleMappedQuery(manager);
		System.out.println(".........");
		exampleRemoveObjectAndQuery(manager, book);
		System.out.println(".........");

		System.out.println("Done!");
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
		// This will result in the following RDF statements

		// person rdf:type <http://enilink.net/examples/objectmapping#Person>
		// person <http://enilink.net/examples/objectmapping#name> "..."
		// person <http://enilink.net/examples/objectmapping#dateOfBirth> "..."
		return person;
	}

	private static void exampleRawQuery(IEntityManager manager) {

		// We now can query the EntityManager for some data using SPARQL. We can
		// override parameters to avoid defining long URIs containing messy
		// strings.

		System.out.println("Do a raw query:");
		IQuery<?> query = manager
				.createQuery(
						"PREFIX om: <"
								+ Library.URI_STRING
								+ "> "
								+ "SELECT ?titleValue ?author ?authorDateOfBirth WHERE { " //
								+ "?book om:title ?title . " //
								+ "?book om:author ?person . " //
								+ "?person om:name ?authorName . " //
								+ "?person om:dateOfBirth ?authorDateOfBirth " //
								+ "}").setParameter("title",
						Library.URI.appendLocalPart("title"));

		// Expected output:
		// LinkedHashBindings: {title=Point of No Return, author=..., ...}
		// LinkedHashBindings: {title=Point of No Return, author=..., ...}

		for (IBindings<?> bindings : query.evaluate(IBindings.class)) {
			System.out.println(bindings);
		}
	}

	private static void exampleMappedQuery(IEntityManager manager) {

		// Besides querying data with SPARQL, we can also use our model
		// interfaces for encapsulating data access to properties. In this
		// function we simply select all instances of Person and print the
		// properties defined by the respective interface.

		System.out.println("Do a mapped query:");
		IQuery<?> query = manager
				.createQuery(
						ISparqlConstants.PREFIX
								+ "SELECT ?person ?clazz WHERE {?person rdf:type ?clazz}")
				.setParameter("clazz", Library.URI.appendLocalPart("Person"));

		// Expected output:
		// Name: Clint Eastwood
		// Place of birth:null
		// Name: Marty McFly
		// Place of birth:null

		for (IBindings<?> bindings : query.evaluate(IBindings.class)) {
			Person person = (Person) bindings.get("person");
			System.out.println("Name: " + person.getName());
			System.out.println("Place of birth:" + person.getPlaceOfBirth());
		}

		// Please note, because of the fact that we did not set any place of
		// birth. The respective getter returns null.
	}

	private static void exampleRemoveObjectAndQuery(IEntityManager manager,
			Book book) {

		// We delete the book and show that it is really gone.

		System.out.println("Select all books");

		IQuery<?> query = manager.createQuery(
				ISparqlConstants.PREFIX
						+ "SELECT ?book WHERE { ?book rdf:type ?clazz .  }")
				.setParameter("clazz", Library.URI.appendLocalPart("Book"));

		for (IBindings<?> bindings : query.evaluate(IBindings.class)) {
			System.out.println(bindings.get("book"));
		}
		manager.remove(book);

		System.out.println("Select all books ... again!");
		query = manager.createQuery(
				ISparqlConstants.PREFIX
						+ "SELECT ?book WHERE { ?book rdf:type ?clazz .  }")
				.setParameter("clazz", Library.URI.appendLocalPart("Book"));

		// Expected output:
		// Select all books
		// http://enilink.net/examples/objectmapping#book1
		// Select all books ... again!

		for (IBindings<?> bindings : query.evaluate(IBindings.class)) {
			System.out.println(bindings.get("book"));
		}
	}
}
