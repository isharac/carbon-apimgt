/*
 *
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
 *
 */

package org.wso2.carbon.apimgt.core.dao.impl;

import org.wso2.carbon.apimgt.core.dao.ApplicationDAO;
import org.wso2.carbon.apimgt.core.exception.APIManagementDAOException;
import org.wso2.carbon.apimgt.core.models.Application;
import org.wso2.carbon.apimgt.core.models.ApplicationSummaryResults;

/**
 * Default implementation of the ApplicationDAO interface. Uses SQL syntax that is common to H2 and MySQL DBs.
 * Hence is considered as the default due to its re-usability.
 */
public class DefaultApplicationDAOImpl implements ApplicationDAO {

    DefaultApplicationDAOImpl() {}

    /**
     * Retrieve a given instance of an Application
     *
     * @param appID The UUID that uniquely identifies an Application
     * @return valid {@link Application} object or null
     * @throws APIManagementDAOException
     */
    @Override
    public Application getApplication(String appID) throws APIManagementDAOException {
        return null;
    }

    /**
     * Retrieves summary data of all available Applications. This method supports result pagination and
     * ensures results returned are those that belong to the specified username
     * @param offset The number of results from the beginning that is to be ignored
     * @param limit The maximum number of results to be returned after the offset
     * @param userName The username to filter results by
     * @return {@link ApplicationSummaryResults} matching results
     * @throws APIManagementDAOException
     *
     */
    @Override
    public ApplicationSummaryResults getApplicationsForUser(int offset, int limit, String userName)
                                                                            throws APIManagementDAOException {
        return null;
    }

    /**
     * Retrieves summary data of all available Applications. This method supports result pagination and
     * ensures results returned are those that belong to the specified Group ID
     *
     * @param offset  The number of results from the beginning that is to be ignored
     * @param limit   The maximum number of results to be returned after the offset
     * @param groupID The Group ID to filter results by
     * @return {@link ApplicationSummaryResults} matching results
     * @throws APIManagementDAOException
     */
    @Override
    public ApplicationSummaryResults getApplicationsForGroup(int offset, int limit, String groupID)
                                                                            throws APIManagementDAOException {
        return null;
    }

    /**
     * Retrieves summary data of all available Applications that match the given search criteria. This method supports
     * result pagination and ensuring results returned are for Apps belonging to the specified username
     * @param searchAttribute The attribute of an Application against which the search will be performed
     * @param searchString The search string provided
     * @param offset The number of results from the beginning that is to be ignored
     * @param limit The maximum number of results to be returned after the offset
     * @param userName The username to filter results by
     * @return {@link ApplicationSummaryResults} matching results
     * @throws APIManagementDAOException
     *
     */
    @Override
    public ApplicationSummaryResults searchApplicationsForUser(String searchAttribute, String searchString, int offset,
                                                         int limit, String userName) throws APIManagementDAOException {
        return null;
    }

    /**
     * Retrieves summary data of all available Applications that match the given search criteria. This method supports
     * result pagination and ensuring results returned are for Apps belonging to the specified Group ID
     *
     * @param searchAttribute The attribute of an Application against which the search will be performed
     * @param searchString    The search string provided
     * @param offset          The number of results from the beginning that is to be ignored
     * @param limit           The maximum number of results to be returned after the offset
     * @param groupID         The Group ID to filter results by
     * @return {@link ApplicationSummaryResults} matching results
     * @throws APIManagementDAOException
     */
    @Override
    public ApplicationSummaryResults searchApplicationsForGroup(String searchAttribute, String searchString,
                                              int offset, int limit, String groupID) throws APIManagementDAOException {
        return null;
    }

    /**
     * Add a new instance of an Application
     *
     * @param application The {@link Application} object to be added
     * @return The newly added {@link Application} object
     * @throws APIManagementDAOException
     */
    @Override
    public Application addApplication(Application application) throws APIManagementDAOException {
        return null;
    }

    /**
     * Update an existing Application
     *
     * @param appID                 The UUID of the Application that needs to be updated
     * @param substituteApplication Substitute {@link Application} object that will replace the existing Application
     * @return The updated {@link Application} object
     * @throws APIManagementDAOException
     */
    @Override
    public Application updateApplication(String appID, Application substituteApplication)
                                                                        throws APIManagementDAOException {
        return null;
    }

    /**
     * Remove an existing Application
     *
     * @param appID The UUID of the Application that needs to be deleted
     * @throws APIManagementDAOException
     */
    @Override
    public void deleteApplication(String appID) throws APIManagementDAOException {

    }
}
