package net.enilink.komma.example.objectmapping.model;

import java.util.Set;

import net.enilink.composition.annotations.Iri;

@Iri(Library.URI_STRING + "Book")
public interface Book extends Document {

	@Iri(Library.URI_STRING + "author")
	Set<Person> getAuthors();

	void setAuthors(Set<Person> persons);

}
