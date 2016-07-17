package mx.hercarr.galileox.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import mx.hercarr.galileox.R;
import mx.hercarr.galileox.util.Constants;
import mx.hercarr.galileox.util.ImageLoaderUtils;

public class PhotoDetailActivity extends AppCompatActivity {

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.imgPhoto)
    ImageView imgPhoto;
    @BindView(R.id.lblDownloads)
    TextView lblDownloads;
    @BindView(R.id.lblLikes)
    TextView lblLikes;

    private String pageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        ButterKnife.bind(this);
        setupToolbar();
        renderDetails();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupToolbar() {
        toolbar.setTitle(getString(R.string.label_photo_detail_loading));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void renderDetails() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            String imageUrl = getIntent().getStringExtra(Constants.IntentExtras.PHOTO_IMAGE_URL);
            String tags = getIntent().getStringExtra(Constants.IntentExtras.PHOTO_TAGS);
            Integer downloads = getIntent().getIntExtra(Constants.IntentExtras.PHOTO_DOWNLOADS, 0);
            Integer likes = getIntent().getIntExtra(Constants.IntentExtras.PHOTO_LIKES, 0);
            ImageLoaderUtils.loadImageDetail(PhotoDetailActivity.this, imgPhoto, imageUrl);
            pageUrl = getIntent().getStringExtra(Constants.IntentExtras.PHOTO_PAGE_URL);
            collapsingToolbarLayout.setTitle(tags);
            lblDownloads.setText(String.format(getString(R.string.label_photo_detail_downloads), downloads));
            lblLikes.setText(String.format(getString(R.string.label_photo_detail_likes), likes));
        }
    }

    @OnClick(R.id.fabShare)
    public void share() {
        Bitmap bitmap;
        if (imgPhoto.getDrawable() instanceof GlideBitmapDrawable) {
            bitmap = ((GlideBitmapDrawable) imgPhoto.getDrawable()).getBitmap();
        } else if (imgPhoto.getDrawable() instanceof BitmapDrawable) {
            bitmap = ((BitmapDrawable) imgPhoto.getDrawable()).getBitmap();
        } else {
            imgPhoto.buildDrawingCache();
            bitmap = imgPhoto.getDrawingCache();
        }

        if (bitmap != null) {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("image/jpeg");
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null);
            Uri imageUri = Uri.parse(path);
            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(share, getString(R.string.title_photo_share)));
        }
        imgPhoto.destroyDrawingCache();
    }

    @OnClick(R.id.btnWebSite)
    public void openBrowser() {
        Uri uri = Uri.parse(pageUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}