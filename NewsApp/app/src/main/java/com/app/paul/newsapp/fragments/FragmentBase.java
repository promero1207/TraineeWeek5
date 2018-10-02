package com.app.paul.newsapp.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.app.paul.newsapp.News;
import com.app.paul.newsapp.R;
import com.app.paul.newsapp.ShowNew;
import com.app.paul.newsapp.adapter.AdapterRvMainNews;
import com.app.paul.newsapp.data.NewsLoader;

import java.util.ArrayList;
import java.util.List;

import static com.app.paul.newsapp.adapter.AdapterFragmentPager.PATH_TO_NEWS;

public class FragmentBase extends Fragment implements AdapterRvMainNews.OnItemClickListener, LoaderManager.LoaderCallbacks<List<News>>  {

    //constants
    public static final String SHOW_TITLE = "SHOW_TITLE";
    public static final String SHOW_BODY = "SHOW_BODY";
    public static final String SHOW_IMG = "SHOW_IMG";
    public static final String SHOW_WEB = "SHOW_WEB";
    public static final String SCROLL = "SCROLL";
    public static final String ID = "ID";

    //fields
    private List<News> newsList = new ArrayList<>();
    protected String path;
    protected RecyclerView recyclerNews;
    protected AdapterRvMainNews adapter;
    private ProgressBar progress;
    private Bundle customBundle;
    private Boolean isScrolling = false;
    int currentItems;
    int totalItems;
    int scrollItems;
    private int cont = 1;
    private int cont2 = 1;
    private boolean isSearching = false;
    private boolean isNewData = false;
    private ImageView wifi;


    //constructor
    public FragmentBase() {
    }

    /**
     * Method for creating view
     * @param inflater inflater
     * @param container container of the view
     * @param savedInstanceState Bundle when conf has changed
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle fragmentBundle = getArguments();

        String initialQuery = "";
        if(fragmentBundle != null) {
            initialQuery = fragmentBundle.getString(PATH_TO_NEWS);
        }

        View v = inflater.inflate(R.layout.fragment_base, container, false);

        wifi = v.findViewById(R.id.wifi);

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

            //recycler view method to check when reached last item
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = manager.getChildCount();
                totalItems = manager.getItemCount();
                scrollItems = manager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollItems == totalItems) && !isSearching){
                    isScrolling = false;

                    path = path.replace(String.valueOf("page="+cont2),String.valueOf("page="+(cont2+1)));
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

        if(savedInstanceState == null){
            load(0);
        }
        else {
            isSearching = true;
            load(savedInstanceState.getInt(ID));
            cont++;

        }

        return v;
    }



    //Method for on click event of recyclerview
    @Override
    public void onItemClick(Integer position) {
        Intent intent = new Intent(getContext(), ShowNew.class);
        intent.putExtra(SHOW_TITLE, newsList.get(position).getHeadline());
        intent.putExtra(SHOW_BODY, newsList.get(position).getBody());
        intent.putExtra(SHOW_IMG, newsList.get(position).getThumnail());
        intent.putExtra(SHOW_WEB, newsList.get(position).getWeb());
        startActivity(intent);
    }

    /**
     * Loader method when creating loader
     * @param id id to diference between calls
     * @param args arguments extras
     * @return Loader
     */
    @NonNull
    @Override
    public Loader<List<News>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.d("Query: ",path);
        return new NewsLoader(getContext(), path);
    }

    /**
     * Method called whe load of data finishes
     * @param loader load
     * @param data data returned of loading
     */
    @Override
    public void onLoadFinished(@NonNull Loader<List<News>> loader, List<News> data) {

        progress.setVisibility(View.INVISIBLE);

        if(data != null) {
            wifi.setVisibility(View.GONE);
            if (newsList.isEmpty()) {
                newsList.addAll(data);
            }

            if (isNewData) {
                newsList.addAll(data);
                isNewData = false;
            }
            if (isSearching) {
                newsList.clear();
                newsList.addAll(data);
                isSearching = false;
            }

            adapter.notifyDataSetChanged();
        }
        else {
            wifi.setVisibility(View.VISIBLE);
        }
    }

    /**
     * method called when reset on load is made
     * @param loader loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader<List<News>> loader) {
        newsList.clear();
        progress.setVisibility(View.INVISIBLE);
        adapter.notifyDataSetChanged();
    }

    /**
     * method for saving state
     * @param outState bundle outstate
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(recyclerNews.getLayoutManager() != null) {
            //saving scroll of recycler
            outState.putParcelable(SCROLL, recyclerNews.getLayoutManager().onSaveInstanceState());
        }
        outState.putInt(ID,getCont() -1);
        super.onSaveInstanceState(outState);
    }


    /**
     * restoring instance
     * @param savedInstanceState bundle of restored state
     */
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        customBundle = savedInstanceState;
    }

    /**
     * method for calling loader
     * @param id id of the loader call, it must be diferent on each call for new data
     */
    public void load(int id){
        ConnectivityManager connMgr;
        if (getContext() != null) {
            connMgr = (ConnectivityManager)
                    getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connMgr != null) {
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    // Get a reference to the LoaderManager, in order to interact with loaders.
                    LoaderManager loaderManager = getLoaderManager();

                    // Initialize the loader. Pass in the int ID constant defined above and pass in null for
                    // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
                    // because this activity implements the LoaderCallbacks interface).
                    loaderManager.initLoader(id, null, this);
                }
            }


        }

    }

    /**
     * method for building query
     * @param query query string to be searched
     */
    public void buildString(String query){
        path = "https://content.guardianapis.com/search?&show-fields=bodyText%2Cheadline%2Csection%2Cthumbnail&page=1&page-size=30&q="+ query +"&api-key=819465fe-ccca-48b5-a3ca-af834bf6741e";
    }

    //setter and getters
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
