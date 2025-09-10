<%@ page errorPage="../../ErrorPage.jsp" %>
 
${ pageContext.response.sendRedirect( uploadJspBean.getConfirmRemoveUpload( pageContext.request ) ) }