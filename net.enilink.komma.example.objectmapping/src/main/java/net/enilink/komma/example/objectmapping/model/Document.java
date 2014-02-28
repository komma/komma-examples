package net.enilink.komma.example.objectmapping.model;

import javax.xml.datatype.XMLGregorianCalendar;

import net.enilink.composition.annotations.Iri;

@Iri(Library.URI_STRING + "Document")
public interface Document {

	@Iri(Library.URI_STRING + "dateOfBirth")
	XMLGregorianCalendar getDateOfRelease();

	void setDateOfRelease(XMLGregorianCalendar dateOfRelease);
	
	@Iri(Library.URI_STRING + "title")
	String getTitle();

	void setTitle(String title);
	
}
