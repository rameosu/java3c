# Linux下安装MySQL8.0和重置密码

## 安装MySQL8.0
### 1、下载并安装MySQL官方的 Yum Repository

```shell
[root@Rameo ~]# wget https://dev.mysql.com/get/mysql80-community-release-el8-1.noarch.rpm
```

使用上面的命令就直接下载了安装用的Yum Repository，此时就可以直接yum安装了。

```shell
[root@Rameo ~]# yum install mysql80-community-release-el8-1.noarch.rpm
```

开始安装MySQL服务器

```shell
[root@Rameo ~]# yum -y install mysql-community-server
```

### 2、检查数据源

```shell
[root@Rameo ~]# yum repolist enabled | grep "mysql.*-community.*"
```

### 3、启动MySQL服务

```shell
[root@Rameo ~]# systemctl start mysqld.service
```

查看MySQL运行状态，运行状态如图：
```shell
[root@Rameo ~]# systemctl status mysqld.service
```
出现如下信息，证明启动成功
![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6139f2161e0e4155adeb19b2c16d9f94~tplv-k3u1fbpfcp-watermark.image)

### 4、显示mysql的随机密码

```shell
[root@Rameo ~]# grep 'temporary password' /var/log/mysqld.log
```

### 5、登录并修改mysql密码

```shell
[root@Rameo ~]# mysql -u root -p
```

修改密码：

```sql
mysql> ALTER USER 'root'@'localhost' IDENTIFIED BY '新密码';
```

### 6、开放远程访问

```sql
mysql> create user 'root'@'%' identified by '新密码';
```

 ```sql
 mysql> grant all privileges on *.* to 'root'@'%' with grant option;                                  
 ```

            ```sql
            mysql> ALTER USER 'root'@'%' IDENTIFIED BY '新密码' PASSWORD EXPIRE NEVER;                                  
            ```

          ```sql
          ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '新密码';  
          ```

                             ```sql
                             mysql> FLUSH PRIVILEGES;                    
                             ```



## 重置MySQL8.0密码

### 1、修改MySQL的登录设置
```shell
[root@Rameo ~]# vim /etc/my.cnf
```
添加如图中的配置
```shell
[mysqld]
skip-grant-tables
```
![image.png](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8e4cdf36953b457abc226c572a15ddf3~tplv-k3u1fbpfcp-watermark.image)

保存，然后重启MySQL服务

```shell
[root@Rameo ~]# systemctl restart mysqld.service
```

### 2、修改MySQL密码
进入mysql

```shell
[root@Rameo ~]# mysql -u root
```

切换数据库至mysql
```shell
mysql> use mysql;
```

清空密码
```shell
mysql> update user set authentication_string='' where user='root';
```

退出mysql
```shell
mysql> exit
```

屏蔽skip-grant-tables  
再次vim /etc/my.cnf，在skip-grant-tables前加上#注释  
保存，再次重启MySQL服务，进入mysql，切换到mysql数据库

设置密码
```shell
mysql> ALTER USER 'root'@'%' IDENTIFIED BY '新密码' PASSWORD EXPIRE NEVER;
```

```shell
mysql> ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '新密码';
```
使改动生效
```shell
mysql> FLUSH PRIVILEGES;
```