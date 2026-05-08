package com.chistesmalos.service;

import com.chistesmalos.dto.UserResponse;
import com.chistesmalos.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir User a UserResponse.
 */
@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
