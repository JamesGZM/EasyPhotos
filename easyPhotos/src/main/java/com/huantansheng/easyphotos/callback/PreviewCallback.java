package com.huantansheng.easyphotos.callback;

import com.huantansheng.easyphotos.models.album.entity.Photo;

public abstract class PreviewCallback {
    //点击下载
    public abstract void onDownload(Photo photo);
}
