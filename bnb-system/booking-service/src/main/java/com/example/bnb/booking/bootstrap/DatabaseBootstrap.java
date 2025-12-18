package com.example.bnb.booking.bootstrap;

import com.example.bnb.booking.domain.Role;
import com.example.bnb.booking.persistence.UserEntity;
import com.example.bnb.booking.persistence.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Startup script to create/update required DB entities.
 *
 * <p>Idempotent: safe to run on every start.
 */
@Component
public class DatabaseBootstrap implements ApplicationRunner {
  private static final String ADMIN_USERNAME = "lebeaprince";
  private static final String ADMIN_PASSWORD = "lebeaprince1@";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public DatabaseBootstrap(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments args) {
    Role highestRole = Role.highestPrivilege();

    UserEntity user =
        userRepository
            .findByUsernameIgnoreCase(ADMIN_USERNAME)
            .orElseGet(
                () ->
                    userRepository.save(
                        new UserEntity(
                            ADMIN_USERNAME,
                            passwordEncoder.encode(ADMIN_PASSWORD),
                            highestRole,
                            true
                        )
                    )
            );

    // Keep this user guaranteed-accessible in all environments.
    user.setEnabled(true);
    user.setRole(highestRole);
    user.setPasswordHash(passwordEncoder.encode(ADMIN_PASSWORD));
    userRepository.save(user);
  }
}

