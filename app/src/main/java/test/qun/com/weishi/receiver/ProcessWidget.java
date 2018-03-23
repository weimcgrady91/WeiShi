package test.qun.com.weishi.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import test.qun.com.weishi.R;
import test.qun.com.weishi.service.UpdateWidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class ProcessWidget extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
    }

    @Override
    public void onDisabled(Context context) {
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.stopService(intent);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.process_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

