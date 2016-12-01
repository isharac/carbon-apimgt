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
package org.wso2.carbon.apimgt.core.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.apimgt.core.api.APILifecycleManager;
import org.wso2.carbon.apimgt.core.api.APIManager;
import org.wso2.carbon.apimgt.core.dao.APISubscriptionDAO;
import org.wso2.carbon.apimgt.core.dao.ApiDAO;
import org.wso2.carbon.apimgt.core.dao.ApplicationDAO;
import org.wso2.carbon.apimgt.core.dao.PolicyDAO;
import org.wso2.carbon.apimgt.core.exception.APIManagementException;
import org.wso2.carbon.apimgt.core.exception.APIMgtDAOException;
import org.wso2.carbon.apimgt.core.exception.APIMgtResourceAlreadyExistsException;
import org.wso2.carbon.apimgt.core.models.API;
import org.wso2.carbon.apimgt.core.models.Application;
import org.wso2.carbon.apimgt.core.models.DocumentInfo;
import org.wso2.carbon.apimgt.core.models.Subscription;
import org.wso2.carbon.apimgt.core.util.APIUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Set;
/**
 * This class contains the implementation of the common methods for Publisher and store
 */
public abstract class AbstractAPIManager implements APIManager {

    private static final Logger log = LoggerFactory.getLogger(AbstractAPIManager.class);

    private ApiDAO apiDAO;
    private ApplicationDAO applicationDAO;
    private APISubscriptionDAO apiSubscriptionDAO;
    private PolicyDAO policyDAO;
    private String username;
    private APILifecycleManager apiLifecycleManager;

    public AbstractAPIManager(String username, ApiDAO apiDAO, ApplicationDAO applicationDAO, APISubscriptionDAO
            apiSubscriptionDAO, PolicyDAO policyDAO, APILifecycleManager apiLifecycleManager)  {
        this.username = username;
        this.apiDAO = apiDAO;
        this.applicationDAO = applicationDAO;
        this.apiSubscriptionDAO = apiSubscriptionDAO;
        this.policyDAO = policyDAO;
        this.apiLifecycleManager = apiLifecycleManager;
    }

    /**
     * Returns a list of all existing APIs by all providers. The API objects returned by this
     * method may be partially initialized (due to performance reasons). Each API instance
     * is guaranteed to have the API name, version, provider name, context, status and icon URL.
     * All other fields may not be initialized. Therefore, the objects returned by this method
     * must not be used to access any metadata item related to an API, other than the ones listed
     * above. For that purpose a fully initialized API object instance should be acquired by
     * calling the getAPI(String) method.
     *
     * @return a List of API objects (partially initialized), possibly empty
     * @throws APIManagementException on error
     */
    @Override
    public List<API> getAllAPIs() throws APIManagementException {
        return null;
    }

    /**
     * Returns details of an API.
     *
     * @param uuid UUID of the API's registry artifact
     * @return An API object related to the given artifact id or null
     * @throws APIManagementException if failed get API from String
     */
    @Override
    public API getAPIbyUUID(String uuid) throws APIManagementException {
        API api = null;
        try {
            api = apiDAO.getAPI(uuid);
        } catch (APIMgtDAOException e) {
            APIUtils.logAndThrowException("Error occurred while retrieving API with id " + uuid, e, log);
        }
        return api;
    }

    /**
     * Checks the Availability of given String
     *
     * @param api
     * @return true, if already exists. False, otherwise
     * @throws APIManagementException if failed to get API availability
     * @api
     */
    @Override
    public boolean isAPIAvailable(API api) throws APIManagementException {
        return false;
    }

    /**
     * Checks whether the given API context is already registered in the system
     *
     * @param context A String representing an API context
     * @return true if the context already exists and false otherwise
     * @throws APIManagementException if failed to check the context availability
     */
    @Override
    public boolean isContextExist(String context) throws APIManagementException {
        try {
            return getApiDAO().isAPIContextExists(context);
        } catch (APIMgtDAOException e) {
            APIUtils.logAndThrowException("Couldn't check API Context " + context + "Exists", e, log);
        }
        return false;
    }

    /**
     * Checks whether the given API name is already registered in the system
     *
     * @param apiName A String representing an API name
     * @return true if the api name already exists and false otherwise
     * @throws APIManagementException if failed to check the context availability
     */
    @Override
    public boolean isApiNameExist(String apiName) throws APIManagementException {
        try {
            return getApiDAO().isAPINameExists(apiName);
        } catch (APIMgtDAOException e) {
            APIUtils.logAndThrowException("Couldn't check API Name " + apiName + "Exists", e, log);
        }
        return false;
    }

