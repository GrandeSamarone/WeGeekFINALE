package com.marlostrinidad.wegeek.nerdzone.Adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.marlostrinidad.wegeek.nerdzone.R;

import me.relex.photodraweeview.PhotoDraweeView;

public class Adapter_ampliar_Array_de_imagem extends PagerAdapter {
    private Context mContext;
    private String[] imageUrls;
    private LayoutInflater layoutInflater;


    public Adapter_ampliar_Array_de_imagem(Context context, String[] imageUrls) {
        this.mContext = context;
        this.imageUrls = imageUrls;
    }


    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.example_item, container, false);
        final PhotoDraweeView imageViewPreview = view.findViewById(R.id.image_view);
        final ProgressBar progressBar = view.findViewById(R.id.progressBarImag);
        progressBar.getIndeterminateDrawable().setColorFilter(0xFF0000FF,
                android.graphics.PorterDuff.Mode.MULTIPLY);
       PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
            controller.setUri(Uri.parse(imageUrls[position]));
            controller.setOldController(imageViewPreview.getController());
            controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                    super.onFinalImageSet(id, imageInfo, animatable);
                    if (imageInfo == null) {
                        progressBar.setVisibility(View.VISIBLE);
                        return;
                    }
                    imageViewPreview.update(imageInfo.getWidth(), imageInfo.getHeight());
                    progressBar.setVisibility(View.GONE);

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

