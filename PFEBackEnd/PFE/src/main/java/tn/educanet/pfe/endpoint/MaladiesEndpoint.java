package tn.educanet.pfe.endpoint;

import java.util.List;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.tn.educanet.pfe.api.maladies.schema.GetMaladiesListQueryType;
import com.tn.educanet.pfe.api.maladies.schema.MaladieEleveListDto;
import com.tn.educanet.pfe.api.maladies.schema.MaladieEleveListDtoList;
import com.tn.educanet.pfe.api.maladies.schema.ObjectFactory;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBElement;
import tn.educanet.pfe.service.EleveMaladieService;

@Endpoint
public class MaladiesEndpoint {

	public static final String NS = "http://www.educanet.tn.com/pfe/api/maladies/schema";

	@Resource
	private EleveMaladieService service;

	private final ObjectFactory factory = new ObjectFactory();

	@PayloadRoot(namespace = NS, localPart = "GetMaladiesListQuery")
	@ResponsePayload
	public JAXBElement<MaladieEleveListDtoList> lister(@RequestPayload JAXBElement<GetMaladiesListQueryType> request) {
		GetMaladiesListQueryType query = request != null ? request.getValue() : null;
		List<MaladieEleveListDto> items = service.listerFiltres(
				query != null ? query.getNiveauId() : null, query != null ? query.getClasseId() : null,
				query != null ? query.getQ() : null);
		MaladieEleveListDtoList response = factory.createMaladieEleveListDtoList();
		for (MaladieEleveListDto dto : items) {
			response.getItem().add(dto);
		}
		return factory.createMaladieListResponse(response);
	}
}
