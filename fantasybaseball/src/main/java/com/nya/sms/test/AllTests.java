package com.nya.sms.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestBasicDataStore.class, TestEntityUserRole.class,
		TestStudentAndGroup.class, TestProgramScores.class, TestOtherStudentData.class,
		TestAuthorization.class, TestSite.class, TestProgram.class, TestPlayerProjections.class,
		TestProjectionProfiles.class})
public class AllTests {

}
