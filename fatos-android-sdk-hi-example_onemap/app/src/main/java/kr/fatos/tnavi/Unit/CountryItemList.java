package kr.fatos.tnavi.Unit;

import android.graphics.drawable.Drawable;

/**
 * countury list.
 *
 */
public class CountryItemList {
	private String name;
	private String code;
	private Drawable flag;

	public CountryItemList(String name, String code){
		this.name = name;
		this.code = code;
	}
	public CountryItemList(String name, String code, Drawable flag){
		this.name = name;
		this.code = code;
		this.flag = flag;
	}
	public String getName() {
		return name;
	}
	public Drawable getFlag() {
		return flag;
	}
	public String getCode() {
		return code;
	}
}
