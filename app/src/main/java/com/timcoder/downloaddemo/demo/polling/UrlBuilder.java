package com.timcoder.downloaddemo.demo.polling;

import android.text.TextUtils;

/**
 * @author Administrator
 * 2019/10/14 0014.
 */
public class UrlBuilder {

    private static final String a = "fy";
    private String f; // 原文内容类型
    private String t; // 译文内容类型
    private String w; // 查询内容

    public UrlBuilder setF(String f) {
        this.f = f;
        return this;
    }

    public UrlBuilder setT(String t) {
        this.t = t;
        return this;
    }

    public UrlBuilder setW(String w) {
        this.w = w;
        return this;
    }

    public String build(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        sb.append(url).append("?").append("f=").append(f)
                .append("t=").append(t)
                .append("w=" + w);
        return sb.toString();
    }

}
