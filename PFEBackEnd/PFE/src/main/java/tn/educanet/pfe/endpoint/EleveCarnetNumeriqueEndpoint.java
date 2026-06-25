package tn.educanet.pfe.endpoint;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.tn.educanet.pfe.api.eleves.carnetnumerique.schema.CarnetImageResponseType;
import com.tn.educanet.pfe.api.eleves.carnetnumerique.schema.CarnetStatusResponseType;
import com.tn.educanet.pfe.api.eleves.carnetnumerique.schema.CreateCarnetUploadRequestType;
import com.tn.educanet.pfe.api.eleves.carnetnumerique.schema.DeleteCarnetNumeriqueRequestType;
import com.tn.educanet.pfe.api.eleves.carnetnumerique.schema.DeleteCarnetNumeriqueResponseType;
import com.tn.educanet.pfe.api.eleves.carnetnumerique.schema.GetCarnetImageQueryType;
import com.tn.educanet.pfe.api.eleves.carnetnumerique.schema.GetCarnetStatusQueryType;
import com.tn.educanet.pfe.api.eleves.carnetnumerique.schema.ObjectFactory;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBElement;
import tn.educanet.pfe.repository.EleveCarnetNumeriqueRepository;
import tn.educanet.pfe.service.EleveCarnetNumeriqueService;

/**
 * Carnet numérique élève (SOAP). L’image est renvoyée en base64 dans {@link CarnetImageResponseType}.
 */
@Endpoint
public class EleveCarnetNumeriqueEndpoint {

	public static final String NS = "http://www.educanet.tn.com/pfe/api/eleves/carnet-numerique/schema";

	@Resource
	private EleveCarnetNumeriqueService eleveCarnetNumeriqueService;

	@Resource
	private EleveCarnetNumeriqueRepository eleveCarnetNumeriqueRepository;

	private final ObjectFactory factory = new ObjectFactory();

	@PayloadRoot(namespace = NS, localPart = "GetCarnetStatusQuery")
	@ResponsePayload
	public JAXBElement<CarnetStatusResponseType> status(
			@RequestPayload JAXBElement<GetCarnetStatusQueryType> request) {
		Long eleveId = request.getValue().getEleveId();
		boolean present = eleveCarnetNumeriqueService.hasImage(eleveId);
		Long version = eleveCarnetNumeriqueRepository.findByEleveId(eleveId)
				.map(c -> c.getUpdatedAt().toEpochMilli()).orElse(null);
		CarnetStatusResponseType body = factory.createCarnetStatusResponseType();
		body.setPresent(present);
		body.setVersion(version);
		body.setDescription(eleveCarnetNumeriqueService.getDescription(eleveId));
		java.util.List<Long> photoIds = eleveCarnetNumeriqueService.listPhotoIds(eleveId);
		if (body.getPhotoId() != null) {
			body.getPhotoId().addAll(photoIds);
		}
		return factory.createCarnetStatusResponse(body);
	}

	@PayloadRoot(namespace = NS, localPart = "GetCarnetImageQuery")
	@ResponsePayload
	public JAXBElement<CarnetImageResponseType> image(
			@RequestPayload JAXBElement<GetCarnetImageQueryType> request) {
		Long eleveId = request.getValue().getEleveId();
		Long photoId = request.getValue().getPhotoId();
		eleveCarnetNumeriqueService.syncLegacyCarnetPhotos(eleveId);
		CarnetImageResponseType body = factory.createCarnetImageResponseType();
		if (!eleveCarnetNumeriqueService.hasImage(eleveId)) {
			return factory.createCarnetImageResponse(body);
		}
		body.setContentType(eleveCarnetNumeriqueService.resolveContentType(eleveId, photoId));
		body.setData(eleveCarnetNumeriqueService.getImageBytes(eleveId, photoId));
		return factory.createCarnetImageResponse(body);
	}

	@PayloadRoot(namespace = NS, localPart = "PostCarnetNumeriqueBody")
	@ResponsePayload
	public JAXBElement<CarnetStatusResponseType> upload(
			@RequestPayload JAXBElement<CreateCarnetUploadRequestType> request) {
		CreateCarnetUploadRequestType v = request.getValue();
		eleveCarnetNumeriqueService.upload(v.getEleveId(), v.getBody().getImage(), v.getBody().getDescription());
		Long version = eleveCarnetNumeriqueRepository.findByEleveId(v.getEleveId())
				.map(c -> c.getUpdatedAt().toEpochMilli()).orElse(null);
		CarnetStatusResponseType body = factory.createCarnetStatusResponseType();
		body.setPresent(true);
		body.setVersion(version);
		body.setDescription(eleveCarnetNumeriqueService.getDescription(v.getEleveId()));
		java.util.List<Long> photoIds = eleveCarnetNumeriqueService.listPhotoIds(v.getEleveId());
		if (body.getPhotoId() != null) {
			body.getPhotoId().addAll(photoIds);
		}
		return factory.createCarnetStatusResponse(body);
	}

	@PayloadRoot(namespace = NS, localPart = "DeleteCarnetNumeriqueRequest")
	@ResponsePayload
	public JAXBElement<DeleteCarnetNumeriqueResponseType> supprimer(
			@RequestPayload JAXBElement<DeleteCarnetNumeriqueRequestType> request) {
		Long eleveId = request.getValue().getEleveId();
		Long photoId = request.getValue().getPhotoId();
		if (photoId != null) {
			eleveCarnetNumeriqueService.supprimerPhoto(eleveId, photoId);
		} else {
			eleveCarnetNumeriqueService.supprimer(eleveId);
		}
		DeleteCarnetNumeriqueResponseType body = factory.createDeleteCarnetNumeriqueResponseType();
		body.setSuccess(true);
		return factory.createDeleteCarnetNumeriqueResponse(body);
	}
}
