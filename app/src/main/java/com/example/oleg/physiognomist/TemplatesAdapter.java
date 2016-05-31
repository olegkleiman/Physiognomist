package com.example.oleg.physiognomist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Oleg on 31-May-16.
 */
public class TemplatesAdapter extends RecyclerView.Adapter<TemplatesAdapter.TemplateHolder>{

    private List<FacePartTemplate>  mItems;
    private Context                 mContext;

    public TemplatesAdapter(Context context, List<FacePartTemplate> objects) {
        mItems = objects;
        mContext = context;
    }

    @Override
    public TemplateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.template_item_row, parent, false);
        return new TemplateHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TemplateHolder holder, int position) {

        FacePartTemplate template = mItems.get(position);
        holder.templateText.setText(template.getText());
        holder.templateImage.setImageResource(template.getImageId());

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class TemplateHolder extends RecyclerView.ViewHolder{

        public ImageView templateImage;
        public TextView     templateText;

        public TemplateHolder(View view) {
            super(view);

            templateImage = (ImageView)view.findViewById(R.id.image_template);
            templateText = (TextView)view.findViewById(R.id.text_template);
        }
    }

}