    /**
     * Returns a set of API versions for the given provider and API name
     *
     * @param providerName name of the provider (common)
     * @param apiName      name of the api
     * @return Set of version strings (possibly empty)
     * @throws APIManagementException if failed to get version for api
     */
    @Override
    public Set<String> getAPIVersions(String providerName, String apiName) throws APIManagementException {
        return null;
    }

    /**
     * Returns the swagger v2.0 definition as a string
     *
     * @param api id of the String
     * @return swagger string
     * @throws APIManagementException
     */
    @Override
    public String getSwagger20Definition(String api) throws APIManagementException {
        try {
            return getApiDAO().getSwaggerDefinition(api);
        } catch (APIMgtDAOException e) {
            APIUtils.logAndThrowException("Couldn't retrieve swagger definition for apiId " + api, e, log);
        }
        return null;
    }

    /**
     * Returns a paginated list of documentation attached to a particular API
     *
     * @param apiId UUID of API
     * @param offset The number of results from the beginning that is to be ignored
     * @param limit The maximum number of results to be returned after the offset
     * @return {@link List<DocumentInfo>} Document meta data list
     * @throws APIManagementException if it failed to fetch Documentations
     */
    public List<DocumentInfo> getAllDocumentation(String apiId, int offset, int limit)
                                                                                throws APIManagementException {
        try {
            return getApiDAO().getDocumentsInfoList(apiId);
        } catch (APIMgtDAOException e) {
            APIUtils.logAndThrowException("Error occurred while retrieving documents", e, log);
        }
        return null;
    }

    /**
     * Get a summary of documentation by doc Id
     *
     * @param docId Document ID
     * @return {@link DocumentInfo} Documentation meta data
     * @throws APIManagementException if it failed to fetch Documentation
     */
    public DocumentInfo getDocumentationSummary(String docId) throws APIManagementException {
        try {
            return getApiDAO().getDocumentInfo(docId);
        } catch (APIMgtDAOException e) {
            APIUtils.logAndThrowException("Error occurred while retrieving document", e, log);
        }
        return null;
    }

    /**
     * This method used to get the content of a documentation
     *
     * @param docId Document ID
     * @return {@link InputStream} Input stream for document content
     * @throws APIManagementException if the requested documentation content is not available
     */
    public InputStream getDocumentationContent(String docId) throws APIManagementException {
        try {
            return getApiDAO().getDocumentFileContent(docId);
        } catch (APIMgtDAOException e) {
            APIUtils.logAndThrowException("Error occurred while retrieving document content", e, log);
        }
        return null;
    }

    /**
     * Returns the corresponding application given the uuid
     * @param uuid uuid of the Application
     * @param userId  Name of the User.
     * @param groupId Id of the group.
     * @return it will return Application corresponds to the uuid provided.
     * @throws APIManagementException
     */
    public Application getApplication(String uuid, String userId, String groupId) throws APIManagementException {
        Application application = null;
        try {
           application = getApplicationDAO().getApplication(uuid);
        } catch (APIMgtDAOException e) {
            APIUtils.logAndThrowException("Error occurred while retrieving application - " + uuid, e, log);
        }
        return application;
    }

    /**
     * Returns the subscriptions for api
     *
     * @param apiId
     * @return
     * @throws APIManagementException
     */
    @Override
    public List<Subscription> getSubscriptionsByAPI(String apiId) throws APIManagementException {
        try {
            return apiSubscriptionDAO.getAPISubscriptionsByAPI(apiId);
        } catch (APIMgtDAOException e) {
            throw new APIManagementException("Couldn't find subscriptions for apiId " + apiId, e);
        }
    }

    protected ApiDAO getApiDAO() {
        return apiDAO;
    }

    protected ApplicationDAO getApplicationDAO() {
        return applicationDAO;
    }

    protected APISubscriptionDAO getApiSubscriptionDAO() {
        return apiSubscriptionDAO;
    }

    protected PolicyDAO getPolicyDAO() {
        return policyDAO;
    }

    protected String getUsername() {
        return username;
    }

    protected void setUsername(String username) {
        this.username = username;
    }

    public APILifecycleManager getApiLifecycleManager() {
        return apiLifecycleManager;
    }

    protected final void handleResourceAlreadyExistsException(String msg) throws APIMgtResourceAlreadyExistsException {
        log.error(msg);
        throw new APIMgtResourceAlreadyExistsException(msg);
    }
}
