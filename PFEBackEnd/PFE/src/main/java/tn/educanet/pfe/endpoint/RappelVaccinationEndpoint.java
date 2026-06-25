package tn.educanet.pfe.endpoint;

import java.util.List;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.tn.educanet.pfe.api.rappels.vaccination.schema.DeleteRappelRequestType;
import com.tn.educanet.pfe.api.rappels.vaccination.schema.DeleteRappelResponseType;
import com.tn.educanet.pfe.api.rappels.vaccination.schema.GetRappelsListQueryType;
import com.tn.educanet.pfe.api.rappels.vaccination.schema.ObjectFactory;
import com.tn.educanet.pfe.api.rappels.vaccination.schema.RappelVaccinationDtoList;
import com.tn.educanet.pfe.api.rappels.vaccination.schema.RappelVaccinationRequest;
import com.tn.educanet.pfe.api.rappels.vaccination.schema.UpdateRappelRequestType;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBElement;
import tn.educanet.pfe.service.RappelVaccinationService;

/**
 * Rappels de vaccination (SOAP).
 */
@Endpoint
public class RappelVaccinationEndpoint {

	public static final String NS = "http://www.educanet.tn.com/pfe/api/rappels-vaccination/schema";

	@Resource
	private RappelVaccinationService service;

	private final ObjectFactory factory = new ObjectFactory();

	@PayloadRoot(namespace = NS, localPart = "GetRappelsListQuery")
	@ResponsePayload
	public JAXBElement<RappelVaccinationDtoList> lister(
			@RequestPayload JAXBElement<GetRappelsListQueryType> request) {
		List<com.tn.educanet.pfe.api.rappels.vaccination.schema.RappelVaccinationDto> items = service.lister();
		RappelVaccinationDtoList response = factory.createRappelVaccinationDtoList();
		for (com.tn.educanet.pfe.api.rappels.vaccination.schema.RappelVaccinationDto dto : items) {
			response.getItem().add(dto);
		}
		return factory.createRappelListResponse(response);
	}

	@PayloadRoot(namespace = NS, localPart = "PostRappelBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.rappels.vaccination.schema.RappelVaccinationDto> creer(
			@RequestPayload JAXBElement<RappelVaccinationRequest> request) {
		var created = service.creer(request.getValue());
		return factory.createRappelResponse(created);
	}

	@PayloadRoot(namespace = NS, localPart = "PutRappelBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.rappels.vaccination.schema.RappelVaccinationDto> modifier(
			@RequestPayload JAXBElement<UpdateRappelRequestType> request) {
		UpdateRappelRequestType v = request.getValue();
		var updated = service.modifier(v.getId(), v.getBody());
		return factory.createRappelResponse(updated);
	}

	@PayloadRoot(namespace = NS, localPart = "DeleteRappelRequest")
	@ResponsePayload
	public JAXBElement<DeleteRappelResponseType> supprimer(
			@RequestPayload JAXBElement<DeleteRappelRequestType> request) {
		service.supprimer(request.getValue().getId());
		DeleteRappelResponseType response = factory.createDeleteRappelResponseType();
		response.setSuccess(true);
		return factory.createDeleteRappelResponse(response);
	}

}
