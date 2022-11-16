package com.igorkucher.authmicroservice.dto.response;

import com.igorkucher.authmicroservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}
