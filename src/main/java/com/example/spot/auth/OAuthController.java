package com.example.spot.auth;

import com.example.spot.config.exception.BaseException;
import com.example.spot.config.exception.BaseResponse;
import com.example.spot.auth.model.PostKakaoReq;
import com.example.spot.model.DTO.PostUserRes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
@Api(tags = "Oauth", description = "소셜로그인")
public class OAuthController {
    private final OAuthService OAuthService;

    public OAuthController(OAuthService OAuthService){
        this.OAuthService=OAuthService;
    }

    /**
     * 카카오 소셜 로그인 api (CALLBACK)
     * [POST] /oauth/kakao
     *
     * @return BaseResponse<PostUserRes>
     */

    @PostMapping(value = "/kakao")
    @Transactional
    @ResponseBody
    @ApiOperation(value="카카오 로그인", notes="accessToken 넣기")
    @ApiResponses(value={@ApiResponse(code =4000,message = "데이터베이스 연결에 실패하였습니다."),@ApiResponse(code=4013,message = "카카오톡 연결에 실패하였습니다.")})
    public BaseResponse<PostUserRes> kakaoLogin (@RequestBody PostKakaoReq postKakaoReq) {
        try{
            PostUserRes postUserRes = OAuthService.saveUser(postKakaoReq.getAccess_token());
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
