package com.example.spot.controller;

import com.example.spot.config.exception.BaseException;
import com.example.spot.config.exception.BaseResponse;
import com.example.spot.model.DTO.*;
import com.example.spot.service.UserService;
import com.example.spot.utils.JwtService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.example.spot.config.exception.BaseResponseStatus.*;


@RestController
@RequiredArgsConstructor
@Api(tags = "User", description = "사용자")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    /**
     * 회원가입 API
     * [POST] /users
     * @return BaseResponse<JoinRes>
     */

    @ResponseBody
    @Transactional
    @PostMapping(value = "/users")
    @ApiOperation(value="회원가입", notes="이메일, 비밀번호, 전화번호, 프로필이미지 등으로 회원 가입한다.")
    @ApiResponses(value={@ApiResponse(code =4000,message = "데이터베이스 연결에 실패하였습니다."),@ApiResponse(code=2017,message = "이미 가입된 이메일입니다."),
           @ApiResponse(code=2024,message = "중복된 닉네임입니다."), @ApiResponse(code=2015,message = "이메일을 입력해주세요."),@ApiResponse(code=2021,message = "비밀번호를 입력해주세요.")
    })
    public BaseResponse<JoinRes> joinUser(@RequestBody JoinRequest joinRequest)  {
        //회원가입 시 이메일을 입력하지 않았을 때
        if (joinRequest.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        //회원가입 시 비밀번호를 입력하지 않았을 때
        if (joinRequest.getPassword() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PWD);
        }



        try{
            JoinRes joinRes = userService.signUp(joinRequest);
            return new BaseResponse<>(joinRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 로그인 API
     * [POST] /users/login
     *
     * @return BaseResponse<LoginRes>
     *
     * */
    @ResponseBody
    @Transactional
    @PostMapping(value = "/users/login")
    @ApiOperation(value="로그인", notes="이메일, 비밀번호로 로그인하기")
    @ApiResponses(value={@ApiResponse(code =2015,message = "이메일을 입력해주세요."),@ApiResponse(code=2021,message = "비밀번호를 입력해주세요."),
            @ApiResponse(code=3014,message = "없는 아이디거나 비밀번호가 틀렸습니다."),@ApiResponse(code=4000,message = "데이터베이스 연결에 실패하였습니다.")})
    public BaseResponse<LoginRes> Login(@RequestBody LoginRequest loginRequest){
        //로그인 시 이메일 입력하지 않았을 때
        if(loginRequest.getEmail() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }

        //로그인 시 비밀번호를 입력하지 않았을 때
        if(loginRequest.getPassword() == null){
            return new BaseResponse<>(POST_USERS_EMPTY_PWD);
        }

        try{
            LoginRes loginRes = userService.login(loginRequest);
            return new BaseResponse<>(loginRes);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 소셜 로그인 추가 정보 API
     * [POST] /users/sns
     *
     * @return BaseResponse<PostUserRes>
     *
     * */

    @ResponseBody
    @Transactional
    @PostMapping(value = "/users/sns")
    @ApiOperation(value="소셜로그인 추가", notes="닉네임 추가 \n headers = {\"X-ACCESS-TOKEN\": jwt}; 설정해주기(jwt는 로그인하면 반환되는 jwt이다.")
    @ApiResponses(value={@ApiResponse(code =2023,message = "닉네임을 입력해주세요."),@ApiResponse(code=2025,message = "닉네임은 2 ~ 20자 사이로 입력해주세요."),
            @ApiResponse(code=2010,message = "유저 아이디 값을 확인해주세요."),
            @ApiResponse(code=4014,message = "유저 정보를 수정하는데 실패했습니다."),
            @ApiResponse(code=4000,message = "데이터베이스 연결에 실패하였습니다."),@ApiResponse(code=2012,message = "이미 저장한 소셜로그인 유저입니다.")
    })
    public BaseResponse<LoginRes> saveUserAfterInfo (@RequestBody PostExtraReq postExtraReq){
        if (postExtraReq.getNickname()==null){
            return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
        }
        if (postExtraReq.getNickname().length()<2 || postExtraReq.getNickname().length()>20){
            return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
        }


        try{
            Long id_jwt = jwtService.getUserIdx();

            if(id_jwt != 0){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            LoginRes loginRes = userService.saveUserSNSInfo(postExtraReq,id_jwt);
            return new BaseResponse<>(loginRes);
        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 마이페이지 수정 API
     * [PUT] /mypage
     * @return BaseResponse<MypageDTO>
     */

    @ResponseBody
    @PutMapping(value = "/mypage")
    @ApiOperation(value="마이페이지 수정", notes="비밀번호을 수정한다. \n headers = {\"X-ACCESS-TOKEN\": jwt}; 설정해주기(jwt는 로그인하면 반환되는 jwt이다.")
    @ApiResponses(value={@ApiResponse(code =4000,message = "데이터베이스 연결에 실패하였습니다."),@ApiResponse(code=2025,message = "닉네임은 2 ~ 20자 사이로 입력해주세요."),
            @ApiResponse(code=2024,message = "중복된 닉네임입니다."), @ApiResponse(code=4014,message = "유저 정보를 수정하는데 실패했습니다."),
            @ApiResponse(code=2003,message = "권한이 없는 유저의 접근입니다."),@ApiResponse(code=2010,message = "유저 아이디 값을 확인해주세요.")
    })
    public BaseResponse<MypageDTO> mypagefix(@RequestParam(required = false) String password, @RequestParam(required = false) String nickname)  {

        try{
            MypageDTO mypageDTO = userService.mypagefixInfo(password,nickname);
            return new BaseResponse<>(mypageDTO);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }



    /**
     * 마이페이지 조회 API
     * [GET] /mypage/{id}
     * @return BaseResponse<MypageDTO>
     */

    @ResponseBody
    @Transactional
    @GetMapping(value = "/mypage")
    @ApiOperation(value="마이페이지 조회", notes=" headers = {\"X-ACCESS-TOKEN\": jwt}; 설정해주기(jwt는 로그인하면 반환되는 jwt이다.")
    @ApiResponses(value={@ApiResponse(code =4000,message = "데이터베이스 연결에 실패하였습니다."),
            @ApiResponse(code=2003,message = "권한이 없는 유저의 접근입니다."),
            @ApiResponse(code=2010,message = "유저 아이디 값을 확인해주세요.")
    })
    public BaseResponse<MypageDTO> mypageget()   {

        try{
            MypageDTO mypageDTO = userService.mypageFocus();
            return new BaseResponse<>(mypageDTO);
        }catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 회원탈퇴 API
     * [DELETE] /users
     *
     * @return BaseResponse<St>
     *
     * */
    @ResponseBody
    @DeleteMapping("/users")
    @ApiOperation(value="회원 탈퇴", notes="회원 탈퇴")
    public BaseResponse<String> deleteUsers() {
        try{
            Long id_jwt = jwtService.getUserIdx();

            if(id_jwt != 0){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

            //회원 삭제
            userService.deleteByUser(id_jwt);

            return new BaseResponse<>("회원 탈퇴를 설공했습니다!");

        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }






}