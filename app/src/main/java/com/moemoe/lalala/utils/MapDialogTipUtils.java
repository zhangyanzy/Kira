package com.moemoe.lalala.utils;

import android.content.Context;
import android.icu.text.UFormat;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.MapMarkEntity;
import com.moemoe.lalala.view.widget.map.MapWidget;
import com.moemoe.lalala.view.widget.tooltip.Tooltip;
import com.moemoe.lalala.view.widget.tooltip.TooltipAnimation;

import java.util.ArrayList;
import java.util.Random;

/**
 * 地图人物对话框控制
 * Created by zhangyan on 2018/5/2.
 */

public class MapDialogTipUtils {

    private Context context;
    private ArrayList<MapMarkEntity> list;
    private MapWidget mapWidget;
    private Handler mHandler;
    private Runnable mShowTip;
    private Random random;
    private int minTime;
    private int maxTime;
    private ViewGroup viewGroup;
    private int screenW;
    private int screenH;

    private static MapDialogTipUtils instance;

    private MapDialogTipUtils() {

    }

    public static MapDialogTipUtils getInstance() {
        if (instance == null) {
            synchronized (MapDialogTipUtils.class) {
                if (instance == null) {
                    instance = new MapDialogTipUtils();
                }
            }
        }
        return instance;
    }

    public void init(Context context, final int minTime, final int maxTime, MapWidget mapWidget, ViewGroup viewGroup) {
        this.context = context;
        this.viewGroup = viewGroup;
        list = new ArrayList<>();
        this.mapWidget = mapWidget;
        this.minTime = minTime;
        this.maxTime = maxTime;
        screenW = DensityUtil.getScreenWidth(context);
        screenH = DensityUtil.getScreenHeight(context);
        random = new Random();
        mHandler = new Handler();
        mShowTip = new Runnable() {
            @Override
            public void run() {
                showTip();
                mHandler.postDelayed(mShowTip, getSec(minTime, maxTime) * 1000);
            }
        };

    }

    public int getSec(int minTime, int maxTime) {
        return random.nextInt(maxTime + 1 - minTime) + minTime;
    }

    public void updateMap(MapWidget mapWidget) {
        this.mapWidget = mapWidget;
    }

    public void updateList(ArrayList<MapMarkEntity> list) {
        this.list = list;
    }

    public void start() {
        if (mHandler == null) {
            return;
        }

        mHandler.postDelayed(mShowTip, getSec(minTime, maxTime) * 1000);
    }

    private void showTip() {
        MapMarkEntity entity = getShowEntity();
        if (entity == null) {
            return;
        }

        TextView textView = (TextView) LayoutInflater.from(context).inflate(R.layout.tooltip_textview, null);
        int viewHeight = mapWidget.getMapHeight();
        int type;
        if (entity.getY() < viewHeight / 2) {
            type = Tooltip.BOTTOM;
        } else {
            type = Tooltip.TOP;
        }
        int x = xToScreenCoords(entity.getX());
        int y = yToScreenCoords(entity.getY());
        ToolTipUtils.showTooltip(context, viewGroup, textView, mapWidget, entity.getContent(), type, x, y, entity.getW(), entity.getH(), true,
                TooltipAnimation.SCALE_AND_FADE,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ContextCompat.getColor(context, R.color.main_cyan));
    }

    private MapMarkEntity getShowEntity() {
        ArrayList<MapMarkEntity> tmpList = new ArrayList<>();
        for (MapMarkEntity entity : list) {
            if (!TextUtils.isEmpty(entity.getContent())) {
                int x = xToScreenCoords(entity.getX());
                int y = yToScreenCoords(entity.getY());
                if (x >= 0 && x <= screenW && y >= 0 && y <= screenH) {
                    tmpList.add(entity);
                }
            }
        }
        if (tmpList.size() > 0) {
            return tmpList.get(random.nextInt(tmpList.size()));
        }
        return null;
    }

    private int xToScreenCoords(int mapCoord) {
        return (int) (mapCoord * mapWidget.getScale() - mapWidget.getScrollX());
    }

    private int yToScreenCoords(int mapCoord) {
        return (int) (mapCoord * mapWidget.getScale() - mapWidget.getScrollY());
    }

    public void stop() {
        if (mHandler != null) mHandler.removeCallbacks(mShowTip);
    }

    public void release() {
        stop();
        minTime = 0;
        maxTime = 0;
        mHandler = null;
        mShowTip = null;
        context = null;
        list = null;
        mapWidget = null;
    }


}
