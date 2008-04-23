<script type="text/javascript" src="http://cdn.othermedia.com/ojay-yui/2.4.1.js"></script>
<script type="text/javascript" src="http://cdn.othermedia.com/ojay/0.1.2/all-min.js"></script>

<h1>JCR Browser</h1>

<form method="get">
<input style="width:500px;" type="text" name="xpath" value="${xpath!}"/>
<input type="submit" value="Go"/>
<br/><small>eg /jcr:root/element(*)</small>
</form>

${nodesHtml}

<script>
$('.properties-area').setStyle({display:'none'}).insert(' <a class="view" href="javascript:void;">[View]</a>', 'before');
$('.view').on('click').siblings().setStyle({display: 'block'});
</script>