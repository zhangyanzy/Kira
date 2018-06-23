package com.moemoe.lalala.view.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.LinearLayout;

import com.moemoe.lalala.R;
import com.moemoe.lalala.app.MoeMoeApplication;
import com.moemoe.lalala.databinding.ActivityPrincipalListBinding;
import com.moemoe.lalala.di.components.DaggerPrincipalListComponent;
import com.moemoe.lalala.di.modules.PrincipalListModule;
import com.moemoe.lalala.model.entity.NewStoryInfo;
import com.moemoe.lalala.model.entity.NewStoryJsonInfo;
import com.moemoe.lalala.model.entity.OnItemListener;
import com.moemoe.lalala.presenter.NewPrincipalListPresenter;
import com.moemoe.lalala.presenter.PrincipalListContract;
import com.moemoe.lalala.utils.SpacesItemDecoration;
import com.moemoe.lalala.view.adapter.PrincipalListAdapter;
import com.moemoe.lalala.view.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PrincipalListActivity extends BaseActivity implements PrincipalListContract.View {

    private ActivityPrincipalListBinding binding;
    private PrincipalListAdapter mAdapter;
    private List<Bitmap> extraBmps;
    private String groupId;
    private String groupName;
    private String scriptId;


    @Inject
    NewPrincipalListPresenter mPresenter;

    @Override
    protected void initComponent() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_principal_list);
        binding.setPresenter(new Presenter());
        DaggerPrincipalListComponent.builder()
                .principalListModule(new PrincipalListModule(this))
                .netComponent(MoeMoeApplication.getInstance().getNetComponent())
                .build()
                .inject(this);

        Intent intent = getIntent();
        groupId = intent.getStringExtra("id");
        groupName = intent.getStringExtra("groupName");
        mPresenter.getPrincipalListInfo(groupId);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        binding.listTitleName.setText(groupName);

    }

    @Override
    protected void initToolbar(Bundle savedInstanceState) {

    }

    @Override
    protected void initListeners() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onFailure(int code, String msg) {

    }

    @Override
    public void getPrincipalListInfoSuccess(ArrayList<NewStoryInfo> newStoryInfos) {
        initAdapter(newStoryInfos);
    }

    @Override
    public void playStorySuccess(NewStoryJsonInfo newStoryJsonInfo) {

    }

    private void initAdapter(final List<NewStoryInfo> infos) {
        mAdapter = new PrincipalListAdapter(this, infos);
        binding.principalListRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayout.VERTICAL, false));
        binding.principalListRecycleView.addItemDecoration(new SpacesItemDecoration(30));
        binding.principalListRecycleView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemListener() {
            @Override
            public void onItemClick(View view, int position) {
                scriptId = infos.get(position).getScriptId();
                if (scriptId == null) {
                    showToast("该剧情暂未解锁哦");
                } else {
                    //TODO 剧情Json脚本
//                    mPresenter.playStory(scriptId);
                    Intent intent = new Intent(PrincipalListActivity.this, MapEventNewActivity.class);
                    intent.putExtra("id", scriptId);
                    intent.putExtra("type", true);
                    startActivity(intent);
                }
            }
        });

    }


    public class Presenter {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.principal_list_back_btn:
                    finish();
                    break;
                default:
                    break;
            }
        }
    }
}
