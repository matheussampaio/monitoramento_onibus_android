package com.sony.monitoramento_onibus.model;

import android.webkit.JavascriptInterface;

public class JavaScriptService {
    private final int height;
    private final int weight;

    public JavaScriptService(int height, int weight) {
        this.height = height;
        this.weight = weight;
    }

    @JavascriptInterface
    public String getHeight() {
        return String.valueOf(this.height + 150);
    }

    @JavascriptInterface
    public String getWeight() {
        return String.valueOf(this.weight);
    }

}
