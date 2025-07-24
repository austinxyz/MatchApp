package com.utr.match;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DemoController {

    @GetMapping("/demo/sse")
    public String sseDemo() {
        return "sse-demo";
    }
}
