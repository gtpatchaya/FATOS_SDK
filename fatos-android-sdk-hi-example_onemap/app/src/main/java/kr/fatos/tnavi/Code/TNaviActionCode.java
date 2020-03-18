package kr.fatos.tnavi.Code;

public class TNaviActionCode {
    public static final String CERTIFICATION = "CERTIFICATION";
    public static final String READY_MAIN_MAP = "READY_MAIN_MAP";

    public static final String GO_MAIN_MAP = "GO_MAIN_MAP";
    public static final String GO_MAIN_MENU = "GO_MAIN_MENU";
    public static final String GO_MENU_SETTING = "GO_MENU_SETTING";
    public static final String GO_MENU_CATE = "GO_MENU_CATE";
    public static final String GO_FROM_ROUTEEDiT = "GO_FROM_ROUTEEDiT";

    public static final String RESULT_FINISH = "RESULT_FINISH";
    public static final String MAIN_MENU_RESULT_SIMUL_START = "MAIN_MENU_RESULT_SIMUL_START";

    public static final String MAIN_ROUTESUMMARY_BACK = "MAIN_ROUTESUMMARY_BACK";
    public static final String MAIN_STARTRGSERVICE= "MAIN_STARTRGSERVICE";

    public static final String EXTRA_AIRMODE_ON = "FATOS_AUTO_AIRMODE_ON";
    public static final String EXTRA_AIRMODE_OFF= "FATOS_AUTO_AIRMODE_OFF";

    //모드 정의
    public static final String APP_MODE_DEFAULT = "APP_MODE_DEFAULT"; //초기실행 모드
    public static final String APP_MODE_SEARCH_TO_ROUTE = "APP_MODE_SEARCH_TO_ROUTE"; //검색화면에서 요약화면으로
    public static final String APP_MODE_SHOW_MAP = "APP_MODE_SHOW_MAP"; //검색화면에서 지도보기시에는 롱클릭 막아야해서
    public static final String APP_MODE_SEARCH = "APP_MODE_SEARCH"; //검색창 뒤에도 맵이 떠있어서 onMapMove되는 경우가 있음
    public static final String APP_MODE_JUST_GOAL_SEARCH = "APP_MODE_JUST_GOAL_SEARCH"; // 목적지 탐색(재탐 X)
    public static final String APP_MODE = "APP_MODE"; // bundle로 앱 모드 값을 스트링으로 넘길때 사용하는 키값
    public static final String APP_MODE_SHOWING_SUMMARY = "APP_MODE_SHOWING_SUMMARY"; // 결과화면 보여주고 있을 때
    public static final String SEARCH_MODE = "SEARCH_MODE"; //검색 액티비티에 bundle 넘길 때, KEY 값
    public static final String SEARCH_MODE_WORD = "SEARCH_MODE_WORD"; //검색 액티비티에 검색 최종 단어를 bundle 넘길 때, KEY 값

    public static final String APP_MODE_ROUTE = "APP_MODE_ROUTE"; //주행 중
    public static final String APP_MODE_SIMULATE = "APP_MODE_SIMULATE"; //모의 주행 중

    public static final String ARRAYLIST_MAP_TO_SEARCH = "ARRAYLIST_MAP_TO_SEARCH";

    public static final String SEARCH_KIND = "SEARCH_KIND";

    //UI 모드 정의
    public final static int UI_DRIVE_MODE = 0;
    public final static int UI_SUMMARY_MODE = 1;
    public final static int UI_SIMUL_MODE = 2;
    public final static int UI_NORMAL_DRIVE_MODE = 3;
    public final static int UI_SHOW_MAP_MODE = 4;

    //GpsAppMode
    public final static int GPS_APP_MODE_DEFAULT = 0;
    public final static int GPS_APP_MODE_SHOW_MAP = 1;
    public final static int GPS_APP_MODE_LONG_TOUCH = 2;

    //넘기는 값 종류 정의
    public static final String POI_ITEM = "POI_ITEM";

    //Handler what 정의
    public static final int HANDLER_MAP_MOVE_DEFAULT = 0;
    public static final int HANDLER_MAP_MOVE_SHOW_MAP = 1;
    public static final int HANDLER_MAP_MOVE_SIMULATE = 2;
    public static final int HANDLER_MAP_MOVE_ROUTE = 3;

    public static final String LONGTOUCH_ROUTESUMMARY = "LONGTOUCH_ROUTESUMMARY";
    public static final String LONGTOUCH_GO_ROUTE = "LONGTOUCH_GO_ROUTE";

    //번들에 키값으로 사용, 밸류값은 하단 종류 정의 사용할 것
    public static final String ROUTE_VIA_OR_GOAL = "ROUTE_VIA_OR_GOAL";

    //출발지, 목적지 변경 종류 정의
    public static final String CHANGE_VIA_GO_ROUTE = "CHANGE_VIA_GO_ROUTE";
    public static final String CHANGE_GOAL_GO_ROUTE = "CHANGE_GOAL_GO_ROUTE";
    public static final String JUST_GOAL = "JUST_GOAL";
    public static final String CHANGE_VIA_GOAL_GO_ROUTE = "CHANGE_VIA_GOAL_GO_ROUTE";
    public static final String CHANGE_VIA_AND_GOAL = "CHANGE_VIA_AND_GOAL"; //출발지와 목적지가 서로 변경될 때(출발->목적, 목적->출발로)

    //intent request code
    public static final int LONGTOUCH_CLICK = 1200;

    //sharedpreferences code
    public static final String RECENT_ROUTE_KEY = "RECENT_ROUTE_KEY";
    public static final String RECENT_ROUTE_SUB_KEY = "RECENT_ROUTE_SUB_KEY";

    //모의주행시 속도 코드
    public static final int SIMUL_SPEED_1 = 1;
    public static final int SIMUL_SPEED_2 = 2;
    public static final int SIMUL_SPEED_4 = 4;
    public static final int SIMUL_SPEED_8 = 8;
    public static int CUR_SIMUL_SPEED = SIMUL_SPEED_1;


    //주행, 모의주행 경로 중 선택한 인덱스
    public static final String SELECT_ROUTE_INDEX = "SELECT_ROUTE_INDEX";
}
