package com.duong3f.mvp.cropimage;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.duong3f.config.Config;
import com.duong3f.module.DuongLog;
import com.duong3f.module.MyEditCrop;
import com.group3f.gifmaker.R;
import com.long3f.activity.EditGifActivity;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.littlecheesecake.croplayout.EditableImage;
import me.littlecheesecake.croplayout.handler.OnBoxChangedListener;
import me.littlecheesecake.croplayout.model.ScalableBox;


/**
 * Created by d on 9/27/2017.
 */

public class CropVideoActivity extends AppCompatActivity {
    //  compile 'me.littlecheesecake:croplayout:1.0.5'
    @Bind(R.id.layout_tool_flip)
    LinearLayout layoutToolFlip;
    @Bind(R.id.layout_tool_ratio)
    LinearLayout layoutToolRatio;
    @Bind(R.id.layout_crop_edit_cropview)
    MyEditCrop layoutCropEditCropview;
    private String sizeCrop;
    private File currentFile;
    private File flagFile;
    private EditableImage editableImage;
    private ScalableBox scalableBox;

    /*
        - crop video, image, gif
        - command:
                ffmpeg.exe -i ic_app.png -filter:v "crop=223:264:233:131" out.png
             223:264 wight:height của đầu ra
             233:131 x:y của vị trí cắt




             1. Flip video  vertically:

ffmpeg -y -i INPUT -vf vflip  OUTPUT
2. Flip video horizontally:

ffmpeg -y  -i in-%03d.png -vf hflip  out-%03d.png
3. Rotate 90 degrees clockwise:

ffmpeg -y  -i INPUT -vf transpose=1 OUTPUT
4. Rotate 90 degrees counterclockwise:

ffmpeg -y  -i INPUT -vf transpose=2 OUTPUT
         */

    String pathFlag = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES) + "/GifEditorFlag/";
    String pathFile = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES) + "/GifEditor/";
    ArrayList<String> listFileFlag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_crop_video);
        ButterKnife.bind(this);
        listFileFlag = new ArrayList<>();
        currentFile = new File(EditGifActivity.currentPathFile);
        flagFile = new File(pathFlag + "/flag-" + String.format("%03d", (EditGifActivity.indexPathFile + 1)) + "." + FilenameUtils.getExtension(EditGifActivity.currentPathFile));
        if (!flagFile.exists()) {
            flagFile.getParentFile().mkdirs();
            try {
                flagFile.createNewFile();
            } catch (IOException e) {
            }
        }
        try {
            //String.format("%03d", (imagesSelect.indexOf(image) + 1))
            for (int i = 0; i < EditGifActivity.currentPathFiles.size(); i++) {
                File fileCopy = new File(pathFlag + "/flag-" + String.format("%03d", (i + 1)) + "." + FilenameUtils.getExtension(EditGifActivity.currentPathFile));
                Config.copy(new File(EditGifActivity.currentPathFiles.get(i)), fileCopy);
                listFileFlag.add(fileCopy.getPath());
            }
            DuongLog.e(getClass(), flagFile.getPath());
        } catch (IOException e) {
            DuongLog.e(getClass(), e.getMessage());
        }
        setImageToCropViewFromPath(flagFile.getPath());
    }

