/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.upload.services.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.paris.lutece.plugins.workflow.service.taskinfo.ITaskInfoProvider;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.provider.IMarkerProvider;
import fr.paris.lutece.plugins.workflowcore.service.provider.InfoMarker;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

import javax.inject.Named;

/**
 * This class represents a NotifyGru marker provider for the Edit record task
 *
 */
public class UploadMarkerProvider implements IMarkerProvider
{
    private static final String ID = "workflow-upload.taskUploadMarkerProvider";

    // Messages
    private static final String MESSAGE_TITLE_KEY = "module.workflow.upload.marker.provider.taskupload.title";
    private static final String MESSAGE_MARKER_URLS_DESCRIPTION_KEY = "module.workflow.upload.marker.provider.taskupload.urls.description";

    // Markers & keys
    private static final String MARK_UPLOAD_URLS = "url_list";
    
    private static final String KEY_FILE_NAME = "file_name";
    private static final String KEY_FILE_URL = "file_url";
    

    // Services
    @Inject
    private ITaskService _taskService;

    @Inject
    @Named(value="workflow-upload.uploadTaskInfoProvider")
    private ITaskInfoProvider _uploadTaskInfoProvider ;
    //private ITaskInfoProvider _uploadTaskInfoProvider = SpringContextService.getBean( "workflow-upload.uploadMarkerProvider" );

    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getId( )
    {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitleI18nKey( )
    {
        return MESSAGE_TITLE_KEY ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<InfoMarker> provideMarkerDescriptions( )
    {
        List<InfoMarker> listMarkers = new ArrayList<>( );

        InfoMarker notifyGruMarkerMsg = new InfoMarker( MARK_UPLOAD_URLS );
        notifyGruMarkerMsg.setDescription( MESSAGE_MARKER_URLS_DESCRIPTION_KEY );
        listMarkers.add( notifyGruMarkerMsg );

        return listMarkers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<InfoMarker> provideMarkerValues( ResourceHistory resourceHistory, ITask task, HttpServletRequest request )
    {
        List<InfoMarker> listMarkers = new ArrayList<>( );
        
        for ( ITask taskOther : _taskService.getListTaskByIdAction( resourceHistory.getAction( ).getId( ), request.getLocale( ) ) )
        {
            if ( taskOther.getTaskType( ).getKey( ).equals( _uploadTaskInfoProvider.getTaskType( ).getKey( ) ) )
            {
                String strJsonInfos = _uploadTaskInfoProvider.getTaskResourceInfo( resourceHistory.getId( ), taskOther.getId( ), request ) ;
                StringBuilder strMsg = new StringBuilder( "<ul class='uploadTaskList'>" );

                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode jsonUrlList = mapper.readTree(strJsonInfos).get(MARK_UPLOAD_URLS);

                    if (jsonUrlList != null )
                    {
                        for ( JsonNode fileItem : jsonUrlList )
                        {
                            String fileUrl = fileItem.get(KEY_FILE_URL).asText();
                            String fileName = fileItem.get(KEY_FILE_NAME).asText();

                            strMsg.append("<li><a href='").append(fileUrl).append("'>").append(fileName).append("</a></li>");
                        }
                    }
                }
                catch (JsonProcessingException e)
                {
                    AppLogService.error("JSON parsing failed", e);
                }
                strMsg.append( "</ul>" );

                InfoMarker notifyMarkerMsg = new InfoMarker( MARK_UPLOAD_URLS );
                notifyMarkerMsg.setValue( strMsg.toString( ) );
                listMarkers.add( notifyMarkerMsg );

                break;
            }
        }

        return listMarkers;
    }

}

