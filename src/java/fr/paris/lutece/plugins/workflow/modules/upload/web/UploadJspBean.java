/*
 * Copyright (c) 2002-2016, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.upload.web;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.workflow.modules.upload.business.file.IUploadFileDAO;
import fr.paris.lutece.plugins.workflow.modules.upload.business.file.UploadFile;
import fr.paris.lutece.plugins.workflow.modules.upload.business.history.UploadHistory;
import fr.paris.lutece.plugins.workflow.modules.upload.services.IUploadHistoryService;
import fr.paris.lutece.plugins.workflow.modules.upload.services.UploadResourceIdService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.web.task.ITaskComponentManager;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.security.SecurityTokenService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.util.http.SecurityUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 * This class manages uploaded files.
 */
@RequestScoped
@Named
public class UploadJspBean extends MVCAdminJspBean
{
    /** Generated serial id. */
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";
    private static final int PORT_HTTP = 80;
    private static final int PORT_HTTPS = 443;
    private static final long serialVersionUID = 5300419950066235152L;

    /** The Constant PARAMETER_ID_HISTORY. */
    // Parameters
    private static final String PARAMETER_ID_HISTORY = "id_history";

    /** The Constant PARAMETER_ID_TASK. */
    private static final String PARAMETER_ID_TASK = "id_task";
    private static final String PARAMETER_ID_FILE_UPLOAD = "id_file_upload";

    /** The Constant PARAMETER_RETURN_URL. */
    private static final String PARAMETER_RETURN_URL = "return_url";

    /** The Constant JSP_DO_REMOVE_UPLOAD. */
    // JSPs
    private static final String JSP_DO_REMOVE_UPLOAD = "jsp/admin/plugins/workflow/modules/upload/DoRemoveUpload.jsp";

    /** The Constant MESSAGE_CONFIRM_REMOVE_UPLOAD. */
    // Messages
    private static final String MESSAGE_CONFIRM_REMOVE_UPLOAD = "module.workflow.upload.message.confirm_remove_upload";

    /** The Constant PARAMETER_ENCODING. */
    // Other constants
    private static final String PARAMETER_ENCODING = "UTF-8";
    private static final String METHOD_POST = "POST";

    // Services
    @Inject
    private IUploadHistoryService _uploadHistoryService;
    
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    
    @Inject
    private ITaskService _taskService;
    
    @Inject
    private ITaskComponentManager _taskComponentManager;

    @Inject
    private IUploadFileDAO _uploadFileDAO;
    
    /**
     * Gets the confirm remove upload.
     *
     * @param request
     *            the request
     * @return the confirm remove upload
     * @throws AccessDeniedException
     *             the access denied exception
     * @throws UnsupportedEncodingException
     *             the unsupported encoding exception
     */
    public String getConfirmRemoveUpload( HttpServletRequest request ) throws AccessDeniedException, UnsupportedEncodingException
    {
        if ( !canDeleteUpload( request ) )
        {
            throw new AccessDeniedException( "The connected user is not allowed to delete this upload" );
        }

        String strIdFileUpload = request.getParameter( PARAMETER_ID_FILE_UPLOAD );
        String strIdHistory = request.getParameter( PARAMETER_ID_HISTORY );
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        String strReturnUrl = getSafeReturnUrl( request, request.getParameter( PARAMETER_RETURN_URL ) );

        Map<String, Object> parameters = new HashMap<>( );
        parameters.put( PARAMETER_ID_HISTORY, strIdHistory );
        parameters.put( PARAMETER_ID_FILE_UPLOAD, strIdFileUpload );
        parameters.put( PARAMETER_ID_TASK, strIdTask );
        parameters.put( PARAMETER_RETURN_URL, URLEncoder.encode( strReturnUrl, PARAMETER_ENCODING ) );
        parameters.put( SecurityTokenService.PARAMETER_TOKEN, getSecurityTokenService( ).getToken( request, JSP_DO_REMOVE_UPLOAD ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_UPLOAD, null, null, JSP_DO_REMOVE_UPLOAD, null, AdminMessage.TYPE_CONFIRMATION,
                parameters, strReturnUrl );
    }

    /**
     * Do remove upload.
     *
     * @param request
     *            the request
     * @return the string
     * @throws AccessDeniedException
     *             if the request is not posted with a valid security token or if the user is not allowed to delete the upload
     * @throws UnsupportedEncodingException
     *             the unsupported encoding exception
     */
    public String doRemoveUpload( HttpServletRequest request ) throws AccessDeniedException, UnsupportedEncodingException
    {
        if ( !METHOD_POST.equalsIgnoreCase( request.getMethod( ) ) || !getSecurityTokenService( ).validate( request, JSP_DO_REMOVE_UPLOAD ) )
        {
            throw new AccessDeniedException( ERROR_INVALID_TOKEN );
        }

        if ( !canDeleteUpload( request ) )
        {
            throw new AccessDeniedException( "The connected user is not allowed to delete this upload" );
        }

        String strIdFileUpload = request.getParameter( PARAMETER_ID_FILE_UPLOAD );
        int nIdFileUpload = WorkflowUtils.convertStringToInt( strIdFileUpload );
        String strIdHistory = request.getParameter( PARAMETER_ID_HISTORY );
        int nIdHistory = WorkflowUtils.convertStringToInt( strIdHistory );
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        int nIdTask = WorkflowUtils.convertStringToInt( strIdTask );

        // removing list file
        UploadFile uploadFile = _uploadFileDAO.findbyprimaryKey( nIdFileUpload, WorkflowUtils.getPlugin( ) );

        if ( uploadFile != null )
        {
            FileHome.remove( uploadFile.getIdFile( ) );
            _uploadFileDAO.deleteByid( nIdFileUpload, WorkflowUtils.getPlugin( ) );
        }

        List<UploadFile> listFile = _uploadFileDAO.load( nIdHistory, WorkflowUtils.getPlugin( ) );

        // Remove task history if not other file in the task
        if ( listFile.isEmpty( ) )
        {
            _uploadHistoryService.removeByHistory( nIdHistory, nIdTask, WorkflowUtils.getPlugin( ) );
        }

        // Remove history if no other task information to display
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );
        List<ITask> listActionTasks = _taskService.getListTaskByIdAction( resourceHistory.getAction( ).getId( ), request.getLocale( ) );

        Iterator<ITask> iterator = listActionTasks.iterator( );
        boolean informationToDisplay = false;

        while ( iterator.hasNext( ) )
        {
            ITask task = iterator.next( );

            String strTaskinformation = _taskComponentManager.getDisplayTaskInformation( resourceHistory.getId( ), request, request.getLocale( ), task );

            if ( !StringUtils.isEmpty( strTaskinformation ) )
            {
                informationToDisplay = true;

                break;
            }
        }

        if ( !informationToDisplay )
        {
            _resourceHistoryService.remove( nIdHistory );
        }

        String strReturnUrl = request.getParameter( PARAMETER_RETURN_URL );

        return getSafeReturnUrl( request, ( strReturnUrl != null ) ? URLDecoder.decode( strReturnUrl, PARAMETER_ENCODING ) : null );
    }

