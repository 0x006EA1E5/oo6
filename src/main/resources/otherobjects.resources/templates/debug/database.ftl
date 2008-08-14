<style>
TD {border:1px solid red;}
</style>

<h1>Database Browser</h1>

<form method="get">
<input style="width:500px;" type="text" name="sql" value="${sql!}"/>
<input type="submit" value="Go"/>
<br/><small>e.g.</small>
<br/><small><a href="?sql=SELECT * FROM app_user">SELECT * FROM app_user</a></small>
<br/><small><a href="?sql=SELECT * FROM information_schema.tables">SELECT * FROM information_schema.tables</a></small>
</form>

${rowsHtml}

