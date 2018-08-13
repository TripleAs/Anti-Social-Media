package com.example.arafatm.anti_socialmedia.Story;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.Fragments.PictureFragment;
import com.example.arafatm.anti_socialmedia.Fragments.VideoFragment;
import com.example.arafatm.anti_socialmedia.Home.MainActivity;
import com.example.arafatm.anti_socialmedia.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.arafatm.anti_socialmedia.Fragments.GroupFeedFragment.selected;

public class PreviewStoryActivity extends AppCompatActivity implements PictureFragment.OnFragmentInteractionListener, VideoFragment.OnFragmentInteractionListener {
    @BindView(R.id.iv_camera)
    ImageButton backToCamera;
    @BindView(R.id.iv_share)
    ImageButton shareButton;
    @BindView(R.id.iv_close)
    ImageButton closeToCamera;
    @BindView(R.id.iv_text)
    ImageButton touchStatus;
    @BindView(R.id.iv_emoji)
    ImageButton emoji;
    @BindView(R.id.iv_rotate)
    ImageButton rotate;
    @BindView(R.id.tv_caption)
    EditText nontouchStatus;
    private ProgressBar progressBar;
    private boolean enabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_story);

        final TextView nontouchStatusDisplay = (TextView) findViewById(R.id.tv_captionShow);
        final TextView touchStatusDisplay = (TextView) findViewById(R.id.tv_addText);
        // set the drawable as progress drawable
        progressBar = (ProgressBar) findViewById(R.id.pb_progress);
        ButterKnife.bind(this);
        final String dataType = getIntent().getStringExtra("dataType");
        final String imageFilePath = getIntent().getStringExtra("imagePath");
        String videoFIlePath = getIntent().getStringExtra("videoPath");
        final byte[] storyInBytes = getIntent().getByteArrayExtra("byteData");

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "rotating", Toast.LENGTH_SHORT).show();
            }
        });

        touchStatus.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (enabled) {
                    enabled = false;
                    touchStatusDisplay.setCursorVisible(false);
                    touchStatus.setBackgroundColor(getColor(R.color.transparent));
                } else {
                    enabled = true;
                    touchStatusDisplay.setVisibility(View.VISIBLE);
                    touchStatusDisplay.setCursorVisible(true);
                    touchStatus.setBackgroundResource(R.drawable.roundcorner);
                    touchStatus.setBackgroundColor(getColor(R.color.green_4));
                }
            }
        });

        nontouchStatus.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nontouchStatusDisplay.setText(charSequence);
                nontouchStatusDisplay.setText(nontouchStatus.getText());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "emoji :)", Toast.LENGTH_SHORT).show();
            }
        });

        closeToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(PreviewStoryActivity.this, StoryActivity.class);
                startActivity(intent);
            }
        });

        //if it is a picture
        if (dataType.compareTo("picture") == 0) {
            selected = true;
            navigateToPictureFragment(imageFilePath, touchStatusDisplay.getText().toString(), nontouchStatusDisplay.getText().toString());

            //if it is a video
        } else if (dataType.compareTo("video") == 0) {
            selected = true;
            navigateToVideoFragment(videoFIlePath, touchStatusDisplay.getText().toString(), nontouchStatusDisplay.getText().toString());
        }

        backToCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PreviewStoryActivity.this, StoryActivity.class);
                startActivity(intent);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get status from preview fragment
                progressBar.setVisibility(View.VISIBLE);
                navigateToMainActivity(dataType, touchStatusDisplay, storyInBytes, nontouchStatusDisplay);
            }
        });
    }

    private void navigateToPictureFragment(String imageFilePath, String text, String caption) {
        Fragment pictureFragment = new PictureFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("imagePath", imageFilePath);
        args.putString("text", text);
        args.putString("caption", caption);
        pictureFragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, pictureFragment).commit();
    }

    private void navigateToVideoFragment(String videoFIlePath, String text, String caption) {
        Fragment videoFragment = new VideoFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("videoPath", videoFIlePath);
        args.putString("text", text);
        args.putString("caption", caption);
        videoFragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, videoFragment).commit();
    }

    private void navigateToMainActivity(String dataType, TextView addText, byte[] storyInBytes, TextView caption) {
        // Toggles progress bar
        Intent intent = new Intent(PreviewStoryActivity.this, MainActivity.class);
        intent.putExtra("key", dataType);
        intent.putExtra("dataType", dataType);
        intent.putExtra("text", addText.getText().toString());
        intent.putExtra("caption", caption.getText().toString());
        intent.putExtra("byteData", storyInBytes);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}

