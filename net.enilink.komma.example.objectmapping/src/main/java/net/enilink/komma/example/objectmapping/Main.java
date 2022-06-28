package net.enilink.komma.example.objectmapping;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import com.google.inject.Guice;
import com.google.inject.Injector;

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

public class Main {

	public static void main(String[] args)
			throws DatatypeConfigurationException, RepositoryException {
		// Initialize an RDF4J memory store and wrap it by an entity manager.
		//
		// We have to tell this manager to use the Book and Person interfaces to
		// encapsulate access to instances and properties of Books or Persons.
		// We have to register them as Concepts.

		SailRepository dataRepository = new SailRepository(new MemoryStore());
		dataRepository.init();
		IEntityManager manager = createEntityManager(new ExampleModule(
				dataRepository, new KommaModule() {
					{
						addConcept(Book.class);
						addConcept(Person.class);
					}
				}));

		// Create a book and add some authors
		Book book = manager.createNamed(Library.NS_URI.appendFragment("book1"),
				Book.class);
		// Set properties using method chaining
		book.title("Point of No Return").dateOfRelease(getCurrentTime());

		book.authors().add(
				createPerson(manager, "person1", "Clint Eastwood", new Date()));
		book.authors().add(
				createPerson(manager, "person2", "Marty McFly", new Date()));

		// This results in the following RDF statements
		// @Prefix om: <http://enilink.net/examples/objectmapping#>
		// om:book1 rdf:type om:Book
		// om:book1 rdf:type om:Document
		// om:book1 om:dateOfRelease "..."^^xsd:datetime
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
		examplePrefetchPropertyValues(manager);
		System.out.println(".........");
		examplePrefetchWholeGraph(manager);
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
		XMLGregorianCalendar cal = getCurrentTime();
		Person person = manager.createNamed(Library.NS_URI.appendFragment(id),
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
		// We now can query the EntityManager for some data using SPARQL.

		System.out.println("Do a raw query:");
		IQuery<?> query = manager.createQuery( //
				"PREFIX om: <" + Library.NS + ">" //
						+ "SELECT ?title ?author ?authorDateOfBirth WHERE { " //
						+ "?book om:title ?title . " //
						+ "?book om:author ?person . " //
						+ "?person om:name ?authorName . " //
						+ "?person om:dateOfBirth ?authorDateOfBirth " //
						+ "}");

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
								+ "SELECT ?person WHERE { ?person a ?type }")
				.setTypeParameter("type", Person.class);

		// Expected output:
		// Name: Clint Eastwood
		// Date of birth: 2018-06-28T21:35:52.209+02:00
		// Name: Marty McFly
		// Date of birth: 2018-06-28T21:35:52.245+02:00

		for (Person person : query.evaluate(Person.class)) {
			System.out.println("Name: " + person.getName());
			System.out.println("Date of birth: " + person.getDateOfBirth());
		}
	}
	
	private static void examplePrefetchPropertyValues(IEntityManager manager) {

		// KOMMA also supports eager loading of property values by
		// using SPARQL CONSTRUCT queries.

		System.out.println("Do a construct query for eager loading:");
		IQuery<?> query = manager
				.createQuery(
						ISparqlConstants.PREFIX
								+ "CONSTRUCT { ?person a <komma:Result> ; ?p ?o } WHERE { ?person a ?type; ?p ?o }")
				.setTypeParameter("type", Person.class);

		// Expected output:
		// Name: Clint Eastwood
		// Date of birth: 2018-06-28T21:35:52.209+02:00
		// Name: Marty McFly
		// Date of birth: 2018-06-28T21:35:52.245+02:00

		for (Person person : query.evaluate(Person.class)) {
			System.out.println("Name: " + person.getName());
			System.out.println("Date of birth: " + person.getDateOfBirth());
		}
	}
	
	private static void examplePrefetchWholeGraph(IEntityManager manager) {

		// KOMMA also supports eager loading an RDF graph of arbitrary depth by
		// using SPARQL CONSTRUCT queries with property paths.
		
		System.out.println("Do a construct query for eager loading:");
		IQuery<?> query = manager
				.createQuery(
						ISparqlConstants.PREFIX
								+ "CONSTRUCT { ?book a <komma:Result> . ?s ?p ?o } WHERE { ?book a ?type; !<:> ?s . ?s ?p ?o }")
				.setTypeParameter("type", Book.class);

		// Expected output:
		// Title: Point of No Return
		// 		Author: Clint Eastwood
		// 		Author: Marty McFly

		for (Book book : query.evaluate(Book.class)) {
			// No additional SPARQL queries are required to access the bean properties here.

			System.out.println("Title: " + book.title());
			for (Person author : book.authors()) {
				System.out.println("	Author: " + author.getName());
			}
		}
	}

	private static void exampleRemoveObjectAndQuery(IEntityManager manager,
			Book book) {

		// We delete the book and show that it is really gone.

		System.out.println("Select all books");

		IQuery<?> query = manager.createQuery(
				ISparqlConstants.PREFIX
						+ "SELECT ?book WHERE { ?book a ?clazz .  }")
				.setTypeParameter("type", Book.class);

		for (IBindings<?> bindings : query.evaluate(IBindings.class)) {
			System.out.println(bindings.get("book"));
		}
		manager.remove(book);

		System.out.println("Select all books ... again!");
		query = manager.createQuery(
				ISparqlConstants.PREFIX
						+ "SELECT ?book WHERE { ?book a ?type .  }")
				.setTypeParameter("type", Book.class);

		// Expected output:
		// Select all books
		// http://enilink.net/examples/objectmapping#book1
		// Select all books ... again!

		for (IBindings<?> bindings : query.evaluate(IBindings.class)) {
			System.out.println(bindings.get("book"));
		}
	}

	private static XMLGregorianCalendar getCurrentTime() {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar cal = null;
		try {
			cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		return cal;
	}

}
