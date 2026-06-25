package tn.educanet.pfe;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.springframework.stereotype.Component;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.AbstractEndpointExceptionResolver;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapMessage;

import tn.educanet.pfe.exception.BusinessException;

/**
 * Convertit les {@link BusinessException} métier en faute SOAP (client) avec le message utilisateur.
 * Les erreurs REST JSON restent gérées par {@code ApiExceptionHandler}.
 */
@Component
public class BusinessSoapFaultResolver extends AbstractEndpointExceptionResolver {

	@Override
	protected boolean resolveExceptionInternal(MessageContext messageContext, Object endpoint, Exception ex) {
		Throwable t = unwrap(ex);
		if (t instanceof BusinessException be) {
			try {
				SoapMessage response = (SoapMessage) messageContext.getResponse();
				SoapBody body = response.getSoapBody();
				body.addClientOrSenderFault(be.getMessage(), Locale.FRENCH);
			} catch (Exception e) {
				return false;
			}
			return true;
		}
		return false;
	}

	private static Throwable unwrap(Exception ex) {
		Throwable t = ex;
		if (ex instanceof InvocationTargetException ite && ite.getCause() != null) {
			t = ite.getCause();
		} else if (ex.getCause() instanceof BusinessException) {
			t = ex.getCause();
		}
		return t;
	}
}
