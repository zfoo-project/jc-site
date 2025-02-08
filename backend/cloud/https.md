# 一、什么是REST风格
```
REST(Representation State Transfer)，表现层状态转化。如果一个架构符合REST原则，就称它为RESTful架构。

资源：REST省略了主语Resource，全称应该叫资源的表现层状态转化，每一个uri都是一个资源的地址，是独一无二的表示符，可以是一张图片，一个文本，一首歌曲；
表现层：Representation，资源是一种实体，它可以有多重外在表现形式，可以是txt展现，也可以是html，jpg展现，有些网址最后的.html是不必要的，
    因为这个后缀名表示格式，属于表现层。具体表现形式应该在HTTP请求的头信息中用Accept和Content-Type字段指定，这两个字段才是对表现层的描述。
状态转化：State Transfer，访问一个网站势必涉及数据和状态的变化，用Get获取资源，Post新建资源，Put更新资源，Delete删除资源。
```


# 二、https证书的制作
- 生成秘钥
```
openssl genrsa -out private.key 2048
```

- 生成用于申请请求的证书文件csr，一般会将该文件发送给CA机构进行认证，本例使用自签名证书
```
openssl req -new -key private.key -out request.csr
```

- 自签名证书
```
openssl x509 -req -days 365 -in request.csr -signkey private.key -out root.crt
```

- 生成p12文件
```
openssl pkcs12 -export -out cert.p12 -inkey private.key -in root.crt
```

- p12文件转换为keystore文件
```
keytool -importkeystore -srckeystore cert.p12 -destkeystore my.keystore -deststoretype pkcs12
```

- p12转换为jks
```
keytool -importkeystore -srckeystore cert.p12 -destkeystore dest.jks -deststoretype pkcs12
```

- 查看证书信息
```
openssl x509 -noout -text -in root.crt
```

- springboot https使用keystore证书
```
server:
  address: 0.0.0.0
  port: 443
  ssl:
    key-store: classpath:my.keystore
    key-store-password: 123456
    keyStoreType: PKCS12
```

- 从私钥中解析出公钥
```
openssl rsa -in private.key -pubout -out public.pem
```

- 从JSK文件中提取公钥
```
keytool -list -rfc -keystore demo.jks -storepass password
```
