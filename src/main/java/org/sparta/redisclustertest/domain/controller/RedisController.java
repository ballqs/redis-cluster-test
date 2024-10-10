package org.sparta.redisclustertest.domain.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sparta.redisclustertest.domain.dto.CreateDto;
import org.sparta.redisclustertest.domain.dto.UpdateDto;
import org.sparta.redisclustertest.domain.service.RedisService;
import org.springframework.web.bind.annotation.*;

// Redis 작성시 참조 글 : https://akku-dev.tistory.com/102
@RestController
@RequiredArgsConstructor
@RequestMapping("/redis")
@Slf4j
public class RedisController {

    private final RedisService redisService;

    @PostMapping
    public void create(@RequestBody CreateDto createDto) {
        redisService.create(createDto);
    }

    @PatchMapping("/{id}")
    public void update(@PathVariable String id , @RequestBody UpdateDto updateDto) {
        redisService.update(id , updateDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        redisService.delete(id);
    }

    @GetMapping("/{id}")
    public void select(@PathVariable String id) {
        redisService.select(id);
    }
}
