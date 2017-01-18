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
package fr.paris.lutece.plugins.workflow.modules.upload.services;

import fr.paris.lutece.plugins.workflow.modules.upload.business.task.TaskUploadConfig;
import fr.paris.lutece.plugins.workflow.modules.upload.factory.FactoryService;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.Task;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;

/**
 * The Class TaskUpload.
 */
public class TaskUpload extends Task
{
    /** The Constant BEAN_UPLOAD_CONFIG_SERVICE. */
    public static final String BEAN_UPLOAD_CONFIG_SERVICE = "workflow-upload.taskUploadConfigService";
    private static final String PARAMETER_UPLOAD_VALUE = "upload_value";
    @Inject
    @Named( BEAN_UPLOAD_CONFIG_SERVICE )
    private ITaskConfigService _taskUploadConfigService;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init( )
    {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        String strUploadValue = PARAMETER_UPLOAD_VALUE + "_" + this.getId( );

        List<FileItem> listFiles = TaskUploadAsynchronousUploadHandler.getHandler( ).getListUploadedFiles( strUploadValue, request.getSession( ) );

        if ( !listFiles.isEmpty( ) )
        {
            FactoryService.getHistoryService( ).create( nIdResourceHistory, this.getId( ), listFiles, WorkflowUtils.getPlugin( ) );
        }

        TaskUploadAsynchronousUploadHandler.getHandler( ).removeSessionFiles( request.getSession( ).getId( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveConfig( )
    {
        _taskUploadConfigService.remove( this.getId( ) );
        FactoryService.getHistoryService( ).removeByTask( this.getId( ), WorkflowUtils.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doRemoveTaskInformation( int nIdHistory )
    {
        FactoryService.getHistoryService( ).removeByHistory( nIdHistory, this.getId( ), WorkflowUtils.getPlugin( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        TaskUploadConfig config = _taskUploadConfigService.findByPrimaryKey( this.getId( ) );

        if ( config != null )
        {
            return config.getTitle( );
        }

        return StringUtils.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getTaskFormEntries( Locale locale )
    {
        Map<String, String> mapEntriesForm = null;
        TaskUploadConfig config = _taskUploadConfigService.findByPrimaryKey( this.getId( ) );

        if ( config != null )
        {
            mapEntriesForm = new HashMap<String, String>( );
            mapEntriesForm.put( PARAMETER_UPLOAD_VALUE + "_" + this.getId( ), config.getTitle( ) );
        }

        return mapEntriesForm;
    }
}
