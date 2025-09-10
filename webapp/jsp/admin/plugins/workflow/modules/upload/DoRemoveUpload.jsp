<%@ page errorPage="../../ErrorPage.jsp" %>
  
${ pageContext.response.sendRedirect( uploadJspBean.doRemoveUpload( pageContext.request ) ) }