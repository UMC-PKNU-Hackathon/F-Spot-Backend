package com.example.spot.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class JoinRequest {
    private String email;
    private String password;
    private String nickname; //사용자 이름
}
