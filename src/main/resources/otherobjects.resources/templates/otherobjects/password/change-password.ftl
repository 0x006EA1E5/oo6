<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
</head>
<body>
	<h2>Change your password</h2>
	<form method="post">
		<fieldset>
		<input type="hidden" name="changeRequestCode" value="${crc!""}" />
			<table>
			<tr>
				<td>New password:</td>
				<td><input type="password" name="newPassword" /></td>
			</tr>
			<tr>
				<td>New password repeated:</td>
				<td><input type="password" name="newPasswordRepeated" /></td>
			</tr>
			<tr>
				<td colspan="2">
				<input type="submit" value="Change password" />
				</td>
			</tr>
			</table>
		</fieldset>
	</form>
</body>
</html>