package com.moemoe.lalala.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.TextUtils;
import android.view.View;

import com.moemoe.lalala.BuildConfig;
import com.moemoe.lalala.R;
import com.moemoe.lalala.view.activity.BaseAppCompatActivity;
import com.moemoe.lalala.view.activity.WebViewActivity;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by yi on 2016/11/28.
 */

public class IntentUtils {//TODO 待优化代码结构与跳转方式 by yi
    public static ArrayList<String> sSupportSchame = new ArrayList<>();

    public static void init(Context context) {
        sSupportSchame.add(context.getResources().getString(R.string.label_doc_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_doc_action_old));
        sSupportSchame.add(context.getResources().getString(R.string.label_tag_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_room_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_url_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_out_url_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_department_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_trash_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_img_trash_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_gacha_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_cal_column_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_plot_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_qiu_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_swim_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_donation_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_snow_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_user_info_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_user_info_user_comment_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_user_info_notify_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_blackboard_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_talk_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_folder_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_bag_action));
        sSupportSchame.add("event_1.0");
        sSupportSchame.add(context.getResources().getString(R.string.label_map_event_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_game_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_luntan_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_dynamic_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_common_file_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_manhua_file_action));
        sSupportSchame.add(context.getResources().getString(R.string.label_manhua_file_action));
        sSupportSchame.add("conversationlist");
        sSupportSchame.add("conversation");
        sSupportSchame.add("conversation/private");
        sSupportSchame.add("conversation/group");
        sSupportSchame.add("conversation/detail");
    }

    public static void haveShareWeb(Context context, Uri uri, View v) {
        if (sSupportSchame.size() <= 0) init(context);
        try {
            Intent i = new Intent();
            String path = uri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (sSupportSchame.contains(path)) {
                if (path.equals(context.getResources().getString(R.string.label_url_action))) {
                    i.setPackage(uri.getHost());
                    i.setData(uri);
                    i.setAction(path);
                    i.putExtra(BaseAppCompatActivity.UUID, uri.getQuery());
                    i.putExtra(WebViewActivity.EXTRA_KEY_SHARE, true);
                    context.startActivity(i);
                }
            }
        } catch (Exception e) {

        }
    }

    public static void toActivityForResultFromUri(Context context, Uri uri, View v, int requestCode) {
        if (sSupportSchame.size() <= 0) init(context);
        try {
            Intent i = new Intent();
            String path = uri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (path.equals(context.getResources().getString(R.string.label_tag_action))) {
                return;
            }
            if (sSupportSchame.contains(path)) {
                if (path.equals("event_1.0")) {
//                    showEvent(context);
                } else if (path.equals(context.getResources().getString(R.string.label_out_url_action))) {
                    Uri uri1 = Uri.parse(uri.getQuery());
                    i.setData(uri1);
                    i.setAction("android.intent.action.VIEW");
                    context.startActivity(i);
                } else {
                    if (path.equals("department_1.0") && uri.getQueryParameter("name").equals("图书馆")) {
                        i.setPackage(uri.getHost());
                        i.setData(uri);
                        i.setAction("library");
                    } else {
                        i.setPackage(uri.getHost());
                        i.setData(uri);
                        i.setAction(path);
                    }
                    String query = uri.getQuery();
                    if (path.equals(context.getResources().getString(R.string.label_url_action))) {
                        if (query.contains("netaopera/chap")) {
                            i.putExtra(WebViewActivity.EXTRA_KEY_SHOW_TOOLBAR, false);
                        }
                        i.putExtra(BaseAppCompatActivity.UUID, uri.getQuery());
                    } else {
                        if (!TextUtils.isEmpty(query)) {
                            if (!query.contains("&")) {
                                if (query.contains("=")) {
                                    Set<String> names = uri.getQueryParameterNames();
                                    for (String name : names) {
                                        i.putExtra(name, uri.getQueryParameter(name));
                                    }
                                } else {
                                    i.putExtra(BaseAppCompatActivity.UUID, uri.getQuery());
                                }
                            } else {
                                Set<String> names = uri.getQueryParameterNames();
                                for (String name : names) {
                                    i.putExtra(name, uri.getQueryParameter(name));
                                }
                            }
                        }
                    }
                    if (path.equals(context.getResources().getString(R.string.label_plot_action))) {
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(v, 0, 0, 0, 0);
                        if (requestCode == -1) {
                            ActivityCompat.startActivity(context, i, options.toBundle());
                        } else {
                            ActivityCompat.startActivityForResult((Activity) context, i, requestCode, options.toBundle());
                        }
                    } else {
                        if (requestCode == -1) {
                            context.startActivity(i);
                        } else {
                            ((Activity) context).startActivityForResult(i, requestCode);
                        }
                    }
                }
            } else {
                WebViewActivity.startActivity(context, BuildConfig.NOT_FOUND_PAGE);
            }
        } catch (Exception e) {
            WebViewActivity.startActivity(context, BuildConfig.NOT_FOUND_PAGE);
        }
    }

