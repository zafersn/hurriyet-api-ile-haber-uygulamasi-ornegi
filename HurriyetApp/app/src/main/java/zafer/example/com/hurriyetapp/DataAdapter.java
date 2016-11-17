package zafer.example.com.hurriyetapp;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.Collections;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private Activity activity;
    List<Article> articles = Collections.emptyList();

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 1;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    public ImageLoader imageLoader;


    public DataAdapter(List<Article> students, RecyclerView recyclerView, Activity activity) {
        this.articles = students;
        this.activity = activity;

        Log.e("zms", "zms 1");
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            Log.e("zms", "zms 2");
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            Log.e("zms", "zms 3");
            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            //Log.e("zms","zms 4");


                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            //Log.e("zms","zms 5");
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                //Log.e("zms","zms 6");
                                // Do something
                                if (onLoadMoreListener != null) {
                                    //		Log.e("zms","zms 7");
                                    onLoadMoreListener.onLoadMore();
                                    //	Log.e("zms","zms 8");
                                }
                                loading = true;
                                //	Log.e("zms","zms 9");
                            }

                        }
                    });
        }

    }

    @Override
    public int getItemViewType(int position) {
        return articles.get(position) != null ? VIEW_ITEM : VIEW_PROG;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        try {


            RecyclerView.ViewHolder vh;
            if (viewType == VIEW_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.list_item, parent, false);

                vh = new StudentViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.custom_bottom_progressbar, parent, false);

                vh = new ProgressViewHolder(v);
            }
            return vh;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StudentViewHolder) {
            Article singleArticle = (Article) articles.get(position);
            ((StudentViewHolder) holder).tv_title.setText(articles.get(position).getTitle());
            ((StudentViewHolder) holder).tv_description.setText(articles.get(position).getDescription());
            ((StudentViewHolder) holder).tv_url.setText(articles.get(position).getUrl());
            ((StudentViewHolder) holder).tv_url.setTextColor(Color.RED);
            //  Log.d("zms00","desc: "+articles.get(position).getDescription()+" url: "+articles.get(position).getUrl());
            ((StudentViewHolder) holder).article = singleArticle;

            //hyperlink
            ((StudentViewHolder) holder).tv_url.setMovementMethod(LinkMovementMethod.getInstance());

            imageLoader = CustomVolleyRequest.getInstance(activity.getApplicationContext()).getImageLoader();
            imageLoader.get(articles.get(position).getFileUrl().toString(), ImageLoader.getImageListener(((StudentViewHolder) holder).networkImageView,
                    R.mipmap.ic_launcher,
                    R.mipmap.ic_launcher));

            ((StudentViewHolder) holder).networkImageView.setImageUrl(articles.get(position).getFileUrl(), imageLoader);
        } else {
            Log.e("zms", "progress");
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }


    //
    public class StudentViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_title, tv_description, tv_url;
        public NetworkImageView networkImageView;
        public Article article;

        public StudentViewHolder(View v) {
            super(v);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            tv_url = (TextView) itemView.findViewById(R.id.tv_url);
            networkImageView = (NetworkImageView) itemView.findViewById(R.id.Picture);

            v.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(),
                            "OnClick :" + article.getTitle() + " \n\n" + article.getDescription(),
                            Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    public class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.bottom_progress_bar);
            progressBar.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        }
    }
}