package com.example.spot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @GetMapping("/sorting-update")
    public ResponseEntity<?> addSortingKey() {
        ValueOperations<String, String> zop = redisTemplate.opsForValue();
        zop.set("yellow", "banana");
        // 게시글의 정보들을 (likeCnt,게시글 때려박은 Json),(createdAt, 게시글 때려박은 Json) redis로 쳐박는다
        // 태그별로 중복 선택이 가능하다?
        // 하루에 한번 호출하고, expire 기간을 하루로 잡아야함
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/sorting-request")
    public ResponseEntity<?> getSortingKey(@PathVariable String key) {
        ValueOperations<String, String> vop = redisTemplate.opsForValue();
        String value = vop.get(key);
        return new ResponseEntity<>(value, HttpStatus.OK);
    }
}