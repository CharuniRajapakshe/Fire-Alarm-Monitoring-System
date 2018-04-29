package server;
import java.util.ArrayList;
import java.util.List;

import database.Database;
import sensor.SensorMessage;

public class MonitorUpdate implements Runnable {

	Database db = new Database();

	List<SensorMessage> snsrList = new ArrayList<SensorMessage>();

	//seep for 1 min and reads the sensor updates from the xml file
	//then send the updated data to all the connected monitors
	@Override
	public void run() {
		try {
			while (true) {
				Thread.sleep(60000);
				snsrList = db.readSensorData("SensorHistory.xml");
				FireEmergencyServer.FireAlarmSensorServer.notifyListeners(snsrList,"update");
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
