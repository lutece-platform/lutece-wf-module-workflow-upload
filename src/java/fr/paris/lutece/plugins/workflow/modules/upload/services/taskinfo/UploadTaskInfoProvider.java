/*
 * Copyright (c) 2002-2019, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.upload.services.taskinfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.paris.lutece.plugins.workflow.modules.upload.business.file.IUploadFileDAO;
import fr.paris.lutece.plugins.workflow.modules.upload.business.file.UploadFile;
import fr.paris.lutece.plugins.workflow.modules.upload.services.download.DownloadFileService;
import fr.paris.lutece.plugins.workflow.service.taskinfo.AbstractTaskInfoProvider;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import fr.paris.lutece.portal.service.util.AppPathService;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;

/**
 *
 * UploadTaskInfoProvider
 *
 */
@ApplicationScoped
@Named( "workflow-upload.uploadTaskInfoProvider" )
public class UploadTaskInfoProvider extends AbstractTaskInfoProvider
{
    private static final String KEY_URL_LIST = "url_list";
    private static final String KEY_FILE_NAME = "file_name";
    private static final String KEY_FILE_URL = "file_url";

    @Inject
    private IUploadFileDAO _uploadFileDAO;
    
    @Inject
    public UploadTaskInfoProvider( @Named( "workflow-upload.taskTypeUpload" ) ITaskType taskType )
    {
        setTaskType( taskType );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getPluginName( )
    {
        return WorkflowUtils.getPlugin( ).getName( );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskResourceInfo( int nIdHistory, int nIdTask, HttpServletRequest request )
    {

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonInfos = mapper.createObjectNode();
        ArrayNode jsonUrlList = mapper.createArrayNode();

        List<UploadFile> uploadFileList = _uploadFileDAO.load( nIdHistory, WorkflowUtils.getPlugin( ) );

        if ( uploadFileList != null )
        {
            for (UploadFile uploadFile : uploadFileList )
            {
                String strDownloadUrl = DownloadFileService.getUrlDownloadFile( uploadFile.getIdFile( ), AppPathService.getProdUrl( request ) ) ;
                
                ObjectNode fileItem = mapper.createObjectNode();
                fileItem.put(KEY_FILE_NAME, uploadFile.getFile().getTitle());
                fileItem.put(KEY_FILE_URL, strDownloadUrl);
                jsonUrlList.add(fileItem);
            }
        }
        
        jsonInfos.set(KEY_URL_LIST, jsonUrlList);
        
        return jsonInfos.toString();
    }
}
