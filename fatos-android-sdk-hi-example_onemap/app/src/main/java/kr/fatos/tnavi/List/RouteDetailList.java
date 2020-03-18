package kr.fatos.tnavi.List;

public class RouteDetailList {
    private int m_nSecSumTime = 0;
    // 좌표 관련
    private String m_strCurCoordX, m_strCurCoordY;

    private String m_strTextInfoName;// service link textinfo
    private String m_strSubInfo; // 시간 /거리 표출
    private String m_strDongName; // 행정동명 표출
    private String m_strTime, m_strDist; // 시간 /거리 표출

    // tbt code
    private int m_nTbtCode; // turn code or tbt code 관리
    public String getCoordX() {
        return m_strCurCoordX;
    }
    public void setCoordX(String strCoordX) {
        this.m_strCurCoordX = strCoordX;
    }
    public String getCoordY() {
        return m_strCurCoordY;
    }

    public void setCoordY(String strCoordY) {
        this.m_strCurCoordY = strCoordY;
    }

    public String getSubInfo() {
        return m_strSubInfo;
    }
    public void setSubInfo(String strSubInfo) {
        this.m_strSubInfo = strSubInfo;
    }

    public int getSecSumTime() {
        return m_nSecSumTime;
    }

    public String getTime() {
        return m_strTime;
    }
    public String getDist() {
        return m_strDist;
    }
    public void setTimeDistInfo(String strTime, String strDist, int nSecTime) {
        this.m_strTime = strTime;
        this.m_strDist = strDist;
        this.m_nSecSumTime = nSecTime;
    }

    public String getDongName() {
        return m_strDongName;
    }
    public void setDongName(String strDongName) {
        this.m_strDongName = strDongName;
    }

    public String getTxtInfoName() {
        return m_strTextInfoName;
    }
    public void setTxtInfoName(String strCrossName) {
        this.m_strTextInfoName = strCrossName;
    }

    public int getTbtCode() {
        return m_nTbtCode;
    }
    public void setTbtCode(int tbtcode) {
        this.m_nTbtCode = tbtcode;
    }
}
