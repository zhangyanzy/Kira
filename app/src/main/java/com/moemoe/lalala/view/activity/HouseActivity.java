package com.moemoe.lalala.view.activity;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.moemoe.lalala.BuildConfig;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.AppSetting;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.databinding.ActivityHouseBinding;
import com.moemoe.lalala.di.components.DaggerHouseComponent;
import com.moemoe.lalala.di.modules.HouseModule;
import com.moemoe.lalala.event.HouseLikeEvent;
import com.moemoe.lalala.galgame.FileManager;
import com.moemoe.lalala.galgame.SoundManager;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.DocDetailEntity;
import com.moemoe.lalala.model.entity.HouseDbEntity;
import com.moemoe.lalala.model.entity.HouseLikeEntity;
import com.moemoe.lalala.model.entity.MapEntity;
import com.moemoe.lalala.model.entity.MapMarkContainer;
import com.moemoe.lalala.model.entity.REPORT;
import com.moemoe.lalala.model.entity.RubbishEntity;
import com.moemoe.lalala.model.entity.RubblishBody;
import com.moemoe.lalala.model.entity.SaveVisitorEntity;
import com.moemoe.lalala.model.entity.ShareArticleEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;
import com.moemoe.lalala.model.entity.VisitorsEntity;
import com.moemoe.lalala.presenter.DormitoryContract;
import com.moemoe.lalala.presenter.DormitoryPresenter;
import com.moemoe.lalala.utils.AlertDialogUtil;
import com.moemoe.lalala.utils.BitmapUtils;
import com.moemoe.lalala.utils.DialogUtils;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.GreenDaoManager;
import com.moemoe.lalala.utils.MapUtil;
import com.moemoe.lalala.utils.NetworkUtils;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ShareUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.base.BaseActivity;
import com.moemoe.lalala.view.widget.map.MapLayout;
import com.moemoe.lalala.view.widget.map.TouchImageView;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.reactivestreams.Subscription;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static android.view.View.DRAWING_CACHE_QUALITY_AUTO;
import static android.view.View.DRAWING_CACHE_QUALITY_HIGH;
import static android.view.View.DRAWING_CACHE_QUALITY_LOW;
import static com.moemoe.lalala.utils.StartActivityConstant.REQ_DELETE_TAG;

public class HouseActivity extends BaseActivity implements DormitoryContract.View {


    private boolean mIsOut = false;
    private ActivityHouseBinding binding;
    static private Activity instance;
    private String mSchema;

    @Inject
    DormitoryPresenter mPresenter;
    private MapMarkContainer mContainer;
    private Disposable initDisposable;
    private Disposable resolvDisposable;
    private int type;
    private RelativeLayout mRlRoleRoot;
    private TextView mTvRoleNum;
    private TextView mTvRoleName;
    private RelativeLayout mRlRoleJuQing;
    private ImageView mIvCover;
    private TextView mTvRewardName;
    private RelativeLayout mRleSelect;
    private TextView mTvLeft;
    private TextView mTvRight;
    private ImageView mIvGongXI;
    private RubbishEntity mRubbishEntity;
    private TextView mTvJuQing;
    private TextView mTvContent;
    private TextView mTvChuWu;
    private TextView mTvCnanle;
    private int count;
    private BottomMenuFragment bottomMenuFragment;
    private String saveBitmap;
    private Bitmap bitmap;
    private TextView mTvUserName;

