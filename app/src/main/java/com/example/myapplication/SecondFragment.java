package com.example.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.FT_Device;
import com.ftdi.j2xx.D2xxManager.D2xxException;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Objects;

public class SecondFragment extends Fragment
{
    public static boolean scannow = false;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        view.findViewById(R.id.button_Multiscan).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Toast toast = Toast.makeText(getContext(), "Hello toast!", Toast.LENGTH_LONG);
                //toast.show();
                TextView txt = (TextView) getView().findViewById(R.id.editTextTextMultiLineResolve);

                D2xxManager d2xxManager;
                FT_Device ftDevice;
                int deviceCount;
                Handler h = new Handler();
// Get SP Version(f ファミリ)コマンド
//                byte[] sendCmd = {(byte) 0xBB, (byte) 0x80, 0x54, 0x00, 0x02, 0x00, (byte) 0xD6, 0x7E};
                byte[] sendCmd = {(byte) 0xBB, (byte) 0x80, 0x27, 0x00, 0x04, 0x22, (byte) 0x00, 0x08, (byte) 0xE5, 0x7E};
                String hexStr = "SendCommand:/BB";
                byte buf = 0;
                for(int i=1;i<sendCmd.length-2;i++)
                {
                    buf = (byte)(buf ^ sendCmd[i]);
                    hexStr += "/"+String.format("%02X",sendCmd[i]);
                }
                hexStr += "/++/"+String.format("%02X",sendCmd[sendCmd.length-2]);
                sendCmd[sendCmd.length-2]=(byte)buf;
                hexStr += "->"+String.format("%02X",buf);

                try
                {
                    d2xxManager = D2xxManager.getInstance(getContext());
                    deviceCount = d2xxManager.createDeviceInfoList(getContext());
                    if (deviceCount > 0)
                    {
                        ftDevice = d2xxManager.openByIndex(getContext(), 0);
                        if (ftDevice.isOpen()) {
                            //ftDevice.setBaudRate(115200); // 回線速度はリーダ・ライタの設定に合わせてください。
                            ftDevice.setBaudRate(19200); // 回線速度はリーダ・ライタの設定に合わせてください。
                            ftDevice.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, D2xxManager.FT_STOP_BITS_1,
                                    D2xxManager.FT_PARITY_NONE);
// コマンド送信
                            ftDevice.write(sendCmd, sendCmd.length, true);
                            ReadThread t = new ReadThread(ftDevice);
                            t.start();
                            txt.setText("Captuer Now"+hexStr);
                        }
                        else
                        {
                            txt.setText("CannotOpen!"+hexStr);
                        }
                    }
                    else
                    {
// 接続デバイスなし
                        txt.setText("NoDvice!"+hexStr);
                    }
                }
                catch (D2xxException e)
                {
                    txt.setText("Error!");
                }
            }
        });

        view.findViewById(R.id.button_light).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Toast toast = Toast.makeText(getContext(), "Hello toast!", Toast.LENGTH_LONG);
                //toast.show();
                TextView txt = (TextView) getView().findViewById(R.id.editTextTextMultiLineResolve);
                txt.setText("Start:");
                D2xxManager d2xxManager;
                FT_Device ftDevice;
                int deviceCount;
                Handler h = new Handler();
// Get SP Version(f ファミリ)コマンド
//                byte[] sendCmd = {(byte) 0xBB, (byte) 0x80, 0x54, 0x00, 0x02, 0x00, (byte) 0xD6, 0x7E};
                byte[] sendCmd = {(byte) 0xBB, (byte) 0x80, 0x39, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, (byte) 0xA1, 0x7E};
                //チェックサム計算
                byte buf = 0;
                for(int i=1;i<sendCmd.length-2;i++)
                {
                    buf = (byte)(buf ^ sendCmd[i]);
                }
                sendCmd[sendCmd.length-2]=(byte)buf;

                try
                {
                    d2xxManager = D2xxManager.getInstance(getContext());
                    deviceCount = d2xxManager.createDeviceInfoList(getContext());
                    if (deviceCount > 0)
                    {
                        scannow = false;
/*
                        for(int i=0;i<5;i++) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
*/
                        ftDevice = d2xxManager.openByIndex(getContext(), 0);
                        if (ftDevice.isOpen() && scannow==false) {
                            //ftDevice.setBaudRate(115200); // 回線速度はリーダ・ライタの設定に合わせてください。
                            ftDevice.setBaudRate(19200); // 回線速度はリーダ・ライタの設定に合わせてください。
                            ftDevice.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, D2xxManager.FT_STOP_BITS_1,
                                    D2xxManager.FT_PARITY_NONE);
                            // コマンド送信
                            ftDevice.write(sendCmd, sendCmd.length, true);
                            ReadThread t = new ReadThread(ftDevice);
                            t.start();
                            scannow = true;
                            txt.setText(txt.getText() + "\n" + "Captuer Now");
                        } else {
                            txt.setText(txt.getText() + "\n" + "CannotOpen! " + scannow);
                        }
//                        }
                    }
                    else
                    {
// 接続デバイスなし
                        txt.setText(txt.getText() + "\n" +"NoDvice!");
                    }
                }
                catch (D2xxException e)
                {
                    txt.setText(txt.getText() + "\n" +"Error!");
                }

            }
        });
    }

        view.findViewById(R.id.button_scan).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Toast toast = Toast.makeText(getContext(), "Hello toast!", Toast.LENGTH_LONG);
                //toast.show();
                TextView txt = (TextView) getView().findViewById(R.id.editTextTextMultiLineResolve);
                txt.setText("Start:");
                D2xxManager d2xxManager;
                FT_Device ftDevice;
                int deviceCount;
                Handler h = new Handler();
