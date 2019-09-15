package com.castor.menulist.Helper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentContainer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.castor.menulist.BuildConfig;
import com.castor.menulist.Fragments.FragmentContent;
import com.castor.menulist.Interface.NavigationManager;
import com.castor.menulist.MainActivity;
import com.castor.menulist.R;


public class FragmentNavigationManager implements NavigationManager {
    private static FragmentNavigationManager mInstance;
    private FragmentManager mFragmentManager;
    private MainActivity mainActivity;

    public static FragmentNavigationManager getmInstance(MainActivity mainActivity){
        if (mInstance == null)
            mInstance = new FragmentNavigationManager();

        mInstance.configure(mainActivity);
        return  mInstance;
    }

    private void configure(MainActivity mainActivity){
        mainActivity = mainActivity;
        mFragmentManager = mainActivity.getSupportFragmentManager();
    }
    @Override
    public void ShowFragment(String title) {
      ShowFragment(FragmentContent.newInstance(title),false);
    }

    public void ShowFragment(Fragment fragmenContent, boolean b){

        FragmentManager fm = mFragmentManager;
        FragmentTransaction ft  = fm.beginTransaction().replace(R.id.container,fragmenContent);
        ft.addToBackStack(null);

        if (b || !BuildConfig.DEBUG)
            ft.commitAllowingStateLoss();
        else
            ft.commit();

        fm.executePendingTransactions();
    }
}
