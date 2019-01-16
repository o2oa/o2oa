package net.zoneland.x.bpm.mobile.v1.zoneXBPM;

import android.app.Application;
import android.test.ApplicationTestCase;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.StringUtil;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testClassUtil() {

        String phone = "853090000";
        if (StringUtil.isPhoneWithHKandMACAO(phone)) {
            System.out.print(".............");

        }else  {
            System.out.print("lllllllllllll");
        }
    }
}