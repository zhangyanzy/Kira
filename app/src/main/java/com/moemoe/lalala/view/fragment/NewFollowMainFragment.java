package com.moemoe.lalala.view.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerFeedComponent;
import com.moemoe.lalala.di.modules.FeedModule;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.Comment24Entity;
import com.moemoe.lalala.model.entity.DepartmentEntity;
import com.moemoe.lalala.model.entity.DepartmentGroupEntity;
import com.moemoe.lalala.model.entity.DiscoverEntity;
import com.moemoe.lalala.model.entity.DocResponse;
import com.moemoe.lalala.model.entity.Image;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.model.entity.SimpleUserEntity;
import com.moemoe.lalala.model.entity.XianChongEntity;
import com.moemoe.lalala.presenter.FeedContract;
import com.moemoe.lalala.presenter.FeedPresenter;
import com.moemoe.lalala.utils.BannerImageLoader;
import com.moemoe.lalala.utils.IntentUtils;
import com.moemoe.lalala.utils.MenuVItemDecoration;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.activity.CreateDynamicActivity;
import com.moemoe.lalala.view.activity.DynamicActivity;
import com.moemoe.lalala.view.activity.NewDynamicActivity;
import com.moemoe.lalala.view.adapter.DiscoverHolder;
import com.moemoe.lalala.view.adapter.FeedAdapter;
import com.moemoe.lalala.view.adapter.FeedHolder;
import com.moemoe.lalala.view.adapter.XianChongListAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by yi on 2017/9/4.
 */

