package com.szip.blewatch.base.Service;

import android.app.DownloadManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.szip.blewatch.base.Const.BroadcastConst;
import com.szip.blewatch.base.Const.SendFileConst;
import com.szip.blewatch.base.Notification.SmsService;
import com.szip.blewatch.base.R;
import com.szip.blewatch.base.Util.FileUtil;
import com.szip.blewatch.base.Util.LogUtil;
import com.szip.blewatch.base.Util.MathUtil;
import com.szip.blewatch.base.Util.ble.BluetoothUtilImpl;
import com.szip.blewatch.base.Util.ble.ClientManager;
import com.szip.blewatch.base.Util.ble.IBluetoothState;
import com.szip.blewatch.base.Util.ble.IBluetoothUtil;
import com.szip.blewatch.base.Broadcast.MyHandle;
import com.szip.blewatch.base.Broadcast.ToServiceBroadcast;
import com.szip.blewatch.base.View.ProgressHudModel;
import com.szip.blewatch.base.db.LoadDataUtil;
import com.szip.blewatch.base.db.dbModel.UserModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BleService extends Service implements MyHandle {

    // Global instance
    private static BleService mSevice = null;

    private int bluetoothState;

    private IBluetoothUtil iBluetoothUtil;

    private ToServiceBroadcast broadcast;

    private SmsService mSmsService = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mSevice = this;
        iBluetoothUtil = new BluetoothUtilImpl(getApplicationContext());
        registerService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcast!=null)
        broadcast.unregister(this);
        mSevice = null;
    }



    private void registerService() {
        // register battery low
        if (broadcast==null){
            broadcast = new ToServiceBroadcast();
            broadcast.registerReceive(this,this);
        }
        UserModel userModel = LoadDataUtil.newInstance().getUserInfo(MathUtil.newInstance().getUserId(this));
        if (userModel!=null)
            mac = userModel.deviceCode;
    }

    /**
     * Start SMS service to push new SMS.
     */
    public void startSmsService() {

        // Start SMS service
        if (mSmsService == null) {
            mSmsService = new SmsService();
        }
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSmsService, filter);
    }

    /**
     * Stop SMS service.
     */
    public void stopSmsService() {
        // Stop SMS service
        if (mSmsService != null) {
            unregisterReceiver(mSmsService);
            mSmsService = null;
        }
    }


    /**
     * 连接蓝牙
     * */
    private String mac;
    private synchronized void connect(){
        if (mac!=null&& BluetoothAdapter.getDefaultAdapter().isEnabled())
            iBluetoothUtil.connect(mac,iBluetoothState);
    }
    private void disConnect(){
        iBluetoothUtil.disconnect();
    }
    private final IBluetoothState iBluetoothState = new IBluetoothState() {
        @Override
        public void updateState(int state) {
            bluetoothState = state;
            Intent intent = new Intent(BroadcastConst.UPDATE_BLE_STATE);
            intent.putExtra("state",bluetoothState);
            sendBroadcast(intent);
            if(bluetoothState==5){
                connect();
            }
        }
    };


    /**
     * 扫描蓝牙
     * */
    private ArrayList<String> mDevices;
    private String deviceName;

    private void searchDevice(boolean search){
        if (search){
            final SearchRequest request = new SearchRequest.Builder()
                    .searchBluetoothLeDevice(9000, 1).build();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    ClientManager.getClient().search(request, mSearchResponse);
                }
            },1000);
        }else {
            ClientManager.getClient().stopSearch();
        }
    }

    private final SearchResponse mSearchResponse = new SearchResponse() {
        @Override
        public void onSearchStarted() {
            LogUtil.getInstance().logd("data******","正在搜索");
            mDevices = new ArrayList<>();
        }

        @Override
        public void onDeviceFounded(SearchResult device) {
            if (!mDevices.contains(device.getAddress())&&device.getName()!=null&&deviceName.equals(device.getName())){
                mDevices.add(device.getAddress());
            }
        }

        @Override
        public void onSearchStopped() {
            LogUtil.getInstance().logd("data******","搜索结束");
            Intent intent = new Intent(BroadcastConst.UPDATE_UI_VIEW);
            intent.putStringArrayListExtra("deviceList",mDevices);
            sendBroadcast(intent);
            mDevices = null;
        }

        @Override
        public void onSearchCanceled() {

        }
    };

    /**
     * 下载文件,通过DownloadManager，下载url上面的文件，把下载状态通过广播返回给前台
     * */
    private DownloadManager downloadManager;
    private long mTaskId;

    public void downloadFirmsoft(String dialUrl) {
        Log.i("data******","开始下载 dialUrl = "+dialUrl);

        String[] fileNames = dialUrl.split("/");
        String fileName = fileNames[fileNames.length-1];
        Log.i("data******","fileName = "+fileName);
        File file = new File(getExternalFilesDir(null).getPath()+ "/" + fileName);
        if (file.exists()){
            Intent intent  = new Intent(BroadcastConst.UPDATE_DOWNLOAD_STATE);
            intent.putExtra("state",true);
            sendBroadcast(intent);
            return;
        }
        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(dialUrl));
        request.setAllowedOverRoaming(true);//漫游网络是否可以下载

        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalFilesDir(this, "/",fileName);

        //将下载请求加入下载队列
        downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等
        mTaskId = downloadManager.enqueue(request);
    }

    //解析下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    //Log.d("DATA******",">>>下载暂停");
                case DownloadManager.STATUS_PENDING:
                    //Log.d("DATA******",">>>下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    //Log.d("DATA******",">>>正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:{
                    Log.d("data******",">>>下载完成");
                    Intent intent  = new Intent(BroadcastConst.UPDATE_DOWNLOAD_STATE);
                    intent.putExtra("state",true);
                    sendBroadcast(intent);
                }
                    break;
                case DownloadManager.STATUS_FAILED:{
                    Log.d("data******",">>>下载失败");
                    Intent intent  = new Intent(BroadcastConst.UPDATE_DOWNLOAD_STATE);
                    intent.putExtra("state",false);
                    sendBroadcast(intent);
                }
                    break;
            }
        }
    }

    /**
     * 发送背景，通过ble的方式分次向手表端发送背景图片，并把发送的进度以及状态通过广播的方式返回给前台
     * */
    private int i = 0;
    private byte datas[];

    private void sendBackgroundByte(){
        if (i>=datas.length)
            return;
        if (datas==null)
            return;
        byte[] newDatas;
        int len = (datas.length- i >128)?128:(datas.length- i);
        newDatas = new byte[len];
        System.arraycopy(datas, i,newDatas,0,len);
        iBluetoothUtil.writeForSendDialBackground(1,0,0, i/128,newDatas);
        i+=128;
        if (i>=datas.length){
            iBluetoothUtil.writeForSendDialBackground(2,0,0,0,new byte[0]);
        }
    }

    /**
     * 发送文件，通过ble的方式分次向手表端发送文件，并把发送的进度以及状态通过广播的方式返回给前台
     * 可以发送OTA升级包也可以发送表盘文件
     * */
    private Timer timer;
    private TimerTask timerTask;
    private int command = 4;
    private int index = 0;
    private byte fileDatas[];
    private int page;
    private int ackPakage = 0;
    private boolean isError = false;


    private void newTimerTask(long delay){
        isError = false;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isError)
                    sendByte();
            }
        };
        timer.schedule(timerTask,delay,20);
    }

    private synchronized void removeTimeTask(){
        timer.cancel();
        timer = null;
        ackPakage = 0;
    }

    private synchronized void sendByte(){
        byte[] newDatas;
        int len = (fileDatas.length-index- page >175)?175:(fileDatas.length-index- page);
        if (len<0)
            return;
        newDatas = new byte[len];
        System.arraycopy(fileDatas, page+index,newDatas,0,len);
        iBluetoothUtil.writeForSendDialFile(command,(byte) 0,index+page, page/175,newDatas);
        page+=175;
        if (page>=fileDatas.length-index){
            if(timer!=null){
                removeTimeTask();
            }
            iBluetoothUtil.writeForSendDialFile(command+1,(byte) 0,0,0,null);
            return;
        }
        ackPakage++;
        if (ackPakage==100&&timer!=null){
            removeTimeTask();
        }
    }

    @Override
    public void onReceive(Intent intent) {
        switch (intent.getAction()){
            case BroadcastConst.SEND_BLE_DATA:{
                if (bluetoothState != 3){
                    ProgressHudModel.newInstance().diss();
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BleService.this,getString(R.string.ble_error),Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                String command = intent.getStringExtra("command");
                switch (command){
                    case "setUnit":
                        iBluetoothUtil.writeForSetUnit();
                        break;
                    case "setWeather":
                        iBluetoothUtil.writeForSetWeather();
                        break;
                    case "findWatch":
                        iBluetoothUtil.writeForFindWatch(1);
                        break;
                    case "stopFindWatch":
                        iBluetoothUtil.writeForFindWatch(0);
                        break;
                    case "setInfo":
                        iBluetoothUtil.writeForUpdateUserInfo();
                        break;
                    case "sendNotify":{
                        String title = intent.getStringExtra("title");
                        String label = intent.getStringExtra("label");
                        int id = intent.getIntExtra("id",0);
                        Log.i("data******","title = "+title+" ;label = "+label+" ;id ="+id);
                        iBluetoothUtil.writeToSendNotify(title,label,id);
                    }
                        break;
                    case "setStep":{
                        iBluetoothUtil.writeForUpdateUserInfo();
                    }
                    break;
                    case "update_data":{
                        new Handler(getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(BleService.this,getString(R.string.updating),Toast.LENGTH_SHORT);
                            }
                        });
                        iBluetoothUtil.writeForUpdate();
                    }
                    break;
                }
            }
                break;
            case BroadcastConst.START_CONNECT_DEVICE:
                int state = intent.getIntExtra("isConnect",0);
                switch (state){
                    case 0:{
                        mac = null;
                        disConnect();
                    }
                        break;
                    case 1:{
                        if (bluetoothState==3||bluetoothState==2){//如果是已经连接的状态，直接把状态用广播丢出去
                            Intent stateIntent = new Intent(BroadcastConst.UPDATE_BLE_STATE);
                            stateIntent.putExtra("state",bluetoothState);
                            sendBroadcast(stateIntent);
                        }else {
                            mac = LoadDataUtil.newInstance().getMacAddress(MathUtil.newInstance().getUserId(mSevice));
                            connect();
                        }
                    }
                    break;
                }

                break;
            case BroadcastConst.START_SEARCH_DEVICE:
                deviceName = intent.getStringExtra("deviceName");
                searchDevice(intent.getBooleanExtra("search",false));
                break;
            case BroadcastConst.DOWNLOAD_FILE:{
                String fileUrl = intent.getStringExtra("fileUrl");
                downloadFirmsoft(fileUrl);
            }
            break;
            case DownloadManager.ACTION_DOWNLOAD_COMPLETE:{
                checkDownloadStatus();
            }
            break;
            case BroadcastConst.UPDATE_DIAL_STATE:{
                if (bluetoothState != 3){
                    ProgressHudModel.newInstance().diss();
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BleService.this,getString(R.string.ble_error),Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                int command = intent.getIntExtra("command", SendFileConst.ERROR);
                if (command == SendFileConst.PROGRESS){//收到进度加1的广播，再发送100包数据
                    LogUtil.getInstance().logd("data******","发送数据包 page = "+page);
                    newTimerTask(0);
                }else if (command == SendFileConst.ERROR){//收到接收错误的包，直接停止发送，清除缓存的数据
                    LogUtil.getInstance().logd("data******","接收到错误包 page = "+page);
                    fileDatas = new byte[0];
                    index = 0;
                    this.page = 0;
                    this.command = 4;
                }else if (command == SendFileConst.FINISH){//收到接收完成的包，清除缓存的数据
                    LogUtil.getInstance().logd("data******","接收到完成包 page = "+page);
                    fileDatas = new byte[0];
                    index = 0;
                    this.page = 0;
                    this.command = 4;
                }else if (command == SendFileConst.CONTINUE){//收到断点续传的包，重新开启发送
                    int pageNum = intent.getIntExtra("page",0);
                    if (!isError){
                        isError = true;
                        removeTimeTask();
                        page = pageNum*175;
                        newTimerTask(500);
                    }
                }
            }
            break;
            case BroadcastConst.UPDATE_BACKGROUND_STATE:{
                if (bluetoothState != 3){
                    ProgressHudModel.newInstance().diss();
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BleService.this,getString(R.string.ble_error),Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                int command = intent.getIntExtra("command",255);
                if (command == SendFileConst.FINISH){
                    i = 0;
                    datas = null;
                }else if (command == SendFileConst.ERROR){
                    i = 0;
                    datas = null;
                }else if (command == SendFileConst.PROGRESS){
                    sendBackgroundByte();
                }
            }
            break;
            case BroadcastConst.SEND_BLE_BACKGROUND:{
                if (bluetoothState != 3){
                    ProgressHudModel.newInstance().diss();
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BleService.this,getString(R.string.ble_error),Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                if (iBluetoothUtil==null)
                    return;
                int command = intent.getIntExtra("command",0);
                if (command == 0){
                    byte clock = (byte) intent.getIntExtra("clock",0);
                    iBluetoothUtil.writeForSendDialBackground(0,clock,0,0,new byte[0]);
                }else{
                    i = 0;
                    String pictureUrl = intent.getStringExtra("pictureUrl");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    FileInputStream fis;
                    try {
                        fis = new FileInputStream(new File(pictureUrl));
                        byte[] buf = new byte[1024];
                        int n;
                        while (-1 != (n = fis.read(buf)))
                            baos.write(buf, 0, n);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    datas = baos.toByteArray();
                    sendBackgroundByte();
                }
            }
            break;
            case BroadcastConst.SEND_BLE_FILE:{
                if (bluetoothState != 3){
                    ProgressHudModel.newInstance().diss();
                    new Handler(getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BleService.this,getString(R.string.ble_error),Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                if (iBluetoothUtil==null)
                    return;
                int command = intent.getIntExtra("command",0);
                if (command == 3||command == 6){//收到开始发送文件的广播，发送起始包到手表端
                    byte clock = (byte) intent.getIntExtra("clock", 0);
                    LogUtil.getInstance().logd("data******","发送开始包 clock = "+clock);
                    iBluetoothUtil.writeForSendDialFile(command,clock,0,0,null);

                }else if (command == 4||command == 7){//开始发送数据到手表端

                    String fileUrl = intent.getStringExtra("fileUrl");
                    int address = intent.getIntExtra("address",0);
                    int page = intent.getIntExtra("page",0);

                    if (fileUrl==null)
                        return;
                    try {
                        InputStream in = new FileInputStream(fileUrl);
                        byte[] datas =  FileUtil.getInstance().toByteArray(in);
                        fileDatas = datas;
                        index = address;
                        this.page = page;
                        this.command = command;
                        newTimerTask(0);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            break;
        }
    }
}