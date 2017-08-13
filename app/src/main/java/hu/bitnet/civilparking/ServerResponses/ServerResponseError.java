package hu.bitnet.civilparking.ServerResponses;

import hu.bitnet.civilparking.Objects.BLE;
import hu.bitnet.civilparking.Objects.MQTT;
import hu.bitnet.civilparking.Objects.Parking_places;
import hu.bitnet.civilparking.Objects.Profile;

/**
 * Created by Attila on 2017.08.13..
 */

public class ServerResponseError {

    private Profile[] profile;
    private Error error;
    private String alert;
    private Parking_places parking_places;
    private Parking_places[] addresses;
    private MQTT mqtt;
    private BLE ble;
    private String start;
    private String stop;
    private String time;
    private String price;

    public Profile[] getProfile() { return profile; }
    public Error getError() { return error; }
    public String getAlert() { return alert; }
    //public Parking_places getParking_places() { return parking_places; }
    public Parking_places[] getAddress() { return addresses; }
    public MQTT getMQTT() { return mqtt; }
    public BLE getBLE() { return ble; }
    public String getStart() { return start; }
    public String getStop() { return stop; }
    public String getTime() { return time; }
    public String getPrice() { return price; }

}
