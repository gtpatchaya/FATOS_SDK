package kr.fatos.tnavi.Unit;

/**
 * 환경 설정 type과 데이터 설정
 *
 */
public class settingItemDetailList {
	public static final int SETTING_TYPE_TEXT = 0;
	public static final int SETTING_TYPE_IMG = 1;
	public static final int SETTING_TYPE_ROUTELINE = 2;
	public static final int SETTING_TYPE_NEXTPAGE = 3;

	public int m_nType;
	public boolean m_bEnable;
	public String m_strSettingName;
	public String m_strSettingDataName;
}
