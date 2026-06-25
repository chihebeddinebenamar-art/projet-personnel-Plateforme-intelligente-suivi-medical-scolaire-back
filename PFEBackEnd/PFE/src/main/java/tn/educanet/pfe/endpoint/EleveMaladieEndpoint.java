package tn.educanet.pfe.endpoint;

import java.util.List;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.tn.educanet.pfe.api.eleves.maladies.schema.DeleteEleveMaladieRequestType;
import com.tn.educanet.pfe.api.eleves.maladies.schema.DeleteEleveMaladieResponseType;
import com.tn.educanet.pfe.api.eleves.maladies.schema.GetEleveMaladiesListQueryType;
import com.tn.educanet.pfe.api.eleves.maladies.schema.MaladieEleveDto;
import com.tn.educanet.pfe.api.eleves.maladies.schema.MaladieEleveDtoList;
import com.tn.educanet.pfe.api.eleves.maladies.schema.ObjectFactory;
import com.tn.educanet.pfe.api.eleves.maladies.schema.UpdateEleveMaladieRequestType;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBElement;
import tn.educanet.pfe.service.EleveMaladieService;

@Endpoint
public class EleveMaladieEndpoint {

	public static final String NS = "http://www.educanet.tn.com/pfe/api/eleves/maladies/schema";

	@Resource
	private EleveMaladieService service;

	private final ObjectFactory factory = new ObjectFactory();

	@PayloadRoot(namespace = NS, localPart = "GetEleveMaladiesListQuery")
	@ResponsePayload
	public JAXBElement<MaladieEleveDtoList> lister(@RequestPayload JAXBElement<GetEleveMaladiesListQueryType> request) {
		Long eleveId = request != null && request.getValue() != null ? request.getValue().getEleveId() : null;
		List<MaladieEleveDto> items = service.lister(eleveId);
		MaladieEleveDtoList response = factory.createMaladieEleveDtoList();
		for (MaladieEleveDto dto : items) {
			response.getItem().add(dto);
		}
		return factory.createMaladieListResponse(response);
	}

	@PayloadRoot(namespace = NS, localPart = "PostMaladieBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.eleves.maladies.schema.MaladieEleveDto> creer(
			@RequestPayload JAXBElement<com.tn.educanet.pfe.api.eleves.maladies.schema.CreateEleveMaladieRequestType> request) {
		com.tn.educanet.pfe.api.eleves.maladies.schema.CreateEleveMaladieRequestType value = request.getValue();
		var created = service.creer(value.getEleveId(), value.getBody());
		return factory.createMaladieResponse(created);
	}

	@PayloadRoot(namespace = NS, localPart = "PutMaladieBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.eleves.maladies.schema.MaladieEleveDto> modifier(
			@RequestPayload JAXBElement<UpdateEleveMaladieRequestType> request) {
		UpdateEleveMaladieRequestType value = request.getValue();
		var updated = service.modifier(value.getEleveId(), value.getId(), value.getBody());
		return factory.createMaladieResponse(updated);
	}

	@PayloadRoot(namespace = NS, localPart = "DeleteEleveMaladieRequest")
	@ResponsePayload
	public JAXBElement<DeleteEleveMaladieResponseType> supprimer(
			@RequestPayload JAXBElement<DeleteEleveMaladieRequestType> request) {
		DeleteEleveMaladieRequestType v = request.getValue();
		service.supprimer(v.getEleveId(), v.getId());
		DeleteEleveMaladieResponseType response = factory.createDeleteEleveMaladieResponseType();
		response.setSuccess(true);
		return factory.createDeleteEleveMaladieResponse(response);
	}

}
