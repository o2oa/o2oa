package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by FancyLou on 2015/12/23.
 */
public abstract class SwipeRefreshCommonRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_VIEW_TYPE = 0;
    private static final int FOOTER_VIEW_TYPE =1;


    private Context context;
    private List<T> datas;
    private LayoutInflater inflater;
    private int itemLayout;
    private View footer;

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListner onItemLongClickListner;




    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListner {
        void onItemLongClick(View view, int position);
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListner(OnItemLongClickListner onItemLongClickListner) {
        this.onItemLongClickListner = onItemLongClickListner;
    }


    public SwipeRefreshCommonRecyclerViewAdapter(Context context, List<T> datas, int itemLayout) {
        this.context = context;
        this.datas = datas;
        this.itemLayout = itemLayout;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == ITEM_VIEW_TYPE){
            View view = inflater.inflate(itemLayout, parent, false);
            return new CommonRecyclerViewHolder(view);
        }else{
            return new FooterViewHolder(footer);
        }

    }

    @Override
    public void onBindViewHolder( final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof CommonRecyclerViewHolder) {
            final CommonRecyclerViewHolder commonRecyclerViewHolder = (CommonRecyclerViewHolder) holder;
            convert(commonRecyclerViewHolder, getItem(position));
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(commonRecyclerViewHolder.itemView, position));
            }
            if(onItemLongClickListner!=null){
                holder.itemView.setOnLongClickListener(v -> {
                    onItemLongClickListner.onItemLongClick(commonRecyclerViewHolder.itemView, position);
                    return false;
                });
            }
        }else{
            //TODO ?底部 不需要处理。。。

        }

    }

    @Override
    public int getItemCount() {
        return datas.size() +  getFooterCount();
    }

    @Override
    public int getItemViewType(int position) {
        if(getFooterCount()>0){
            if((position + 1)==getItemCount()){
                return FOOTER_VIEW_TYPE;
            }else{
                return ITEM_VIEW_TYPE;
            }
        }else{
            return ITEM_VIEW_TYPE;
        }
    }


    public T getItem(int position) {
        return datas.get(position);
    }

    public abstract void convert(CommonRecyclerViewHolder holder, T t);
    /**
     * 添加底部
     * @param view
     */
    public void addFooter(View view) {
        footer = view;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // 刷新操作
                notifyDataSetChanged();
            }
        });
    }

    /**
     * 删除头部
     * @param view
     */
    public void removeFooter(View view) {
        footer = null;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                // 刷新操作
                notifyDataSetChanged();
            }
        });
    }


    private int getFooterCount() {
        if (footer!=null){
            return 1;
        }
        return 0;
    }



    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View view) {
            super(view);
        }

    }

}
