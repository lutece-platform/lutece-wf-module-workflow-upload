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
import fr.paris.lutece.plugins.workflow.modules.upload.business.task.TaskUploadConfig;
import fr.paris.lutece.plugins.workflow.modules.upload.services.IUploadHistoryService;
import fr.paris.lutece.plugins.workflow.modules.upload.services.TaskUploadAsynchronousUploadHandler;
import fr.paris.lutece.plugins.workflow.modules.upload.services.UploadResourceIdService;
import fr.paris.lutece.plugins.workflow.modules.upload.services.download.DownloadFileService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.AbstractTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.upload.MultipartItem;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.cdi.mvc.Models;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * UploadTaskComponent
 *
 */
@ApplicationScoped
@Named( "workflow-upload.uploadTaskComponent" )
public class UploadTaskComponent extends AbstractTaskComponent
{
    // TEMPLATES
    private static final String TEMPLATE_TASK_UPLOAD_CONFIG = "admin/plugins/workflow/modules/upload/task_upload_config.html";
    private static final String TEMPLATE_TASK_UPLOAD_FORM = "admin/plugins/workflow/modules/upload/task_upload_form.html";
    private static final String TEMPLATE_TASK_UPLOAD_INFORMATION = "admin/plugins/workflow/modules/upload/task_upload_information.html";

    // MARKS
    private static final String MARK_ID_HISTORY = "id_history";
    private static final String MARK_TASK = "task";
    private static final String MARK_CONFIG = "config";
    private static final String MARK_LIST_URL = "list_url";
    private static final String MARK_HANDLER = "handler";
    private static final String MARK_FILE_NAME = "upload_value";
    private static final String MARK_LIST_FILE = "list_file_uploaded";
    private static final String MARK_LIST_UPLOADED_FILE = "listUploadedFiles";
    private static final String MARK_HAS_PERMISSION_DELETE = "has_permission_delete";
    private static final String MARK_IS_OWNER = "is_owner";

    // PARAMETERS
    private static final String PARAMETER_UPLOAD_VALUE = "upload_value";

    // MESSAGES
    private static final String MESSAGE_VALIDATION_CONFIG_TITLE_REQUIRED = "module.workflow.upload.validation.taskuploadconfig.Title.notEmpty";
    private static final String MESSAGE_VALIDATION_CONFIG_TITLE_SIZE = "module.workflow.upload.validation.taskuploadconfig.Title.size";
    private static final String MESSAGE_VALIDATION_CONFIG_SIZE_FILE = "module.workflow.upload.validation.taskuploadconfig.MaxSizeFile.minValue";
    private static final String MESSAGE_VALIDATION_CONFIG_NUMBER_MAX_FILE = "module.workflow.upload.validation.taskuploadconfig.MaxFile.minValue";
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.upload.task_upload_config.message.mandatory.field";
    private static final String MESSAGE_NO_CONFIGURATION_FOR_TASK_UPLOAD = "module.workflow.upload.task_upload_config.message.no_configuration_for_task_upload";

    @Inject
    private IUploadHistoryService _uploadHistoryService;
    
    @Inject
    private IUploadFileDAO _uploadFileDAO;
    
    @Inject
    @Named( TaskUploadAsynchronousUploadHandler.BEAN_TASK_ASYNCHRONOUS_UPLOAD_HANDLER )
    private TaskUploadAsynchronousUploadHandler _taskUploadAsynchronousUploadHandler;
    
    @Inject
    public UploadTaskComponent( @Named( "workflow-upload.taskTypeUpload" ) ITaskType taskType, 
    		                    @Named( "workflow-upload.taskUploadConfigService" ) ITaskConfigService taskConfigService )
    {
        setTaskType( taskType );
        setTaskConfigService( taskConfigService );
    }
    
    @Inject
    private Models model;
    
