package com.rcloud.netflix;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.util.PrefManager;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.rcloud.netflix.MainActivity.TAG_ADMOB_INTERSTITIAL_FREQUENCY;
import static com.rcloud.netflix.MainActivity.TAG_BANNER;
import static com.rcloud.netflix.MainActivity.TAG_BANNERMAINRS;
import static com.rcloud.netflix.MainActivity.TAG_INTERPRELOAD;
import static com.rcloud.netflix.MainActivity.TAG_INTERSDIALOG;
import static com.rcloud.netflix.MainActivity.TAG_INTERSDIALOG_MAX_TIME;
import static com.rcloud.netflix.MainActivity.TAG_INTERSTITIAL;
import static com.rcloud.netflix.MainActivity.TAG_INTERSTITIALMAINRS;

//import com.google.ads.mediation.facebook.FacebookAdapter;

public class AdController {

    private static final String TAG = "Rajan_";
    //Prefrance
    private static PrefManager prf;

    //rajanads
    // ad will be shown after each x url loadings or clicks on navigation drawer menu
    private static final int ADMOB_INTERSTITIAL_FREQUENCY = 3;
    private static int sInterstitialCounter = 1;

    private static volatile AdController adControllerInstance = null;

    private MyCallback myCallback;

    public interface MyCallback {
        void callbackCall();
    }

    private AdController() {}

    public static AdController getInstance() {
        if (adControllerInstance == null) {
            synchronized(AdController.class) {
                if (adControllerInstance == null) {
                    System.out.println("Rajan_instance_created");
                    adControllerInstance = new AdController();
                }
            }
        }

        return adControllerInstance;
    }

