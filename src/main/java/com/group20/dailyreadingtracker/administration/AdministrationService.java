package com.group20.dailyreadingtracker.administration;

import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.group20.dailyreadingtracker.role.Role;
import com.group20.dailyreadingtracker.role.RoleRepository;
import com.group20.dailyreadingtracker.user.User;
import com.group20.dailyreadingtracker.user.UserRepository;
import com.group20.dailyreadingtracker.utils.EmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Service handling administrative operations and user management.
 * 
 * Provides core functionality for:
 * - User account state management (freezing/unfreezing)
 * - Role administration (promotion/demotion of admins)
 * - User listing and sorting
 * - Admin privilege verification
 * 
 * Security:
 * - All operations require proper authorization checks at the controller level
 * - Modifies user states and privileges
 * - Handles role-based access control modifications
 * 
 * Key Functionality:
 * - User status modifications
 * - Role assignment and revocation
 * - Sorted user retrieval for administrative views
 * - Email notifications for account actions
 * 
 * Transactional Operations:
 * - All state-changing methods are transactional
 * - Ensures data consistency during batch operations
 * - Maintains referential integrity for role assignments
 * 
 * @author Yongtai Li
 * @author Yaxiang Yao
 */

@Service
@RequiredArgsConstructor
public class AdministrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    public List<User> getUsersSortedBy(String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return userRepository.findAll(sort);
    }

    @Transactional
    public void FreezeUserByIds(List<Long> UserIds) {
        List<User> u = userRepository.findAllById(UserIds);
        for(User usr: u){
            usr.freezeUser();
            emailService.sendAccountFrozenEmail(usr);
            userRepository.save(usr);
        }
    }

    @Transactional
    public void UnfreezeUserByIds(List<Long> UserIds) {
        List<User> u = userRepository.findAllById(UserIds);
        for(User usr: u){
            usr.unfreezeUser();
            userRepository.save(usr);
        }
    }

    @Transactional
    public void promoteUsersToAdmin(List<Long> userIds) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
            .orElseGet(() -> {
                Role newRole = new Role("ROLE_ADMIN");
                return roleRepository.save(newRole);
            });


        for (Long userId : userIds) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {

                if (user.getRoles() == null) {
                    user.setRoles(new HashSet<>());
                } else {
                    user.getRoles().clear();
                }

                user.getRoles().add(adminRole);
                userRepository.save(user);
            }
        }
    }

    @Transactional
    public void demoteUsersFromAdmin(List<Long> userIds) {
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole = roleRepository.save(userRole);
        }

        for (Long userId : userIds) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                if (user.getRoles() == null) {
                    user.setRoles(new HashSet<>());
                } else {
                    user.getRoles().clear();
                }

                user.getRoles().add(userRole);
                userRepository.save(user);
            }
        }
    }
}