public class NewFollowMainFragment extends BaseFragment implements FeedContract.View {

    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_to_wen)
    ImageView mIvCreateDynamic;
    @BindView(R.id.iv_trends)
    ImageView mTrends;

    @Inject
    FeedPresenter mPresenter;

    private Banner banner;
    private View bannerView;
    private View xianChongView;
    private View hotView;
    private FeedAdapter mAdapter;
    private long minIdx;
    private long maxIdx;
    private boolean isLoading = false;
    private String type;
    private String uuidType;
    private boolean mIsSelf;

    public static NewFollowMainFragment newInstance(String type) {
        NewFollowMainFragment fragment = new NewFollowMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static NewFollowMainFragment newInstance(String type, String userId) {
        NewFollowMainFragment fragment = new NewFollowMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString("id", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static NewFollowMainFragment newInstance(String type, String userId, String uuidType) {
        NewFollowMainFragment fragment = new NewFollowMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString("id", userId);
        bundle.putString("uuidType", uuidType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_onepull;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerFeedComponent.builder()
                .feedModule(new FeedModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        type = getArguments().getString("type");
        final String userId = getArguments().getString("id");
        uuidType = getArguments().getString("uuidType");
        mIsSelf = userId.equals(PreferenceUtils.getUUid());
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mListDocs.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.bg_f6f6f6));
        mListDocs.setLoadMoreEnabled(true);
        mListDocs.setLayoutManager(new LinearLayoutManager(getContext()));
        mListDocs.getRecyclerView().addItemDecoration(new MenuVItemDecoration((int) getResources().getDimension(R.dimen.y24)));
        mAdapter = new FeedAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DynamicActivity.startActivity(getContext(), mAdapter.getItem(position));
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                if (mAdapter.getList().size() == 0) {
                    mPresenter.loadList(0, type, userId);
                } else {
                    mPresenter.loadList(mAdapter.getItem(mAdapter.getList().size() - 1).getTimestamp(), type, userId);
                }
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadDiscoverList(type, minIdx, maxIdx, true);
                mPresenter.loadList(0, type, userId);
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        if ("ground".equals(type)) {
            mPresenter.requestBannerData("CLASSROOM");
            mPresenter.loadXianChongList();
            mIvCreateDynamic.setVisibility(View.VISIBLE);
            mIvCreateDynamic.setImageResource(R.drawable.btn_feed_create_dynamic);
            mIvCreateDynamic.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i4 = new Intent(getContext(), CreateDynamicActivity.class);
                    i4.putExtra("default_tag", "广场");
                    startActivity(i4);
                }
            });
        }
        if (!TextUtils.isEmpty(uuidType) && uuidType.equals("house")) {
            mIvCreateDynamic.setVisibility(View.VISIBLE);
            mIvCreateDynamic.setImageResource(R.drawable.btn_feed_create_dynamic);
            mIvCreateDynamic.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    Intent i4 = new Intent(getContext(), CreateDynamicActivity.class);
                    i4.putExtra("default_tag", "广场");
                    startActivity(i4);
                }
            });
        }

        //TODO  DFASF
        if (mIsSelf) {
            mTrends.setVisibility(View.VISIBLE);
            mTrends.setImageResource(R.drawable.btn_feed_create_dynamic);
            mTrends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                Intent intent = new Intent(getContext(), NewDynamicActivity.class);
//                startActivity(intent);
                    Intent i4 = new Intent(getContext(), CreateDynamicActivity.class);
                    i4.putExtra("default_tag", "广场");
                    startActivity(i4);
                }
            });
        } else {
            mTrends.setVisibility(View.GONE);
        }


        minIdx = PreferenceUtils.getDiscoverMinIdx(getContext(), type);
        maxIdx = PreferenceUtils.getDiscoverMaxIdx(getContext(), type);
        mPresenter.loadDiscoverList(type, minIdx, maxIdx, true);
        mPresenter.loadList(0, type, userId);
    }

    public void setSmoothScrollToPosition() {
        mListDocs.getRecyclerView().smoothScrollToPosition(0);
    }

    public void likeDynamic(String id, boolean isLie, int position) {
        mPresenter.likeDynamic(id, isLie, position);
    }

    @Override
    public void onFailure(int code, String msg) {
        isLoading = false;
        mListDocs.setComplete();
    }

    public void release() {
        if (mPresenter != null) mPresenter.release();
        super.release();
    }

    @Override
    public void onLoadListSuccess(ArrayList<NewDynamicEntity> resList, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if (isPull) {
            mAdapter.setList(resList);
        } else {
            mAdapter.addList(resList);
        }
    }

    @Override
    public void onBannerLoadSuccess(final ArrayList<BannerEntity> bannerEntities) {
        if (bannerEntities.size() > 0) {
            if (bannerView == null) {
                bannerView = LayoutInflater.from(getContext()).inflate(R.layout.item_new_banner, null);
                banner = bannerView.findViewById(R.id.banner);
                mAdapter.addHeaderView(bannerView, 0);
            }
            banner.setImages(bannerEntities)
                    .setImageLoader(new BannerImageLoader())
                    .setDelayTime(2000)
                    .setIndicatorGravity(BannerConfig.CENTER)
                    .start();
            banner.setOnBannerListener(new OnBannerListener() {
                @Override
                public void OnBannerClick(int position) {
                    BannerEntity bean = bannerEntities.get(position);
                    if (!TextUtils.isEmpty(bean.getSchema())) {
                        Uri uri = Uri.parse(bean.getSchema());
                        IntentUtils.toActivityFromUri(getContext(), uri, null);
                    }
                }
            });
        } else {
            if (bannerView != null) {
                mAdapter.removeHeaderView(bannerView);
                bannerView = null;
            }
        }
    }

    @Override
    public void onLoadXianChongSuccess(ArrayList<XianChongEntity> entities) {
        if (entities.size() > 0) {
            if (xianChongView == null) {
                xianChongView = LayoutInflater.from(getContext()).inflate(R.layout.item_class_featured, null);

                int count = mAdapter.getHeaderViewCount();
                if (count == 0) {
                    mAdapter.addHeaderView(xianChongView);
                } else if (count == 1) {
                    View view = mAdapter.getmHeaderLayout().getChildAt(0);
                    if (view == bannerView) {
                        mAdapter.addHeaderView(xianChongView);
                    } else {
                        mAdapter.addHeaderView(xianChongView, 0);
                    }
                } else if (count == 2) {
                    mAdapter.addHeaderView(xianChongView, 1);
                }

            }
            RecyclerView rvList = xianChongView.findViewById(R.id.rv_class_featured);
            rvList.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.y180)));
            rvList.setBackgroundColor(Color.WHITE);
            final XianChongListAdapter recyclerViewAdapter = new XianChongListAdapter();
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvList.setLayoutManager(layoutManager);
            TextView text = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_text, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_VERTICAL;
            lp.leftMargin = (int) getResources().getDimension(R.dimen.x20);
            lp.rightMargin = (int) getResources().getDimension(R.dimen.x12);
            text.setLayoutParams(lp);
            recyclerViewAdapter.addHeaderView(text, -1, LinearLayout.HORIZONTAL);
            rvList.setAdapter(recyclerViewAdapter);
            recyclerViewAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    XianChongEntity entity = recyclerViewAdapter.getItem(position);
                    ViewUtils.toPersonal(getContext(), entity.getUserId());
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            });
            recyclerViewAdapter.setList(entities);
        } else {
            if (xianChongView != null) {
                mAdapter.removeHeaderView(xianChongView);
                xianChongView = null;
            }
        }
    }

    @Override
    public void onLoadFolderSuccess(ArrayList<ShowFolderEntity> entities) {

    }

    @Override
    public void onLoadCommentSuccess(Comment24Entity entity) {

    }

    @Override
    public void onLikeDynamicSuccess(boolean isLike, int position) {
        if (position == -1) {
            if (mCurDiscover != null) {
                if ("dynamic".equals(mCurDiscover.getType())) {
                    NewDynamicEntity entity = new Gson().fromJson(mCurDiscover.getObj(), NewDynamicEntity.class);
                    entity.setThumb(isLike);
                    if (isLike) {
                        SimpleUserEntity userEntity = new SimpleUserEntity();
                        userEntity.setUserName(PreferenceUtils.getAuthorInfo().getUserName());
                        userEntity.setUserId(PreferenceUtils.getUUid());
                        userEntity.setUserIcon(PreferenceUtils.getAuthorInfo().getHeadPath());
                        entity.getThumbUsers().add(0, userEntity);
                        entity.setThumbs(entity.getThumbs() + 1);
                    } else {
                        for (SimpleUserEntity userEntity : entity.getThumbUsers()) {
                            if (userEntity.getUserId().equals(PreferenceUtils.getUUid())) {
                                entity.getThumbUsers().remove(userEntity);
                                break;
                            }
                        }
                        entity.setThumbs(entity.getThumbs() - 1);
                    }
                    Gson gson = new Gson();
                    JsonObject newObj = gson.toJsonTree(entity).getAsJsonObject();
                    mCurDiscover.setObj(newObj);
                    holder.createItem(entity, -1, mCurDiscover.getFrom());
                }
            }
        } else {
            mAdapter.getList().get(position).setThumb(isLike);
            if (isLike) {
                SimpleUserEntity userEntity = new SimpleUserEntity();
                userEntity.setUserName(PreferenceUtils.getAuthorInfo().getUserName());
                userEntity.setUserId(PreferenceUtils.getUUid());
                userEntity.setUserIcon(PreferenceUtils.getAuthorInfo().getHeadPath());
                mAdapter.getList().get(position).getThumbUsers().add(0, userEntity);
                mAdapter.getList().get(position).setThumbs(mAdapter.getList().get(position).getThumbs() + 1);
            } else {
                for (SimpleUserEntity userEntity : mAdapter.getList().get(position).getThumbUsers()) {
                    if (userEntity.getUserId().equals(PreferenceUtils.getUUid())) {
                        mAdapter.getList().get(position).getThumbUsers().remove(userEntity);
                        break;
                    }
                }
                mAdapter.getList().get(position).setThumbs(mAdapter.getList().get(position).getThumbs() - 1);
            }
            if (mAdapter.getHeaderLayoutCount() != 0) {
                mAdapter.notifyItemChanged(position + 1);
            } else {
                mAdapter.notifyItemChanged(position);
            }
        }
    }

    private DiscoverEntity mCurDiscover;
    private DiscoverHolder holder;

    @Override
    public void onLoadDiscoverListSuccess(ArrayList<DiscoverEntity> entities, boolean isPull) {
        if (entities != null && entities.size() > 0) {
            changeIdx(entities);
            if (hotView == null) {
                hotView = LayoutInflater.from(getContext()).inflate(R.layout.item_new_feed_list, null);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.topMargin = (int) getResources().getDimension(R.dimen.y24);
                hotView.setLayoutParams(lp);
                mAdapter.addHeaderView(hotView);
            }
            holder = new DiscoverHolder(hotView);
            mCurDiscover = entities.get(0);
            if ("dynamic".equals(mCurDiscover.getType())) {
                final NewDynamicEntity entity = new Gson().fromJson(mCurDiscover.getObj(), NewDynamicEntity.class);
                holder.createItem(entity, -1, mCurDiscover.getFrom());
                hotView.setOnClickListener(new NoDoubleClickListener() {
                    @Override
                    public void onNoDoubleClick(View v) {
                        DynamicActivity.startActivity(getContext(), entity);
                    }
                });
            } else {
                if (hotView != null) {
                    mAdapter.removeHeaderView(hotView);
                    hotView = null;
                }
            }
        } else {
            if (hotView != null) {
                mAdapter.removeHeaderView(hotView);
                hotView = null;
            }
        }
    }

    private void changeIdx(ArrayList<DiscoverEntity> entities) {
        if (entities.size() > 0) {
            long min = entities.get(0).getTimestamp();
            long max = entities.get(0).getTimestamp();
            for (DiscoverEntity entity : entities) {
                if (entity.getTimestamp() == 0) {
                    min = 0;
                    max = 0;
                    break;
                } else if (entity.getTimestamp() > max) {
                    max = entity.getTimestamp();
                } else if (entity.getTimestamp() < min) {
                    min = entity.getTimestamp();
                }
            }
            if (min == 0 && max == 0) {
                minIdx = maxIdx = 0;
            } else {
                if (min < minIdx || minIdx == 0) {
                    minIdx = min;
                }
                if (max > maxIdx) {
                    maxIdx = max;
                }
            }
            if ("random".equals(type)) {
                PreferenceUtils.setDiscoverMinIdx(getContext(), type, minIdx);
                PreferenceUtils.setDiscoverMaxIdx(getContext(), type, maxIdx);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
