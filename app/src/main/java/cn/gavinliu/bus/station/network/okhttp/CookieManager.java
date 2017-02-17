package cn.gavinliu.bus.station.network.okhttp;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.internal.http.HttpDate;

/**
 * Created by Gavin on 17-1-6.
 */

public class CookieManager implements CookieJar {

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = new ArrayList<>();

        Cookie cookie = new Cookie.Builder()
                .name("openid3")
                .value("oiFDwslyfVLKMuf8bP_Dz1XwxZbk")
                .domain("www.zhbuswx.com")
                .path("/Handlers")
                .expiresAt(HttpDate.MAX_DATE)
                .build();
        cookies.add(cookie);

        cookie = new Cookie.Builder()
                .name("IfAuth")
                .value("93cba07454f06a4a960172bbd6e2a435")
                .domain("www.zhbuswx.com")
                .path("/Handlers")
                .expiresAt(HttpDate.MAX_DATE)
                .build();
        cookies.add(cookie);

        cookie = new Cookie.Builder()
                .name("ptcz")
                .value("93cba07454f06a4a960172bbd6e2a435")
                .domain("www.zhbuswx.com")
                .path("/Handlers")
                .expiresAt(HttpDate.MAX_DATE)
                .build();
        cookies.add(cookie);

        return cookies;
    }
}
