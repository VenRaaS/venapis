# venapis
## guid api:
 * path: http://${server.domain.name}/venapis/vengu/id=${website.domain.name}
 * input: id=${website.domain.name}
 * return: 
   * Http Response Header Set-Cookie: ${uuid} 
   * Http Response Body Message: ${uuid}
 * ex:
   * Javascript
<pre><code>
var venguid = document.createElement('iframe');
venguid.setAttribute("id", "venguid");
venguid.style.display = "none";
venguid.src = "http://localhost:8080/venapis/vengu/?id="+top.location.host;
document.body.appendChild(venguid);	
</code></pre>

## log api:
 * path: http://${server.domain.name}/venapis/log/
 * ipnut:
   * GET/POST key-values[] Parameters
 * return:
   * Http Response Body Message: ok

## check alive api:
 * path: http://${server.domain.name}/venapis/alive
 * input: (no input)
 * return:
   * Http Response Body Message: ok
