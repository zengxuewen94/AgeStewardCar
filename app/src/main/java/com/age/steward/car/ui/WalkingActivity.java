package com.age.steward.car.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.age.steward.car.R;
import com.age.steward.car.bean.PointBean;
import com.age.steward.car.expand.ConstDataConfig;
import com.age.steward.car.utils.Hint;
import com.age.steward.car.utils.StringUtils;
import com.age.steward.car.utils.overlayutil.DrivingRouteOverlay;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
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
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.IntegralRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.core.collection.CollectionUtil;


public class WalkingActivity extends RxAppCompatActivity {
    @BindView(R.id.et_walking_activity_start)
    EditText etStart;
    @BindView(R.id.et_walking_activity_end)
    EditText etEnd;
    @BindView(R.id.mapView_walking_activity)
    MapView mMapView;
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
    LatLng ll_st;
    LatLng ll_end;
    String stAddress = "我的位置";
    String endAddress = "";
    float mCurrentZoom = 18f;
    double lastX;
    SensorManager mSensorManager;
    float mCurrentDirection;
    float mCurrentAccracy;
    double mCurrentLat;
    double mCurrentLon;


    private List<PointBean> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walking_activity);
        ButterKnife.bind(this);
        initBaiDuMap();
        initPointData();
        requestGPSPermission();
        initData();


    }

    private void initPointData() {
        PointBean pointBean = new PointBean(new LatLng(28.345138, 112.940350));
        dataList.add(pointBean);

        pointBean = new PointBean(new LatLng(28.345201, 112.939898));
        dataList.add(pointBean);

        pointBean = new PointBean(new LatLng(28.343611, 112.937807));
        dataList.add(pointBean);


        pointBean = new PointBean(new LatLng(28.344778, 112.940480));
        dataList.add(pointBean);


        pointBean = new PointBean(new LatLng(28.26138, 112.992584));
        dataList.add(pointBean);


        pointBean = new PointBean(new LatLng(28.300867, 113.004623));
        dataList.add(pointBean);


        pointBean = new PointBean(new LatLng(28.257317, 113.039693));
        dataList.add(pointBean);


    }


    private void initData() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                double x = sensorEvent.values[SensorManager.DATA_X];
                if (Math.abs(x - lastX) > 1.0) {
                    mCurrentDirection = (float) x;
// 构造定位图层数据
                    MyLocationData myLocationData = new MyLocationData.Builder()
                            .accuracy(mCurrentAccracy)
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(mCurrentDirection)
                            .latitude(mCurrentLat)
                            .longitude(mCurrentLon).build();
                    // 设置定位图层数据
                    mBaiduMap.setMyLocationData(myLocationData);
                }
                lastX = x;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        }, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);
        Bundle bundle = getIntent().getBundleExtra("data");
        ll_st = bundle.getParcelable("ll_st");
        ll_end = bundle.getParcelable("ll_end");
        endAddress = bundle.getString("endAddress");
        etStart.setText(stAddress);
        etEnd.setText(endAddress);


        routePlan(ll_st, ll_end);


        Log.d("驾驶路线", "出发点:" + ll_st.latitude + "," + ll_st.longitude + ",终点：" + ll_end.latitude + "," + ll_end.longitude);
        //calculationCenterPoint(ll_st, ll_end);
    }


    //计算两点的中心点
    private void calculationCenterPoint(LatLng start, LatLng end) {
        double centerLat = (start.latitude + end.latitude) / 2;
        double centerLng = (start.longitude + end.longitude) / 2;
        LatLng center = new LatLng(centerLat, centerLng);

        Log.d("驾驶路线", "中心点:" + centerLat + "," + centerLng);
    }


    private void initBaiDuMap() {

        mBaiduMap = mMapView.getMap();
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);
        mBaiduMap.setMyLocationEnabled(true);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(listener);

        mRoutePlanSearch = RoutePlanSearch.newInstance();
        mRoutePlanSearch.setOnGetRoutePlanResultListener(routePlanResultListener);

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
                mCurrentZoom = arg0.zoom;

                if (mCurrentZoom >= 13f) {
                    addMark();
                } else {
                    removeMark();
                }
                Log.d("地图缩放级别:", mCurrentZoom + "----");
            }

            @Override
            public void onMapStatusChange(MapStatus arg0) {

            }
        });
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        final LatLngBounds bounds = builder.build();
        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLngBounds(bounds);
                mBaiduMap.setMapStatus(update);
            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(WalkingActivity.this, "点击了途径点" + marker.getPosition().longitude + "--" + marker.getPosition().latitude, Toast.LENGTH_LONG).show();

                int canVisit = 0;
                for (PointBean pointBean : dataList) {
                    if (pointBean.isCanVisit()) {
                        canVisit += 1;
                    }
                    if (pointBean.getLatLng().latitude == marker.getPosition().latitude) {
                        pointBean.setCanVisit(true);
                        canVisit += 1;
                        break;
                    }

                }

                if (canVisit >= 2) {
                    mBaiduMap.clear();
                    routePlan(ll_st, ll_end);
                }
                return true;
            }
        });


    }

    //poi检索
    private void poiSearch(String city, String keyword, int pageNum, int pageCapacity) {
        /**
         *  PoiCiySearchOption 设置检索属性
         *  city 检索城市
         *  keyword 检索内容关键字
         *  pageNum 分页页码
         */
        if (StringUtils.isNull(keyword)) {
            return;
        }
        mPoiSearch.searchInCity(new PoiCitySearchOption()
                .cityLimit(false)
                .city(city) //必填
                .keyword(keyword) //必填
                .pageNum(pageNum).pageCapacity(pageCapacity));
    }


    List<OverlayOptions> optionsList = new ArrayList<OverlayOptions>();
    List<Marker> markerList = new ArrayList<>();
    InfoWindow mInfoWindow;

    //添加标记点
    private void addMark() {
        if (optionsList.isEmpty()) {
            BitmapDescriptor bitmap = BitmapDescriptorFactory
                    .fromResource(R.drawable.icon_mark1);
            for (PointBean pointBean : dataList) {
                OverlayOptions option = new MarkerOptions()
                        .position(pointBean.getLatLng())
                        .icon(bitmap);
                Marker marker = (Marker) mBaiduMap.addOverlay(option);
                markerList.add(marker);

                //用来构造InfoWindow的Button
                Button button = new Button(getApplicationContext());
                button.setText("InfoWindow");

                //构造InfoWindow
                //point 描述的位置点
                //-100 InfoWindow相对于point在y轴的偏移量
                mInfoWindow = new InfoWindow(button, pointBean.getLatLng(), -100);
                //使InfoWindow生效
                mBaiduMap.showInfoWindow(mInfoWindow);
            }

        }

    }


    //移除标识
    private void removeMark() {
        if (!CollectionUtil.isEmpty(markerList)) {
            for (Marker marker :
                    markerList) {
                marker.remove();
            }
        }
        mBaiduMap.hideInfoWindow();
    }

    /**
     * 线路
     */
    private void routePlan(LatLng loc_start, LatLng loc_end) {
        PlanNode stNode = PlanNode.withLocation(loc_start);
        PlanNode enNode = PlanNode.withLocation(loc_end);
        DrivingRoutePlanOption drivingRoutePlanOption = new DrivingRoutePlanOption();


        //添加途经点
        List<PlanNode> planNodeList = new ArrayList<>();
        for (PointBean pointBean : dataList) {
            if (pointBean.isCanVisit()) {
                PlanNode node = PlanNode.withLocation(pointBean.getLatLng());
                planNodeList.add(node);
            }
        }

        drivingRoutePlanOption.passBy(planNodeList);
        mRoutePlanSearch.drivingSearch((drivingRoutePlanOption)
                .from(stNode)
                .to(enNode));
    }

    DrivingRouteOverlay overlay;
    OnGetRoutePlanResultListener routePlanResultListener = new OnGetRoutePlanResultListener() {
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
            overlay = new DrivingRouteOverlay(mBaiduMap);
            List<DrivingRouteLine> list = drivingRouteResult.getRouteLines();
            if (list == null || list.isEmpty()) {
                Hint.showShort(WalkingActivity.this, "没有检索到结果");

            } else {

                //获取路径规划数据,(以返回的第一条数据为例)
                //为WalkingRouteOverlay实例设置路径数据
                overlay.setData(drivingRouteResult.getRouteLines().get(0));
                //在地图上绘制WalkingRouteOverlay
                overlay.addToMap();
                overlay.zoomToSpan();
                setLevel();

                for (DrivingRouteLine drivingRouteLine :
                        drivingRouteResult.getRouteLines()) {

                    Log.d("路径规划：", "路长：" + drivingRouteLine.getDistance() + "--时长：" + drivingRouteLine.getDuration());

                }
//                overlay.setData(drivingRouteResult.getRouteLines().get(1));
//                //在地图上绘制WalkingRouteOverlay
//                overlay.addToMap();
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

    private int[] distanceArr = new int[]{20, 50, 100, 200, 500, 1000, 2000, 5000, 10000, 20000, 25000, 50000, 100000, 200000, 500000, 1000000, 2000000, 5000000, 10000000};
    private int[] levelArr = new int[]{21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3};

    //设置地图缩放级别
    private void setLevel() {
        //起点： latitude纬度           longitude经度
        if (ll_st != null) {

            int distance = (int) DistanceUtil.getDistance(ll_st, ll_end);
            int level = getLevel(distance);
            //设置缩放级别

            Log.d("驾驶路线", "直线距离:" + distance + "--");
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
        locationOption.setScanSpan(0);
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
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
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

            Log.e("locData", latitude + "--" + longitude + "--" + radius + "--" + coorType + "--" + errorCode + "--" + location.getCity() + location.getAddrStr());
            city = location.getCity();
//            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(location.getRadius())
//                    // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(location.getDirection())
//                    .latitude(location.getLatitude())
//                    .longitude(location.getLongitude()).build();
//            mBaiduMap.setMyLocationData(locData);
            ll_st = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll_st).zoom(mCurrentZoom);
            //画圆，主要是这里
            OverlayOptions ooCircle = new CircleOptions().fillColor(0x384d73b3)
                    .center(ll_st).stroke(new Stroke(3, 0x784d73b3))
                    .radius(50);
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
                Hint.showShort(WalkingActivity.this, "未找到结果");
                return;
            }
            Log.e("result", list.size() + "");

        }

        @Override
        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
            if (poiDetailSearchResult != null || poiDetailSearchResult.error != SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Hint.showShort(WalkingActivity.this, "未找到结果");
                return;
            }
            List<PoiDetailInfo> detailInfoList = poiDetailSearchResult.getPoiDetailInfoList();
            if (detailInfoList != null && !detailInfoList.isEmpty()) {
                PoiDetailInfo detailInfo = detailInfoList.get(0);
            }
            Log.e("DetailResult", detailInfoList.size() + "");
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }

        //废弃
        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }
    };


    /**
     * 请求存储权限
     */
    private void requestExternalPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // 申请权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, ConstDataConfig.REQUEST_CODE_EXTERNAL_STORAGE);
                return;
            }
        }

    }

    /**
     * 请求存储权限
     */
    private void requestGPSPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // 申请权限
                        ActivityCompat.requestPermissions(WalkingActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, ConstDataConfig.REQUEST_CODE_FINE_LOCATION);

                    }
                }, 1500);
            }
            initLocationOption();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ConstDataConfig.REQUEST_CODE_EXTERNAL_STORAGE) {
            // 摄像头权限申请
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMapView.onResume();
            } else {
                // 被禁止授权
                Hint.showShort(this, R.string.external_storage);
            }
        } else if (requestCode == ConstDataConfig.REQUEST_CODE_FINE_LOCATION) {
            // 摄像头权限申请
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestGPSPermission();
            } else {
                // 被禁止授权
                Hint.showShort(this, R.string.external_storage);
            }
        }
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
