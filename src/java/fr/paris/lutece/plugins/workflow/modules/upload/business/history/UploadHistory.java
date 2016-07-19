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

import fr.paris.lutece.portal.service.rbac.RBACResource;


// TODO: Auto-generated Javadoc
/**
 * The Class UploadHistory.
 */
public class UploadHistory implements RBACResource
{
    /** The Constant UPLOAD_RESOURCE_TYPE. */
    public static final String UPLOAD_RESOURCE_TYPE = "UPLOAD_WORKFLOW_HISTORY";

    /** The Constant SEPARATOR. */
    private static final String SEPARATOR = "-";

    /** The _n id resource history. */
    private int _nIdResourceHistory;

    /** The _n id task. */
    private int _nIdTask;

    /**
     * Gets the id resource history.
     *
     * @return the id resource history
     */
    public int getIdResourceHistory(  )
    {
        return _nIdResourceHistory;
    }

    /**
     * Sets the id resource history.
     *
     * @param id the new id resource history
     */
    public void setIdResourceHistory( int id )
    {
        _nIdResourceHistory = id;
    }

    /**
     * Gets the id task.
     *
     * @return the id task
     */
    public int getIdTask(  )
    {
        return _nIdTask;
    }

    /**
     * Sets the id task.
     *
     * @param idTask the new id task
     */
    public void setIdTask( int idTask )
    {
        _nIdTask = idTask;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.portal.service.rbac.RBACResource#getResourceId()
     */
    @Override
    public String getResourceId(  )
    {
        StringBuilder sb = new StringBuilder( _nIdResourceHistory );
        sb.append( SEPARATOR ).append( _nIdTask );

        return sb.toString(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.portal.service.rbac.RBACResource#getResourceTypeCode()
     */
    @Override
    public String getResourceTypeCode(  )
    {
        return UPLOAD_RESOURCE_TYPE;
    }
}
