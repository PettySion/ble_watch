package com.szip.blewatch.base.Util.ble;

public interface IBluetoothUtil {
    void connect(String mac,IBluetoothState iBluetoothState);
    void disconnect();
    void sendCommand(byte []datas);
    void writeForSetUnit();
    void writeForSetWeather();
    void writeForFindWatch(int state);
    void writeForUpdateUserInfo();
    void writeToSendNotify(String title,String label,int id);
    void writeForSendDialFile(int type,byte clockId,int address,int num,byte[] data);
    void writeForSendDialBackground(int type,int clockType,int clockIndex,int num,byte[] datas);
    void writeForUpdate();
}
