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
import tn.educanet.pfe.service.AiRecommendationResult;
import tn.educanet.pfe.service.AiRecommendationService;

@Endpoint
public class AiRecommendationEndpoint {

	public static final String NS = EleveEndpoint.NS;

	@Resource
	private AiRecommendationService aiRecommendationService;

	@PayloadRoot(namespace = NS, localPart = "GenerateAiRecommendationsBody")
	@ResponsePayload
	public Element generateAiRecommendations(@RequestPayload Element request) {
		Long eleveId = parseLong(firstChildText(request, "eleveId"));
		AiRecommendationResult result = aiRecommendationService.generateForEleve(eleveId);
		return buildResponse(result);
	}

	private Element buildResponse(AiRecommendationResult result) {
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element response = doc.createElementNS(NS, "ns2:GenerateAiRecommendationsResponse");
			doc.appendChild(response);
			appendText(doc, response, "ok", Boolean.toString(result.ok()));
			appendText(doc, response, "message", result.message());
			appendText(doc, response, "patientSummary", result.patientSummary());
			appendText(doc, response, "statisticalSummary", result.statisticalSummary());
			appendText(doc, response, "businessRules", result.businessRules());
			appendText(doc, response, "llmPrompt", result.llmPrompt());
			appendText(doc, response, "recommendations", result.recommendations());
			appendText(doc, response, "vaccinationPlan", result.vaccinationPlan());
			appendText(doc, response, "preventionAdvice", result.preventionAdvice());
			return response;
		} catch (Exception ex) {
			throw new IllegalStateException("Impossible de construire GenerateAiRecommendationsResponse", ex);
		}
	}

	private void appendText(Document doc, Element parent, String localName, String value) {
		if (!StringUtils.hasText(value) && !"ok".equals(localName)) {
			return;
		}
		Element el = doc.createElementNS(NS, "ns2:" + localName);
		el.setTextContent(value);
		parent.appendChild(el);
	}

	private Long parseLong(String value) {
		try {
			return StringUtils.hasText(value) ? Long.valueOf(value.trim()) : null;
		} catch (Exception ex) {
			return null;
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
