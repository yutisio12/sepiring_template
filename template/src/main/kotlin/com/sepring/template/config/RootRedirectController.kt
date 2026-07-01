package com.sepring.template.config

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class RootRedirectController {

    @GetMapping("/")
    fun redirectToSwagger(): String = "redirect:/swagger-ui/index.html"
}
