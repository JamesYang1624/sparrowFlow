package com.yangwz.sparrowflow.cover;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.yangwz.sparrowflow.R;
import com.yangwz.sparrowflow.cover.view.SelCoverAdapter;
import com.yangwz.sparrowflow.cover.view.ThumbnailSelTimeView;
import com.yangwz.sparrowflow.databinding.ActivitySelCoverTimeBinding;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import static com.yangwz.sparrowflow.cover.StaticFinalValues.COMR_FROM_SEL_COVER_TIME_ACTIVITY;


public class SelCoverTimeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SEL_TIME = 0;
    private static final int SUBMIT = 1;
    private static final int SAVE_BITMAP = 2;

    private List<Bitmap> mBitmapList = new ArrayList<>();
    private String mVideoPath = "/storage/emulated/0/DCIM/Camera/VID_20220114_160539.mp4";
    public SelCoverAdapter mSelCoverAdapter;
    private float mSelStartTime = 0.5f;
    private boolean mIsSelTime;//是否点了完成按钮
    public String mVideoRotation;
    private ActivitySelCoverTimeBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_sel_cover_time);
//        setContentView(R.layout.activity_sel_cover_time);
//        mVideoPath = getIntent().getStringExtra(StaticFinalValues.VIDEOFILEPATH);
        initThumbs();
        initSetParam();
        initView();
        initListener();
        StatusBarUtil.transparencyBar(this);
    }

    private void initListener() {
        mBinding.ivBack.setOnClickListener(this);
        mBinding.cutTimeFinishTv.setOnClickListener(this);
        mBinding.thumbSelTimeView.setOnScrollBorderListener(new ThumbnailSelTimeView.OnScrollBorderListener() {
            @Override
            public void OnScrollBorder(float start, float end) {
            }

            @Override
            public void onScrollStateChange() {
                myHandler.removeMessages(SEL_TIME);
                float rectLeft = mBinding.thumbSelTimeView.getRectLeft();
                mSelStartTime = (mVideoDuration * rectLeft) / 1000;
                Log.e("Atest", "onScrollStateChange: " + mSelStartTime);
                mBinding.selCoverVideoView.seekTo((int) mSelStartTime);
                myHandler.sendEmptyMessage(SEL_TIME);
            }
        });
    }

    private void initSetParam() {
        ViewGroup.LayoutParams layoutParams = mBinding.selCoverVideoView.getLayoutParams();
        if (mVideoRotation.equals("0") && mVideoWidth > mVideoHeight) {//本地视频横屏 0表示竖屏
            layoutParams.width = 1120;
            layoutParams.height = 630;
        } else {
            layoutParams.width = 630;
            layoutParams.height = 1120;
        }

        mBinding.selCoverVideoView.setLayoutParams(layoutParams);
        mBinding.selCoverVideoView.setVideoPath(mVideoPath);
        mBinding.selCoverVideoView.start();
        mBinding.selCoverVideoView.getDuration();
    }

    private void initView() {
        mSelCoverAdapter = new SelCoverAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        mBinding.cutRecyclerView.setLayoutManager(linearLayoutManager);
        mBinding.cutRecyclerView.setAdapter(mSelCoverAdapter);
    }

    public int mVideoHeight, mVideoWidth, mVideoDuration;

    private void initThumbs() {
        final MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
        mediaMetadata.setDataSource(this, Uri.parse(mVideoPath));
        mVideoRotation =
                mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
        mVideoWidth =
                Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        mVideoHeight =
                Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        mVideoDuration =
                Integer.parseInt(mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        final int frame = 10;
        final int frameTime = mVideoDuration / frame * 1000;
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                for (int x = 0; x < frame; x++) {
                    Bitmap bitmap = mediaMetadata.getFrameAtTime(frameTime * x,
                            MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                    Message msg = myHandler.obtainMessage();
                    msg.what = SAVE_BITMAP;
                    msg.obj = bitmap;
                    msg.arg1 = x;
                    myHandler.sendMessage(msg);
                }
                mediaMetadata.release();
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                myHandler.sendEmptyMessage(SUBMIT);
            }
        }.execute();
    }

    private Handler myHandler = new MyHandler(this);

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.cut_time_finish_tv:
                mIsSelTime = true;
                onBackPressed();
                break;
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<SelCoverTimeActivity> mActivityWeakReference;

        public MyHandler(SelCoverTimeActivity activityWeakReference) {
            mActivityWeakReference = new WeakReference<SelCoverTimeActivity>(activityWeakReference);
        }

        @Override
        public void handleMessage(Message msg) {
            SelCoverTimeActivity activity = mActivityWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case SEL_TIME:
                        activity.mBinding.selCoverVideoView.seekTo((int) activity.mSelStartTime * 1000);
                        activity.mBinding.selCoverVideoView.start();
                        sendEmptyMessageDelayed(SEL_TIME, 1000);
                        break;
                    case SAVE_BITMAP:
                        activity.mBitmapList.add(msg.arg1, (Bitmap) msg.obj);
                        break;
                    case SUBMIT:
                        activity.mSelCoverAdapter.addBitmapList(activity.mBitmapList);
                        sendEmptyMessageDelayed(SEL_TIME, 1000);
                        break;
                }
            }
        }
    }



    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        if (mIsSelTime) {
            if (mSelStartTime < 0.5f) {
                mSelStartTime = 0.5f;
            }
            intent.putExtra(StaticFinalValues.CUT_TIME, mSelStartTime);
        } else {
            intent.putExtra(StaticFinalValues.CUT_TIME, 0.5f);
        }
        setResult(COMR_FROM_SEL_COVER_TIME_ACTIVITY, intent);
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new ContextWrapper(newBase) {
            @Override
            public Object getSystemService(String name) {
                if (Context.AUDIO_SERVICE.equals(name))
                    return getApplicationContext().getSystemService(name);
                return super.getSystemService(name);
            }
        });
    }
}
