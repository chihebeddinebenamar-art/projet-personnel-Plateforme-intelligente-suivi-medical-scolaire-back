package tn.educanet.pfe.endpoint;

import java.util.List;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.tn.educanet.pfe.api.consultations.schema.ConsultationDto;
import com.tn.educanet.pfe.api.consultations.schema.ConsultationDtoList;
import com.tn.educanet.pfe.api.consultations.schema.ConsultationRequest;
import com.tn.educanet.pfe.api.consultations.schema.DeleteConsultationRequestType;
import com.tn.educanet.pfe.api.consultations.schema.DeleteConsultationResponseType;
import com.tn.educanet.pfe.api.consultations.schema.GetConsultationsListQueryType;
import com.tn.educanet.pfe.api.consultations.schema.ObjectFactory;
import com.tn.educanet.pfe.api.consultations.schema.UpdateConsultationRequestType;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBElement;
import tn.educanet.pfe.service.ConsultationService;

@Endpoint
public class ConsultationEndpoint {

	public static final String NS = "http://www.educanet.tn.com/pfe/api/consultations/schema";

	@Resource
	private ConsultationService service;

	private final ObjectFactory factory = new ObjectFactory();

	@PayloadRoot(namespace = NS, localPart = "GetConsultationsListQuery")
	@ResponsePayload
	public JAXBElement<ConsultationDtoList> lister(@RequestPayload JAXBElement<GetConsultationsListQueryType> request) {
		GetConsultationsListQueryType query = request != null ? request.getValue() : null;
		Long eleveId = query != null ? query.getEleveId() : null;
		List<ConsultationDto> items = eleveId != null ? service.listerParEleve(eleveId)
				: service.listerFiltres(query != null ? query.getNiveauId() : null,
						query != null ? query.getClasseId() : null, query != null ? query.getQ() : null);
		ConsultationDtoList response = factory.createConsultationDtoList();
		for (ConsultationDto dto : items) {
			response.getItem().add(dto);
		}
		return factory.createConsultationListResponse(response);
	}

	@PayloadRoot(namespace = NS, localPart = "PostConsultationBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.consultations.schema.ConsultationDto> creer(
			@RequestPayload JAXBElement<ConsultationRequest> request) {
		ConsultationDto created = service.creer(request.getValue());
		return factory.createConsultationResponse(created);
	}

	@PayloadRoot(namespace = NS, localPart = "PutConsultationBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.consultations.schema.ConsultationDto> modifier(
			@RequestPayload JAXBElement<UpdateConsultationRequestType> request) {
		UpdateConsultationRequestType value = request.getValue();
		ConsultationDto updated = service.modifier(value.getId(), value.getBody());
		return factory.createConsultationResponse(updated);
	}

	@PayloadRoot(namespace = NS, localPart = "DeleteConsultationRequest")
	@ResponsePayload
	public JAXBElement<DeleteConsultationResponseType> supprimer(
			@RequestPayload JAXBElement<DeleteConsultationRequestType> request) {
		service.supprimer(request.getValue().getId());
		DeleteConsultationResponseType response = factory.createDeleteConsultationResponseType();
		response.setSuccess(true);
		return factory.createDeleteConsultationResponse(response);
	}

}
