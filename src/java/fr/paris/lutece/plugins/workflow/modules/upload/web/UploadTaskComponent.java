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
import fr.paris.lutece.plugins.workflow.modules.upload.business.task.TaskUploadConfig;
import fr.paris.lutece.plugins.workflow.modules.upload.factory.FactoryDOA;
import fr.paris.lutece.plugins.workflow.modules.upload.factory.FactoryService;
import fr.paris.lutece.plugins.workflow.modules.upload.services.TaskUploadAsynchronousUploadHandler;
import fr.paris.lutece.plugins.workflow.modules.upload.services.UploadResourceIdService;
import fr.paris.lutece.plugins.workflow.modules.upload.services.download.DownloadFileService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.AbstractTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.rbac.RBACService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.fileupload.FileItem;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * UploadTaskComponent
 *
 */
public class UploadTaskComponent extends AbstractTaskComponent
{
    // XML
    private static final String TAG_UPLOAD = "upload";

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
    private static final String MESSAGE_MANDATORY_FIELD = "module.workflow.upload.task_upload_config.message.mandatory.field";
    private static final String MESSAGE_NO_CONFIGURATION_FOR_TASK_UPLOAD = "module.workflow.upload.task_upload_config.message.no_configuration_for_task_upload";

    /**
     * {@inheritDoc}
     */
    @Override
    public String doValidateTask( int nIdResource, String strResourceType, HttpServletRequest request, Locale locale,
        ITask task )
    {
        String strUploadValue = PARAMETER_UPLOAD_VALUE + "_" + task.getId(  );
        TaskUploadConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );

        if ( config == null )
        {
            return AdminMessageService.getMessageUrl( request, MESSAGE_NO_CONFIGURATION_FOR_TASK_UPLOAD,
                AdminMessage.TYPE_STOP );
        }

        List<FileItem> listFiles = TaskUploadAsynchronousUploadHandler.getHandler(  )
                                                                      .getListUploadedFiles( strUploadValue,
                request.getSession(  ) );

        if ( config.isMandatory(  ) && listFiles.isEmpty(  ) )
        {
            Object[] tabRequiredFields = { config.getTitle(  ) };

            return AdminMessageService.getMessageUrl( request, MESSAGE_MANDATORY_FIELD, tabRequiredFields,
                AdminMessage.TYPE_STOP );
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        TaskUploadConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );
        model.put( MARK_CONFIG, config );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_UPLOAD_CONFIG, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskForm( int nIdResource, String strResourceType, HttpServletRequest request,
        Locale locale, ITask task )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        TaskUploadConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );
        String strUpload = PARAMETER_UPLOAD_VALUE + "_" + task.getId(  );
        model.put( MARK_CONFIG, config );
        model.put( MARK_HANDLER, TaskUploadAsynchronousUploadHandler.getHandler(  ) );
        model.put( MARK_FILE_NAME, strUpload );

        model.put( MARK_LIST_UPLOADED_FILE,
            TaskUploadAsynchronousUploadHandler.getHandler(  )
                                               .getListUploadedFiles( PARAMETER_UPLOAD_VALUE + "_" + task.getId(  ),
                request.getSession(  ) ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_UPLOAD_FORM, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        UploadHistory uploadValue = FactoryService.getHistoryService(  )
                                                  .findByPrimaryKey( nIdHistory, task.getId(  ),
                WorkflowUtils.getPlugin(  ) );

        Map<String, Object> model = new HashMap<String, Object>(  );
        TaskUploadConfig config = this.getTaskConfigService(  ).findByPrimaryKey( task.getId(  ) );
        AdminUser userConnected = AdminUserService.getAdminUser( request );

        List<UploadFile> listFile = FactoryDOA.getUploadFileDAO(  ).load( nIdHistory, WorkflowUtils.getPlugin(  ) );

        @SuppressWarnings( "deprecation" )
        String strBaseUrl = ( request != null ) ? AppPathService.getBaseUrl( request ) : AppPathService.getBaseUrl(  );

        Map<String, Object> modelUrl = new HashMap<String, Object>(  );

        for ( int i = 0; i < listFile.size(  ); i++ )
        {
            modelUrl.put( Integer.toString( listFile.get( i ).getIdUploadFile(  ) ),
                DownloadFileService.getUrlDownloadFile( listFile.get( i ).getIdFile(  ), strBaseUrl ) );
        }

        model.put( MARK_LIST_URL, modelUrl );
        model.put( MARK_ID_HISTORY, nIdHistory );
        model.put( MARK_TASK, task );
        model.put( MARK_CONFIG, config );
        model.put( MARK_LIST_FILE, listFile );
        model.put( MARK_HAS_PERMISSION_DELETE,
            RBACService.isAuthorized( uploadValue, UploadResourceIdService.PERMISSION_DELETE, userConnected ) );
        model.put( MARK_IS_OWNER, FactoryService.getHistoryService(  ).isOwner( nIdHistory, userConnected ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_UPLOAD_INFORMATION, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        StringBuffer strXml = new StringBuffer(  );
        UploadHistory uploadValue = FactoryService.getHistoryService(  )
                                                  .findByPrimaryKey( nIdHistory, task.getId(  ),
                WorkflowUtils.getPlugin(  ) );

        if ( uploadValue != null )
        {
            XmlUtil.addElementHtml( strXml, TAG_UPLOAD, "dddd" );
        }
        else
        {
            XmlUtil.addEmptyElement( strXml, TAG_UPLOAD, null );
        }

        return strXml.toString(  );
    }
}
