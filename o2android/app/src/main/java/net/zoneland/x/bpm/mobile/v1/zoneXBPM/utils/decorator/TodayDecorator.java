package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.decorator;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import net.muliba.changeskin.FancySkinManager;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

/**
 * Created by FancyLou on 2016/3/29.
 */
public class TodayDecorator implements DayViewDecorator {


    private final Drawable drawable;

    public TodayDecorator(Activity context) {
        drawable = FancySkinManager.Companion.instance().getDrawable(context, R.drawable.calendar_today_selector);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        CalendarDay today = CalendarDay.today();
        return today != null && day.equals(today);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(drawable);
    }
}
