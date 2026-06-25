package tn.educanet.pfe.endpoint;

import java.util.List;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.tn.educanet.pfe.api.eleves.schema.DeleteEleveRequestType;
import com.tn.educanet.pfe.api.eleves.schema.DeleteEleveResponseType;
import com.tn.educanet.pfe.api.eleves.schema.EleveDto;
import com.tn.educanet.pfe.api.eleves.schema.EleveDtoList;
import com.tn.educanet.pfe.api.eleves.schema.EleveRequest;
import com.tn.educanet.pfe.api.eleves.schema.GetElevesListQueryType;
import com.tn.educanet.pfe.api.eleves.schema.ObjectFactory;
import com.tn.educanet.pfe.api.eleves.schema.UpdateEleveRequestType;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBElement;
import tn.educanet.pfe.service.EleveService;

@Endpoint
public class EleveEndpoint {

	public static final String NS = "http://www.educanet.tn.com/pfe/api/eleves/schema";

	@Resource
	private EleveService service;
	private final ObjectFactory factory = new ObjectFactory();

	@PayloadRoot(namespace = NS, localPart = "GetElevesListQuery")
	@ResponsePayload
	public JAXBElement<EleveDtoList> lister(@RequestPayload JAXBElement<GetElevesListQueryType> request) {
		GetElevesListQueryType query = request != null ? request.getValue() : null;
		List<EleveDto> items = service.listerFiltres(query != null ? query.getNiveauId() : null,
				query != null ? query.getClasseId() : null, query != null ? query.getQ() : null);
		EleveDtoList response = factory.createEleveDtoList();
		response.getItem().addAll(items);
		return factory.createEleveListResponse(response);
	}

	@PayloadRoot(namespace = NS, localPart = "PostEleveBody")
	@ResponsePayload
	public JAXBElement<EleveDto> creer(@RequestPayload JAXBElement<EleveRequest> request) {
		EleveDto created = service.creer(request.getValue());
		return factory.createEleveResponse(created);
	}

	@PayloadRoot(namespace = NS, localPart = "PutEleveBody")
	@ResponsePayload
	public JAXBElement<EleveDto> modifier(@RequestPayload JAXBElement<UpdateEleveRequestType> request) {
		UpdateEleveRequestType value = request.getValue();
		EleveDto updated = service.modifier(value.getId(), value.getBody());
		return factory.createEleveResponse(updated);
	}

	@PayloadRoot(namespace = NS, localPart = "DeleteEleveRequest")
	@ResponsePayload
	public JAXBElement<DeleteEleveResponseType> supprimer(
			@RequestPayload JAXBElement<DeleteEleveRequestType> request) {
		service.supprimer(request.getValue().getId());
		DeleteEleveResponseType response = factory.createDeleteEleveResponseType();
		response.setSuccess(true);
		return factory.createDeleteEleveResponse(response);
	}
}
