package cn.emay.ql.uniloginsdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.geetest.onelogin.OneLoginHelper;
import com.geetest.onelogin.config.OneLoginThemeConfig;
import com.geetest.onelogin.listener.AbstractOneLoginListener;

import org.json.JSONException;
import org.json.JSONObject;

import cn.emay.ql.uniloginsdk.listeners.InitCallback;
import cn.emay.ql.uniloginsdk.listeners.LoginCallback;
import cn.emay.ql.uniloginsdk.net.HttpCallback;
import cn.emay.ql.uniloginsdk.net.HttpUtils;

public class UniSDK {
    private Context mContext;
    private String mAppId;
    private String mAesKey = "";
    protected String mSdkSign;
    private String mSdkId;
    private String mSdkSecretKey;
    private boolean isDialogStyle;
    protected String mToken;
    protected String mAuthCode;
    protected String mBusinessId;
    protected String mProcessId;
    private boolean initSucc;
    private static UniSDK instance;
    protected InitCallback mInitCallBack;
    protected LoginCallback mLoginCallback;

    private UniSDK() {
    }

    public void initAuth(Context context, String appId, String appSecret,InitCallback initCallback) {
        mContext = context;
        this.mAppId = appId;
        this.mAesKey = appSecret;
        mInitCallBack = initCallback;
        //首先访问亿美接口获取key等信息
        getAppConfig();
    }

    public static synchronized UniSDK getInstance() {
        if (instance == null) {
            instance = new UniSDK();
        }
        return instance;
    }

    private static Handler myHandler = new Handler(Looper.getMainLooper());

    private void getAppConfig() {
        //通过code去取access_token
        byte[] commonData = DataUtil.getConfigJsonData(mContext, mAesKey);
        HttpUtils.sendRequest(Constans.URL_SDK_CONFIG, mAppId, mAesKey, commonData, new HttpCallback() {
            @Override
            public void onSuccess(String msg) {
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    mSdkSign = jsonObject.getString("sdkSign");
                    mSdkId = jsonObject.getString("appId");
                    mSdkSecretKey = jsonObject.getString("secretKey");
                    mBusinessId = jsonObject.getString("businessId");
                    initSDKLogin();
                    if (mInitCallBack != null) {
                        mInitCallBack.onSuccess("初始化成功");
                    }
                    initSucc = true;
                } catch (Exception e) {
                    initSucc = false;
                    e.printStackTrace();
                    if (mInitCallBack != null) {
                        myHandler.removeCallbacksAndMessages(null);
                        mInitCallBack.onFailed("取配置json解析失败" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailed(String msg) {
                initSucc = false;
                if (mInitCallBack != null) {
                    mInitCallBack.onFailed(msg);
                }
                myHandler.removeCallbacksAndMessages(null);
            }
        });
    }

    private void initSDKLogin() {
//        mSdkId = "106a4d5b7690e80413981e3bc873440d";
        OneLoginHelper.with().setLogEnable(true).init(mContext,mSdkId).register("",5000);
    }
    public void login(OneLoginThemeConfig oneLoginThemeConfig, LoginCallback callback){
        mLoginCallback = callback;
        OneLoginHelper
                .with()
                .requestToken(oneLoginThemeConfig, new AbstractOneLoginListener() {
                    @Override
                    public void onResult(JSONObject jsonObject) {
                        try {
                            int statusResult = jsonObject.getInt("status");
                            // status=200 为取号成功，其他返回码请参考返回码章节
                            if (statusResult != 200) {
                                if (mLoginCallback != null) {
                                    mLoginCallback.onFailed(jsonObject.toString());
                                }
                                return;
                            }
                                try {
                                    mProcessId = jsonObject.getString("process_id");
                                    mToken = jsonObject.getString("token");
                                    mAuthCode = jsonObject.optString("authcode"); //必选参数
                                    accessEmayLogin();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    protected void accessEmayLogin() {
        //访问亿美login接口
        byte[] commonData = DataUtil.getLoginJsonData(mContext, mAesKey);
        HttpUtils.sendRequest(Constans.URL_SDK_LOGIN, mAppId, mAesKey, commonData, new HttpCallback() {
            @Override
            public void onSuccess(String msg) {
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    String mobile = jsonObject.getString("mobile");
                    myHandler.removeCallbacksAndMessages(null);
                    if (mLoginCallback != null) {
                        mLoginCallback.onSuccess(mobile);
                    }
                } catch (Exception e) {
                    myHandler.removeCallbacksAndMessages(null);
                    if (mLoginCallback != null) {
                        mLoginCallback.onFailed("解析最终登录结果失败" + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailed(String msg) {
                myHandler.removeCallbacksAndMessages(null);
                if (mLoginCallback != null) {
                    mLoginCallback.onFailed(msg);
                }
            }
        });

    }



    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
