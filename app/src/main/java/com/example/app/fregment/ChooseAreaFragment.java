package com.example.app.fregment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.app.activity.WeatherActivity;
import com.example.app.db.City;
import com.example.app.db.Country;
import com.example.app.db.Province;
import com.example.app.util.HttpUtil;
import com.example.app.util.Utility;
import com.example.app.view.LoadingDialog;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by roach on 2017/10/12.
 */

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVICE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;
    public static final String HTTP_GUOLIN_TECH_API_CHINA = "http://guolin.tech/api/china";

    private TextView tv_title;
    private Button btn_back;
    private RecyclerView rv_list;
    private List<City> mCityList;
    private List<Province> mProvinceList;
    private List<Country> mCountryList;

    private Province mProvince;
    private City mCity;
    private int mCurrentLevel;

    private List<String> dataList = new ArrayList<>();
    private AraeListAdapter mListAdapter = new AraeListAdapter();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.choose_area, container);
        tv_title = inflate.findViewById(R.id.tv_title);
        btn_back = inflate.findViewById(R.id.btn_back);
        rv_list = inflate.findViewById(R.id.rv_list);
        rv_list.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_list.addItemDecoration(new DividerItemDecoration(getContext(),LinearLayoutManager.VERTICAL));
        rv_list.setAdapter(mListAdapter);
        return inflate;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListAdapter.setOnItemClicekListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mCurrentLevel == LEVEL_PROVICE){
                    mProvince = mProvinceList.get(position);
                    queryCities();
                }else if(mCurrentLevel == LEVEL_CITY){
                    mCity = mCityList.get(position);
                    queryCountries();
                }else if(mCurrentLevel == LEVEL_COUNTRY){
                    String weatherId = mCountryList.get(position).getWeatherId();
                    Intent intent = new Intent(getActivity(), WeatherActivity.class);
                    intent.putExtra("weather_id",weatherId);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentLevel == LEVEL_COUNTRY){
                    queryCities();
                }else if(mCurrentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }

        });

        queryProvinces();
    }

    private void queryProvinces() {
        tv_title.setText("中国");
        btn_back.setVisibility(View.GONE);
        mProvinceList = DataSupport.findAll(Province.class);

        if(mProvinceList.size() > 0) {
            dataList.clear();
            for (Province p : mProvinceList) {
                dataList.add(p.getProvinceName());
            }
            mListAdapter.notifyDataSetChanged();
            rv_list.scrollToPosition(0);
            mCurrentLevel = LEVEL_PROVICE;
        }else{
            String address = HTTP_GUOLIN_TECH_API_CHINA;
            queryFromServer(address,"province");
        }


    }

    private void queryCountries() {
        tv_title.setText(mCity.getCityName());
        btn_back.setVisibility(View.VISIBLE);
        mCountryList = DataSupport.where("cityId = ?",String.valueOf(mCity.getId())).find(Country.class);
        if(mCountryList.size() > 0){
            dataList.clear();
            for (Country country : mCountryList){
                dataList.add(country.getCountryName());
            }
            mListAdapter.notifyDataSetChanged();
            rv_list.scrollToPosition(0);
            mCurrentLevel = LEVEL_COUNTRY;
        }else{
            String address = HTTP_GUOLIN_TECH_API_CHINA +"/"+mProvince.getProvinceCode()+"/"+mCity.getId() ;
            queryFromServer(address,"country");
        }
    }

    private void queryCities() {
        tv_title.setText(mProvince.getProvinceName());
        btn_back.setVisibility(View.VISIBLE);
        mCityList = DataSupport.where("proviceId = ?",String.valueOf(mProvince.getId())).find(City.class);
        if(mCityList.size() > 0){
            dataList.clear();
            for (City c : mCityList){
                dataList.add(c.getCityName());
            }
            mListAdapter.notifyDataSetChanged();
            rv_list.scrollToPosition(0);
            mCurrentLevel = LEVEL_CITY;
        }else{
            int provinceCode = mProvince.getProvinceCode();
            String address = HTTP_GUOLIN_TECH_API_CHINA +"/"+provinceCode;
            queryFromServer(address,"city");
        }
    }

    private void queryFromServer(String address, final String type) {
        final String key = getClass().getName();
        LoadingDialog.show(getFragmentManager(),key);

        HttpUtil.setOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoadingDialog.dismiss(key);
                Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                boolean result = false;
                if(TextUtils.equals("province",type)){
                    result = Utility.handleProvinceResponse(string);
                }else if(TextUtils.equals("city",type)){
                    result = Utility.handleCityResponse(string,mProvince.getId());
                }else if(TextUtils.equals("country",type)){
                    result = Utility.handleCountryResponse(string,mCity.getId());
                }


                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LoadingDialog.dismiss(key);
                            if(TextUtils.equals("province",type)){
                                    queryProvinces();
                            }else if(TextUtils.equals("city",type)){
                                    queryCities();
                            }else if(TextUtils.equals("country",type)){
                                    queryCountries();
                            }

                        }
                    });
                }
            }
        });
    }



    class AraeListAdapter extends RecyclerView.Adapter<AraeListAdapter.Holder>{
        private OnItemClickListener mOnItemClicekListener;

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1,parent,false);
            Holder holder = new Holder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(Holder holder, final int position) {
            holder.mTextView.setText(dataList.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClicekListener != null){
                        mOnItemClicekListener.onItemClick(view,position);
                    }
                }
            });
        }

         public void setOnItemClicekListener(OnItemClickListener onItemClicekListener) {
             mOnItemClicekListener = onItemClicekListener;
         }

         @Override
        public int getItemCount() {
            return dataList.size();
        }


        class Holder extends RecyclerView.ViewHolder{
            TextView mTextView;
            public Holder(View itemView) {
                super(itemView);
                mTextView = itemView.findViewById(android.R.id.text1);
            }
        }


    }

    interface OnItemClickListener {
        void onItemClick(View view,int position);
    }
}
