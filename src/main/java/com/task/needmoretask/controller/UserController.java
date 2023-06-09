package com.task.needmoretask.controller;

import com.task.needmoretask.core.auth.jwt.MyJwtProvider;
import com.task.needmoretask.core.auth.session.MyUserDetails;
import com.task.needmoretask.dto.ResponseDTO;
import com.task.needmoretask.dto.user.UserRequest;
import com.task.needmoretask.dto.user.UserResponse;
import com.task.needmoretask.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid UserRequest.JoinIn joinIn, Errors errors) {
        userService.join(joinIn);
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserRequest.Login login, Errors errors, HttpServletRequest request){
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = request.getRemoteAddr();
        String jwt = userService.login(login,userAgent,ipAddress);
        return ResponseEntity.ok().header(MyJwtProvider.HEADER, jwt).body(new ResponseDTO<>());
    }

    //프로필 업로드
    @PostMapping(value = "/user/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateImage(@RequestPart(value = "profileImage") MultipartFile image) throws IOException {
        UserResponse.ProfileOut url = userService.updateImage(image);
        return ResponseEntity.ok().body(new ResponseDTO<>(url));
    }

    //전체 유저 조회
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(){
        UserResponse.AllUsersOut users = userService.getAllUsers();
        return ResponseEntity.ok().body(new ResponseDTO<>(users));
    }

    //유져 조회
    @GetMapping("/admin/users")
    public ResponseEntity<?> getUsers(@RequestParam("role") String role, @RequestParam("page") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        UserResponse.UsersOut users = userService.getUsers(role, pageable);
        return ResponseEntity.ok().body(new ResponseDTO<>(users));
    }

    //유저 검색
    @GetMapping("/users/search")
    public ResponseEntity<?> searchUsers(@RequestParam("fullName") String fullName, @RequestParam("page") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        UserResponse.UsersOut users = userService.searchUsers(fullName, pageable);
        return ResponseEntity.ok().body(new ResponseDTO<>(users));
    }

    //개인정보 조회
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id, @AuthenticationPrincipal MyUserDetails myUserDetails) {
        UserResponse.UserOut user = userService.getUserInfo(id);
        return ResponseEntity.ok().body(new ResponseDTO<>(user));
    }

    //개인정보 수정
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUserInfo(@PathVariable Long id, @RequestBody @Valid UserRequest.UserIn userIn, Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails){
        UserResponse.UserOut user = userService.updateUserInfo(id,userIn,myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(user));
    }

    //비밀번호 확인
    @PostMapping("/password/validate")
    public ResponseEntity<?> validatePassword(@RequestBody @Valid UserRequest.UserPasswordValidate userPasswordDto, Errors errors, @AuthenticationPrincipal MyUserDetails myUserDetails ){
        userService.validatePassword(userPasswordDto,myUserDetails.getUser());
        return  ResponseEntity.ok(new ResponseDTO<>());
    }

    //이메일 중복확인
    @PostMapping("/email/validate")
    public ResponseEntity<?> validateEmail(@RequestBody @Valid UserRequest.UserEmailValidate emailValidateDto, Errors errors){
        userService.isDuplicatedId(emailValidateDto);
        return ResponseEntity.ok(new ResponseDTO<>());
    }

    // 유저 role변경
    @PutMapping("/admin/role")
    public ResponseEntity<?> updateRole(
            @RequestBody @Valid UserRequest.updateRoleInDTO updateRoleInDTO,
            Errors errors,
            @AuthenticationPrincipal MyUserDetails myUserDetails){
        userService.updateRole(myUserDetails.getUser(), updateRoleInDTO);
        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    //정보 요청
    @GetMapping("/auth/me")
    public ResponseEntity<?> getAuth(@AuthenticationPrincipal MyUserDetails myUserDetails){
        UserResponse.UserOut user = userService.getAuth(myUserDetails.getUser());
        return ResponseEntity.ok().body(new ResponseDTO<>(user));
    }
}
