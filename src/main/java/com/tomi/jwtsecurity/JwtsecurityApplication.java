package com.tomi.jwtsecurity;

import com.tomi.jwtsecurity.entity.Role;
import com.tomi.jwtsecurity.repository.RoleRepo;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import java.util.Optional;

@SpringBootApplication
public class JwtsecurityApplication implements CommandLineRunner {

	private RoleRepo roleRepo;

	public JwtsecurityApplication(RoleRepo roleRepo) {
		this.roleRepo = roleRepo;
	}

	public static void main(String[] args) {
		SpringApplication.run(JwtsecurityApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Override
	public void run(String... args) throws Exception {
		createRoleIfNotExist("USER");
		createRoleIfNotExist("ADMIN");
	}

	public void createRoleIfNotExist(String name) {
		Optional<Role> role = roleRepo.findByName(name);

		if (role.isEmpty()) {
			Role newRole = new Role();
			newRole.setName(name);
			roleRepo.save(newRole);
		}
	}
}
