package server;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import database.Database;
import sensor.SensorMessage;

public class DBReader implements Runnable {

	static int snsrCnt = 0;

	Database db = new Database();

	DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date msgDate = null;
	Date nowDate = null;
	List<SensorMessage> snsrList = new ArrayList<SensorMessage>();
	List<SensorMessage> emergencyList = new ArrayList<SensorMessage>();

	// seep for 10 sec and reads the sensor updates from the xml file
	// check for emergency and sensor not responding situations
	// if any send notifications to all the connected monitors
	@Override
	public void run() {
		try {
			while (true) {
				snsrList = db.readSensorData("SensorHistory.xml");
				snsrCnt = snsrList.size();
				checkData();
				Thread.sleep(10000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void checkData() {
		emergencyList.removeAll(emergencyList);
		System.out.println("checking data");
		nowDate = new Date();
		for (SensorMessage msg : snsrList) {

			String dateString = msg.date;
			try {
				// get the responded date and time
				msgDate = df.parse(dateString);

				// get the difference between dates
				long diff = nowDate.getTime() - msgDate.getTime();

				long diffMinutes = diff / (60 * 1000) % 60;

				/*
				 * for the original system with 1h readings long diffHours = diff / (60 * 60 *
				 * 1000) % 24;
				 */
				
				// check whether difference is greater than 1min
				// and set the response flag
				if (diffMinutes >= 1) {
					System.out.println("not responding" + diffMinutes);
					msg.setResponsFlag(false);
					emergencyList.add(msg);
				}

				// check whether emegency flag is set
				else if (msg.isEmergencyFlag() == true) {
					System.out.println("emergency");
					emergencyList.add(msg);
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("error");
			}
		}
		// notify all the registered listerners
		FireEmergencyServer.FireAlarmSensorServer.notifyListeners(emergencyList, "emergency");
	}
}
