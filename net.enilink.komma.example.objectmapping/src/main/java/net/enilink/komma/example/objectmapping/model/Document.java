package net.enilink.komma.example.objectmapping.model;

import javax.xml.datatype.XMLGregorianCalendar;

import net.enilink.composition.annotations.Iri;

@Iri(Library.NS + "Document")
public interface Document {

	@Iri(Library.NS + "dateOfRelease")
	XMLGregorianCalendar getDateOfRelease();

	void setDateOfRelease(XMLGregorianCalendar dateOfRelease);

	@Iri(Library.NS + "title")
	String getTitle();

	void setTitle(String title);

}
