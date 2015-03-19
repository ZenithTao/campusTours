package com.yiweigao.campustours;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.suitebuilder.annotation.MediumTest;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    
    MainActivity mMainActivity;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mMainActivity = new MainActivity();
    }

    public ApplicationTest() {
        super(Application.class);
    }

    @MediumTest
    public void testOnCreate() throws Exception {
        
    }
}