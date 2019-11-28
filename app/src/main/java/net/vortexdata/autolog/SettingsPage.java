package net.vortexdata.autolog;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import net.vortexdata.autolog.adapter.SliderAdapter;
import net.vortexdata.autolog.configs.Cfg;

import java.time.Year;

public class SettingsPage extends Fragment {

    TextView settingsHeader;
    TextView back;
    TextView copyright;
    TextView vtext;
    Switch rgb;
    Switch rgb2;
    Switch autoConn;
    Switch lockInput;
    TextView version;
    CardView hr1;
    CardView hr2;
    public ConstraintLayout bg;

    private int clicked = 0;

    private OnFragmentInteractionListener mListener;

    public SettingsPage() {

    }


    public static SettingsPage newInstance(String param1, String param2) {
        SettingsPage fragment = new SettingsPage();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_page, container, false);

        settingsHeader = v.findViewById(R.id.settingsHeader);
        back = v.findViewById(R.id.back);
        copyright = v.findViewById(R.id.copyright);
        rgb = v.findViewById(R.id.rgbMode);
        rgb2 = v.findViewById(R.id.rgbMode2);
        version = v.findViewById(R.id.version);
        vtext = v.findViewById(R.id.vtext);
        autoConn = v.findViewById(R.id.connectToWifi);
        hr1 = v.findViewById(R.id.hr1);
        hr2 = v.findViewById(R.id.hr2);
        bg = v.findViewById(R.id.bg_settings);
        lockInput = v.findViewById(R.id.lockInput);


        ConstraintLayout[] backgrounds = {bg, SliderAdapter.mainPage.bg, SliderAdapter.news.bg};

        if(Cfg.fancyBackground) {
            BasicMethods.setFancyBackground(bg, getContext());
            rgb.setChecked(true);
            rgb2.setVisibility(View.VISIBLE);
        }

        lockInput.setChecked(Cfg.lockCredentials);
        autoConn.setChecked(Cfg.autoConnect);

        version.setText(Cfg.version);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            copyright.setText(Year.now().equals(Year.parse("2019")) ? "Copyright Vortexdata.NET © 2019" : "Copyright Vortexdata.NET © 2019  - " + Year.now());
        } else {
            copyright.setText("Copyright Vortexdata.NET © 2019 - 2020");
        }

        back.setOnClickListener(view -> home.main.vp.setCurrentItem(1, true));

        if (Cfg.fancyBackground) {
            BasicMethods.setFancyBackground(bg, getContext());
            rgb.setChecked(true);
        }

        rgb2.setChecked(Cfg.fancyBGinQConn);

        rgb.setOnClickListener(view -> {
            if (rgb.isChecked()) {
                BasicMethods.setFancyBackgrounds(backgrounds, getContext());
                rgb2.setVisibility(View.VISIBLE);
                rgb2.setChecked(true); // set as default
            }

            if (!rgb.isChecked()) {
                BasicMethods.removeFancyBackgrounds(backgrounds, getContext());
                rgb2.setVisibility(View.GONE);
                Cfg.fancyBGinQConn = false;
            }

            Cfg.fancyBackground = rgb.isChecked();
            BasicMethods.saveApkData(getContext());
        });

        rgb2.setOnClickListener(view -> {
            Cfg.fancyBGinQConn = rgb2.isChecked();
            BasicMethods.saveApkData(getContext());
        });

        autoConn.setOnClickListener(view -> {
            Cfg.autoConnect = autoConn.isChecked();
            BasicMethods.saveApkData(getContext());
        });

        lockInput.setOnClickListener(v1 -> {
            Cfg.lockCredentials = lockInput.isChecked();
            BasicMethods.updateInput();
            BasicMethods.saveApkData(getContext());
        });

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
