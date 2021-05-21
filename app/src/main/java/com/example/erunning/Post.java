package com.example.erunning;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.example.erunning.Utillity.isStorageUrl;

public class Post extends BasicActivity{
    private Button btn_comment;
    private ImageButton like;
    private ImageButton bookmark;
    private TextView tv_like;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        boolean isLiked = false;
        boolean isBookmarked = false;

        btn_comment = findViewById(R.id.btn_comment);
        like = findViewById(R.id.btn_nolike);
        bookmark = findViewById(R.id.btn_bookmark);
        tv_like = findViewById(R.id.tv_like);

        PostInfo postInfo = (PostInfo) getIntent().getSerializableExtra("postInfo");
        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.setText(postInfo.getTitle());

        TextView createdAtTextView = findViewById(R.id.createdAtTextView);

        btn_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("comment : ", "클릭");
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLiked) {
                    like.setImageResource(R.drawable.ic_like_red);
                    Log.e("like : ", "클릭");
                } else {
                    like.setImageResource(R.drawable.ic_like_gray);
                    Log.e("like : ", "클릭");
                }
            }
        });
        tv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLiked) {
                    like.setImageResource(R.drawable.ic_like_red);
                    Log.e("tv_like : ", "클릭");
                } else {
                    like.setImageResource(R.drawable.ic_like_gray);
                    Log.e("tv_like : ", "클릭");
                }
            }
        });
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isBookmarked) {
                    like.setImageResource(R.drawable.ic_bookmark);
                    Log.e("bookmark : ", "클릭");
                } else {
                    like.setImageResource(R.drawable.ic_nobookmark);
                    Log.e("bookmark : ", "클릭");
                }
            }
        });
        long now = System.currentTimeMillis(); // 현재 시간 변수 생성
        SimpleDateFormat dateSet = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분"); // date형식 지정
        String date = dateSet.format(new Date()); //현재 시각을 date형식에 맞게 저장
        String CreatedDate = new SimpleDateFormat("yyyy년MM월dd일 HH시mm분", Locale.getDefault()).format(postInfo.getCreatedAt());
        // 게시글 만든 시간
        try {
            Date startDate = dateSet.parse(date);
            Date endDate = dateSet.parse(CreatedDate);
            long duration = startDate.getTime() - endDate.getTime();
            long min = duration / 60000;
            if (min <= 1) {
                createdAtTextView.setText("방금 전");
            } else if (min == 2) {
                createdAtTextView.setText("1분 전");
            } else if (min == 3) {
                createdAtTextView.setText("2분 전");
            } else if (min == 4) {
                createdAtTextView.setText("3분 전");
            } else if (min == 5) {
                createdAtTextView.setText("4분 전");
            } else if (min == 6) {
                createdAtTextView.setText("5분 전");
            } else if (min == 7) {
                createdAtTextView.setText("6분 전");
            } else if (min == 8) {
                createdAtTextView.setText("7분 전");
            } else if (min == 9) {
                createdAtTextView.setText("10분 전");
            } else if (min == 10) {
                createdAtTextView.setText("11분 전");
            } else if (min == 11) {
                createdAtTextView.setText("12분 전");
            } else if (min == 12) {
                createdAtTextView.setText("13분 전");
            } else if (min == 13) {
                createdAtTextView.setText("14분 전");
            } else if (min == 14) {
                createdAtTextView.setText("15분 전");
            } else if (min == 15) {
                createdAtTextView.setText("16분 전");
            } else if (min == 16) {
                createdAtTextView.setText("17분 전");
            } else if (min == 17) {
                createdAtTextView.setText("18분 전");
            } else if (min == 18) {
                createdAtTextView.setText("19분 전");
            } else if (min == 19) {
                createdAtTextView.setText("20분 전");
            } else if (min == 20) {
                createdAtTextView.setText("21분 전");
            } else if (min == 21) {
                createdAtTextView.setText("22분 전");
            } else if (min == 22) {
                createdAtTextView.setText("23분 전");
            } else if (min == 23) {
                createdAtTextView.setText("24분 전");
            } else if (min == 24) {
                createdAtTextView.setText("25분 전");
            } else if (min == 25) {
                createdAtTextView.setText("26분 전");
            } else if (min == 26) {
                createdAtTextView.setText("28분 전");
            } else if (min == 27) {
                createdAtTextView.setText("29분 전");
            } else if (min == 28) {
                createdAtTextView.setText("30분 전");
            }//
            else if (min >= 29 && min <= 59) {
                createdAtTextView.setText("+ 30분");
            } else if (min >= 60 && min <= 119) {
                createdAtTextView.setText("+ 1시간");
            } else if (min >= 120 && min <= 179) {
                createdAtTextView.setText("+ 2시간");
            } else if (min >= 180 && min <= 239) {
                createdAtTextView.setText("+ 3시간");
            } else if (min >= 240 && min <= 299) {
                createdAtTextView.setText("+ 4시간");
            } else if (min >= 300 && min <= 359) {
                createdAtTextView.setText("+ 5시간");
            } else if (min >= 360 && min <= 419) {
                createdAtTextView.setText("+ 6시간");
            } else if (min >= 420 && min <= 479) {
                createdAtTextView.setText("+ 7시간");
            } else if (min >= 480 && min <= 539) {
                createdAtTextView.setText("+ 8시간");
            } else if (min >= 540 && min <= 599) {
                createdAtTextView.setText("+ 9시간");
            } else if (min >= 600 && min <= 659) {
                createdAtTextView.setText("+ 10시간");
            } else if (min >= 660 && min <= 719) {
                createdAtTextView.setText("+ 11시간");
            } else if (min >= 720 && min <= 779) {
                createdAtTextView.setText("+ 12시간");
            } else if (min >= 780 && min <= 839) {
                createdAtTextView.setText("+ 13시간");
            } else if (min >= 840 && min <= 899) {
                createdAtTextView.setText("+ 14시간");
            } else if (min >= 900 && min <= 959) {
                createdAtTextView.setText("+ 15시간");
            } else if (min >= 960 && min <= 719) {
                createdAtTextView.setText("+ 16시간");
            } else if (min >= 720 && min <= 779) {
                createdAtTextView.setText("+ 17시간");
            } else if (min >= 780 && min <= 839) {
                createdAtTextView.setText("+ 18시간");
            } else if (min >= 840 && min <= 899) {
                createdAtTextView.setText("+ 19시간");
            } else if (min >= 900 && min <= 959) {
                createdAtTextView.setText("+ 20시간");
            } else if (min >= 960 && min <= 1019) {
                createdAtTextView.setText("+ 21시간");
            } else if (min >= 1020 && min <= 1079) {
                createdAtTextView.setText("+ 22시간");
            } else if (min >= 1080 && min <= 1139) {
                createdAtTextView.setText("+ 23시간");
            } else if (min >= 1140 && min <= 1199) {
                createdAtTextView.setText("+ 1일 전");
            } else {
                createdAtTextView.setText(CreatedDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        LinearLayout contentsLayout = findViewById(R.id.contentsLayout);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ArrayList<String> contentList = postInfo.getContents();

        if (contentsLayout.getTag() == null || !contentsLayout.getTag().equals(contentList)) {
            contentsLayout.setTag(contentList);
            contentsLayout.removeAllViews();
            for (int i = 0; i < contentList.size(); i++) {
                String contents = contentList.get(i);
                if (isStorageUrl(contents)) {
                    ImageView imageView = new ImageView(this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    contentsLayout.addView(imageView);
                    Glide.with(this).load(contents).override(1000).thumbnail(0.1f).into(imageView);
                } else {
                    TextView textView = new TextView(this);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(contents);
                    contentsLayout.addView(textView);
                }
            }
        }
    }
}