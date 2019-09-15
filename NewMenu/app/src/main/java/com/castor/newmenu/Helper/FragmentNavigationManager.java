package com.castor.newmenu.Helper;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.castor.newmenu.BuildConfig;
import com.castor.newmenu.Fragments.FragmentContent;
import com.castor.newmenu.Interface.NavigationManager;
import com.castor.newmenu.MainActivity;
import com.castor.newmenu.R;

public class FragmentNavigationManager implements NavigationManager {
    private static FragmentNavigationManager mInstance;
    private FragmentManager mFragmentManager;
    private MainActivity mainActivity;

    public static FragmentNavigationManager getmInstance (MainActivity mainActivity){
        if (mInstance == null)
            mInstance = new FragmentNavigationManager();
        mInstance.configure(mainActivity);
        return mInstance;
    }

    private void configure(MainActivity mainActivity) {
        mainActivity = mainActivity;
        mFragmentManager = mainActivity.getSupportFragmentManager();
    }

    @Override
    public void showFragment(String title) {
       showFragment(FragmentContent.newInstance(title),false);
    }

    private void showFragment(Fragment fragmentContent, boolean b) {
        FragmentManager fm = mFragmentManager;
        FragmentTransaction ft = fm.beginTransaction().replace(R.id.container,fragmentContent);
        ft.addToBackStack(null);
        if (b || !BuildConfig.DEBUG)
            ft.commitAllowingStateLoss();
        else
            ft.commit();

        fm.executePendingTransactions();
    }


}
