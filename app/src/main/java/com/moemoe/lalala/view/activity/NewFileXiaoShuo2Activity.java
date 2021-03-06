package com.moemoe.lalala.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.di.components.DaggerNewFileComponent;
import com.moemoe.lalala.di.components.DaggerNewFolderXiaoShuoComponent;
import com.moemoe.lalala.di.modules.NewFileModule;
import com.moemoe.lalala.di.modules.NewFileXiaoShuoModule;
import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.entity.BagLoadReadprogressEntity;
import com.moemoe.lalala.model.entity.BagReadprogressEntity;
import com.moemoe.lalala.model.entity.FileXiaoShuoEntity;
import com.moemoe.lalala.model.entity.FolderType;
import com.moemoe.lalala.presenter.NewFolderItemXiaoShuoContract;
import com.moemoe.lalala.presenter.NewFolderItemXiaoShuoPresenter;
import com.moemoe.lalala.utils.DensityUtil;
import com.moemoe.lalala.utils.ErrorCodeUtils;
import com.moemoe.lalala.utils.FileUtil;
import com.moemoe.lalala.utils.NoDoubleClickListener;
import com.moemoe.lalala.utils.PreferenceUtils;
import com.moemoe.lalala.utils.StorageUtils;
import com.moemoe.lalala.utils.StringUtils;
import com.moemoe.lalala.utils.ViewUtils;
import com.moemoe.lalala.view.adapter.XiaoShuoAdapter;
import com.moemoe.lalala.view.widget.netamenu.BottomMenuFragment;
import com.moemoe.lalala.view.widget.netamenu.MenuItem;
import com.moemoe.lalala.view.widget.recycler.PullAndLoadView;
import com.moemoe.lalala.view.widget.recycler.PullCallback;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 通常文件列表
 * Created by yi on 2017/8/20.
 */

