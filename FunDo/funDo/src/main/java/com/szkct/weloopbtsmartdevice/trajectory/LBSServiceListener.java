package com.szkct.weloopbtsmartdevice.trajectory;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LBSServiceListener implements LocationListener {
	public int GPSCurrentStatus;
	public Location currentLocation;
	public void onLocationChanged(Location location) {
		//-------
		// Called when a new location is found by the location provider.
		if (currentLocation != null) {
			if (isBetterLocation(location, currentLocation)) {
				// Log.v("GPSTEST", "It's a better location");
				currentLocation = location;
			} else {
				// Log.v("GPSTEST", "Not very good!");
			}
		} else {
			// Log.v("GPSTEST", "It's first location");
			currentLocation = location;

		}
	}

	// 将数据通过get的方式发送到服务器,服务器可以根据这个数据进行跟踪用户的行走状态
	public void doGet(String string) {
		//
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		GPSCurrentStatus = status;
	}

	private static final int CHECK_INTERVAL = 1000 * 30;

	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > CHECK_INTERVAL;
		boolean isSignificantlyOlder = timeDelta < -CHECK_INTERVAL;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location,
		// use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must
			// be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
}
