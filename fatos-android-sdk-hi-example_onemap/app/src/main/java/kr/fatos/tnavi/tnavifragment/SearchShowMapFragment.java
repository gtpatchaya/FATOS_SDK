package kr.fatos.tnavi.tnavifragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import biz.fatossdk.newanavi.ANaviApplication;
import kr.fatos.tnavi.Code.TNaviActionCode;
import kr.fatos.tnavi.Lib.GoLib;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.TNaviMainActivity;
import kr.fatos.tnavi.Unit.NPoiItem;

public class SearchShowMapFragment extends Fragment implements View.OnClickListener{
    Button button_back, poiview_result_route_btn;
    static TextView textView_PoiName, textView_Name;
    static TextView textView_Address;
    ImageButton imageButton_allCancel, imageButton_poiDefault;
    private final static String TAG = SearchShowMapFragment.class.getSimpleName();
    View activity_picker;
    static Locale currentLocale;

    double m_dX, m_dY, m_dGetX, m_dGetY;
    String m_strName;
    String m_strAddress;
    static Context mContext;
    static Activity mActivity;
    static SearchTask searchTask;
    String args;
    String app_mode;
    int m_nSearchKind = 1;
    public static boolean isTouch = false;

    NPoiItem items;

    public static SearchShowMapFragment newInstance()
    {
        SearchShowMapFragment fragment = new SearchShowMapFragment();

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchshowmap, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        items = Parcels.unwrap(getArguments().getParcelable(TNaviActionCode.POI_ITEM));
        mContext = view.getContext();
        mActivity = (TNaviMainActivity)getActivity();
        if(mActivity!=null) {
            ((TNaviMainActivity) mActivity).showTbtLayout(false);
        }
        ANaviApplication mApplication = (ANaviApplication)mContext.getApplicationContext();
        currentLocale = mApplication.getFatosLocale();

        args = getArguments().getString(TNaviActionCode.ROUTE_VIA_OR_GOAL);
        app_mode = getArguments().getString(TNaviActionCode.APP_MODE);
        m_nSearchKind = getArguments().getInt(TNaviActionCode.SEARCH_KIND);

        button_back = view.findViewById(R.id.button_back);
        textView_PoiName = view.findViewById(R.id.textView_PoiName);
        imageButton_allCancel = view.findViewById(R.id.imageButton_allCancel);
        imageButton_poiDefault = view.findViewById(R.id.imageButton_poiDefault);

        activity_picker = view.findViewById(R.id.include_popup);
        textView_Name = activity_picker.findViewById(R.id.textView_Name);
        textView_Address = activity_picker.findViewById(R.id.textView_Address);
        poiview_result_route_btn = activity_picker.findViewById(R.id.poiview_result_route_btn);

        if(m_nSearchKind == 0 || args.equals(TNaviActionCode.CHANGE_VIA_GO_ROUTE))
        {
            poiview_result_route_btn.setBackgroundResource(R.drawable.btn_start_n);
        }
        else
        {
            poiview_result_route_btn.setBackgroundResource(R.drawable.selector_rg);
        }

        button_back.setOnClickListener(this);
        imageButton_allCancel.setOnClickListener(this);
        imageButton_poiDefault.setOnClickListener(this);
        poiview_result_route_btn.setOnClickListener(this);

        m_dX = items.getLocationPointX();
        m_dY = items.getLocationPointY();
        m_dGetX = m_dX;
        m_dGetY = m_dY;
        m_strName = items.getEnglishName();
        m_strAddress = items.getAddressFull();

        textView_PoiName.setText(m_strName);
        textView_Name.setText(m_strName);
        textView_Address.setText(m_strAddress);

        ((TNaviMainActivity)getActivity()).MovePoint(m_dX, m_dY, m_strAddress);
    }


