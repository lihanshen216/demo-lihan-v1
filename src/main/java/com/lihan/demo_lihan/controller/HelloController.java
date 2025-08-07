package com.lihan.demo_lihan.controller;

import com.lihan.demo_lihan.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Result<String> hello() {
        return Result.success("Hello, Spring Boot!");
    }

    // 测试异常接口
    @GetMapping("/exception")
    public Result<String> throwException() {
        throw new RuntimeException("故意抛出的异常，用于测试全局异常处理");
    }
}
