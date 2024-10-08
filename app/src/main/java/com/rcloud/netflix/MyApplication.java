package com.rcloud.netflix;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import com.classes.purchaselogic.BaseApplication;
import com.example.config.config;
import com.example.webview.WebViewAppConfig;
import com.onesignal.OSInAppMessage;
import com.onesignal.OSInAppMessageLifecycleHandler;
import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.alfonz.utility.Logcat;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyApplication extends BaseApplication {
    private static MyApplication mInstance;
    public SharedPreferences preferences;
    public String prefName = "Primeflix";

    private AdController adControllerlocalInstance;

    private String appId = "cd6a92d4-12c9-4692-bc90-3397c652e739";

    public MyApplication() {
        mInstance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        adControllerlocalInstance = AdController.getInstance();

        adControllerlocalInstance.initAd(this);

        OSInAppMessageLifecycleHandler handler = new OSInAppMessageLifecycleHandler() {
            @Override
            public void onWillDisplayInAppMessage(OSInAppMessage message) {
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "MainApplication onWillDisplayInAppMessage");
            }
            @Override
            public void onDidDisplayInAppMessage(OSInAppMessage message) {
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "MainApplication onDidDisplayInAppMessage");
            }
            @Override
            public void onWillDismissInAppMessage(OSInAppMessage message) {
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "MainApplication onWillDismissInAppMessage");
            }
            @Override
            public void onDidDismissInAppMessage(OSInAppMessage message) {
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "MainApplication onDidDismissInAppMessage");
            }
        };
        OneSignal.setInAppMessageLifecycleHandler(handler);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.setAppId(appId);
        OneSignal.initWithContext(this);
        OneSignal.setNotificationOpenedHandler(result ->
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "OSNotificationOpenedResult result: " + result.toString()));
        OneSignal.setNotificationWillShowInForegroundHandler(notificationReceivedEvent -> {
            OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "NotificationWillShowInForegroundHandler fired!" +
                    " with notification event: " + notificationReceivedEvent.toString());

            OSNotification notification = notificationReceivedEvent.getNotification();
            JSONObject data = notification.getAdditionalData();

            notificationReceivedEvent.complete(notification);
        });
        OneSignal.unsubscribeWhenNotificationsAreDisabled(true);
        OneSignal.pauseInAppMessages(true);
        OneSignal.setLocationShared(false);

        mInstance = this;
//        AnalyticsTrackers.initialize(this);
//        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

//        if (AudienceNetworkAds.isInAdsProcess(this)) {
//            return;
//        } // else execute default application initialization code

        //Advance webview
        // init logcat
        Logcat.init(WebViewAppConfig.LOGS, "Primeflix");

        //Facebook Login
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);

        //Facebook Login print Keyhash
        // Add code to print out the key hash
//        Log.d("KeyHash", "key:" + FacebookSdk.getApplicationSignature(this));
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                System.out.println("KeyHash:"+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPurchaseCode() {
        return config.PURCHASE_CODE;
    }

    @Override
    public String getEmail() {
        return "TAG_EMAIL";
    }


    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void saveIsLogin(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedIn", flag);
        editor.apply();
    }

    public boolean getIsLogin() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsLoggedIn", false);
    }

    public void saveIsRemember(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putBoolean("IsLoggedRemember", flag);
        editor.apply();
    }

    public boolean getIsRemember() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsLoggedRemember", false);
    }

    public void saveRemember(String email, String password) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putString("remember_email", email);
        editor.putString("remember_password", password);
        editor.apply();
    }

    public String getRememberEmail() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("remember_email", "");
    }

    public String getRememberPassword() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("remember_password", "");
    }

    public void saveLogin(String user_id, String user_name, String email) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putString("user_id", user_id);
        editor.putString("user_name", user_name);
        editor.putString("email", email);
        editor.apply();
    }

    public String getUserId() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("user_id", "");
    }

    public String getUserName() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("user_name", "");
    }

    public String getUserEmail() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getString("email", "");
    }

    public void saveIsNotification(boolean flag) {
        preferences = this.getSharedPreferences(prefName, 0);
        Editor editor = preferences.edit();
        editor.putBoolean("IsNotification", flag);
        editor.apply();
    }

    public boolean getNotification() {
        preferences = this.getSharedPreferences(prefName, 0);
        return preferences.getBoolean("IsNotification", true);
    }

//    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
//        @Override
//        public void notificationOpened(OSNotificationOpenResult result) {
//            JSONObject data = result.notification.payload.additionalData;
//            System.out.println("Rajan_data" + data);
//            Log.e("data", "" + data);
//            String customKey;
//            String isExternalLink;
//            if (data != null) {
//                customKey = data.optString("video_id", null);
//                isExternalLink = data.optString("external_link", null);
//                 if (customKey != null) {
//                    if (!customKey.equals("0")) {
//                        Constant.LATEST_IDD=customKey;
//                        Intent intent = new Intent(MyApplication.this, ActivityVideoDetails.class);
//                        intent.putExtra("Id", Constant.LATEST_IDD);
//                        intent.putExtra("isNotification", true);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    } else {
//                        if (!isExternalLink.equals("false")) {
//                            Intent intent = new Intent(Intent.ACTION_VIEW,
//                                    Uri.parse(isExternalLink));
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        } else {
//                            Intent intent = new Intent(MyApplication.this, SplashActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        }
//                    }
//                }
//            }
//        }
//    }
}
