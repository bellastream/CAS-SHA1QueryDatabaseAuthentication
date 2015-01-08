CAS-SHA1QueryDatabaseAuthentication

A CAS server authentication support for Salt+SHA1. 
With this authentication method, CAS can use user table created by django 1.3 as backend directly.

OK for cas-server-4.0.0. Other cas-server versions may need some change.


Download JAR [here][downloadCAS-SHA]


Build:

Required JAR lib:

cas-server-core-4.0.0.jar
cas-server-support-jdbc-4.0.0.jar
libs/spring-core-3.2.9.RELEASE.jar
spring-jdbc-3.2.9.RELEASE.jar
spring-tx-3.2.9.RELEASE.jar
validation-api-1.0.0.GA.jar
log4j-1.2.17.jar



Deployment:

Put cas-server-support-sha1.jar to cas/WEB-INF/lib/

Config deployerConfigContext.xml

<bean id="primaryAuthenticationHandler" class="org.jasig.cas.adaptors.jdbc.SHA1QueryDatabaseAuthenticationHandler">
    <property name="dataSource" ref="dataSource"></property>
    <property name="sql" value="select password from auth_user where username=?"></property>
    <property name="passwordEncoder" ref="passwordEncoder"></property>
</bean>
		
<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
    <property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
    <property name="url" value="jdbc:mysql://localhost:3306/database"></property>
    <property name="username" value="work"></property>
    <property name="password" value="work"></property>
</bean>
	
<bean id="passwordEncoder" class="org.jasig.cas.authentication.handler.DefaultPasswordEncoder">
    <constructor-arg value="SHA1"/>
</bean>

use your own database url.
The query pattern 'select password from auth_user where username=?' works well with auth_user table which is built by Django auth system.

restart your cas server.

[downloadCAS-SHA]: https://github.com/bellastream/CAS-SHA1QueryDatabaseAuthentication/releases/download/0.1/cas-server-support-sha1.jar