package tn.educanet.pfe.util;

import java.time.LocalDate;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public final class SchemaDateUtils {

	private SchemaDateUtils() {
	}

	public static LocalDate toLocalDate(XMLGregorianCalendar value) {
		if (value == null) {
			return null;
		}
		return value.toGregorianCalendar().toZonedDateTime().toLocalDate();
	}

	public static XMLGregorianCalendar toXmlDate(LocalDate value) {
		if (value == null) {
			return null;
		}
		try {
			return DatatypeFactory.newInstance()
					.newXMLGregorianCalendarDate(value.getYear(), value.getMonthValue(), value.getDayOfMonth(), 0);
		} catch (DatatypeConfigurationException e) {
			throw new IllegalStateException("Impossible de convertir la date en XML", e);
		}
	}
}
