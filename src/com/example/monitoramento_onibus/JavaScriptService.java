package com.example.monitoramento_onibus;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class JavaScriptService {
    private int height;
    private int weight;
    
    public JavaScriptService(int height, int weight) {
        this.height = height;
        this.weight = weight;
    }
    
    @JavascriptInterface
    public String getHeight() {
        return String.valueOf(this.height);
    }
    
    @JavascriptInterface
    public String getWeight() {
        return String.valueOf(this.weight);
    }


}
