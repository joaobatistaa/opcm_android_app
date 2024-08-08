package com.android.opcmpt.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.opcmpt.Config;
import com.android.opcmpt.R;
import com.android.opcmpt.models.Events;
import com.android.opcmpt.utils.Tools;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdapterEvents extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_PROG = 0;
    private final int VIEW_ITEM = 1;

    private List<Events> items = new ArrayList<>();

    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Events obj, int position);
    }

    public void setmOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterEvents(Context context, RecyclerView view, List<Events> items) {
        this.items = items;
        ctx = context;
        lastItemViewDetector(view);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView datainicial;
        public TextView datafinal;
        public TextView localdoevento;
        public ImageView image;
        public LinearLayout lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.title);
            datainicial = v.findViewById(R.id.data_inicial);
            datafinal = v.findViewById(R.id.data_fim);
            localdoevento = v.findViewById(R.id.localdoevento);
            image = v.findViewById(R.id.image);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar1);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_events, parent, false   );
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_load_more, parent, false);
            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof OriginalViewHolder) {
            final Events e = items.get(position);
            OriginalViewHolder vItem = (OriginalViewHolder) holder;

            vItem.title.setText(Html.fromHtml(e.event_title));

            if(Config.ENABLE_DATE_TIME_AGO) {
                PrettyTime prettyTime = new PrettyTime();
                long timeAgo = Tools.timeStringtoMilis(e.event_datainicial);
                vItem.datainicial.setText(prettyTime.format(new Date(timeAgo)));
            } else {
                vItem.datainicial.setText(Tools.getFormatedDateSimple(e.event_datainicial));
            }

            if(Config.ENABLE_DATE_TIME_AGO) {
                PrettyTime prettyTime = new PrettyTime();
                long timeAgo = Tools.timeStringtoMilis(e.event_datafinal);
                vItem.datafinal.setText(prettyTime.format(new Date(timeAgo)));
            } else {
                vItem.datafinal.setText(Tools.getFormatedDateSimple(e.event_datafinal));
            }

            Picasso.with(ctx)
                    .load(Config.ADMIN_PANEL_URL + "/upload/" + e.event_image.replace(" ", "%20"))
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(vItem.image);

            vItem.localdoevento.setText(Html.fromHtml(e.event_localname));

            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, e, position);
                    }
                }
            });
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(items.get(position) != null) {
            return VIEW_ITEM;
        } else {
            return VIEW_PROG;
        }
    }

    public void insertData(List<Events> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void  setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        if (onLoadMoreListener != null) {
                            int current_page = getItemCount() / Config.LOAD_MORE;
                            onLoadMoreListener.onLoadMore(current_page);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }



}