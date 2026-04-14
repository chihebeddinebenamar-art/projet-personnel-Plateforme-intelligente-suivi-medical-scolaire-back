package tn.educanet.pfe.serviceimpl;

import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import tn.educanet.pfe.persistence.User;
import tn.educanet.pfe.repository.UserRepository;
import tn.educanet.pfe.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	@Resource
	private UserRepository userRepository;

	@Override
	public User getUserById(long id) {
		User user = new User();
		try {
			
			user = userRepository.findById(id).orElse(null);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}
}
