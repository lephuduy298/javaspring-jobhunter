package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.User;
import com.example.demo.domain.dto.LoginDTO;
import com.example.demo.domain.dto.ResLoginDTO;
import com.example.demo.service.UserService;
import com.example.demo.util.SecurityUtil;
import com.example.demo.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        private final SecurityUtil securityUtil;
        private final UserService userService;

        @Value("${lephuduy.jwt.refresh-token-validity-in-seconds}")
        private long refreshTokenExpiration;

        public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                        UserService userService) {
                this.authenticationManagerBuilder = authenticationManagerBuilder;
                this.securityUtil = securityUtil;
                this.userService = userService;
        }

        @PostMapping("/auth/login")
        public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginDto) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                loginDto.getUsername(), loginDto.getPassword());
                // xác thực người dùng => cần viết hàm loadUserByUsername
                Authentication authentication = authenticationManagerBuilder.getObject()
                                .authenticate(authenticationToken);

                // create access token
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // trả ra thông tin ResLoginDTO
                ResLoginDTO resLoginDTO = new ResLoginDTO();

                // lấy user hiện tại
                User currentUser = this.userService.fetchUserByEmail(loginDto.getUsername());
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(currentUser.getId(), currentUser.getName(),
                                currentUser.getEmail());
                resLoginDTO.setUserLogin(userLogin);

                String access_Token = this.securityUtil.createAccessToken(userLogin);

                resLoginDTO.setAccessToken(access_Token);

                // create refresh token
                String refresh_Token = this.securityUtil.createRefreshToken(resLoginDTO);
                this.userService.updateUserToken(refresh_Token, loginDto.getUsername());

                // set cookies
                ResponseCookie springCookie = ResponseCookie.from("refresh_token", refresh_Token)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(refreshTokenExpiration)
                                .build();
                return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                                .body(resLoginDTO);
        }

        @GetMapping("/auth/account")
        @ApiMessage("get account")
        public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
                String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                                : "";
                User currentUser = this.userService.fetchUserByEmail(email);
                ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();
                if (currentUser != null) {
                        userLogin.setId(currentUser.getId());
                        userLogin.setEmail(currentUser.getEmail());
                        userLogin.setName(currentUser.getName());
                }
                return ResponseEntity.ok().body(userLogin);
        }
}
