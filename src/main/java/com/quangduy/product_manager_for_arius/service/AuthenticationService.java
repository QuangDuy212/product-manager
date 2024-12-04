package com.quangduy.product_manager_for_arius.service;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Constants.ConstantException;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import com.quangduy.product_manager_for_arius.constant.PredefinedRole;
import com.quangduy.product_manager_for_arius.dto.request.AuthenticationRequest;
import com.quangduy.product_manager_for_arius.dto.request.IntrospectRequest;
import com.quangduy.product_manager_for_arius.dto.request.UserCreationRequest;
import com.quangduy.product_manager_for_arius.dto.response.ApiResponse;
import com.quangduy.product_manager_for_arius.dto.response.AuthenticationResponse;
import com.quangduy.product_manager_for_arius.dto.response.IntrospectResponse;
import com.quangduy.product_manager_for_arius.dto.response.UserResponse;
import com.quangduy.product_manager_for_arius.entity.Role;
import com.quangduy.product_manager_for_arius.entity.User;
import com.quangduy.product_manager_for_arius.exception.AppException;
import com.quangduy.product_manager_for_arius.exception.ErrorCode;
import com.quangduy.product_manager_for_arius.mapper.AuthMapper;
import com.quangduy.product_manager_for_arius.mapper.UserMapper;
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
    RoleService roleService;
    PasswordEncoder passwordEncoder;
    AuthMapper authMapper;
    UserMapper userMapper;
    UserRepository userRepository;

    @Value("${quangduy.jwt.refresh-token-validity-in-seconds}")
    @NonFinal
    long refreshTokenExpiration;

    @Value("${quangduy.jwt.base64-secret-access}")
    @NonFinal
    String SIGNER_KEY;

    public ResponseEntity<IntrospectResponse> introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;

        try {
            verifyToken(token, false);
        } catch (JOSEException | ParseException e) {
            isValid = false;
        }

        return ResponseEntity.ok().body(IntrospectResponse.builder().valid(isValid).build());
    }

    public ResponseEntity<AuthenticationResponse> login(AuthenticationRequest request) {
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
            res.setUser(this.authMapper.toUserResponse(currentUserDB));
        }
        // create a token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res.getUser());
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

    public ResponseEntity<UserResponse> register(UserCreationRequest request) throws AppException {

        String hashPass = passwordEncoder.encode(request.getPassword());
        request.setPassword(hashPass);
        boolean isExist = this.userService.isExistByUsername(request.getUsername());
        if (isExist) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User user = this.authMapper.toUser(request);
        user.setPassword(this.passwordEncoder.encode(request.getPassword()));
        if (request.getRole() == null) {
            Role role = this.roleService.findByName(PredefinedRole.USER_ROLE.toString());
            user.setRole(role);
        } else {
            Role role = this.roleService.findByName(request.getRole());
            user.setRole(role);
        }
        user = this.userRepository.save(user);
        UserResponse response = this.authMapper.toUserResponse(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public ResponseEntity<UserResponse> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUserDB = this.userService.handleGetUserByUsername(email);
        UserResponse res = this.userMapper.toUserResponse(currentUserDB);
        return ResponseEntity.ok().body(res);
    }

    public ResponseEntity<Void> logout() throws AppException {
        String username = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (username.equals("")) {
            throw new AppException(ErrorCode.INVALID_ACCESSTOKEN);
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

    public ResponseEntity<AuthenticationResponse> refreshToken(String refresh_token) throws AppException {
        // check valid token
        if (refresh_token.equals("duy")) {
            throw new AppException(ErrorCode.COOKIES_EMPTY);
        }
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String username = decodedToken.getSubject();

        // check user by token + email ( 2nd layer check)
        User currentUser = this.userService.getUserByRefreshTokenAndUsername(refresh_token, username);
        if (currentUser == null) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // issue new token/set refresh token as cookies
        AuthenticationResponse res = new AuthenticationResponse();
        User currentUserDB = this.userService.handleGetUserByUsername(username);
        if (currentUserDB != null) {
            res = AuthenticationResponse.builder()
                    .user(this.authMapper.toUserResponse(currentUserDB))
                    .build();
        }

        // create a token
        String access_token = this.securityUtil.createAccessToken(username, res.getUser());
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
            throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                        .toInstant().plus(refreshTokenExpiration, ChronoUnit.SECONDS)
                        .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        String username = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        if (username.equals("")) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

}
