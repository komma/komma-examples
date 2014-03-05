package net.enilink.komma.example.objectmapping.model;

import javax.xml.datatype.XMLGregorianCalendar;

import net.enilink.composition.annotations.Iri;

@Iri(Library.NS + "Person")
public interface Person {

	@Iri(Library.NS + "name")
	String getName();

	void setName(String name);

	@Iri(Library.NS + "dateOfBirth")
	XMLGregorianCalendar getDateOfBirth();

	void setDateOfBirth(XMLGregorianCalendar dateOfBirth);

	@Iri(Library.NS + "placeOfBirth")
	String getPlaceOfBirth();

	void setPlaceOfBirth(String placeOfBirth);

}
