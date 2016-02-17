package com.seafile.seadroid2;

import android.app.Application;
import android.content.Context;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialCommunityModule;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.seafile.seadroid2.avatar.AuthImageDownloader;
import com.seafile.seadroid2.data.DataManager;
import com.seafile.seadroid2.gesturelock.AppLockManager;

import java.io.File;
import java.security.Security;

public class SeadroidApplication extends Application {
    private static Context context;
    
    public void onCreate() {
        super.onCreate();
        Iconify.with(new MaterialCommunityModule());

        SeadroidApplication.context = getApplicationContext();
        initImageLoader(getApplicationContext());

        // set gesture lock if available
        AppLockManager.getInstance().enableDefaultAppLockIfAvailable(this);
    }

    static {
        // http://stackoverflow.com/questions/6898801/how-to-include-the-spongy-castle-jar-in-android
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public static Context getAppContext() {
        return SeadroidApplication.context;
    }
    
    public static void initImageLoader(Context context) {
        
        File cacheDir = DataManager.getThumbnailCacheDirectory();
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .diskCache(new UnlimitedDiscCache(cacheDir))
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .imageDownloader(new AuthImageDownloader(context, 10000, 10000))
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}