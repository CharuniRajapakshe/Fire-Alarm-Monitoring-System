package server;
import java.util.List;

import monitor.FireAlarmListener;
import sensor.SensorMessage;

public interface FireAlarmSensor extends java.rmi.Remote {
	
	public List<SensorMessage> getFireAlarmSensorData() throws java.rmi.RemoteException;
	
	public SensorMessage getCurrentData(String id) throws java.rmi.RemoteException;
		
	public void addFireAlarmListener(FireAlarmListener listener) throws java.rmi.RemoteException;

	public void removeFireAlarmListener(FireAlarmListener listener) throws java.rmi.RemoteException;

	public boolean login(String un, String pw) throws java.rmi.RemoteException;

}
