package com.example.arafatm.anti_socialmedia.Story;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.Fragments.PictureFragment;
import com.example.arafatm.anti_socialmedia.Fragments.VideoFragment;
import com.example.arafatm.anti_socialmedia.Home.MainActivity;
import com.example.arafatm.anti_socialmedia.R;

public class PreviewStoryActivity extends AppCompatActivity implements PictureFragment.OnFragmentInteractionListener, VideoFragment.OnFragmentInteractionListener {

    public static String url;
    private boolean enabled = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_story);

        ImageButton backToCamera = (ImageButton) findViewById(R.id.iv_camera);
        ImageButton shareButton = (ImageButton) findViewById(R.id.iv_share);
        ImageButton closeToCamera = (ImageButton) findViewById(R.id.iv_close);
        final ImageButton text = (ImageButton) findViewById(R.id.iv_text);
        ImageButton emoji = (ImageButton) findViewById(R.id.iv_emoji);
        ImageButton rotate = (ImageButton) findViewById(R.id.iv_rotate);
        final EditText caption = (EditText) findViewById(R.id.tv_caption);
        final TextView captionShow = (TextView) findViewById(R.id.tv_captionShow);
        final TextView addText = (TextView) findViewById(R.id.tv_addText);

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "rotating", Toast.LENGTH_SHORT).show();
            }
        });

        text.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Text", Toast.LENGTH_SHORT).show();
                if (enabled) {
                    enabled = false;
                    addText.setCursorVisible(false);
                    text.setBackgroundColor(getColor(R.color.transparent));
                } else {
                    enabled = true;
                    addText.setVisibility(View.VISIBLE);
                    addText.setCursorVisible(true);
                    text.setBackgroundResource(R.drawable.roundcorner);
                    text.setBackgroundColor(getColor(R.color.green_4));
                }
            }
        });

        caption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                captionShow.setText(charSequence);
                captionShow.setText(caption.getText());
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
                Intent intent = new Intent(PreviewStoryActivity.this, StoryActivity.class);
                startActivity(intent);
            }
        });

        final FragmentManager fragmentManager = getSupportFragmentManager(); //Initiates FragmentManager
        Fragment pictureFragment = new PictureFragment();
        Fragment videoFragment = new VideoFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        final String intentResult = getIntent().getStringExtra("dataType");

        final String imageFilePath = getIntent().getStringExtra("imagePath");
        final String videoFIlePath = getIntent().getStringExtra("videoPath");
        url = videoFIlePath;

        final byte[] bytes = getIntent().getByteArrayExtra("byteData");

        Bundle args = new Bundle();
        //if it is a picture
        if (intentResult.compareTo("picture") == 0) {
            args.putString("imagePath", imageFilePath);
            args.putString("text", addText.getText().toString());
            args.putString("caption", caption.getText().toString());
            pictureFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragment_container, pictureFragment).commit();

            //if it is a video
        } else if (intentResult.compareTo("video") == 0) {
            args.putString("videoPath", videoFIlePath);
            args.putString("text", addText.getText().toString());
            args.putString("caption", caption.getText().toString());
            videoFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragment_container, videoFragment).commit();
        } else {
            //This is wouldn't even happen
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
                Toast.makeText(getApplicationContext(), "Sharing story", Toast.LENGTH_SHORT).show();

                //get status from preview fragment
                Intent intent = new Intent(PreviewStoryActivity.this, MainActivity.class);
                intent.putExtra("key", intentResult);
                intent.putExtra("dataType", intentResult);
                intent.putExtra("text", addText.getText().toString());
                intent.putExtra("caption", caption.getText().toString());
                intent.putExtra("byteData", bytes);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

