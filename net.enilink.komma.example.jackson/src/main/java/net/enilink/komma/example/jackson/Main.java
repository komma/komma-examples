package net.enilink.komma.example.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import net.enilink.komma.core.IEntityManager;
import net.enilink.komma.core.IEntityManagerFactory;
import net.enilink.komma.core.KommaModule;
import net.enilink.komma.core.URI;
import net.enilink.komma.em.rdf4j.RDF4JEntityManagerModule;
import net.enilink.komma.em.util.KommaUtil;
import net.enilink.komma.example.objectmapping.model.Book;
import net.enilink.komma.example.objectmapping.model.Library;
import net.enilink.komma.example.objectmapping.model.Person;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.GregorianCalendar;

public class Main {
	public static void main(String[] args) throws RepositoryException {
		// Initialize an RDF4J memory store and wrap it by an entity manager.
		SailRepository dataRepository = new SailRepository(new MemoryStore());
		dataRepository.init();

		KommaModule baseModule = new KommaModule() {
			{
				addConcept(Book.class);
				addConcept(Person.class);
			}
		};
		KommaModule fullModule = new KommaModule();
		fullModule.includeModule(KommaUtil.getCoreModule());
		fullModule.includeModule(baseModule);

		IEntityManager manager = createEntityManager(new RDF4JEntityManagerModule(
				dataRepository, fullModule));

		// Create a book and add some authors
		Book book = manager.createNamed(Library.NS_URI.appendFragment("book1"),
				Book.class);
		// Set properties using method chaining
		book.title("Point of No Return").dateOfRelease(toCalendar(new Date()));

		book.authors().add(
				createPerson(manager, "person1", "Clint Eastwood", new Date()));
		book.authors().add(
				createPerson(manager, "person2", "Marty McFly", new Date()));

		ObjectMapper jacksonMapper = JsonMapper.builder().enable(SerializationFeature.INDENT_OUTPUT)
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
				.serializationInclusion(JsonInclude.Include.NON_NULL)
				.build();

		jacksonMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
			@Override
			public boolean hasIgnoreMarker(AnnotatedMember m) {
				// ignore anything that is not mapped within baseModule
				if (m.getAnnotated() instanceof Method) {
					for (KommaModule.Association association : baseModule.getConcepts()) {
						try {
							if (association.getJavaClass().getMethod(m.getName()) != null) {
								return false;
							}
						} catch (NoSuchMethodException e) {
						}
					}
					// do not ignore the URI
					return !m.getName().equals("getURI");
				}
				return true;
			}

			@Override
			public PropertyName findNameForSerialization(Annotated a) {
				PropertyName result = super.findNameForSerialization(a);
				if (result == null) {
					// support non-getters like authors()
					if (a.getAnnotated() instanceof Method) {
						final Method method = (Method) a.getAnnotated();
						if (method.getParameterCount() == 0 && !method.getName().startsWith("get")) {
							return new PropertyName(method.getName());
						}
					}
				}
				return result;
			}
		});

		SimpleModule jacksonModule = new SimpleModule();
		jacksonModule.addSerializer(URI.class, new URISerializer());
		jacksonMapper.registerModule(jacksonModule);

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
		Person person = manager.createNamed(Library.NS_URI.appendFragment(id),
				Person.class);
		person.setName(name);
		person.setDateOfBirth(toCalendar(date));
		// This will result in the following RDF statements

		// person rdf:type <http://enilink.net/examples/objectmapping#Person>
		// person <http://enilink.net/examples/objectmapping#name> "..."
		// person <http://enilink.net/examples/objectmapping#dateOfBirth> "..."
		return person;
	}

	private static XMLGregorianCalendar toCalendar(Date date) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(date);
		XMLGregorianCalendar cal;
		try {
			cal = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		return cal;
	}

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
	class KommaFilterMixin {

	}

}
