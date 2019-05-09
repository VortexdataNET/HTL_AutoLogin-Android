package net.vortexdata.autolog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.vortexdata.autolog.configs.Cfg;
import net.vortexdata.autolog.configs.Msg;
import net.vortexdata.autolog.updater.checkWeb;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements ResponseReceiver {

    private MainActivity main;

    private EditText inUsername;
    private EditText inPassword;
    private Button saveButton;
    private FloatingActionButton connectButton;
    private Button connectWifi;
    private ImageView img;

    private Thread quickconnThread;

    private ConstraintLayout background;

    private boolean firstStart = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inUsername = (EditText) findViewById(R.id.inUsername);
        inPassword = findViewById(R.id.inPassword);
        saveButton = findViewById(R.id.savebutton);
        background = findViewById(R.id.background);
        img = findViewById(R.id.config);


        Thread t = new Thread(() -> {
            new checkWeb(getApplicationContext());
            if (!Cfg.sentUsage) {
                checkWeb.sendUsage();
            }
        });
        t.start();


        main = this;
        loadData();
        loadApkData();
        saveButton(saveButton);

        if (Cfg.fancyBackground) Settings.setFancyBackground(background, this);

        img.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
        });

/*        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, Cfg.expireMonth);
        c.set(Calendar.DATE, Cfg.expireDay);
        c.set(Calendar.YEAR, Cfg.expireYear);

        Date date = new Date();
        Date lockDate = c.getTime();
*/
        //if(firstStart) showMessage("Attention", "This Beta will run out on " + Cfg.expireDay + "." + (Cfg.expireMonth + 1) + "." + Cfg.expireYear);

        //if(date.after(lockDate)) {
        //  Cfg.expired = true;
        //Intent i = new Intent(getApplicationContext(), TimeOut.class);
        //startActivity(i);
        //}

    }


    private void saveButton(Button b) {
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (inUsername.getText().toString().equals("VarChar42")) {
                    Snackbars.Snackbar(view, "VarChar42 treibt sein unwesen!!!!", "#eb3b5a");
                    return;
                }

                if (inUsername.getText().toString().equals("quit")) {
                    Snackbars.Snackbar(view, "Bye!", "#eb3b5a");
                    System.exit(0);
                    return;
                }

                if (inUsername.getText().toString().equals("notifyMe")) {
                    showMessage("Note", Msg.MobileData);
                }

                if (inUsername.getText().length() < 4 || inPassword.getText().length() < 4) {

                    Snackbars.Snackbar(view, Msg.noUsername, Msg.err_color);
                    return;
                }

                SharedPreferences.Editor editor = getSharedPreferences("userData", MODE_PRIVATE).edit();
                editor.putString("user", inUsername.getText().toString());
                editor.putString("pw", inPassword.getText().toString());
                editor.apply();
                //Toast.makeText(getBaseContext(), "Data saved!!", Toast.LENGTH_LONG).show();
                Snackbars.Snackbar(view, Msg.loginData, "#00d873");

                if (connecting) {
                    //toast = Toast.makeText(main, "Be patient!", Toast.LENGTH_LONG);
                    //toast.show();
                    Snackbars.Snackbar(view, Msg.processing, "#fc5c65");
                    return;
                }
                //toast = Toast.makeText(main, "Sending request!", Toast.LENGTH_LONG);
                //toast.show();
                Snackbars.Snackbar(view, Msg.processing, Msg.GreyColor);
                connecting = true;
                //LoginPost.send(inUsername.getText().toString(), inPassword.getText().toString(), main);

                String networkSSID = "HTBLA";
                String networkPass = "htlgrieskirchen";

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + networkSSID + "\"";
                conf.preSharedKey = "\"" + networkPass + "\"";
                WifiManager wifiManager = (WifiManager) main.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.addNetwork(conf);


                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                for (WifiConfiguration i : list) {
                    if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(i.networkId, true);

                        wifiManager.reconnect();
                        if (toast != null) toast.cancel();
                        //toast = Toast.makeText(main, "Connecting to "+i.SSID+"...", Toast.LENGTH_LONG);
                        //toast.show();
             //           Snackbars.Snackbar(view, "Connecting to " + i.SSID + "...", Msg.GreyColor);


                        break;
                    }
                }

                quickconnThread = new Thread(() -> {
                    try {
                        quickconnThread.sleep(6000);
                        Snackbars.Snackbar(view, Msg.MainProcessing, Msg.GreyColor);
                        LoginPost.send(inUsername.getText().toString(), inPassword.getText().toString(), main);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                quickconnThread.start();
            }

        });
    }

    private boolean connecting = false;
    private Toast toast;

    private void connectButton(FloatingActionButton b) {
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toast != null) toast.cancel();
                if (connecting) {
                    //toast = Toast.makeText(main, "Be patient!", Toast.LENGTH_LONG);
                    //toast.show();
                    Snackbars.Snackbar(view, "Be patient!", "#fc5c65");
                    return;
                }
                //toast = Toast.makeText(main, "Sending request!", Toast.LENGTH_LONG);
                //toast.show();
                Snackbars.Snackbar(view, Msg.processing, Msg.GreyColor);
                connecting = true;
                LoginPost.send(inUsername.getText().toString(), inPassword.getText().toString(), main);

            }
        });
    }

    private void connectWifi(ImageView b) {

        if (firstStart) showMessage("Notify", Msg.MobileData);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inUsername.getText().length() < 4 || inPassword.getText().length() < 4) {
                    Snackbars.Snackbar(view, Msg.noUsername, Msg.err_color);
                    return;
                }

                String networkSSID = "HTBLA";
                String networkPass = "htlgrieskirchen";

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = "\"" + networkSSID + "\"";
                conf.preSharedKey = "\"" + networkPass + "\"";
                WifiManager wifiManager = (WifiManager) main.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.addNetwork(conf);


                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
                for (WifiConfiguration i : list) {
                    if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(i.networkId, true);

                        wifiManager.reconnect();
                        if (toast != null) toast.cancel();
                        //toast = Toast.makeText(main, "Connecting to "+i.SSID+"...", Toast.LENGTH_LONG);
                        //toast.show();
                        Snackbars.Snackbar(view, "Connecting to " + i.SSID + "...", Msg.GreyColor);


                        break;
                    }
                }

                /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                    for (Network net : connectivityManager.getAllNetworks()) {

                        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(net);

                        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            connectivityManager.bindProcessToNetwork(net);
                            break;
                        }
                    }
                }*/


                quickconnThread = new Thread(() -> {
                    try {
                        quickconnThread.sleep(6000);
                        Snackbars.Snackbar(view, Msg.MainProcessing, Msg.GreyColor);
                        LoginPost.send(inUsername.getText().toString(), inPassword.getText().toString(), main);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
                quickconnThread.start();
            }
        });
    }

    private void loadData() {
        SharedPreferences prefs = getSharedPreferences("userData", MODE_PRIVATE);
        inUsername.setText(prefs.getString("user", ""));
        inPassword.setText(prefs.getString("pw", ""));

    }

    private void saveApkData() {
        SharedPreferences.Editor editor = getSharedPreferences("apkData", 0).edit();
        editor.putBoolean("firstStart", firstStart);
        editor.putBoolean("easteregg", Cfg.easteregg);
        editor.putBoolean("fancyBackground", Cfg.fancyBackground);
        editor.putBoolean("Expired", Cfg.expired);
        editor.putBoolean("sentUsage", Cfg.sentUsage);
        editor.putBoolean("QConnBg", Cfg.fancyBGinQConn);
        editor.apply();
    }

    private void loadApkData() {
        SharedPreferences prefs = getSharedPreferences("apkData", 0);
        firstStart = prefs.getBoolean("firstStart", true);
        Cfg.easteregg = prefs.getBoolean("easteregg", false);
        Cfg.fancyBackground = prefs.getBoolean("fancyBackground", false);
        Cfg.expired = prefs.getBoolean("Expired", false);
        Cfg.sentUsage = prefs.getBoolean("sentUsage", false);
        Cfg.fancyBGinQConn = prefs.getBoolean("QConnBg", false);
    }

    public void showMessage(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        View view = getLayoutInflater().inflate(R.layout.errormsg, null);

        builder.setView(view);

        TextView name = view.findViewById(R.id.name);
        Button btn = view.findViewById(R.id.accept);
        TextView t = view.findViewById(R.id.error);

        name.setText(title);
        t.setText(msg);

        AlertDialog dialog = builder.create();
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow().setDimAmount((float) 0.9);

        btn.setOnClickListener(v -> {
            dialog.dismiss();
            firstStart = false;
            saveApkData();
        });


        dialog.show();
    }

    @Override
    public void error(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connecting = false;

                Snackbars.Snackbar(getWindow().getDecorView().getRootView(), Msg.err, Msg.err_color);
            }
        });
    }

    @Override
    public void ok(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                connecting = false;

                //Toast.makeText(main, message, Toast.LENGTH_LONG).show();
                if (message.equals(Msg.loginSuccess)) {
                    Snackbars.Snackbar(getWindow().getDecorView().getRootView(), message, Msg.successColor);
                } else {
                    Snackbars.Snackbar(getWindow().getDecorView().getRootView(), message, Msg.err_color);
                }

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        saveApkData();

        Thread t = new Thread(() -> {
            new checkWeb(getApplicationContext());
        });
        t.start();

        if (Cfg.fancyBackground) {
            Settings.setFancyBackground(background, this);
        } else {
            Settings.removeFancyBackground(background, this);
        }
    }
}
