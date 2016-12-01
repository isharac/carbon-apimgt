/*
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.apimgt.core.dao.impl;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.apimgt.core.SampleTestObjectCreator;
import org.wso2.carbon.apimgt.core.TestUtil;
import org.wso2.carbon.apimgt.core.dao.ApplicationDAO;
import org.wso2.carbon.apimgt.core.models.Application;

import java.time.Duration;
import java.util.List;

public class ApplicationDAOImplIT extends DAOIntegrationTestBase {

    @Test
    public void testAddAndGetApplication() throws Exception {
        //add new app
        Application app = TestUtil.addTestApplication();
        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        //get added app
        Application appFromDB = applicationDAO.getApplication(app.getId());
        Assert.assertNotNull(appFromDB);
        //compare
        validateApp(appFromDB, app);
    }

    @Test
    public void testUpdateApplication() throws Exception {
        //add new app
        Application currentApp = TestUtil.addTestApplication();
        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        Application newApp = SampleTestObjectCreator.createAlternativeApplication();
        newApp.setUuid(currentApp.getId());
        newApp.setCreatedTime(currentApp.getCreatedTime());
        //update app
        applicationDAO.updateApplication(currentApp.getId(), newApp);
        //get app
        Application appFromDB = applicationDAO.getApplication(newApp.getId());
        Assert.assertNotNull(appFromDB);
        //compare
        validateApp(appFromDB, newApp);
    }

    @Test
    public void testDeleteApplication() throws Exception {
        // add app
        Application app = TestUtil.addTestApplication();
        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        //delete app
        applicationDAO.deleteApplication(app.getId());
        Application appFromDB = applicationDAO.getApplication(app.getId());
        Assert.assertNull(appFromDB);
    }

    @Test
    public void testIsApplicationNameExists() throws Exception {
        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        //check for a non-existing application
        Assert.assertFalse(applicationDAO.isApplicationNameExists("ExistingApp"));
        //add new app
        Application app = TestUtil.addTestApplication();
        //check for the existing application
        Assert.assertTrue(applicationDAO.isApplicationNameExists(app.getName()));
    }

    @Test
    public void testGetAllApplications() throws Exception {
        //add 4 apps
        String username = "admin";
        Application app1 = TestUtil.addCustomApplication("App1", username);
        Application app2 = TestUtil.addCustomApplication("App2", username);
        Application app3 = TestUtil.addCustomApplication("App3", username);
        Application app4 = TestUtil.addCustomApplication("App4", username);
        ApplicationDAO applicationDAO = DAOFactory.getApplicationDAO();
        //get added apps
        List<Application> appsFromDB = applicationDAO.getApplications(username);
        Assert.assertNotNull(appsFromDB);
        Assert.assertEquals(appsFromDB.size(), 4);
        for (Application application : appsFromDB) {
            Assert.assertNotNull(application);
            if (application.getName().equals(app1.getName())) {
                validateApp(application, app1);
            } else if (application.getName().equals(app2.getName())) {
                validateApp(application, app2);
            } else if (application.getName().equals(app3.getName())) {
                validateApp(application, app3);
            } else if (application.getName().equals(app4.getName())) {
                validateApp(application, app4);
            } else {
                Assert.fail("Invalid Application returned.");
            }
        }
    }

    @Test
    public void testGetApplicationsForUser() throws Exception {

    }

    @Test
    public void testGetApplicationsForGroup() throws Exception {

    }

    @Test
    public void testSearchApplicationsForUser() throws Exception {

    }

    @Test
    public void testSearchApplicationsForGroup() throws Exception {

    }

    private void validateApp(Application appFromDB, Application expectedApp) {
        Assert.assertEquals(appFromDB.getName(), expectedApp.getName());
        Assert.assertEquals(appFromDB.getCallbackUrl(), expectedApp.getCallbackUrl());
        Assert.assertEquals(appFromDB.getGroupId(), expectedApp.getGroupId());
        Assert.assertEquals(appFromDB.getStatus(), expectedApp.getStatus());
        Assert.assertEquals(appFromDB.getId(), expectedApp.getId());
        Assert.assertEquals(appFromDB.getTier(), expectedApp.getTier());
        Assert.assertEquals(appFromDB.getCreatedUser(), expectedApp.getCreatedUser());
        Assert.assertTrue(Duration.between(expectedApp.getCreatedTime(), appFromDB.getCreatedTime()).toMillis() < 1000L,
                "Application created time is not the same!");
        Assert.assertEquals(appFromDB.getUpdatedUser(), expectedApp.getUpdatedUser());
        Assert.assertTrue(Duration.between(expectedApp.getUpdatedTime(), appFromDB.getUpdatedTime()).toMillis() < 1000L,
                "Application updated time is not the same!");
    }

}