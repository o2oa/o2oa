package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 通用 RecyclerView adapter
 *
 * Created by FancyLou on 2015/12/14.
 */
public abstract class CommonRecycleViewAdapter<T> extends RecyclerView.Adapter<CommonRecyclerViewHolder> {

    private Context context;
    private List<T> datas;
    private LayoutInflater inflater;
    private int itemLayout;

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



    public CommonRecycleViewAdapter(Context context, List<T> datas, int itemLayout) {
        this.context = context;
        this.datas = datas;
        this.itemLayout = itemLayout;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public CommonRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(itemLayout, parent, false);
        CommonRecyclerViewHolder viewHolder = new CommonRecyclerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CommonRecyclerViewHolder holder, final int position) {
        //remind holder.getLayoutPosition() 如果datas数据和界面上面的item不一致的时候 有可能传入的position 和 LayoutPostion不一样
        convert(holder, getItem(position));
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
        if(onItemLongClickListner!=null){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListner.onItemLongClick(holder.itemView, position);
                    return false;
                }
            });
        }

    }


    public T getItem(int position) {
        return datas.get(position);
    }



    public abstract void convert(CommonRecyclerViewHolder holder, T t);
}
