package kr.fatos.tnavi.tnavifragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import kr.fatos.tnavi.R;

public class ProgressFragment extends DialogFragment {

    public static ProgressFragment newInstance() {
        ProgressFragment fragment = new ProgressFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        ProgressDialog dialog = new ProgressDialog(getActivity(), getTheme());
        dialog.setTitle(getString(R.string.app_name));
        dialog.setMessage(getString(R.string.fmp_txt_route_msg));
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }



}
