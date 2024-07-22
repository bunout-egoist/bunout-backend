package dough.login.dto.response;

import java.util.Map;

public class AppleResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public AppleResponse(Map<String, Object> attribute) {
        // Apple API의 응답 구조에 따라 attribute에서 필요한 정보를 추출합니다.
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return "apple";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString(); // Apple의 사용자 ID (sub)
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
