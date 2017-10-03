package hu.bitnet.civilparking.Objects;

/**
 * Created by Attila on 2017.08.14..
 */

public class ParkingListObject {

    private String address;
    private String price;
    private String distance;
    private String time;
    private String id;
    private String latitude, longitude;
    private BLE ble;
    private MQTT mqtt;
    private String name;
    private String description;


    public String getAddress() { return address; }
    public String getPrice() { return price; }
    public String getDistance() { return distance; }
    public String getTime() { return time; }
    public String getId() { return id; }
    public String getLatitude() {return latitude;}
    public String getLongitude() {return longitude;}
    public BLE getBLE() { return ble; }
    public MQTT getMQTT() { return mqtt; }
    public String getName() { return name; }
    public String getDescription() { return description; }

}
