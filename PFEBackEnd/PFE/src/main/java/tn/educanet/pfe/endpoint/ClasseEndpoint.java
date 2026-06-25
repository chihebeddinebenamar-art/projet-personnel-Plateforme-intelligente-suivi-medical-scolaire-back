package tn.educanet.pfe.endpoint;

import java.util.List;

import org.dozer.Mapper;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.tn.educanet.pfe.api.classes.schema.ClasseDtoList;
import com.tn.educanet.pfe.api.classes.schema.ClasseDto;
import com.tn.educanet.pfe.api.classes.schema.ClasseRequest;
import com.tn.educanet.pfe.api.classes.schema.DeleteClasseRequestType;
import com.tn.educanet.pfe.api.classes.schema.DeleteClasseResponseType;
import com.tn.educanet.pfe.api.classes.schema.GetClassesListQueryType;
import com.tn.educanet.pfe.api.classes.schema.ObjectFactory;
import com.tn.educanet.pfe.api.classes.schema.UpdateClasseRequestType;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBElement;
import tn.educanet.pfe.service.ClasseService;

@Endpoint
public class ClasseEndpoint {

	public static final String NS = "http://www.educanet.tn.com/pfe/api/classes/schema";

	@Resource
	private Mapper mapper;

	@Resource
	private ClasseService service;
	private final ObjectFactory factory = new ObjectFactory();

	@PayloadRoot(namespace = NS, localPart = "GetClassesListQuery")
	@ResponsePayload
	public JAXBElement<ClasseDtoList> lister(@RequestPayload JAXBElement<GetClassesListQueryType> request) {
		GetClassesListQueryType query = request != null ? request.getValue() : null;
		List<ClasseDto> items = service.lister(query != null ? query.getNiveauId() : null,
				query != null ? query.getNom() : null);
		ClasseDtoList response = factory.createClasseDtoList();
		for (ClasseDto dto : items) {
			com.tn.educanet.pfe.api.classes.schema.ClasseDto ws = mapper.map(dto,
					com.tn.educanet.pfe.api.classes.schema.ClasseDto.class);
			response.getItem().add(ws);
		}
		return factory.createClasseListResponse(response);
	}

	@PayloadRoot(namespace = NS, localPart = "PostClasseBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.classes.schema.ClasseDto> creer(
			@RequestPayload JAXBElement<ClasseRequest> request) {
		ClasseRequest apiRequest = mapper.map(request.getValue(), ClasseRequest.class);
		ClasseDto created = service.creer(apiRequest);
		com.tn.educanet.pfe.api.classes.schema.ClasseDto response = mapper.map(created,
				com.tn.educanet.pfe.api.classes.schema.ClasseDto.class);
		return factory.createClasseResponse(response);
	}

	@PayloadRoot(namespace = NS, localPart = "PutClasseBody")
	@ResponsePayload
	public JAXBElement<com.tn.educanet.pfe.api.classes.schema.ClasseDto> modifier(
			@RequestPayload JAXBElement<UpdateClasseRequestType> request) {
		UpdateClasseRequestType value = request.getValue();
		ClasseRequest apiRequest = mapper.map(value.getBody(), ClasseRequest.class);
		ClasseDto updated = service.modifier(value.getId(), apiRequest);
		com.tn.educanet.pfe.api.classes.schema.ClasseDto response = mapper.map(updated,
				com.tn.educanet.pfe.api.classes.schema.ClasseDto.class);
		return factory.createClasseResponse(response);
	}

	@PayloadRoot(namespace = NS, localPart = "DeleteClasseRequest")
	@ResponsePayload
	public JAXBElement<DeleteClasseResponseType> supprimer(
			@RequestPayload JAXBElement<DeleteClasseRequestType> request) {
		service.supprimer(request.getValue().getId());
		DeleteClasseResponseType response = factory.createDeleteClasseResponseType();
		response.setSuccess(true);
		return factory.createDeleteClasseResponse(response);
	}
}
