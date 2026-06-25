package tn.educanet.pfe.endpoint;

import java.util.List;

import org.dozer.Mapper;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.tn.educanet.pfe.api.vaccins.types.schema.DeleteTypeVaccinRequestType;
import com.tn.educanet.pfe.api.vaccins.types.schema.DeleteTypeVaccinResponseType;
import com.tn.educanet.pfe.api.vaccins.types.schema.GetTypesVaccinListQueryType;
import com.tn.educanet.pfe.api.vaccins.types.schema.ObjectFactory;
import com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinDto;
import com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinDtoList;
import com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinRequest;
import com.tn.educanet.pfe.api.vaccins.types.schema.UpdateTypeVaccinRequestType;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBElement;
import tn.educanet.pfe.service.TypeVaccinService;

@Endpoint
public class TypeVaccinEndpoint {

	public static final String NS = "http://www.educanet.tn.com/pfe/api/vaccins/types/schema";

	@Resource
	private Mapper mapper;

	@Resource
	private TypeVaccinService service;

	private final ObjectFactory factory = new ObjectFactory();

	@PayloadRoot(namespace = NS, localPart = "GetTypesVaccinListQuery")
	@ResponsePayload
	public JAXBElement<TypeVaccinDtoList> lister(@RequestPayload JAXBElement<GetTypesVaccinListQueryType> request) {
		List<TypeVaccinDto> items = service.lister();
		TypeVaccinDtoList response = factory.createTypeVaccinDtoList();
		for (TypeVaccinDto dto : items) {
			response.getItem().add(mapper.map(dto, com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinDto.class));
		}
		return factory.createTypeVaccinListResponse(response);
	}

	@PayloadRoot(namespace = NS, localPart = "PostTypeVaccinBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinDto> creer(
			@RequestPayload JAXBElement<TypeVaccinRequest> request) {
		TypeVaccinRequest apiRequest = mapper.map(request.getValue(), TypeVaccinRequest.class);
		TypeVaccinDto created = service.creer(apiRequest);
		com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinDto out = mapper.map(created,
				com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinDto.class);
		return factory.createTypeVaccinResponse(out);
	}

	@PayloadRoot(namespace = NS, localPart = "PutTypeVaccinBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinDto> modifier(
			@RequestPayload JAXBElement<UpdateTypeVaccinRequestType> request) {
		UpdateTypeVaccinRequestType value = request.getValue();
		TypeVaccinRequest apiRequest = mapper.map(value.getBody(), TypeVaccinRequest.class);
		TypeVaccinDto updated = service.modifier(value.getId(), apiRequest);
		com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinDto out = mapper.map(updated,
				com.tn.educanet.pfe.api.vaccins.types.schema.TypeVaccinDto.class);
		return factory.createTypeVaccinResponse(out);
	}

	@PayloadRoot(namespace = NS, localPart = "DeleteTypeVaccinRequest")
	@ResponsePayload
	public JAXBElement<DeleteTypeVaccinResponseType> supprimer(
			@RequestPayload JAXBElement<DeleteTypeVaccinRequestType> request) {
		service.supprimer(request.getValue().getId());
		DeleteTypeVaccinResponseType response = factory.createDeleteTypeVaccinResponseType();
		response.setSuccess(true);
		return factory.createDeleteTypeVaccinResponse(response);
	}
}
