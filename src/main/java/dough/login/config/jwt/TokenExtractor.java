package dough.login.config.jwt;

import dough.global.exception.InvalidJwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static dough.global.exception.ExceptionCode.INVALID_ACCESS_TOKEN;

@Component
public class TokenExtractor {

    private final static String AUTHORIZATION_HEADER = "Authorization";
    private final static String REFRESH_TOKEN_HEADER = "RefreshToken";
    private final static String BEARER_PREFIX = "Bearer ";

    public String getAccessToken() {
        return getToken(AUTHORIZATION_HEADER);
    }

    public String getRefreshToken() {
        return getToken(REFRESH_TOKEN_HEADER);
    }

    private String getToken(final String header) {
        final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        final String headerValue = request.getHeader(header);

        if (headerValue != null && headerValue.startsWith(BEARER_PREFIX)) {
            return headerValue.substring(BEARER_PREFIX.length()).trim();
        }
        throw new InvalidJwtException(INVALID_ACCESS_TOKEN);
    }
}