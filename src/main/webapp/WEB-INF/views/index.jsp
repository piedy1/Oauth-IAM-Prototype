<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css">
<!-- Latest compiled and minified JavaScript -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>
<style type="text/css">
.margin {
	margin-left: 3%;
}
</style>
<title>IAM-Prototype</title>
</head>
<body>
	<br />
	<h2 class="margin">OAuth client IAM-Protype</h2>
	<br>
	<p class="margin">Select your resource server and the file you want
		to upload to it.</p>
	<br>

	<div class="margin">
		<form method="post" action="oauthAuthorization"
			enctype="multipart/form-data">

			<div class="form-group">
				<label>Your resource server</label> <select name="server">
					<option value="dropbox" selected="true">Dropbox</option>
				</select>
			</div>
			<div class="form-group">
				<label>File to upload in your resource server</label> <input
					type="file" name="file">
			</div>
			<br />
			<button type="submit" class="btn btn-success">Submit</button>
		</form>
	</div>
</body>
</html>