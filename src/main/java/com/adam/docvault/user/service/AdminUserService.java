package com.adam.docvault.user.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.adam.docvault.validation.SortValidator;

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
        
        SortValidator.validate(pageable, UserSortField.values());
        
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