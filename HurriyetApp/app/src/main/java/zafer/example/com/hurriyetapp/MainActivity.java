package zafer.example.com.hurriyetapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private MaterialSearchView searchView;
    private RecyclerView recyclerView;
    private ArrayList<Article> arrayList = new ArrayList<Article>();
    private DataAdapter mAdapter;
    //private String json_string = Config.BaseUrl + "articles?apikey=" + Config.ApiKey + "&$top=";//ilk 10 makale
    private String json_string;
    //private String json_string = Config.BaseUrl + "articles?apikey=" + Config.ApiKey ;//ilk 10 makale
    private String TAG = "zms";
    protected Handler handler;
    private static String stringQuery;
    private boolean isQuery = false;
    //*******************************************//

    LinearLayoutManager linearLayoutManager;
    int moreNum = 2;
    private ItemTouchHelper mItemTouchHelper;
    int load = 10;
    ArrayList<String[]> imagesFileName = new ArrayList<String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        handler = new Handler();
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        imagesFileName.add(getResources().getStringArray(R.array.query_suggestions));
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));


        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Snackbar.make(findViewById(R.id.container), "Query: " + query, Snackbar.LENGTH_LONG)
                        .show();
                Log.d("zms dturr: ", " " + getResources().getStringArray(R.array.query_suggestions)[1] + " q: " + query);
                boolean isTrue = false;
                for (int i = 0; i < getResources().getStringArray(R.array.query_suggestions).length; i++) {

                    isTrue = getResources().getStringArray(R.array.query_suggestions)[i].equals(query);
                    if (isTrue == true) {
                        break;
                    }

                }
                if (isTrue) {
                    showToast("Haber getiriliyor...");
                    stringQuery = query;
                    load = 10;
                    GetContacts getContacts = new GetContacts(getApplicationContext());
                    getContacts.execute(query);
                    isQuery = true;
                } else {
                    showToast("Aradığınız kelimede haber bulunamadı");
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });

        loadArticle();

        //****************************************//


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }


    private class GetContacts extends AsyncTask<String, Article, Void> {
        private Context ctx;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        public GetContacts(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected Void doInBackground(String... arg0) {
            Log.e("zms 0-0", "eer");
            if (arg0.length != 0 && !arg0[0].substring(0, 1).equals("f") && arg0.length == 1) {
                // arrayList.removeAll(arrayList);
                Log.d("zms0-1", "sat:  " + arg0[0]);
                String filter = arg0[0];
                json_string = Config.BaseUrl + "articles?apikey=" + Config.ApiKey + "&$top=10&$filter=Path eq '/" + filter + "/'";

            } else if (!arg0[0].equals("") && arg0[0].substring(0, 1).equals("f") && arg0.length == 1) {
                String deget = arg0[0].substring(1, arg0[0].length());
                Log.d("zms0-3", "deg:  " + deget);
                //   arrayList.removeAll(arrayList);
                json_string = Config.BaseUrl + "articles?apikey=" + Config.ApiKey + "&$top=" + deget;
            } else if (!arg0[0].equals("") && arg0[1].substring(0, 1).equals("f") && arg0.length == 2) {
                String filter = arg0[0];
                String deget = arg0[1].substring(1, arg0[1].length());
                Log.d("zms0-2", "fillll:  " + filter + "degggg: " + deget);
                json_string = Config.BaseUrl + "articles?apikey=" + Config.ApiKey + "&$top=" + deget + "&$filter=Path eq '/" + filter + "/'";

            }
            Log.e("zms 0-4", "eer2");
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(json_string);

            //Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONArray jsonArray = new JSONArray(jsonStr);

                    arrayList.removeAll(arrayList);
                    if (jsonArray != null) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject c = jsonArray.getJSONObject(i);
                            if (c != null) {
                                //   Log.e("zms", "jsonOO:  " + c);
                                String id = c.getString("Id");
                                String contentType = c.getString("ContentType");
                                String createdDate = c.getString("CreatedDate");
                                String description = c.getString("Description");
                                String modifiedDate = c.getString("ModifiedDate");
                                String path = c.getString("Path");
                                String titleNews = c.getString("Title");
                                String url = c.getString("Url");
                                // Log.d("zms","url: "+ url);
                                // Phone node is JSON Object
                                JSONArray files = c.getJSONArray("Files");
                                //  Log.d("zms","files: "+ files+" siz: "+files.length());
                                String fileUrl = "";
                                if (files.length() != 0) {
                                    JSONObject joFiles = files.getJSONObject(0);
                                    //    Log.d("zms","joFiles: "+ joFiles);

                                    if (joFiles != null) {

                                        fileUrl = joFiles.getString("FileUrl");
                                    }
                                } else {

                                    fileUrl = "http://kadirli80ylcumhuriyetal.meb.k12.tr/meb_iys_dosyalar/80/04/957089/resimler/2012_12/26112310_yok.jpg";
                                }
                                //  String metadata = joFiles.getString("Metadata");
                                //String titleFile = joFiles.getString("Title");
                                Log.d("zms", "url: " + fileUrl);

                                Article article = new Article(titleNews, description, url.trim(), fileUrl);
                                publishProgress(article);


                            }
                        }
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Haber getirilirken bir hata oldu." + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Haber getirilirken bir hata oldu.Server'a bağlanamadık.",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Article... values) {

            arrayList.add(values[0]);
            mAdapter.notifyItemInserted(arrayList.size());
            mAdapter.notifyDataSetChanged();


            // }
            //  mAdapter.setLoaded();


        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();

        }
    }

    public void swipeRefresh() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                //    swipeRefreshLayout.setEnabled(topRowVerticalPosition >= 0);

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    public void loadArticle() {
        final GetContacts get = new GetContacts(getApplicationContext());
        get.execute("f10");
        isQuery = false;
        // LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        //
        Log.e("zms", "zms 2. giris");

        recyclerView.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(this);

        // use a linear layout manager
        recyclerView.setLayoutManager(linearLayoutManager);

        // create an Object for Adapter
        mAdapter = new DataAdapter(arrayList, recyclerView, this);

        // set the adapter object to the Recyclerview
        recyclerView.setAdapter(mAdapter);


        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                if (load < 50) {
                    arrayList.add(null);
                    mAdapter.notifyItemInserted(arrayList.size() - 1);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            load += 10;
                            Log.e("zms", "giris load: " + load + "is: " + isQuery);
                            if (!isQuery) {
                                GetContacts getContacts = new GetContacts(getApplicationContext());
                                getContacts.execute("f" + load);
                            } else {
                                GetContacts getContacts = new GetContacts(getApplicationContext());
                                getContacts.execute(stringQuery, "f" + load);
                                Log.d("zms", "fillll22:  " + stringQuery + " degggg222: " + "f" + load);

                            }
                            Log.e("zms", "true");
                            mAdapter.setLoaded();
                            //or you can add all at once but do not forget to call mAdapter.notifyDataSetChanged();
                        }
                    }, 1000);
                } else {
                    showToast("Görüntülenebilecek maksimum haber sayısına ulaştınız..");

                }
            }
        });
        swipeRefresh();
    }

    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

    }
}
