package de.onyxbits.geobookmark;

import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import android.content.Intent;

public class DashclockExtensionService extends DashClockExtension {
	public DashclockExtensionService() {
	}

	@Override
	protected void onUpdateData(int arg0) {
		String title = getString(R.string.dashclock_title);
		publishUpdate(new ExtensionData().visible(true).icon(R.drawable.ic_extension).status(title)
				.visible(true).clickIntent(new Intent(this, ProxyActivity.class)));
	}
}