    @Override
    protected void initComponent() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_house);
        binding.setPresenter(new Presenter());
        mRlRoleRoot = findViewById(R.id.rl_role_root);
        mTvRoleNum = findViewById(R.id.tv_role_num);
        mTvRoleName = findViewById(R.id.tv_role_name);
        mRlRoleJuQing = findViewById(R.id.rl_role_juqing);
        mIvCover = findViewById(R.id.iv_cover_next);
        mRleSelect = findViewById(R.id.rl_select);
        mTvLeft = findViewById(R.id.tv_left);
        mTvRight = findViewById(R.id.tv_right);
        mIvGongXI = findViewById(R.id.iv_gongxi);
        mTvJuQing = findViewById(R.id.tv_juqing);
        mTvContent = findViewById(R.id.tv_content_gongxi);
        mTvChuWu = findViewById(R.id.tv_chuwu);
        mTvCnanle = findViewById(R.id.tv_canle);
        mTvUserName = findViewById(R.id.tv_user_name);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        AppSetting.isRunning = true;
        Intent mIntent = getIntent();
        if (mIntent != null) {
            mSchema = mIntent.getStringExtra("schema");
        }
        DaggerHouseComponent.builder()
                .houseModule(new HouseModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);

        mContainer = new MapMarkContainer();
        initMap();
        SoundManager.init(this);
        FileManager.init(this);
        initPopupMenus();
        EventBus.getDefault().register(this);
    }

    public void initMap() {
        mPresenter.addMapMark(HouseActivity.this, mContainer, binding.map, "house");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (MoeMoeApplication.getInstance().GoneDiaLog()) {
            return true;
        }
        if (MoeMoeApplication.getInstance().isMenu()) {
            MoeMoeApplication.getInstance().GoneMenu();
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.ivHouseQrCode.setVisibility(View.INVISIBLE);
        binding.tvHouseName.setVisibility(View.INVISIBLE);
        binding.tvHouseVivit.setVisibility(View.INVISIBLE);
        MoeMoeApplication.getInstance().VisibilityWindowMager(this);
        binding.map.clearAllView();
        binding.map.addTouchView(HouseActivity.this);
        mPresenter.getVisitorsInfo();
        mPresenter.loadHouseObjects(true, "");
        if (TextUtils.isEmpty(PreferenceUtils.getUUid())) {
            binding.ivPersonal.setImageResource(R.drawable.bg_default_circle);
        } else {
            int size = (int) getResources().getDimension(R.dimen.x64);
            Glide.with(this)
                    .load(StringUtils.getUrl(this, PreferenceUtils.getAuthorInfo().getHeadPath(), size, size, false, true))
                    .error(R.drawable.bg_default_circle)
                    .placeholder(R.drawable.bg_default_circle)
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(binding.ivPersonal);
        }
        binding.map.setOnImageClickLietener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsOut) {
                    imgIn();
                    mIsOut = false;
                } else {
                    imgOut();
                    mIsOut = true;
                }
            }
        });
    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {
        mRlRoleRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRlRoleRoot.setVisibility(View.GONE);
            }
        });
        mRlRoleJuQing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int type = mRubbishEntity.getType();
