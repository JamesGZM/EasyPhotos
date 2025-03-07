package com.huantansheng.easyphotos.ui.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.huantansheng.easyphotos.R;
import com.huantansheng.easyphotos.constant.Type;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.huantansheng.easyphotos.setting.Setting;
import com.huantansheng.easyphotos.utils.media.DurationUtils;

import java.util.ArrayList;

/**
 * 拼图相册适配器
 * Created by huan on 2017/10/23.
 */

public class PuzzleSelectorAdapter extends RecyclerView.Adapter {


    private ArrayList<Photo> dataList;
    private LayoutInflater mInflater;
    private OnClickListener listener;


    public PuzzleSelectorAdapter(Context cxt, ArrayList<Photo> dataList, OnClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
        this.mInflater = LayoutInflater.from(cxt);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new PhotoViewHolder(mInflater.inflate(R.layout.item_puzzle_selector_easy_photos, parent, false));

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final int p = position;
        Photo photo = dataList.get(position);
        String path = photo.path;
        String type = photo.type;
        Uri uri = photo.uri;
        long duration = photo.duration;
        final boolean isGif = path.endsWith(Type.GIF) || type.endsWith(Type.GIF);
        if (Setting.showGif && isGif) {
            ((PhotoViewHolder) holder).tvType.setText(R.string.gif_easy_photos);
            ((PhotoViewHolder) holder).tvType.setVisibility(View.VISIBLE);
        } else if (Setting.showVideo && type.contains(Type.VIDEO)) {
            ((PhotoViewHolder) holder).tvType.setText(DurationUtils.format(duration));
            ((PhotoViewHolder) holder).tvType.setVisibility(View.VISIBLE);
        } else {
            ((PhotoViewHolder) holder).tvType.setVisibility(View.GONE);
        }

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .setOldController(((PhotoViewHolder) holder).ivPhoto.getController())
                .build();
        ((PhotoViewHolder) holder).ivPhoto.setController(controller);

        ((PhotoViewHolder) holder).ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoClick(p);
            }
        });
    }


    @Override
    public int getItemCount() {
        return null == dataList ? 0 : dataList.size();
    }


    public interface OnClickListener {
        void onPhotoClick(int position);
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView ivPhoto;
        TextView tvType;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            this.ivPhoto = (SimpleDraweeView) itemView.findViewById(R.id.iv_photo);
            this.tvType = (TextView) itemView.findViewById(R.id.tv_type);
        }
    }
}
