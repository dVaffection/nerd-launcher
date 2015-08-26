package com.dvlab.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends ListFragment {

    public static final String TAG = NerdLauncherFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager packageManager = getActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

        Log.i(TAG, activities.size() + " activities found");

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo lActivity, ResolveInfo rActivity) {
                String lhs = lActivity.loadLabel(packageManager).toString();
                String rhs = rActivity.loadLabel(packageManager).toString();

                return String.CASE_INSENSITIVE_ORDER.compare(lhs, rhs);
            }
        });

        ArrayAdapter<ResolveInfo> adapter =
                new ArrayAdapter<ResolveInfo>(getActivity(), R.layout.list_item, activities) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        if (convertView == null) {
                            convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item, null);
                        }

                        ResolveInfo resolveInfo = getItem(position);

                        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
                        icon.setImageDrawable(resolveInfo.loadIcon(packageManager));

                        TextView label = (TextView) convertView.findViewById(R.id.label);
                        label.setText(resolveInfo.loadLabel(packageManager));

                        return convertView;
                    }
                };
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ResolveInfo resolveInfo = (ResolveInfo) getListAdapter().getItem(position);
        ActivityInfo activityInfo = resolveInfo.activityInfo;

        if (activityInfo == null) return;

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName(activityInfo.packageName, activityInfo.name);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Log.i(TAG, activityInfo.packageName + " -> " + activityInfo.name);

        startActivity(intent);
    }
}
