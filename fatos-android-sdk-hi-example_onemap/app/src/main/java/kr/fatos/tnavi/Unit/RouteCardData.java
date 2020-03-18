package kr.fatos.tnavi.Unit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;


import kr.fatos.tnavi.R;

public class RouteCardData implements Parcelable {

    private static final long serialVersionUID = 1L;

    public String 						   strTypeName;				   // 경로탐색 옵션 타이틀
    public int                             nType;                      // 경로탐색 옵션 (최소, 최단, 추천 등)
    public int                             nLength;                    // 경로 총 길이 (단위 미터)
    public int                             nTime;                      // 경로 총 소요시간 (단위 초)
    public int                             nFee;                       // 요금 정보 (원)
    public int                             nAvgSpeed;                  // 구간 평균 속도
    public int                             nTurnCongestion;            // 구간 혼잡도
    //public int                             nOptionColor;    	       // 경로 옵션 Color
    public Drawable nOptionColor;    	       // 경로 옵션 drawable
    public boolean charge;
    public Context mContext;

    //for test
    public RouteCardData(){
        this.strTypeName = "strTypeName";
        this.nType = 1;
        this.nLength = 123;
        this.nTime = 6000;
        this.nFee = 50000;
        this.nAvgSpeed = 33;
        this.nTurnCongestion= 0;
        this.nOptionColor = mContext.getDrawable(R.drawable.info_bg_recomm);
    }

    public RouteCardData(Parcel src){

        readFromParcel(src);
       /* this.strTypeName = src.readString();
        this.nType = src.readInt();
        this.nLength = src.readInt();
        this.nTime = src.readInt();
        this.nFee = src.readInt();
        this.nAvgSpeed = src.readInt();
        this.nTurnCongestion= src.readInt();
        this.nOptionColor = src.readInt();*/
    }

    public RouteCardData(String strTypeName, int nType, int nLength, int nTime, int nFee, int nAvgSpeed, int nTurnCongestion, Context mContext){
        this.strTypeName = strTypeName;
        this.nType = nType;
        this.nLength = nLength;
        this.nTime = nTime;
        this.nFee = nFee;
        this.nAvgSpeed = nAvgSpeed;
        this.nTurnCongestion = nTurnCongestion;
        this.mContext = mContext;


        if(nFee != 0){
            this.charge = true;
        }else this.charge = false;

        switch(this.nType) {

            case RouteType.ROUTE_RECOMM: //추천
                this.nOptionColor =  mContext.getResources().getDrawable(R.drawable.info_bg_recomm);
                break;
            case RouteType.ROUTE_OPTION2: //추천2
                this.nOptionColor =  mContext.getResources().getDrawable(R.drawable.info_bg_recomm);
                break;
            case RouteType.ROUTE_EXP: //고속
                this.nOptionColor =  mContext.getResources().getDrawable(R.drawable.info_bg_exp);
                break;
            case RouteType.ROUTE_GENERAL: //일반
                this.nOptionColor =  mContext.getResources().getDrawable(R.drawable.info_bg_general);
                break;
            case RouteType.ROUTE_SHORT: //최단
                this.nOptionColor =  mContext.getResources().getDrawable(R.drawable.info_bg_short);
                break;
            case RouteType.ROUTE_FREE: //무료
                this.nOptionColor =  mContext.getResources().getDrawable(R.drawable.info_bg_free);
                break;
        }


    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RouteCardData createFromParcel(Parcel in) {
            return new RouteCardData(in);
        }

        public RouteCardData[] newArray(int size) {
            return new RouteCardData[size];
        }
    };


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(strTypeName);
        dest.writeInt(nType);
        dest.writeInt(nLength);
        dest.writeInt(nTime);
        dest.writeInt(nFee);
        dest.writeInt(nAvgSpeed);
        dest.writeInt(nTurnCongestion);
    }

    private void readFromParcel(Parcel in){
        strTypeName = in.readString();
        nType = in.readInt();
        nLength = in.readInt();
        nTime = in.readInt();
        nFee = in.readInt();
        nAvgSpeed = in.readInt();
        nTurnCongestion = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public boolean isCharge() {
        return charge;
    }

    public void setCharge(boolean charge) {
        this.charge = charge;
    }

    public String getStrTypeName() {
        return strTypeName;
    }

    public void setStrTypeName(String strTypeName) {
        this.strTypeName = strTypeName;
    }

    public int getnType() {
        return nType;
    }

    public void setnType(int nType) {
        this.nType = nType;
    }

    public int getnLength() {
        return nLength;
    }

    public void setnLength(int nLength) {
        this.nLength = nLength;
    }

    public int getnTime() {
        return nTime;
    }

    public void setnTime(int nTime) {
        this.nTime = nTime;
    }

    public int getnFee() {
        return nFee;
    }

    public void setnFee(int nFee) {
        this.nFee = nFee;
    }

    public int getnAvgSpeed() {
        return nAvgSpeed;
    }

    public void setnAvgSpeed(int nAvgSpeed) {
        this.nAvgSpeed = nAvgSpeed;
    }

    public int getnTurnCongestion() {
        return nTurnCongestion;
    }

    public void setnTurnCongestion(int nTurnCongestion) {
        this.nTurnCongestion = nTurnCongestion;
    }

    public Drawable getnOptionColor() {
        return nOptionColor;
    }

    public void setnOptionColor(Drawable nOptionColor) {
        this.nOptionColor = nOptionColor;
    }
}
