package com.example.spot.config.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.example.spot.config.exception.BaseResponseStatus.*;


@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess","code","message","result"})
public class BaseResponse<T> {
    // BaseResponse : Response할 때, 공통 부분은 묶고 다른 부분은 제네릭을 통해 구현함으로써 반복되는 코드를 줄여준다.
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String message;
    private final int code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    //만약 요청에 성공했을 경우
    public BaseResponse(T result) {
        this.isSuccess = SUCCESS.isSuccess(); //성공 여부 true로 설정
        this.message = SUCCESS.getMessage(); //성공 메시지 설정
        this.code = SUCCESS.getCode(); //성공 코드 설정
        this.result = result; //응답 결과 데이터 설정
    }

    //요청 실패의 경우
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess=status.isSuccess(); //실패상태 설정
        this.message = status.getMessage(); //실패 메시지 설정
        this.code = status.getCode(); //실패 코드 설정
    }

}
