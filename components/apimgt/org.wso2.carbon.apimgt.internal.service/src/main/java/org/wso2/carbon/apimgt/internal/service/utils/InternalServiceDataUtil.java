/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.apimgt.internal.service.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.lang3.StringUtils;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.dto.ConditionDTO;
import org.wso2.carbon.apimgt.api.model.subscription.API;
import org.wso2.carbon.apimgt.api.model.subscription.APIPolicy;
import org.wso2.carbon.apimgt.api.model.subscription.APIPolicyConditionGroup;
import org.wso2.carbon.apimgt.api.model.subscription.Application;
import org.wso2.carbon.apimgt.api.model.subscription.ApplicationKeyMapping;
import org.wso2.carbon.apimgt.api.model.subscription.ApplicationPolicy;
import org.wso2.carbon.apimgt.api.model.subscription.Subscription;
import org.wso2.carbon.apimgt.api.model.subscription.SubscriptionPolicy;
import org.wso2.carbon.apimgt.api.model.subscription.URLMapping;
import org.wso2.carbon.apimgt.impl.internal.ServiceReferenceHolder;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;
import org.wso2.carbon.apimgt.internal.service.dto.APIDTO;
import org.wso2.carbon.apimgt.internal.service.dto.APIListDTO;
import org.wso2.carbon.apimgt.internal.service.dto.ApiPolicyConditionGroupDTO;
import org.wso2.carbon.apimgt.internal.service.dto.ApiPolicyDTO;
import org.wso2.carbon.apimgt.internal.service.dto.ApiPolicyListDTO;
import org.wso2.carbon.apimgt.internal.service.dto.ApplicationAttributeDTO;
import org.wso2.carbon.apimgt.internal.service.dto.ApplicationDTO;
import org.wso2.carbon.apimgt.internal.service.dto.ApplicationKeyMappingDTO;
import org.wso2.carbon.apimgt.internal.service.dto.ApplicationKeyMappingListDTO;
import org.wso2.carbon.apimgt.internal.service.dto.ApplicationListDTO;
import org.wso2.carbon.apimgt.internal.service.dto.ApplicationPolicyDTO;
import org.wso2.carbon.apimgt.internal.service.dto.ApplicationPolicyListDTO;
import org.wso2.carbon.apimgt.internal.service.dto.GroupIdDTO;
import org.wso2.carbon.apimgt.internal.service.dto.SubscriptionDTO;
import org.wso2.carbon.apimgt.internal.service.dto.SubscriptionListDTO;
import org.wso2.carbon.apimgt.internal.service.dto.SubscriptionPolicyDTO;
import org.wso2.carbon.apimgt.internal.service.dto.SubscriptionPolicyListDTO;
import org.wso2.carbon.apimgt.internal.service.dto.URLMappingDTO;
import org.wso2.carbon.apimgt.rest.api.util.RestApiConstants;
import org.wso2.carbon.apimgt.rest.api.util.dto.ErrorDTO;
import org.wso2.carbon.apimgt.rest.api.util.exception.ForbiddenException;
import org.wso2.carbon.apimgt.rest.api.util.utils.RestApiUtil;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InternalServiceDataUtil {
    private static final Log log = LogFactory.getLog(InternalServiceDataUtil.class);

    private static APIDTO fromAPItoDTO(API model) {

        APIDTO apidto = null;
        if (model != null) {
            apidto = new APIDTO();
            apidto.setApiId(model.getApiId());
            apidto.setVersion(model.getVersion());
            apidto.setName(model.getName());
            apidto.setContext(model.getContext());
            apidto.setPolicy(model.getPolicy());
            apidto.setProvider(model.getProvider());
            apidto.setApiType(model.getApiType());
            apidto.setName(model.getName());
            apidto.setIsDefaultVersion(model.isDefaultVersion());
            Map<String,URLMapping> urlMappings = model.getAllResources();
            List<URLMappingDTO> urlMappingsDTO = new ArrayList<>();
            for (URLMapping urlMapping : urlMappings.values()) {
                URLMappingDTO urlMappingDTO = new URLMappingDTO();
                urlMappingDTO.setAuthScheme(urlMapping.getAuthScheme());
                urlMappingDTO.setHttpMethod(urlMapping.getHttpMethod());
                urlMappingDTO.setThrottlingPolicy(urlMapping.getThrottlingPolicy());
                urlMappingDTO.setUrlPattern(urlMapping.getUrlPattern());
                urlMappingDTO.setScopes(urlMapping.getScopes());
                urlMappingsDTO.add(urlMappingDTO);
            }
            apidto.setUrlMappings(urlMappingsDTO);
        }
        return apidto;
    }

    public static APIListDTO fromAPIToAPIListDTO(API model) {

        APIListDTO apiListdto = new APIListDTO();
        if (model != null) {
            APIDTO apidto = new APIDTO();
            apidto.setApiId(model.getApiId());
            apidto.setVersion(model.getVersion());
            apidto.setContext(model.getContext());
            apidto.setPolicy(model.getPolicy());
            apidto.setProvider(model.getProvider());
            apidto.setApiType(model.getApiType());
            apidto.setName(model.getName());
            apidto.setIsDefaultVersion(model.isDefaultVersion());
            Map<String,URLMapping> urlMappings = model.getAllResources();
            List<URLMappingDTO> urlMappingsDTO = new ArrayList<>();
            for (URLMapping urlMapping : urlMappings.values()) {
                URLMappingDTO urlMappingDTO = new URLMappingDTO();
                urlMappingDTO.setAuthScheme(urlMapping.getAuthScheme());
                urlMappingDTO.setHttpMethod(urlMapping.getHttpMethod());
                urlMappingDTO.setThrottlingPolicy(urlMapping.getThrottlingPolicy());
                urlMappingDTO.setUrlPattern(urlMapping.getUrlPattern());
                urlMappingDTO.setScopes(urlMapping.getScopes());
                urlMappingsDTO.add(urlMappingDTO);
            }
            apidto.setUrlMappings(urlMappingsDTO);
            apiListdto.setCount(1);
            apiListdto.getList().add(apidto);
        } else {
            apiListdto.setCount(0);
        }
        return apiListdto;
    }

    public static APIListDTO fromAPIListToAPIListDTO(List<API> apiList) {

        APIListDTO apiListDTO = new APIListDTO();

        if (apiList != null) {
            for (API api : apiList) {
                apiListDTO.getList().add(fromAPItoDTO(api));
            }
            apiListDTO.setCount(apiList.size());
        } else {
            apiListDTO.setCount(0);
        }

        return apiListDTO;
    }

    public static ApplicationListDTO fromApplicationToApplicationListDTO(List<Application> model) {

        ApplicationListDTO applicationListDTO = new ApplicationListDTO();
        if (model != null) {
            for (Application appModel : model) {
                ApplicationDTO applicationDTO = new ApplicationDTO();
                applicationDTO.setId(appModel.getId());
                applicationDTO.setName(appModel.getName());
                applicationDTO.setPolicy(appModel.getPolicy());
                applicationDTO.setSubName(appModel.getSubName());
                applicationDTO.setTokenType(appModel.getTokenType());

                Set<String> groupIds = appModel.getGroupIds();
                for (String grp : groupIds) {
                    GroupIdDTO groupIdDTO = new GroupIdDTO();
                    groupIdDTO.setApplicationId(appModel.getId());
                    groupIdDTO.setGroupId(grp);
                    applicationDTO.getGroupIds().add(groupIdDTO);
                }

                Map<String, String> attributes = appModel.getAttributes();
                for (String attrib : attributes.keySet()) {
                    ApplicationAttributeDTO applicationAttributeDTO = new ApplicationAttributeDTO();
                    applicationAttributeDTO.setName(attrib);
                    applicationAttributeDTO.setValue(attributes.get(attrib));
                    applicationDTO.getAttributes().add(applicationAttributeDTO);
                }
                applicationListDTO.getList().add(applicationDTO);
            }
            applicationListDTO.setCount(model.size());

        } else {
            applicationListDTO.setCount(0);
        }
        return applicationListDTO;
    }

    public static SubscriptionListDTO fromSubscriptionToSubscriptionListDTO(List<Subscription> model) {

        SubscriptionListDTO subscriptionListDTO = new SubscriptionListDTO();
        if (model != null) {
            for (Subscription subsModel : model) {
                SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
                subscriptionDTO.setApiId(subsModel.getApiId());
                subscriptionDTO.setAppId(subsModel.getAppId());
                subscriptionDTO.setSubscriptionId(subsModel.getSubscriptionId());
                subscriptionDTO.setPolicyId(subsModel.getPolicyId());
                subscriptionDTO.setSubscriptionState(subsModel.getSubscriptionState());

                subscriptionListDTO.getList().add(subscriptionDTO);

            }
            subscriptionListDTO.setCount(model.size());

        } else {
            subscriptionListDTO.setCount(0);
        }
        return subscriptionListDTO;
    }

    public static SubscriptionPolicyListDTO fromSubscriptionPolicyToSubscriptionPolicyListDTO (
            List<SubscriptionPolicy> model) {

        SubscriptionPolicyListDTO subscriptionPolicyListDTO = new SubscriptionPolicyListDTO();
        if (model != null) {
            for (SubscriptionPolicy subscriptionPolicyModel : model) {
                SubscriptionPolicyDTO subscriptionPolicyDTO = new SubscriptionPolicyDTO();
                subscriptionPolicyDTO.setId(subscriptionPolicyModel.getId());
                subscriptionPolicyDTO.setName(subscriptionPolicyModel.getName());
                subscriptionPolicyDTO.setQuotaType(subscriptionPolicyModel.getQuotaType());
                subscriptionPolicyDTO.setGraphQLMaxDepth(subscriptionPolicyModel.getGraphQLMaxDepth());
                subscriptionPolicyDTO.setGraphQLMaxComplexity(subscriptionPolicyModel.getGraphQLMaxComplexity());
                subscriptionPolicyDTO.setTenantId(subscriptionPolicyModel.getTenantId());
                subscriptionPolicyDTO.setRateLimitCount(subscriptionPolicyModel.getRateLimitCount());
                subscriptionPolicyDTO.setStopOnQuotaReach(subscriptionPolicyModel.isStopOnQuotaReach());
                subscriptionPolicyDTO.setRateLimitTimeUnit(subscriptionPolicyModel.getRateLimitTimeUnit());

                subscriptionPolicyListDTO.getList().add(subscriptionPolicyDTO);

            }
            subscriptionPolicyListDTO.setCount(model.size());

        } else {
            subscriptionPolicyListDTO.setCount(0);
        }
        return subscriptionPolicyListDTO;
    }

    public static ApplicationPolicyListDTO fromApplicationPolicyToApplicationPolicyListDTO(List<ApplicationPolicy> model) {

        ApplicationPolicyListDTO applicationPolicyListDTO = new ApplicationPolicyListDTO();
        if (model != null) {
            for (ApplicationPolicy applicationPolicyModel : model) {
                ApplicationPolicyDTO applicationPolicyDTO = new ApplicationPolicyDTO();
                applicationPolicyDTO.setId(applicationPolicyModel.getId());
                applicationPolicyDTO.setName(applicationPolicyModel.getName());
                applicationPolicyDTO.setQuotaType(applicationPolicyModel.getQuotaType());
                applicationPolicyDTO.setTenantId(applicationPolicyModel.getTenantId());

                applicationPolicyListDTO.getList().add(applicationPolicyDTO);

            }
            applicationPolicyListDTO.setCount(model.size());

        } else {
            applicationPolicyListDTO.setCount(0);
        }
        return applicationPolicyListDTO;
    }

    public static ApiPolicyListDTO fromApiPolicyToApiPolicyListDTO(List<APIPolicy> model) {

        ApiPolicyListDTO apiPolicyListDTO = new ApiPolicyListDTO();
        if (model != null) {
            for (APIPolicy apiPolicyModel : model) {
                ApiPolicyDTO policyDTO = new ApiPolicyDTO();
                policyDTO.setName(apiPolicyModel.getName());
                policyDTO.setQuotaType(apiPolicyModel.getQuotaType());
                policyDTO.setTenantId(apiPolicyModel.getTenantId());
                policyDTO.setApplicableLevel(apiPolicyModel.getApplicableLevel());
                apiPolicyListDTO.getList().add(policyDTO);

                List<APIPolicyConditionGroup> retrievedGroups = apiPolicyModel.getConditionGroups();
                List<ApiPolicyConditionGroupDTO> condGroups = new ArrayList<ApiPolicyConditionGroupDTO>();
                for (APIPolicyConditionGroup retGroup : retrievedGroups) {
                    ApiPolicyConditionGroupDTO group = new ApiPolicyConditionGroupDTO();
                    group.setConditionGroupId(retGroup.getConditionGroupId());
                    group.setQuotaType(retGroup.getQuotaType());
                    group.setPolicyId(retGroup.getPolicyId());

                    List<org.wso2.carbon.apimgt.internal.service.dto.ConditionDTO> condition = 
                            new ArrayList<org.wso2.carbon.apimgt.internal.service.dto.ConditionDTO>();

                    List<ConditionDTO> retrievedConditions = retGroup.getConditionDTOS();
                    for (ConditionDTO retrievedCondition : retrievedConditions) {
                        org.wso2.carbon.apimgt.internal.service.dto.ConditionDTO conditionDTO = 
                                new org.wso2.carbon.apimgt.internal.service.dto.ConditionDTO();
                        conditionDTO.setConditionType(retrievedCondition.getConditionType());
                        conditionDTO.setIsInverted(retrievedCondition.isInverted());
                        conditionDTO.setName(retrievedCondition.getConditionName());
                        conditionDTO.setValue(retrievedCondition.getConditionValue());
                        condition.add(conditionDTO);
                    }
                    group.setCondition(condition);
                    condGroups.add(group);
                }
                policyDTO.setConditionGroups(condGroups);
            }
            apiPolicyListDTO.setCount(model.size());
        } else {
            apiPolicyListDTO.setCount(0);
        }
        return apiPolicyListDTO;
    }

    public static ApplicationKeyMappingListDTO fromApplicationKeyMappingToApplicationKeyMappingListDTO(
            List<ApplicationKeyMapping> model) {

        ApplicationKeyMappingListDTO applicationKeyMappingListDTO = new ApplicationKeyMappingListDTO();
        if (model != null) {
            for (ApplicationKeyMapping applicationKeyMapping : model) {
                ApplicationKeyMappingDTO applicationKeyMappingDTO = new ApplicationKeyMappingDTO();
                applicationKeyMappingDTO.setApplicationId(applicationKeyMapping.getApplicationId());
                applicationKeyMappingDTO.setConsumerKey(applicationKeyMapping.getConsumerKey());
                applicationKeyMappingDTO.setKeyType(applicationKeyMapping.getKeyType());
                applicationKeyMappingDTO.setKeyManager(applicationKeyMapping.getKeyManager());
                applicationKeyMappingListDTO.getList().add(applicationKeyMappingDTO);

            }
            applicationKeyMappingListDTO.setCount(model.size());

        } else {
            applicationKeyMappingListDTO.setCount(0);
        }
        return applicationKeyMappingListDTO;
    }

    public static String validateTenantDomain(String xWSO2Tenant) {

        String tenantDomain = RestApiUtil.getLoggedInUserTenantDomain();
        if (xWSO2Tenant == null) {
            return tenantDomain;
        } else {
            if (MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equals(tenantDomain)) {
                return xWSO2Tenant;
            } else {
                return tenantDomain;
            }
        }

    }

    /*
     * Check whether the user is allowed to access the resources by validating logged in user tenant domain and
     * the role against super tenant
     *
     * @param targetTenantDomain Tenant domain of which resources are requested
     * @return isAuthorized
     *
     * */
    public static boolean isUserAuthorizedToTenant(String targetTenantDomain) {

        if (StringUtils.isEmpty(targetTenantDomain)) {
            handleUnauthorizedError();
        }
        String username = RestApiUtil.getLoggedInUsername();
        boolean isCrossTenantAccess = !targetTenantDomain.equals(MultitenantUtils.getTenantDomain(username));
        if (isCrossTenantAccess || MultitenantConstants.SUPER_TENANT_DOMAIN_NAME.equalsIgnoreCase(targetTenantDomain)) {
            String superAdminRole = null;
            try {
                superAdminRole = ServiceReferenceHolder.getInstance().getRealmService().getTenantUserRealm(
                        MultitenantConstants.SUPER_TENANT_ID).getRealmConfiguration().getAdminRoleName();
            } catch (UserStoreException e) {
                RestApiUtil.handleInternalServerError("Error in getting super admin role name", e, log);
            }

            //check whether logged in user is a super tenant user
            boolean isSuperTenantUser = RestApiUtil.getLoggedInUserTenantDomain().equals(
                    MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            if (!isSuperTenantUser) {
                String errorMsg = "Cross Tenant resource access is not allowed for this request. User " + username +
                        " is not allowed to access resources in " + targetTenantDomain + " as the requester is not a super " +
                        "tenant user";
                log.error(errorMsg);
                handleUnauthorizedError();
            }

            //check whether the user has super tenant admin role
            boolean isSuperAdminRoleNameExist = false;
            try {
                isSuperAdminRoleNameExist = APIUtil.isUserInRole(username, superAdminRole);
            } catch (UserStoreException | APIManagementException e) {
                RestApiUtil.handleInternalServerError("Error in checking whether the user has admin role", e, log);
            }

            if (!isSuperAdminRoleNameExist) {
                String errorMsg = "Cross Tenant resource access is not allowed for this request. User " + username +
                        " is not allowed to access resources in " + targetTenantDomain + " as the requester is not a " +
                        "super tenant admin";
                log.error(errorMsg);
                handleUnauthorizedError();
            }
        } else {
            String tenantAdminRole = null;
            try {
                tenantAdminRole = ServiceReferenceHolder.getInstance().getRealmService().
                        getTenantUserRealm(APIUtil.getTenantIdFromTenantDomain(targetTenantDomain)).
                        getRealmConfiguration().getAdminRoleName();
            } catch (UserStoreException e) {
                RestApiUtil.handleInternalServerError("Error in getting super admin role name", e, log);
            }
            //check for existent of tenant admin role
            boolean isTenantAdminRoleNameExist = false;
            try {
                isTenantAdminRoleNameExist = APIUtil.isUserInRole(username, tenantAdminRole);
            } catch (UserStoreException | APIManagementException e) {
                RestApiUtil.handleInternalServerError("Error in checking whether the user has admin role", e, log);
            }
            if (!isTenantAdminRoleNameExist ){
                String errorMsg = "Tenant resource access is not allowed for this request. User " + username +
                        " is not allowed to access resources in " + targetTenantDomain + " as the requester is not an " +
                        "admin";
                log.error(errorMsg);
                handleUnauthorizedError();
            }
        }
        return true;
    }

    public static void handleUnauthorizedError() {

        ErrorDTO errorDTO = RestApiUtil.getErrorDTO(RestApiConstants.STATUS_FORBIDDEN_MESSAGE_DEFAULT, 403l,
                "User is not allowed to access the requested resource");
        throw new ForbiddenException(errorDTO);
    }

}
