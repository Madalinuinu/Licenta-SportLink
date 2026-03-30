package com.licenta.sport_link.service;

import com.licenta.sport_link.domain.UserAccount;
import com.licenta.sport_link.dto.AuthRequest;
import com.licenta.sport_link.dto.AuthResponse;
import com.licenta.sport_link.dto.RegisterRequest;
import com.licenta.sport_link.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;

    public AuthService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        validateRegisterRequest(request);

        String email = request.email().trim().toLowerCase();
        if (userAccountRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        UserAccount user = new UserAccount();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(request.password().trim());

        UserAccount saved = userAccountRepository.save(user);
        return toAuthResponse(saved);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        validateLoginRequest(request);

        UserAccount user = userAccountRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!user.getPassword().equals(request.password().trim())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return toAuthResponse(user);
    }

    private AuthResponse toAuthResponse(UserAccount user) {
        return new AuthResponse(user.getId(), user.getName(), user.getEmail());
    }

    private void validateRegisterRequest(RegisterRequest request) {
        if (request == null
                || isBlank(request.name())
                || isBlank(request.email())
                || isBlank(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name, email and password are required");
        }
    }

    private void validateLoginRequest(AuthRequest request) {
        if (request == null || isBlank(request.email()) || isBlank(request.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
