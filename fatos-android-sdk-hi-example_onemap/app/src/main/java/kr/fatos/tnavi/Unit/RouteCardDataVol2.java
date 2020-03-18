package kr.fatos.tnavi.Unit;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import kr.fatos.tnavi.R;

public class RouteCardDataVol2 {

    private static final long serialVersionUID = 1L;

    public String 						   strTypeName;				   // 경로탐색 옵션 타이틀
    public int                             nType;                      // 경로탐색 옵션 (최소, 최단, 추천 등)
    public int                             nLength;                    // 경로 총 길이 (단위 미터)
    public int                             nTime;                      // 경로 총 소요시간 (단위 초)
    public int                             nFee;                       // 요금 정보 (원)
    public int                             nAvgSpeed;                  // 구간 평균 속도
    public int                             nTurnCongestion;            // 구간 혼잡도
    //public int                             nOptionColor;    	       // 경로 옵션 Color
    public boolean charge;
    public Context mContext;

    public RouteCardDataVol2(){
    }

    public RouteCardDataVol2(String strTypeName, int nType, int nLength, int nTime, int nFee, int nAvgSpeed, int nTurnCongestion){

        //readFromParcel(src);
        this.strTypeName = strTypeName;
        this.nType = nType;
        this.nLength = nLength;
        this.nTime = nTime;
        this.nFee = nFee;
        this.nAvgSpeed = nAvgSpeed;
        this.nTurnCongestion= nTurnCongestion;

        if(nFee != 0){
            this.charge = true;
        }else this.charge = false;


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

}
