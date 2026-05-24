package com.openex.backend.service;

import com.openex.backend.dto.UserRequest;
import com.openex.backend.dto.UserResponse;
import com.openex.backend.model.User;
import com.openex.backend.repository.UserRepository;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable("usersAll")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "usersById", key = "#id")
    public UserResponse getUserById(Long id) {
        return toResponse(findUserEntityById(id));
    }

    @CacheEvict(cacheNames = {"usersAll", "usersById"}, allEntries = true)
    public UserResponse createUser(UserRequest request) {
        validateUserRequest(request);
        ensureUsernameAvailable(request.username(), null);
        ensureEmailAvailable(request.email(), null);

        User user = User.builder()
                .username(request.username().trim())
                .email(request.email().trim().toLowerCase())
                .password(request.password())
                .enabled(request.enabled() != null ? request.enabled() : Boolean.TRUE)
                .build();

        return toResponse(userRepository.save(user));
    }

    @CacheEvict(cacheNames = {"usersAll", "usersById"}, allEntries = true)
    public UserResponse updateUser(Long id, UserRequest request) {
        validateUserRequest(request);

        User user = findUserEntityById(id);
        ensureUsernameAvailable(request.username(), id);
        ensureEmailAvailable(request.email(), id);

        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(request.password());
        user.setEnabled(request.enabled() != null ? request.enabled() : user.getEnabled());

        return toResponse(userRepository.save(user));
    }

    @CacheEvict(cacheNames = {"usersAll", "usersById"}, allEntries = true)
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public User findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private void validateUserRequest(UserRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User payload is required");
        }
        if (isBlank(request.username())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }
        if (isBlank(request.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }
        if (isBlank(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required");
        }
    }

    private void ensureUsernameAvailable(String username, Long currentUserId) {
        userRepository.findByUsername(username.trim())
                .filter(existingUser -> !existingUser.getId().equals(currentUserId))
                .ifPresent(existingUser -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
                });
    }

    private void ensureEmailAvailable(String email, Long currentUserId) {
        userRepository.findByEmail(email.trim().toLowerCase())
                .filter(existingUser -> !existingUser.getId().equals(currentUserId))
                .ifPresent(existingUser -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
                });
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
