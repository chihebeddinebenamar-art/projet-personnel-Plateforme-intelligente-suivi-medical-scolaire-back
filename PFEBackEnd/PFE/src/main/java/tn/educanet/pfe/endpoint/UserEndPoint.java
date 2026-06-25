package tn.educanet.pfe.endpoint;

import org.dozer.Mapper;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import jakarta.annotation.Resource;
import tn.educanet.pfe.persistence.User;
import tn.educanet.business.ws.user.GetUserByIdRequest;
import tn.educanet.business.ws.user.GetUserByIdResponse;
import tn.educanet.pfe.service.UserService;

@Endpoint
public class UserEndPoint {

	
	public static final String NS = "http://www.educanet.tn.com/business/user/schema";
	
	
	@Resource
	Mapper mapper;
	
	@Resource
	private UserService userService;
	
	@PayloadRoot(namespace = NS, localPart = "GetUserByIdRequest")
	@ResponsePayload
	GetUserByIdResponse getUserById(@RequestPayload GetUserByIdRequest request) {
		User user = userService.getUserById(request.getId());
		
		return mapper.map(user, GetUserByIdResponse.class);
	}
}
