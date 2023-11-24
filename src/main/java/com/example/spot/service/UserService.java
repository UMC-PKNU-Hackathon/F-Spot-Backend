package com.example.spot.service;

import com.example.spot.config.exception.BaseException;
import com.example.spot.model.DTO.*;
import com.example.spot.model.User;
import com.example.spot.repository.UserRepository;
import com.example.spot.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Optional;

import static com.example.spot.config.exception.BaseResponseStatus.*;


@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    //회원가입
    public JoinRes signUp(JoinRequest joinRequest) throws BaseException {
        if(userRepository.findByEmail(joinRequest.getEmail()).isPresent()){
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        if (userRepository.findByNickname(joinRequest.getNickname()).isPresent()){
            throw new BaseException(POST_USERS_EXISTS_NICKNAME);
        }

        try{

            User user = User.builder()
                    .email(joinRequest.getEmail())
                    .password(joinRequest.getPassword())
                    .nickname(joinRequest.getNickname())
                    .createAt(new Timestamp(System.currentTimeMillis()))
                    .type("NORMAL")
                    .build();

            userRepository.save(user);

            Long id = user.getId();
            String jwt = jwtService.createJwt(id);
            String resultMessage = "'"+user.getNickname()+"'"+"님 회원가입을 환영합니다!";
            return new JoinRes(id, user.getEmail(), user.getNickname(),jwt,resultMessage);

        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }


    }

    //마이페이지 조회
    public MypageDTO mypageFocus() throws BaseException {

        Long id = jwtService.getUserIdx();

        if (id == 0) {
            throw  new BaseException(USERS_EMPTY_USER_ID);
        }

        try {
            Optional<User> userOptional = userRepository.findById(id);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String jwt = jwtService.createJwt(id);
                String resultMessage = "'"+user.getNickname()+"'"+"님 조회 성공하였습니다!";

                MypageDTO mypageDTO = new MypageDTO();
                mypageDTO.setId(user.getId());
                mypageDTO.setEmail(user.getEmail());
                mypageDTO.setPassword(user.getPassword());
                mypageDTO.setNickname(user.getNickname());
                mypageDTO.setJwt(jwt);
                mypageDTO.setResultMessage(resultMessage);


                return mypageDTO;

            } else {
                throw new BaseException(USERS_EMPTY_USER_ID);
            }
        } catch (BaseException exception) {
            if(exception.getStatus().equals(USERS_EMPTY_USER_ID)){
                throw exception;
            } else {
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }


    //마이페이지 수정
    @Transactional
    public MypageDTO mypagefixInfo(String password, String nickname) throws BaseException{
        Long idx = jwtService.getUserIdx();

        if (idx == 0) {
            throw  new BaseException(USERS_EMPTY_USER_ID);
        }

        try{

            Optional<User> userOptional=userRepository.findById(idx);

            if (userOptional.isPresent()){
                User user = userOptional.get();
                String jwt=jwtService.createJwt(idx);

                if(password!=null){

                    user.setPassword(password);
                }


                if(nickname!=null){
                    if (nickname.length()<2 || nickname.length()>20){
                        throw  new BaseException(POST_USERS_INVALID_NICKNAME);
                    }
                    if (userRepository.findByNickname(nickname).isPresent()){
                        throw new BaseException(POST_USERS_EXISTS_NICKNAME);
                    }
                    user.setNickname(nickname);
                }

                user.setUpdateAt(new Timestamp(System.currentTimeMillis()));

                String resultMessage = "'"+user.getNickname()+"'"+"님 마이페이지 수정 성공하였습니다!";

                userRepository.save(user);

                MypageDTO mypageDTO = new MypageDTO();
                mypageDTO.setId(user.getId());
                mypageDTO.setEmail(user.getEmail());
                mypageDTO.setPassword(user.getPassword());
                mypageDTO.setNickname(user.getNickname());
                mypageDTO.setJwt(jwt);
                mypageDTO.setResultMessage(resultMessage);


                return mypageDTO;
            } else {
                throw new BaseException(UPDATE_FAIL_USER);
            }

        }catch (BaseException exception){
            if(exception.getStatus().equals(UPDATE_FAIL_USER)){
                throw exception;
            }  else if (exception.getStatus().equals(POST_USERS_INVALID_NICKNAME)) {
                throw exception;
            } else if (exception.getStatus().equals(POST_USERS_EXISTS_NICKNAME)) {
                throw exception;
            } else{
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }


    //로그인
    public LoginRes login(LoginRequest loginRequest) throws BaseException {
        try {
            Optional<User> userOptional = userRepository.findByEmailAndAndPassword(loginRequest.getEmail(), loginRequest.getPassword());

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                Long id = user.getId();
                String jwt = jwtService.createJwt(id);
                String resultMessage = "'"+user.getNickname()+"'"+"님 로그인에 성공하였습니다!";
                return new LoginRes(id, user.getEmail(),user.getNickname(), jwt,resultMessage);

            } else {
                throw new BaseException(FAILED_TO_LOGIN);
            }
        } catch (BaseException exception) {
            if(exception.getStatus().equals(FAILED_TO_LOGIN)){
                throw exception;
            } else{
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    //소셜로그인 추가 정보
    public LoginRes saveUserSNSInfo(PostExtraReq postExtraReq, Long id) throws BaseException{
        Optional<User> userOptional=userRepository.findById(id);

        if(userOptional.isPresent()){
            User user =userOptional.get();
            if(user.getNickname() != null){
                throw new BaseException(USERS_ALREADY_REGISTER);
            }
        }

        try{

            if (userOptional.isPresent()){
                User user = userOptional.get();
                String jwt=jwtService.createJwt(id);
                user.setNickname(postExtraReq.getNickname());
                user.setUpdateAt(new Timestamp(System.currentTimeMillis()));

                String resultMessage = "'"+user.getNickname()+"'"+"님 소셜로그인에 성공하였습니다!";
                userRepository.save(user);
                return new LoginRes(id, user.getEmail(), user.getNickname(), jwt,resultMessage);
            } else {
                throw new BaseException(UPDATE_FAIL_USER);
            }

        }catch (BaseException exception){
            if(exception.getStatus().equals(UPDATE_FAIL_USER)){
                throw exception;
            } else{
                throw new BaseException(DATABASE_ERROR);
            }
        }
    }

    public void deleteByUser (Long id) throws BaseException {
        try{
            Optional<User> userOptional = userRepository.findById(id);
            if (userOptional.isPresent()){
                User user = userOptional.get();

                userRepository.delete(user);
            }

        }catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }




}