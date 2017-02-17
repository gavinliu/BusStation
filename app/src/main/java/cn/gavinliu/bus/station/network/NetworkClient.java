package cn.gavinliu.bus.station.network;

import java.util.concurrent.TimeUnit;

import cn.gavinliu.bus.station.network.okhttp.BasicParamsInterceptor;
import cn.gavinliu.bus.station.network.okhttp.CookieManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Gavin on 17-1-6.
 */

public class NetworkClient {

    private static NetworkClient sInstance;

    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;

    private NetworkClient() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);

        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(new BasicParamsInterceptor.Builder().build())
                .cookieJar(new CookieManager())
                .addInterceptor(logging)
                .build();

        mRetrofit = new Retrofit.Builder()
                .baseUrl("http://www.zhbuswx.com")
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public synchronized static NetworkClient getInstance() {
        if (sInstance == null) sInstance = new NetworkClient();
        return sInstance;
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}
