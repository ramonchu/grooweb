<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
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
				<h1>Welcome to Grooweb</h1>
				<p style="font-weight: bold;">The simple dinamic JVM web
					framework, based on groovy.</p>

				<h3>Description</h3>
				<p>This framework was designed with no tomcat/jetty restart in
					mind. Taking features of Spring Framework MVC, the controller layer
					can change in runtime. You can add/remove/modify methods that
					response the web request without stop and start the application
					server thanks to reloading function of groovy class loader. But, we
					don't want loss the compiler help. For this reason, the reloading
					is only availiable in presetantion layer and not in service layer
					provider by CDI.</p>

				<h2>Includes</h2>
				<ul>
					<li>Html 5 boilerplate</li>
					<li>Twitter bootstrap</li>
					<li>Jquery</li>
					<li>Groovy controllers</li>
					<li>Traditional JSP views</li>
					<li>Forms validators with JSR 303 (Hibernate)</li>
					<li>Dependency injection (CDI) with Guice.
				</ul>

				<h2>Samples</h2>

				<ul>
					<li><a href="/prueba.html">Default simply get action</a></li>
					<li><a href="/parameters.html?id=1&from=2012-01-01&to=2012-12-31">Simple get action, with parameters</a></li>
					<li><a href="/prueba.json">JSON Response</a></li>
					<li><a href="/redirect.html">Redirect to prueba.html, from redirect.html</a></li>
					<li><a href="/form.html">JSR 303 Form validation</a></li>
					<li><a href="/groovyForm.html">Groovy Forms with validation</a></li>
					<li><a href="/i18n.html">i18N Messages</a></li>
					<li><a href="/notAllowed.html">Role based security</a> (require ADMIN role)</li>
					<li><a href="/xssAvoided.html">Cross Site Scripting avoided</a></li>
					<li><a href="/jadeTemplate.html">Jade Template Engine sample</a></li>
				</ul>
				
				<h3>To Be Done</h3>
				<ul>
					<li><a href="#">Custom converter</a></li>
					<li><a href="#">Custom validator</a></li>
					
					<li><a href="#">Html helpers</a></li>
					<li><a href="#">More complex expressions with OGNL</a></li>
				</ul>

			</div>
		</div>

		<hr>
		<jsp:include page="include/footer.jsp"/>
	</div>
	<jsp:include page="include/footerScripts.jsp"/>
</body>
</html>