package dough.global.restdocs;

import dough.global.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RestDocsController.class)
@MockBean(JpaMetamodelMappingContext.class)
public class RestDocsControllerTest extends AbstractControllerTest {

    @Test
    void RestDocsTest() throws Exception {
        mockMvc.perform(get("/rest-docs")).andExpect(status().isOk());
    }
}
