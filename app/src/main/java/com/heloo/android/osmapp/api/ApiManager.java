package com.heloo.android.osmapp.api;

import android.os.Build;
import android.util.Log;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.utils.MD5;
import com.heloo.android.osmapp.utils.StringUtils;
import com.heloo.android.osmapp.utils.Utils;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * <p>
 * 所有的请求控制
 */

public class ApiManager {

    private static final String TAG = "ApiManager";
    private Retrofit mRetrofit;
    private static final int DEFAULT_TIMEOUT = 60;
    OkHttpClient.Builder builder;

    /**
     * 初始化请求体
     */
    private ApiManager() {
        ClearableCookieJar cookieJar =    //此操作将后台的cookie持久化，避免上传文件清空了后台的cookie
                new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(Utils.getContext()));
        builder = new OkHttpClient.Builder();
        //手动创建一个OkHttpClient并设置超时时间
        builder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        builder.cookieJar(cookieJar);
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i(TAG, "log: " + message);
            }
        });
        loggingInterceptor.setLevel(level);
        builder.addInterceptor(loggingInterceptor);
//        builder.addInterceptor(postInterceptor);
        builder.addInterceptor(cookileInter);
        builder.addInterceptor(headerInterceptor);
//        builder.addNetworkInterceptor(new ParamsInterceptor());  //添加网络拦截器
    }

    private static class SingletonHolder {
        private static final ApiManager INSTANCE = new ApiManager();
    }

    //获取单例
    public static ApiManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 获取请求代理
     *
     * @param service
     * @param url
     * @param <T>
     * @return
     */
    public <T> T configRetrofit(Class<T> service, String url) {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(builder.build())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return mRetrofit.create(service);
    }

    Interceptor postInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Request.Builder requestBuilder = request.newBuilder();
            if (request.body() instanceof MultipartBody) {   //上传文件时，不进入此过滤器
                return chain.proceed(request);
            }
            String postBodyString = bodyToString(request.body());
            String params = "";
            if (postBodyString.contains("&")) {
                String[] post = postBodyString.split("&");
                SortedMap<String, Object> keyAndValues = new TreeMap<>();
                if (post != null && post.length != 0) {
                    for (String param : post) {
                        String key = param.split("=")[0];
                        String value;
                        try {
                            value = param.split("=")[1];
                        } catch (Exception e) {
                            e.printStackTrace();
                            value = "";
                        }
                        keyAndValues.put(key, value);
                    }
                }
            }
            //避免中文生成签名时和后台的编码冲突
            String encodeParm = StringUtils.isEmpty(params) ? postBodyString : params.substring(0, params.length() - 1);
            String sign = MD5.strToMd5Low32(URLEncoder.encode(URLDecoder.decode(encodeParm, "UTF-8"), "UTF-8"));
            RequestBody formBody = new FormBody.Builder()
                    .add("sign", sign)
                    .build();
            postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(formBody);
            request = requestBuilder
                    .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"),
                            postBodyString))
                    .build();
            return chain.proceed(request);
        }
    };

    /**
     * 请求时获取session，方便各个H5的session同步
     */
    Interceptor cookileInter = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            Headers headers = response.headers();
            //获取cookie
            String cookies = headers.get("Set-Cookie");
            if (cookies != null) {
                String cookie[] = cookies.split(";");
                if (cookie != null && cookie.length != 0) {
                    if (cookie[0].split("=") != null && cookie[0].split("=").length != 0) {
//                        MyApplication.SESSIONID = cookie[0].split("=")[1];
//                        LogUtils.I(MyApplication.SESSIONID);
                    }
                }
            }
            return response;
        }
    };

    Interceptor headerInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            String token = MyApplication.spUtils.getString("token", "");
            if (StringUtils.isEmpty(token)) {
                return chain.proceed(chain.request());
            }
            Request request;
            request = chain.request().newBuilder()
                    .removeHeader("Authorization")
                    .addHeader("Authorization", MyApplication.spUtils.getString("token", ""))
                    .build();
            return chain.proceed(request);
        }
    };


    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }


    /**
     * 将英文参数进行排序
     */
    Comparator comparator = new Comparator<String>() {
        private final int DOWN_LINE = '_';

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public int compare(String s1, String s2) {
            s1 = s1.toLowerCase();
            s2 = s2.toLowerCase();
            for (int i = 0; i < Math.min(s1.length(), s2.length()); i++) {
                int atChar1 = s1.charAt(i);
                atChar1 = atChar1 == DOWN_LINE ? Integer.MAX_VALUE : atChar1;
                int atChar2 = s2.charAt(i);
                atChar2 = atChar2 == DOWN_LINE ? Integer.MAX_VALUE : atChar2;
                if (Integer.compare(atChar1, atChar2) != 0) {
                    return Integer.compare(atChar1, atChar2);
                }
            }
            return Integer.compare(s1.length(), s2.length());
        }
    };

}
