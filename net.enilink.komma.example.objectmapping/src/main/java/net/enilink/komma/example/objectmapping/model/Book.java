package net.enilink.komma.example.objectmapping.model;

import java.util.Set;

import net.enilink.composition.annotations.Iri;

@Iri(Library.NS + "Book")
public interface Book extends Document {

	@Iri(Library.NS + "author")
	Set<Person> getAuthors();

	void setAuthors(Set<Person> persons);

}