    @Override
    public String validateConfig( ITaskConfig config, HttpServletRequest request )
    {
        String strTitle = request.getParameter( "title" );
        String strMaxSizeFile = request.getParameter( "maxSizeFile" );
        String strMaxFile = request.getParameter( "maxFile" );

        int nNumberSize = -1;
        int nNumberFile = -1;

        try
        {
            nNumberSize = Integer.valueOf( strMaxSizeFile );
            nNumberFile = Integer.valueOf( strMaxFile );
        }
        catch( Exception e )
        {
        }

        String strMessageError = StringUtils.EMPTY;

        if ( StringUtils.isBlank( strTitle ) )
        {
            strMessageError = MESSAGE_VALIDATION_CONFIG_TITLE_REQUIRED;
        }
        else
        {
            if ( strTitle.length( ) > 255 )
            {
                strMessageError = MESSAGE_VALIDATION_CONFIG_TITLE_SIZE;
            }
        }

        if ( nNumberSize <= 0 )
        {
            strMessageError = MESSAGE_VALIDATION_CONFIG_SIZE_FILE;
        }

        if ( nNumberFile <= 0 )
        {
            strMessageError = MESSAGE_VALIDATION_CONFIG_NUMBER_MAX_FILE;
        }

        if ( StringUtils.isNotBlank( strMessageError ) )
        {
            return AdminMessageService.getMessageUrl( request, strMessageError, AdminMessage.TYPE_STOP );
        }

        return strMessageError;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        String strUploadValue = PARAMETER_UPLOAD_VALUE + "_" + task.getId( );
        TaskUploadConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );

        if ( config == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_CONFIGURATION_FOR_TASK_UPLOAD, AdminMessage.TYPE_STOP );
        }

        List<MultipartItem> listFiles = _taskUploadAsynchronousUploadHandler.getListUploadedFiles( strUploadValue, request.getSession( ) );

        if ( config.isMandatory( ) && listFiles.isEmpty( ) )
        {
            Object [ ] tabRequiredFields = {
                config.getTitle( )
            };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields, AdminMessage.TYPE_STOP );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        TaskUploadConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        model.put( MARK_CONFIG, config );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_UPLOAD_CONFIG, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale, ITask task )
    {
        TaskUploadConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        String strUpload = PARAMETER_UPLOAD_VALUE + "_" + task.getId( );
        model.put( MARK_CONFIG, config );
        model.put( MARK_HANDLER, _taskUploadAsynchronousUploadHandler );
        model.put( MARK_FILE_NAME, strUpload );

        _taskUploadAsynchronousUploadHandler.removeSessionFiles( request.getSession( ).getId( ) );
        model.put( MARK_LIST_UPLOADED_FILE, new ArrayList<MultipartItem>( ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_UPLOAD_FORM, locale, model );

        return template.getHtml( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        UploadHistory uploadValue = _uploadHistoryService.findByPrimaryKey( nIdHistory, task.getId( ), WorkflowUtils.getPlugin( ) );

        TaskUploadConfig config = this.getTaskConfigService( ).findByPrimaryKey( task.getId( ) );
        AdminUser userConnected = AdminUserService.getAdminUser( request );

        List<UploadFile> listFile = _uploadFileDAO.load( nIdHistory, WorkflowUtils.getPlugin( ) );

        @SuppressWarnings( "deprecation" )
        String strBaseUrl = ( request != null ) ? AppPathService.getBaseUrl( request ) : AppPathService.getBaseUrl( );

        Map<String, Object> modelUrl = new HashMap<String, Object>( );

        for ( int i = 0; i < listFile.size( ); i++ )
        {
            modelUrl.put( Integer.toString( listFile.get( i ).getIdUploadFile( ) ),
                    DownloadFileService.getUrlDownloadFile( listFile.get( i ).getIdFile( ), strBaseUrl ) );
        }

        model.put( MARK_LIST_URL, modelUrl );
        model.put( MARK_ID_HISTORY, nIdHistory );
        model.put( MARK_TASK, task );
        model.put( MARK_CONFIG, config );
        model.put( MARK_LIST_FILE, listFile );
        model.put( MARK_HAS_PERMISSION_DELETE, RBACService.isAuthorized( uploadValue, UploadResourceIdService.PERMISSION_DELETE, (User)  userConnected ) );
        model.put( MARK_IS_OWNER, _uploadHistoryService.isOwner( nIdHistory, userConnected ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_UPLOAD_INFORMATION, locale, model );

        return template.getHtml( );
    }

}
