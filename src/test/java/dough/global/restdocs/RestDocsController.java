package dough.global.restdocs;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestDocsController {

    @GetMapping("/rest-docs")
    public String restDocs() {
        return "rest docs!";
    }
}
