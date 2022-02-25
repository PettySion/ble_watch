package com.szip.blewatch.base.Util.ble;

public interface IBluetoothUtil {
    void connect(String mac,IBluetoothState iBluetoothState);
    void disconnect();
    void sendCommand(byte []datas);
    void writeForSetUnit();
    void writeForSetWeather();
    void writeForFindWatch();
    void writeForUpdateUserInfo();
    void writeToSendNotify(String title,String label,int id);
}
