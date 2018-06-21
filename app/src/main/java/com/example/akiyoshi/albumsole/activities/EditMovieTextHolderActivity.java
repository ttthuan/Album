package com.example.akiyoshi.albumsole.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.akiyoshi.albumsole.R;
import com.example.akiyoshi.albumsole.models.PictureEditMovie;

import java.util.ArrayList;
import java.util.List;

public class EditMovieTextHolderActivity extends AppCompatActivity implements EditTextHolderCallBack {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static ArrayList<PictureEditMovie> listPictureEdit;
    ImageView img_edit_movie_text_holder;
    int position = -1;
    String text_holder_style;
    ViewGroup edit_movie_text_holder;
    TextHolderEditMovieAdapter adapter;
    RecyclerView rcvEditMovieTextHolder;
    View textHolder;
    ImageView imgTextHolder;
    int w, h;
    int textSizeTit = 0, textSizeSub = 0;
    EditText txt_edit_text_holder_title, txt_edit_text_holder_sub_title;
    Button btn_edit_text_holder_movie;
    ImageButton btn_close_edit_text_holder_movie;
    String chosenStyle = "1";

    public static Intent newIntent(Context context, ArrayList<PictureEditMovie> listPictureEdit, int position) {
        Intent intent = new Intent(context, EditMovieTextHolderActivity.class);
        intent.putExtra(ARG_PARAM1, listPictureEdit);
        intent.putExtra(ARG_PARAM2, position);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_movie_text_holder);
        if (getIntent() != null) {
            listPictureEdit = (ArrayList<PictureEditMovie>) getIntent().getSerializableExtra(ARG_PARAM1);
            position = getIntent().getIntExtra(ARG_PARAM2, 0);
        }
        img_edit_movie_text_holder = findViewById(R.id.img_edit_movie_text_holder);
        w = Math.round(MainActivity.widthScreen * 1.0f / 2);
        h = Math.round(w * 1.0f * 16 / 9);
        ClearImageFilterd clearImageFilterd = new ClearImageFilterd(this, img_edit_movie_text_holder, listPictureEdit.get(0).getPath(), listPictureEdit.get(0).getFilter(), w, h);
        clearImageFilterd.execute();
        List<String> listTextHolder = new ArrayList<>();
        listTextHolder.add("1");
        listTextHolder.add("2");
        listTextHolder.add("3");
        listTextHolder.add("4");

        adapter = new TextHolderEditMovieAdapter(this, listTextHolder, (EditTextHolderCallBack) this);
        rcvEditMovieTextHolder = findViewById(R.id.rcvEditMovieTextHolder);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        rcvEditMovieTextHolder.setLayoutManager(layoutManager);
        rcvEditMovieTextHolder.setHasFixedSize(true);
        rcvEditMovieTextHolder.setAdapter(adapter);

        txt_edit_text_holder_title = findViewById(R.id.txt_edit_text_holder_title);
        txt_edit_text_holder_sub_title = findViewById(R.id.txt_edit_text_holder_sub_title);
        txt_edit_text_holder_title.setText(listPictureEdit.get(0).getTitle());
        txt_edit_text_holder_sub_title.setText(listPictureEdit.get(0).getSubtitle());


