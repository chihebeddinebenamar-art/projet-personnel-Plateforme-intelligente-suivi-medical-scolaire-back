package tn.educanet.pfe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import tn.educanet.pfe.persistence.SecurityAccount;
import tn.educanet.pfe.repository.SecurityAccountRepository;

@Component
public class SecurityAccountInitializer implements ApplicationRunner {

	private final SecurityAccountRepository securityAccountRepository;

	@Value("${educanet.security.admin-username:admin}")
	private String adminUsername;

	@Value("${educanet.security.admin-password:admin123}")
	private String adminPassword;

	@Value("${educanet.security.infirmier-username:infirmier}")
	private String infirmierUsername;

	@Value("${educanet.security.infirmier-password:infirmier123}")
	private String infirmierPassword;

	public SecurityAccountInitializer(SecurityAccountRepository securityAccountRepository) {
		this.securityAccountRepository = securityAccountRepository;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		ensureAccount(adminUsername, adminPassword, "ADMIN");
		ensureAccount(infirmierUsername, infirmierPassword, "INFIRMIER");
	}

	private void ensureAccount(String username, String password, String role) {
		if (!StringUtils.hasText(username) || !StringUtils.hasText(password) || !StringUtils.hasText(role)) {
			return;
		}
		if (securityAccountRepository.existsByUsernameIgnoreCase(username.trim())) {
			return;
		}
		SecurityAccount account = new SecurityAccount();
		account.setUsername(username.trim());
		account.setPassword(password);
		account.setRole(role.trim().toUpperCase());
		account.setEnabled(true);
		securityAccountRepository.save(account);
	}
}