    public static void toActivityFromUri(Context context, Uri uri, View v) {
        toActivityForResultFromUri(context, uri, v, -1);
    }

    public static Intent getIntentFromUri(Context context, Uri uri) {
        if (sSupportSchame.size() <= 0) init(context);
        Intent i = new Intent();
        try {
            String path = uri.getPath();
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            if (sSupportSchame.contains(path)) {
                if (path.equals(context.getResources().getString(R.string.label_out_url_action))) {
                    Uri uri1 = Uri.parse(uri.getQuery());
                    i.setData(uri1);
                    i.setAction("android.intent.action.VIEW");
                } else {
                    i.setPackage(uri.getHost());
                    i.setData(uri);
                    i.setAction(path);
                    String query = uri.getQuery();
                    if (path.equals(context.getResources().getString(R.string.label_url_action))) {
                        i.putExtra(BaseAppCompatActivity.UUID, uri.getQuery());
                    } else {
                        if (!query.contains("&")) {
                            i.putExtra(BaseAppCompatActivity.UUID, uri.getQuery());
                        } else {
                            Set<String> names = uri.getQueryParameterNames();
                            for (String name : names) {
                                i.putExtra(name, uri.getQueryParameter(name));
                            }
                        }
                    }
                }
            } else {
                i.setClass(context, WebViewActivity.class);
                i.putExtra(WebViewActivity.EXTRA_KEY_URL, BuildConfig.NOT_FOUND_PAGE);
            }
            return i;
        } catch (Exception e) {
            i.setClass(context, WebViewActivity.class);
            i.putExtra(WebViewActivity.EXTRA_KEY_URL, BuildConfig.NOT_FOUND_PAGE);
        } finally {
            return i;
        }
    }

//    private static void showEvent(final Context context){
//        int pass = PreferenceUtils.getPassEvent(context);
//        if(StringUtils.isKillEvent() && pass < 3){
//            if(!AppSetting.isEnterEventToday){
//                final AlertDialogUtil alertDialogUtil = AlertDialogUtil.getInstance();
//                alertDialogUtil.createNormalDialog(context,context.getString(R.string.label_enter_event));
//                alertDialogUtil.setOnClickListener(new AlertDialogUtil.OnClickListener() {
//                    @Override
//                    public void CancelOnClick() {
//                        alertDialogUtil.dismissDialog();
//                    }
//
//                    @Override
//                    public void ConfirmOnClick() {
//                        alertDialogUtil.dismissDialog();
//                        Intent i = new Intent(context,EventActivity.class);
//                        context.startActivityForResult(i);
//                    }
//                });
//                alertDialogUtil.showDialog();
//            }
//        }else if((StringUtils.isKillEvent() && pass == 3) || pass > 3){
//            AppSetting.isEnterEventToday = true;
//            WebViewActivity.startActivityForResult(context,false,"http://prize.moemoe.la:8000/netaopera/chap4/?pass=" + pass  + "&user_id=" + PreferenceUtils.getUUid());
//        }
//    }
}
