package com.android.opcmpt.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.opcmpt.Config;
import com.android.opcmpt.R;
import com.android.opcmpt.callbacks.CallbackEventsDetails;
import com.android.opcmpt.models.Events;
import com.android.opcmpt.realm.RealmController;
import com.android.opcmpt.rests.ApiInterface;
import com.android.opcmpt.rests.RestAdapter;
import com.android.opcmpt.utils.AppBarLayoutBehavior;
import com.android.opcmpt.utils.NetworkCheck;
import com.android.opcmpt.utils.Tools;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityNotificationDetailEvents extends AppCompatActivity {

    TextView txt_title, txt_datainicial, txt_datafinal, txt_local;
    ImageView event_image;
    private WebView webview;
    long eid;
    CoordinatorLayout lyt_content;
    private MenuItem read_later_menu;
    private boolean flag_read_later;
    private Menu menu;
    View parent_view, lyt_parent, lyt_progress;
    Events events;
    private Call<CallbackEventsDetails> callbackCall = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        initToolbar();

        Intent intent = getIntent();
        eid = intent.getLongExtra("id", 0);

        parent_view = findViewById(android.R.id.content);
        lyt_parent = findViewById(R.id.lyt_parent);
        lyt_content = findViewById(R.id.lyt_content);
        lyt_progress = findViewById(R.id.lyt_progress);
        txt_title = findViewById(R.id.title);
        txt_datainicial = findViewById(R.id.data_inicial);
        txt_datafinal = findViewById(R.id.data_fim);
        txt_local = findViewById(R.id.evento_localevento);
        event_image = findViewById(R.id.image);
        webview = findViewById(R.id.news_description);

        requestAction();

    }

    private void initToolbar() {
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    private void requestAction() {
        showFailedView(false, "");
        requestDetailsPostApi();
    }

    private void requestDetailsPostApi() {
        ApiInterface apiInterface = RestAdapter.createAPI();
        callbackCall = apiInterface.getEventsDetail(eid);
        callbackCall.enqueue(new Callback<CallbackEventsDetails>() {
            @Override
            public void onResponse(Call<CallbackEventsDetails> call, Response<CallbackEventsDetails> response) {
                CallbackEventsDetails resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    events = resp.events;
                    if (Config.ENABLE_RTL_MODE) {
                        displayDataRTL();
                    } else {
                        displayData();
                    }
                } else {
                    onFailRequest();
                }
            }

            @Override
            public void onFailure(Call<CallbackEventsDetails> call, Throwable t) {
                if (!call.isCanceled()) onFailRequest();
            }

        });
    }

    private void onFailRequest() {
        if (NetworkCheck.isConnect(this)) {
            showFailedView(true, getString(R.string.msg_no_network));
        } else {
            showFailedView(true, getString(R.string.msg_offline));
        }
    }

    private void displayData() {
        txt_title.setText(Html.fromHtml(events.event_title));
        txt_datainicial.setText(Tools.getFormatedDate(events.event_datainicial));
        txt_datafinal.setText(Tools.getFormatedDate(events.event_datafinal));
        txt_local.setText(Html.fromHtml(events.event_localname));

        webview.setBackgroundColor(Color.parseColor("#ffffff"));
        webview.setFocusableInTouchMode(false);
        webview.setFocusable(false);
        if (!Config.ENABLE_TEXT_SELECTION) {
            webview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            webview.setLongClickable(false);
        }
        webview.getSettings().setDefaultTextEncodingName("UTF-8");
        webview.getSettings().setJavaScriptEnabled(true);

        WebSettings webSettings = webview.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = events.event_description;

        String text = "<html><head>"
                + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                + "<style type=\"text/css\">body{color: #000000;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Config.OPEN_LINK_INSIDE_APP) {
                    if (url.startsWith("http://")) {
                        Intent intent = new Intent(getApplicationContext(), ActivityWebView.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                    if (url.startsWith("https://")) {
                        Intent intent = new Intent(getApplicationContext(), ActivityWebView.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                    if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png")) {
                        Intent intent = new Intent(getApplicationContext(), ActivityWebViewImage.class);
                        intent.putExtra("image_url", url);
                        startActivity(intent);
                    }
                    if (url.endsWith(".pdf")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
                return true;
            }
        });

        webview.loadData(text, mimeType, encoding);

            Picasso.with(this)
                    .load(Config.ADMIN_PANEL_URL + "/upload/" + events.event_image.replace(" ", "%20"))
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(event_image);

            event_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ActivityFullScreenImage.class);
                    intent.putExtra("image", events.event_image);
                    startActivity(intent);
                }
            });
        }

    private void displayDataRTL() {
        txt_title.setText(Html.fromHtml(events.event_title));
        txt_datainicial.setText(Tools.getFormatedDate(events.event_datainicial));
        txt_datafinal.setText(Tools.getFormatedDate(events.event_datafinal));
        txt_local.setText(Html.fromHtml(events.event_localname));

        webview.setBackgroundColor(Color.parseColor("#ffffff"));
        webview.setFocusableInTouchMode(false);
        webview.setFocusable(false);
        if (!Config.ENABLE_TEXT_SELECTION) {
            webview.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            webview.setLongClickable(false);
        }
        webview.getSettings().setDefaultTextEncodingName("UTF-8");
        webview.getSettings().setJavaScriptEnabled(true);

        WebSettings webSettings = webview.getSettings();
        Resources res = getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = events.event_description;

        String text = "<html dir='rtl'><head>"
                + "<style>img{max-width:100%;height:auto;} figure{max-width:100%;height:auto;} iframe{width:100%;}</style> "
                + "<style type=\"text/css\">body{color: #000000;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Config.OPEN_LINK_INSIDE_APP) {
                    if (url.startsWith("http://")) {
                        Intent intent = new Intent(getApplicationContext(), ActivityWebView.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                    if (url.startsWith("https://")) {
                        Intent intent = new Intent(getApplicationContext(), ActivityWebView.class);
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                    if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png")) {
                        Intent intent = new Intent(getApplicationContext(), ActivityWebViewImage.class);
                        intent.putExtra("image_url", url);
                        startActivity(intent);
                    }
                    if (url.endsWith(".pdf")) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
                return true;
            }
        });

        webview.loadData(text, mimeType, encoding);

            Picasso.with(this)
                    .load(Config.ADMIN_PANEL_URL + "/upload/" + events.event_image.replace(" ", "%20"))
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(event_image);

            event_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), ActivityFullScreenImage.class);
                    intent.putExtra("image", events.event_image);
                    startActivity(intent);
                }
            });
        }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_events_detail, menu);
        this.menu = menu;
        read_later_menu = menu.findItem(R.id.action_later);
        refreshReadLaterMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_later:
                String str;
                if (flag_read_later) {
                    RealmController.with(this).deleteEvents(events.eid);
                    str = getString(R.string.favorite_removed);
                } else {
                    RealmController.with(this).saveEvents(events);
                    str = getString(R.string.favorite_added);
                }
                Snackbar.make(parent_view, str, Snackbar.LENGTH_SHORT).show();
                refreshReadLaterMenu();

                break;

            case R.id.action_share:

                String formattedString = Html.fromHtml(events.event_description).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, events.event_title + "\n" + formattedString + "\n" + getResources().getString(R.string.share_content) + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void refreshReadLaterMenu() {
        flag_read_later = RealmController.with(this).getEvents(eid) != null;
        if (flag_read_later) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_white));
        } else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_outline_white));
        }
    }

    private void showFailedView(boolean show, String message) {
        View lyt_failed = findViewById(R.id.lyt_failed_home);
        ((TextView) findViewById(R.id.failed_message)).setText(message);
        if (show) {
            lyt_content.setVisibility(View.GONE);
            lyt_failed.setVisibility(View.VISIBLE);
            lyt_progress.setVisibility(View.GONE);
        } else {
            lyt_content.setVisibility(View.VISIBLE);
            lyt_failed.setVisibility(View.GONE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    lyt_progress.setVisibility(View.GONE);
                }
            }, 1500);
        }
        findViewById(R.id.failed_retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestAction();
            }
        });
    }

}
