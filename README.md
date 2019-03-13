# venapis

## Developemnt evn.
* JDK (JRE/JDK) 1.7+
* [STS - Spring Tool Suite](https://spring.io/tools3/sts/all)

## Prerequisite for running environmnet
* Java runtime (JRE/JDK) 1.7+
* [Tomcat 8.5+](https://tomcat.apache.org/download-80.cgi)

## APIs Usage
### uuid api:
 * path: http://${server.domain.name}/venapis/vengu/
 * input: id=${website.domain.name}
 * return: 
   * Http Response Header Set-Cookie: ${uuid} 
   * Http Response Body Message: ${uuid}
 * ex:
   * Javascript
<pre><code>
var venuuid = document.createElement('iframe');
venuuid.setAttribute("id", "venguid");
venuuid.style.display = "none";
venuuid.src = "http://localhost:8080/venapis/vengu/?id="+top.location.host;
document.body.appendChild(venuuid);	
</code></pre>

### log api:
 * path: http://${server.domain.name}/venapis/log/
 * HEADER TODO....
 * ipnut:
   * GET/POST key-values[] Parameters
 * return:
   * Http Response Body Message: "(空值回傳)"

## check alive api:
 * path: http://${server.domain.name}/venapis/alive
 * input: (no input)
 * return:
   * Http Response Body Message: ok