// Get SP Version(f ファミリ)コマンド
//                byte[] sendCmd = {(byte) 0xBB, (byte) 0x80, 0x54, 0x00, 0x02, 0x00, (byte) 0xD6, 0x7E};
                byte[] sendCmd = {(byte) 0xBB, (byte) 0x80, 0x22, 0x00, 0x02, 0x01, (byte) 0xA1, 0x7E};
                //チェックサム計算
                byte buf = 0;
                for(int i=1;i<sendCmd.length-2;i++)
                {
                    buf = (byte)(buf ^ sendCmd[i]);
                }
                sendCmd[sendCmd.length-2]=(byte)buf;

                try
                {
                    d2xxManager = D2xxManager.getInstance(getContext());
                    deviceCount = d2xxManager.createDeviceInfoList(getContext());
                    if (deviceCount > 0)
                    {
                        scannow = false;
/*
                        for(int i=0;i<5;i++) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
*/
                            ftDevice = d2xxManager.openByIndex(getContext(), 0);
                            if (ftDevice.isOpen() && scannow==false) {
                                //ftDevice.setBaudRate(115200); // 回線速度はリーダ・ライタの設定に合わせてください。
                                ftDevice.setBaudRate(19200); // 回線速度はリーダ・ライタの設定に合わせてください。
                                ftDevice.setDataCharacteristics(D2xxManager.FT_DATA_BITS_8, D2xxManager.FT_STOP_BITS_1,
                                        D2xxManager.FT_PARITY_NONE);
                                // コマンド送信
                                ftDevice.write(sendCmd, sendCmd.length, true);
                                ReadThread t = new ReadThread(ftDevice);
                                t.start();
                                scannow = true;
                                txt.setText(txt.getText() + "\n" + "Captuer Now");
                            } else {
                                txt.setText(txt.getText() + "\n" + "CannotOpen! " + scannow);
                            }
//                        }
                    }
                    else
                    {
// 接続デバイスなし
                        txt.setText(txt.getText() + "\n" +"NoDvice!");
                    }
                }
                catch (D2xxException e)
                {
                    txt.setText(txt.getText() + "\n" +"Error!");
                }

            }
        });
    }

    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            TextView txt = (TextView) getView().findViewById(R.id.editTextTextMultiLineResolve);
            String bufstr = "";
            if (msg.what == 20)
            {
                bufstr = (String)msg.obj;
                txt.setText(txt.getText() + "\n" + bufstr);
 //               bufstr = Integer.toString(msg.arg1, 16);
 //               txt.setText(txt.getText() + "\n" + bufstr);
            }
            else
            {
                txt.setText("NNN");
            }
            scannow=false;
        }
    };

    private class ReadThread extends Thread
    {
        private FT_Device _ftDevice;

        ReadThread(FT_Device device)
        {
            this._ftDevice = device;
            this.setPriority(MIN_PRIORITY);
        }
        //@Override
        public void run()
        {
            byte[] recvCmd = new byte[1024];
            int length = 0;
            int dataLength = 0;
            String hexStr = "Header:";
            String EPC ="";

            try
            {
                if (this._ftDevice.isOpen() == false)
                {
                    return;
                }
                byte[] header = new byte[5];
// ヘッダ部受信
                length = this._ftDevice.read(header, 5, 3000);
                if (length != 5)
                {
// ヘッダ部受信失敗
                    return;
                }
                for (int i = 0; i < length; i++)
                {
                    recvCmd[i] = header[i];
                    hexStr += String.format("%02X",header[i]);
                }
                dataLength = ((recvCmd[3] << 8) | recvCmd[4]) + 1;
                hexStr += String.format(" /// %02X",dataLength);
                byte[] data = new byte[dataLength];
// データ部受信
                length = this._ftDevice.read(data, dataLength, 3000);
                if (length != dataLength)
                {
// データ部受信失敗
                    return;
                }
                hexStr += "  Data:";
                int j=0;
                for (int i = 0; i < length; i++)
                {
                    recvCmd[(5 + i)] = data[i];
                    hexStr += String.format("%02X",data[i]);
                    if(i>1){
                        j++;
                    }
                    if(j>0 && j<dataLength-5){
                        EPC += String.format("%02X",data[i]);
                    }
                }
            }
            catch (Exception e)
            {
            }
            finally
            {
                Message msg = mHandler.obtainMessage();
                msg.what = 20;
                msg.obj = hexStr+"///"+EPC;
//                msg.arg1 = Integer.parseInt("0x"+EPC);
                mHandler.sendMessage(msg);
                    this._ftDevice.close();
           }
        }
    }
}

