package com.nowy.baselib.utils.statusBar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by Nowy on 2018/3/8.
 * 注意：
 * 在StatusBarManager.Mode.immersive()方法需要在setContentView之前调用，否则可能会无效（华为4.4无效）
 */

public class StatusBarManager {
    public static int DEFAULT_COLOR = 0;//默认的状态栏颜色
    public static float DEFAULT_ALPHA = 0;//Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 0.2f : 0.3f;

    public static final int MIN_API = 19;//最小API


    public static Builder newBuilder(Activity activity){
        return new Builder(activity);
    }

    public static class Builder{
        private Reference<Activity> mAtyRef ;
        private int mStatusBarColor = StatusBarManager.DEFAULT_COLOR;
        private boolean mFitSystemWindow = true;
        private boolean isImmersive;
        private boolean isDark ;
        private View[] paddingViews;
        private View[] paddingSmartViews;
        private View[] marginViews;
        public Builder(Activity activity){
            this.mAtyRef = new WeakReference<>(activity);
        }

        public Builder darkRes(@ColorRes int colorRes){
            if(mAtyRef.get() == null) return this;
            int color = ContextCompat.getColor(mAtyRef.get(),colorRes);
            return dark(color);
        }

        public Builder darkRes(@ColorRes int colorRes, boolean fitSystemWindow){
            int color = ContextCompat.getColor(mAtyRef.get(),colorRes);
            return dark(color,fitSystemWindow);
        }

        public Builder dark(@ColorInt int color){
            return dark(color,true);
        }

        public Builder dark(@ColorInt int color, boolean fitSystemWindow){
            this.isDark = true;
            this.mStatusBarColor = color;
            this.mFitSystemWindow = fitSystemWindow;
            return this;
        }

        public Builder lightRes(@ColorRes int colorRes){
            return lightRes(colorRes,true);
        }


        public Builder lightRes(@ColorRes int colorRes, boolean fitSystemWindow){
            int color = ContextCompat.getColor(mAtyRef.get(),colorRes);
            return light(color,fitSystemWindow);
        }


        public Builder light(@ColorInt int color){
            return light(color,true);
        }

        public Builder light(@ColorInt int color, boolean fitSystemWindow){
            this.isDark = false;
            this.mStatusBarColor = color;
            this.mFitSystemWindow = fitSystemWindow;
            return this;
        }

        public Builder padding(View ... view){
            this.paddingViews = view;
            return this;
        }


        public Builder paddingSmart(View... view){
            this.paddingSmartViews = view;
            return this;
        }

        public Builder margin(View ... view){
            this.marginViews = view;
            return this;
        }


        public Builder immersive(){
            this.isImmersive = true;
            return this;
        }

        public void build(){
            if(mAtyRef != null && mAtyRef.get() != null){
                if(isImmersive){
                    Mode.immersive(mAtyRef.get().getWindow());
                }
                if(isDark){
                    Theme.dark(mAtyRef.get(),mStatusBarColor,mFitSystemWindow);
                }else{
                    Theme.light(mAtyRef.get(),mStatusBarColor,mFitSystemWindow);
                }


                if(paddingViews != null && paddingViews.length > 0){
                    for(View view : paddingViews){
                        Size.setPadding(mAtyRef.get(),view);
                    }
                }

                if(marginViews != null && marginViews.length > 0 ){
                    for(View view : marginViews){
                        Size.setMargin(mAtyRef.get(),view);
                    }
                }



                if(paddingSmartViews != null && paddingSmartViews.length > 0 ){
                    for(View view : paddingSmartViews){
                        Size.setPaddingSmart(mAtyRef.get(),view);
                    }
                }
            }
        }

    }

    /**
     * 状态栏样式
     */
    public static class Theme{
        /**
         * 文字为黑色
         * @param activity
         * @param color
         * @param fitSystemWindow 注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View . 预留出系统 View 的空间.
         *                        {@link StatusBarKitkatImpl#setStatusBarColor(Window ,int ,boolean) }
         *                        页面如果需要显示到顶部（类似详情界面），设置为false
         *                        fitSystemWindow主要用来兼容API>=19或者华为系统
         */
        public static void dark(Activity activity, @ColorInt int color, boolean fitSystemWindow){
            theme(activity,color,true,fitSystemWindow);
        }


        /**
         * @see Theme#dark(Activity activity,int color, boolean fitSystemWindow)
         * @param activity
         */
        public static void dark(Activity activity){
            dark(activity,DEFAULT_COLOR);
        }

        /**
         * @see Theme#dark(Activity activity,int color, boolean fitSystemWindow)
         * @param activity
         */
        public static void darkRes(Activity activity,@ColorRes int colorRes){
            if(activity == null) return;
            int color =  ContextCompat.getColor(activity,colorRes);
            dark(activity,color);
        }

        /**
         * @see Theme#dark(Activity activity,int color, boolean fitSystemWindow)
         * @param activity
         */
        public static void dark(Activity activity,@ColorInt int color){
            theme(activity,color,true,true);
        }

