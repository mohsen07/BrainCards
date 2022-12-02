package com.example.mdic.di.component;

import android.app.Application;
import android.content.Context;

import com.example.mdic.DemoApplication;
import com.example.mdic.data.DataManager;
import com.example.mdic.data.DbHelper;
import com.example.mdic.data.SharedPrefsHelper;
import com.example.mdic.di.ApplicationContext;
import com.example.mdic.di.module.ApplicationModule;
import javax.inject.Singleton;

import dagger.Component;


/**
 * Created by janisharali on 08/12/16.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(DemoApplication demoApplication);

    @ApplicationContext
    Context getContext();

    Application getApplication();

    DataManager getDataManager();

    SharedPrefsHelper getPreferenceHelper();

    DbHelper getDbHelper();

}
