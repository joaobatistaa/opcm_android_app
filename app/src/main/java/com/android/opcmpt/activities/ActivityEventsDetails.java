package com.android.opcmpt.activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.opcmpt.Config;
import com.android.opcmpt.R;
import com.android.opcmpt.models.Events;
import com.android.opcmpt.realm.RealmController;
import com.android.opcmpt.utils.AppBarLayoutBehavior;
import com.android.opcmpt.utils.Constant;
import com.android.opcmpt.utils.GDPR;
import com.android.opcmpt.utils.Tools;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ActivityEventsDetails extends AppCompatActivity {

    public static final String EXTRA_OBJC = "key.EXTRA_OBJC";

    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionView, Events obj) {
        Intent intent = new Intent(activity, ActivityEventsDetails.class);
        intent.putExtra(EXTRA_OBJC, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionView, EXTRA_OBJC);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    private List<Events> items = new ArrayList<>();
    private MenuItem read_later_menu;
    private Events events;
    private boolean flag_read_later;
    RelativeLayout thumb3;
    View parent_view, lyt_parent;
    private Menu menu;
    TextView txt_title, txt_datainicial, txt_datafinal, txt_local, txt_website;
    private WebView webview;
    private AdView adView;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_detail);

        if (Config.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).setBehavior(new AppBarLayoutBehavior());

        parent_view = findViewById(android.R.id.content);
        webview = findViewById(R.id.news_description);
        lyt_parent = findViewById(R.id.lyt_parent);

        txt_title = findViewById(R.id.evento_title2);
        txt_datainicial = findViewById(R.id.evento_datainicio2);
        txt_datafinal = findViewById(R.id.evento_datafinal2);
        txt_local = findViewById(R.id.ev_localevento);
        txt_website = findViewById(R.id.evento_website2);


        ViewCompat.setTransitionName(findViewById(R.id.image), EXTRA_OBJC);

        events = (Events) getIntent().getSerializableExtra(EXTRA_OBJC);

        initToolbar();

        if (Config.ENABLE_RTL_MODE) {
            displayDataRTL();
        } else {
            displayData();
        }

        loadBannerAd();
        loadInterstitialAd();
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

    private void displayData() {
        txt_title.setText(Html.fromHtml(events.event_title));
        txt_local.setText(Html.fromHtml(events.event_localname));
        txt_datainicial.setText(Html.fromHtml(events.event_datainicial));
        txt_datainicial.setText(Tools.getFormatedDate(events.event_datainicial));
        txt_datafinal.setText(Tools.getFormatedDate(events.event_datafinal));
        txt_website.setText(Html.fromHtml(events.event_moreinfo));

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

        ImageView event_image = findViewById(R.id.image);
        ImageView event_image2 = findViewById(R.id.image5);

        Picasso.with(this)
                .load(Config.ADMIN_PANEL_URL + "/upload/" + events.event_image.replace(" ", "%20"))
                .placeholder(R.drawable.ic_thumbnail)
                .into(event_image);

        event_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityFullScreenImage.class);
                intent.putExtra("image", events.event_image);
                startActivity(intent);
            }
        });



        if (events.event_video.equals("")) {
            thumb3 = findViewById(R.id.thumb3);
            thumb3.setVisibility(View.GONE);
        } else {
            thumb3 = findViewById(R.id.thumb3);
            thumb3.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(Constant.YOUTUBE_IMG_FRONT + events.event_videoid + Constant.YOUTUBE_IMG_BACK)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(event_image2);

            event_image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ActivityYoutubePlayer.class);
                    intent.putExtra("video_id", events.event_videoid);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_events_detail, menu);
        this.menu = menu;
        read_later_menu = menu.findItem(R.id.action_later);
        refreshReadLaterMenu();
        return true;

    }

    private void displayDataRTL() {
        txt_title.setText(Html.fromHtml(events.event_title));
        txt_local.setText(Html.fromHtml(events.event_localname));
        txt_datainicial.setText(Html.fromHtml(events.event_datainicial));
        txt_datafinal.setText(Html.fromHtml(events.event_datafinal));
        txt_website.setText(Html.fromHtml(events.event_moreinfo));

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
        ImageView event_image = findViewById(R.id.image);

        Picasso.with(this)
                .load(Constant.YOUTUBE_IMG_FRONT + events.event_videoid + Constant.YOUTUBE_IMG_BACK)
                .placeholder(R.drawable.ic_thumbnail)
                .into(event_image);

        if (events.event_image != null) {

            Picasso.with(this)
                    .load(Config.ADMIN_PANEL_URL + "/upload/" + events.event_image.replace(" ", "%20"))
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(event_image);

            event_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ActivityFullScreenImage.class);
                    intent.putExtra("image", events.event_image);
                    startActivity(intent);
                }
            });
        }
        ImageView event_image2 = findViewById(R.id.image5);
        if (events.event_video != "") {
            thumb3 = findViewById(R.id.thumb3);
            thumb3.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(Constant.YOUTUBE_IMG_FRONT + events.event_videoid + Constant.YOUTUBE_IMG_BACK)
                    .placeholder(R.drawable.ic_thumbnail)
                    .into(event_image2);

            event_image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), ActivityYoutubePlayer.class);
                    intent.putExtra("video_id", events.event_videoid);
                    startActivity(intent);
                }
            });
        }else{
            thumb3 = findViewById(R.id.thumb3);
            thumb3.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.action_later:

                if (events.isDraft()) {
                    Snackbar.make(parent_view, R.string.cannot_add_to_favorite, Snackbar.LENGTH_SHORT).show();
                    return true;
                }
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

                String formattedString = android.text.Html.fromHtml(events.event_description).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, events.event_title + "\n" + formattedString );//+ "\n" + getResources().getString(R.string.share_content) + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void refreshReadLaterMenu() {
        flag_read_later = RealmController.with(this).getEvents(events.eid) != null;
        if (flag_read_later) {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_white));
        } else {
            menu.getItem(0).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_outline_white));
        }
    }

    public void loadBannerAd() {
        if (Config.ENABLE_ADMOB_BANNER_ADS) {
            adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(ActivityEventsDetails.this)).build();
            adView.loadAd(adRequest);
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdFailedToLoad(int error) { adView.setVisibility(View.GONE); }


                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdLoaded() { adView.setVisibility(View.VISIBLE); }

            });

        }
    }

    private void loadInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS_ON_CLICK_VIDEO) {
            interstitialAd = new InterstitialAd(getApplicationContext());
            interstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_unit_id));
            AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, GDPR.getBundleAd(ActivityEventsDetails.this)).build();
            interstitialAd.loadAd(adRequest);
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
        }
    }

    private void showInterstitialAd() {
        if (Config.ENABLE_ADMOB_INTERSTITIAL_ADS_ON_CLICK_VIDEO) {
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                interstitialAd.show();
            }
        }
    }

}