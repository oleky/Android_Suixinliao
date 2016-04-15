package com.tencent.qcloud.presentation.event;

import android.util.Log;

import com.tencent.TIMCallBack;
import com.tencent.TIMFriendGroup;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMFriendshipProxyListener;
import com.tencent.TIMFriendshipProxyStatus;
import com.tencent.TIMSNSChangeInfo;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * 好友关系链数据缓存，维持更新状态，底层IMSDK会维护本地存储
 * 由于IMSDK有内存缓存，所以每次关系链变更时全量同步数据，此处也可以只更新变量数据
 */
public class FriendshipEvent extends Observable implements TIMCallBack, TIMFriendshipProxyListener {

    private final String TAG = FriendshipEvent.class.getSimpleName();

    private FriendshipEvent(){}

    private static FriendshipEvent instance = new FriendshipEvent();

    public static FriendshipEvent getInstance(){
        return instance;
    }

    public void init(){
        TIMFriendshipManager.getInstance().getFriendshipProxy().syncWithFlags(0xff, null, this);
        TIMFriendshipManager.getInstance().getFriendshipProxy().setListener(this);
    }



    @Override
    public void onError(int i, String s) {
        Log.e(TAG, "sync friendship error " + s);
    }


    @Override
    public void onSuccess() {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.REFRESH, null));
    }

    @Override
    public void OnProxyStatusChange(TIMFriendshipProxyStatus timFriendshipProxyStatus) {

    }

    @Override
    public void OnAddFriends(List<TIMUserProfile> list) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.ADD, list));
    }

    @Override
    public void OnDelFriends(List<String> list) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.DEL, list));
    }

    @Override
    public void OnFriendProfileUpdate(List<TIMUserProfile> list) {
    }

    @Override
    public void OnAddFriendReqs(List<TIMSNSChangeInfo> list) {
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.ADD_REQ, list));
    }

    @Override
    public void OnAddFriendGroups(List<TIMFriendGroup> list) {
    }

    @Override
    public void OnDelFriendGroups(List<String> list) {
    }

    @Override
    public void OnFriendGroupUpdate(List<TIMFriendGroup> list) {
    }

    /**
     * 好友关系链消息已读通知
     */
    public void OnFriendshipMessageRead(){
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.READ_MSG, null));
    }

    /**
     * 好友分组变更通知
     */
    public void OnFriendGroupChange(){
        setChanged();
        notifyObservers(new NotifyCmd(NotifyType.PROFILE_UPDATE, null));
    }


    /**
     * 通知上层用的数据
     */
    public class NotifyCmd{
        public final NotifyType type;
        public final Object data;

        NotifyCmd(NotifyType type, Object data){
            this.type = type;
            this.data = data;
        }

    }

    public enum NotifyType{
        REFRESH,//刷新数据
        ADD_REQ,//请求添加
        READ_MSG,//关系链通知已读
        ADD,//添加好友
        DEL,//删除好友
        PROFILE_UPDATE,//变更好友资料
    }


}
