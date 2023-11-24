package com.example.spot.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JoinRes {

    private Long id;

    private String email;
    private String nickname; //사용자 이름

    private String jwt;
    private String resultMessage;
}
