<style>
TD {border:1px solid red;}
</style>

<h1>Database Browser</h1>

<form method="get">
<input style="width:500px;" type="text" name="sql" value="${sql!}"/>
<input type="submit" value="Go"/>
<br/><small>eg select * from app_user</small>
</form>

${rowsHtml}

