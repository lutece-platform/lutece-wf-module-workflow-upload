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
package fr.paris.lutece.plugins.workflow.modules.upload.business.task;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;


/**
 * The Class TaskUploadConfig.
 */
public class TaskUploadConfig extends TaskConfig
{
    /** The _n max file. */
    // Variables declarations 
    private int _nMaxFile;

    /** The _str title. */
    private String _strTitle;

    /** The _b mandatory. */
    private boolean _bMandatory;

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * Sets the title.
     *
     * @param strTitle the new title
     */
    public void setTitle( String strTitle )
    {
        this._strTitle = strTitle;
    }

    /**
     * Returns the MaxFile.
     *
     * @return The MaxFile
     */
    public int getMaxFile(  )
    {
        return _nMaxFile;
    }

    /**
     * Sets the MaxFile.
     *
     * @param nMaxFile The MaxFile
     */
    public void setMaxFile( int nMaxFile )
    {
        _nMaxFile = nMaxFile;
    }

    /**
     * Checks if is mandatory.
     *
     * @return true, if is mandatory
     */
    public boolean isMandatory(  )
    {
        return _bMandatory;
    }

    /**
     * Sets the mandatory.
     *
     * @param bMandatory the new mandatory
     */
    public void setMandatory( boolean bMandatory )
    {
        _bMandatory = bMandatory;
    }
}
