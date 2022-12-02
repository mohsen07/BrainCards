package com.example.mdic.di.component;

import com.example.mdic.CardActivity;
import com.example.mdic.ExamActivity;
import com.example.mdic.FolderActivity;
import com.example.mdic.FolderActivity2;
import com.example.mdic.MainActivity;
import com.example.mdic.data.FolderAdapter;
import com.example.mdic.di.PerActivity;
import com.example.mdic.di.module.ActivityModule;
import dagger.Component;

/**
 * Created by janisharali on 08/12/16.
 */

@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity mainActivity);
    void inject(FolderActivity folderActivity);
    void inject(FolderActivity2 folderActivity2);
    void inject(CardActivity cardActivity);
    void inject(ExamActivity examActivity);

}
