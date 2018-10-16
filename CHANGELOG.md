# Change Log

## 0.1.0 - 2018-10-15
- First version
- Simple TCP Server connection
- EDN configuration with following structure:
```
{:server {:type :tcp
          :port 17777}
 :apps {"appalias" {:Caption "AppName.exe"
                    :path "C:\\Program Files\\AppName\AppName.exe"}
        "anotheralias" {:caption "App2.exe"
                        :path "C:\\AnotherApp\App2.exe"}}}
```