public class NewFileXiaoShuo2Activity extends BaseAppCompatActivity implements NewFolderItemXiaoShuoContract.View {

    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.tv_left_menu)
    TextView mTvMenuLeft;
    @BindView(R.id.list)
    PullAndLoadView mListDocs;
    @BindView(R.id.iv_add_folder)
    ImageView mIvAddFolder;
    @BindView(R.id.iv_menu_list)
    ImageView mIvMenu;

    @Inject
    NewFolderItemXiaoShuoPresenter mPresenter;

    private String mFolderName;
    private XiaoShuoAdapter mAdapter;
    private View mBottomView;
    private ArrayList<FileXiaoShuoEntity> mManHualist;
    private int mPosition;
    // private RxDownload downloadSub;
    private int mbBufferLen;
    private MappedByteBuffer mbBuff;
    private int curEndPos;
    private String charset;
    private boolean isLoading = false;
    private String mUserId;
    private String path;
    private boolean isFromFolder;
    private BottomMenuFragment bottomMenuFragment;
    private String fildId;
    private String fildName;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_folder;
    }

    public static void startActivity(Context context, ArrayList<FileXiaoShuoEntity> entities, String userId, int position) {
        Intent i = new Intent(context, NewFileXiaoShuo2Activity.class);
        i.putExtra(UUID, userId);
        i.putParcelableArrayListExtra("folders", entities);
        i.putExtra("position", position);
        i.putExtra("from_folder", true);
        context.startActivity(i);
    }

    public static void startActivity(Context context, ArrayList<FileXiaoShuoEntity> entities, String userId, int position, String fildId, String fildName) {
        Intent i = new Intent(context, NewFileXiaoShuo2Activity.class);
        i.putExtra(UUID, userId);
        i.putParcelableArrayListExtra("folders", entities);
        i.putExtra("position", position);
        i.putExtra("fildId", fildId);
        i.putExtra("fildName", fildName);
        i.putExtra("from_folder", true);
        context.startActivity(i);
    }

    public static void startActivity(Context context, String path) {
        Intent i = new Intent(context, NewFileXiaoShuo2Activity.class);
        i.putExtra("path", path);
        i.putExtra("from_folder", false);
        context.startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        ViewUtils.setStatusBarLight(getWindow(), $(R.id.top_view));
        DaggerNewFolderXiaoShuoComponent.builder()
                .newFileXiaoShuoModule(new NewFileXiaoShuoModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);
        mUserId = getIntent().getStringExtra(UUID);
        mManHualist = getIntent().getParcelableArrayListExtra("folders");
        mPosition = getIntent().getIntExtra("position", 0);
        fildId = getIntent().getStringExtra("fildId");
        fildName = getIntent().getStringExtra("fildName");
        isFromFolder = getIntent().getBooleanExtra("from_folder", false);
        path = getIntent().getStringExtra("path");
        mIvAddFolder.setVisibility(View.GONE);
//        mListDocs.setPadding(0, (int) getResources().getDimension(R.dimen.x36), 0, 0);
        mListDocs.getSwipeRefreshLayout().setColorSchemeResources(R.color.main_light_cyan, R.color.main_cyan);
        mAdapter = new XiaoShuoAdapter();
        mListDocs.getRecyclerView().setAdapter(mAdapter);
        mListDocs.getRecyclerView().setVerticalScrollBarEnabled(true);
        mListDocs.setLayoutManager(new LinearLayoutManager(this));
        mListDocs.getSwipeRefreshLayout().setEnabled(false);
        mListDocs.setLoadMoreEnabled(true);
        mListDocs.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                isLoading = true;
                loadTxt();
            }

            @Override
            public void onRefresh() {

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

        if (isFromFolder) {
            if (mManHualist == null || mManHualist.size() == 0) {
                mFolderName = fildName;
                mTvMenuLeft.setText(mFolderName);
            } else {
                mFolderName = mManHualist.get(mPosition).getFileName();
                mTvMenuLeft.setText(mFolderName);
                createBottomView();
            }
        }
        initTxt();
        mIvMenu.setVisibility(View.VISIBLE);
        mIvMenu.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                if (bottomMenuFragment != null)
                    bottomMenuFragment.show(getSupportFragmentManager(), "xiaoshuo");
            }
        });
        initPopupMenus();
    }

    private void initPopupMenus() {
        bottomMenuFragment = new BottomMenuFragment();
        ArrayList<MenuItem> items = new ArrayList<>();
        MenuItem item = new MenuItem(4, "添加书签");
        items.add(item);
        bottomMenuFragment.setMenuItems(items);
        bottomMenuFragment.setShowTop(false);
        bottomMenuFragment.setMenuType(BottomMenuFragment.TYPE_VERTICAL);
        bottomMenuFragment.setmClickListener(new BottomMenuFragment.MenuItemClickListener() {
            @Override
            public void OnMenuItemClick(int itemId) {
                if (itemId == 4) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) mListDocs.getRecyclerView().getLayoutManager();
                    int firstVisibleItemPosition1 = layoutManager.findFirstVisibleItemPosition();
                    View viewByPosition = layoutManager.findViewByPosition(firstVisibleItemPosition1);
                    int height = viewByPosition.getHeight();
                    int top = firstVisibleItemPosition1 * height - viewByPosition.getTop();
                    double topPercentage = top / (double) (height * (firstVisibleItemPosition1 + 1));
                    BagReadprogressEntity entity = new BagReadprogressEntity();
                    entity.setReadProgress(topPercentage + firstVisibleItemPosition1);
                    if (mManHualist == null || mManHualist.size() == 0) {
                        entity.setTargetId(fildId);
                    } else {
                        entity.setTargetId(mManHualist.get(mPosition).getFileId());
                    }
                    entity.setType(FolderType.XS.toString());
                    mPresenter.loadBagReadpressUpdate(entity);
                }
            }
        });
    }

    private void initTxt() {
        File file;
        if (isFromFolder) {
            if (mManHualist == null || mManHualist.size() == 0) {
                file = new File(StorageUtils.getNovRootPath() + fildId, fildName);

            } else {
                file = new File(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId(), mManHualist.get(mPosition).getFileName());
            }
        } else {
            file = new File(path);
        }
        long length = file.length();
        if (length > 10) {
            mbBufferLen = (int) length;
            try {
                mbBuff = new RandomAccessFile(file, "r")
                        .getChannel()
                        .map(FileChannel.MapMode.READ_ONLY, 0, length);
                charset = FileUtil.getCharset(file.getAbsolutePath());
                byte[] parabuffer = readParagraphForward(curEndPos);
                curEndPos += parabuffer.length;
                String strParagraph = "";
                try {
                    strParagraph = new String(parabuffer, charset);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
//                strParagraph = strParagraph.replaceAll("\r\n", "  ")
//                        .replaceAll("\n", " "); // 段落中的换行符去掉，绘制的时候再换行
                mAdapter.addItem(strParagraph);
                mAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BagReadprogressEntity entity = new BagReadprogressEntity();
        entity.setType(FolderType.XS.toString());
        if (mManHualist == null || mManHualist.size() == 0) {
            entity.setTargetId(fildId);
        } else {
            entity.setTargetId(mManHualist.get(mPosition).getFileId());
        }
        mPresenter.loadBagReadprogress(entity);
    }

    private void loadTxt() {
        byte[] parabuffer = readParagraphForward(curEndPos);
        curEndPos += parabuffer.length;
        String strParagraph = "";
        try {
            strParagraph = new String(parabuffer, charset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        strParagraph = strParagraph.replaceAll("\r\n", "  ")
//                .replaceAll("\n", " "); // 段落中的换行符去掉，绘制的时候再换行
        if (TextUtils.isEmpty(strParagraph)) {
            mAdapter.setEnableLoadMore(false);
            if (mAdapter.getFooterLayoutCount() != 1) {
                if (isFromFolder && !mUserId.equals(PreferenceUtils.getUUid()) && mBottomView != null) {
                    RecyclerView.LayoutManager manager = mListDocs.getRecyclerView().getLayoutManager();
                    int last = -1;
                    if (manager instanceof GridLayoutManager) {
                        last = ((GridLayoutManager) manager).findLastVisibleItemPosition();
                    } else if (manager instanceof LinearLayoutManager) {
                        last = ((LinearLayoutManager) manager).findLastVisibleItemPosition();
                    }
                    if (last >= 0) {
                        View lastVisibleView = manager.findViewByPosition(last);
                        int[] lastLocation = new int[2];
                        lastVisibleView.getLocationOnScreen(lastLocation);
                        int lastY = lastLocation[1] + lastVisibleView.getMeasuredHeight();
                        int[] location = new int[2];
                        mListDocs.getRecyclerView().getLocationOnScreen(location);
                        int rvY = location[1] + mListDocs.getRecyclerView().getMeasuredHeight();
                        int topMargin;
                        if (lastY >= rvY) {//view超过一屏了
                            topMargin = 0;
                        } else {//view小于一屏
                            topMargin = rvY - lastY;
                        }
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        lp.topMargin = topMargin;
                        mBottomView.setLayoutParams(lp);
                        mAdapter.addFooterView(mBottomView);
                    }
                }
            }
        } else {
            mAdapter.addItem(strParagraph);
            mAdapter.notifyDataSetChanged();
        }
        isLoading = false;
        mListDocs.setComplete();
    }

    /**
     * 读取下一段落
     *
     * @param curEndPos 当前页结束位置指针
     * @return
     */
    private byte[] readParagraphForward(int curEndPos) {
        byte b0;
        int i = curEndPos;
        while (i < mbBufferLen) {
            b0 = mbBuff.get(i++);
//            if (b0 == 0x0a) {
//                break;
//            }
            if (i - curEndPos >= 1024 * 30) {
                break;
            }
        }
        int nParaSize = i - curEndPos;
        byte[] buf = new byte[nParaSize];
        for (i = 0; i < nParaSize; i++) {
            buf[i] = mbBuff.get(curEndPos + i);
        }
        return buf;
    }


    @Override
    protected void initToolbar(Bundle savedInstanceState) {
        mIvBack.setPadding((int) getResources().getDimension(R.dimen.x36), 0, (int) getResources().getDimension(R.dimen.x36), 0);
        mIvBack.setVisibility(View.VISIBLE);
        mIvBack.setImageResource(R.drawable.btn_back_black_normal);
        mIvBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                finish();
            }
        });
        mTvMenuLeft.setTextColor(ContextCompat.getColor(NewFileXiaoShuo2Activity.this, R.color.black_1e1e1e));
    }

    @Override
    protected void initListeners() {
    }

    @Override
    protected void initData() {

    }

    private void createBottomView() {
        mBottomView = LayoutInflater.from(this).inflate(R.layout.item_folder_next, null);

        View preRoot = mBottomView.findViewById(R.id.ll_pre_root);
        View nextRoot = mBottomView.findViewById(R.id.ll_next_root);
        RelativeLayout preRl = mBottomView.findViewById(R.id.rl_pre_root);
        RelativeLayout nextRl = mBottomView.findViewById(R.id.rl_next_root);
        ImageView ivPre = mBottomView.findViewById(R.id.iv_cover);
        ImageView ivNext = mBottomView.findViewById(R.id.iv_cover_next);
        TextView markPre = mBottomView.findViewById(R.id.tv_mark);
        TextView markNext = mBottomView.findViewById(R.id.tv_mark_next);
        TextView titlePre = mBottomView.findViewById(R.id.tv_title);
        TextView titleNext = mBottomView.findViewById(R.id.tv_title_next);
        markPre.setVisibility(View.GONE);
        markNext.setVisibility(View.GONE);
        if (mPosition == mManHualist.size()) {
            nextRoot.setVisibility(View.GONE);
            preRoot.setVisibility(View.VISIBLE);
        } else if (mPosition == 0) {
            nextRoot.setVisibility(View.VISIBLE);
            preRoot.setVisibility(View.GONE);
        } else {
            nextRoot.setVisibility(View.VISIBLE);
            preRoot.setVisibility(View.VISIBLE);
        }
        int width = (DensityUtil.getScreenWidth(this) - (int) getResources().getDimension(R.dimen.x84)) / 3;
        int height = (int) getResources().getDimension(R.dimen.y280);

        preRl.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        nextRl.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        if (mPosition > 0) {
            Glide.with(this)
                    .load(StringUtils.getUrl(this, mManHualist.get(mPosition - 1).getCover(), width, height, false, true))
                    .placeholder(R.drawable.shape_gray_e8e8e8_background)
                    .error(R.drawable.shape_gray_e8e8e8_background)
                    .bitmapTransform(new CropTransformation(this, width, height), new RoundedCornersTransformation(this, (int) getResources().getDimension(R.dimen.y8), 0))
                    .into(ivPre);
            titlePre.setText(mManHualist.get(mPosition - 1).getFileName());
            preRoot.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    mPosition--;
                    if (FileUtil.isExists(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId() + File.separator + mManHualist.get(mPosition).getFileName())) {
                        NewFileXiaoShuo2Activity.startActivity(NewFileXiaoShuo2Activity.this, mManHualist, mUserId, mPosition);
                    } else {
                        File file = new File(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId());
                        if (file.mkdir()) {
                            final ProgressDialog dialog = new ProgressDialog(NewFileXiaoShuo2Activity.this);
                            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                            dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
                            dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                            dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
                            dialog.setTitle("下载中");
                            FileDownloader.getImpl().create(ApiService.URL_QINIU + mManHualist.get(mPosition).getPath())
                                    .setPath(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId() + "/" + mManHualist.get(mPosition).getFileName())
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
                                            NewFileXiaoShuo2Activity.startActivity(NewFileXiaoShuo2Activity.this, mManHualist, mUserId, mPosition);
                                        }

                                        @Override
                                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                        }

                                        @Override
                                        protected void error(BaseDownloadTask task, Throwable e) {
                                            dialog.dismiss();
                                            FileUtil.deleteDir(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId());
                                            showToast("下载失败");
                                        }

                                        @Override
                                        protected void warn(BaseDownloadTask task) {

                                        }
                                    }).start();
//                            downloadSub.download(ApiService.URL_QINIU +  mManHualist.get(mPosition).getPath(),mManHualist.get(mPosition).getFileName(),StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId())
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(new NetTResultSubscriber<DownloadStatus>() {
//                                        @Override
//                                        public void onSuccess() {
//                                            dialog.dismiss();
//                                            NewFileXiaoShuo2Activity.startActivityForResult(NewFileXiaoShuo2Activity.this,mManHualist,mUserId,mPosition);
//                                            downloadSub.deleteServiceDownload(ApiService.URL_QINIU +  mManHualist.get(mPosition).getPath(),false).subscribe();
//                                        }
//
//                                        @Override
//                                        public void onLoading(DownloadStatus res) {
//                                            dialog.setMax((int) res.getTotalSize());
//                                            dialog.setProgress((int) res.getDownloadSize());
//                                        }
//
//                                        @Override
//                                        public void onFail(Throwable e) {
//                                            dialog.dismiss();
//                                            FileUtil.deleteDir(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId());
//                                            showToast("下载失败");
//                                            downloadSub.deleteServiceDownload(ApiService.URL_QINIU +  mManHualist.get(mPosition).getPath(),true).subscribe();
//                                        }
//                                    });
                            dialog.show();
                        }
                    }
                    finish();
                }
            });
        }
        if (mPosition < mManHualist.size() - 1) {
            Glide.with(this)
                    .load(StringUtils.getUrl(this, mManHualist.get(mPosition + 1).getCover(), width, height, false, true))
                    .placeholder(R.drawable.shape_gray_e8e8e8_background)
                    .error(R.drawable.shape_gray_e8e8e8_background)
                    .bitmapTransform(new CropTransformation(this, width, height), new RoundedCornersTransformation(this, (int) getResources().getDimension(R.dimen.y8), 0))
                    .into(ivNext);
            titleNext.setText(mManHualist.get(mPosition + 1).getFileName());
            nextRoot.setOnClickListener(new NoDoubleClickListener() {
                @Override
                public void onNoDoubleClick(View v) {
                    mPosition++;
                    if (FileUtil.isExists(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId() + File.separator + mManHualist.get(mPosition).getFileName())) {
                        NewFileXiaoShuo2Activity.startActivity(NewFileXiaoShuo2Activity.this, mManHualist, mUserId, mPosition);
                    } else {
                        File file = new File(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId());
                        if (file.mkdir()) {
                            final ProgressDialog dialog = new ProgressDialog(NewFileXiaoShuo2Activity.this);
                            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
                            dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
                            dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
                            dialog.setIcon(R.drawable.ic_launcher);// 设置提示的title的图标，默认是没有的
                            dialog.setTitle("下载中");
                            FileDownloader.getImpl().create(ApiService.URL_QINIU + mManHualist.get(mPosition).getPath())
                                    .setPath(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId() + "/" + mManHualist.get(mPosition).getFileName())
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
                                            NewFileXiaoShuo2Activity.startActivity(NewFileXiaoShuo2Activity.this, mManHualist, mUserId, mPosition);
                                        }

                                        @Override
                                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                        }

                                        @Override
                                        protected void error(BaseDownloadTask task, Throwable e) {
                                            dialog.dismiss();
                                            FileUtil.deleteDir(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId());
                                            showToast("下载失败");
                                        }

                                        @Override
                                        protected void warn(BaseDownloadTask task) {

                                        }
                                    }).start();
//                            downloadSub.download(ApiService.URL_QINIU +  mManHualist.get(mPosition).getPath(),mManHualist.get(mPosition).getFileName(),StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId())
//                                    .subscribeOn(Schedulers.io())
//                                    .observeOn(AndroidSchedulers.mainThread())
//                                    .subscribe(new NetTResultSubscriber<DownloadStatus>() {
//                                        @Override
//                                        public void onSuccess() {
//                                            dialog.dismiss();
//                                            NewFileXiaoShuo2Activity.startActivityForResult(NewFileXiaoShuo2Activity.this,mManHualist,mUserId,mPosition);
//                                            downloadSub.deleteServiceDownload(ApiService.URL_QINIU +  mManHualist.get(mPosition).getPath(),false).subscribe();
//                                        }
//
//                                        @Override
//                                        public void onLoading(DownloadStatus res) {
//                                            dialog.setMax((int) res.getTotalSize());
//                                            dialog.setProgress((int) res.getDownloadSize());
//                                        }
//
//                                        @Override
//                                        public void onFail(Throwable e) {
//                                            dialog.dismiss();
//                                            FileUtil.deleteDir(StorageUtils.getNovRootPath() + mManHualist.get(mPosition).getFileId());
//                                            showToast("下载失败");
//                                            downloadSub.deleteServiceDownload(ApiService.URL_QINIU +  mManHualist.get(mPosition).getPath(),true).subscribe();
//                                        }
//                                    });
                            dialog.show();
                        }
                    }
                    finish();
                }
            });
        }
    }

    @Override
    public void onLoadBagReadprogressSuccess(BagLoadReadprogressEntity entity) {
        if (entity != null) {
            double readProgress = entity.getReadProgress();

            double hou = readProgress - ((int) readProgress);

            if ((int) readProgress > 0) {
                for (int i = 1; i < entity.getReadProgress(); i++) {
                    loadTxt();
                }
            }
            View childAt = mListDocs.getRecyclerView().getChildAt(0);
            int height = childAt.getHeight();
            mListDocs.getRecyclerView().scrollBy(0, (int) (height * hou));
        }

    }

    @Override
    public void onloadBagReadpressUpdateSuccess() {
        showToast("保存书签成功～");
    }

    @Override
    public void onFailure(int code, String msg) {
        ErrorCodeUtils.showErrorMsgByCode(this, code, msg);
    }
}
