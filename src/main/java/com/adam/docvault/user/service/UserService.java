package com.adam.docvault.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.adam.docvault.user.mapper.UserMapper;
import com.adam.docvault.user.repository.UserRepository;
import com.adam.docvault.user.dto.UserResponseDTO;
import com.adam.docvault.user.entity.User;
import com.adam.docvault.user.exception.EmailAlreadyExistsException;
import com.adam.docvault.user.dto.RegisterRequest;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public UserResponseDTO register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        String hashedPassword =
                passwordEncoder.encode(request.password());

        User user = new User(
                request.firstName(),
                request.lastName(),
                request.email(),
                hashedPassword
        );

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }
}