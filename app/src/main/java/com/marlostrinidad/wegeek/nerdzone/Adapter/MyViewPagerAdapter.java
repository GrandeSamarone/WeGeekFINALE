package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.marlostrinidad.wegeek.nerdzone.Model.Foto;
import com.marlostrinidad.wegeek.nerdzone.R;

import java.util.ArrayList;

import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by fulanoeciclano on 30/06/2018.
 */

public class MyViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<Foto> mExampleList;
    private LayoutInflater layoutInflater;


    public MyViewPagerAdapter(Context context, ArrayList<Foto> fotoArrayList){
        this.mContext=context;
        this.mExampleList=fotoArrayList;
    }




    public int getCount() {
        return mExampleList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.example_item, container, false);
        final PhotoDraweeView imageViewPreview =  view.findViewById(R.id.image_view);
        Foto image = mExampleList.get(position);
        String imageUrl = image.getUrl();
        String erro= String.valueOf((R.drawable.gradiente_icone));

       /* Glide.with(mContext).load(image.getUrl())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageViewPreview);*/

            PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setUri(Uri.parse(imageUrl));
            controller.setOldController(imageViewPreview.getController());
            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    // int width = 400, height = 400;
                    if (imageInfo == null ) {

                        return;
                    }
                    imageViewPreview.update(imageInfo.getWidth(), imageInfo.getHeight());
                }
            });
            imageViewPreview.setController(controller.build());

        container.addView(view);

        return view;
    }
    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == ((View) obj);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}