    /**
     * Gets the return URL if it stays in the application, the admin home page otherwise (no open redirect)
     *
     * @param request
     *            the request
     * @param strReturnUrl
     *            the return URL given in the request
     * @return the return URL, or the admin home page if it is blank or leaves the application
     */
    String getSafeReturnUrl( HttpServletRequest request, String strReturnUrl )
    {
        if ( StringUtils.isBlank( strReturnUrl ) )
        {
            return AppPathService.getBaseUrl( request ) + AppPathService.getAdminMenuUrl( );
        }
        if ( SecurityUtil.isInternalRedirectUrlSafe( strReturnUrl, request ) )
        {
            return strReturnUrl;
        }
        String strUrlWithDefaultPort = getUrlWithDefaultPort( strReturnUrl );
        if ( strUrlWithDefaultPort != null && SecurityUtil.isInternalRedirectUrlSafe( strUrlWithDefaultPort, request ) )
        {
            return strUrlWithDefaultPort;
        }

        return AppPathService.getBaseUrl( request ) + AppPathService.getAdminMenuUrl( );
    }

    /**
     * Gets an absolute URL written without port (the address of the browser) with the default port of its scheme written explicitly, as the base URL
     * of the application writes it (https://host:443/context/... behind a proxy in https)
     *
     * @param strUrl
     *            the URL
     * @return the URL with the default port of its scheme, or null if it is not an absolute http(s) URL without port nor user information
     */
    private static String getUrlWithDefaultPort( String strUrl )
    {
        try
        {
            URI uri = new URI( strUrl );
            if ( uri.getScheme( ) == null || uri.getHost( ) == null || uri.getPort( ) != -1 || uri.getRawUserInfo( ) != null )
            {
                return null;
            }
            int nPort = -1;
            if ( SCHEME_HTTPS.equalsIgnoreCase( uri.getScheme( ) ) )
            {
                nPort = PORT_HTTPS;
            }
            else
                if ( SCHEME_HTTP.equalsIgnoreCase( uri.getScheme( ) ) )
                {
                    nPort = PORT_HTTP;
                }
            if ( nPort == -1 )
            {
                return null;
            }
            StringBuilder sbUrl = new StringBuilder( uri.getScheme( ) ).append( "://" ).append( uri.getRawAuthority( ) ).append( ':' ).append( nPort )
                    .append( StringUtils.defaultString( uri.getRawPath( ) ) );
            if ( uri.getRawQuery( ) != null )
            {
                sbUrl.append( '?' ).append( uri.getRawQuery( ) );
            }
            if ( uri.getRawFragment( ) != null )
            {
                sbUrl.append( '#' ).append( uri.getRawFragment( ) );
            }
            return sbUrl.toString( );
        }
        catch( URISyntaxException e )
        {
            return null;
        }
    }

    /**
     * Can delete upload.
     *
     * @param request
     *            the request
     * @return true if the file belongs to the history and the user can delete the upload of this history
     */
    private boolean canDeleteUpload( HttpServletRequest request )
    {
        String strIdHistory = request.getParameter( PARAMETER_ID_HISTORY );
        int nIdHistory = WorkflowUtils.convertStringToInt( strIdHistory );
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        int nIdTask = WorkflowUtils.convertStringToInt( strIdTask );
        AdminUser userConnected = AdminUserService.getAdminUser( request );

        // The file must belong to the history whose rights are checked
        UploadFile uploadFile = _uploadFileDAO.findbyprimaryKey( WorkflowUtils.convertStringToInt( request.getParameter( PARAMETER_ID_FILE_UPLOAD ) ),
                WorkflowUtils.getPlugin( ) );

        if ( ( uploadFile == null ) || ( uploadFile.getIdHistory( ) != nIdHistory ) )
        {
            return false;
        }

        UploadHistory uploadValue = _uploadHistoryService.findByPrimaryKey( nIdHistory, nIdTask, WorkflowUtils.getPlugin( ) );

        boolean bHasPermissionDeletion = RBACService.isAuthorized( uploadValue, UploadResourceIdService.PERMISSION_DELETE, ( User ) userConnected );
        boolean bIsOwner = _uploadHistoryService.isOwner( nIdHistory, userConnected );

        return bHasPermissionDeletion || bIsOwner;
    }
}
