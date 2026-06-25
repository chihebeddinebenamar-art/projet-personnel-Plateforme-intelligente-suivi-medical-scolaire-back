package tn.educanet.pfe.endpoint;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.util.StringUtils;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jakarta.annotation.Resource;
import tn.educanet.pfe.service.ParentNotificationService;

@Endpoint
public class ParentNotificationEndpoint {

	private static final String NS = EleveEndpoint.NS;

	@Resource
	private ParentNotificationService parentNotificationService;

	@PayloadRoot(namespace = NS, localPart = "PublishParentRiskNotificationBody")
	@ResponsePayload
	public Element publishParentRiskNotification(@RequestPayload Element request) {
		Long eleveId = parseLong(childText(request, "eleveId"));
		Integer riskScore = parseInt(childText(request, "riskScore"));
		String detail = childText(request, "detail");
		Document doc = createDocument();
		Element root = doc.createElementNS(NS, "ns2:PublishParentRiskNotificationResponse");
		doc.appendChild(root);
		if (eleveId == null) {
			append(doc, root, "ok", "false");
			append(doc, root, "message", "eleveId requis.");
			return root;
		}
		int score = riskScore != null ? riskScore : 0;
		String msg = parentNotificationService.publishEleveRiskElevated(eleveId, score, detail);
		boolean ok = !msg.contains("Numéro parent absent") && !msg.contains("introuvable")
				&& !msg.contains("manquant");
		append(doc, root, "ok", ok ? "true" : "false");
		append(doc, root, "message", msg);
		return root;
	}

	@PayloadRoot(namespace = NS, localPart = "GetParentNotificationsBody")
	@ResponsePayload
	public Element getParentNotifications(@RequestPayload Element request) {
		String numeroTelephone = childText(request, "numeroTelephone");
		Document doc = createDocument();
		Element root = doc.createElementNS(NS, "ns2:ParentNotificationsResponse");
		doc.appendChild(root);
		if (!StringUtils.hasText(numeroTelephone)) {
			return root;
		}
		for (var item : parentNotificationService.listByNumeroParent(numeroTelephone)) {
			Element row = doc.createElementNS(NS, "ns2:item");
			append(doc, row, "id", item.getId() != null ? String.valueOf(item.getId()) : null);
			append(doc, row, "eleveId", item.getEleveId() != null ? String.valueOf(item.getEleveId()) : null);
			append(doc, row, "eleveNomComplet", item.getEleveNomComplet());
			append(doc, row, "classeNom", item.getClasseNom());
			append(doc, row, "typeEvenement", item.getTypeEvenement());
			append(doc, row, "titre", item.getTitre());
			append(doc, row, "message", item.getMessage());
			append(doc, row, "createdAt", item.getCreatedAt() != null ? item.getCreatedAt().toString() : null);
			root.appendChild(row);
		}
		return root;
	}

	private static Document createDocument() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		} catch (Exception e) {
			throw new IllegalStateException("Impossible de construire la réponse SOAP", e);
		}
	}

	private static void append(Document doc, Element parent, String localName, String value) {
		if (!StringUtils.hasText(value)) {
			return;
		}
		Element el = doc.createElementNS(NS, "ns2:" + localName);
		el.setTextContent(value);
		parent.appendChild(el);
	}

	private static String childText(Element parent, String localName) {
		if (parent == null) {
			return null;
		}
		var nodes = parent.getElementsByTagNameNS("*", localName);
		if (nodes.getLength() == 0) {
			return null;
		}
		String value = nodes.item(0).getTextContent();
		return StringUtils.hasText(value) ? value.trim() : null;
	}

	private static Long parseLong(String value) {
		try {
			return StringUtils.hasText(value) ? Long.valueOf(value.trim()) : null;
		} catch (Exception e) {
			return null;
		}
	}

	private static Integer parseInt(String value) {
		try {
			return StringUtils.hasText(value) ? Integer.valueOf(value.trim()) : null;
		} catch (Exception e) {
			return null;
		}
	}
}
