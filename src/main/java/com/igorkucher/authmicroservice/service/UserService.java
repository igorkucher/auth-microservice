package com.igorkucher.authmicroservice.service;

import com.igorkucher.authmicroservice.model.User;

import java.util.List;

public interface UserService {
    User create(User user);
//    boolean saveUser(UserRequest userRequest);
    User readById(long id);
    User readByEmail(String email);
    User update(User user);
    void delete(long id);
    List<User> getAll();
//    UserResponse findByLoginAndPassword(UserRequest userRequest);
//    boolean matchPassword(UserRequest userRequest);
}
