package com.placementassist.institute.modules.health;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller to handle root requests and redirect them to the Institute dashboard.
 */
@Controller
public class RootRedirectController {

    @GetMapping(value = {"/", "/index.html"})
    public String rootRedirect() {
        return "redirect:/dashboard.html";
    }
}
