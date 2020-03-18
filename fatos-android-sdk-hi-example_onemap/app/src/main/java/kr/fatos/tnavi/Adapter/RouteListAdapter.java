package kr.fatos.tnavi.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import biz.fatossdk.newanavi.ANaviApplication;
import kr.fatos.tnavi.Lib.GUtilLib;
import kr.fatos.tnavi.R;
import kr.fatos.tnavi.Unit.RouteCardData;
import kr.fatos.tnavi.Unit.RouteType;

public class RouteListAdapter extends RecyclerView.Adapter<RouteListAdapter.RouteViewHolder> {

    private ArrayList<RouteCardData> dataSet;

    private int selected = 0;

    private Drawable defaultIcon;
    private Drawable chargeIconNormal;
    private Drawable chargeIconSelected;
    private int defaultCardColor;
    private int defaultTextColor;
    private int selectedTextColor;
    private int simul_or_go;
    private OnItemClicked onClick;
    public Context mContext;
    private Drawable btn_go_selected_background;
    private ANaviApplication m_gApp;


    //make interface like this
    public interface OnItemClicked {
        void onRouteListItemClick(int position);
        void onRouteListItemStartBtnClick(int position, boolean simul_or_go, int nRouteType);
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public LinearLayout layoutCharge;
        public TextView textCharge;
        public TextView textDistance;
        public TextView textTime;
        public TextView textSimul;
        public TextView textGo;

        public LinearLayout linearLayout_DistCharge, cardView_background;
        public RelativeLayout btn_start;
        public RelativeLayout btn_simul;
        public ImageView btn_anim;

        public LinearLayout ll_item, linearLayout_Kind;
        public TextView textView_Kind;
        public ImageView imageCharge, imageView_Simul;
        public RelativeLayout relativeLayout_DetailRoute;

        public RouteViewHolder(View itemView) {
            super(itemView);

            linearLayout_DistCharge = itemView.findViewById(R.id.linearLayout_DistCharge);
            cardView = itemView.findViewById(R.id.cardview);
            layoutCharge = itemView.findViewById(R.id.layoutCharge);
            textCharge = itemView.findViewById(R.id.textCharge);
            textDistance = itemView.findViewById(R.id.textDistance);
            textTime = itemView.findViewById(R.id.textTime);
            btn_start = itemView.findViewById(R.id.ll_go);
            btn_simul = itemView.findViewById(R.id.ll_simul);
            btn_anim = itemView.findViewById(R.id.btn_anim);
            textSimul = itemView.findViewById(R.id.summary_txt_simul);
            textGo = itemView.findViewById(R.id.summary_txt_go);

            ll_item = itemView.findViewById(R.id.ll_item);
            linearLayout_Kind = itemView.findViewById(R.id.linearLayout_Kind);
            textView_Kind = itemView.findViewById(R.id.textView_Kind);
            imageCharge = itemView.findViewById(R.id.imageCharge);
            relativeLayout_DetailRoute = itemView.findViewById(R.id.relativeLayout_DetailRoute);
            imageView_Simul = itemView.findViewById(R.id.imageView_Simul);
        }
    }

    public RouteListAdapter(ArrayList<RouteCardData> data) {
        this.dataSet = data;

    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_routepath, parent, false);

        mContext = parent.getContext();
        m_gApp = (ANaviApplication) mContext.getApplicationContext();

        view.getLayoutParams().width = parent.getWidth();
        defaultIcon = view.getResources().getDrawable(R.drawable.s_info_icon_sel);
        chargeIconNormal = view.getResources().getDrawable(R.drawable.s_info_icon_charge_nor);
        chargeIconSelected = view.getResources().getDrawable(R.drawable.s_info_icon_charge_sel);
        defaultCardColor = view.getResources().getColor(R.color.white);
        btn_go_selected_background = view.getResources().getDrawable(R.drawable.btn_go_ani_s);
        selectedTextColor = view.getResources().getColor(R.color.white);
        defaultTextColor = view.getResources().getColor(R.color.black);

        return (new RouteViewHolder(view));
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        LinearLayout layoutCharge = holder.layoutCharge;
        LinearLayout linearLayout_DistCharge = holder.linearLayout_DistCharge;
        TextView textCharge = holder.textCharge;
        TextView textDistance = holder.textDistance;
        TextView textTime = holder.textTime;
        TextView textGo = holder.textGo;
        TextView textSimul = holder.textSimul;

        RelativeLayout btn_start = holder.btn_start;
        final RelativeLayout btn_simul = holder.btn_simul;

        final ImageView iv = holder.btn_anim;

        LinearLayout ll_item = holder.ll_item;
        LinearLayout linearLayout_Kind = holder.linearLayout_Kind;
        TextView textView_Kind = holder.textView_Kind;
        ImageView imageCharge = holder.imageCharge;
        RelativeLayout relativeLayout_DetailRoute = holder.relativeLayout_DetailRoute;
        ImageView imageView_Simul = holder.imageView_Simul;

        RouteCardData routeCardData = dataSet.get(position);

        ll_item.setBackgroundResource(R.color.cardview_onemap_background);

