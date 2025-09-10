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
package fr.paris.lutece.plugins.workflow.modules.upload.business.history;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;
import jakarta.enterprise.context.ApplicationScoped;

// TODO: Auto-generated Javadoc
/**
 * The Class UploadHistoryDAO.
 */
@ApplicationScoped
public class UploadHistoryDAO implements IUploadHistoryDAO
{
    /** The Constant SQL_QUERY_FIND_BY_PRIMARY_KEY. */
    private static final String SQL_QUERY_FIND_BY_PRIMARY_KEY = "SELECT id_history,id_task  "
            + "FROM workflow_task_upload_history WHERE id_history=? AND id_task=?";

    /** The Constant SQL_QUERY_INSERT. */
    private static final String SQL_QUERY_INSERT = "INSERT INTO  workflow_task_upload_history " + "(id_history,id_task )VALUES(?,?)";

    /** The Constant SQL_QUERY_DELETE_BY_HISTORY. */
    private static final String SQL_QUERY_DELETE_BY_HISTORY = "DELETE FROM workflow_task_upload_history  WHERE id_history=? AND id_task=?";

    /** The Constant SQL_QUERY_DELETE_BY_TASK. */
    private static final String SQL_QUERY_DELETE_BY_TASK = "DELETE FROM workflow_task_upload_history  WHERE id_task=?";

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert( UploadHistory history, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin ) )
        {
            int nPos = 0;

            daoUtil.setInt( ++nPos, history.getIdResourceHistory( ) );
            daoUtil.setInt( ++nPos, history.getIdTask( ) );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UploadHistory load( int nIdHistory, int nIdTask, Plugin plugin )
    {
        UploadHistory uploadHistory = null;

        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_FIND_BY_PRIMARY_KEY, plugin ) )
        {
        	int nPos = 0;
            daoUtil.setInt( ++nPos, nIdHistory );
            daoUtil.setInt( ++nPos, nIdTask );

            nPos = 0;

            daoUtil.executeQuery( );

            if ( daoUtil.next( ) )
            {
                uploadHistory = new UploadHistory( );
                uploadHistory.setIdResourceHistory( daoUtil.getInt( ++nPos ) );
                uploadHistory.setIdTask( daoUtil.getInt( ++nPos ) );
            }
        }

        return uploadHistory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByHistory( int nIdHistory, int nIdTask, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_HISTORY, plugin ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, nIdHistory );
            daoUtil.setInt( ++nPos, nIdTask );

            daoUtil.executeUpdate( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteByTask( int nIdTask, Plugin plugin )
    {
        try( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE_BY_TASK, plugin ) )
        {
            int nPos = 0;
            daoUtil.setInt( ++nPos, nIdTask );
            
            daoUtil.executeUpdate( );
        }
    }
}
