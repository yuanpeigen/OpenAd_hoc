package com.nexfi.yuanpeigen.openadhoc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataOutputStream;

public class MainActivity extends AppCompatActivity {
    private boolean isRoot;
    private Handler handler;
    private Button button, button_in;
    private String release;
    private String model;
    private String sdk;
    private String[] commands = new String[]{"ifconfig wlan0 down", "ifconfig wlan0 up", "iw dev wlan0 set type ibss", "iw dev wlan0 ibss join imesh 2437", "ifconfig wlan0 up", "iw dev wlan0 info", "ifconfig wlan0 inet 192.168.5.138 netmask 255.255.255.0", "ifconfig wlan0 up", "mount -o rw,remount /", "mkdir -p /var/run/", "mount -o rw,remount /dev/block/platform/msm_sdcc.1/by-name/system /system", "chmod 777 /system/bin/batmand", "/system/bin/batmand -a 192.168.5.138/32 wlan0"};
    private String[] commandsHMTWO = new String[]{
            "su",
            "rmmod /system/lib/modules/wlan.ko",
            "insmod /system/lib/modules/wlan.ko",
            "ifconfig wlan0 down",
            "ifconfig wlan0 up",
            "iw dev wlan0 set type ibss",
            "iw dev wlan0 ibss join imesh 2437 NOHT fixed-freq 00:11:22:33:44:55",
            "ifconfig wlan0 inet 192.168.5.139 netmask 255.255.255.0",
            "ifconfig wlan0 up",
            "mount -o rw,remount /",
            "mkdir -p /data/run/",
            "mount -o rw,remount /dev/block/bootdevice/by-name/system /system",
            "chmod 777 /system/bin/batmand",
            "/system/bin/batmand -a 192.168.5.139/32 wlan0"};

    private String[] commandsNexus5x = new String[]{"ifconfig wlan0 down",
            "ifconfig wlan0 up",
            "iw dev wlan0 set power_save off",
            "iw dev wlan0 set type ibss",
            "iw dev wlan0 ibss join imesh 2437 HT20 fixed-freq 00:11:22:33:44:55",
            "ifconfig wlan0 inet 192.168.5.138 netmask 255.255.255.0",
            "ifconfig wlan0 up", "mount -o rw,remount /",
            "mkdir -p /data/run/",
            "mount -o rw,remount /dev/block/platform/soc.0/f9824900.sdhci/by-name/system /system",
            "chmod 777 /system/bin/batmand",
            "/system/bin/batmand -a 192.168.5.138/32 wlan0"
    };

    private String[] commandsXMFOUR = new String[]{"su", "rmmod /system/lib/modules/wlan.ko",
            "insmod /system/lib/modules/wlan.ko",
            "ifconfig wlan0 down", "ifconfig wlan0 up",
            "iw dev wlan0 set type ibss",
            "iw dev wlan0 ibss join imesh 2437 HT20 fixed-freq 00:11:22:33:44:55",
            "ifconfig wlan0 inet 192.168.5.140 netmask 255.255.255.0",
            "ifconfig wlan0 up",
            "mount -o rw,remount /",
            "mkdir -p /data/run/", "mount -o rw,remount /dev/block/bootdevice/by-name/system /system",
            "chmod 777 /system/bin/batmand",
            "/system/bin/batmand -a 192.168.5.140/32 wlan0"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        button_in = (Button) findViewById(R.id.button_in);
        button_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewActivity.class));
                finish();
            }
        });
        sdk = android.os.Build.VERSION.SDK;
        model = android.os.Build.MODEL;
        release = android.os.Build.VERSION.RELEASE;
        button.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          if (model.equals("Nexus 5X")) {
                                              openAdhoc(model, commandsNexus5x);
                                          } else if (model.equals("HM 2LTE-CMCC")) {
                                              openAdhoc(model, commandsHMTWO);
                                          } else if (model.equals("MI 4LTE")) {
                                              openAdhoc(model, commandsXMFOUR);
                                          } else {
                                              Toast.makeText(MainActivity.this, "无法识别该机型", Toast.LENGTH_SHORT).show();
                                              printLog();
                                          }
                                      }
                                  }
        );
    }

    private void openAdhoc(String model, String[] command) {
        printLog();
        if (model.equals("Nexus 5X")) {
            Log.e("IP", "192.168.5.138");
        } else if (model.equals("HM 2LTE-CMCC")) {
            Log.e("IP", "192.168.5.139");
        } else if (model.equals("MI 4LTE")) {
            Log.e("IP", "192.168.5.140");
        }
        Toast.makeText(MainActivity.this, model, Toast.LENGTH_SHORT).show();
        isRoot = upgradeRootPermission(getPackageCodePath());
        Log.e("Root", isRoot + "");
        ShellUtils.CommandResult result = ShellUtils.execCommand(command, isRoot, true);
        String errorMsg = result.errorMsg;
        String successMsg = result.successMsg;
        int resultNum = result.result;
        Log.e("errorMsg", errorMsg);
        Log.e("successMsg", successMsg);
        Log.e("resultNum", resultNum + "");
    }

    private void printLog() {
        Log.e("SDK", sdk);
        Log.e("model", model);
        Log.e("release", release);
    }

    /**
     * 应用程序运行命令获取 Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
            }
        }
        return true;
    }

    private void exCommand(String model, final String[] excommands) {
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < excommands.length; i++) {
                    ShellUtils.CommandResult result = ShellUtils.execCommand(excommands[i], true);
                    String errorMsg = result.errorMsg;
                    String successMsg = result.successMsg;
                    int resultNum = result.result;
                    Log.e("errorMsg", errorMsg);
                    Log.e("successMsg", successMsg);
                    Log.e("resultNum", resultNum + "");
                }
            }
        }, 500);

    }

}
