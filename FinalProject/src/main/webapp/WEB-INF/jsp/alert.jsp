<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib uri="http://www.springframework.org/security/tags"
	prefix="security"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<script type="text/javascript">
	var msg = "<c:out value='${msg}'/>";
	var url = "<c:out value='${url}'/>";
	alert(msg);
	location.href = url;
</script>
