package com.age.steward.car.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.age.steward.car.utils.overlayutil.DrivingRouteOverlay;
import com.android.library.zxing.activity.CaptureActivity;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatus;

import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;

import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;

import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.IntegralRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.age.steward.car.R;
import com.age.steward.car.adapter.PoiSearchAdapter;
import com.age.steward.car.expand.ConstDataConfig;
import com.age.steward.car.utils.Hint;
import com.age.steward.car.utils.ItemClickSupport;
import com.age.steward.car.utils.StringUtils;
import com.age.steward.car.utils.overlayutil.WalkingRouteOverlay;
import butterknife.BindView;
import butterknife.ButterKnife;


public class MapActivity extends RxAppCompatActivity {
    @BindView(R.id.et_map_activity_search)
    EditText etSearch;
    @BindView(R.id.mapView_map_activity)
    MapView mMapView;
    @BindView(R.id.lt_map_activity_Layout)
    LinearLayout linearLayout;
    @BindView(R.id.rv_map_activity_poiList)
    RecyclerView recyclerView;
    BaiduMap mBaiduMap;
    LocationClient mLocationClient;
    private boolean isFirst = true;
    private String city = "";
    private long firstExitTime = 0;
    private boolean mDoubleClickExit = false;
    //poi检索
    private PoiSearch mPoiSearch;
    //线路
    RoutePlanSearch mRoutePlanSearch;
    private List<PoiInfo> list = new ArrayList<>();
    private PoiSearchAdapter poiSearchAdapter;
    LatLng ll_st;
    LatLng ll_end;
    private String distanceStr;
    private int[] distanceArr = new int[]{20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 25000, 50000, 100000, 200000, 500000, 1000000, 2000000, 5000000, 10000000};
    private int[] levelArr = new int[]{21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        ButterKnife.bind(this);
        initView();
        requestPermissions();

    }

