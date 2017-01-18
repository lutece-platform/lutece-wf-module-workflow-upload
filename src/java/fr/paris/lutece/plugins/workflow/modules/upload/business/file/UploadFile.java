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

import fr.paris.lutece.portal.business.file.File;

/**
 * The Class UploadFile.
 */
public class UploadFile
{
    // Variables declarations
    private int _nIdUploadFile;
    private int _nIdHistory;
    private int _nIdFile;
    private File _file;

    /**
     * Gets the file.
     *
     * @return the file
     */
    public File getFile( )
    {
        return _file;
    }

    /**
     * Sets the file.
     *
     * @param file
     *            the new file
     */
    public void setFile( File file )
    {
        this._file = file;
    }

    /**
     * Returns the IdUploadFile
     * 
     * @return The IdUploadFile
     */
    public int getIdUploadFile( )
    {
        return _nIdUploadFile;
    }

    /**
     * Sets the IdUploadFile
     * 
     * @param nIdUploadFile
     *            The IdUploadFile
     */
    public void setIdUploadFile( int nIdUploadFile )
    {
        _nIdUploadFile = nIdUploadFile;
    }

    /**
     * Returns the IdHistory
     * 
     * @return The IdHistory
     */
    public int getIdHistory( )
    {
        return _nIdHistory;
    }

    /**
     * Sets the IdHistory
     * 
     * @param nIdHistory
     *            The IdHistory
     */
    public void setIdHistory( int nIdHistory )
    {
        _nIdHistory = nIdHistory;
    }

    /**
     * Returns the IdFile
     * 
     * @return The IdFile
     */
    public int getIdFile( )
    {
        return _nIdFile;
    }

    /**
     * Sets the IdFile
     * 
     * @param nIdFile
     *            The IdFile
     */
    public void setIdFile( int nIdFile )
    {
        _nIdFile = nIdFile;
    }
}
