package de.onyxbits.geobookmark;

import java.util.Date;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.format.DateFormat;
import android.widget.RemoteViews;
import android.widget.Toast;

public class BookmarkService extends Service implements LocationListener {

	private Thread timer;

	public BookmarkService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int cmd) {
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, this);
		timer = new Thread(new TimeOut(this, 41000));
		timer.start();
		Toast.makeText(this, R.string.getting_location, Toast.LENGTH_SHORT).show();

		return START_STICKY;
	}

	private void bookmark(boolean precise, double lat, double lon, String tag) {
		Uri uri = Uri.parse("geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(" + tag + ")");
		Intent target = new Intent(Intent.ACTION_VIEW, uri);

		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, target);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, tag);
		if (precise) {
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
					Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_bookmark_precise));
		}
		else {
			addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
					Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_bookmark_unprecise));
		}
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		sendBroadcast(addIntent);
	}

	@Override
	public void onLocationChanged(Location location) {
		timer.interrupt();
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		lm.removeUpdates(this);
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		bookmark(true, lat, lon, createTag());
		feedback();
		stopSelf();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	protected void timeout() {
		LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		lm.removeUpdates(this);
		if (loc != null) {
			bookmark(false, loc.getLatitude(), loc.getLongitude(), createTag());
			Toast.makeText(this,R.string.bad_fix,Toast.LENGTH_LONG).show();
		}
		else {
			// This happens if GPS is unavailable and no app has requested a location
			// since the last reboot.
			NotificationCompat.Builder builder =
	        new NotificationCompat.Builder(this)
	        .setSmallIcon(R.drawable.ic_stat_error)
	        .setContentTitle(getString(R.string.no_fix_title))
	        .setAutoCancel(true)
	        .setContentText(getString(R.string.no_fix_content));

			NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(0,builder.build());
		}
		feedback();
		stopSelf();
	}

	private String createTag() {
		Date d = new Date(System.currentTimeMillis());
		return DateFormat.getTimeFormat(this).format(d) + " "
				+ DateFormat.getDateFormat(this).format(d);
	}
	
	private void feedback() {
		Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone r = RingtoneManager.getRingtone(this, notification);
		r.play();
	}
}
