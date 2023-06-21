package com.alphacircle.filereceiverapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private List<FileInfo> files;
    private Context context;

    public GalleryAdapter(Context context) {
        this.context = context;
        files = new ArrayList<>();
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FileInfo fileInfo = files.get(position);
        holder.bind(fileInfo);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView itemImageView;
        private TextView fileNameTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            fileNameTextView = itemView.findViewById(R.id.fileNameTextView);
        }

        public void bind(FileInfo fileInfo) {
            fileNameTextView.setText(fileInfo.getFileName());

            if (fileInfo.getFileType().equals("image")) {
                loadImageFromServer(fileInfo.getFileName(), fileInfo.getFileType(), itemImageView);
            } else {
                int iconResId = getFileTypeIcon(fileInfo.getFileType());
                itemImageView.setImageResource(iconResId);
            }
        }

        private void loadImageFromServer(String fileName, String fileType, ImageView imageView) {
            // 애뮬레이터의 로컬 호스트 IP는 127.0.0.1, 때문에 서버 URL을 따로 표기해 주어야 함.
            String imageUrl = "http://121.133.180.56:4000/file/" + fileName + "?type=" + fileType;

            ImageLoadTask imageLoadTask = new ImageLoadTask(imageView);
            imageLoadTask.execute(imageUrl);
        }

        private int getFileTypeIcon(String fileType) {
            int iconResId;

            switch (fileType) {
                case "image":
                    iconResId = R.drawable.icon_image;
                    break;
                case "text":
                    iconResId = R.drawable.icon_text;
                    break;
                case "zip":
                    iconResId = R.drawable.icon_zip;
                    break;
                default:
                    iconResId = R.drawable.icon_file;
                    break;
            }

            return iconResId;
        }
    }
}

