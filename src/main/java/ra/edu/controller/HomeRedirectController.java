package ra.edu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeRedirectController {
    @GetMapping("/")
    public String redirectToCourseList() {
        return "redirect:/courses/list";
    }
}