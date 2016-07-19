<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:useBean id="manageUpload" scope="session" class="fr.paris.lutece.plugins.workflow.modules.upload.web.UploadJspBean" />
<%  
	response.sendRedirect( manageUpload.doRemoveUpload(request) );
%>
