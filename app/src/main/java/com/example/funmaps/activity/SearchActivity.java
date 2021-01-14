package com.example.funmaps.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.example.funmaps.R;
import com.example.funmaps.bean.NaviAddress;
import com.example.funmaps.bean.TargetAddress;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private EditText editText;
    private ImageButton btnClear;
    private Button btnCancel;
    private SuggestionSearch mSuggestionSearch;
    private List<SuggestionResult.SuggestionInfo> suggest;
    private String key = null;
    private TargetAddress targetAddress = null;
    private TargetAddress targetAddress1 = null;
    private NaviAddress naviAddress = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏应用程序的标题栏，即当前activity的label
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 隐藏android系统的状态栏
        setContentView(R.layout.activity_search);
        initData();
        initView();
        search();
    }


    private void initData() {
        key = getIntent().getStringExtra("Key");
        targetAddress = new TargetAddress();
        targetAddress1 = new TargetAddress();
        naviAddress = new NaviAddress();
        targetAddress1 = (TargetAddress) getIntent().getSerializableExtra("targetAddress1");//获取序列化传进来的参数
        mSuggestionSearch = SuggestionSearch.newInstance();
        suggest = new ArrayList<>();
        OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                //处理sug检索结果
                if(suggestionResult == null || suggestionResult.getAllSuggestions() == null){
                    System.out.println("未找到");
                }else{
                    suggest = suggestionResult.getAllSuggestions();
                    for (SuggestionResult.SuggestionInfo suggestionInfo:suggest){
                        System.out.println("所查找地址:"+suggestionInfo.getAddress()+"长度:"+suggestionInfo.getAddress().length());
                    }

                    searchAdapter.reFresh(suggest);
                }
            }
        };
        mSuggestionSearch.setOnGetSuggestionResultListener(listener);
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyle);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(SearchActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        searchAdapter = new SearchAdapter(suggest);
        recyclerView.setAdapter(searchAdapter);
        editText = findViewById(R.id.input);
        btnClear = findViewById(R.id.clear);
        btnCancel = findViewById(R.id.search_cancel);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //查找逻辑实现
    private void search() {
        TextWatcher watcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                if (editText.getEditableText().length() >= 1){
                    btnClear.setVisibility(View.VISIBLE);
                } else{
                    btnClear.setVisibility(View.GONE);
                }
                mSuggestionSearch.requestSuggestion(new SuggestionSearchOption()
                        .city("长沙")
                        .keyword(""+editText.getText().toString()));
            }
        };
        editText.addTextChangedListener(watcher);
    }

    public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {
        List<SuggestionResult.SuggestionInfo> suggest = new ArrayList<>();
        TargetAddress targetAddress = null;
        SearchAdapter(List<SuggestionResult.SuggestionInfo> suggest){
            this.suggest = suggest;
        }
        @NonNull
        //绑定当前视图布局
        @Override
        public SearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item, parent, false);
            final SearchAdapter.MyViewHolder vh = new SearchAdapter.MyViewHolder(view);
            vh.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = vh.getAdapterPosition();
                    targetAddress = new TargetAddress();
                    targetAddress.setLatitude(suggest.get(position).pt.latitude);
                    targetAddress.setLongitude(suggest.get(position).pt.longitude);
                    targetAddress.setAddr(suggest.get(position).address);
                    System.out.println(key);
                    if (key.equals("weather")){
                        Intent intent = new Intent(SearchActivity.this, WeatherSearchActivity.class);
                        intent.putExtra("targetAddress",targetAddress);
                        intent.putExtra("targetAddress1",targetAddress1);
                        intent.putExtra("returnKey","WeatherSearchActivity");
                        startActivity(intent);
                        finish();
                    }else if (key.equals("MainActivity")){
                        Intent intent1 = new Intent(SearchActivity.this, HeatMapActivity.class);
                        intent1.putExtra("targetAddress",targetAddress);
                        intent1.putExtra("returnKey","HeatMapActivity");
                        startActivity(intent1);
                        finish();
                    }else if (key.equals("BNaviMainActivityStart")){
                        Intent intent2 = new Intent(SearchActivity.this, BNaviMainActivity.class);
                        naviAddress.setStartLocation(targetAddress.getAddr());
                        naviAddress.setStartLatitude(targetAddress.getLatitude());
                        naviAddress.setStartLongitude(targetAddress.getLongitude());
//                        naviAddress.setStartStreet(targetAddress.getStreet());
                        naviAddress.setEndLocation(targetAddress1.getAddr());
//                        naviAddress.setEndStreet(targetAddress1.getStreet());
                        naviAddress.setEndLatitude(targetAddress1.getLatitude());
                        naviAddress.setEndLongitude(targetAddress1.getLongitude());
                        naviAddress.setUsername(targetAddress1.getUsername());
                        intent2.putExtra("naviAddress",naviAddress);
                        intent2.putExtra("returnKey","BNaviMainActivityStart");
                        startActivity(intent2);
                        finish();
                    }else if (key.equals("BNaviMainActivityEnd")){
                        Intent intent3 = new Intent(SearchActivity.this, BNaviMainActivity.class);
                        naviAddress.setStartLocation(targetAddress1.getAddr());
//                        naviAddress.setStartStreet(targetAddress1.getStreet());
                        naviAddress.setStartLatitude(targetAddress1.getLatitude());
                        naviAddress.setStartLongitude(targetAddress1.getLongitude());
                        naviAddress.setEndLocation(targetAddress.getAddr());
//                        naviAddress.setEndStreet(targetAddress.getStreet());
                        naviAddress.setEndLatitude(targetAddress.getLatitude());
                        naviAddress.setEndLongitude(targetAddress.getLongitude());
                        naviAddress.setUsername(targetAddress1.getUsername());
                        intent3.putExtra("naviAddress",naviAddress);
                        intent3.putExtra("returnKey","BNaviMainActivityEnd");
                        startActivity(intent3);
                        finish();
                    }

                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull SearchAdapter.MyViewHolder holder, int position) {
            SuggestionResult.SuggestionInfo suggestionInfo = suggest.get(position);
            if (suggestionInfo.getAddress().length()!=0){
                holder.textView.setText(suggestionInfo.getAddress());
            }
        }

        @Override
        public int getItemCount() {
            if (suggest == null){
                return 0;
            }else {
                return suggest.size();
            }

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView textView;
            public MyViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.address_name);
            }
        }

        public void reFresh(List<SuggestionResult.SuggestionInfo> suggest){
            this.suggest.clear();
            for (SuggestionResult.SuggestionInfo sug:suggest){
                    if (sug.getAddress().length()==0){
                        suggest.remove(sug);
                        break;
                    }
            }
            this.suggest = suggest;
            notifyDataSetChanged();
        }

    }
}