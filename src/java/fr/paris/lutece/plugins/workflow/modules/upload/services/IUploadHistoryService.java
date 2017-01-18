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

import fr.paris.lutece.plugins.workflow.modules.upload.business.history.UploadHistory;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.plugin.Plugin;

import org.apache.commons.fileupload.FileItem;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Interface IUploadHistoryService.
 */
public interface IUploadHistoryService
{
    /** The Constant BEAN_SERVICE. */
    String BEAN_SERVICE = "workflow-upload.uploadValueService";

    /**
     * Creates the.
     *
     * @param nIdResourceHistory
     *            the n id resource history
     * @param nidTask
     *            the nid task
     * @param listFiles
     *            the list files
     * @param plugin
     *            the plugin
     */
    @Transactional( "workflow.transactionManager" )
    void create( int nIdResourceHistory, int nidTask, List<FileItem> listFiles, Plugin plugin );

    /**
     * Removes the by history.
     *
     * @param nIdHistory
     *            the n id history
     * @param nIdTask
     *            the n id task
     * @param plugin
     *            the plugin
     */
    @Transactional( "workflow.transactionManager" )
    void removeByHistory( int nIdHistory, int nIdTask, Plugin plugin );

    /**
     * Removes the by task.
     *
     * @param nIdTask
     *            the n id task
     * @param plugin
     *            the plugin
     */
    @Transactional( "workflow.transactionManager" )
    void removeByTask( int nIdTask, Plugin plugin );

    /**
     * Find by primary key.
     *
     * @param nIdHistory
     *            the n id history
     * @param nIdTask
     *            the n id task
     * @param plugin
     *            the plugin
     * @return the upload history
     */
    UploadHistory findByPrimaryKey( int nIdHistory, int nIdTask, Plugin plugin );

    /**
     * Checks if is owner.
     *
     * @param nIdHistory
     *            the n id history
     * @param adminUser
     *            the admin user
     * @return true, if is owner
     */
    boolean isOwner( int nIdHistory, AdminUser adminUser );
}