    public static void initAd(Context context) {
        prf = new PrefManager(context);
        try {
            MobileAds.initialize(context, initializationStatus -> {
            });

            //fb
//            AudienceNetworkInitializeHelper.initialize(context);

        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private AdView mAdView;
    public void loadBannerAd(Activity context, FrameLayout bannerAdContainer) {
        AdRequest adRequest = null;

        if(prf.getString(TAG_BANNER).equalsIgnoreCase("admob")) {
            adRequest = new AdRequest.Builder().build();

//        mAdView = (AdView) findViewById(R.id.adView_view);
            mAdView = new AdView(context);

//            mAdView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
            AdSize adSize = getAdSize(context, bannerAdContainer);
            mAdView.setAdSize(adSize);

            mAdView.setAdUnitId(prf.getString(TAG_BANNERMAINRS));
            ((FrameLayout) bannerAdContainer).addView(mAdView);
            if (prf.getString("SUBSCRIBED").equals("FALSE")) {
                mAdView.loadAd(adRequest);
            }
        }

        if (prf.getString(TAG_BANNER).equalsIgnoreCase("fb")) {
            //facebook banner ads
            loadAndShowBannerAdFb(context, 2, bannerAdContainer);
        }
    }

    public void loadBannerAdwithAdUnitId(Activity context, FrameLayout bannerAdContainer, String AdUnitId) {
        AdRequest adRequest = null;

        if(prf.getString(TAG_BANNER).equalsIgnoreCase("admob")) {
            adRequest = new AdRequest.Builder().build();
        }

        if (prf.getString(TAG_BANNER).equalsIgnoreCase("admob")) {
//        mAdView = (AdView) findViewById(R.id.adView_view);
            mAdView = new AdView(context);

//            mAdView.setAdSize(com.google.android.gms.ads.AdSize.SMART_BANNER);
            AdSize adSize = getAdSize(context, bannerAdContainer);
            mAdView.setAdSize(adSize);

            System.out.println("Rajan_benner_id"+AdUnitId);
            mAdView.setAdUnitId(AdUnitId);
            ((FrameLayout) bannerAdContainer).addView(mAdView);
            if (prf.getString("SUBSCRIBED").equals("FALSE")) {
                mAdView.loadAd(adRequest);
            }
        }

        if(prf.getString(TAG_BANNER).equalsIgnoreCase("fb")) {
            //facebook banner ads
        }
    }

    public void loadBannerAdforNative(Activity context, int nativesize, FrameLayout bannerAdContainer) {
        AdView mAdView = null;
        AdRequest adRequest = null;

        if(prf.getString(TAG_BANNER).equalsIgnoreCase("admob")) {
            adRequest = new AdRequest.Builder().build();

//        mAdView = (AdView) findViewById(R.id.adView_view);
            mAdView = new AdView(context);

            if (nativesize == 0) {
                mAdView.setAdSize(AdSize.SMART_BANNER);
            } else if (nativesize == 2) {
                mAdView.setAdSize(AdSize.SMART_BANNER);
            } else {
                mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            }
//            com.google.android.gms.ads.AdSize adSize = getAdSize(context, bannerAdContainer);
//            mAdView.setAdSize(adSize);

            mAdView.setAdUnitId(prf.getString(TAG_BANNERMAINRS));
            ((FrameLayout) bannerAdContainer).addView(mAdView);
            if (prf.getString("SUBSCRIBED").equals("FALSE")) {
                mAdView.loadAd(adRequest);
            }
        }

        if(prf.getString(TAG_BANNER).equalsIgnoreCase("fb")) {
            //facebook banner ads
            loadAndShowBannerAdFb(context, nativesize, bannerAdContainer);
        }
    }

    static AdSize getAdSize(Activity context, FrameLayout bannerAdContainer) {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = bannerAdContainer.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    private void loadAndShowBannerAdFb(Activity activity, int nativesize, FrameLayout bannerAdContainer) {
        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(activity,
                prf.getString(TAG_BANNERMAINRS), com.facebook.ads.AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        FrameLayout adContainer = bannerAdContainer;

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();
    }

    private InterstitialAd mInterstitialAd; //admob
    private com.facebook.ads.InterstitialAd fbinterstitialAd; //fb

    //admob & fb
    public void loadInterAd(Activity context) {
        if (prf.getString(TAG_INTERSTITIALMAINRS).contains("rajan")) {
            return;
        }
        if (prf.getString(TAG_INTERPRELOAD).contains("no") && prf.getString(TAG_INTERSDIALOG).contains("yes")) {
            return;
        }

        if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("admob")) {
            if (mInterstitialAd == null) {
                System.out.println("Rajan_interads_load_start" + context.getClass().getSimpleName());

                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(context, prf.getString(TAG_INTERSTITIALMAINRS), adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        System.out.println("Rajan_interads_loaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        // Handle the error
                        mInterstitialAd = null;
                        // System.out.println("Rajan_interads_load_failed" + loadAdError.getMessage() + loadAdError.getResponseInfo());
                        //reload
                        // loadInterAd(context);
                    }
                });
            }
        } else if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("fb")) {
            //load facebook inter ads
            if (fbinterstitialAd == null
                    || !fbinterstitialAd.isAdLoaded()
                    || fbinterstitialAd.isAdInvalidated()) {
                System.out.println("Rajan_fb_startload");
                fbinterstitialAd = new com.facebook.ads.InterstitialAd(context, prf.getString(TAG_INTERSTITIALMAINRS));

                // Create listeners for the Interstitial Ad
                InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                    @Override
                    public void onInterstitialDisplayed(Ad ad) {
                        // Interstitial ad displayed callback
                        Log.e(TAG, "Interstitial ad displayed.");
                    }

                    @Override
                    public void onInterstitialDismissed(Ad ad) {
                        // Interstitial dismissed callback
                        // Called when fullscreen content is dismissed.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        fbinterstitialAd = null;
                        // System.out.println("Rajan_The ad was dismissed.");
                        loadInterAd(context);
                        // startActivity(context, intent, requstCode);
                        myCallback.callbackCall();
                        myCallback = null;
                        Log.e(TAG, "Interstitial ad dismissed.");
                    }

                    @Override
                    public void onError(Ad ad, AdError adError) {
                        // Ad error callback
                        fbinterstitialAd = null;
                        Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        // Interstitial ad is loaded and ready to be displayed
                        // auto saved
                        // fbinterstitialAd = ad;
                        Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                    }

                    @Override
                    public void onAdClicked(Ad ad) {
                        // Ad clicked callback
                        Log.d(TAG, "Interstitial ad clicked!");
                    }

                    @Override
                    public void onLoggingImpression(Ad ad) {
                        // Ad impression logged callback
                        Log.d(TAG, "Interstitial ad impression logged!");
                    }
                };

                fbinterstitialAd.loadAd(
                        fbinterstitialAd.buildLoadAdConfig()
                                .withAdListener(interstitialAdListener)
                                .build());
            }
        }
    }

