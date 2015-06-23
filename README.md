# venapis
## guid api:
 * path: http://${your.domain.name}/venapis/vengu/
 * input: (no input)
 * return: 
   * Http Response Header Set-Cookie: ${uuid} 
   * Http Response Body Message: ${uuid}

## log api:
 * path: http://${your.domain.name}/venapis/log/
 * ipnut:
   * GET/POST key-values[] Parameters
 * return:
   * Http Response Body Message: ok

## check alive api:
 * path: http://${your.domain.name}/venapis/alive
 * input: (no input)
 * return:
   * Http Response Body Message: ok
