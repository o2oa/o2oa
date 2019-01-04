package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.decorator;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import net.muliba.changeskin.FancySkinManager;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;

import java.util.Date;

/**
 * Created by FancyLou on 2016/3/29.
 */
public class SelectorDecorator implements DayViewDecorator {

    private CalendarDay date;
    private final Drawable drawable;

    public SelectorDecorator(Context context) {
        date = CalendarDay.today();
        drawable = FancySkinManager.Companion.instance().getDrawable(context, R.drawable.calendar_selector);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }

    public void setDate(Date date) {
        this.date = CalendarDay.from(date);
    }
}
