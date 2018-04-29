package sensor;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SensorMessage implements Serializable{

	public String type;
	private String id;
	public String location;	
	private double temp;
	private int battery;
	private int smoke;
	private int co2;
	private boolean responsFlag;
	private boolean emergencyFlag;
	public String date;

	public SensorMessage(){
		
	}
	
	public SensorMessage(String type, String id,String location, double temp, int battery, int smoke, int co2, boolean responsFlag,
			boolean emergencyFlag) {

		this.type = type;
		this.id = id;
		this.location = location;
		this.temp = temp;
		this.battery = battery;
		this.smoke = smoke;
		this.co2 = co2;
		this.responsFlag = responsFlag;
		this.emergencyFlag = emergencyFlag;
	}	

	public SensorMessage(String type, String id) {

		this.type = type;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@XmlElement
	public void setId(String id) {
		this.id = id;
	}

	public double getTemp() {
		return temp;
	}

	@XmlElement
	public void setTemp(double temp) {
		this.temp = temp;
	}

	public int getBattery() {
		return battery;
	}

	@XmlElement
	public void setBattery(int battery) {
		this.battery = battery;
	}

	public int getSmoke() {
		return smoke;
	}

	@XmlElement
	public void setSmoke(int smoke) {
		this.smoke = smoke;
	}

	public int getCo2() {
		return co2;
	}

	@XmlElement
	public void setCo2(int co2) {
		this.co2 = co2;
	}

	public boolean isResponsFlag() {
		return responsFlag;
	}

	@XmlElement
	public void setResponsFlag(boolean responsFlag) {
		this.responsFlag = responsFlag;
	}

	public boolean isEmergencyFlag() {
		return emergencyFlag;
	}

	@XmlElement
	public void setEmergencyFlag(boolean emergencyFlag) {
		this.emergencyFlag = emergencyFlag;
	}
	
	@Override
    public String toString() {
        return "Employee:: Name=" + this.id + " Age=" + this.temp + " Gender=" + this.smoke +
                " Role=" + this.date;
    }

}
