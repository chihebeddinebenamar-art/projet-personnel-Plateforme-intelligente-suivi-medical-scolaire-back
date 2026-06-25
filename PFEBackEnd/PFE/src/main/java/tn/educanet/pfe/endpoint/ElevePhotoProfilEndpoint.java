package tn.educanet.pfe.endpoint;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.tn.educanet.pfe.api.eleves.photoprofil.schema.DeletePhotoProfilRequestType;
import com.tn.educanet.pfe.api.eleves.photoprofil.schema.DeletePhotoProfilResponseType;
import com.tn.educanet.pfe.api.eleves.photoprofil.schema.GetPhotoProfilImageQueryType;
import com.tn.educanet.pfe.api.eleves.photoprofil.schema.GetPhotoProfilStatusQueryType;
import com.tn.educanet.pfe.api.eleves.photoprofil.schema.ObjectFactory;
import com.tn.educanet.pfe.api.eleves.photoprofil.schema.PhotoProfilImageResponseType;
import com.tn.educanet.pfe.api.eleves.photoprofil.schema.PhotoProfilStatusResponseType;

import jakarta.annotation.Resource;
import jakarta.xml.bind.JAXBElement;
import tn.educanet.pfe.repository.ElevePhotoProfilRepository;
import tn.educanet.pfe.service.ElevePhotoProfilService;

@Endpoint
public class ElevePhotoProfilEndpoint {

	public static final String NS = "http://www.educanet.tn.com/pfe/api/eleves/photo-profil/schema";

	@Resource
	private ElevePhotoProfilService elevePhotoProfilService;

	@Resource
	private ElevePhotoProfilRepository elevePhotoProfilRepository;

	private final ObjectFactory factory = new ObjectFactory();

	@PayloadRoot(namespace = NS, localPart = "GetPhotoProfilStatusQuery")
	@ResponsePayload
	public JAXBElement<PhotoProfilStatusResponseType> status(
			@RequestPayload JAXBElement<GetPhotoProfilStatusQueryType> request) {
		Long eleveId = request.getValue().getEleveId();
		boolean present = elevePhotoProfilService.hasImage(eleveId);
		Long version = elevePhotoProfilRepository.findByEleveId(eleveId).map(p -> p.getUpdatedAt().toEpochMilli())
				.orElse(null);
		PhotoProfilStatusResponseType body = factory.createPhotoProfilStatusResponseType();
		body.setPresent(present);
		body.setVersion(version);
		return factory.createPhotoProfilStatusResponse(body);
	}

	@PayloadRoot(namespace = NS, localPart = "GetPhotoProfilImageQuery")
	@ResponsePayload
	public JAXBElement<PhotoProfilImageResponseType> image(
			@RequestPayload JAXBElement<GetPhotoProfilImageQueryType> request) {
		Long eleveId = request.getValue().getEleveId();
		PhotoProfilImageResponseType body = factory.createPhotoProfilImageResponseType();
		if (!elevePhotoProfilService.hasImage(eleveId)) {
			return factory.createPhotoProfilImageResponse(body);
		}
		body.setContentType(elevePhotoProfilService.getContentType(eleveId));
		body.setData(elevePhotoProfilService.getImageBytes(eleveId));
		return factory.createPhotoProfilImageResponse(body);
	}

	@PayloadRoot(namespace = NS, localPart = "PostPhotoProfilBody")
	@ResponsePayload
	public JAXBElement<PhotoProfilStatusResponseType> upload(
			@RequestPayload JAXBElement<com.tn.educanet.pfe.api.eleves.photoprofil.schema.CreatePhotoProfilUploadRequestType> request) {
		var v = request.getValue();
		elevePhotoProfilService.upload(v.getEleveId(), v.getBody().getImage());
		Long version = elevePhotoProfilRepository.findByEleveId(v.getEleveId()).map(p -> p.getUpdatedAt().toEpochMilli())
				.orElse(null);
		PhotoProfilStatusResponseType body = factory.createPhotoProfilStatusResponseType();
		body.setPresent(true);
		body.setVersion(version);
		return factory.createPhotoProfilStatusResponse(body);
	}

	@PayloadRoot(namespace = NS, localPart = "DeletePhotoProfilRequest")
	@ResponsePayload
	public JAXBElement<DeletePhotoProfilResponseType> delete(
			@RequestPayload JAXBElement<DeletePhotoProfilRequestType> request) {
		Long eleveId = request.getValue().getEleveId();
		elevePhotoProfilService.supprimer(eleveId);
		DeletePhotoProfilResponseType body = factory.createDeletePhotoProfilResponseType();
		body.setSuccess(true);
		return factory.createDeletePhotoProfilResponse(body);
	}
}
