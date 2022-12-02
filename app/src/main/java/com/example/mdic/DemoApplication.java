package com.example.mdic;

import android.app.Application;
import android.content.Context;

import com.example.mdic.data.DataManager;
import com.example.mdic.di.component.ApplicationComponent;
import com.example.mdic.di.component.DaggerApplicationComponent;
import com.example.mdic.di.module.ApplicationModule;
import javax.inject.Inject;

/**
 * Created by janisharali on 25/12/16.
 */

public class DemoApplication extends Application {

    protected ApplicationComponent applicationComponent;

    @Inject
    DataManager dataManager;

    public static DemoApplication get(Context context) {
        return (DemoApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent =  DaggerApplicationComponent
                                    .builder()
                                    .applicationModule(new ApplicationModule(this))
                                    .build();
        applicationComponent.inject(this);
    }

    public ApplicationComponent getComponent(){
        return applicationComponent;
    }
}
