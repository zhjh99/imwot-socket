# imwot-socket
imwot-socket

一、简介


二、开发环境

2.1、Eclipse IDE for Java Developers
Version: Oxygen.3a Release (4.7.3a)
Build id: 20180405-1200

2.2、jdk1.8.0_161


三、开发准备:

3.1、下载源码到本地

3.2、生成 Eclipse 项目文件,并修改编码为utf-8

mvn eclipse:eclipse

3.3、导入到项目

四、开发备注:
TYPE = 0 并且 command = "close"表示客户端将关闭连接，请服务端关闭socket
