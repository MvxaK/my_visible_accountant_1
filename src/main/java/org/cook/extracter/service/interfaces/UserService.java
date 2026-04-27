package org.cook.extracter.service.interfaces;

import org.cook.extracter.model.Role;
import org.cook.extracter.model.User;
import org.cook.extracter.model.UserCreateRequest;
import org.cook.extracter.security.auth.RegisterRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    User getUserById(Long id);
    List<User> getAllUsers();
    User createUser(UserCreateRequest request);
    void updateUser(Long id, String newEmail, String password, Long requestingUserId, boolean isAdmin);
    void updatePassword(Long id, String oldPassword, String newPassword, Long requestingUserId, boolean isAdmin);
    void deleteUser(Long id, Long requestingUserId, boolean isAdmin);

}
