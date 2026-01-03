package yuseong.com.guchung.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import yuseong.com.guchung.auth.dto.SignupRequestDto;
import yuseong.com.guchung.auth.service.AuthService;

import java.util.Map;

@Tag(name = "User Management", description = "사용자 정보 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserUpdateController {

    private final AuthService authService;

    @Operation(summary = "회원가입 추가 정보 저장", description = "사용자가 입력한 주소 및 수신동의 정보를 저장합니다.")
    @PostMapping("/{userId}/complete-signup")
    public ResponseEntity<?> completeSignup(
            @PathVariable Long userId,
            @RequestBody SignupRequestDto dto) {
        
        authService.completeSignup(userId, dto);
        return ResponseEntity.ok(Map.of("message", "가입 정보 업데이트 성공", "userId", userId));
    }
}