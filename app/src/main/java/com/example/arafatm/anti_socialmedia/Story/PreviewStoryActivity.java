package com.example.arafatm.anti_socialmedia.Story;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.Fragments.PictureFragment;
import com.example.arafatm.anti_socialmedia.Fragments.VideoFragment;
import com.example.arafatm.anti_socialmedia.Home.MainActivity;
import com.example.arafatm.anti_socialmedia.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PreviewStoryActivity extends AppCompatActivity implements PictureFragment.OnFragmentInteractionListener, VideoFragment.OnFragmentInteractionListener {
    @BindView(R.id.iv_camera) ImageButton backToCamera;
    @BindView(R.id.iv_share) ImageButton shareButton;
    @BindView(R.id.iv_close) ImageButton closeToCamera;
    @BindView(R.id.iv_text) ImageButton text;
    @BindView(R.id.iv_emoji) ImageButton emoji;
    @BindView(R.id.iv_rotate) ImageButton rotate;
    @BindView(R.id.tv_caption) EditText caption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_story);
        ButterKnife.bind(this);

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "rotating", Toast.LENGTH_SHORT).show();
            }
        });

        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Text", Toast.LENGTH_SHORT).show();
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
        final byte[] bytes = getIntent().getByteArrayExtra("byteData");



        Bundle args = new Bundle();
        //if it is a picture
        if (intentResult.compareTo("picture") == 0) {
            args.putString("imagePath", imageFilePath);
            pictureFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragment_container, pictureFragment).commit();

            //if it is a video
        } else if (intentResult.compareTo("video") == 0) {
            args.putString("videoPath", videoFIlePath);
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
                String status = caption.getText().toString();
                Intent intent = new Intent(PreviewStoryActivity.this, MainActivity.class);
                intent.putExtra("key", status);

//                intent.putExtra("dataType", intentResult);
//                intent.putExtra("byteData", bytes);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

