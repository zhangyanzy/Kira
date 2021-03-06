package com.moemoe.lalala.view.adapter;

import com.moemoe.lalala.R;
import com.moemoe.lalala.model.entity.BagMyShowEntity;
import com.moemoe.lalala.view.widget.adapter.BaseRecyclerViewAdapter;

/**
 *
 * Created by yi on 2017/6/26.
 */

public class BagMyAdapter extends BaseRecyclerViewAdapter<BagMyShowEntity,BagMyViewHolder> {

    private String userId;
    private String type;
    private String sign;
    public BagMyAdapter(String userId,String type,String sign) {
        super(R.layout.item_bag_my);
        this.userId = userId;
        this.type = type;
        this.sign = sign;
    }


    @Override
    protected void convert(BagMyViewHolder helper, final BagMyShowEntity item, int position) {
        helper.createItem(item,position, userId,type, sign);
    }

    @Override
    public int getItemType(int position) {
        return 0;
    }
}
