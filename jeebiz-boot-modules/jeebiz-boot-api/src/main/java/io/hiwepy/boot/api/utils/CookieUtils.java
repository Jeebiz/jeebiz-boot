package io.hiwepy.boot.api.utils;

import javax.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

public final class CookieUtils {

    public static String getCookieValue(HttpServletRequest request, String name) {
        return Optional.ofNullable(getCookie(request, name)).map(Cookie::getValue).orElse(null);
    }

    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(name)).findFirst().orElse(null);
    }
}
