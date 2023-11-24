package com.example.spot.auth;

import com.example.spot.auth.model.KakaoProfile;
import com.example.spot.config.exception.BaseException;
import com.example.spot.model.DTO.PostUserRes;
import com.example.spot.model.User;
import com.example.spot.utils.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.Optional;

import static com.example.spot.config.exception.BaseResponseStatus.DATABASE_ERROR;
import static com.example.spot.config.exception.BaseResponseStatus.KAKAO_CONNECTION_ERROR;

@Service
public class OAuthService {
    private final JwtService jwtService;
    private final OAuthRepository oAuthRepository;

    @Autowired
    public OAuthService(JwtService jwtService, OAuthRepository oAuthRepository){
        this.oAuthRepository=oAuthRepository;
        this.jwtService=jwtService;
    }



    public PostUserRes saveUser(String token) throws BaseException {
        KakaoProfile profile = findProfile(token);

        Optional<User> existingUser = oAuthRepository.findByNicknameAndType(profile.getProperties().getNickname(), "KAKAO");

        if (existingUser.isPresent()) {
            Long id = existingUser.get().getId();
            String jwt = jwtService.createJwt(id);
            return new PostUserRes(id, jwt);
        }

        try {

            User user = new User();
            user.setEmail(profile.getKakao_account().getEmail());
            user.setNickname(profile.getProperties().getNickname());
            user.setType("KAKAO");
            user.setCreateAt(new Timestamp(System.currentTimeMillis()));
            oAuthRepository.save(user);

            Long id = user.getId();
            String jwt = jwtService.createJwt(user.getId());

            return new PostUserRes(id,jwt);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public KakaoProfile findProfile(String token) throws BaseException {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> kakaoProfileResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (Exception exception) {
            throw new BaseException(KAKAO_CONNECTION_ERROR);
        }

        return kakaoProfile;
    }

    public PostUserRes loginUser(KakaoProfile kakaoProfile) throws BaseException{
        try {
            Long id = kakaoProfile.getId();
            String jwt = jwtService.createJwt(id);
            return new PostUserRes(id,jwt);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
