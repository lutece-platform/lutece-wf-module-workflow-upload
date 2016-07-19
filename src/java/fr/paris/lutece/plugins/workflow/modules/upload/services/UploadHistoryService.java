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

import java.util.List;

import fr.paris.lutece.plugins.workflow.modules.upload.business.file.UploadFile;
import fr.paris.lutece.plugins.workflow.modules.upload.business.history.IUploadHistoryDAO;
import fr.paris.lutece.plugins.workflow.modules.upload.business.history.UploadHistory;
import fr.paris.lutece.plugins.workflow.modules.upload.factory.FactoryDOA;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.filesystem.FileSystemUtil;

import org.apache.commons.fileupload.FileItem;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import fr.paris.lutece.util.sql.TransactionManager;

// TODO: Auto-generated Javadoc
/**
 * The Class UploadHistoryService.
 */
public class UploadHistoryService implements IUploadHistoryService
{
    /** The _dao. */
    private IUploadHistoryDAO _dao;

    /** The _resource history service. */
    @Inject
    private IResourceHistoryService _resourceHistoryService;

    /**
     * Gets the upload history dao.
     *
     * @return the upload history dao
     */
    private IUploadHistoryDAO getUploadHistoryDAO(  )
    {
        if ( _dao == null )
        {
            _dao = SpringContextService.getBean( IUploadHistoryDAO.BEAN_SERVICE );
        }

        return _dao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void create( int nIdResourceHistory, int nidTask, List<FileItem> listFiles, Plugin plugin )
    {
    	TransactionManager.beginTransaction( WorkflowUtils.getPlugin(  ) );
    	   for ( FileItem fileitem : listFiles )
           {
               File file = buildFileWithPhysicalFile( fileitem );
               int nidFile = FileHome.create( file );

               UploadFile uploadFile = new UploadFile(  );
               uploadFile.setIdFile( nidFile );
               uploadFile.setIdHistory( nIdResourceHistory );

               FactoryDOA.getUploadFileDAO(  ).insert( uploadFile, WorkflowUtils.getPlugin(  ) );
           }
    	   
    	   UploadHistory uploadValue = new UploadHistory(  );
           uploadValue.setIdResourceHistory( nIdResourceHistory );
           uploadValue.setIdTask( nidTask );
    	   
        getUploadHistoryDAO(  ).insert( uploadValue, plugin );
        
        TransactionManager.rollBack( WorkflowUtils.getPlugin(  ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void removeByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        getUploadHistoryDAO(  ).deleteByHistory( nIdHistory, nIdTask, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow.transactionManager" )
    public void removeByTask( int nIdTask, Plugin plugin )
    {
        getUploadHistoryDAO(  ).deleteByTask( nIdTask, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UploadHistory findByPrimaryKey( int nIdHistory, int nIdTask, Plugin plugin )
    {
        return getUploadHistoryDAO(  ).load( nIdHistory, nIdTask, plugin );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOwner( int nIdHistory, AdminUser adminUser )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdHistory );
        AdminUser userOwner = AdminUserHome.findUserByLogin( resourceHistory.getUserAccessCode(  ) );

        return userOwner.getUserId(  ) == adminUser.getUserId(  );
    }
    
    /**
     * Builds the file with physical file.
     *
     * @param fileItem the file item
     * @return the file
     */
    private File buildFileWithPhysicalFile( FileItem fileItem )
    {
        File file = new File(  );
        file.setTitle( fileItem.getName(  ) );
        file.setSize( ( fileItem.getSize(  ) < Integer.MAX_VALUE ) ? (int) fileItem.getSize(  ) : Integer.MAX_VALUE );
        file.setMimeType( FileSystemUtil.getMIMEType( file.getTitle(  ) ) );

        PhysicalFile physicalFile = new PhysicalFile(  );
        physicalFile.setValue( fileItem.get(  ) );
        file.setPhysicalFile( physicalFile );

        return file;
    }
}
