package com.example.arafatm.anti_socialmedia.Story;

import android.content.Intent;
import android.content.res.Resources;
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
    ImageButton text;
    @BindView(R.id.iv_emoji)
    ImageButton emoji;
    @BindView(R.id.iv_rotate)
    ImageButton rotate;
    @BindView(R.id.tv_caption)
    EditText caption;
    private boolean enabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_story);

        final TextView captionShow = (TextView) findViewById(R.id.tv_captionShow);
        final TextView addText = (TextView) findViewById(R.id.tv_addText);
        ButterKnife.bind(this);

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

//        captionShow.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                android.support.constraint.ConstraintLayout.LayoutParams layoutParams1;
//               layoutParams1 = (ConstraintLayout.LayoutParams) captionShow.getLayoutParams();
//
//
//                switch(event.getActionMasked())
//                {
//                    case MotionEvent.ACTION_DOWN:
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        int x_cord = (int) event.getRawX();
//                        int y_cord = (int) event.getRawY();
//                        if (x_cord > getScreenWidth()) {
//                            x_cord = getScreenWidth();
//                        }
//                        if (y_cord > getScreenHeight()) {
//                            y_cord = getScreenHeight();
//                        }
//                        layoutParams1.leftMargin = x_cord - 25;
//                        layoutParams1.topMargin = y_cord - 7;
//                        captionShow.setLayoutParams(layoutParams1);
//                        break;
//                    default:
//                        break;
//                }
//                return true;
//            }
//        });

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
        String videoFIlePath = getIntent().getStringExtra("videoPath");
        final byte[] StoryBytes = getIntent().getByteArrayExtra("byteData");

        Bundle args = new Bundle();
        //if it is a picture
        if (intentResult.compareTo("picture") == 0) {
            selected = true;
            args.putString("imagePath", imageFilePath);
            args.putString("text", addText.getText().toString());
            args.putString("caption", caption.getText().toString());
            pictureFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragment_container, pictureFragment).commit();
            //if it is a video
        } else if (intentResult.compareTo("video") == 0) {
            selected = true;
            args.putString("videoPath", videoFIlePath);
            args.putString("text", addText.getText().toString());
            args.putString("caption", caption.getText().toString());
            videoFragment.setArguments(args);
            fragmentTransaction.replace(R.id.fragment_container, videoFragment).commit();
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
                Intent intent = new Intent(PreviewStoryActivity.this, MainActivity.class);
                intent.putExtra("key", intentResult);
                intent.putExtra("dataType", intentResult);
                intent.putExtra("text", addText.getText().toString());
                intent.putExtra("caption", caption.getText().toString());
                intent.putExtra("byteData", StoryBytes);
                startActivity(intent);
            }
        });
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

