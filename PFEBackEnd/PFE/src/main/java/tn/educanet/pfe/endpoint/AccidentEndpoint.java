package tn.educanet.pfe.endpoint;

import java.util.List;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.tn.educanet.pfe.api.accidents.schema.AccidentDtoList;
import com.tn.educanet.pfe.api.accidents.schema.AccidentDto;
import com.tn.educanet.pfe.api.accidents.schema.AccidentRequest;
import com.tn.educanet.pfe.api.accidents.schema.DeleteAccidentRequestType;
import com.tn.educanet.pfe.api.accidents.schema.DeleteAccidentResponseType;
import com.tn.educanet.pfe.api.accidents.schema.GetAccidentsListQueryType;
import com.tn.educanet.pfe.api.accidents.schema.ObjectFactory;
import com.tn.educanet.pfe.api.accidents.schema.UpdateAccidentRequestType;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBElement;
import tn.educanet.pfe.service.AccidentService;

@Endpoint
public class AccidentEndpoint {

	public static final String NS = "http://www.educanet.tn.com/pfe/api/accidents/schema";

	@Resource
	private AccidentService service;
	private final ObjectFactory factory = new ObjectFactory();

	@PayloadRoot(namespace = NS, localPart = "GetAccidentsListQuery")
	@ResponsePayload
	public JAXBElement<AccidentDtoList> lister(@RequestPayload JAXBElement<GetAccidentsListQueryType> request) {
		GetAccidentsListQueryType query = request != null ? request.getValue() : null;
		Long eleveId = query != null ? query.getEleveId() : null;
		List<AccidentDto> items = eleveId != null ? service.listerParEleve(eleveId)
				: service.listerFiltres(query != null ? query.getNiveauId() : null,
						query != null ? query.getClasseId() : null, query != null ? query.getQ() : null);
		AccidentDtoList response = factory.createAccidentDtoList();
		for (AccidentDto dto : items) {
			response.getItem().add(dto);
		}
		return factory.createAccidentListResponse(response);
	}

	@PayloadRoot(namespace = NS, localPart = "PostAccidentBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.accidents.schema.AccidentDto> creer(
			@RequestPayload JAXBElement<AccidentRequest> request) {
		AccidentDto created = service.creer(request.getValue());
		return factory.createAccidentResponse(created);
	}

	@PayloadRoot(namespace = NS, localPart = "PutAccidentBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.accidents.schema.AccidentDto> modifier(
			@RequestPayload JAXBElement<UpdateAccidentRequestType> request) {
		UpdateAccidentRequestType value = request.getValue();
		AccidentDto updated = service.modifier(value.getId(), value.getBody());
		return factory.createAccidentResponse(updated);
	}

	@PayloadRoot(namespace = NS, localPart = "DeleteAccidentRequest")
	@ResponsePayload
	public JAXBElement<DeleteAccidentResponseType> supprimer(
			@RequestPayload JAXBElement<DeleteAccidentRequestType> request) {
		service.supprimer(request.getValue().getId());
		DeleteAccidentResponseType response = factory.createDeleteAccidentResponseType();
		response.setSuccess(true);
		return factory.createDeleteAccidentResponse(response);
	}

}
