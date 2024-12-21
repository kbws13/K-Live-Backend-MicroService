package xyz.kbws.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author kbws
 * @date 2024/12/21
 * @description:
 */
@RestController
public class TestController {

    @RequestMapping("/test")
    public String test() {
        return "Hello, World";
    }
}
