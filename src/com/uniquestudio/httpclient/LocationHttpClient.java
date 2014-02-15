package com.uniquestudio.httpclient;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;


public class LocationHttpClient {

    private static final String BASE_URL = "http://api.map.baidu.com/";

    private static AsyncHttpClient httpClient = new AsyncHttpClient();

    public static void get(String url, RequestParams params,
	    AsyncHttpResponseHandler responseHandler) {
	httpClient.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params,
	    AsyncHttpResponseHandler responseHandler) {
	httpClient.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
	return BASE_URL + relativeUrl;
    }
}
