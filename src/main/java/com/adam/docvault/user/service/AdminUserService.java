package com.adam.docvault.user.service;

import java.util.Arrays;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adam.docvault.exception.IllegalOperationException;
import com.adam.docvault.exception.InvalidRequestException;
import com.adam.docvault.user.dto.AdminUserResponseDTO;
import com.adam.docvault.user.entity.Role;
import com.adam.docvault.user.entity.User;
import com.adam.docvault.user.exception.UserNotFoundException;
import com.adam.docvault.user.repository.UserRepository;
import com.adam.docvault.user.validation.UserSortField;

@Service
public class AdminUserService {

    private final UserRepository userRepository;
    private static final int MAX_PAGE_SIZE = 50;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void promoteToAdmin(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        System.out.println("Target user: " + user.getEmail());
        System.out.println("Target role: " + user.getRole());

        if (user.getRole() == Role.ADMIN) {
            throw new IllegalOperationException("User is already an admin");
        }

        user.promoteToAdmin();
    }

    @Transactional
    public void demoteToUser(UUID targetUserId, User adminUser){
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(UserNotFoundException::new);

        if(targetUser.getRole() == Role.USER){
            throw new IllegalOperationException("User is already a regular user");
        }
        
        if (targetUser.getId().equals(adminUser.getId())) {
            throw new IllegalOperationException(
                "Administrators cannot demote themselves"
            );  
        }

        long numberOfAdminUsers = userRepository.countByRole(Role.ADMIN);

        if(numberOfAdminUsers <= 1){
            throw new IllegalOperationException("Cannot demote the last administrator");
        }

        targetUser.demoteToUser();

    }

    public Page<AdminUserResponseDTO> getUsers(String search, Pageable pageable){

        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new InvalidRequestException(
                    "Page size cannot exceed " + MAX_PAGE_SIZE
            );
        }
        
        validateSortFields(pageable);
        
        if (search == null || search.isBlank()) {
            return userRepository.findAll(pageable)
                    .map(this::toResponseDTO);
        }

        return userRepository
            .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search,
                    search,
                    search,
                    pageable
            )
            .map(this::toResponseDTO);
    }


    private void validateSortFields(Pageable pageable) {

        for (Sort.Order order : pageable.getSort()) {

            boolean allowed = Arrays.stream(UserSortField.values())
                    .anyMatch(field ->
                            field.getProperty().equals(order.getProperty())
                    );

            if (!allowed) {
                throw new InvalidRequestException(
                        "Invalid sort field: " + order.getProperty()
                );
            }
        }
    }

    private AdminUserResponseDTO toResponseDTO(User user) {
        return new AdminUserResponseDTO(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );

    }


}