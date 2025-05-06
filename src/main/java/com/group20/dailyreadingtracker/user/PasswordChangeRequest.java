package com.group20.dailyreadingtracker.user;

import lombok.Data;

/*
* DTO for password change operations.

* @author Sofiia Mamonova
*/

@Data
class PasswordChangeRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