        /**
         * @see Theme#dark(Activity activity,int color, boolean fitSystemWindow)
         * @param activity
         * @param fitSystemWindow
         */
        public static void dark(Activity activity, boolean fitSystemWindow){
            theme(activity,DEFAULT_COLOR,true,fitSystemWindow);
        }


        /**
         * @see Theme#light(Activity activity,int color, boolean fitSystemWindow)
         * @param activity
         */
        public static void light(Activity activity){
            light(activity,DEFAULT_COLOR);
        }


        /**
         * @see Theme#light(Activity activity,int color, boolean fitSystemWindow)
         * @param activity
         */
        public static void lightRes(Activity activity,@ColorRes int colorRes){
            if(activity == null) return;
            int color =  ContextCompat.getColor(activity,colorRes);
            light(activity,color);
        }

        /**
         * @see Theme#light(Activity activity,int color, boolean fitSystemWindow)
         * @param activity
         */
        public static void light(Activity activity,@ColorInt int color){
            theme(activity,color,false,true);
        }

        /**
         * 文字为白色
         * @param activity
         * @param color
         * @param fitSystemWindow
         */
        public static void light(Activity activity,@ColorInt int color, boolean fitSystemWindow){
            theme(activity,color,false,fitSystemWindow);
        }


        /**
         * @see Theme#light(Activity activity,int color, boolean fitSystemWindow)
         * @param activity
         * @param fitSystemWindow
         */
        public static void light(Activity activity, boolean fitSystemWindow){
            theme(activity,DEFAULT_COLOR,false,fitSystemWindow);
        }


        public static void theme(Activity activity, @ColorInt int color,boolean isDark, boolean fitSystemWindow){
            StatusBarCompat.setStatusBarColor(activity,color,isDark,fitSystemWindow);
        }
    }

    /**
     * 沉浸式
     */
    public static class Mode{
        public static void immersive(Window window) {
            if (Build.VERSION.SDK_INT >= 21) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
                systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                window.getDecorView().setSystemUiVisibility(systemUiVisibility);
            } else if (Build.VERSION.SDK_INT >= 19) {
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else if (Build.VERSION.SDK_INT >= MIN_API && Build.VERSION.SDK_INT > 16) {
                int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
                systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                window.getDecorView().setSystemUiVisibility(systemUiVisibility);
            }
        }
    }

    /**
     * 宽高编辑
     */
    public static class Size{
        /** 增加View的paddingTop,增加的值为状态栏高度 */
        public static void setPadding(Context context, View view) {
            if (Build.VERSION.SDK_INT >= MIN_API) {
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                        view.getPaddingRight(), view.getPaddingBottom());
            }
        }
        /** 增加View的paddingTop,增加的值为状态栏高度 (智能判断，并设置高度)*/
        public static void setPaddingSmart(Context context, View view) {
            if (Build.VERSION.SDK_INT >= MIN_API) {
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                if (lp != null && lp.height > 0) {
                    lp.height += getStatusBarHeight(context);//增高
                }
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                        view.getPaddingRight(), view.getPaddingBottom());
            }
        }

        /** 增加View的高度以及paddingTop,增加的值为状态栏高度.一般是在沉浸式全屏给ToolBar用的 */
        public static void setHeightAndPadding(Context context, View view) {
            if (Build.VERSION.SDK_INT >= MIN_API) {
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                lp.height += getStatusBarHeight(context);//增高
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(context),
                        view.getPaddingRight(), view.getPaddingBottom());
            }
        }
        /** 增加View上边距（MarginTop）一般是给高度为 WARP_CONTENT 的小控件用的*/
        public static void setMargin(Context context, View view) {
            if (Build.VERSION.SDK_INT >= MIN_API) {
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                if(lp == null){
                    lp = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }

                if (lp instanceof ViewGroup.MarginLayoutParams) {
                    ((ViewGroup.MarginLayoutParams) lp).topMargin += getStatusBarHeight(context);//增高
                }
                view.setLayoutParams(lp);
            }
        }


        public static void subMargin(Context context, View view) {
            if (Build.VERSION.SDK_INT >= MIN_API) {
                ViewGroup.LayoutParams lp = view.getLayoutParams();
                if(lp == null){
                    lp = new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }

                if (lp instanceof ViewGroup.MarginLayoutParams) {
                    ((ViewGroup.MarginLayoutParams) lp).topMargin -= getStatusBarHeight(context);//增高
                }
                view.setLayoutParams(lp);
            }
        }

        /** 获取状态栏高度 */
        public static int getStatusBarHeight(Context context) {
            int result = 24;
            int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resId > 0) {
                result = context.getResources().getDimensionPixelSize(resId);
            } else {
                result = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        result, Resources.getSystem().getDisplayMetrics());
            }

            return result;
        }
    }


    /**
     * 当前界面是否沉浸模式
     * @param activity
     * @return
     */
    public static boolean isImmersive(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if ((WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS & activity.getWindow().getAttributes().flags)
                    == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) {
                return true;
            }
        }else if(Build.VERSION.SDK_INT >= MIN_API && Build.VERSION.SDK_INT > 16){
            int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
            if((View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN & systemUiVisibility)
                    == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN){
                return true;
            }

        }

        return false;
    }
}
