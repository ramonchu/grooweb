<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<!--[if lt IE 7]>      <html class="no-js lt-ie9 lt-ie8 lt-ie7"> <![endif]-->
<!--[if IE 7]>         <html class="no-js lt-ie9 lt-ie8"> <![endif]-->
<!--[if IE 8]>         <html class="no-js lt-ie9"> <![endif]-->
<!--[if gt IE 8]><!-->
<html class="no-js">
<!--<![endif]-->
<head>
	<title>Grooweb</title>
	<meta name="description" content="">
	<jsp:include page="include/metas.jsp"/>
	<jsp:include page="include/assets.jsp"/>
</head>
<body>
	<div class="container">
		<div class="row">
			<div class="span12">
				<h1><a href="/index.html">Grooweb</a></h1>
			
				<p>Messages</p>
			
				<p>es: <c:out value="${messageEs}"/></p>
				<p>en: <c:out value="${messageEn}"/> ${messageEn}</p>
				<p>default: <c:out value="${messageDefault}"/></p>
				
			</div>
		</div>

		<hr>
		<jsp:include page="include/footer.jsp"/>
	</div>
	<jsp:include page="include/footerScripts.jsp"/>
</body>
</html>
