package com.huantansheng.easyphotos.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.models.album.AlbumModel;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.result.Result;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.ui.adapter.PreviewPhotosAdapter;
import com.huantansheng.easyphotos.ui.widget.PressedTextView;
import com.huantansheng.easyphotos.utils.Color.ColorUtils;
import com.huantansheng.easyphotos.utils.system.SystemUtils;

import java.util.ArrayList;

/**
 * 预览页 不关联选择
 */
public class Preview2Activity extends AppCompatActivity implements PreviewPhotosAdapter.OnClickListener, View.OnClickListener, PreviewFragment.OnPreviewFragmentClickListener {


    public static long startTime = 0;

    public static boolean doubleClick() {
        long now = System.currentTimeMillis();
        if (now - startTime < 600) {
            return true;
        }
        startTime = now;
        return false;
    }


    public static void start(Activity act) {
        if (doubleClick()) return;
        Intent intent = new Intent(act, Preview2Activity.class);
        act.startActivity(intent);
    }

    public static void start(Fragment fragment) {
        if (doubleClick()) return;
        Intent intent = new Intent(fragment.getActivity(), Preview2Activity.class);
        fragment.startActivity(intent);
    }

    public static void start(androidx.fragment.app.Fragment fragment) {
        if (doubleClick()) return;
        Intent intent = new Intent(fragment.getContext(), Preview2Activity.class);
        fragment.startActivity(intent);
    }

    /**
     * 一些旧设备在UI小部件更新之间需要一个小延迟
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @Override
        public void run() {
            SystemUtils.getInstance().systemUiHide(Preview2Activity.this, decorView);
        }
    };
    private RelativeLayout mBottomBar;
    private FrameLayout mToolBar;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // 延迟显示UI元素
            mBottomBar.setVisibility(View.VISIBLE);
            mToolBar.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    View decorView;
    private TextView tvNumber;
    private PressedTextView tvDownload;
    private RecyclerView rvPhotos;
    private PreviewPhotosAdapter adapter;
    private PagerSnapHelper snapHelper;
    private LinearLayoutManager lm;
    private int index;
    private ArrayList<Photo> photos = new ArrayList<>();
    private int lastPosition = 0;//记录recyclerView最后一次角标位置，用于判断是否转换了item

    private PreviewFragment previewFragment;
    private int statusColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();
        SystemUtils.getInstance().systemUiInit(this, decorView);

        setContentView(R.layout.activity_preview2_easy_photos);

        hideActionBar();
        adaptationStatusBar();
        if (null == AlbumModel.instance) {
            finish();
            return;
        }
        initData();
        initView();
    }

    private void adaptationStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            statusColor = ContextCompat.getColor(this, R.color.easy_photos_status_bar);
            if (ColorUtils.isWhiteColor(statusColor)) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        }
    }

    private void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }


    private void initData() {
        photos.clear();
        photos.addAll(Result.photos);
        index = Setting.currentIndex;

        lastPosition = index;
        mVisible = true;
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        AlphaAnimation hideAnimation = new AlphaAnimation(1.0f, 0.0f);
        hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBottomBar.setVisibility(View.GONE);
                mToolBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        hideAnimation.setDuration(UI_ANIMATION_DELAY);
        mBottomBar.startAnimation(hideAnimation);
        mToolBar.startAnimation(hideAnimation);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);

        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);

    }


    private void show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 16) {
            SystemUtils.getInstance().systemUiShow(this, decorView);
        }

        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.post(mShowPart2Runnable);
    }

    @Override
    public void onPhotoClick() {
        toggle();
    }

    @Override
    public void onPhotoScaleChanged() {
        if (mVisible) hide();
    }


    private void initView() {
        setClick(R.id.iv_back);

        mToolBar = (FrameLayout) findViewById(R.id.m_top_bar_layout);
        if (!SystemUtils.getInstance().hasNavigationBar(this)) {
            FrameLayout mRootView = (FrameLayout) findViewById(R.id.m_root_view);
            mRootView.setFitsSystemWindows(true);
            mToolBar.setPadding(0, SystemUtils.getInstance().getStatusBarHeight(this), 0, 0);
            if (ColorUtils.isWhiteColor(statusColor)) {
                SystemUtils.getInstance().setStatusDark(this, true);
            }
        }
        mBottomBar = (RelativeLayout) findViewById(R.id.m_bottom_bar);
        tvNumber = (TextView) findViewById(R.id.tv_number);
        tvDownload = (PressedTextView) findViewById(R.id.tv_download);
        previewFragment =
                (PreviewFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_preview);

        tvDownload.setVisibility(Setting.showDownload ? View.VISIBLE : View.GONE);
        setClick(tvDownload);

        initRecyclerView();
    }

    private void initRecyclerView() {
        rvPhotos = (RecyclerView) findViewById(R.id.rv_photos);
        adapter = new PreviewPhotosAdapter(this, photos, this);
        lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvPhotos.setLayoutManager(lm);
        rvPhotos.setAdapter(adapter);
        rvPhotos.scrollToPosition(index);
        snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvPhotos);
        rvPhotos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                View view = snapHelper.findSnapView(lm);
                if (view == null) {
                    return;
                }
                int position = lm.getPosition(view);
                if (lastPosition == position) {
                    return;
                }
                lastPosition = position;
                previewFragment.setSelectedPosition(lastPosition);
                tvNumber.setText(getString(R.string.preview_current_number_easy_photos,
                        lastPosition + 1, photos.size()));
            }
        });
        tvNumber.setText(getString(R.string.preview_current_number_easy_photos, index + 1,
                photos.size()));

        previewFragment.setSelectedPosition(lastPosition);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_back == id) {
            onBackPressed();
        } else if (R.id.tv_download == id) {
            //点击下载
            if (Setting.mPreviewCallback != null) {
                Setting.mPreviewCallback.onDownload(photos.get(lastPosition));
            }
        }
    }


    @Override
    public void onPreviewPhotoClick(int position) {
        String path = Result.getPhotoPath(position);
        int size = photos.size();
        for (int i = 0; i < size; i++) {
            if (TextUtils.equals(path, photos.get(i).path)) {
                rvPhotos.scrollToPosition(i);
                lastPosition = i;
                tvNumber.setText(getString(R.string.preview_current_number_easy_photos,
                        lastPosition + 1, photos.size()));
                previewFragment.setSelectedPosition(position);
                return;
            }
        }
    }

    private void setClick(@IdRes int... ids) {
        for (int id : ids) {
            findViewById(id).setOnClickListener(this);
        }
    }

    private void setClick(View... views) {
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }
}
