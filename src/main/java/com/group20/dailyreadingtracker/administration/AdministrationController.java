package com.group20.dailyreadingtracker.administration;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group20.dailyreadingtracker.auth.AccountFrozenException;
import com.group20.dailyreadingtracker.user.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Controller handling administrative operations and user management functions.
 * 
 * Provides endpoints for:
 * - Admin dashboard views and interfaces
 * - User account status management (freezing/unfreezing)
 * - User role management (promotion/demotion of admins)
 * - Viewing system logs (user logs and violation logs)
 * 
 * Security:
 * - All endpoints require ADMIN role privileges
 * - Performs sensitive user state modifications
 * - Handles role elevation/demotion operations
 * 
 * Endpoint Structure:
 * - View endpoints return Thymeleaf templates for admin interfaces
 * - Action endpoints perform state modifications and redirect
 * 
 * Flow:
 * - All modification operations redirect back to the admin user list
 * - Maintains sorting state through redirects
 * 
 * @author Yongtai Li
 * @author Yaxiang Yao
 */

@Controller
@RequiredArgsConstructor
public class AdministrationController {

    private final AdministrationService administrationService;

    @GetMapping("/account-frozen")
    public String getAccountFrozenPage() {
        return "administration/account-frozen"; 
    }

    @GetMapping("/homeforadmin")
    public String getAdminHome() {
        return "administration/homeforadmin";
    }

    @GetMapping("/viologs")
    public String getViolationLogs() {
        return "administration/violationlog";
    }

    @GetMapping("/sorted_admin_userlist")
    public String showSortedUsers(
        @RequestParam(required = false) String searchQuery,
        @RequestParam(name = "sortField", defaultValue = "id") String sortField,
        @RequestParam(name = "sortDirection", defaultValue = "asc") String sortDirection,
        Model m, Principal principal) {
            List<User> usersinlist = administrationService.getUsersSortedBy(sortField, sortDirection);

            m.addAttribute("users", usersinlist);
            m.addAttribute("sortField", sortField);
            m.addAttribute("sortDirection", sortDirection);

            return "administration/admin-user-list";
        }

    @PostMapping("/sorted_admin_userlist/users_freeze")
    public String freezeTheUser(@RequestParam(name = "selectedUsers") List<Long> selectedUserIds, RedirectAttributes redirectAttributes) {
        administrationService.FreezeUserByIds(selectedUserIds);
        return "redirect:/sorted_admin_userlist"; 
    }


    @PostMapping("/sorted_admin_userlist/users_unfreeze")
    public String unfreezeTheUser(@RequestParam(name = "selectedUsers") List<Long> selectedUserIds) {
        administrationService.UnfreezeUserByIds(selectedUserIds);
        return "redirect:/sorted_admin_userlist"; 
    }

    @PostMapping("/sorted_admin_userlist/promote_to_admin")
    @Transactional
    public String promoteToAdmin(@RequestParam(name = "selectedUsers", required = false) List<Long> selectedUserIds) {
        if (selectedUserIds != null && !selectedUserIds.isEmpty()) {
            administrationService.promoteUsersToAdmin(selectedUserIds);
        }
        return "redirect:/sorted_admin_userlist";
    }

    @PostMapping("/sorted_admin_userlist/demote_from_admin")
    @Transactional
    public String demoteFromAdmin(@RequestParam(required = false) List<Long> selectedUsers) {
        if (selectedUsers != null && !selectedUsers.isEmpty()) {
            administrationService.demoteUsersFromAdmin(selectedUsers);
        }
        return "redirect:/sorted_admin_userlist";
    }

    @ExceptionHandler(AccountFrozenException.class)
    public String handleFrozenAccount(AccountFrozenException ex) {
        return "redirect:/account-frozen";    
    }
}
