package com.moemoe.lalala.presenter;

import com.moemoe.lalala.model.api.ApiService;
import com.moemoe.lalala.model.api.NetResultSubscriber;
import com.moemoe.lalala.model.api.NetSimpleResultSubscriber;
import com.moemoe.lalala.model.entity.AddressEntity;
import com.moemoe.lalala.model.entity.CommentV2Entity;
import com.moemoe.lalala.model.entity.DocTagEntity;
import com.moemoe.lalala.model.entity.NewDynamicEntity;
import com.moemoe.lalala.model.entity.TagLikeEntity;
import com.moemoe.lalala.model.entity.TagSendEntity;
import com.moemoe.lalala.model.entity.UserTopEntity;

import java.util.ArrayList;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by yi on 2016/11/29.
 */

public class DynamicPresenter implements DynamicContract.Presenter {

    private DynamicContract.View view;
    private ApiService apiService;

    @Inject
    public DynamicPresenter(DynamicContract.View view, ApiService apiService) {
        this.view = view;
        this.apiService = apiService;
    }

    @Override
    public void release() {
        view = null;
    }

    @Override
    public void loadTags(String id) {
        apiService.loadTags(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<DocTagEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<DocTagEntity> tagEntities) {
                        if(view != null) view.onLoadTagsSuccess(tagEntities);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code, msg);
                    }
                });
    }

    @Override
    public void deleteDynamic(String id,String type) {
        apiService.deleteDynamic(id,type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onDeleteDynamicSuccess();
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void followUser(String id, boolean isFollow) {
        if (!isFollow){
            apiService.followUser(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onFollowUserSuccess(true);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else {
            apiService.cancelfollowUser(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onFollowUserSuccess(false);
                        }

                        @Override
                        public void onFail(int code,String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }
    }

    @Override
    public void favoriteDynamic(String id, final boolean isFavorite) {
        if(isFavorite){
            apiService.cancelCollectDynamic(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onFavoriteDynamicSuccess(!isFavorite);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }else {
            apiService.collectDynamic(id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetSimpleResultSubscriber() {
                        @Override
                        public void onSuccess() {
                            if(view != null) view.onFavoriteDynamicSuccess(!isFavorite);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code, msg);
                        }
                    });
        }
    }

    @Override
    public void sendTag(final TagSendEntity bean) {
        apiService.sendTag(bean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<String>() {
                    @Override
                    public void onSuccess(String s) {
                        if(view != null) view.onSendTagSuccess(s,bean.getTag());
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void plusTag(final boolean isLike, final int position, TagLikeEntity entity) {
        apiService.plusTag(!isLike,entity.getDocId(),entity.getTagId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onPlusTagSuccess(position, !isLike);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });

    }

    @Override
    public void giveCoin(String id, final int coin) {
        apiService.giveCoinToDynamic(id,coin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onGiveCoinSuccess(coin);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void loadCommentsList(String id, int type, int sortType, final int index , final int sart) {
        if(type == 0){//0 转发 1 评论
            apiService.loadRtComment(id,ApiService.LENGHT,index)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new NetResultSubscriber<ArrayList<CommentV2Entity>>() {
                        @Override
                        public void onSuccess(ArrayList<CommentV2Entity> commentV2Entities) {
                            if(view != null) view.onLoadCommentsSuccess(commentV2Entities,sart == 0);
                        }

                        @Override
                        public void onFail(int code, String msg) {
                            if(view != null) view.onFailure(code,msg);
                        }
                    });
        }else {
            if (sortType==0){
                apiService.loadComment(id,"default",ApiService.LENGHT,index)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<ArrayList<CommentV2Entity>>() {
                            @Override
                            public void onSuccess(ArrayList<CommentV2Entity> commentV2Entities) {
                                if(view != null) view.onLoadCommentsSuccess(commentV2Entities,sart == 0);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                if(view != null) view.onFailure(code,msg);
                            }
                        });
            }else if (sortType==1){
                apiService.loadComment(id,"like",ApiService.LENGHT,index)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<ArrayList<CommentV2Entity>>() {
                            @Override
                            public void onSuccess(ArrayList<CommentV2Entity> commentV2Entities) {
                                if(view != null) view.onLoadCommentsSuccess(commentV2Entities,sart == 0);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                if(view != null) view.onFailure(code,msg);
                            }
                        });
            }else if (sortType==2){
                apiService.loadComment(id,"time",ApiService.LENGHT,index)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new NetResultSubscriber<ArrayList<CommentV2Entity>>() {
                            @Override
                            public void onSuccess(ArrayList<CommentV2Entity> commentV2Entities) {
                                if(view != null) view.onLoadCommentsSuccess(commentV2Entities,sart == 0);
                            }

                            @Override
                            public void onFail(int code, String msg) {
                                if(view != null) view.onFailure(code,msg);
                            }
                        });
            }

        }
    }

    @Override
    public void deleteComment(String id, String commentId, final int position) {
        apiService.deleteComment(id,"dynamic",commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onDeleteCommentSuccess(position);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void favoriteComment(String id, String commentId, final boolean isFavorite, final int position) {
        apiService.favoriteComment(id,!isFavorite,commentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.favoriteCommentSuccess(!isFavorite,position);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void getDynamic(String id) {
        apiService.getDynamic(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<NewDynamicEntity>() {
                    @Override
                    public void onSuccess(NewDynamicEntity entity) {
                        if(view != null) view.onLoadDynamicSuccess(entity);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void likeDynamic(String id, final boolean isLike) {
        apiService.likeDynamic(id,!isLike)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetSimpleResultSubscriber() {
                    @Override
                    public void onSuccess() {
                        if(view != null) view.onLikeDynamicSuccess(!isLike);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if(view != null) view.onFailure(code,msg);
                    }
                });
    }

    @Override
    public void loadGetCommentsLz(String targetId, final int start) {
        apiService.loadGetCommentsLz(targetId,ApiService.LENGHT,start)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<CommentV2Entity>>() {
                    @Override
                    public void onSuccess(ArrayList<CommentV2Entity> entities) {
                        if (view != null) view.onLoadGetCommentsLzSuccess(entities,start ==0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if (view != null) view.onFailure(code, msg);
                    }
                });
    }
    @Override
    public void loadDynamicLikeUsers(String targetId, final int start) {
        apiService.loadDynamicLikeUsers(targetId,start,ApiService.LENGHT)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NetResultSubscriber<ArrayList<UserTopEntity>>() {
                    @Override
                    public void onSuccess(ArrayList<UserTopEntity> entities) {
                        if (view != null) view.onloadDynamicLikeUsersSuccess(entities,start ==0);
                    }

                    @Override
                    public void onFail(int code, String msg) {
                        if (view != null) view.onFailure(code, msg);
                    }
                });
    }

}
