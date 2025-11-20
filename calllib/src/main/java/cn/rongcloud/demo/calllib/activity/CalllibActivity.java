/*
 * Copyright © 2021 RongCloud. All rights reserved.
 */

package cn.rongcloud.demo.calllib.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.nimo.facebeauty.FBEffect;
import com.nimo.facebeauty.model.FBRotationEnum;
import com.nimo.fb_effect.FBPanelLayout;
import com.nimo.fb_effect.fragment.FBBeautyFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.rongcloud.demo.calllib.R;
import cn.rongcloud.rtc.api.RCRTCEngine;
import io.rong.calllib.CallVideoFrame;
import io.rong.calllib.IRongCallListener;
import io.rong.calllib.IRongReceivedCallListener;
import io.rong.calllib.IVideoFrameListener;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.calllib.StartCameraCallback;
import io.rong.imlib.model.Conversation;

public class CalllibActivity extends AppCompatActivity {


    private static final String TAG = "CalllibActivity";
    private static CallStatus currentStatus = CallStatus.Idle;


    private FrameLayout local;
    private FrameLayout remote;

    private Button callButton;
    private Button acceptButton;
    private Button hangUpButton;
    private EditText idInputEditText;
    private TextView statusTextView;
    //todo --FB--Start--1
    private boolean isInitBuffer= false;
    private FBPanelLayout FBPanelLayout;
    //todo --FB--end --1
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.call) {
                //todo --FB--Start--3
                registerVideoFrameListener();
                //todo --FB--end --3
                call();
            } else if (id == R.id.accept) {
                acceptCall();
            } else if (id == R.id.hang_up) {
                hangUpCall();

            }
        }
    };
    private IRongReceivedCallListener receivedCallListener = new IRongReceivedCallListener() {

        @Override
        public void onCheckPermission(RongCallSession session) {

        }

        @Override
        public void onReceivedCall(RongCallSession session) {
            Log.d(TAG, "onReceivedCall");
            currentStatus = CallStatus.BeCall;
            changeUi();
        }
    };
    private IRongCallListener callListener = new IRongCallListener() {

        private void addLocalView(SurfaceView view) {
            local.removeAllViews();
            local.addView(view);
        }

        private void addRemoteView(SurfaceView view) {
            remote.removeAllViews();
            remote.addView(view);
        }

        private void clearViews() {
            local.removeAllViews();
            remote.removeAllViews();
        }

        @Override
        public void onCallIncoming(RongCallSession callSession, SurfaceView localVideo) {

        }

        /**
         * 电话已拨出
         *
         * @param session 通话实体
         * @param local 本地 camera 信息
         */
        @Override
        public void onCallOutgoing(RongCallSession session, SurfaceView local) {
            Log.d(TAG, "onCallOutgoing");
            currentStatus = CallStatus.Calling;
            changeUi();
        }

        /**
         * 已建立通话
         *
         * @param session 通话实体
         * @param local 本地 camera 信息
         */
        @Override
        public void onCallConnected(RongCallSession session, SurfaceView local) {
            Log.d(TAG, "onCallConnected");
            currentStatus = CallStatus.OnCall;
            changeUi();
            addLocalView(local);
        }

        /**
         * 通话结束
         *
         * @param session 通话实体
         * @param reason 通话中断原因
         */
        @Override
        public void onCallDisconnected(RongCallSession session, RongCallCommon.CallDisconnectedReason reason) {
            Log.d(TAG, "onCallDisconnected reason = " + reason);
            currentStatus = CallStatus.Idle;
            changeUi();
            clearViews();
            //todo --FB--Start--4
            RongCallClient.getInstance().unregisterVideoFrameObserver();
            FBEffect.shareInstance().releaseTextureOESRenderer();
            isInitBuffer = false;
            //todo --FB--end --4
        }

        /**
         * 被叫端正在振铃
         *
         * @param uid 振铃端用户id
         */
        @Override
        public void onRemoteUserRinging(String uid) {
            Log.d(TAG, "onRemoteUserRinging uid = " + uid);

        }

        @Override
        public void onRemoteUserAccept(String userId, RongCallCommon.CallMediaType mediaType) {

        }

        /**
         * 被叫端加入通话
         *
         * @param uid 加入的用户id
         * @param type 加入用户的媒体类型
         * @param ut 加入用户的类型
         * @param view 加入用户者的 camera 信息
         */
        @Override
        public void onRemoteUserJoined(String uid, RongCallCommon.CallMediaType type, int ut, SurfaceView view) {
            Log.d(TAG, "onRemoteUserRinging uid = " + uid);
            addRemoteView(view);
        }

        /**
         * 被叫端离开通话
         *
         * @param uid 离开的用户id
         * @param reason 离开原因
         */
        @Override
        public void onRemoteUserLeft(String uid, RongCallCommon.CallDisconnectedReason reason) {
            Log.d(TAG, "onRemoteUserLeft uid = " + uid);
            currentStatus = CallStatus.Idle;
            changeUi();
            clearViews();
        }

        /**
         * 通话过程中发生异常
         *
         * @param code 异常原因
         */
        @Override
        public void onError(RongCallCommon.CallErrorCode code) {
            Log.e(TAG, "onError code = " + code);
            currentStatus = CallStatus.Idle;
            changeUi();
            clearViews();
        }

        @Override
        public void onRemoteUserInvited(String uid, RongCallCommon.CallMediaType type) {
            Log.d(TAG, "onRemoteUserInvited uid = " + uid);
        }

        @Override
        public void onMediaTypeChanged(String uid, RongCallCommon.CallMediaType type, SurfaceView video) {
            Log.d(TAG, "onMediaTypeChanged uid = " + uid + ", type = " + type);
        }

        @Override
        public void onRemoteCameraDisabled(String uid, boolean disabled) {
            Log.d(TAG, "onRemoteCameraDisabled uid = " + uid + ", disabled = " + disabled);
        }

        @Override
        public void onRemoteMicrophoneDisabled(String uid, boolean disabled) {
            Log.d(TAG, "onRemoteMicrophoneDisabled uid = " + uid + ", disabled = " + disabled);
        }

        @Override
        public void onRemoteUserPublishVideoStream(String uid, String sid, String tag, SurfaceView surfaceView) {
            Log.d(TAG, "onRemoteUserPublishVideoStream uid = " + uid + ", sid = " + sid + ", tag = " + tag);
        }

        @Override
        public void onRemoteUserUnpublishVideoStream(String uid, String sid, String tag) {
            Log.d(TAG, "onRemoteUserUnpublishVideoStream uid = " + uid + ", sid = " + sid + ", tag = " + tag);
        }

        @Override
        public void onFirstRemoteVideoFrame(String uid, int height, int width) {
            Log.d(TAG, "onFirstRemoteVideoFrame uid = " + uid + ", height = " + height + ", width = " + width);
        }

        @Override
        public void onFirstRemoteAudioFrame(String userId) {

        }

        @Override
        public void onNetworkSendLost(int lossRate, int delay) {
            Log.d(TAG, "onNetworkSendLost lossRate = " + lossRate + ", delay = " + delay);
        }

        @Override
        public void onNetworkReceiveLost(String uid, int lossRate) {
            Log.d(TAG, "onNetworkReceiveLost uid = " + uid + ", lossRate = " + lossRate);
        }

        @Override
        public void onAudioLevelSend(String level) {
            Log.d(TAG, "onAudioLevelSend level = " + level);
        }

        @Override
        public void onAudioLevelReceive(HashMap<String, String> levels) {
            Log.d(TAG, "onAudioLevelReceive levels = " + levels);
        }
    };

    public static void start(Context context) {
        Intent intent = new Intent(context, CalllibActivity.class);
        context.startActivity(intent);
    }

    private void changeUi() {
        if (CallStatus.Idle == currentStatus) {
            callButton.setVisibility(View.VISIBLE);
            statusTextView.setText("");
            acceptButton.setVisibility(View.INVISIBLE);
            hangUpButton.setVisibility(View.INVISIBLE);
        } else if (CallStatus.Calling == currentStatus) {
            callButton.setVisibility(View.INVISIBLE);
            statusTextView.setText("呼叫中");
            hangUpButton.setVisibility(View.VISIBLE);
            acceptButton.setVisibility(View.INVISIBLE);
        } else if (CallStatus.BeCall == currentStatus) {
            callButton.setVisibility(View.INVISIBLE);
            statusTextView.setText("有人找你");
            hangUpButton.setVisibility(View.VISIBLE);
            acceptButton.setVisibility(View.VISIBLE);
        } else if (CallStatus.OnCall == currentStatus) {
            callButton.setVisibility(View.INVISIBLE);
            statusTextView.setText("通话中");
            hangUpButton.setVisibility(View.VISIBLE);
            acceptButton.setVisibility(View.INVISIBLE);
        }
    }

    private void initUi() {
        local = findViewById(R.id.local);
        remote = findViewById(R.id.remote);

        idInputEditText = findViewById(R.id.et_userId);
        statusTextView = findViewById(R.id.tv_status);
        callButton = findViewById(R.id.call);
        acceptButton = findViewById(R.id.accept);
        hangUpButton = findViewById(R.id.hang_up);

        callButton.setOnClickListener(onClickListener);
        acceptButton.setOnClickListener(onClickListener);
        hangUpButton.setOnClickListener(onClickListener);
    }

    private void registerCallListener() {
        RongCallClient.setReceivedCallListener(receivedCallListener);
        RongCallClient.getInstance().setVoIPCallListener(callListener);

    }

    private void unRegisterCallListener() {
        RongCallClient.setReceivedCallListener(null);
        RongCallClient.getInstance().setVoIPCallListener(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_main);
        //todo --FB--Start--5
        //在activity_call_main中的最下面添加===
//        <FrameLayout
//        android:id="@+id/bottom_container"
//        android:layout_width="match_parent"
//        android:layout_height="wrap_content"
//        android:layout_gravity="bottom"
//        app:layout_constraintBottom_toBottomOf="parent" />
        //todo --FB--end--5
        //todo --FB--Start--6
        FBPanelLayout = new FBPanelLayout(this).init(getSupportFragmentManager());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FBBeautyFragment beautyFragment = new FBBeautyFragment();
        fragmentTransaction.add(R.id.bottom_container, beautyFragment);
        fragmentTransaction.commit();
        //todo --FB--end--6
        initUi();
        changeUi();
        registerCallListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterCallListener();
    }

    private void call() {
        Conversation.ConversationType conversationType = Conversation.ConversationType.PRIVATE;
        String targetId = idInputEditText.getText().toString().trim();
        if (TextUtils.isEmpty(targetId)) {
            Toast.makeText(this, "请输入userid", Toast.LENGTH_LONG).show();
            return;
        }
//todo --FB--Start--7
        List<String> userIds = new ArrayList<>();
        userIds.add(targetId);
        RongCallCommon.CallMediaType mediaType = RongCallCommon.CallMediaType.VIDEO;
        String extra = "";
        boolean mirror = false;
        int cameraId = 1;
        List<String> observerUserIds = new ArrayList<>();
        RongCallClient.getInstance().startCall(cameraId, mirror, conversationType, targetId, userIds, observerUserIds, mediaType, extra, new StartCameraCallback() {
            @Override
            public void onDone(boolean b) {
            }

            @Override
            public void onError(int i) {
            }
        });
        //todo --FB--end--7
//        注意：融云官方给的例子是 RongCallClient.getInstance().startCall(conversationType, targetId, userIds, null, mediaType, extra);但是在主叫方的本地会出现镜像问题
//        RongCallClient.getInstance().startCall(conversationType, targetId, userIds, null, mediaType, extra);
    }
    //todo --FB--Start--2
    private  void  registerVideoFrameListener(){
        RongCallClient.getInstance().registerVideoFrameListener(new IVideoFrameListener() {

            @Override
            public CallVideoFrame processVideoFrame(CallVideoFrame callVideoFrame) {
                //todo--start--FB 添加渲染
                if (!isInitBuffer){
                    isInitBuffer = FBEffect.shareInstance().initTextureOESRenderer(
                            callVideoFrame.getWidth(),
                            callVideoFrame.getHeight(),
                            FBRotationEnum.FBRotationClockwise270,
                            false,
                            5);
                    Log.i(TAG, "processVideoFrame:isInitBuffer "+isInitBuffer);
                }
                int textureId =  FBEffect.shareInstance().processTextureOES(callVideoFrame.getOesTextureId());
                callVideoFrame.setOesTextureId(textureId);
                return callVideoFrame;
            }
        });
    }
    //todo --FB--Start--2
    private void acceptCall() {
        if (RongCallClient.getInstance() != null && RongCallClient.getInstance().getCallSession() != null) {
            RongCallClient.getInstance().acceptCall(RongCallClient.getInstance().getCallSession().getCallId());
        }
    }

    private void hangUpCall() {
        if (RongCallClient.getInstance() != null && RongCallClient.getInstance().getCallSession() != null) {
            Log.d(TAG, "onClick: 挂断111===");
            RongCallClient.getInstance().hangUpCall(RongCallClient.getInstance().getCallSession().getCallId());

        }
    }

    enum CallStatus {
        Idle,
        Calling,
        BeCall,
        OnCall
    }
}