    @Override
    public void onDestroy() {
        isTouch = false;
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.button_back : {
                getActivity().onBackPressed();
                break;
            }

            case R.id.imageButton_allCancel : {
                Bundle bundle = new Bundle();
                bundle.putString(TNaviActionCode.APP_MODE, TNaviActionCode.APP_MODE_DEFAULT);
                ((TNaviMainActivity)getActivity()).setSearchWord("");
                GoLib.getInstance().goTNaviMainActivity(getContext(), bundle);

                break;
            }

            case R.id.imageButton_poiDefault :
                isTouch = false;
                ((TNaviMainActivity)getActivity()).setDriveInfoThreadFlag(false);

                textView_PoiName.setText(m_strName);
                textView_Name.setText(m_strName);
                textView_Address.setText(m_strAddress);


                m_dGetX = m_dX;
                m_dGetY = m_dY;

                ((TNaviMainActivity)getActivity()).MovePoint(m_dX, m_dY, m_strAddress);

                break;

                //// TODO: 2019. 1. 24. 맵이동 후 경탐시에 리스트에 해당 item으로 바꿔줘야함
            case R.id.poiview_result_route_btn :

                String addrText = "";

                if(isTouch){
                    addrText = (String)textView_Address.getText();
                }else{
                    addrText = (String)textView_Name.getText();
                }


                if(args.equals(TNaviActionCode.CHANGE_VIA_GO_ROUTE))
                {
//                    Log.e(TAG,"CHANGE_VIA_GO_ROUTE");
                    ((TNaviMainActivity) getActivity()).set_strAddr(addrText,0);
                    ((TNaviMainActivity) getActivity()).setStartCoord(items.getLocationPointX(), items.getLocationPointY());
                }
                else if(args.equals(TNaviActionCode.CHANGE_GOAL_GO_ROUTE))
                {
//                    Log.e(TAG,"CHANGE_GOAL_GO_ROUTE");
                    ((TNaviMainActivity)getActivity()).set_strAddr(addrText,1);
                }
                else if(args.equals(TNaviActionCode.JUST_GOAL)){
//                    Log.e(TAG,"JUST_GOAL : " + m_nSearchKind);

                    if(m_nSearchKind == 0) {
                        ((TNaviMainActivity) getActivity()).set_strAddr(addrText, 0);
                        ((TNaviMainActivity) getActivity()).setStartCoord(items.getLocationPointX(), items.getLocationPointY());
                    }
                    else {

                        if(((TNaviMainActivity) getActivity()).get_strAddr()[0].equals("")){
                            ((TNaviMainActivity) getActivity()).set_strAddr(getResources().getString(R.string.string_via_hint),0);
                        }

                        ((TNaviMainActivity) getActivity()).set_strAddr(addrText, 1);
                    }
                }

                if(m_nSearchKind != 0) {
                    ((TNaviMainActivity) getActivity()).routeTovia(m_dGetX, m_dGetY, args, items);
                }

                if(searchTask != null ){
                    searchTask.cancel(true);
                }

                if(m_nSearchKind == 0) {
                    ((TNaviMainActivity) getActivity()).setSearchWord(addrText);
                    getActivity().onBackPressed();
                }

                break;
        }
    }

    public void setName(double[] i_dCoord)
    {
        m_dGetX = i_dCoord[0];
        m_dGetY = i_dCoord[1];

        String strX = String.format("%.4f", m_dGetX);
        String strY = String.format("%.4f", m_dGetY);

        if(isTouch){
            textView_Name.setText("X : " + strX + ", Y : " + strY);
        }
    }

    public void setAddrText(String i_strAddr)
    {
        textView_Address.setText(i_strAddr);

    }

    //address 만 표출되는 문제 있음. POI 의  wgs값과 동일할 경우 그냥 EnglishName을 뿌려주는 방식으로 하던가..
    public void startSearchAddress()
    {
        if(isTouch){
            double[] params = new double[2];
            params[0] = m_dGetY;
            params[1] = m_dGetX;
            try{
                searchTask = new SearchTask();
                searchTask.execute(params);
                //Log.d(TAG,"국내 주소 : " + ((TNaviMainActivity)getActivity()).m_FMInterface.FM_GetAddressVol2(m_dGetX,m_dGetY));
            }catch (Exception e){

            }
        }
    }

    public static class SearchTask extends AsyncTask<double[], Void, Address> {

        @Override
        protected Address doInBackground(double[]... geoPoints)
        {
            try
            {
                if(isCancelled()){
                    return null;
                }

                Geocoder geoCoder = new Geocoder(mContext,currentLocale);

                double[] latYlonX = geoPoints[0];

                double longitude = latYlonX[0];
                double latitude = latYlonX[1];

                List<Address> addresses = geoCoder.getFromLocation(longitude,latitude, 1);
                if (addresses.size() > 0)
                    return addresses.get(0);
            }
            catch (IOException ex)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Address address)
        {
            if(address != null){
                setAddress(address);
            }
        }

    }

    public static void setAddress(Address address){
        boolean bAppended = false;

        if (null != address ) {
            StringBuilder sb = new StringBuilder();
            if(address.getAdminArea() != null && address.getAdminArea().equals("") == false){
                sb.append(address.getAdminArea());
                bAppended = true;
            }

            if(address.getLocality() != null && address.getLocality().equals("") == false){
                //AdminArea와 Locality가 같은경우 덮어쓰지 않음 (19/01/31 for FATOSMapHi)
                if(!sb.toString().equals(address.getLocality())){
                    if(bAppended == true)
                        sb.append(" ");//sb.append("/");
                    sb.append(address.getLocality());
                    bAppended = true;
                }
            }

            if(address.getSubLocality() != null && address.getSubLocality().equals("") == false){

                if(bAppended == true)
                    sb.append(" ");//sb.append("/");

                sb.append(address.getSubLocality());
                bAppended = true;
            }
            if(address.getThoroughfare() != null && address.getThoroughfare().equals("") == false){

                if(bAppended == true)
                    sb.append(" ");//sb.append("/");

                sb.append(address.getThoroughfare());
                bAppended = true;
            }
            //Log.d(TAG,"address Async ::" + sb.toString());
            final String res = sb.toString();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(mActivity!=null) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView_Address.setText(res);
                                textView_PoiName.setText(res);
                            }
                        });
                    }
                }
            }).start();
        }

    }

}
