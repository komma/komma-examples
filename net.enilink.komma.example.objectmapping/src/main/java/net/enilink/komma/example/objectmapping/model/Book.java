package net.enilink.komma.example.objectmapping.model;

import java.util.Set;

import net.enilink.composition.annotations.Iri;

@Iri(Library.URI_STRING + "Book")
public interface Book {

	@Iri(Library.URI_STRING + "title")
	String getTitle();

	void setTitle(String value);

	@Iri(Library.URI_STRING + "author")
	Set<Person> getAuthors();

	void setAuthors(Set<Person> persons);

}
