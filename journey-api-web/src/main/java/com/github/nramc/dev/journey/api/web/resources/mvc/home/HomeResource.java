package com.github.nramc.dev.journey.api.web.resources.mvc.home;

import com.github.nramc.dev.journey.api.core.app.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static com.github.nramc.dev.journey.api.web.resources.Resources.HOME;

@Controller
@RequiredArgsConstructor
public class HomeResource {
    private final ApplicationProperties applicationProperties;

    @GetMapping(value = HOME, produces = MediaType.TEXT_HTML_VALUE)
    public String home(Model model) {
        model.addAttribute("applicationProperties", applicationProperties);
        return "home";
    }

}
