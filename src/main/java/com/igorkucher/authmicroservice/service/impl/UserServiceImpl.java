package com.igorkucher.authmicroservice.service.impl;

import com.igorkucher.authmicroservice.model.Role;
import com.igorkucher.authmicroservice.model.User;
import com.igorkucher.authmicroservice.repository.RoleRepository;
import com.igorkucher.authmicroservice.repository.UserRepository;
import com.igorkucher.authmicroservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("userServiceImpl")
@Slf4j
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User create(User user) {
        Role role = roleRepository.findByName("ROLE_USER");
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User readById(long id) {
        Optional<User> optional = userRepository.findById(id);
        if(optional.isEmpty()) {return null;}
        return optional.get();
    }

    @Override
    public User readByEmail(String email) {
        User result = userRepository.getUserByEmail(email);
        if (result == null) {
            log.warn("IN readByEmail - no user found by email: {}", email);
            return null;
        }
        log.info("IN readByEmail - user: {} found by email: {}", result, email);
        return result;
    }

    @Override
    public User update(User user) {
        User oldUser = readById(user.getId());
        return userRepository.save(user);
    }

    @Override
    public void delete(long id) {
        User user = readById(id);
        userRepository.delete(user);
    }

    @Override
    public List<User> getAll() {
        List<User> users = userRepository.findAll();
        return users.isEmpty() ? new ArrayList<>() : users;
    }

    /*@Override
    public boolean saveUser(UserRequest userRequest) {
        User user = new User();
        user.setRole(roleRepository.getOne(2L));
        user.setEmail(userRequest.getLogin());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        return (userRepository.save(user) != null);
    }

    @Override
    public UserResponse findByLoginAndPassword(UserRequest userRequest) {
        UserResponse userResponse = null;
        User user = userRepository.getUserByEmail(userRequest.getLogin());
        if((user != null) && (passwordEncoder.matches(userRequest.getPassword(), user.getPassword()))) {
            userResponse = new UserResponse();
            userResponse.setLogin(userRequest.getLogin());
            userResponse.setPassword(userRequest.getPassword());
        }
        return userResponse;
    }

    @Override
    public boolean matchPassword(UserRequest userRequest) {
        User user = userRepository.getUserByEmail(userRequest.getLogin());
        return passwordEncoder.matches(userRequest.getPassword(), user.getPassword());
    }*/
}
