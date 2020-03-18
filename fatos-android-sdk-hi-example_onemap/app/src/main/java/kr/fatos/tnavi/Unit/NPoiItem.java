package kr.fatos.tnavi.Unit;

import androidx.annotation.NonNull;

@org.parceler.Parcel
public class NPoiItem implements Comparable<NPoiItem> {
    private String LocalName;
    private String CategoryCode;
    private String Country;
    private String EnglishName;
    private String HouseNumber;
    private String LocalCategoryCode;
    private String NostraId;
    private String Postcode;
    private String TelephoneNumber;
    private double LocationPointX;
    private double LocationPointY;
    private double Route1X;
    private double Route1Y;
    private double Route2X;
    private double Route2Y;
    private double Route3X;
    private double Route3Y;
    private double Route4X;
    private double Route4Y;
    private String AdminLevel1Code;
    private String AdminLevel1EnglishName;
    private String AdminLevel1LocalName;
    private String AdminLevel2Code;
    private String AdminLevel2EnglishName;
    private String AdminLevel2LocalName;
    private String AdminLevel3Code;
    private String AdminLevel3EnglishName;
    private String AdminLevel3LocalName;
    private String AdminLevel4Code;
    private String AdminLevel4Englishname;
    private String AdminLevel4LocalName;
    private String BranchEnglishName;
    private String BranchLocalName;
    private double Distance;
    private int Order;
    private String strName;

    // poi data items
    private int m_nSectionGubun  = 0; // 0 : item , 1 : section , 2 : expand item, 3 : expand back
    private int m_nSearchGubun  = 0; // 0 : 어디야? ,1 : Google , 2 : Naver , 3 : T map
    private int m_nSearchItemCount = 0;
    private boolean m_bShowSubMenu = false;

//    public String id; //NostraId
//    public String name; //EnglishName
//    public String telNo; //TelephoneNumber
//    public String frontLat; //LocationPointX
//    public String frontLon; //LocationPointY
//    public String upperAddrName; //AdminLevel1EnglishName
//    public String middleAddrName; //AdminLevel2EnglishName
//    public String lowerAddrName; //AdminLevel3EnglishName
//    public String detailAddrName; //AdminLevel4EnglishName
    private String noorLat;
    private String noorLon;
    private String firstNo;
    private String secondNo;
    private String roadName;
    private String firstBuildNo;
    private String secondBuildNo;
    private String radius;
    private String bizName;
    private String upperBizName;
    private String middleBizName;
    private String lowerBizName;
    private String detailBizName;
    private String rpFlag;
    private String parkFlag;
    private String detailInfoFlag;
    private String desc;

    public NPoiItem()
    {
        init();
    }

    public void init()
    {
        LocalName = "";
        CategoryCode = "";
        Country = "";
        EnglishName = "";
        HouseNumber = "";
        LocalCategoryCode = "";
        NostraId = "";
        Postcode = "";
        TelephoneNumber = "";
        LocationPointX  = 0;
        LocationPointY = 0;
        Route1X = 0;
        Route1Y = 0;
        Route2X = 0;
        Route2Y = 0;
        Route3X = 0;
        Route3Y = 0;
        Route4X = 0;
        Route4Y = 0;
        AdminLevel1Code = "";
        AdminLevel1EnglishName = "";
        AdminLevel1LocalName = "";
        AdminLevel2Code = "";
        AdminLevel2EnglishName = "";
        AdminLevel2LocalName = "";
        AdminLevel3Code = "";
        AdminLevel3EnglishName = "";
        AdminLevel3LocalName = "";
        AdminLevel4Code = "";
        AdminLevel4Englishname = "";
        AdminLevel4LocalName = "";
        BranchEnglishName = "";
        BranchLocalName = "";
        Distance = 0;
        Order = 0;
        strName = "";

        noorLat = "";
        noorLon = "";
        firstNo = "";
        secondNo = "";
        roadName = "";
        firstBuildNo = "";
        secondBuildNo = "";
        radius = "";
        bizName = "";
        upperBizName = "";
        middleBizName = "";
        lowerBizName = "";
        detailBizName = "";
        rpFlag = "";
        parkFlag = "";
        detailInfoFlag = "";
        desc = "";
    }

    public String getLocalName() {
        return LocalName;
    }

    public void setLocalName(String localName) {
        LocalName = localName;
    }

