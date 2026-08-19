package com.adam.docvault.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.adam.docvault.user.repository.UserRepository;
import com.adam.docvault.exception.IllegalOperationException;
import com.adam.docvault.user.entity.Role;
import com.adam.docvault.user.entity.User;
import com.adam.docvault.user.entity.UserTestFactory;
import com.adam.docvault.user.exception.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
public class AdminUserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminUserService adminUserService;

    @Test
    void shouldPromoteUserToAdmin() {
        // Arrange
        User user = new User(
                "test_first_name",
                "test_lastname",
                "test@email.com",
                "testingPassword"
        );

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        // Act
        adminUserService.promoteToAdmin(user.getId());

        // Assert
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void shouldNotPromoteExistingAdmin() {
        // Arrange
        User user = new User(
            "test_first_name",
            "test_lastname",
            "test@email.com",
            "testingPassword"
        );
        user.promoteToAdmin();
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));

        // Act + Assert
        IllegalOperationException exception = assertThrows(
            IllegalOperationException.class,
            () -> adminUserService.promoteToAdmin(user.getId())
        );

        // Assert message

        assertEquals(
            "User is already an admin",
            exception.getMessage()
        );
    }


    @Test
    void shouldThrowWhenUserDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId))
            .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
            UserNotFoundException.class,
            () -> adminUserService.promoteToAdmin(userId)
        );
    }

    @Test
    void shouldDemoteAdminToUser() {

        // Arrange
        UUID adminId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        User adminUser = UserTestFactory.create(
                adminId,
                Role.ADMIN
        );

        User targetUser = UserTestFactory.create(
                targetId,
                Role.ADMIN
        );

        when(userRepository.findById(targetId))
                .thenReturn(Optional.of(targetUser));

        when(userRepository.countByRole(Role.ADMIN))
                .thenReturn(2L);

        // Act
        adminUserService.demoteToUser(targetId, adminUser);

        // Assert
        assertEquals(Role.USER, targetUser.getRole());
    }


    @Test
    void shouldNotDemoteRegularUser() {
        // Arrange
        UUID adminId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();

        User adminUser = UserTestFactory.create(
                adminId,
                Role.ADMIN
        );

        User targetUser = UserTestFactory.create(
                adminId,
                Role.USER
        );

        when(userRepository.findById(targetId))
                .thenReturn(Optional.of(targetUser));



        // Act + Assert
        IllegalOperationException exception = assertThrows(
            IllegalOperationException.class,
            () -> adminUserService.demoteToUser(targetId, adminUser)
        );

        // Assert message
        assertEquals(
            "User is already a regular user",
            exception.getMessage()
        );

    }

    @Test
    void shouldNotDemoteLasAdmin(){
         // Arrange
        UUID adminId = UUID.randomUUID();
        UUID targeId = UUID.randomUUID();

        User adminUser = UserTestFactory.create(
                adminId,
                Role.ADMIN
        );

        User targetUser = UserTestFactory.create(
                targeId,
                Role.ADMIN
        );

        when(userRepository.findById(targeId))
                .thenReturn(Optional.of(targetUser));

        when(userRepository.countByRole(Role.ADMIN))
            .thenReturn(1L);

            

        // Act + Assert
        IllegalOperationException exception = assertThrows(
            IllegalOperationException.class,
            () -> adminUserService.demoteToUser(targeId, adminUser)
        );

        // Assert message
        assertEquals(
            "Cannot demote the last administrator",
            exception.getMessage()
        );


    }

    @Test
    void shouldNotDemoteSelf(){
         // Arrange
        UUID adminId = UUID.randomUUID();

        User adminUser = UserTestFactory.create(
                adminId,
                Role.ADMIN
        );

        when(userRepository.findById(adminId))
                .thenReturn(Optional.of(adminUser));

        
        // Act + Assert
        IllegalOperationException exception = assertThrows(
            IllegalOperationException.class,
            () -> adminUserService.demoteToUser(adminId, adminUser)
        );

        // Assert message
        assertEquals(
            "Administrators cannot demote themselves",
            exception.getMessage()
        );


    }

    @Test
    void shouldNotDemoteUserThatDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID adminId = UUID.randomUUID();

        User adminUser = UserTestFactory.create(
                adminId,
                Role.ADMIN
        );

        when(userRepository.findById(userId))
            .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
            UserNotFoundException.class,
            () -> adminUserService.demoteToUser(userId, adminUser)
        );
    }

    
}
