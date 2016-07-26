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

import fr.paris.lutece.plugins.workflow.modules.upload.business.file.UploadFile;
import fr.paris.lutece.plugins.workflow.modules.upload.business.history.UploadHistory;
import fr.paris.lutece.plugins.workflow.modules.upload.factory.FactoryDOA;
import fr.paris.lutece.plugins.workflow.modules.upload.factory.FactoryService;
import fr.paris.lutece.plugins.workflow.modules.upload.services.IUploadHistoryService;
import fr.paris.lutece.plugins.workflow.modules.upload.services.UploadHistoryService;
import fr.paris.lutece.plugins.workflow.modules.upload.services.UploadResourceIdService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.TaskComponentManager;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.plugins.workflowcore.web.task.ITaskComponentManager;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.util.mvc.admin.MVCAdminJspBean;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;

import java.net.URLDecoder;
import java.net.URLEncoder;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;


/**
 * This class manages uploaded files.
 */
public class UploadJspBean extends MVCAdminJspBean
{
    /** Generated serial id. */
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

    // Services
    private IUploadHistoryService _uploadHistoryService = SpringContextService.getBean( UploadHistoryService.BEAN_SERVICE );
    private IResourceHistoryService _resourceHistoryService = SpringContextService.getBean( ResourceHistoryService.BEAN_SERVICE );
    private ITaskService _taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );
    private ITaskComponentManager _taskComponentManager = SpringContextService.getBean( TaskComponentManager.BEAN_MANAGER );

    /**
     * Gets the confirm remove upload.
     *
     * @param request the request
     * @return the confirm remove upload
     * @throws AccessDeniedException the access denied exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public String getConfirmRemoveUpload( HttpServletRequest request )
        throws AccessDeniedException, UnsupportedEncodingException
    {
        if ( !canDeleteUpload( request ) )
        {
            throw new AccessDeniedException( "The connected user is not allowed to delete this upload" );
        }

        String strIdFileUpload = request.getParameter( PARAMETER_ID_FILE_UPLOAD );
        String strIdHistory = request.getParameter( PARAMETER_ID_HISTORY );
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        String strReturnUrl = request.getParameter( PARAMETER_RETURN_URL );

        UrlItem url = new UrlItem( JSP_DO_REMOVE_UPLOAD );
        url.addParameter( PARAMETER_ID_HISTORY, strIdHistory );
        url.addParameter( PARAMETER_ID_FILE_UPLOAD, strIdFileUpload );
        url.addParameter( PARAMETER_ID_TASK, strIdTask );
        url.addParameter( PARAMETER_RETURN_URL, URLEncoder.encode( strReturnUrl, PARAMETER_ENCODING ) );

        return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_UPLOAD, url.getUrl(  ),
            AdminMessage.TYPE_CONFIRMATION );
    }

    /**
     * Do remove upload.
     *
     * @param request the request
     * @return the string
     * @throws AccessDeniedException the access denied exception
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    public String doRemoveUpload( HttpServletRequest request )
        throws AccessDeniedException, UnsupportedEncodingException
    {
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

        //removing list file
        UploadFile uploadFile = FactoryDOA.getUploadFileDAO(  )
                                          .findbyprimaryKey( nIdFileUpload, WorkflowUtils.getPlugin(  ) );

        if ( uploadFile != null )
        {
            FileHome.remove( uploadFile.getIdFile(  ) );
            FactoryDOA.getUploadFileDAO(  ).deleteByid( nIdFileUpload, WorkflowUtils.getPlugin(  ) );
        }

        List<UploadFile> listFile = FactoryDOA.getUploadFileDAO(  ).load( nIdHistory, WorkflowUtils.getPlugin(  ) );

        // Remove task history if not other file in the task
        if ( listFile.isEmpty(  ) )
        {
            _uploadHistoryService.removeByHistory( nIdHistory, nIdTask, WorkflowUtils.getPlugin(  ) );
        }

        // Remove history if no other task information to display
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );
        List<ITask> listActionTasks = _taskService.getListTaskByIdAction( resourceHistory.getAction(  ).getId(  ),
                request.getLocale(  ) );

        Iterator<ITask> iterator = listActionTasks.iterator(  );
        boolean informationToDisplay = false;

        while ( iterator.hasNext(  ) )
        {
            ITask task = iterator.next(  );

            String strTaskinformation = _taskComponentManager.getDisplayTaskInformation( resourceHistory.getId(  ),
                    request, request.getLocale(  ), task );

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

        return URLDecoder.decode( request.getParameter( PARAMETER_RETURN_URL ), PARAMETER_ENCODING );
    }

    /**
     * Can delete upload.
     *
     * @param request the request
     * @return true, if successful
     */
    private boolean canDeleteUpload( HttpServletRequest request )
    {
        String strIdHistory = request.getParameter( PARAMETER_ID_HISTORY );
        int nIdHistory = WorkflowUtils.convertStringToInt( strIdHistory );
        String strIdTask = request.getParameter( PARAMETER_ID_TASK );
        int nIdTask = WorkflowUtils.convertStringToInt( strIdTask );
        AdminUser userConnected = AdminUserService.getAdminUser( request );

        UploadHistory uploadValue = FactoryService.getHistoryService(  )
                                                  .findByPrimaryKey( nIdHistory, nIdTask, WorkflowUtils.getPlugin(  ) );

        boolean bHasPermissionDeletion = RBACService.isAuthorized( uploadValue,
                UploadResourceIdService.PERMISSION_DELETE, userConnected );
        boolean bIsOwner = FactoryService.getHistoryService(  ).isOwner( nIdHistory, userConnected );

        return bHasPermissionDeletion || bIsOwner;
    }
}