//                if (type == 3 && mIvGongXI.getVisibility() == View.VISIBLE) {
//                    mRlRoleJuQing.setVisibility(View.GONE);
//                    mTvContent.setVisibility(View.GONE);
//                    mTvJuQing.setVisibility(View.GONE);
//                    mIvGongXI.setVisibility(View.GONE);
//                    mRleSelect.setVisibility(View.GONE);
//                } else if (type == 4 && mIvGongXI.getVisibility() == View.VISIBLE) {
//                    mRlRoleJuQing.setVisibility(View.GONE);
//                    mTvContent.setVisibility(View.GONE);
//                    mTvJuQing.setVisibility(View.GONE);
//                    mIvGongXI.setVisibility(View.GONE);
//                    mRleSelect.setVisibility(View.GONE);
//                } else if (type == 2 && mIvGongXI.getVisibility() == View.VISIBLE) {
//                   
//                }
                mRlRoleJuQing.setVisibility(View.GONE);
                mTvContent.setVisibility(View.GONE);
                mTvJuQing.setVisibility(View.GONE);
                mIvGongXI.setVisibility(View.GONE);
                mRleSelect.setVisibility(View.GONE);
            }
        });
        mTvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int type = mRubbishEntity.getType();
                switch (type) {
                    case 1:
                        mRlRoleJuQing.setVisibility(View.GONE);
                        mTvJuQing.setVisibility(View.GONE);
                        mTvContent.setVisibility(View.GONE);
                        mIvGongXI.setVisibility(View.GONE);
                        mRleSelect.setVisibility(View.GONE);
                        break;
                    case 2:
                        mRlRoleJuQing.setVisibility(View.GONE);
                        mIvGongXI.setVisibility(View.GONE);
                        mTvContent.setVisibility(View.GONE);
                        mTvJuQing.setVisibility(View.GONE);
                        mRleSelect.setVisibility(View.GONE);
//                        mPresenter.loadHouseSave(new RubblishBody(PreferenceUtils.getUUid(), "", mRubbishEntity.getId()));
                        Intent i6 = new Intent(HouseActivity.this, StorageActivity.class);
                        startActivity(i6);
                        break;
                    case 3:

                        break;
                }
            }
        });
        mTvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int type = mRubbishEntity.getType();
                switch (type) {
                    case 1:
                        mRlRoleJuQing.setVisibility(View.GONE);
                        mIvGongXI.setVisibility(View.GONE);
                        mTvJuQing.setVisibility(View.GONE);
                        mTvContent.setVisibility(View.GONE);
                        mRleSelect.setVisibility(View.GONE);
                        Intent i = new Intent(HouseActivity.this, MapEventNewActivity.class);
                        i.putExtra("id", mRubbishEntity.getScriptId());
                        i.putExtra("type", true);
                        startActivity(i);
                        break;
                    case 3:

                        break;
                    case 2:
                    case 4:
                        showToast("功能未开放~");
//                        mRlRoleJuQing.setVisibility(View.GONE);
//                        mIvGongXI.setVisibility(View.GONE);
//                        mTvContent.setVisibility(View.GONE);
//                        mTvJuQing.setVisibility(View.GONE);
//                        mRleSelect.setVisibility(View.GONE);
//                        mIvCover.setImageResource(R.drawable.bg_garbage_background_1);
                        break;
                }
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(HouseActivity.this, code, msg);
        finish();
    }

    @Override
    public void onLoadHouseObjects(ArrayList<MapEntity> entities) {
        for (MapEntity entity : entities) {
            if (entity.getType() == 2) {
                entity.setShows("1,2,3,4,5,6");
            } else if (entity.getType() == 3) {
                entity.setShows("1,2,3,4,5,6");
            }
        }
        final ArrayList<HouseDbEntity> res = new ArrayList<>();
        final ArrayList<HouseDbEntity> errorList = new ArrayList<>();
        MapUtil.checkAndDownloadHouse(this, true, HouseDbEntity.toDb(entities, "house"), "house", new Observer<HouseDbEntity>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                initDisposable = d;
            }

            @Override
            public void onNext(@NonNull HouseDbEntity mapDbEntity) {
                if (!mapDbEntity.getType().equals("3")) {
                    File file = new File(StorageUtils.getHouseRootPath() + mapDbEntity.getFileName());
                    String md5 = mapDbEntity.getMd5();
                    if (md5.length() < 32) {
                        int n = 32 - md5.length();
                        for (int i = 0; i < n; i++) {
                            md5 = "0" + md5;
                        }
                    }
                    if (mapDbEntity.getDownloadState() == 3 || !md5.equals(StringUtils.getFileMD5(file))) {
                        FileUtil.deleteFile(StorageUtils.getHouseRootPath() + mapDbEntity.getFileName());
                        errorList.add(mapDbEntity);
                    } else {
                        res.add(mapDbEntity);
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                GreenDaoManager.getInstance().getSession().getHouseDbEntityDao().insertOrReplaceInTx(res);
                mPresenter.addMapMark(HouseActivity.this, mContainer, binding.map, "house");
                if (errorList.size() > 0) {
                    resolvErrorList(errorList, "house");
                }
            }
        });
    }

    /**
     * 采集好感度值
     */
    @Override
    public void onLoadRoleLikeCollect(HouseLikeEntity entity) {
        binding.map.setTimerLikeView(entity);
        mRlRoleRoot.setVisibility(View.VISIBLE);
        mTvRoleNum.setText("好感度+" + entity.getRoleLike());
        mTvRoleName.setText(entity.getRoleName());
    }

    /**
     * 访客总数
     *
     * @param entities
     */
    @Override
    public void getVisitorsInfoSuccess(ArrayList<VisitorsEntity> entities) {
        binding.llLikeUserRoot.removeAllViews();
        if (entities != null && entities.size() > 0) {
            binding.visitorInfo.setVisibility(View.VISIBLE);
            int trueSize = (int) getResources().getDimension(R.dimen.y48);
            int imgSize = (int) getResources().getDimension(R.dimen.y44);
            int startMargin = (int) -getResources().getDimension(R.dimen.y10);
            int showSize = 4;
            if (entities.size() < showSize) {
                showSize = entities.size();
            }
            if (showSize == 4) {
                ImageView iv = new ImageView(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(trueSize, trueSize);
                lp.leftMargin = startMargin;
                iv.setLayoutParams(lp);
                iv.setImageResource(R.drawable.btn_feed_like_more);
                binding.llLikeUserRoot.addView(iv);
            }
//            binding.visitorCount.setText(entities.get(0).getCount());
            for (int i = showSize - 1; i >= 0; i--) {
                final VisitorsEntity userEntity = entities.get(i);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(trueSize, trueSize);
                if (i != 0) {
                    lp.leftMargin = startMargin;
                }
                View likeUser = LayoutInflater.from(this).inflate(R.layout.item_white_border_img, null);
                likeUser.setLayoutParams(lp);
                ImageView img = likeUser.findViewById(R.id.iv_img);
                Glide.with(this)
                        .load(StringUtils.getUrl(this, userEntity.getVisitorImage(), imgSize, imgSize, false, true))
                        .error(R.drawable.bg_default_circle)
                        .placeholder(R.drawable.bg_default_circle)
                        .bitmapTransform(new CropCircleTransformation(this))
                        .into(img);
//                img.setOnClickListener(new NoDoubleClickListener() {
//                    @Override
//                    public void onNoDoubleClick(View v) {
//                        ViewUtils.toPersonal(HouseActivity.this, userEntity.getVisitorId());
//                    }
//                });
                binding.llLikeUserRoot.addView(likeUser);
            }
            count = entities.get(0).getCount();
            binding.visitorCount.setText(entities.get(0).getCount() + "");
            binding.tvHouseName.setText(PreferenceUtils.getAuthorInfo().getUserName() + "的宅屋");
            binding.tvHouseVivit.setText("访客数量:" + count);
        } else {
            binding.visitorInfo.setVisibility(View.GONE);
        }

    }

    /**
     * 捡垃圾
     */
    @Override
    public void saveVisitorSuccess() {
        if (type == 3) {

        } else {
        }
    }

    /**
     * 捡垃圾获取奖池
     *
     * @param entity
     */
    @Override
    public void onLoadHouseRubblish(final RubbishEntity entity) {
        mRlRoleJuQing.setVisibility(View.GONE);
        mTvContent.setVisibility(View.GONE);
        mTvJuQing.setVisibility(View.GONE);
        mIvGongXI.setVisibility(View.GONE);
        mRleSelect.setVisibility(View.GONE);
        mTvContent.setText("");
        mTvCnanle.setVisibility(View.GONE);
        mTvCnanle.setText("");
        mTvChuWu.setText("");
        mTvChuWu.setVisibility(View.GONE);
        mTvUserName.setVisibility(View.GONE);
        mTvUserName.setText("");
        mRubbishEntity = entity;
        if (entity != null && entity.getType() != 0) {
            mRlRoleJuQing.setVisibility(View.VISIBLE);
            mTvLeft.setVisibility(View.VISIBLE);
            mIvGongXI.setVisibility(View.VISIBLE);
            int type = entity.getType();
            if (type == 1) {//剧情
                int w = getResources().getDimensionPixelSize(R.dimen.x456);
                int h = getResources().getDimensionPixelSize(R.dimen.y608);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIvCover.getLayoutParams();
                layoutParams.width = w;
                layoutParams.height = h;
                mIvCover.setLayoutParams(layoutParams);
                Glide.with(HouseActivity.this)
                        .load(ApiService.URL_QINIU + entity.getImage())
                        .error(R.drawable.shape_transparent_background)
                        .placeholder(R.drawable.shape_transparent_background)
                        .into(mIvCover);
                mRleSelect.setVisibility(View.VISIBLE);
                mTvJuQing.setText(entity.getName());
                mTvJuQing.setVisibility(View.VISIBLE);
                mTvLeft.setVisibility(View.GONE);
                mTvRight.setText("观看剧情");
                mTvChuWu.setVisibility(View.VISIBLE);
                mTvCnanle.setVisibility(View.VISIBLE);
                mTvChuWu.setText("(已放入储物箱)");
                mTvCnanle.setText("点击任意区域关闭");
            } else if (type == 2) {
                int w = getResources().getDimensionPixelSize(R.dimen.x360);
                int h = getResources().getDimensionPixelSize(R.dimen.y360);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIvCover.getLayoutParams();
                layoutParams.width = w;
                layoutParams.height = h;
                mIvCover.setLayoutParams(layoutParams);
                Glide.with(HouseActivity.this)
                        .load(ApiService.URL_QINIU + entity.getImage())
                        .error(R.drawable.shape_transparent_background)
                        .placeholder(R.drawable.shape_transparent_background)
                        .into(mIvCover);
                mRleSelect.setVisibility(View.VISIBLE);
                mTvJuQing.setText(entity.getName());
                mTvJuQing.setVisibility(View.VISIBLE);
                mTvChuWu.setVisibility(View.VISIBLE);
                mTvCnanle.setVisibility(View.VISIBLE);
                mTvChuWu.setText("(已放入储物箱)");
                mTvCnanle.setText("点击任意区域关闭");
                mTvRight.setText("立即使用");
                mTvLeft.setVisibility(View.VISIBLE);
                mTvLeft.setText("查看储物箱");
                mPresenter.loadHouseSave(new RubblishBody(PreferenceUtils.getUUid(), "", mRubbishEntity.getId()));
            } else if (type == 3) {
                mIvCover.setImageResource(R.drawable.bg_garbage_background_3);
                mRleSelect.setVisibility(View.GONE);
                mTvJuQing.setText("(点击任意区域关闭)");
                mTvContent.setText(entity.getName());
                mTvContent.setVisibility(View.VISIBLE);
                mTvJuQing.setVisibility(View.VISIBLE);
                mTvUserName.setVisibility(View.VISIBLE);
                mTvUserName.setText(entity.getName());
            } else if (type == 4) {
                int w = getResources().getDimensionPixelSize(R.dimen.x360);
                int h = getResources().getDimensionPixelSize(R.dimen.y360);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mIvCover.getLayoutParams();
                layoutParams.width = w;
                layoutParams.height = h;
                mIvCover.setLayoutParams(layoutParams);
                Glide.with(HouseActivity.this)
                        .load(ApiService.URL_QINIU + entity.getImage())
                        .error(R.drawable.shape_transparent_background)
                        .placeholder(R.drawable.shape_transparent_background)
                        .into(mIvCover);
                mRleSelect.setVisibility(View.GONE);
                mTvJuQing.setText(entity.getName());
                mTvJuQing.setVisibility(View.VISIBLE);
                mPresenter.loadHouseSave(new RubblishBody(PreferenceUtils.getUUid(), "", mRubbishEntity.getId()));
                mTvChuWu.setVisibility(View.VISIBLE);
                mTvCnanle.setVisibility(View.GONE);
                mTvChuWu.setText("(已放入储物箱)");
            }

        }
    }

    /**
     * 保存
     */
    @Override
    public void onLoadHouseSave() {

    }

    private void resolvErrorList(ArrayList<HouseDbEntity> errorList, final String type) {
        final ArrayList<HouseDbEntity> errorListTmp = new ArrayList<>();
        final ArrayList<HouseDbEntity> res = new ArrayList<>();
        MapUtil.checkAndDownloadHouse(this, false, errorList, type, new Observer<HouseDbEntity>() {

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                resolvDisposable = d;
            }

            @Override
            public void onNext(@NonNull HouseDbEntity mapDbEntity) {
                File file = new File(StorageUtils.getHouseRootPath() + mapDbEntity.getFileName());
                String md5 = mapDbEntity.getMd5();
                if (md5.length() < 32) {
                    int n = 32 - md5.length();
                    for (int i = 0; i < n; i++) {
                        md5 = "0" + md5;
                    }
                }
                if (!md5.equals(StringUtils.getFileMD5(file)) || mapDbEntity.getDownloadState() == 3) {
                    FileUtil.deleteFile(StorageUtils.getHouseRootPath() + mapDbEntity.getFileName());
                    errorListTmp.add(mapDbEntity);
                } else {
                    res.add(mapDbEntity);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {
                GreenDaoManager.getInstance().getSession().getHouseDbEntityDao().insertOrReplaceInTx(res);
                if (errorListTmp.size() > 0) {
                    resolvErrorList(errorListTmp, type);
                } else {
                    if ("house".equals(type)) {
                        mPresenter.addMapMark(HouseActivity.this, mContainer, binding.map, "house");
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (initDisposable != null && !initDisposable.isDisposed()) initDisposable.dispose();
        if (resolvDisposable != null && !resolvDisposable.isDisposed()) resolvDisposable.dispose();
        binding.map.clearAllView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        MoeMoeApplication.getInstance().GoneWindowMager(this);
        MoeMoeApplication.getInstance().GoneDiaLog();
        MoeMoeApplication.getInstance().GoneMenu();
        super.onPause();
    }

    private void imgIn() {
        ObjectAnimator phoneAnimator = ObjectAnimator.ofFloat(binding.llToolBar, "translationY", -binding.llToolBar.getHeight() - getResources().getDimension(R.dimen.y60), 0).setDuration(300);
        phoneAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mRoleAnimator = ObjectAnimator.ofFloat(binding.dormitoryRole, "translationY", binding.dormitoryRole.getHeight() - getResources().getDimension(R.dimen.y60), 0).setDuration(300);
        mRoleAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mStorageAnimator = ObjectAnimator.ofFloat(binding.dormitoryStorage, "translationY", binding.dormitoryStorage.getHeight() - getResources().getDimension(R.dimen.y60), 0).setDuration(300);
        mStorageAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mDramaAnimator = ObjectAnimator.ofFloat(binding.dormitoryDrama, "translationY", binding.dormitoryDrama.getHeight() - getResources().getDimension(R.dimen.y60), 0).setDuration(300);
        mDramaAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mVisitorInfoAnimator = ObjectAnimator.ofFloat(binding.visitorInfo, "translationX", -binding.visitorInfo.getWidth(), 0).setDuration(300);
        mVisitorInfoAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mIvSleepAnimator = ObjectAnimator.ofFloat(binding.ivSleep, "translationX", binding.ivSleep.getWidth() + getResources().getDimension(R.dimen.y60), 0).setDuration(300);
        mIvSleepAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mIvTrandsAnimator = ObjectAnimator.ofFloat(binding.ivTrends, "translationX", binding.ivTrends.getWidth() + getResources().getDimension(R.dimen.y60), 0).setDuration(300);
        mIvTrandsAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mIvAlarmsAnimator = ObjectAnimator.ofFloat(binding.ivAlarm, "translationX", binding.ivAlarm.getWidth() + getResources().getDimension(R.dimen.y60), 0).setDuration(300);
        mIvAlarmsAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mIvMesssgeAnimator = ObjectAnimator.ofFloat(binding.ivMessage, "translationX", binding.ivMessage.getWidth() + getResources().getDimension(R.dimen.y60), 0).setDuration(300);
        mIvMesssgeAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mIvShareAnimator = ObjectAnimator.ofFloat(binding.ivShare, "translationX", -binding.ivShare.getWidth(), 0).setDuration(300);
        mIvMesssgeAnimator.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(phoneAnimator).with(mRoleAnimator);
        set.play(mStorageAnimator).with(mDramaAnimator);
        set.play(mVisitorInfoAnimator).with(mIvSleepAnimator);
        set.play(mIvTrandsAnimator).with(mIvAlarmsAnimator);
        set.play(mIvMesssgeAnimator).with(mIvShareAnimator);
        set.start();
    }

    private void imgOut() {
        ObjectAnimator phoneAnimator = ObjectAnimator.ofFloat(binding.llToolBar, "translationY", 0, -getResources().getDimension(R.dimen.y60) - binding.llToolBar.getHeight()).setDuration(300);
        phoneAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mStorageAnimator = ObjectAnimator.ofFloat(binding.dormitoryRole, "translationY", 0, -getResources().getDimension(R.dimen.y60) - -binding.dormitoryRole.getHeight() * 2).setDuration(300);
        mStorageAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mRoleAnimator = ObjectAnimator.ofFloat(binding.dormitoryStorage, "translationY", 0, -getResources().getDimension(R.dimen.y60) - -binding.dormitoryStorage.getHeight() * 2).setDuration(300);
        mRoleAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mDramaAnimator = ObjectAnimator.ofFloat(binding.dormitoryDrama, "translationY", 0, -getResources().getDimension(R.dimen.y60) - -binding.dormitoryDrama.getHeight() * 2).setDuration(300);
        mDramaAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mVisitorInfoAnimator = ObjectAnimator.ofFloat(binding.visitorInfo, "translationX", 0, -binding.visitorInfo.getWidth()).setDuration(300);
        mVisitorInfoAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mIvSleepAnimator = ObjectAnimator.ofFloat(binding.ivSleep, "translationX", 0, binding.ivSleep.getWidth() + getResources().getDimension(R.dimen.y60)).setDuration(300);
        mIvSleepAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mIvTrandsAnimator = ObjectAnimator.ofFloat(binding.ivTrends, "translationX", 0, binding.ivTrends.getWidth() + getResources().getDimension(R.dimen.y60)).setDuration(300);
        mIvTrandsAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mIvAlarmsAnimator = ObjectAnimator.ofFloat(binding.ivAlarm, "translationX", 0, binding.ivAlarm.getWidth() + getResources().getDimension(R.dimen.y60)).setDuration(300);
        mIvAlarmsAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mIvMesssgeAnimator = ObjectAnimator.ofFloat(binding.ivMessage, "translationX", 0, binding.ivMessage.getWidth() + getResources().getDimension(R.dimen.y60)).setDuration(300);
        mIvMesssgeAnimator.setInterpolator(new OvershootInterpolator());
        ObjectAnimator mIvShareAnimator = ObjectAnimator.ofFloat(binding.ivShare, "translationX", 0, -getResources().getDimension(R.dimen.y60) - binding.dormitoryDrama.getWidth()).setDuration(300);
        mIvMesssgeAnimator.setInterpolator(new OvershootInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.play(phoneAnimator).with(mRoleAnimator);
        set.play(mStorageAnimator).with(mDramaAnimator);
        set.play(mVisitorInfoAnimator).with(mIvSleepAnimator);
        set.play(mIvTrandsAnimator).with(mIvAlarmsAnimator);
        set.play(mIvMesssgeAnimator).with(mIvShareAnimator);
        set.start();
    }

    public class Presenter {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_left:
                    Intent i3 = new Intent(HouseActivity.this, FeedV3Activity.class);
                    startActivity(i3);
                    finish();
                    break;
                case R.id.iv_right:
                    finish();
                    break;
                case R.id.iv_personal:
                    if (NetworkUtils.checkNetworkAndShowError(HouseActivity.this) && DialogUtils.checkLoginAndShowDlg(HouseActivity.this)) {
                        //埋点统计：手机个人中心
                        clickEvent("个人中心");
                        Intent i1 = new Intent(HouseActivity.this, PersonalV2Activity.class);
                        i1.putExtra(UUID, PreferenceUtils.getUUid());
                        startActivity(i1);
                    }
                    break;
                case R.id.tv_search:
                    //埋点统计：手机个人中心
                    clickEvent("宅屋-搜索");
                    Intent intent = new Intent(HouseActivity.this, AllSearchActivity.class);
                    intent.putExtra("type", "all");
                    startActivity(intent);
                    break;
                case R.id.visitor_info:
                    Intent i7 = new Intent(HouseActivity.this, NewVisitorActivity.class);
//                    Intent i7 = new Intent(HouseActivity.this, VisitorsActivity.class);
                    startActivity(i7);
                    break;
                case R.id.dormitory_storage:
                    Intent i6 = new Intent(HouseActivity.this, StorageActivity.class);
                    startActivity(i6);
                    break;
                case R.id.dormitory_role:
                    Intent i4 = new Intent(HouseActivity.this, RoleActivity.class);
                    startActivity(i4);
                    break;
                case R.id.dormitory_drama:
                    Intent i8 = new Intent(HouseActivity.this, DormitoryDramaActivity.class);
                    startActivity(i8);
                    break;
                case R.id.iv_sleep:
                    Intent i9 = new Intent(HouseActivity.this, Live3dActivity.class);
                    startActivity(i9);
//                    binding.tvHouseName.setText(PreferenceUtils.getAuthorInfo().getUserName() + "的宅屋");
//                    binding.tvHouseVivit.setText("访客数量:" + count);
//                    binding.ivHouseQrCode.setVisibility(View.VISIBLE);
//                    binding.tvHouseName.setVisibility(View.VISIBLE);
//                    binding.tvHouseVivit.setVisibility(View.VISIBLE);
//                    Bitmap bitmap = getBitmap(binding.flShare);
////                    Bitmap viewBitmap = getViewBitmap(R.layout.activity_house);
//                    binding.rlCoverInfo.setVisibility(View.VISIBLE);
//                    binding.ivCoverNextLie.setImageBitmap(bitmap);
                    break;
                case R.id.iv_alarm://同桌电话
                    Intent intent1 = new Intent(HouseActivity.this, AlarmActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.iv_trends://动态
                    Intent i5 = new Intent(HouseActivity.this, NewDynamicActivity.class);
                    startActivity(i5);
                    break;
                case R.id.iv_message://留言板
                    Intent i1 = new Intent(HouseActivity.this, CommentsListActivity.class);
                    i1.putExtra("uuid", PreferenceUtils.getUUid());
                    startActivity(i1);
                    break;
                case R.id.iv_share:
                    if (bottomMenuFragment != null) {
                        createDialog("生成图片中...");
                        binding.ivHouseQrCode.setVisibility(View.VISIBLE);
                        binding.tvHouseName.setVisibility(View.VISIBLE);
                        binding.tvHouseVivit.setVisibility(View.VISIBLE);
                        bitmap = getBitmap(binding.flShare);
                        saveBitmap = StorageUtils.saveBitmap(HouseActivity.this, bitmap);
                        if (!TextUtils.isEmpty(saveBitmap) && bottomMenuFragment != null) {
                            finalizeDialog();
                            binding.ivHouseQrCode.setVisibility(View.INVISIBLE);
                            binding.tvHouseName.setVisibility(View.INVISIBLE);
                            binding.tvHouseVivit.setVisibility(View.INVISIBLE);
                            bottomMenuFragment.show(getSupportFragmentManager(), "house");
                        } else {
                            finalizeDialog();
                            binding.ivHouseQrCode.setVisibility(View.INVISIBLE);
                            binding.tvHouseName.setVisibility(View.INVISIBLE);
                            binding.tvHouseVivit.setVisibility(View.INVISIBLE);
                            showToast("图片生成失败，请重新生成~");
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 把一个view转化成bitmap对象
     */

    public Bitmap getViewBitmap(int layoutId) {

        View view = getLayoutInflater().inflate(layoutId, null);

        int me = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        view.measure(me, me);

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache();

        Bitmap bitmap = view.getDrawingCache();


        return bitmap;

    }

    public Bitmap getBitmap(View view) {


        Bitmap bitmap = null;
        int width = view.getRight() - view.getLeft();
        int height = view.getBottom() - view.getTop();
        final boolean opaque = view.getDrawingCacheBackgroundColor() != 0 || view.isOpaque();
        Bitmap.Config quality;
        if (!opaque) {
            switch (view.getDrawingCacheQuality()) {
                case DRAWING_CACHE_QUALITY_AUTO:
                case DRAWING_CACHE_QUALITY_LOW:
                case DRAWING_CACHE_QUALITY_HIGH:
                default:
                    quality = Bitmap.Config.ARGB_8888;
                    break;
            }
        } else {
            quality = Bitmap.Config.RGB_565;
        }
        bitmap = Bitmap.createBitmap(getResources().getDisplayMetrics(),
                width, height, quality);
        bitmap.setDensity(getResources().getDisplayMetrics().densityDpi);
        if (opaque) bitmap.setHasAlpha(false);
        boolean clear = view.getDrawingCacheBackgroundColor() != 0;
        Canvas canvas = new Canvas(bitmap);
        if (clear) {
            bitmap.eraseColor(view.getDrawingCacheBackgroundColor());
        }
        view.computeScroll();
        final int restoreCount = canvas.save();
        canvas.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(canvas);
        canvas.restoreToCount(restoreCount);
        canvas.setBitmap(null);

        return bitmap;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void houseLikeEvent(HouseLikeEvent event) {
        if (event != null) {
            String roleId = event.getRoleId();
            int type = event.getType();
            if (type == 3) {
                mPresenter.saveVisitor(new SaveVisitorEntity("", PreferenceUtils.getUUid(), 3));
                mPresenter.loadHouseRubblish(new RubblishBody(PreferenceUtils.getUUid(), roleId, ""));
            } else {
                this.type = 2;
                mPresenter.loadRoleLikeCollect(roleId);
            }
        }
    }


    /**
     * 分享
     */
    private void initPopupMenus() {
        bottomMenuFragment = new BottomMenuFragment();
        ArrayList<com.moemoe.lalala.view.widget.netamenu.MenuItem> items = new ArrayList<>();

        MenuItem item = new MenuItem(1, "QQ", R.drawable.btn_doc_option_send_qq);
        items.add(item);

        item = new MenuItem(2, "QQ空间", R.drawable.btn_doc_option_send_qzone);
        items.add(item);

        item = new MenuItem(3, "微信", R.drawable.btn_doc_option_send_wechat);
        items.add(item);

        item = new MenuItem(4, "微信朋友圈", R.drawable.btn_doc_option_send_pengyouquan);
        items.add(item);

        item = new MenuItem(5, "微博", R.drawable.btn_doc_option_send_weibo);
        items.add(item);

        bottomMenuFragment.setShowTop(false);
        bottomMenuFragment.setMenuItems(items);
        bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_HORIZONTAL);
        bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(final int itemId) {
                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                // text是分享文本，所有平台都需要这个字段
                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数

                switch (itemId) {
                    case 1:
                        ShareUtils.shareQQBimtp(HouseActivity.this, "", "", "", saveBitmap, platformActionListener);
                        break;
                    case 2:
                        ShareUtils.shareQQzoneBitmap(HouseActivity.this, "", "", "", saveBitmap, platformActionListener);
                        break;
                    case 3:
                        ShareUtils.shareWechatBitmap(HouseActivity.this, "", "", "", bitmap, platformActionListener);
                        break;
                    case 4:
                        ShareUtils.sharepyqBitmap(HouseActivity.this, "", "", "", bitmap, platformActionListener);
                        break;
                    case 5:
                        ShareUtils.shareWeiboBitMap(HouseActivity.this, "", "", "", bitmap, platformActionListener);
                        break;

                }

            }
        });
    }

    /**
     * 分享回调
     */
    PlatformActionListener platformActionListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            Log.e("kid", "分享成功");
            if (bottomMenuFragment != null) {
                bottomMenuFragment.dismiss();
            }
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Log.e("kid", "分享失败");
            if (bottomMenuFragment != null) {
                bottomMenuFragment.dismiss();
            }
        }

        @Override
        public void onCancel(Platform platform, int i) {
            Log.e("kid", "分享取消");
            if (bottomMenuFragment != null) {
                bottomMenuFragment.dismiss();
            }
        }
    };
}
