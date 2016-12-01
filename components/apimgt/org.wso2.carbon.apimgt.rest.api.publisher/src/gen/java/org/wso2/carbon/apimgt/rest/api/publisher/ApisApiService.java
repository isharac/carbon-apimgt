package org.wso2.carbon.apimgt.rest.api.publisher;

import org.wso2.carbon.apimgt.rest.api.publisher.*;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.*;

import org.wso2.msf4j.formparam.FormDataParam;
import org.wso2.msf4j.formparam.FileInfo;

import org.wso2.carbon.apimgt.rest.api.publisher.dto.APIDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.APIListDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.DocumentDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.DocumentListDTO;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.ErrorDTO;
import java.io.File;
import org.wso2.carbon.apimgt.rest.api.publisher.dto.FileInfoDTO;

import java.util.List;
import org.wso2.carbon.apimgt.rest.api.publisher.NotFoundException;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "class org.wso2.maven.plugins.JavaMSF4JServerCodegen", date = "2016-11-30T11:33:50.722+05:30")
public abstract class ApisApiService {
    public abstract Response apisApiIdDelete(String apiId
 ,String ifMatch
 ,String ifUnmodifiedSince
 ) throws NotFoundException;
    public abstract Response apisApiIdDocumentsDocumentIdContentGet(String apiId
 ,String documentId
 ,String accept
 ,String ifNoneMatch
 ,String ifModifiedSince
 ) throws NotFoundException;
    public abstract Response apisApiIdDocumentsDocumentIdContentPost(String apiId
 ,String documentId
 ,String contentType
 ,InputStream fileInputStream, FileInfo fileDetail
 ,String inlineContent
 ,String ifMatch
 ,String ifUnmodifiedSince
 ) throws NotFoundException;
    public abstract Response apisApiIdDocumentsDocumentIdDelete(String apiId
 ,String documentId
 ,String ifMatch
 ,String ifUnmodifiedSince
 ) throws NotFoundException;
    public abstract Response apisApiIdDocumentsDocumentIdGet(String apiId
 ,String documentId
 ,String accept
 ,String ifNoneMatch
 ,String ifModifiedSince
 ) throws NotFoundException;
    public abstract Response apisApiIdDocumentsDocumentIdPut(String apiId
 ,String documentId
 ,DocumentDTO body
 ,String contentType
 ,String ifMatch
 ,String ifUnmodifiedSince
 ) throws NotFoundException;
    public abstract Response apisApiIdDocumentsGet(String apiId
 ,Integer limit
 ,Integer offset
 ,String accept
 ,String ifNoneMatch
 ) throws NotFoundException;
    public abstract Response apisApiIdDocumentsPost(String apiId
 ,DocumentDTO body
 ,String contentType
 ) throws NotFoundException;
    public abstract Response apisApiIdGatewayConfigGet(String apiId
 ,String accept
 ,String ifNoneMatch
 ,String ifModifiedSince
 ) throws NotFoundException;
    public abstract Response apisApiIdGatewayConfigPut(String apiId
 ,String gatewayConfig
 ,String contentType
 ,String ifMatch
 ,String ifUnmodifiedSince
 ) throws NotFoundException;
    public abstract Response apisApiIdGet(String apiId
 ,String accept
 ,String ifNoneMatch
 ,String ifModifiedSince
 ) throws NotFoundException;
    public abstract Response apisApiIdPut(String apiId
 ,APIDTO body
 ,String contentType
 ,String ifMatch
 ,String ifUnmodifiedSince
 ) throws NotFoundException;
    public abstract Response apisApiIdSwaggerGet(String apiId
 ,String accept
 ,String ifNoneMatch
 ,String ifModifiedSince
 ) throws NotFoundException;
    public abstract Response apisApiIdSwaggerPut(String apiId
 ,String apiDefinition
 ,String contentType
 ,String ifMatch
 ,String ifUnmodifiedSince
 ) throws NotFoundException;
    public abstract Response apisApiIdThumbnailGet(String apiId
 ,String accept
 ,String ifNoneMatch
 ,String ifModifiedSince
 ) throws NotFoundException;
    public abstract Response apisApiIdThumbnailPost(String apiId
 ,InputStream fileInputStream, FileInfo fileDetail
 ,String contentType
 ,String ifMatch
 ,String ifUnmodifiedSince
 ) throws NotFoundException;
    public abstract Response apisChangeLifecyclePost(String action
 ,String apiId
 ,String lifecycleChecklist
 ,String ifMatch
 ,String ifUnmodifiedSince
 ) throws NotFoundException;
    public abstract Response apisCopyApiPost(String newVersion
 ,String apiId
 ) throws NotFoundException;
    public abstract Response apisGet(Integer limit
 ,Integer offset
 ,String query
 ,String accept
 ,String ifNoneMatch
 ) throws NotFoundException;
    public abstract Response apisHead(String query
 ,String accept
 ,String ifNoneMatch
 ) throws NotFoundException;
    public abstract Response apisPost(APIDTO body
 ,String contentType
 ) throws NotFoundException;
}
