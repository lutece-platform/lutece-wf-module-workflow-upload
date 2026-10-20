/*
 * Copyright (c) 2002-2026, City of Paris
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.paris.lutece.portal.service.html.OwaspXSSSanitizer;

/**
 * The HTML list of the notification marker url_list (LUT-33153): the file names, given by the users who upload the files,
 * and the URLs are escaped; the list keeps its links once sanitized by richText(), the way a notification writes it.
 */
public class UploadMarkerProviderTest
{
    private static final String HOSTILE_NAME = "report <b>x</b> \"q\" 'a' & <script>alert(1)</script>.pdf";
    private static final String HOSTILE_URL = "http://localhost:8080/lutece/servlet/plugins/workflow/upload/download?id_file=31&signature=a\"b'c<d>&timestamp=1";
    private static final String URL = "http://localhost:8080/lutece/servlet/plugins/workflow/upload/download?id_file=32&signature=abc&timestamp=1";

    private static String json( String... namesAndUrls )
    {
        ObjectMapper mapper = new ObjectMapper( );
        ObjectNode jsonInfos = mapper.createObjectNode( );
        ArrayNode jsonUrlList = jsonInfos.putArray( "url_list" );
        for ( int i = 0; i < namesAndUrls.length; i += 2 )
        {
            ObjectNode fileItem = jsonUrlList.addObject( );
            fileItem.put( "file_name", namesAndUrls [i] );
            fileItem.put( "file_url", namesAndUrls [i + 1] );
        }

        return jsonInfos.toString( );
    }

    /**
     * A hostile file name and URL are written as text: no tag and no attribute come from them.
     */
    @Test
    public void testHostileFileNameIsEscaped( )
    {
        String strHtml = UploadMarkerProvider.buildUrlListHtml( json( HOSTILE_NAME, HOSTILE_URL, "photo.png", URL ) );

        assertEquals( "<ul class=\"uploadTaskList\">"
                + "<li><a href=\"http://localhost:8080/lutece/servlet/plugins/workflow/upload/download?id_file=31&amp;signature=a&quot;b'c&lt;d&gt;&amp;timestamp=1\">"
                + "report &lt;b&gt;x&lt;/b&gt; &quot;q&quot; 'a' &amp; &lt;script&gt;alert(1)&lt;/script&gt;.pdf</a></li>"
                + "<li><a href=\"http://localhost:8080/lutece/servlet/plugins/workflow/upload/download?id_file=32&amp;signature=abc&amp;timestamp=1\">photo.png</a></li>"
                + "</ul>", strHtml );
    }

    /**
     * A JSON without file, or that cannot be read, gives an empty list.
     */
    @Test
    public void testNoFile( )
    {
        assertEquals( "<ul class=\"uploadTaskList\"></ul>", UploadMarkerProvider.buildUrlListHtml( json( ) ) );
        assertEquals( "<ul class=\"uploadTaskList\"></ul>", UploadMarkerProvider.buildUrlListHtml( "{}" ) );
        assertEquals( "<ul class=\"uploadTaskList\"></ul>", UploadMarkerProvider.buildUrlListHtml( "not json" ) );
        assertEquals( "<ul class=\"uploadTaskList\"></ul>", UploadMarkerProvider.buildUrlListHtml( null ) );
    }

    /**
     * Written ${richText( url_list! )} in a notification message, the list is sanitized by the core sanitizer: the links
     * are kept, the hostile file name stays text.
     */
    @Test
    public void testListKeepsItsLinksOnceSanitized( )
    {
        OwaspXSSSanitizer sanitizer = new OwaspXSSSanitizer( );
        sanitizer.init( );

        String strSanitized = sanitizer.sanitize( UploadMarkerProvider.buildUrlListHtml( json( HOSTILE_NAME, URL ) ) );

        assertTrue( strSanitized.contains( "<ul>" ) || strSanitized.contains( "<ul " ), strSanitized );
        assertTrue( strSanitized.contains( "<li><a href=\"" ), strSanitized );
        assertTrue( strSanitized.contains( "id_file&#61;32" ) || strSanitized.contains( "id_file=32" ), strSanitized );
        assertTrue( strSanitized.contains( "&lt;script&gt;" ), strSanitized );
        assertFalse( strSanitized.contains( "<script" ), strSanitized );
        assertFalse( strSanitized.contains( "<b>" ), strSanitized );
    }
}