        text_holder_style = listPictureEdit.get(0).getHolder();
        edit_movie_text_holder = findViewById(R.id.edit_movie_text_holder);
        switch (text_holder_style) {
            case "1":
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_1, (ViewGroup) edit_movie_text_holder, false);
                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_1)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                textSizeTit = 20;
                textSizeSub = 12;
                break;
            case "2":
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_2, (ViewGroup) edit_movie_text_holder, false);

                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_2)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                textSizeTit = 14;
                textSizeSub = 8;
                break;
            case "3":
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_3, (ViewGroup) edit_movie_text_holder, false);

                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_3)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                textSizeTit = 20;
                textSizeSub = 12;
                break;
            case "4":
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_4, (ViewGroup) edit_movie_text_holder, false);

                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_4)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                textSizeTit = 24;
                textSizeSub = 12;
                break;
            default:
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_1, (ViewGroup) edit_movie_text_holder, false);

                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_1)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                textSizeTit = 20;
                textSizeSub = 12;
                break;

        }
        chosenStyle = text_holder_style;
        TextView txtTextHolderTitle = (TextView) textHolder.findViewById(R.id.txtTextHolderTitle);
        TextView txtTextHolderSubtitle = (TextView) textHolder.findViewById(R.id.txtTextHolderSubtitle);
        txtTextHolderTitle.setText(txt_edit_text_holder_title.getText().toString());
        txtTextHolderTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeTit);
        txtTextHolderSubtitle.setText(txt_edit_text_holder_sub_title.getText().toString());
        txtTextHolderSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSub);
        ViewGroup.LayoutParams param = edit_movie_text_holder.getLayoutParams();
        param.height = h;
        param.width = w;
        edit_movie_text_holder.setLayoutParams(param);
        edit_movie_text_holder.addView(textHolder);


        txt_edit_text_holder_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtTextHolderTitle.setText(s.toString());
                if (s.length() == 0) {
                    txtTextHolderTitle.setText("Title");
                    btn_edit_text_holder_movie.setEnabled(false);
                    btn_edit_text_holder_movie.setAlpha(0.5f);
                }
                if (s.length() > 0 && !btn_edit_text_holder_movie.isEnabled()) {
                    btn_edit_text_holder_movie.setEnabled(true);
                    btn_edit_text_holder_movie.setAlpha(1f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txt_edit_text_holder_sub_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtTextHolderSubtitle.setText(s.toString());
                if (s.length() == 0) {
                    txtTextHolderSubtitle.setText("Subtitle");
                    btn_edit_text_holder_movie.setEnabled(false);
                    btn_edit_text_holder_movie.setAlpha(0.5f);
                }
                if (s.length() > 0 && !btn_edit_text_holder_movie.isEnabled()) {
                    btn_edit_text_holder_movie.setEnabled(true);
                    btn_edit_text_holder_movie.setAlpha(1f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_edit_text_holder_movie = findViewById(R.id.btn_edit_text_holder_movie);
        btn_edit_text_holder_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_edit_text_holder_title.requestFocus();
                txt_edit_text_holder_sub_title.requestFocus();
                EditMovieActivity.changeTextHolder(chosenStyle, txtTextHolderTitle.getText().toString(), txtTextHolderSubtitle.getText().toString());
                finish();
            }
        });
        btn_close_edit_text_holder_movie = findViewById(R.id.btn_close_edit_text_holder_movie);
        btn_close_edit_text_holder_movie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onClickEditTextHolder(String style) {
        switch (style) {
            case "1":
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_1, (ViewGroup) edit_movie_text_holder, false);
                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_1)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                textSizeTit = 20;
                textSizeSub = 12;
                break;
            case "2":
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_2, (ViewGroup) edit_movie_text_holder, false);

                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_2)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                textSizeTit = 14;
                textSizeSub = 8;
                break;
            case "3":
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_3, (ViewGroup) edit_movie_text_holder, false);

                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_3)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                textSizeTit = 20;
                textSizeSub = 12;
                break;
            case "4":
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_4, (ViewGroup) edit_movie_text_holder, false);

                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_4)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                textSizeTit = 24;
                textSizeSub = 12;
                break;
            default:
                textHolder = LayoutInflater.from(this).inflate(R.layout.tab_item_text_holder_1, (ViewGroup) edit_movie_text_holder, false);

                imgTextHolder = (ImageView) textHolder.findViewById(R.id.imgTextHolder);
                Glide.with(this)
                        .load(R.drawable.text_holder_1)
                        .apply(new RequestOptions().dontAnimate().override(imgTextHolder.getMeasuredWidth(), imgTextHolder.getMeasuredHeight()))
                        .into(imgTextHolder);
                textSizeTit = 20;
                textSizeSub = 12;
                break;

        }
        chosenStyle = style;
        TextView txtTextHolderTitle = (TextView) textHolder.findViewById(R.id.txtTextHolderTitle);
        TextView txtTextHolderSubtitle = (TextView) textHolder.findViewById(R.id.txtTextHolderSubtitle);
        if (txt_edit_text_holder_title.getText().toString().length() == 0) {
            txtTextHolderTitle.setText("Title");
            btn_edit_text_holder_movie.setEnabled(false);
            btn_edit_text_holder_movie.setAlpha(0.5f);
        } else {
            txtTextHolderTitle.setText(txt_edit_text_holder_title.getText().toString());
        }

        txtTextHolderTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeTit);
        if (txt_edit_text_holder_sub_title.getText().toString().length() == 0) {
            txtTextHolderSubtitle.setText("Subtitle");
            btn_edit_text_holder_movie.setEnabled(false);
            btn_edit_text_holder_movie.setAlpha(0.5f);
        } else {
            txtTextHolderSubtitle.setText(txt_edit_text_holder_sub_title.getText().toString());
        }
        txtTextHolderSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeSub);
        edit_movie_text_holder.removeAllViews();
        ViewGroup.LayoutParams param = edit_movie_text_holder.getLayoutParams();
        param.height = h;
        param.width = w;
        edit_movie_text_holder.setLayoutParams(param);
        edit_movie_text_holder.addView(textHolder);

        txt_edit_text_holder_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtTextHolderTitle.setText(s.toString());
                if (s.length() == 0) {
                    txtTextHolderTitle.setText("Title");
                    btn_edit_text_holder_movie.setEnabled(false);
                    btn_edit_text_holder_movie.setAlpha(0.5f);
                }
                if (s.length() > 0 && !btn_edit_text_holder_movie.isEnabled()) {
                    btn_edit_text_holder_movie.setEnabled(true);
                    btn_edit_text_holder_movie.setAlpha(1f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txt_edit_text_holder_sub_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txtTextHolderSubtitle.setText(s.toString());
                if (s.length() == 0) {
                    txtTextHolderSubtitle.setText("Subtitle");
                    btn_edit_text_holder_movie.setEnabled(false);
                    btn_edit_text_holder_movie.setAlpha(0.5f);
                }
                if (s.length() > 0 && !btn_edit_text_holder_movie.isEnabled()) {
                    btn_edit_text_holder_movie.setEnabled(true);
                    btn_edit_text_holder_movie.setAlpha(1f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static int dpToPx(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
