package kr.fatos.tnavi.Interface;

import android.os.Bundle;
import android.view.View;

import java.util.List;

import biz.fatossdk.navi.RoutePosition;

public interface FragmentCommunicator {

    public void fragmentContactActivity(View v);
    public void rerouteFromSummary(Bundle bundle);

}
