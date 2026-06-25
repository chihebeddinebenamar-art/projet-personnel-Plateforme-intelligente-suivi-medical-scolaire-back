package tn.educanet.pfe.endpoint;

import java.util.List;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.tn.educanet.pfe.api.vaccinations.schema.DeleteVaccinationRequestType;
import com.tn.educanet.pfe.api.vaccinations.schema.DeleteVaccinationResponseType;
import com.tn.educanet.pfe.api.vaccinations.schema.GetVaccinationsListQueryType;
import com.tn.educanet.pfe.api.vaccinations.schema.ObjectFactory;
import com.tn.educanet.pfe.api.vaccinations.schema.UpdateVaccinationRequestType;
import com.tn.educanet.pfe.api.vaccinations.schema.VaccinationDto;
import com.tn.educanet.pfe.api.vaccinations.schema.VaccinationDtoList;
import com.tn.educanet.pfe.api.vaccinations.schema.VaccinationRequest;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBElement;
import tn.educanet.pfe.service.VaccinationService;

@Endpoint
public class VaccinationEndpoint {

	public static final String NS = "http://www.educanet.tn.com/pfe/api/vaccinations/schema";

	@Resource
	private VaccinationService service;

	private final ObjectFactory factory = new ObjectFactory();

	@PayloadRoot(namespace = NS, localPart = "GetVaccinationsListQuery")
	@ResponsePayload
	public JAXBElement<VaccinationDtoList> lister(@RequestPayload JAXBElement<GetVaccinationsListQueryType> request) {
		GetVaccinationsListQueryType query = request != null ? request.getValue() : null;
		Long eleveId = query != null ? query.getEleveId() : null;
		List<VaccinationDto> items = eleveId != null ? service.listerParEleve(eleveId)
				: service.listerFiltres(query != null ? query.getNiveauId() : null,
						query != null ? query.getClasseId() : null, query != null ? query.getTypeVaccinId() : null,
						query != null ? query.getQ() : null, query != null ? query.getNumeroLot() : null);
		VaccinationDtoList response = factory.createVaccinationDtoList();
		for (VaccinationDto dto : items) {
			response.getItem().add(dto);
		}
		return factory.createVaccinationListResponse(response);
	}

	@PayloadRoot(namespace = NS, localPart = "PostVaccinationBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.vaccinations.schema.VaccinationDto> creer(
			@RequestPayload JAXBElement<VaccinationRequest> request) {
		VaccinationDto created = service.creer(request.getValue());
		return factory.createVaccinationResponse(created);
	}

	@PayloadRoot(namespace = NS, localPart = "PutVaccinationBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.vaccinations.schema.VaccinationDto> modifier(
			@RequestPayload JAXBElement<UpdateVaccinationRequestType> request) {
		UpdateVaccinationRequestType value = request.getValue();
		VaccinationDto updated = service.modifier(value.getId(), value.getBody());
		return factory.createVaccinationResponse(updated);
	}

	@PayloadRoot(namespace = NS, localPart = "DeleteVaccinationRequest")
	@ResponsePayload
	public JAXBElement<DeleteVaccinationResponseType> supprimer(
			@RequestPayload JAXBElement<DeleteVaccinationRequestType> request) {
		service.supprimer(request.getValue().getId());
		DeleteVaccinationResponseType response = factory.createDeleteVaccinationResponseType();
		response.setSuccess(true);
		return factory.createDeleteVaccinationResponse(response);
	}

}