        linearLayout_Kind.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams textTimelayoutParams = (LinearLayout.LayoutParams)textTime.getLayoutParams();
        textTimelayoutParams.gravity = Gravity.CENTER_VERTICAL;
        textTimelayoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, mContext.getResources().getDisplayMetrics());
        textTime.setLayoutParams(textTimelayoutParams);
        textTime.setTextColor(ContextCompat.getColor(mContext, R.color.cardview_onemap_textcolor));

        LinearLayout.LayoutParams textView_KindlayoutParams = (LinearLayout.LayoutParams)textView_Kind.getLayoutParams();
        textView_KindlayoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, mContext.getResources().getDisplayMetrics());
        textView_Kind.setLayoutParams(textView_KindlayoutParams);

        LinearLayout.LayoutParams linearLayout_DistChargelayoutParams = (LinearLayout.LayoutParams)linearLayout_DistCharge.getLayoutParams();
        linearLayout_DistChargelayoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, mContext.getResources().getDisplayMetrics());
        linearLayout_DistCharge.setLayoutParams(linearLayout_DistChargelayoutParams);

        textDistance.setPadding(0, 0, 0, 0);
        LinearLayout.LayoutParams textDistancelayoutParams = (LinearLayout.LayoutParams)textDistance.getLayoutParams();
        textDistancelayoutParams.gravity = Gravity.CENTER_VERTICAL;
        textDistance.setLayoutParams(textDistancelayoutParams);
        textDistance.setTextColor(ContextCompat.getColor(mContext, R.color.cardview_onemap_textcolor2));

        LinearLayout.LayoutParams imageChargelayoutParams = (LinearLayout.LayoutParams)imageCharge.getLayoutParams();
        imageChargelayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, mContext.getResources().getDisplayMetrics());
        imageCharge.setLayoutParams(imageChargelayoutParams);

        textCharge.setPadding(0, 0, 0, 0);
        LinearLayout.LayoutParams textChargeLayoutParams = (LinearLayout.LayoutParams)textCharge.getLayoutParams();
        textChargeLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        textCharge.setLayoutParams(textChargeLayoutParams);
        textCharge.setTextColor(ContextCompat.getColor(mContext, R.color.cardview_onemap_textcolor2));

        relativeLayout_DetailRoute.setVisibility(View.VISIBLE);

        imageView_Simul.setImageResource(R.drawable.selector_simul_summary);
        textSimul.setVisibility(View.GONE);
        RelativeLayout.LayoutParams imageView_simulLayoutParams = (RelativeLayout.LayoutParams)imageView_Simul.getLayoutParams();
        imageView_simulLayoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mContext.getResources().getDisplayMetrics());
        imageView_simulLayoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        imageView_Simul.setLayoutParams(imageView_simulLayoutParams);

        iv.setBackgroundResource(0);
        iv.setImageResource(R.drawable.selector_go_summary);
        textGo.setVisibility(View.GONE);
        RelativeLayout.LayoutParams ivLayoutParams = (RelativeLayout.LayoutParams)iv.getLayoutParams();
        ivLayoutParams.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, mContext.getResources().getDisplayMetrics());
        ivLayoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        iv.setLayoutParams(ivLayoutParams);

        imageCharge.setImageResource(R.drawable.s_info_icon_charge_sel_one);

        switch(routeCardData.getnType())
        {
            case RouteType.ROUTE_RECOMM :
                textView_Kind.setText(R.string.onemap_route_type1);
                break;

            case RouteType.ROUTE_GENERAL :
                textView_Kind.setText(R.string.onemap_route_type2);
                break;

            case RouteType.ROUTE_SHORT :
                textView_Kind.setText(R.string.onemap_route_type3);
                break;

            case RouteType.ROUTE_EXP:
                textView_Kind.setText(R.string.onemap_route_type4);
                break;

            case RouteType.ROUTE_FREE :
                textView_Kind.setText(R.string.onemap_route_type5);
                break;
        }

        relativeLayout_DetailRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("openRouteDetail");
                mContext.sendBroadcast(intent);
            }
        });

        if (routeCardData.getStrTypeName().isEmpty()) {
            cardView.setVisibility(View.INVISIBLE);
            return;
        } else {
            cardView.setVisibility(View.VISIBLE);
        }

        if (routeCardData.isCharge()) {
            int nFee = routeCardData.nFee;
            DecimalFormat format = new DecimalFormat("###,###");
            String strFee = format.format(nFee);

            textCharge.setText(strFee);
            layoutCharge.setVisibility(View.VISIBLE);
        } else {
            layoutCharge.setVisibility(View.INVISIBLE);
        }

        ANaviApplication m_gApp = (ANaviApplication) mContext.getApplicationContext();

        if (m_gApp.getAppSettingInfo().m_nDistUnit == 0) { //0 : km , 1: mile
            textDistance.setText(GUtilLib.getInstance(mContext).updateTotalRemainDist(routeCardData.getnLength()));
        } else {
            textDistance.setText(GUtilLib.getInstance(mContext).updateTotalRemainDistForMile(routeCardData.getnLength()));
        }

        //fatostest - 남은시간 일자 제거
        String strTime = GUtilLib.getInstance(mContext).updateTotalRemainTimeWithoutDay(routeCardData.getnTime(), false, m_gApp.getFatosLocale());

        //fatostest - 남은시간 일자 포함
//        String strTime = GUtilLib.getInstance(mContext).updateTotalRemainTime(routeCardData.getnTime(), false, m_gApp.getFatosLocale());

        textTime.setText(strTime);

        final int clickPosition = position;
        final int nRouteType = routeCardData.nType;

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onRouteListItemClick(clickPosition);
            }
        });

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onRouteListItemStartBtnClick(clickPosition, true, nRouteType);
                iv.setBackground(btn_go_selected_background);
            }
        });

        btn_simul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onRouteListItemStartBtnClick(clickPosition, false, nRouteType);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    public void setSelected(int selected) {
        this.selected = selected;
        notifyDataSetChanged();
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }
}
