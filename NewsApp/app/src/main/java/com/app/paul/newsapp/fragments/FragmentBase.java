package com.app.paul.newsapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.app.paul.newsapp.adapter.AdapterRvMainNews;
import com.app.paul.newsapp.News;
import com.app.paul.newsapp.data.NewsLoader;
import com.app.paul.newsapp.R;
import com.app.paul.newsapp.ShowNew;

import java.util.ArrayList;
import java.util.List;

import static com.app.paul.newsapp.adapter.AdapterFragmentPager.PATH_TO_NEWS;

public class FragmentBase extends Fragment implements AdapterRvMainNews.OnItemClickListener, LoaderManager.LoaderCallbacks<List<News>>  {

    public static final String SHOW_TITLE = "SHOW_TITLE";
    public static final String SHOW_BODY = "SHOW_BODY";
    public static final String SHOW_IMG = "SHOW_IMG";
    public static final String SHOW_WEB = "SHOW_WEB";
    public static final String SCROLL = "SCROLL";

    private List<News> newsList = new ArrayList<>();
    protected String path;
    protected RecyclerView recyclerNews;
    protected AdapterRvMainNews adapter;
    private ProgressBar progress;
    private Bundle customBundle;
    private Boolean isScrolling = false;
    private SearchView searchView;
    int currentItems;
    int totalItems;
    int scrollItems;
    private int cont = 1;
    private int cont2 = 1;
    private boolean isSearching = false;
    private boolean isNewData = false;
    private String initialQuery;

    public FragmentBase() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle fragmentBundle = getArguments();
        initialQuery = fragmentBundle.getString(PATH_TO_NEWS);

        View v = inflater.inflate(R.layout.fragment_base, container, false);
        progress = v.findViewById(R.id.progress_loading);
        recyclerNews = v.findViewById(R.id.recycler_main_mews);
        adapter = new AdapterRvMainNews(newsList, this);
        recyclerNews.setAdapter(adapter);
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerNews.setLayoutManager(manager);

        recyclerNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollItems = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollItems == totalItems) && !isSearching){
                    isScrolling = false;

                    path = path.replace(String.valueOf("page="+cont2).toString(),String.valueOf("page="+(cont2+1)).toString());
                    cont2++;
                    increaseCont();
                    path = path.replace("size=30","size=5");
                    recyclerNews.post(new Runnable() {
                        @Override
                        public void run() {
                            isNewData = true;
                            load(cont);
                        }
                    });
                    }
            }
        });

        buildString(initialQuery);
        load(0);

        return v;
    }

    @Override
    public void onItemClick(Integer position) {
        Intent intent = new Intent(getContext(), ShowNew.class);
        intent.putExtra(SHOW_TITLE, newsList.get(position).getHeadline());
        intent.putExtra(SHOW_BODY, newsList.get(position).getBody());
        intent.putExtra(SHOW_IMG, newsList.get(position).getThumnail());
        intent.putExtra(SHOW_WEB, newsList.get(position).getWeb());
        startActivity(intent);
    }

    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d("Query: ",path);
        return new NewsLoader(getContext(), path);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> data) {
        if(newsList.isEmpty()){
            newsList.addAll(data);
        }
        progress.setVisibility(View.INVISIBLE);

        if(customBundle != null) {
            recyclerNews.getLayoutManager().onRestoreInstanceState(customBundle.getParcelable(SCROLL));
        }
        if(isNewData){
            newsList.addAll(data);
            isNewData = false;
        }
        if(isSearching){
            newsList.clear();
            newsList.addAll(data);
            isSearching = false;
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        newsList.clear();
        progress.setVisibility(View.INVISIBLE);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(outState != null) {
            outState.putParcelable(SCROLL, recyclerNews.getLayoutManager().onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);

    }



    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        customBundle = savedInstanceState;
    }

    public void load(int id){
        ConnectivityManager connMgr = (ConnectivityManager)
                getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();


        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(id, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible


            // Update empty state with no connection error message


        }
    }

    public void buildString(String query){
        path = "https://content.guardianapis.com/search?&show-fields=bodyText%2Cheadline%2Csection%2Cthumbnail&page=1&page-size=30&q="+ query +"&api-key=819465fe-ccca-48b5-a3ca-af834bf6741e";
    }


    public void setSearching(boolean searching) {
        isSearching = searching;
    }

    public void increaseCont() {
        cont++;
    }

    public int getCont(){
        return cont;
    }
}
