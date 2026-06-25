package tn.educanet.pfe.dozer;

import java.time.LocalDate;
import javax.xml.datatype.XMLGregorianCalendar;
import org.dozer.CustomConverter;
import tn.educanet.pfe.util.SchemaDateUtils;

public class LocalDateXmlGregorianCalendarConverter implements CustomConverter {

	@Override
	public Object convert(Object destination, Object source, Class<?> destinationClass, Class<?> sourceClass) {
		if (source == null) {
			return null;
		}
		if (source instanceof LocalDate localDate && XMLGregorianCalendar.class.isAssignableFrom(destinationClass)) {
			return SchemaDateUtils.toXmlDate(localDate);
		}
		if (source instanceof XMLGregorianCalendar xmlDate && LocalDate.class.isAssignableFrom(destinationClass)) {
			return SchemaDateUtils.toLocalDate(xmlDate);
		}
		throw new IllegalStateException("Unsupported date conversion: " + sourceClass + " -> " + destinationClass);
	}
}
