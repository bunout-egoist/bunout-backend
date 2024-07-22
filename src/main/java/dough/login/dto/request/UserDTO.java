package dough.login.dto.request;

import dough.login.domain.type.RoleType;
import lombok.Getter;
import lombok.Setter;

// 데이터를 담아서 넘겨줄 DTO
@Getter
@Setter
public class UserDTO {
    private RoleType role;
    private String name; // 사용자 Id 담을 값
    private String username; // 우리 서버에서 만들어줄 유저 네임 값
}