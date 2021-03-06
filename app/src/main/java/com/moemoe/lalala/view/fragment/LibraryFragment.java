package com.moemoe.lalala.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerLibraryComponent;
import com.moemoe.lalala.di.modules.LibraryModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BannerEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.presenter.LibraryContract;
import com.moemoe.lalala.presenter.LibraryPresenter;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.FolderDecoration;
import com.moemoe.lalala.utils.FolderDecorationNew;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StoryListDecoration;
import com.moemoe.lalala.view.activity.LibraryActivity;
import com.moemoe.lalala.view.activity.NewFileCommonActivity;
import com.moemoe.lalala.view.activity.NewFileManHua2Activity;
import com.moemoe.lalala.view.activity.NewFileXiaoShuo2Activity;
import com.moemoe.lalala.view.activity.NewFileXiaoshuoActivity;
import com.moemoe.lalala.view.adapter.LibraryBagAdapter;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

/**
 * Created by Hygge on 2018/7/18.
 */

public class LibraryFragment extends BaseFragment implements LibraryContract.View {

    @BindView(R.id.list)
    PullAndLoadView mListDocs;

    private boolean isLoading;


    @Inject
    LibraryPresenter mPresenter;
    private String mFolderType;
    private LibraryBagAdapter mAdapter;


    public static LibraryFragment newInstance(String mFolderType) {
        LibraryFragment libraryFragment = new LibraryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", mFolderType);
        libraryFragment.setArguments(bundle);
        return libraryFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_library;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        DaggerLibraryComponent.builder()
                .libraryModule(new LibraryModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mFolderType = getArguments().getString("type");
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        if (mFolderType.equals("历史记录")) {
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
            mListDocs.setLayoutManager(manager);
        } else {
            GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
            mListDocs.setLayoutManager(manager);
            mListDocs.getRecyclerView().addItemDecoration(new FolderDecorationNew());
        }
        mAdapter = new LibraryBagAdapter(mFolderType);
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.setLoadMoreEnabled(false);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                mPresenter.loadLibraryBagList(mFolderType, mAdapter.getItemCount());
            }

            @Override
            public void onRefresh() {
                isLoading = true;
                mPresenter.loadLibraryBagList(mFolderType, 0);
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
        mAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                ShowFolderEntity item = mAdapter.getItem(position);
                if (item.getType().equals(FolderType.MH.toString())) {
                    NewFileManHua2Activity.startActivity(getContext(),
                            mAdapter.getItem(position).getFolderId(), mAdapter.getItem(position).getLevel2FolderId(), mAdapter.getItem(position).getCreateUser(), position, item.getFolderName())
                    ;
                } else if (item.getType().equals(FolderType.TJ.toString())) {
                    NewFileCommonActivity.startActivity(getContext(), FolderType.TJ.toString(), mAdapter.getItem(position).getFolderId(), mAdapter.getItem(position).getCreateUser());
                } else if (item.getType().equals(FolderType.XS.toString())) {
                    final ShowFolderEntity entity = mAdapter.getItem(position);
                    if (FileUtil.isExists(StorageUtils.getNovRootPath() + entity.getLevel2FolderId() + File.separator + entity.getFolderName())) {
                        NewFileXiaoShuo2Activity.startActivity(getContext(), null, entity.getCreateUser(), position, entity.getLevel2FolderId(), entity.getFolderName());
                    } else {
                        File file = new File(StorageUtils.getNovRootPath() + entity.getLevel2FolderId());
                        if (file.mkdir()) {
                            final ProgressDialog dialog = new ProgressDialog(getContext());
                            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                            dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
                            dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                            dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
                            dialog.setTitle("下载中");
                            FileDownloader.getImpl().create(ApiService.URL_QINIU + entity.getPath())
                                    .setPath(StorageUtils.getNovRootPath() + entity.getLevel2FolderId() + "/" + entity.getFolderName())
                                    .setCallbackProgressTimes(1)
                                    .setListener(new FileDownloadListener() {
                                        @Override
                                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                        }

                                        @Override
                                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                                            dialog.setMax(totalBytes);
                                            dialog.setProgress(soFarBytes);
                                        }

                                        @Override
                                        protected void completed(BaseDownloadTask task) {
                                            dialog.dismiss();
                                            NewFileXiaoShuo2Activity.startActivity(getContext(), null, entity.getCreateUser(), position, entity.getLevel2FolderId(), entity.getFolderName());
                                        }

                                        @Override
                                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                        }

                                        @Override
                                        protected void error(BaseDownloadTask task, Throwable e) {
                                            dialog.dismiss();
                                            FileUtil.deleteDir(StorageUtils.getNovRootPath() + entity.getLevel2FolderId());
                                            ((LibraryActivity) getContext()).showToast("下载失败");
                                        }

                                        @Override
                                        protected void warn(BaseDownloadTask task) {

                                        }
                                    }).start();
                            dialog.show();
                        }
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        isLoading = true;
        mPresenter.loadLibraryBagList(mFolderType, 0);
    }

    @Override
    public void release() {
        if (mPresenter != null) {
            mPresenter.release();
        }
    }

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(getContext(), code, msg);
        isLoading = false;
        mListDocs.setComplete();
    }

    @Override
    public void onBannerLoadSuccess(ArrayList<BannerEntity> bannerEntities) {

    }

    @Override
    public void onLoadLibraryListSuccess(ArrayList<ShowFolderEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if (isPull) {
            mAdapter.setList(entities);
        } else {
            mAdapter.addList(entities);
        }
        if (entities.size() >= ApiService.LENGHT1) {
            mListDocs.setLoadMoreEnabled(true);
        } else {
            mListDocs.setLoadMoreEnabled(false);
        }
    }

    @Override
    public void onLoadLibraryNewestReadhistorySuccess(ArrayList<ShowFolderEntity> entities, boolean isPull) {
        isLoading = false;
        mListDocs.setComplete();
        if (isPull) {
            mAdapter.setList(entities);
        } else {
            mAdapter.addList(entities);
        }
        if (entities.size() >= ApiService.LENGHT) {
            mListDocs.setLoadMoreEnabled(true);
        } else {
            mListDocs.setLoadMoreEnabled(false);
        }
    }
}
