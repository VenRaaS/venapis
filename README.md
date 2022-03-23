# venapis

## Developemnt environment
* OS with GUI mode
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
   * Get ven_guid ('typ' is 'g') by Javascript
<pre><code>
var venuuid = document.createElement('iframe');
venuuid.setAttribute("id", "venguid");
venuuid.style.display = "none";
venuuid.src = "http://localhost:8080/venapis/vengu/?typ=g&id="+top.location.host;
document.body.appendChild(venuuid);	
</code></pre>

 * ex:
   *  Get ven_session ('typ' is 's') by HTTP GET method
<pre><code>
GET
https://localhost:8080/venapis/vengu?id=www.test-comp.com.tw&typ=s
</code></pre>

### log api:
 * path: http://${server.domain.name}/venapis/log/
 * header:
   * `content-type:application/x-www-form-urlencoded`
 * ipnut:
   * GET/POST key-values[] Parameters
 * return:
   * Http Response Body Message: "(空值回傳)"

## check alive api:
 * path: http://${server.domain.name}/venapis/alive
 * input: (no input)
 * return:
   * Http Response Body Message: ok
