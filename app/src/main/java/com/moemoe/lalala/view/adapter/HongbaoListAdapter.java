package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.HongBaoEntity;
import com.moemoe.lalala.model.entity.ShowFolderEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class HongbaoListAdapter extends BaseRecyclerViewAdapter<HongBaoEntity,HongbaoListHolder> {

    public HongbaoListAdapter() {
        super(R.layout.item_hongbao);
    }


    @Override
    protected void convert(HongbaoListHolder helper, final HongBaoEntity item, int position) {
        helper.createItem(item,position);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }

}
