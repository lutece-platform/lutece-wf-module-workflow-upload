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

import fr.paris.lutece.plugins.workflow.modules.upload.business.file.UploadFile;
import fr.paris.lutece.plugins.workflow.modules.upload.factory.FactoryDOA;
import fr.paris.lutece.plugins.workflow.modules.upload.services.download.DownloadFileService;
import fr.paris.lutece.plugins.workflow.service.taskinfo.AbstractTaskInfoProvider;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.portal.service.util.AppPathService;
import java.util.List;


import javax.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * UploadTaskInfoProvider
 *
 */
public class UploadTaskInfoProvider extends AbstractTaskInfoProvider
{
    private static final String KEY_URL_LIST = "url_list";
    private static final String KEY_FILE_NAME = "file_name";
    private static final String KEY_FILE_URL = "file_url";

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
        JSONObject jsonInfos = new JSONObject( );

        List<UploadFile> uploadFileList = FactoryDOA.getUploadFileDAO( ).load( nIdHistory, WorkflowUtils.getPlugin( ) );

        JSONArray jsonUrlList = new JSONArray( );
        if ( uploadFileList != null )
        {
            for (UploadFile uploadFile : uploadFileList )
            {
                String strDownloadUrl = DownloadFileService.getUrlDownloadFile( uploadFile.getIdFile( ), AppPathService.getBaseUrl( request ) ) ;
                JSONObject fileItem = new JSONObject( );
                fileItem.accumulate(KEY_FILE_NAME, uploadFile.getFile( ).getTitle( ) );
                fileItem.accumulate(KEY_FILE_URL, strDownloadUrl );
                
                jsonUrlList.add( fileItem );
            }
        }
        
        jsonInfos.accumulate( KEY_URL_LIST, jsonUrlList );
        
        return jsonInfos.toString( );
    }
}
