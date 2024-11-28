package com.quangduy.product_manager_for_arius.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Constants.ConstantException;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.quangduy.product_manager_for_arius.constant.PredefinedRole;
import com.quangduy.product_manager_for_arius.dto.request.AuthenticationRequest;
import com.quangduy.product_manager_for_arius.dto.request.IntrospectRequest;
import com.quangduy.product_manager_for_arius.dto.request.LogoutRequest;
import com.quangduy.product_manager_for_arius.dto.request.RefreshRequest;
import com.quangduy.product_manager_for_arius.dto.response.AuthenticationResponse;
import com.quangduy.product_manager_for_arius.dto.response.IntrospectResponse;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.entity.InvalidatedToken;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.AuthMapper;
import com.quangduy.product_manager_for_arius.mapper.UserMapper;
import com.quangduy.product_manager_for_arius.repository.InvalidatedTokenRepository;
import com.quangduy.product_manager_for_arius.repository.UserRepository;
import com.quangduy.product_manager_for_arius.util.SecurityUtil;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    AuthenticationManagerBuilder authenticationManagerBuilder;
    SecurityUtil securityUtil;
    UserService userService;
    PasswordEncoder passwordEncoder;
    AuthMapper authMapper;
    UserMapper userMapper;
    UserRepository userRepository;

    @Value("${quangduy.jwt.refresh-token-validity-in-seconds}")
    @NonFinal
    long refreshTokenExpiration;

    @Value("${quangduy.jwt.base64-secret}")
    @NonFinal
    String SIGNER_KEY;

    public ResponseEntity<IntrospectResponse> introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (JOSEException | ParseException | ConstantException e) {
            isValid = false;
        }

        return ResponseEntity.ok().body(IntrospectResponse.builder().valid(isValid).build());
    }

    public ResponseEntity<UserResponse> login(AuthenticationRequest request) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject()
                .authenticate(authenticationToken);

        // save info auth into security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // return api
        AuthenticationResponse res = new AuthenticationResponse();
        User currentUserDB = this.userService.handleGetUserByUsername(request.getUsername());
        if (currentUserDB != null) {
            res.setUser(this.authMapper.toUserLogin(currentUserDB));
        }
        // create a token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(access_token);

        // create refesh token
        String refresh_token = this.securityUtil.createRefreshToken(request.getUsername(), res);
        res.setRefreshToken(refresh_token);
        this.userService.updateUserToken(refresh_token, request.getUsername());

        // set cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    public ResponseEntity<RegisterResponse> register(RegisterRequest request) throws MyAppException {

        String hashPass = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashPass);
        boolean isExistsEmail = this.userService.isEmailExist(request.getEmail());
        if (isExistsEmail) {
            throw new MyAppException("Email đã tồn tại, vui lòng nhập lại!");
        }

        boolean isExistUsername = this.userService.isUsernameExist(request.getUsername());
        if (isExistUsername) {
            throw new MyAppException("Username đã tồn tại, vui lòng nhập lại!");
        }
        User user = this.authMapper.toUser(request);
        user.setPassword(this.passwordEncoder.encode(request.getPassword()));
        user.setType("SYSTEM");
        user.setVerify(false);
        if (request.getRole() == null) {
            user.setRole(PredefinedRole.USER_ROLE.toString());
        }
        user = this.userRepository.save(user);
        RegisterResponse response = this.authMapper.toRegisterResponse(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<UserResponse> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUserDB = this.userService.handleGetUserByUsername(email);
        UserResponse res = this.userMapper.toUserResponse(currentUserDB);
        return ResponseEntity.ok().body(res);
    }

    public ResponseEntity<Void> logout() throws MyAppException {
        String username = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (username.equals("")) {
            throw new MyAppException("Access Token không hợp lệ");
        }
        User currentUserDB = this.userService.handleGetUserByUsername(username);
        if (currentUserDB != null) {
            // set refresh token == null
            this.userService.handleLogout(currentUserDB);
        }
        // remove refresh_token in cookies
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }

    public ResponseEntity<LoginResponse> refreshToken(String refresh_token) throws MyAppException {
        // check valid token
        if (refresh_token.equals("duy")) {
            throw new MyAppException("Bạn không có refresh_token ở cookies");
        }
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String username = decodedToken.getSubject();

        // check user by token + email ( 2nd layer check)
        User currentUser = this.userService.getUserByRefreshTokenAndUsername(refresh_token, username);
        if (currentUser == null) {
            throw new MyAppException("Refresh token không hợp lệ");
        }

        // issue new token/set refresh token as cookies
        LoginResponse res = new LoginResponse();
        User currentUserDB = this.userService.handleGetUserByUsername(username);
        if (currentUserDB != null) {
            res = LoginResponse.builder()
                    .user(this.authMapper.toUserLogin(currentUserDB))
                    .build();
        }

        // create a token
        String access_token = this.securityUtil.createAccessToken(username, res);
        res.setAccessToken(access_token);

        // create refesh token
        String new_refresh_token = this.securityUtil.createRefreshToken(username, res);
        res.setRefreshToken(new_refresh_token);
        this.userService.updateUserToken(new_refresh_token, username);

        // set cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    private SignedJWT verifyToken(String token, boolean isRefresh)
            throws JOSEException, ParseException, ConstantException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                        .toInstant().plus(refreshTokenExpiration, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new ConstantException(ErrorCode.UNAUTHENTICATED);

        String username = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (username.equals("")) {
            throw new ConstantException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

}
