package com.an.pathtrip.services;

import com.an.pathtrip.dtos.request.UpdateUserRequest;
import com.an.pathtrip.dtos.response.UserResponse;
import com.an.pathtrip.enums.UserStatus;
import com.an.pathtrip.exceptions.AppException;
import com.an.pathtrip.models.User;
import com.an.pathtrip.repositories.UserRepository;
import com.an.pathtrip.securities.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CloudinaryService cloudinaryService;

    public UserResponse getMe(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String email = userPrincipal.getUsername();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new AppException("Không tìm thấy người dùng", 404)
                );
        if (user.getStatus() == UserStatus.PENDING) {
            throw new AppException("Tài khoản chưa được xác minh email", 403);
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AppException("Tài khoản đã bị khóa", 403);
        }

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatar(),
                user.getRole(),
                user.getStatus()
        );
    }

    public UserResponse updateMe(Authentication authentication, UpdateUserRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal)  authentication.getPrincipal();
        String email = userPrincipal.getUsername();
        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new AppException("Không tìm thấy người dùng", 404)
                );

        if (user.getStatus() == UserStatus.PENDING) {
            throw new AppException(
                    "Tài khoản chưa được xác minh email",
                    403
            );
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AppException(
                    "Tài khoản đã bị khóa",
                    403
            );
        }
        String phone = request.getPhone();

        Optional<User> existingUser = userRepo.findByPhone(phone);

        if (existingUser.isPresent()
                && !existingUser.get().getId().equals(user.getId())) {

            throw new AppException(
                    "Số điện thoại đã được sử dụng",
                    409
            );
        }

        user.setName(request.getName());
        user.setPhone(request.getPhone());


        MultipartFile avatar = request.getAvatar();
        if(avatar != null && !avatar.isEmpty()){
            String avatarUrl = cloudinaryService.upload(avatar);
            user.setAvatar(avatarUrl);
        }
        User savedUser = userRepo.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getPhone(), savedUser.getAvatar(), savedUser.getRole(), savedUser.getStatus());
    }


}
