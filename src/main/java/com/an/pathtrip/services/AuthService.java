package com.an.pathtrip.services;

import com.an.pathtrip.dtos.request.LoginRequest;
import com.an.pathtrip.dtos.request.RegisterRequest;
import com.an.pathtrip.dtos.request.VerifyEmailRequest;
import com.an.pathtrip.dtos.response.LoginResponse;
import com.an.pathtrip.dtos.response.RegisterResponse;
import com.an.pathtrip.dtos.response.VerifyEmailResponse;
import com.an.pathtrip.enums.OtpType;
import com.an.pathtrip.enums.Role;
import com.an.pathtrip.enums.UserStatus;
import com.an.pathtrip.exceptions.AppException;
import com.an.pathtrip.models.Otp;
import com.an.pathtrip.models.User;
import com.an.pathtrip.repositories.OtpRepository;
import com.an.pathtrip.repositories.UserRepository;
import com.an.pathtrip.securities.UserPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final SecureRandom random = new SecureRandom();

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private OtpRepository otpRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTService jwtService;

    @Transactional
    public RegisterResponse register(@Valid RegisterRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(
                    "Email đã tồn tại",
                    409
            );
        }

        if (userRepo.findByPhone(request.getPhone()).isPresent()) {
            throw new AppException(
                    "Số điện thoại đã tồn tại",
                    409
            );
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(Role.CUSTOMER)
                .status(UserStatus.PENDING)
                .build();

        userRepo.save(user);

        String randomOtp = generateOtp();

        Otp otp = Otp.builder().user(user).code(passwordEncoder.encode(randomOtp)).type(OtpType.REGISTER).build();

        otpRepo.save(otp);


        String content = """
        <h2>Xác thực email</h2>

        <p>Mã OTP của bạn là:</p>

        <h1 style="color:#1098F7;">%s</h1>

        <p>Mã OTP có hiệu lực trong 1 phút.</p>
        """.formatted(randomOtp);

        emailService.sendEmail(
                user.getEmail(),
                "Xác thực tài khoản PathTrip",
                content
        );

        LocalDateTime expiresAt = otp.getCreatedAt().plusSeconds(60);

        return new RegisterResponse(user.getId(), user.getName(), user.getEmail(), user.getPhone(), expiresAt);
    }

    public String generateOtp() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder otp = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            otp.append(chars.charAt(random.nextInt(chars.length())));
        }

        return otp.toString();
    }


    public VerifyEmailResponse verifyEmail(@Valid VerifyEmailRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isEmpty()) {
            throw new AppException(
                    "Không tìm thấy người dùng",
                    404
            );
        }
        User user = userRepo.findByEmail(request.getEmail()).get();
        if (user.getStatus() != UserStatus.PENDING) {
            throw new AppException(
                    "Không tìm thấy người dùng",
                    404
            );
        }
        Optional<Otp> otpOptional =
                otpRepo.findTopByUser_EmailAndTypeOrderByCreatedAtDesc(
                        request.getEmail(),
                        OtpType.REGISTER
                );

        if (otpOptional.isEmpty()) {
            throw new AppException(
                    "OTP không tồn tại",
                    404
            );
        }

        Otp otp = otpOptional.get();

        if (!passwordEncoder.matches(request.getOtp(), otp.getCode())) {
            throw new AppException(
                    "OTP không chính xác",
                    400
            );
        }
        if (otp.getCreatedAt().plusSeconds(60).isBefore(LocalDateTime.now())) {
            throw new AppException(
                    "OTP đã hết hạn",
                    400
            );
        }
        user.setStatus(UserStatus.ACTIVE);
        userRepo.save(user);

        return new VerifyEmailResponse(
                "Xác thực email thành công"
        );

    }


    public LoginResponse login(
            LoginRequest request,
            HttpServletResponse response
    ) {
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getEmail());

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new AppException("Email hoặc mật khẩu không chính xác", 401)
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                userDetails.getPassword()
        )) {
            throw new AppException(
                    "Email hoặc mật khẩu không chính xác",
                    401
            );
        }

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


        String accessToken =
                jwtService.generateAccessToken(userDetails.getUsername());

        String refreshToken =
                jwtService.generateRefreshToken(userDetails.getUsername());

        ResponseCookie cookie = ResponseCookie.from(
                        "refreshToken",
                        refreshToken
                )
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return new LoginResponse(accessToken);
    }

    public LoginResponse refresh(String refreshToken) {
        String type = jwtService.extractType(refreshToken);

        if (!"REFRESH".equals(type)) {
            throw new AppException("Refresh token không hợp lệ", 401);
        }

        String email = jwtService.extractUsername(refreshToken);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new AppException("User không tồn tại", 401)
                );

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new AppException("Tài khoản không hoạt động", 401);
        }

        String accessToken = jwtService.generateAccessToken(email);

        return new LoginResponse(accessToken);
    }
}
