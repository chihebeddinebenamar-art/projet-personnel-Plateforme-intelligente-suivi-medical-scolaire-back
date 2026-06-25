package tn.educanet.pfe.endpoint;

import java.util.List;

import org.dozer.Mapper;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.tn.educanet.pfe.api.niveaux.schema.DeleteNiveauRequestType;
import com.tn.educanet.pfe.api.niveaux.schema.DeleteNiveauResponseType;
import com.tn.educanet.pfe.api.niveaux.schema.GetNiveauxListQueryType;
import com.tn.educanet.pfe.api.niveaux.schema.NiveauDto;
import com.tn.educanet.pfe.api.niveaux.schema.NiveauDtoList;
import com.tn.educanet.pfe.api.niveaux.schema.NiveauRequest;
import com.tn.educanet.pfe.api.niveaux.schema.ObjectFactory;
import com.tn.educanet.pfe.api.niveaux.schema.UpdateNiveauRequestType;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBElement;
import tn.educanet.pfe.service.NiveauService;

@Endpoint
public class NiveauEndpoint {

	public static final String NS = "http://www.educanet.tn.com/pfe/api/niveaux/schema";

	@Resource
	private Mapper mapper;

	@Resource
	private NiveauService service;
	private final ObjectFactory factory = new ObjectFactory();

	@PayloadRoot(namespace = NS, localPart = "GetNiveauxListQuery")
	@ResponsePayload
	public JAXBElement<NiveauDtoList> lister(@RequestPayload JAXBElement<GetNiveauxListQueryType> request) {
		GetNiveauxListQueryType query = request != null ? request.getValue() : null;
		List<NiveauDto> items = service.lister(query != null ? query.getAnnee() : null,
				query != null ? query.getNom() : null);
		NiveauDtoList response = factory.createNiveauDtoList();
		for (NiveauDto dto : items) {
			com.tn.educanet.pfe.api.niveaux.schema.NiveauDto ws = mapper.map(dto,
					com.tn.educanet.pfe.api.niveaux.schema.NiveauDto.class);
			response.getItem().add(ws);
		}
		return factory.createNiveauListResponse(response);
	}

	@PayloadRoot(namespace = NS, localPart = "PostNiveauBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.niveaux.schema.NiveauDto> creer(
			@RequestPayload JAXBElement<NiveauRequest> request) {
		NiveauRequest apiRequest = mapper.map(request.getValue(), NiveauRequest.class);
		NiveauDto created = service.creer(apiRequest);
		com.tn.educanet.pfe.api.niveaux.schema.NiveauDto response = mapper.map(created,
				com.tn.educanet.pfe.api.niveaux.schema.NiveauDto.class);
		return factory.createNiveauResponse(response);
	}

	@PayloadRoot(namespace = NS, localPart = "PutNiveauBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.niveaux.schema.NiveauDto> modifier(
			@RequestPayload JAXBElement<UpdateNiveauRequestType> request) {
		UpdateNiveauRequestType value = request.getValue();
		NiveauRequest apiRequest = mapper.map(value.getBody(), NiveauRequest.class);
		NiveauDto updated = service.modifier(value.getId(), apiRequest);
		com.tn.educanet.pfe.api.niveaux.schema.NiveauDto response = mapper.map(updated,
				com.tn.educanet.pfe.api.niveaux.schema.NiveauDto.class);
		return factory.createNiveauResponse(response);
	}

	@PayloadRoot(namespace = NS, localPart = "DeleteNiveauRequest")
	@ResponsePayload
	public JAXBElement<DeleteNiveauResponseType> supprimer(
			@RequestPayload JAXBElement<DeleteNiveauRequestType> request) {
		service.supprimer(request.getValue().getId());
		DeleteNiveauResponseType response = factory.createDeleteNiveauResponseType();
		response.setSuccess(true);
		return factory.createDeleteNiveauResponse(response);
	}
}
