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
package fr.paris.lutece.plugins.workflow.modules.upload.business.file;

import fr.paris.lutece.portal.business.file.FileHome;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

import java.util.ArrayList;
import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Class UploadFileDAO.
 */
public class UploadFileDAO implements IUploadFileDAO
{
    /** The Constant SQL_QUERY_NEW_PK. */
    private static final String SQL_QUERY_NEW_PK = "SELECT max( id_upload_file ) FROM workflow_task_upload_files";

    /** The Constant SQL_QUERY_FIND_BY_HISTORY. */
    private static final String SQL_QUERY_FIND_BY_HISTORY = "SELECT id_upload_file,id_file,id_history  " +
        "FROM workflow_task_upload_files WHERE id_history=?";
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_upload_file,id_file,id_history  " +
        "FROM workflow_task_upload_files WHERE id_upload_file=?";

    /** The Constant SQL_QUERY_INSERT. */
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_task_upload_files " +
        "(id_upload_file,id_file,id_history)VALUES(?,?,?)";

    /** The Constant SQL_QUERY_DELETE_BY_HISTORY. */
    private static final String SQL_QUERY_DELETE_BY_HISTORY = "DELETE FROM workflow_task_upload_files  WHERE id_history=?";

    /** The Constant SQL_QUERY_DELETE_BY_FILE. */
    private static final String SQL_QUERY_DELETE_BY_ID = "DELETE FROM workflow_task_upload_files  WHERE id_upload_file=?";

    /**
     * Generates a new primary key.
     *
     * @param plugin The Plugin
     * @return The new primary key
     */
    public int newPrimaryKey( Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_NEW_PK, plugin );
        daoUtil.executeQuery(  );

        int nKey = 1;

        if ( daoUtil.next(  ) )
        {
            nKey = daoUtil.getInt( 1 ) + 1;
        }

        daoUtil.free(  );

        return nKey;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( UploadFile upload, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );

        upload.setIdUploadFile( newPrimaryKey( plugin ) );

        int nPos = 0;

        daoUtil.setInt( ++nPos, upload.getIdUploadFile(  ) );
        daoUtil.setInt( ++nPos, upload.getIdFile(  ) );
        daoUtil.setInt( ++nPos, upload.getIdHistory(  ) );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UploadFile> load( int nIdHistory, Plugin plugin )
    {
        UploadFile uploadUpload = null;

        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_HISTORY, plugin );
        int nPos = 0;
        daoUtil.setInt( ++nPos, nIdHistory );

        List<UploadFile> fileList = new ArrayList<UploadFile>(  );

        daoUtil.executeQuery(  );

        while ( daoUtil.next(  ) )
        {
            nPos = 0;
            uploadUpload = new UploadFile(  );
            uploadUpload.setIdUploadFile( daoUtil.getInt( ++nPos ) );
            uploadUpload.setIdFile( daoUtil.getInt( ++nPos ) );
            uploadUpload.setIdHistory( daoUtil.getInt( ++nPos ) );
            uploadUpload.setFile( FileHome.findByPrimaryKey( uploadUpload.getIdFile(  ) ) );

            if ( uploadUpload.getFile(  ) != null )
            {
                fileList.add( uploadUpload );
            }
        }

        daoUtil.free(  );

        return fileList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByHistory( int nIdHistory, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_HISTORY, plugin );
        int nPos = 0;
        daoUtil.setInt( ++nPos, nIdHistory );

        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByid( int nIdFileUpload, Plugin plugin )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_ID, plugin );
        int nPos = 0;
        daoUtil.setInt( ++nPos, nIdFileUpload );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    @Override
    public UploadFile findbyprimaryKey( int nIdFileUpload, Plugin plugin )
    {
        UploadFile uploadUpload = null;
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin );
        int nPos = 0;
        daoUtil.setInt( ++nPos, nIdFileUpload );

        daoUtil.executeQuery(  );

        nPos = 0;

        if ( daoUtil.next(  ) )
        {
            uploadUpload = new UploadFile(  );
            uploadUpload.setIdUploadFile( daoUtil.getInt( ++nPos ) );
            uploadUpload.setIdFile( daoUtil.getInt( ++nPos ) );
            uploadUpload.setIdHistory( daoUtil.getInt( ++nPos ) );
            uploadUpload.setFile( FileHome.findByPrimaryKey( uploadUpload.getIdFile(  ) ) );
        }

        daoUtil.free(  );

        return uploadUpload;
    }
}
