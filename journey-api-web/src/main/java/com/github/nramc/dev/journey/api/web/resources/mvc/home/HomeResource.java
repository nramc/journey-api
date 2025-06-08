package com.github.nramc.dev.journey.api.web.resources.mvc.home;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.github.nramc.dev.journey.api.web.resources.Resources.HOME;

@Controller
@RequiredArgsConstructor
public class HomeResource {

    @GetMapping(value = HOME, produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        return "home";
    }

}
