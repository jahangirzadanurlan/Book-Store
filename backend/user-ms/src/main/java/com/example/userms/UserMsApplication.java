package com.example.userms;

import com.example.userms.model.entity.Role;
import com.example.userms.model.entity.User;
import com.example.userms.model.enums.RoleType;
import com.example.userms.repository.RoleRepository;
import com.example.userms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@SpringBootApplication
public class UserMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserMsApplication.class, args);
	}

	@Component
	@RequiredArgsConstructor
	public static class DataLoader implements CommandLineRunner {
		private final RoleRepository roleRepository;
		private final UserRepository userRepository;
		private final PasswordEncoder passwordEncoder;

		@Override
		public void run(String... args) {
			createRoleIfNotExist(RoleType.USER);
			createRoleIfNotExist(RoleType.ADMIN);

			createAdminUserIfNotExist(
					"admin",
					"cahangirzadenurlan043@gmail.com",
					"admin123"
			);
		}

		private void createRoleIfNotExist(RoleType roleName) {
			roleRepository.findRoleByName(roleName)
					.ifPresentOrElse(
							role -> System.out.println("Role already exists: " + roleName),
							() -> {
								Role newRole = Role.builder()
										.name(roleName)
										.build();
								roleRepository.save(newRole);
								System.out.println("New role created: " + roleName);
							}
					);
		}

		private void createAdminUserIfNotExist(String username, String email, String password) {
			userRepository.findUserByUsernameOrEmail(username)
					.ifPresentOrElse(
							user -> System.out.println("Admin already exists: " + user.getEmail()),
							() -> {
								Optional<Role> adminRole = roleRepository.findRoleByName(RoleType.ADMIN);
								if (adminRole.isPresent()) {
									User admin = User.builder()
											.username(username)
											.email(email)
											.password(passwordEncoder.encode(password))
											.role(adminRole.get())
											.build();
									userRepository.save(admin);
									System.out.println("Admin user created: " + email);
								} else {
									System.out.println("Admin role not found!");
								}
							}
					);
		}
	}
}
