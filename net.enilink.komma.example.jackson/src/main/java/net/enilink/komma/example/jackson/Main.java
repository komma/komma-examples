package net.enilink.komma.example.jackson;

import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.inject.Guice;
import com.google.inject.Injector;

import net.enilink.komma.core.IBindings;
import net.enilink.komma.core.IEntityManager;
import net.enilink.komma.core.IEntityManagerFactory;
import net.enilink.komma.core.IQuery;
import net.enilink.komma.core.KommaModule;
import net.enilink.komma.core.URI;
import net.enilink.komma.em.rdf4j.RDF4JEntityManagerModule;
import net.enilink.komma.em.util.ISparqlConstants;
import net.enilink.komma.em.util.KommaUtil;
import net.enilink.komma.example.objectmapping.model.Book;
import net.enilink.komma.example.objectmapping.model.Library;
import net.enilink.komma.example.objectmapping.model.Person;
import net.enilink.komma.example.objectmapping.util.ExampleModule;

public class Main {
	public static class URISerializer extends StdSerializer<URI> {
	    
	    public URISerializer() {
	        this(null);
	    }
	  
	    public URISerializer(Class<URI> t) {
	        super(t);
	    }

	    @Override
	    public void serialize(
	      URI value, JsonGenerator jgen, SerializerProvider provider) 
	      throws IOException, JsonProcessingException {
	    	jgen.writeString(value.toString());
	    }
	}
	
	@JsonFilter("KommaFilter")
	class KommaFilterMixin
	{

	}

	public static void main(String[] args)
			throws DatatypeConfigurationException, RepositoryException {
		// Initialize an RDF4J memory store and wrap it by an entity manager.
		SailRepository dataRepository = new SailRepository(new MemoryStore());
		dataRepository.init();
		
		KommaModule module = new KommaModule() {
			{
				addConcept(Book.class);
				addConcept(Person.class);
			}
		};
		module.includeModule(KommaUtil.getCoreModule());
		
		IEntityManager manager = createEntityManager(new RDF4JEntityManagerModule(
				dataRepository, module));

		// Create a book and add some authors
		Book book = manager.createNamed(Library.NS_URI.appendFragment("book1"),
				Book.class);
		// Set properties using method chaining
		book.title("Point of No Return").dateOfRelease(getCurrentTime());

		book.authors().add(
				createPerson(manager, "person1", "Clint Eastwood", new Date()));
		book.authors().add(
				createPerson(manager, "person2", "Marty McFly", new Date()));
		
		ObjectMapper jacksonMapper = new ObjectMapper();

		// the filter is required to skip internal fields
		SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		filterProvider
				.addFilter("KommaFilter", SimpleBeanPropertyFilter.serializeAllExcept("entityManager", "uri", "reference"));

		SimpleModule jacksonModule = new SimpleModule();
		jacksonModule.addSerializer(URI.class, new URISerializer());
		jacksonMapper.registerModule(jacksonModule);

		jacksonMapper.setFilterProvider(filterProvider);
		jacksonMapper.addMixIn(Object.class, KommaFilterMixin.class);

		try {
			System.out.println("JSON: " + jacksonMapper.writeValueAsString(book));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	private static IEntityManager createEntityManager(RDF4JEntityManagerModule module) {
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
