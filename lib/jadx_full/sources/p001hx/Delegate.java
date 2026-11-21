package p001hx;

import android.app.Activity;
import android.app.AppComponentFactory;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.util.Log;

/* loaded from: classes2.dex */
public final class Delegate extends AppComponentFactory {
    private static final String TAG = "hxreborn/ijiami";
    private AppComponentFactory packer;

    private AppComponentFactory packer(ClassLoader classLoader, ApplicationInfo applicationInfo) {
        if (this.packer != null) {
            return this.packer;
        }
        String packerFactory = Boot.packerFactory(applicationInfo);
        if (packerFactory.isEmpty()) {
            Log.e(TAG, "missing packer component factory name");
            return null;
        }
        try {
            this.packer = (AppComponentFactory) Class.forName(packerFactory, true, classLoader).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            Log.i(TAG, "packer component factory " + packerFactory + " ready");
        } catch (Throwable th) {
            Log.e(TAG, "cannot instantiate component factory " + packerFactory, th);
        }
        return this.packer;
    }

    @Override // android.app.AppComponentFactory
    public ClassLoader instantiateClassLoader(ClassLoader classLoader, ApplicationInfo applicationInfo) {
        ClassLoader instantiateClassLoader;
        Boot.restoreFactoryName(applicationInfo);
        Boot.install(applicationInfo);
        AppComponentFactory packer = packer(classLoader, applicationInfo);
        if (packer == null) {
            instantiateClassLoader = super.instantiateClassLoader(classLoader, applicationInfo);
        } else {
            instantiateClassLoader = packer.instantiateClassLoader(classLoader, applicationInfo);
        }
        Log.i(TAG, "payload class loader ready");
        Boot.writeCodeOverlay(instantiateClassLoader, applicationInfo);
        Boot.runAppHook(instantiateClassLoader);
        return instantiateClassLoader;
    }

    @Override // android.app.AppComponentFactory
    public Application instantiateApplication(ClassLoader classLoader, String str) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Application instantiateApplication;
        Log.i(TAG, "instantiating application " + str);
        if (this.packer == null) {
            instantiateApplication = super.instantiateApplication(classLoader, str);
        } else {
            instantiateApplication = this.packer.instantiateApplication(classLoader, str);
        }
        Log.i(TAG, "application ready");
        return instantiateApplication;
    }

    @Override // android.app.AppComponentFactory
    public Activity instantiateActivity(ClassLoader classLoader, String str, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return this.packer == null ? super.instantiateActivity(classLoader, str, intent) : this.packer.instantiateActivity(classLoader, str, intent);
    }

    @Override // android.app.AppComponentFactory
    public BroadcastReceiver instantiateReceiver(ClassLoader classLoader, String str, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return this.packer == null ? super.instantiateReceiver(classLoader, str, intent) : this.packer.instantiateReceiver(classLoader, str, intent);
    }

    @Override // android.app.AppComponentFactory
    public Service instantiateService(ClassLoader classLoader, String str, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return this.packer == null ? super.instantiateService(classLoader, str, intent) : this.packer.instantiateService(classLoader, str, intent);
    }

    @Override // android.app.AppComponentFactory
    public ContentProvider instantiateProvider(ClassLoader classLoader, String str) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return this.packer == null ? super.instantiateProvider(classLoader, str) : this.packer.instantiateProvider(classLoader, str);
    }
}
