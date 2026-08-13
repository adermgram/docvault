package com.adam.docvault.user.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.adam.docvault.exception.IllegalOperationException;
import com.adam.docvault.user.entity.Role;
import com.adam.docvault.user.entity.User;
import com.adam.docvault.user.exception.UserNotFoundException;
import com.adam.docvault.user.repository.UserRepository;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

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


}