package com.huantansheng.easyphotos.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.FrescoImageViewFactory;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.ui.widget.ProgressPieIndicator;

import java.io.File;
import java.util.ArrayList;


/**
 * 大图预览界面图片集合的适配器
 * Created by huan on 2017/10/26.
 */
public class PreviewPhotosAdapter extends RecyclerView.Adapter<PreviewPhotosAdapter.PreviewPhotosViewHolder> {
    private ArrayList<Photo> photos;
    private OnClickListener listener;
    private LayoutInflater inflater;

    public interface OnClickListener {
        void onPhotoClick();

        void onPhotoScaleChanged();
    }

    public PreviewPhotosAdapter(Context cxt, ArrayList<Photo> photos, OnClickListener listener) {
        this.photos = photos;
        this.inflater = LayoutInflater.from(cxt);
        this.listener = listener;
    }

    @NonNull
    @Override
    public PreviewPhotosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PreviewPhotosViewHolder(inflater.inflate(R.layout.item_preview_photo_easy_photos, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PreviewPhotosViewHolder holder, int position) {
        final Uri uri = photos.get(position).uri;
        final String path = photos.get(position).path;
        final String type = photos.get(position).type;
        final double ratio =
                (double) photos.get(position).height / (double) photos.get(position).width;

        holder.ivPlay.setVisibility(View.GONE);

        if (type.contains(Type.VIDEO)) {
            holder.ivPlay.setVisibility(View.VISIBLE);
            holder.ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toPlayVideo(v, uri, type);
                }
            });
        }
        holder.ivZoomView.setImageViewFactory(new FrescoImageViewFactory());
        holder.ivZoomView.showImage(uri);
        holder.ivZoomView.setProgressIndicator(new ProgressPieIndicator());
        holder.ivZoomView.setInitScaleType(BigImageView.INIT_SCALE_TYPE_CENTER_INSIDE);

        holder.ivZoomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoClick();
            }
        });

        if (holder.ivZoomView.getSSIV() != null) {
            holder.ivZoomView.getSSIV().setOnStateChangedListener(new SubsamplingScaleImageView.OnStateChangedListener() {
                @Override
                public void onScaleChanged(float newScale, int origin) {
                    listener.onPhotoScaleChanged();
                }

                @Override
                public void onCenterChanged(PointF newCenter, int origin) {

                }
            });
        }


//
//        holder.ivZoomView.setOnScaleChangeListener(new OnScaleChangeListener() {
//            @Override
//            public void onScaleChange(float scaleFactor, float focusX, float focusY) {
//                listener.onPhotoScaleChanged();
//            }
//        });
    }

    private void toPlayVideo(View v, Uri uri, String type) {
        Context context = v.getContext();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, type);
        context.startActivity(intent);
    }

    private Uri getUri(Context context, String path, Intent intent) {
        File file = new File(path);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            return FileProvider.getUriForFile(context, Setting.fileProviderAuthority, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    public static class PreviewPhotosViewHolder extends RecyclerView.ViewHolder {
        public BigImageView ivZoomView;
        ImageView ivPlay;

        PreviewPhotosViewHolder(View itemView) {
            super(itemView);
            ivZoomView = itemView.findViewById(R.id.iv_zoom_view);
            ivPlay = itemView.findViewById(R.id.iv_play);
        }
    }
}
