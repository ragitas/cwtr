package com.example.app.view;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.app.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by roach on 2017/8/31.
 */

public class LoadingDialog {
    private static Map<String,Object> dialogMap = Collections.synchronizedMap(new HashMap<String,Object>());


    public static void show(android.app.FragmentManager manager,String key){
        checkThread();
        AppLoadingDialog dialog = newInstance(manager);
        dialogMap.put(key,dialog);
        dialog.show();
    }

    public static void show(android.support.v4.app.FragmentManager manager,String key){
        checkThread();
        V4LoadingDialog dialog = newInstance(manager);
        dialogMap.put(key,dialog);
        dialog.show();
    }


    public static void dismiss(String key){
        Object o = dialogMap.remove(key);
        if(o != null){
            if(o instanceof AppLoadingDialog){
                ((AppLoadingDialog)o).dismiss();
            }else if( o instanceof  V4LoadingDialog){
                ((V4LoadingDialog)o).dismiss();
            }
        }
    }


    private static AppLoadingDialog newInstance(android.app.FragmentManager manager){
        return new AppLoadingDialog(manager);
    }

    private static V4LoadingDialog newInstance(android.support.v4.app.FragmentManager manager){
        return new V4LoadingDialog(manager);
    }


    @SuppressLint("ValidFragment")
    public static class AppLoadingDialog extends android.app.DialogFragment {

        android.app.FragmentManager mAppFragmentManager;
        public AppLoadingDialog(android.app.FragmentManager manager){
            mAppFragmentManager = manager;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(android.app.DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Light_Panel);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.loading_dialog,container,false);
            return view;
        }

        public void show() {
            show(mAppFragmentManager,"show");
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            Log.d("roach","the dialog dismiss");
        }
    }




    @SuppressLint("ValidFragment")
    public static class V4LoadingDialog extends android.support.v4.app.DialogFragment {
        android.support.v4.app.FragmentManager mFragmentManager;

        public V4LoadingDialog(android.support.v4.app.FragmentManager manager) {
            mFragmentManager = manager;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setStyle(android.app.DialogFragment.STYLE_NO_TITLE,android.R.style.Theme_Holo_Light_Panel);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.loading_dialog,container,false);
            return view;
        }

        public void show() {
            show(mFragmentManager,"show");
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            Log.d("roach","the dialog dismiss");
        }
    }

    private static void checkThread(){
        if(Looper.getMainLooper() != Looper.myLooper()){
            throw  new RuntimeException("current thread isn't ui thread");
        }
    }
}
