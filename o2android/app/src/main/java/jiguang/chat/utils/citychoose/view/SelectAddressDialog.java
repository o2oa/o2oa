package jiguang.chat.utils.citychoose.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.AssetManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import jiguang.chat.activity.PersonalActivity;
import jiguang.chat.application.JGApplication;
import jiguang.chat.utils.ThreadUtil;
import jiguang.chat.utils.ToastUtil;
import jiguang.chat.utils.citychoose.XmlParserHandler;
import jiguang.chat.utils.citychoose.model.CityModel;
import jiguang.chat.utils.citychoose.model.DistrictModel;
import jiguang.chat.utils.citychoose.model.ProvinceModel;
import jiguang.chat.utils.citychoose.view.adapter.ArrayWheelAdapter;
import jiguang.chat.utils.citychoose.view.myinterface.OnWheelChangedListener;
import jiguang.chat.utils.citychoose.view.myinterface.SelectAddressInterface;
import jiguang.chat.utils.timechoose.NumericWheelAdapter;


public class SelectAddressDialog implements OnClickListener,
        OnWheelChangedListener {
    private boolean isMyDatas;//是否自定义数据

    public static final int STYLE_ONE = 1;//一级联动
    public static final int STYLE_TWO = 2;//二级联动
    public static final int STYLE_THREE = 3;//三级联动

    private WheelView year;
    private WheelView month;
    private WheelView day;
    /**
     * 所有省
     */
    protected String[] mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市 values - 区
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

    /**
     * key - 区 values - 邮编
     */
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();


    /**
     * 当前区的postion
     */
    protected int mCurrentDistrictNamePosition;
    /**
     * 当前省的postion
     */
    protected int mCurrentProviceNamePosition;
    /**
     * 当前市的postion
     */
    protected int mCurrentCityNamePosition;
    /**
     * 当前省的名称
     */
    protected String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;
    /**
     * 当前区的名称
     */
    protected String mCurrentDistrictName = "";
    /**
     * 当前区的邮政编码
     */
    protected String mCurrentZipCode = "";

    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;
    private Button mBtnConfirm, mBtnCancel;
    private Activity context;
    private Dialog overdialog;
    private SelectAddressInterface selectAdd;
    private int tmp1, tmp2, tmp3;
    private int type;
    private UserInfo mInfo;

    public SelectAddressDialog(Activity context,
                               SelectAddressInterface selectAdd, int type, String[] mProvinceDatas, UserInfo info) {
        this.selectAdd = selectAdd;
        this.type = type;
        this.mInfo = info;
        this.context = context;
        View overdiaView = View.inflate(context,
                R.layout.dialog_select_address, null);

        mViewProvince = (WheelView) overdiaView.findViewById(R.id.id_province);
        mViewCity = (WheelView) overdiaView.findViewById(R.id.id_city);
        mViewDistrict = (WheelView) overdiaView.findViewById(R.id.id_district);
        if (STYLE_TWO == type) {
            mViewDistrict.setVisibility(View.GONE);
        }
        if (STYLE_ONE == type) {
            mViewDistrict.setVisibility(View.GONE);
            mViewCity.setVisibility(View.GONE);
        }
        mBtnConfirm = (Button) overdiaView.findViewById(R.id.btn_confirm);
        mBtnCancel = (Button) overdiaView.findViewById(R.id.btn_cancel);
        overdialog = new Dialog(context, R.style.dialog_lhp);
        Window window = overdialog.getWindow();
        window.setWindowAnimations(R.style.mystyle); // 添加动画
        overdialog.setContentView(overdiaView);
        overdialog.setCanceledOnTouchOutside(true);
        setUpListener();
        if (mProvinceDatas == null) {
            setUpData();
            isMyDatas = false;
        } else {
            isMyDatas = true;
            this.mProvinceDatas = mProvinceDatas;
            mCurrentProviceName = mProvinceDatas[0];
            mViewProvince.setViewAdapter(new ArrayWheelAdapter<>(context,
                    this.mProvinceDatas));
            // 设置可见条目数量
            mViewProvince.setVisibleItems(7);
            mViewCity.setVisibleItems(7);
            mViewDistrict.setVisibleItems(7);
        }

    }

    public SelectAddressDialog(SelectAddressInterface selectAdd) {
        this.selectAdd = selectAdd;
    }

    public void showDateDialog(final PersonalActivity context, final UserInfo myInfo) {
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .create();
        dialog.show();
        Window window = dialog.getWindow();
        // 设置布局
        window.setContentView(R.layout.dialog_select_address);
        // 设置宽高
        window.getDecorView().setPadding(0, 0, 0, 0);

        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        // 设置弹出的动画效果
        window.setWindowAnimations(R.style.mystyle);
        window.setGravity(Gravity.BOTTOM);

        Calendar c = Calendar.getInstance();
        int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
        int curDate = c.get(Calendar.DATE);
        year = (WheelView) window.findViewById(R.id.id_province);
        initYear(context);
        month = (WheelView) window.findViewById(R.id.id_city);
        initMonth(context);
        day = (WheelView) window.findViewById(R.id.id_district);
        initDay(curYear, curMonth, context);

        year.setCurrentItem(curYear - JGApplication.START_YEAR);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);
        year.setVisibleItems(7);
        month.setVisibleItems(7);
        day.setVisibleItems(7);

        // 设置监听
        Button ok = (Button) window.findViewById(R.id.btn_confirm);
        Button cancel = (Button) window.findViewById(R.id.btn_cancel);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = String.format(Locale.CHINA,
                        "%4d-%2d-%2d", year.getCurrentItem() + JGApplication.START_YEAR,//1900
                        month.getCurrentItem() + 1, day.getCurrentItem() + 1);
                selectAdd.setTime(str);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    final Date date = dateFormat.parse(str);
                    ThreadUtil.runInThread(new Runnable() {
                        @Override
                        public void run() {
                            myInfo.setBirthday(date.getTime());
                            JMessageClient.updateMyInfo(UserInfo.Field.birthday, myInfo, new BasicCallback() {
                                @Override
                                public void gotResult(int responseCode, String responseMessage) {
                                    if (responseCode == 0) {
                                        ToastUtil.shortToast(context, "更新成功");
                                    } else {
                                        ToastUtil.shortToast(context, "更新失败" + responseMessage);
                                    }
                                }
                            });
                        }
                    });
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                dialog.cancel();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        LinearLayout cancelLayout = (LinearLayout) window.findViewById(R.id.view_none);
        cancelLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                dialog.cancel();
                return false;
            }
        });
    }

    public void showGenderDialog(final PersonalActivity context, final UserInfo myInfo) {
        final Dialog genderDialog = new Dialog(context, R.style.jmui_default_dialog_style);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_set_sex, null);
        genderDialog.setContentView(view);
        Window window = genderDialog.getWindow();
        window.setWindowAnimations(R.style.mystyle); // 添加动画
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        genderDialog.show();
        genderDialog.setCanceledOnTouchOutside(true);
        Button man = (Button) view.findViewById(R.id.man_rl);
        Button woman = (Button) view.findViewById(R.id.woman_rl);
        Button secrecy = (Button) view.findViewById(R.id.rl_secrecy);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.man_rl:
                        selectAdd.setGender("男");
                        genderDialog.cancel();
                        ThreadUtil.runInThread(new Runnable() {
                            @Override
                            public void run() {
                                myInfo.setGender(UserInfo.Gender.male);
                                JMessageClient.updateMyInfo(UserInfo.Field.gender, myInfo, new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        if (responseCode == 0) {
                                            ToastUtil.shortToast(context, "更新成功");
                                        } else {
                                            ToastUtil.shortToast(context, "更新失败" + responseMessage);
                                        }
                                    }
                                });
                            }
                        });
                        break;
                    case R.id.woman_rl:
                        selectAdd.setGender("女");
                        genderDialog.cancel();
                        ThreadUtil.runInThread(new Runnable() {
                            @Override
                            public void run() {
                                myInfo.setGender(UserInfo.Gender.female);
                                JMessageClient.updateMyInfo(UserInfo.Field.gender, myInfo, new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        if (responseCode == 0) {
                                            ToastUtil.shortToast(context, "更新成功");
                                        } else {
                                            ToastUtil.shortToast(context, "更新失败" + responseMessage);
                                        }
                                    }
                                });
                            }
                        });
                        break;
                    case R.id.rl_secrecy:
                        selectAdd.setGender("保密");
                        genderDialog.cancel();
                        ThreadUtil.runInThread(new Runnable() {
                            @Override
                            public void run() {
                                myInfo.setGender(UserInfo.Gender.unknown);
                                JMessageClient.updateMyInfo(UserInfo.Field.gender, myInfo, new BasicCallback() {
                                    @Override
                                    public void gotResult(int responseCode, String responseMessage) {
                                        if (responseCode == 0) {
                                            ToastUtil.shortToast(context, "更新成功");
                                        } else {
                                            ToastUtil.shortToast(context, "更新失败" + responseMessage);
                                        }
                                    }
                                });
                            }
                        });
                        break;
                    default:
                        break;
                }
            }
        };
        man.setOnClickListener(listener);
        woman.setOnClickListener(listener);
        secrecy.setOnClickListener(listener);
    }

    /**
     * 初始化年
     */
    private void initYear(PersonalActivity context) {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context,
                JGApplication.START_YEAR, JGApplication.END_YEAR);
        numericWheelAdapter.setLabel(" 年");
        year.setViewAdapter(numericWheelAdapter);
        year.setCyclic(true);
    }

    /**
     * 初始化月
     */
    private void initMonth(PersonalActivity context) {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context, 1, 12, "%02d");
        numericWheelAdapter.setLabel(" 月");
        month.setViewAdapter(numericWheelAdapter);
        month.setCyclic(true);
    }

    /**
     * 初始化天
     */
    private void initDay(int arg1, int arg2, PersonalActivity context) {
        NumericWheelAdapter numericWheelAdapter = new NumericWheelAdapter(context, 1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel(" 日");
        day.setViewAdapter(numericWheelAdapter);
        day.setCyclic(true);
    }

    private int getDay(int year, int month) {
        int day;
        boolean flag;
        switch (year % 4) {
            case 0:
                flag = true;
                break;
            default:
                flag = false;
                break;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                day = flag ? 29 : 28;
                break;
            default:
                day = 30;
                break;
        }
        return day;
    }

    public void showDialog() {

        if (overdialog != null) {
            if (mViewProvince != null) mViewProvince.setCurrentItem(mCurrentProviceNamePosition);
            if (mViewCity != null) mViewCity.setCurrentItem(mCurrentCityNamePosition);
            if (mViewDistrict != null) mViewDistrict.setCurrentItem(mCurrentDistrictNamePosition);
            overdialog.show();
            Window win = overdialog.getWindow();
            //弹出的窗口左上右下的距离
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setGravity(Gravity.BOTTOM);
            win.setAttributes(lp);
        }
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            tmp3 = newValue;
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {

        int pCurrent = mViewCity.getCurrentItem();
        tmp2 = pCurrent;
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[] {""};
        }
        mCurrentDistrictName = areas[0];
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(context,
                areas));
        mViewDistrict.setCurrentItem(0);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        tmp1 = pCurrent;
        mCurrentProviceName = mProvinceDatas[pCurrent];
        if (!isMyDatas) {//不是自定义数据
            String[] cities = mCitisDatasMap.get(mCurrentProviceName);
            if (cities == null) {
                cities = new String[] {""};
            }
            mViewCity
                    .setViewAdapter(new ArrayWheelAdapter<String>(context, cities));
            mViewCity.setCurrentItem(0);
            updateAreas();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                if (type == STYLE_TWO) {
                    selectAdd.setAreaString(mCurrentProviceName + "-" + mCurrentCityName);
                } else if (type == STYLE_ONE) {
                    selectAdd.setAreaString(mCurrentProviceName);
                } else {
                    selectAdd.setAreaString(mCurrentProviceName + "-" + mCurrentCityName + "-"
                            + mCurrentDistrictName);
                }
                mCurrentProviceNamePosition = tmp1;
                mCurrentCityNamePosition = tmp2;
                mCurrentDistrictNamePosition = tmp3;
                ThreadUtil.runInThread(new Runnable() {
                    @Override
                    public void run() {
                        mInfo.setAddress(mCurrentProviceName + "-" + mCurrentCityName + "-" + mCurrentDistrictName);
                        JMessageClient.updateMyInfo(UserInfo.Field.region, mInfo, new BasicCallback() {
                            @Override
                            public void gotResult(int responseCode, String responseMessage) {
                                if (responseCode == 0) {
                                    ToastUtil.shortToast(context, "更新成功");
                                } else {
                                    ToastUtil.shortToast(context, "更新失败" + responseMessage);
                                }
                            }
                        });
                    }
                });
                overdialog.cancel();
                break;

            case R.id.btn_cancel:
                overdialog.cancel();
                break;
            default:
                break;
        }
    }

    private void setUpData() {
        initProvinceDatas();
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(context,
                mProvinceDatas));
        // 设置可见条目数量
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        mViewDistrict.setVisibleItems(7);
        updateCities();
        updateAreas();
    }

    private void setUpListener() {
        // 添加change事件
        mViewProvince.addChangingListener(this);
        // 添加change事件
        mViewCity.addChangingListener(this);
        // 添加change事件
        mViewDistrict.addChangingListener(this);
        // 添加onclick事件
        mBtnConfirm.setOnClickListener(this);
        // 添加onclick事件
        mBtnCancel.setOnClickListener(this);
    }

    /**
     * 解析省市区的XML数据
     */

    protected void initProvinceDatas() {
        List<ProvinceModel> provinceList = null;
        AssetManager asset = context.getAssets();
        try {
            InputStream input = asset.open("province_data.xml");
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            //初始化默认选中的省、市、区
            if (provinceList != null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList != null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0)
                            .getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                    mCurrentZipCode = districtList.get(0).getZipcode();
                }
            }

            mProvinceDatas = new String[provinceList.size()];
            for (int i = 0; i < provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j = 0; j < cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j)
                            .getDistrictList();
                    String[] distrinctNameArray = new String[districtList
                            .size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList
                            .size()];
                    for (int k = 0; k < districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        DistrictModel districtModel = new DistrictModel(
                                districtList.get(k).getName(), districtList
                                .get(k).getZipcode());
                        // 区/县对于的邮编，保存到mZipcodeDatasMap
                        mZipcodeDatasMap.put(districtList.get(k).getName(),
                                districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDatasMap
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }
}