    public void loadInterAdWriteCallBackAtLoadTime(Activity context) {
        if (prf.getString(TAG_INTERSTITIALMAINRS).contains("rajan")) {
            return;
        }
        if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("admob")) {
            if (mInterstitialAd == null) {
                System.out.println("Rajan_interads_load_start" + context.getClass().getSimpleName());

                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(context, prf.getString(TAG_INTERSTITIALMAINRS), adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        System.out.println("Rajan_interads_loaded");
                        //new
                        //write call back declare here
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                // System.out.println("Rajan_The ad was dismissed.");
                                loadInterAdWriteCallBackAtLoadTime(context);
                                // startActivity(context, intent, requstCode);
                                if (myCallback != null) {
                                    myCallback.callbackCall();
                                    myCallback = null;
                                }
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                                // Called when fullscreen content failed to show.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                // System.out.println("Rajan_The ad failed to show.");
                                loadInterAdWriteCallBackAtLoadTime(context);
                                // startActivity(context, intent, requstCode);
                                if (myCallback != null) {
                                    myCallback.callbackCall();
                                    myCallback = null;
                                }
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        // Handle the error
                        mInterstitialAd = null;
                        System.out.println("Rajan_interads_load_failed" + loadAdError.getMessage());
                    }
                });
            }
        } else if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("fb")) {
            //load facebook inter ads
        }
    }

    private InterstitialAd mInterstitialAdDialog;
    private Dialog dialog = null;
    //admob & fb
    public void loadAndShowInterAdWithDialog(Activity context, MyCallback myCallback2) {
        //note: ads counter already checked in parent method so not check again

        myCallback = myCallback2;

        if (prf.getString(TAG_INTERSTITIALMAINRS).contains("rajan")) {
            if (myCallback != null) {
                myCallback.callbackCall();
                myCallback = null;
            }
            return;
        }
        dialog = new Dialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.ad_progress_dialog, null);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(view);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("admob")) {
            if (mInterstitialAdDialog == null) {
                AdRequest adRequest = new AdRequest.Builder().build();
                InterstitialAd.load(context, prf.getString(TAG_INTERSTITIALMAINRS), adRequest, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAdDialog = interstitialAd;
                        //System.out.println("Rajan_interads_loaded");
                        //new
                        //check already dialog is dismissed by countdown or not
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();

                            //write call back declare here
                            mInterstitialAdDialog.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when fullscreen content is dismissed.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    mInterstitialAdDialog = null;
                                    // System.out.println("Rajan_The ad was dismissed.");
                                    // No preload for Dialog bqz orignal mInterstitialAd object
                                    // preload running j che so
                                    // loadInterAd(context);
                                    if (myCallback != null) {
                                        myCallback.callbackCall();
                                        myCallback = null;
                                    }
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                                    // Called when fullscreen content failed to show.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    mInterstitialAdDialog = null;
                                    // System.out.println("Rajan_The ad failed to show.");
                                    // No preload for Dialog bqz orignal mInterstitialAd object
                                    // preload running j che so
                                    // loadInterAd(context);
                                    if (myCallback != null) {
                                        myCallback.callbackCall();
                                        myCallback = null;
                                    }
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    // Called when fullscreen content is shown.
                                    // Log.d("TAG", "The ad was shown.");
                                }
                            });

                            //show ads need to pass Activity object
                            mInterstitialAdDialog.show((Activity) context);
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        // Handle the error
                        mInterstitialAdDialog = null;
                        System.out.println("Rajan_interads_load_failed" + loadAdError.getMessage());
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        if (myCallback != null) {
                            myCallback.callbackCall();
                            myCallback = null;
                        }
                        //ad not showed so do not increment counter
                        checkfbAdMinusOne();
                    }
                });
            } else {
                // count down na lidhe load thyeli ads show na thy hoi tyre aa execute thshe
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();

                    //write call back declare here
                    mInterstitialAdDialog.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            mInterstitialAdDialog = null;
                            // System.out.println("Rajan_The ad was dismissed.");
                            // No preload for Dialog bqz orignal mInterstitialAd object
                            // preload running j che so
                            // loadInterAd(context);
                            if (myCallback != null) {
                                myCallback.callbackCall();
                                myCallback = null;
                            }
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                            // Called when fullscreen content failed to show.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            mInterstitialAdDialog = null;
                            // System.out.println("Rajan_The ad failed to show.");
                            // No preload for Dialog bqz orignal mInterstitialAd object
                            // preload running j che so
                            // loadInterAd(context);
                            if (myCallback != null) {
                                myCallback.callbackCall();
                                myCallback = null;
                            }
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            // Log.d("TAG", "The ad was shown.");
                        }
                    });

                    //show ads need to pass Activity object
                    mInterstitialAdDialog.show((Activity) context);
                }
            }
        } else if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("fb")) {
            com.facebook.ads.InterstitialAd fbinterstitialAdDialog =
                    new com.facebook.ads.InterstitialAd(context, prf.getString(TAG_INTERSTITIALMAINRS)); //fb
            //load facebook inter ads
            // Create listeners for the Interstitial Ad
            InterstitialAdListener interstitialAdListenerDialog = new InterstitialAdListener() {
                @Override
                public void onInterstitialDisplayed(Ad ad) {
                    // Interstitial ad displayed callback
                    Log.e(TAG, "Interstitial ad displayed.");
                }

                @Override
                public void onInterstitialDismissed(Ad ad) {
                    // Interstitial dismissed callback
                    // Called when fullscreen content is dismissed.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    // fbinterstitialAdDialog = null;
                    // System.out.println("Rajan_The ad was dismissed.");
                    loadInterAd(context);
                    if (myCallback != null) {
                        myCallback.callbackCall();
                        myCallback = null;
                    }
                    Log.e(TAG, "Interstitial ad dismissed.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Ad error callback
                    // fbinterstitialAdDialog = null;
                    Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Interstitial ad is loaded and ready to be displayed
                    // auto saved
                    // fbinterstitialAdDialog = ad;
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();

                        if (fbinterstitialAdDialog != null
                                && fbinterstitialAdDialog.isAdLoaded()
                                && !fbinterstitialAdDialog.isAdInvalidated()) {
                            fbinterstitialAdDialog.show();
                        } else {
                            if (myCallback != null) {
                                myCallback.callbackCall();
                                myCallback = null;
                            }
                        }
                    }
                    Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Ad clicked callback
                    Log.d(TAG, "Interstitial ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Ad impression logged callback
                    Log.d(TAG, "Interstitial ad impression logged!");
                }
            };

            fbinterstitialAdDialog.loadAd(
                    fbinterstitialAdDialog.buildLoadAdConfig()
                            .withAdListener(interstitialAdListenerDialog)
                            .build());
        }

        new CountDownTimer(Integer.parseInt(prf.getString(TAG_INTERSDIALOG_MAX_TIME)) * 1000, 10) {
            @Override
            public void onTick(long millisUntilFinished) {
                double time = (millisUntilFinished / 10) / Integer.parseInt(prf.getString(TAG_INTERSDIALOG_MAX_TIME));
            }

            @Override
            public void onFinish() {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    // Called when fullscreen content is dismissed.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    // mInterstitialAd = null;
                    // System.out.println("Rajan_The ad was dismissed.");
                    if (myCallback != null) {
                        myCallback.callbackCall();
                        myCallback = null;
                    }
                    // startActivity(context, intent, requstCode);
                    //not loaded so reload it.
                    // loadInterAd(context);
                }
            }
        }.start();
    }

    public void showInterAd(final Activity context, final Intent intent, final int requstCode) {
        if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("admob")) {
            try {
                if (mInterstitialAd != null) {
                    if (AdController.checkfbAd()) {
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                // System.out.println("Rajan_The ad was dismissed.");
                                loadInterAd(context);
                                startActivity(context, intent, requstCode);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                                // Called when fullscreen content failed to show.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                // System.out.println("Rajan_The ad failed to show.");
                                loadInterAd(context);
                                startActivity(context, intent, requstCode);
                            }


                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Log.d("TAG", "The ad was shown.");
                            }
                        });
                        mInterstitialAd.show((Activity) context);
                    } else {
                        startActivity(context, intent, requstCode);
                    }
                } else {
                    startActivity(context, intent, requstCode);
                    //not loaded so reload it.
                    loadInterAd(context);
                }
            } catch (Exception e) {
                startActivity(context, intent, requstCode);
//            e.printStackTrace();
            }
        } else if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("fb")) {
            startActivity(context, intent, requstCode);
        }
    }

    private void startActivity(Activity context, Intent intent, int requestCode) {
        if (intent != null) {
            context.startActivityForResult(intent, requestCode);
        }
    }

    private void showInterAd(final Fragment context, final Intent intent, final int requstCode) {
        if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("admob")) {
            try {
                if (mInterstitialAd != null) {
                    if (AdController.checkfbAd()) {
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                // Log.d("TAG", "The ad was dismissed.");
                                loadInterAd(context.getActivity());
                                startActivity(context, intent, requstCode);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                                // Called when fullscreen content failed to show.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                // Log.d("TAG", "The ad failed to show.");
                                loadInterAd(context.getActivity());
                                startActivity(context, intent, requstCode);
                            }


                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Log.d("TAG", "The ad was shown.");
                            }
                        });
                        mInterstitialAd.show(context.getActivity());
                    } else {
                        startActivity(context, intent, requstCode);
                    }
                } else {
                    startActivity(context, intent, requstCode);
                    //not loaded so reload it.
                    loadInterAd(context.getActivity());
                }
            } catch (Exception e) {
                startActivity(context, intent, requstCode);
//            e.printStackTrace();
            }
        } else if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("fb")) {
            startActivity(context, intent, requstCode);
        }
    }

    private void startActivity(Fragment context, Intent intent, int requestCode) {
        if (intent != null) {
            context.startActivityForResult(intent, requestCode);
        }
    }

    public void showInterAdOnly(final Activity context, final int requstCode) {
        if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("admob")) {
            try {
                if (mInterstitialAd != null) {
                    if (AdController.checkfbAd()) {
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                // System.out.println("Rajan_The ad was dismissed.");
                                loadInterAd(context);
                                // startActivity(context, intent, requstCode);
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                                // Called when fullscreen content failed to show.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                // System.out.println("Rajan_The ad failed to show.");
                                loadInterAd(context);
                                // startActivity(context, intent, requstCode);
                            }


                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Log.d("TAG", "The ad was shown.");
                            }
                        });
                        mInterstitialAd.show((Activity) context);
                    } else {
//                        startActivity(context, intent, requstCode);
                    }
                } else {
//                    startActivity(context, intent, requstCode);
                    //not loaded so reload it.
                    loadInterAd(context);
                }
            } catch (Exception e) {
//                startActivity(context, intent, requstCode);
//            e.printStackTrace();
            }
        } else if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("fb")) {
//            startActivity(context, intent, requstCode);
        }
    }

    public void showInterAdCallBack(final Activity context, final MyCallback myCallback2) {
        myCallback = myCallback2;

        if (AdController.checkfbAd()) {
            //continue next for show ad
        } else {
            // startActivity(context, intent, requstCode);
            myCallback.callbackCall();
            myCallback = null;
            return;
        }

        if (prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("admob")) {
            try {
                if (mInterstitialAd != null) {
                    //ads loaded so directly show it no need for dialog yes or no check
                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when fullscreen content is dismissed.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            mInterstitialAd = null;
                            // System.out.println("Rajan_The ad was dismissed.");
                            loadInterAd(context);
                            // startActivity(context, intent, requstCode);
                            myCallback.callbackCall();
                            myCallback = null;
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                            // Called when fullscreen content failed to show.
                            // Make sure to set your reference to null so you don't
                            // show it a second time.
                            mInterstitialAd = null;
                            // System.out.println("Rajan_The ad failed to show.");
                            loadInterAd(context);
                            // startActivity(context, intent, requstCode);
                            myCallback.callbackCall();
                            myCallback = null;
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when fullscreen content is shown.
                            // Log.d("TAG", "The ad was shown.");
                        }
                    });
                    //show ads need to pass Activity object
                    mInterstitialAd.show((Activity) context);
                } else {
                    //ads not loaded so check for dialog yes or no than do next
                    if (prf.getString(TAG_INTERSDIALOG).equalsIgnoreCase("yes")) {
                        //preload with dialog(show dialog if ad not loaded)
                        //ad not loaded so show dialog till ad loaded
                        loadAndShowInterAdWithDialog(context, myCallback2);
                    } else {
                        //preload with no dialog
                        myCallback.callbackCall();
                        myCallback = null;
                        //not loaded so reload it.
                        loadInterAd(context);
                        //ad not showed so do not increment counter
                        checkfbAdMinusOne();
                    }
                }
            } catch (Exception e) {
                myCallback.callbackCall();
                myCallback = null;
                e.printStackTrace();
            }
        } else if (prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("fb")) {
            if (fbinterstitialAd != null
                    && fbinterstitialAd.isAdLoaded()
                    && !fbinterstitialAd.isAdInvalidated()) {
                //ads loaded so directly show it no need for dialog yes or no check
                // Create listeners for the Interstitial Ad
                fbinterstitialAd.show();
            } else {
                //ads not loaded so check for dialog yes or no than do next
                if (prf.getString(TAG_INTERSDIALOG).equalsIgnoreCase("yes")) {
                    //preload with dialog(show dialog if ad not loaded)
                    //ad not loaded so show dialog till ad loaded
                    loadAndShowInterAdWithDialog(context, myCallback2);
                } else {
                    //preload with no dialog
                    myCallback.callbackCall();
                    myCallback = null;
                    //not loaded so reload it.
                    loadInterAd(context);
                    //ad not showed so do not increment counter
                    checkfbAdMinusOne();
                }
            }
        }
    }

    public void showInterAdCallBackWriteCallBackAtLoadTime(final Activity context, final MyCallback myCallback2) {
        myCallback = myCallback2;

        if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("admob")) {
            try {
                if (mInterstitialAd != null) {
                    if (AdController.checkfbAd()) {
                        //show ads need to pass Activity object
                        mInterstitialAd.show((Activity) context);
                    } else {
                        // startActivity(context, intent, requstCode);
                        myCallback.callbackCall();
                        myCallback = null;
                    }
                } else {
                    myCallback.callbackCall();
                    myCallback = null;
                    // startActivity(context, intent, requstCode);
                    //not loaded so reload it.
                    loadInterAd(context);
                }
            } catch (Exception e) {
                myCallback.callbackCall();
                myCallback = null;
                // startActivity(context, intent, requstCode);
//            e.printStackTrace();
            }
        } else if(prf.getString(TAG_INTERSTITIAL).equalsIgnoreCase("fb")) {
            myCallback.callbackCall();
            myCallback = null;
            // startActivity(context, intent, requstCode);
        }
    }

    public static boolean checkfbAd() {
        if (prf.getString("SUBSCRIBED").equals("TRUE")) {
            return false;
        }
//        System.out.println("Rajan_fb_counter"+sInterstitialCounter);
        if(prf.getString(TAG_ADMOB_INTERSTITIAL_FREQUENCY) != "") {
            if (Integer.parseInt(prf.getString(TAG_ADMOB_INTERSTITIAL_FREQUENCY)) > 0 && sInterstitialCounter % Integer.parseInt(prf.getString(TAG_ADMOB_INTERSTITIAL_FREQUENCY)) == 0) {
                sInterstitialCounter++;
                return true;
            } else {
                sInterstitialCounter++;
                return false;
            }
        } else {
            if (AdController.ADMOB_INTERSTITIAL_FREQUENCY > 0 && sInterstitialCounter % AdController.ADMOB_INTERSTITIAL_FREQUENCY == 0) {
                sInterstitialCounter++;
                return true;
            } else {
                sInterstitialCounter++;
                return false;
            }
        }
    }

    public static void checkfbAdMinusOne() {
        sInterstitialCounter--;
    }

}
