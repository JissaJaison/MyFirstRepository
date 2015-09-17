package com.onbts.ITSMobile.UI.activities.base;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.model.FileModel;
import com.onbts.ITSMobile.services.DbService;

import java.util.ArrayList;

import util.SqliteImageDownloader;

public class ImageGalleryActivity extends Activity {
    private static final String STATE_POSITION = "STATE_POSITION";
    private ImageLoader imageLoader = ImageLoader.getInstance();
    //overlay text
    private TextView tvImageName, tvImageNumber;
    private DisplayImageOptions options;
//    private String[] filenames;
    private ViewPager pager;
    private ArrayList<FileModel> fileModels;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_image_gallery);
        tvImageName = (TextView) findViewById(R.id.tvImageNameOverlayed);
        tvImageNumber = (TextView) findViewById(R.id.tvImageNumberOverlayed);
        // Creating configuration for UIL, more info about config at
        // https://github.com/nostra13/Android-Universal-Image-Loader
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ImageGalleryActivity.this)
//                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
//                .imageDownloader(new SqliteImageDownloader(ImageGalleryActivity.this, DbService.getInstance(this).getIssutraxdb()))
//                .discCacheFileNameGenerator(new Md5FileNameGenerator())
//                .tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs().build();
//        imageLoader.init(config);
        //We send array of strings[urls] to activity through intent with key "images"
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;

        fileModels = bundle.getParcelableArrayList("images");
        int pagerPosition = bundle.getInt("currentPosition");

        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true).cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY).bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true).displayer(new FadeInBitmapDisplayer(300)).build();
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new ImagePagerAdapter());
        pager.setCurrentItem(pagerPosition);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tvImageName.setText(fileModels.get(position).getFilename());
                tvImageNumber.setText(position + 1 + "/" + fileModels.size());
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, pager.getCurrentItem());
    }

    //View pager adapter for UIL
    private class ImagePagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        ImagePagerAdapter() {
            inflater = getLayoutInflater();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return fileModels!=null ? fileModels.size() : 0;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_gallery, view, false);

            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.imgGalleryImage);
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.pbGalleryLoading);

            imageLoader.displayImage(fileModels.get(position).getPath(), imageView, options,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            spinner.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            String message = null;
                            switch (failReason.getType()) {
                                case IO_ERROR:
                                    message = "Input/Output error";
                                    break;
                                case DECODING_ERROR:
                                    message = "Image can't be decoded";
                                    break;
                                case NETWORK_DENIED:
                                    message = "Downloads are denied";
                                    break;
                                case OUT_OF_MEMORY:
                                    message = "Out Of Memory error";
                                    break;
                                case UNKNOWN:
                                    message = "Unknown error";
                                    break;
                            }
                            Toast.makeText(ImageGalleryActivity.this, message, Toast.LENGTH_SHORT).show();

                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            spinner.setVisibility(View.GONE);
                        }
                    }
            );

            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

}
