package net.enilink.komma.example.objectmapping.model;

import javax.xml.datatype.XMLGregorianCalendar;

import net.enilink.composition.annotations.Iri;

@Iri(Library.URI_STRING + "Person")
public interface Person {

	@Iri(Library.URI_STRING + "name")
	String getName();

	void setName(String name);

	@Iri(Library.URI_STRING + "dateOfBirth")
	XMLGregorianCalendar getDateOfBirth();

	void setDateOfBirth(XMLGregorianCalendar dateOfBirth);

	@Iri(Library.URI_STRING + "placeOfBirth")
	String getPlaceOfBirth();

	void setPlaceOfBirth(String placeOfBirth);

}
