package tn.educanet.pfe.endpoint;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.tn.educanet.pfe.api.eleves.schema.ObjectFactory;
import com.tn.educanet.pfe.api.eleves.schema.VerifierParentRequestType;
import com.tn.educanet.pfe.api.eleves.schema.VerifierParentResponseType;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilderFactory;
import tn.educanet.pfe.persistence.SecurityAccount;
import tn.educanet.pfe.repository.SecurityAccountRepository;
import tn.educanet.pfe.service.ChangeParentPasswordResult;
import tn.educanet.pfe.service.EleveService;

@Endpoint
public class ParentAuthEndpoint {

	public static final String NS = EleveEndpoint.NS;

	@Resource
	private EleveService eleveService;
	@Resource
	private SecurityAccountRepository securityAccountRepository;

	@Value("${educanet.security.admin-username:admin}")
	private String adminUsername;

	@Value("${educanet.security.admin-password:admin123}")
	private String adminPassword;

	@Value("${educanet.security.infirmier-username:infirmier}")
	private String infirmierUsername;

	@Value("${educanet.security.infirmier-password:infirmier123}")
	private String infirmierPassword;

	private final ObjectFactory factory = new ObjectFactory();

	@PayloadRoot(namespace = NS, localPart = "VerifierParentBody")
	@ResponsePayload
	public JAXBElement<VerifierParentResponseType> verifierParent(
			@RequestPayload JAXBElement<VerifierParentRequestType> request) {
		VerifierParentRequestType body = request != null ? request.getValue() : null;
		String tel = body != null ? body.getNumeroTelephone() : null;
		String pwd = body != null ? body.getMotDePasse() : null;
		boolean ok = eleveService.verifierCredentialsParent(tel, pwd);
		VerifierParentResponseType resp = factory.createVerifierParentResponseType();
		resp.setOk(ok);
		return factory.createVerifierParentResponse(resp);
	}

	@PayloadRoot(namespace = NS, localPart = "ChangeParentPasswordBody")
	@ResponsePayload
	public Element changeParentPassword(@RequestPayload Element request) {
		String tel = firstChildText(request, "numeroTelephone");
		String ancien = firstChildText(request, "ancienMotDePasse");
		String nouveau = firstChildText(request, "nouveauMotDePasse");
		ChangeParentPasswordResult result = eleveService.changerMotDePasseParent(tel, ancien, nouveau);
		return buildChangeParentPasswordResponse(result.ok(), result.message());
	}

	@PayloadRoot(namespace = NS, localPart = "VerifierCompteBody")
	@ResponsePayload
	public Element verifierCompte(@RequestPayload Element request) {
		String login = firstChildText(request, "login");
		String pwd = firstChildText(request, "motDePasse");
		boolean ok = false;
		String role = null;
		if (StringUtils.hasText(login) && StringUtils.hasText(pwd)) {
			String u = login.trim();
			SecurityAccount account = securityAccountRepository.findByUsernameIgnoreCase(u).orElse(null);
			if (account != null && account.isEnabled()
					&& StringUtils.hasText(account.getPassword())
					&& account.getPassword().equals(pwd)
					&& StringUtils.hasText(account.getRole())) {
				ok = true;
				role = account.getRole().trim().toUpperCase();
			} else if (adminUsername.equalsIgnoreCase(u) && adminPassword.equals(pwd)) {
				ok = true;
				role = "ADMIN";
			} else if (infirmierUsername.equalsIgnoreCase(u) && infirmierPassword.equals(pwd)) {
				ok = true;
				role = "INFIRMIER";
			} else if (eleveService.verifierCredentialsParent(u, pwd)) {
				ok = true;
				role = "PARENT";
			}
		}
		return buildVerifierCompteResponse(ok, role);
	}

	private Element buildChangeParentPasswordResponse(boolean ok, String message) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element response = doc.createElementNS(NS, "ns2:ChangeParentPasswordResponse");
			doc.appendChild(response);
			Element okEl = doc.createElementNS(NS, "ns2:ok");
			okEl.setTextContent(Boolean.toString(ok));
			response.appendChild(okEl);
			if (StringUtils.hasText(message)) {
				Element msgEl = doc.createElementNS(NS, "ns2:message");
				msgEl.setTextContent(message);
				response.appendChild(msgEl);
			}
			return response;
		} catch (Exception e) {
			throw new IllegalStateException("Impossible de construire ChangeParentPasswordResponse", e);
		}
	}

	private Element buildVerifierCompteResponse(boolean ok, String role) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element response = doc.createElementNS(NS, "ns2:VerifierCompteResponse");
			doc.appendChild(response);
			Element okEl = doc.createElementNS(NS, "ns2:ok");
			okEl.setTextContent(Boolean.toString(ok));
			response.appendChild(okEl);
			if (StringUtils.hasText(role)) {
				Element roleEl = doc.createElementNS(NS, "ns2:role");
				roleEl.setTextContent(role);
				response.appendChild(roleEl);
			}
			return response;
		} catch (Exception e) {
			throw new IllegalStateException("Impossible de construire la réponse VerifierCompteResponse", e);
		}
	}

	private String firstChildText(Element parent, String localName) {
		if (parent == null || !StringUtils.hasText(localName)) {
			return null;
		}
		var children = parent.getElementsByTagNameNS("*", localName);
		if (children.getLength() == 0) {
			return null;
		}
		String value = children.item(0).getTextContent();
		return StringUtils.hasText(value) ? value.trim() : null;
	}
}