//    private void apply() {
//        for (int i = 0; i < listFileFlag.size(); i++) {
//            try {
//                Config.copy(new File(listFileFlag.get(i)), new File(EditGifActivity.currentPathFiles.get(i)));
//            } catch (IOException e) {
//
//            }
//        }
//    }

    public void setImageToCropViewFromPath(String pathFile) {
        editableImage = new EditableImage(pathFile);
        scalableBox = new ScalableBox(0, 0, 100, 200);
        editableImage.setBox(scalableBox);

        layoutCropEditCropview.initView(this, editableImage);
        layoutCropEditCropview.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
                sizeCrop = "" + (x2 - x1) + ":" + (y2 - y1) + ":" + x1 + ":" + y1 + "";
                DuongLog.e(getClass(), sizeCrop);
                DuongLog.e(getClass(), " " + editableImage.getOriginalImage().getWidth() + " x " + editableImage.getOriginalImage().getHeight());
                DuongLog.e(getClass(), " x1 = " + x1 + " y1 = " + y1 + " x2 = " + x2 + " y2 = " + y2);
                DuongLog.e(getClass(), " x1 = " + x1 + " y1 = " + y1 + " x2 = " + (y2 + x1) + " y2 = " + (y2 + y1));
                int w = editableImage.getOriginalImage().getWidth();
                int h = editableImage.getOriginalImage().getHeight();
                int duongCheo = (int) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
//                if ((y2 + x1) > editableImage.getOriginalImage().getWidth()) {
//                    x1 = (y2 + x1) - x1;
//                }
                int x22 = duongCheo + x1;
                int y22 = duongCheo + y1;
                layoutCropEditCropview.refeshSelectionViewScale(x1, y1, x22, y22);
            }
        });
    }

    public void setImageToCropViewFromDrawable(int id) {
        EditableImage image = new EditableImage(this, id);
        ScalableBox box = new ScalableBox(0, 0, image.getViewWidth() / 2, image.getViewHeight() / 2);
        image.setBox(box);
        layoutCropEditCropview.initView(this, image);
        layoutCropEditCropview.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
                sizeCrop = "" + (x2 - x1) + ":" + (y2 - y1) + ":" + x1 + ":" + y1 + "";
                DuongLog.e(getClass(), sizeCrop);
            }
        });
    }

    @OnClick({R.id.layout_crop_imv_ratio, R.id.layout_crop_layout_flip_horizontal, R.id.layout_crop_layout_flip_vertical, R.id.layout_crop_layout_rotate, R.id.layout_crop_layout_reset, R.id.layout_tool_flip, R.id.layout_crop_imv_ratio_free, R.id.layout_crop_imv_ratio_1_1, R.id.layout_crop_imv_ratio_3_4, R.id.layout_crop_imv_ratio_4_3, R.id.layout_crop_imv_ratio_9_16, R.id.layout_crop_imv_ratio_16_9, R.id.layout_tool_ratio, R.id.layout_crop_txt_cancel, R.id.layout_crop_txt_apply, R.id.layout_bottom, R.id.layout_crop_edit_cropview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_crop_imv_ratio:
                if (layoutToolFlip.getVisibility() == View.VISIBLE) {
                    layoutToolFlip.setVisibility(View.GONE);
                    layoutToolRatio.setVisibility(View.VISIBLE);
                } else {
                    layoutToolFlip.setVisibility(View.VISIBLE);
                    layoutToolRatio.setVisibility(View.GONE);
                }
                break;
            case R.id.layout_crop_layout_flip_horizontal:
                Config.runFFmpegCommandCallback(
                        Config.getCommandFlipHolizontal(flagFile.getPath(), flagFile.getPath()),
                        this,
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (Config.onSuccess == msg.what) {
                                    Toast.makeText(CropVideoActivity.this, "done getCommandFlipHolizontal", Toast.LENGTH_SHORT).show();
//                                    Picasso.with(CropVideoActivity.this).load(new File(flagFile.getPath())).into(layoutCropEditCropview.getImageView());
                                    layoutCropEditCropview.refeshView(flagFile.getPath());
                                }


                            }
                        }
                );

                break;
            case R.id.layout_crop_layout_flip_vertical:
                Config.runFFmpegCommandCallback(
                        Config.getCommandFlipVertical(flagFile.getPath(), flagFile.getPath()),
                        this,
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (Config.onSuccess == msg.what) {
//                                    Picasso.with(CropVideoActivity.this).load(new File(flagFile.getPath())).into(layoutCropEditCropview.getImageView());
                                    layoutCropEditCropview.refeshView(flagFile.getPath());
                                    layoutCropEditCropview.selectionView.updateOriginalBox();
                                    Toast.makeText(CropVideoActivity.this, "done getCommandFlipVertical", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                );
                break;
            case R.id.layout_crop_layout_rotate:

                Config.runFFmpegCommandCallback(
                        Config.getCommandRotation(flagFile.getParent() + "/flag-%03d." + FilenameUtils.getExtension(flagFile.getPath()), flagFile.getParent() + "/flag-%03d." + FilenameUtils.getExtension(flagFile.getPath())),
                        this,
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (Config.onSuccess == msg.what) {
                                    layoutCropEditCropview.rotateImageView();
                                    Toast.makeText(CropVideoActivity.this, "done getCommandRotation", Toast.LENGTH_SHORT).show();
//                                    Picasso.with(CropVideoActivity.this).load(new File(flagFile.getPath())).into(layoutCropEditCropview.getImageView());
                                    layoutCropEditCropview.refeshView(flagFile.getPath());
                                }
                            }

                        }
                );
                break;
            case R.id.layout_crop_layout_reset:

                break;
            case R.id.layout_tool_flip:

                break;
            case R.id.layout_crop_imv_ratio_free:

                break;
            case R.id.layout_crop_imv_ratio_1_1:

                break;
            case R.id.layout_crop_imv_ratio_3_4:

                break;
            case R.id.layout_crop_imv_ratio_4_3:

                break;
            case R.id.layout_crop_imv_ratio_9_16:

                break;
            case R.id.layout_crop_imv_ratio_16_9:

                break;
            case R.id.layout_tool_ratio:

                break;
            case R.id.layout_crop_txt_cancel:
                finish();
                break;
            case R.id.layout_crop_txt_apply:
                DuongLog.e(CropVideoActivity.this.getClass(), sizeCrop);
//                for (String str : listFileFlag) {
//                    DuongLog.e(getClass(), editableImage.cropOriginalImage(new File(str).getPath(), new File(EditGifActivity.currentPathFiles.get(listFileFlag.indexOf(str))).getName()));
//                }
                editableImage.cropOriginalImage(pathFlag, "abc.png");


                DuongLog.e(getClass(), "" + editableImage.getViewWidth());
                DuongLog.e(getClass(), "" + editableImage.getViewHeight());
                DuongLog.e(getClass(), "" + editableImage.getOriginalImage().getWidth());
                DuongLog.e(getClass(), "" + editableImage.getOriginalImage().getHeight());
//                scalableBox = new ScalableBox(0, 0, 150, 150);
//                editableImage.setBox(scalableBox);
                layoutCropEditCropview.refeshSelectionView(0, 0, 100, 300);
//                Config.runFFmpegCommandCallback(
//                        Config.getCommandCrop(flagFile.getParent() + "/flag-%03d." + FilenameUtils.getExtension(flagFile.getPath()), flagFile.getParent() + "/flag-%03d." + FilenameUtils.getExtension(flagFile.getPath()), sizeCrop),
//                        this, new Handler() {
//                            @Override
//                            public void handleMessage(Message msg) {
//                                if (Config.onSuccess == msg.what) {
//                                    Toast.makeText(CropVideoActivity.this, "done CommandCrop", Toast.LENGTH_SHORT).show();
//                                    finish();
//                                }
//                            }
//                        }
//                );
                break;
            case R.id.layout_crop_edit_cropview:
                break;
        }
    }
}