    public String getCategoryCode() {
        return CategoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        CategoryCode = categoryCode;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getEnglishName() {
        return EnglishName;
    }

    public void setEnglishName(String englishName) {
        EnglishName = englishName;
    }

    public String getHouseNumber() {
        return HouseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        HouseNumber = houseNumber;
    }

    public String getLocalCategoryCode() {
        return LocalCategoryCode;
    }

    public void setLocalCategoryCode(String localCategoryCode) {
        LocalCategoryCode = localCategoryCode;
    }

    public String getNostraId() {
        return NostraId;
    }

    public void setNostraId(String nostraId) {
        NostraId = nostraId;
    }

    public String getPostcode() {
        return Postcode;
    }

    public void setPostcode(String postcode) {
        Postcode = postcode;
    }

    public String getTelephoneNumber() {
        return TelephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        TelephoneNumber = telephoneNumber;
    }

    public double getLocationPointX() {
        return LocationPointX;
    }

    public void setLocationPointX(double locationPointX) {
        LocationPointX = locationPointX;
    }

    public double getLocationPointY() {
        return LocationPointY;
    }

    public void setLocationPointY(double locationPointY) {
        LocationPointY = locationPointY;
    }

    public double getRoute1X() {
        return Route1X;
    }

    public void setRoute1X(double route1X) {
        Route1X = route1X;
    }

    public double getRoute1Y() {
        return Route1Y;
    }

    public void setRoute1Y(double route1Y) {
        Route1Y = route1Y;
    }

    public double getRoute2X() {
        return Route2X;
    }

    public void setRoute2X(double route2X) {
        Route2X = route2X;
    }

    public double getRoute2Y() {
        return Route2Y;
    }

    public void setRoute2Y(double route2Y) {
        Route2Y = route2Y;
    }

    public double getRoute3X() {
        return Route3X;
    }

    public void setRoute3X(double route3X) {
        Route3X = route3X;
    }

    public double getRoute3Y() {
        return Route3Y;
    }

    public void setRoute3Y(double route3Y) {
        Route3Y = route3Y;
    }

    public double getRoute4X() {
        return Route4X;
    }

    public void setRoute4X(double route4X) {
        Route4X = route4X;
    }

    public double getRoute4Y() {
        return Route4Y;
    }

    public void setRoute4Y(double route4Y) {
        Route4Y = route4Y;
    }

    public String getAdminLevel1Code() {
        return AdminLevel1Code;
    }

    public void setAdminLevel1Code(String adminLevel1Code) {
        AdminLevel1Code = adminLevel1Code;
    }

    public String getAdminLevel1EnglishName() {
        return AdminLevel1EnglishName;
    }

    public void setAdminLevel1EnglishName(String adminLevel1EnglishName) {
        AdminLevel1EnglishName = adminLevel1EnglishName;
    }

    public String getAdminLevel1LocalName() {
        return AdminLevel1LocalName;
    }

    public void setAdminLevel1LocalName(String adminLevel1LocalName) {
        AdminLevel1LocalName = adminLevel1LocalName;
    }

    public String getAdminLevel2Code() {
        return AdminLevel2Code;
    }

    public void setAdminLevel2Code(String adminLevel2Code) {
        AdminLevel2Code = adminLevel2Code;
    }

    public String getAdminLevel2EnglishName() {
        return AdminLevel2EnglishName;
    }

    public void setAdminLevel2EnglishName(String adminLevel2EnglishName) {
        AdminLevel2EnglishName = adminLevel2EnglishName;
    }

    public String getAdminLevel2LocalName() {
        return AdminLevel2LocalName;
    }

    public void setAdminLevel2LocalName(String adminLevel2LocalName) {
        AdminLevel2LocalName = adminLevel2LocalName;
    }

    public String getAdminLevel3Code() {
        return AdminLevel3Code;
    }

    public void setAdminLevel3Code(String adminLevel3Code) {
        AdminLevel3Code = adminLevel3Code;
    }

    public String getAdminLevel3EnglishName() {
        return AdminLevel3EnglishName;
    }

    public void setAdminLevel3EnglishName(String adminLevel3EnglishName) {
        AdminLevel3EnglishName = adminLevel3EnglishName;
    }

    public String getAdminLevel3LocalName() {
        return AdminLevel3LocalName;
    }

    public void setAdminLevel3LocalName(String adminLevel3LocalName) {
        AdminLevel3LocalName = adminLevel3LocalName;
    }

    public String getAdminLevel4Code() {
        return AdminLevel4Code;
    }

    public void setAdminLevel4Code(String adminLevel4Code) {
        AdminLevel4Code = adminLevel4Code;
    }

    public String getAdminLevel4Englishname() {
        return AdminLevel4Englishname;
    }

    public void setAdminLevel4Englishname(String adminLevel4Englishname) {
        AdminLevel4Englishname = adminLevel4Englishname;
    }

    public String getAdminLevel4LocalName() {
        return AdminLevel4LocalName;
    }

    public void setAdminLevel4LocalName(String adminLevel4LocalName) {
        AdminLevel4LocalName = adminLevel4LocalName;
    }

    public String getBranchEnglishName() {
        return BranchEnglishName;
    }

    public void setBranchEnglishName(String branchEnglishName) {
        BranchEnglishName = branchEnglishName;
    }

    public String getBranchLocalName() {
        return BranchLocalName;
    }

    public void setBranchLocalName(String branchLocalName) {
        BranchLocalName = branchLocalName;
    }

    public double getDistance() {
        return Distance;
    }

    public void setDistance(double distance) {
        Distance = distance;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        Order = order;
    }

    public String getAddressFull()
    {
        return AdminLevel1EnglishName + " " + AdminLevel2EnglishName + " " + AdminLevel3EnglishName + " " + AdminLevel4Englishname;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public int getM_nSectionGubun() {
        return m_nSectionGubun;
    }

    public void setM_nSectionGubun(int m_nSectionGubun) {
        this.m_nSectionGubun = m_nSectionGubun;
    }

    public int getM_nSearchGubun() {
        return m_nSearchGubun;
    }

    public void setM_nSearchGubun(int m_nSearchGubun) {
        this.m_nSearchGubun = m_nSearchGubun;
    }

    public int getM_nSearchItemCount() {
        return m_nSearchItemCount;
    }

    public void setM_nSearchItemCount(int m_nSearchItemCount) {
        this.m_nSearchItemCount = m_nSearchItemCount;
    }

    public boolean isM_bShowSubMenu() {
        return m_bShowSubMenu;
    }

    public void setM_bShowSubMenu(boolean m_bShowSubMenu) {
        this.m_bShowSubMenu = m_bShowSubMenu;
    }

    public String getNoorLat() {
        return noorLat;
    }

    public void setNoorLat(String noorLat) {
        this.noorLat = noorLat;
    }

    public String getNoorLon() {
        return noorLon;
    }

    public void setNoorLon(String noorLon) {
        this.noorLon = noorLon;
    }

    public String getFirstNo() {
        return firstNo;
    }

    public void setFirstNo(String firstNo) {
        this.firstNo = firstNo;
    }

    public String getSecondNo() {
        return secondNo;
    }

    public void setSecondNo(String secondNo) {
        this.secondNo = secondNo;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getFirstBuildNo() {
        return firstBuildNo;
    }

    public void setFirstBuildNo(String firstBuildNo) {
        this.firstBuildNo = firstBuildNo;
    }

    public String getSecondBuildNo() {
        return secondBuildNo;
    }

    public void setSecondBuildNo(String secondBuildNo) {
        this.secondBuildNo = secondBuildNo;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }

    public String getUpperBizName() {
        return upperBizName;
    }

    public void setUpperBizName(String upperBizName) {
        this.upperBizName = upperBizName;
    }

    public String getMiddleBizName() {
        return middleBizName;
    }

    public void setMiddleBizName(String middleBizName) {
        this.middleBizName = middleBizName;
    }

    public String getLowerBizName() {
        return lowerBizName;
    }

    public void setLowerBizName(String lowerBizName) {
        this.lowerBizName = lowerBizName;
    }

    public String getDetailBizName() {
        return detailBizName;
    }

    public void setDetailBizName(String detailBizName) {
        this.detailBizName = detailBizName;
    }

    public String getRpFlag() {
        return rpFlag;
    }

    public void setRpFlag(String rpFlag) {
        this.rpFlag = rpFlag;
    }

    public String getParkFlag() {
        return parkFlag;
    }

    public void setParkFlag(String parkFlag) {
        this.parkFlag = parkFlag;
    }

    public String getDetailInfoFlag() {
        return detailInfoFlag;
    }

    public void setDetailInfoFlag(String detailInfoFlag) {
        this.detailInfoFlag = detailInfoFlag;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public int compareTo(@NonNull NPoiItem nPoiItem) {
        if(this.getDistance() < nPoiItem.getDistance())
        {
            return -1;
        }
        else if(this.getDistance() > nPoiItem.getDistance())
        {
            return 1;
        }

        return 0;
    }
}
