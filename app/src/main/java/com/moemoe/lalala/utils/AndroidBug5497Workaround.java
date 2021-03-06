package com.moemoe.lalala.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

/**
 * Created by yi on 2017/4/12.
 */

public class AndroidBug5497Workaround {
    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.

    public static void assistActivity (Activity activity) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            new AndroidBug5497Workaround(activity);
        }
     }

    private Activity activity;
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private AndroidBug5497Workaround(final Activity activity) {
        this.activity = activity;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                  possiblyResizeChildOfContent(activity);
              }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent(Activity activity) {
        int usableHeightNow = computeUsableHeight(activity);
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();

            //这个判断是为了解决19之前的版本不支持沉浸式状态栏导致布局显示不完全的问题
//            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
//            Rect frame = new Rect();
//            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
            //int statusBarHeight = frame.top;
           // usableHeightSansKeyboard -= statusBarHeight;
//            }
           // int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
           // if(resourceId > 0){
              //  usableHeightSansKeyboard -= activity.getResources().getDimensionPixelSize(resourceId);
                usableHeightSansKeyboard -= ViewUtils.getNavigationBarHeight(activity);
          //  }

            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard/4)) {
                    // keyboard probably just became visible
                    frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                } else {
                    // keyboard probably just became hidden
                    frameLayoutParams.height = usableHeightSansKeyboard;
                }
                mChildOfContent.requestLayout();
                usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight(Activity activity) {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        if(r.top==0){
            int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if(resourceId > 0){
                r.top = activity.getResources().getDimensionPixelSize(resourceId);
            }
            //状态栏目的高度
        }
        return (r.bottom);
    }

}