    private void requestPermissions(){

        LocationClient.setAgreePrivacy(true);
        XXPermissions.with(this).permission(Permission.ACCESS_FINE_LOCATION).request(new OnPermissionCallback() {
            @Override
            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                initLocationOption();
            }
        });
    }

    @SuppressLint("WrongConstant")
    private void initView() {
        etSearch.addTextChangedListener(new ETSearchTextWatcher());
        mBaiduMap = mMapView.getMap();
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);
        mBaiduMap.setCompassEnable(true);
        mBaiduMap.setMyLocationEnabled(true);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(listener);

        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(routePlanResultListener);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        poiSearchAdapter = new PoiSearchAdapter(this, list);
        recyclerView.setAdapter(poiSearchAdapter);
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                PoiInfo poiInfo=list.get(position);
                ll_end= new LatLng(poiInfo.location.latitude, poiInfo.location.longitude);
                //routePlan(ll_st,ll_end);
                Bundle bundle=new Bundle();
                bundle.putParcelable("ll_st",ll_st);
                bundle.putParcelable("ll_end",ll_end);
                Intent intent=new Intent(MapActivity.this,WalkingActivity.class);
                bundle.putString("endAddress",poiInfo.name);
                intent.putExtra("data",bundle);
                startActivity(intent);
                //mPoiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUids(poiInfo.uid));
            }
        });

        /**
         * 添加地图缩放状态变化监听，当手动放大或缩小地图时，拿到缩放后的比例，然后获取到下次定位，
         *  给地图重新设置缩放比例，否则地图会重新回到默认的mCurrentZoom缩放比例
         */
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {

            @Override
            public void onMapStatusChangeStart(MapStatus arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus arg0) {
                Log.d("地图缩放级别:",arg0.zoom+"----");
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {

            }
        });

    }

    //设置地图缩放级别
    private void setLevel() {
        //起点： latitude纬度           longitude经度
        if (ll_st != null ) {
            int distance = (int) DistanceUtil.getDistance(ll_st, ll_end);
            distanceStr = "距离约" + distance + "米";
            int level = getLevel(distance);
            //设置缩放级别
            Log.d("驾驶路线","直线距离:"+distance);
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(levelArr[level]).build()));
        }
    }
    //根据距离计算出差值数组,并排序（取正数）
    private int getLevel(int distance) {
        int level = -1;
        int min = 10000000;
        for (int i = 0; i < distanceArr.length; i++) {
            if (distanceArr[i] - distance > 0 && distanceArr[i] - distance < min) {
                min = distanceArr[i] - distance;
                level = i;
            }
        }
        return level;
    }


    //poi检索
    private void poiSearch(String city, String keyword, int pageNum, int pageCapacity) {
        /**
         *  PoiCiySearchOption 设置检索属性
         *  city 检索城市
         *  keyword 检索内容关键字
         *  pageNum 分页页码
         */
        if (StringUtils.isNull(keyword)){
            return;
        }
        mPoiSearch.searchInCity(new PoiCitySearchOption()
                .cityLimit(false)
                .city(city) //必填
                .keyword(keyword) //必填
                .pageNum(pageNum).pageCapacity(pageCapacity));
    }



    OnGetRoutePlanResultListener routePlanResultListener =new OnGetRoutePlanResultListener() {
        @Override
        public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

        }

        @Override
        public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

        }

        @Override
        public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

        }

        @Override
        public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
            //创建WalkingRouteOverlay实例
            DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaiduMap);
            if (drivingRouteResult.getRouteLines().size() > 0) {
                //获取路径规划数据,(以返回的第一条数据为例)
                //为WalkingRouteOverlay实例设置路径数据
                overlay.setData(drivingRouteResult.getRouteLines().get(0));
                //在地图上绘制WalkingRouteOverlay
                overlay.addToMap();
                overlay.zoomToSpan();
                setLevel();
            }
        }

        @Override
        public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

        }

        @Override
        public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

        }

        @Override
        public void onGetIntegralRouteResult(IntegralRouteResult integralRouteResult) {

        }
    };

    /**
     * 初始化定位参数配置
     */
    private void initLocationOption() {
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        try {
            mLocationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
        //注册监听函数
        mLocationClient.registerLocationListener(myLocationListener);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGnss(true);
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.setLocOption(locationOption);

        MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ic_clear);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, bitmapDescriptor));

        //开始定位
        mLocationClient.start();


        searchLng();

    }
    private void searchLng(){
        Geocoder geocoder=new Geocoder(getApplicationContext());
        try {
            List<Address>  list=geocoder.getFromLocationName("靖远县北寺门",1);
            if (null!=list&&!list.isEmpty()){

                Log.d("获取地点经纬度：",list.get(0).getLatitude()+"--"+list.get(0).getLongitude());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明
            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            city = location.getCity();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            ll_st = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll_st).zoom(18.0f);
            //画圆，主要是这里
            OverlayOptions ooCircle = new CircleOptions().fillColor(0x384d73b3)
                    .center(ll_st).stroke(new Stroke(3, 0x784d73b3))
                    .radius(50);
            mBaiduMap.clear();
            mBaiduMap.addOverlay(ooCircle);
            if (isFirst) {
                isFirst = false;
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }


        }
    }


    OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            // 获取POI检索结果
            if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Hint.showShort(MapActivity.this, "未找到结果");
                return;
            }
            List<PoiInfo> mList = poiResult.getAllPoi();
            if (mList != null && !mList.isEmpty()) {
                //发送延迟消息
                Message message = myHandler.obtainMessage();
                message.what = 1;
                message.obj = mList;
                myHandler.sendMessage(message);
            }
            Log.e("result", list.size() + "");

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            if (poiDetailSearchResult!=null||poiDetailSearchResult.error!=SearchResult.ERRORNO.RESULT_NOT_FOUND){
                Hint.showShort(MapActivity.this, "未找到结果");
                return;
            }
            List<PoiDetailInfo> detailInfoList=poiDetailSearchResult.getPoiDetailInfoList();
            if (detailInfoList!=null&&!detailInfoList.isEmpty()){
                PoiDetailInfo detailInfo=detailInfoList.get(0);
            }
            Log.e("DetailResult", detailInfoList.size()+"");
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }

        //废弃
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    };

    private Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if (msg.what == 0 && etSearch.hasFocus()) {
                //当搜索框500毫秒文本无变化时执行
                String searchValue = (String) msg.obj;
                poiSearch(city, searchValue, 0, 20);
            } else if (msg.what == 1) {
                List<PoiInfo> mList = (List<PoiInfo>) msg.obj;
                list.clear();
                list.addAll(mList);
                poiSearchAdapter.notifyDataSetChanged();
                linearLayout.setVisibility(View.VISIBLE);
            }
            return false;
        }
    });

    /**
     * 搜索框改变监听事件
     */
    class ETSearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Do nothing because afterTextChanged
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Do nothing because afterTextChanged
        }

        @Override
        public void afterTextChanged(Editable s) {
            //有延迟消息，移除
            if (myHandler.hasMessages(0)) {
                myHandler.removeMessages(0);
            }
            //发送延迟消息
            Message message = myHandler.obtainMessage();
            message.what = 0;
            message.obj = s.toString();
            myHandler.sendMessageDelayed(message, 800);
        }
    }


    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (linearLayout.getVisibility()==View.VISIBLE) {
                    linearLayout.setVisibility(View.GONE);
                    return true;
                } else {
                    long doubleExitTime = System.currentTimeMillis();
                    if (mDoubleClickExit && (doubleExitTime - firstExitTime) < 3500) {
                        finish();
                    } else {
                        firstExitTime = doubleExitTime;
                        Hint.showLong(this, getResources().getString(R.string.quit));
                        mDoubleClickExit = true;
                    }
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mPoiSearch.destroy();
        mMapView.onDestroy();
        super.onDestroy();
    }
}
