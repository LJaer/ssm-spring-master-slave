# spring4_springmvc_mybatis
环境：
win7
tomcat8
jdk8
spring4
##maven搭建的spring4+springmvc+mybatis框架

并进行简单测试
创建test数据库并导入sql文件夹下的sql语句建表

在浏览器输入http://localhost:8080/ssm/selectByPrimaryKey?id=1

可得到：User{id=1, username='zk', password='123'}

##Spring 一主多从测试 测试

###　１、在 Service 层使用 AOP 打印 Service 日志

在浏览器输入http://localhost:8080/ssm/redis/getRedis

控制台打印：

```
DEBUG [http-nio-8080-exec-7] - Returning handler method [public void cn.ljaer.ssm.controller.UserController.selectByPrimaryKeyCont(int,javax.servlet.http.HttpServletResponse) throws java.lang.Exception]
DEBUG [http-nio-8080-exec-7] - Returning cached instance of singleton bean 'userController'
DEBUG [http-nio-8080-exec-7] - Last-Modified value for [/ssm/selectByPrimaryKey] is: -1
 INFO [http-nio-8080-exec-7] - 
DataSource routing report -------- 2017-09-19 17:35:22 -------------------------------------
Service   : cn.ljaer.ssm.service.impl.UserServiceImpl
Method    : saveMethod
DataSource: null ====> master0
--------------------------------------------------------------------------------------------
DEBUG [http-nio-8080-exec-7] - Creating new transaction with name [cn.ljaer.ssm.service.impl.UserServiceImpl.saveMethod]: PROPAGATION_REQUIRED,ISOLATION_DEFAULT,-java.lang.Exception
DEBUG [http-nio-8080-exec-7] - {conn-10005} pool-connect
DEBUG [http-nio-8080-exec-7] - Acquired Connection [com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl@480b8781] for JDBC transaction
DEBUG [http-nio-8080-exec-7] - Switching JDBC Connection [com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl@480b8781] to manual commit
DEBUG [http-nio-8080-exec-7] - {conn-10005} setAutoCommit false
 INFO [http-nio-8080-exec-7] - --------------------saveMethod-----------------------
 INFO [http-nio-8080-exec-7] - 
Service execute report -------- 2017-09-19 17:35:22 ----------------------------------------
Service   : cn.ljaer.ssm.service.impl.UserServiceImpl
Method    : saveMethod
Parameter : 
Result    : null
Cost Time : 0 ms
--------------------------------------------------------------------------------------------
DEBUG [http-nio-8080-exec-7] - Initiating transaction commit
DEBUG [http-nio-8080-exec-7] - Committing JDBC transaction on Connection [com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl@480b8781]
DEBUG [http-nio-8080-exec-7] - {conn-10005} commited
DEBUG [http-nio-8080-exec-7] - {conn-10005} setAutoCommit true
DEBUG [http-nio-8080-exec-7] - Releasing JDBC Connection [com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl@480b8781] after transaction
DEBUG [http-nio-8080-exec-7] - Returning JDBC Connection to DataSource
DEBUG [http-nio-8080-exec-7] - {conn-10005} pool-recycle
 INFO [http-nio-8080-exec-7] - 
DataSource routing report -------- 2017-09-19 17:35:22 -------------------------------------
Service   : cn.ljaer.ssm.service.impl.UserServiceImpl
Method    : selectMethod
DataSource: null ====> slave3
--------------------------------------------------------------------------------------------
 INFO [http-nio-8080-exec-7] - --------------------selectMethod-----------------------
 INFO [http-nio-8080-exec-7] - 
Service execute report -------- 2017-09-19 17:35:22 ----------------------------------------
Service   : cn.ljaer.ssm.service.impl.UserServiceImpl
Method    : selectMethod
Parameter : 
Result    : null
Cost Time : 0 ms
--------------------------------------------------------------------------------------------
 INFO [http-nio-8080-exec-7] - 
DataSource routing report -------- 2017-09-19 17:35:22 -------------------------------------
Service   : cn.ljaer.ssm.service.impl.UserServiceImpl
Method    : selectByPrimaryKey
DataSource: null ====> slave0
--------------------------------------------------------------------------------------------
 INFO [http-nio-8080-exec-7] - 
Service execute report -------- 2017-09-19 17:35:22 ----------------------------------------
Service   : cn.ljaer.ssm.service.impl.UserServiceImpl
Method    : selectByPrimaryKey
Parameter : 1
Result    : {}
Cost Time : 3 ms
--------------------------------------------------------------------------------------------
DEBUG [http-nio-8080-exec-7] - Null ModelAndView returned to DispatcherServlet with name 'springmvc_rest': assuming HandlerAdapter completed request handling
 INFO [http-nio-8080-exec-7] - 
Controller execute report -------- 2017-09-19 17:35:22 -------------------------------------
URI         : /ssm/selectByPrimaryKey, Method : GET
Controller  : cn.ljaer.ssm.controller.UserController, Method : selectByPrimaryKeyCont
QueryString : id=1
Cost Time   : 19 ms
--------------------------------------------------------------------------------------------
DEBUG [http-nio-8080-exec-7] - Successfully completed request
```

### 2、相关配置如下

[在应用层通过spring解决数据库读写分离](http://www.iteye.com/topic/1127642)

db.properties

```
master0.jdbc.driverclass=com.mysql.jdbc.Driver
master0.jdbc.url=jdbc:mysql://192.168.99.100:3306/test?useUnicode=true&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&autoReconnect=true
master0.jdbc.username=root
master0.jdbc.password=123456

slave0.jdbc.driverclass=com.mysql.jdbc.Driver
slave0.jdbc.url=jdbc:mysql://192.168.99.100:3306/test?useUnicode=true&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&autoReconnect=true
slave0.jdbc.username=root
slave0.jdbc.password=123456
```

Spring 一主多从主要配置

```
	<!-- 加载db.properties文件中的内容，db.properties文件中的key命名要有一定的特殊规则 -->
	<context:property-placeholder location="classpath:config/db.properties" />

	<bean id="master0_datasource" class="com.alibaba.druid.pool.DruidDataSource"
		init-method="init" destroy-method="close">
		<!-- 基本属性 url、user、password -->
		<property name="url" value="${master0.jdbc.url}" />
		<property name="username" value="${master0.jdbc.username}" />
		<property name="password" value="${master0.jdbc.password}" />

		<!-- 配置初始化大小、最小、最大 -->
		<property name="initialSize" value="5" />
		<!-- 连接池中最少空闲maxIdle个连接 -->
		<property name="minIdle" value="5" />
		<!-- 连接池激活的最大数据库连接总数。设为0表示无限制 -->
		<property name="maxActive" value="20" />
		<!-- 最大建立连接等待时间，单位为 ms，如果超过此时间将接到异常。设为-1表示无限制 -->
		<property name="maxWait" value="60000" />
		<!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
		<property name="timeBetweenEvictionRunsMillis" value="60000" />
		<!-- 配置连接池中连接可空闲的时间(针对连接池中的连接对象.空闲超过这个时间则断开，直到连接池中的连接数到minIdle为止)，单位是毫秒 -->
		<property name="minEvictableIdleTimeMillis" value="300000" />
		<!-- 用来检测连接是否有效的sql，要求是一个查询语句 -->
		<property name="validationQuery" value="SELECT 'x' FROM DUAL" />
		<!-- 建议配置为true，不影响性能，并且保证安全性 -->
		<property name="testWhileIdle" value="true" />
		<!-- 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能 -->
		<property name="testOnBorrow" value="false" />
		<!-- 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能 -->
		<property name="testOnReturn" value="false" />
		<!-- 打开PSCache，并且指定每个连接上PSCache的大小(Oracle或mysql5.5及以上使用) -->
		<property name="poolPreparedStatements" value="true" />
		<property name="maxPoolPreparedStatementPerConnectionSize"
			value="20" />
		<!-- 配置监控统计拦截的filters -->
		<property name="filters" value="stat,log4j,wall" />
		<!-- 配置关闭长时间不使用的连接 -->
		<!-- 是否清理removeAbandonedTimeout秒没有使用的活动连接,清理后并没有放回连接池(针对未被close的活动连接) -->
		<property name="removeAbandoned" value="true" />
		<!-- 活动连接的最大空闲时间,1800秒，也就是30分钟 -->
		<property name="removeAbandonedTimeout" value="1800" />
		<!-- 连接池收回空闲的活动连接时是否打印消息 -->
		<property name="logAbandoned" value="true" />
	</bean>

	<bean id="slave0_datasource" class="com.alibaba.druid.pool.DruidDataSource"
		init-method="init" destroy-method="close">
		<!-- 基本属性 url、user、password -->
		<property name="url" value="${slave0.jdbc.url}" />
		<property name="username" value="${slave0.jdbc.username}" />
		<property name="password" value="${slave0.jdbc.password}" />

		<!-- 配置初始化大小、最小、最大 -->
		<property name="initialSize" value="5" />
		<!-- 连接池中最少空闲maxIdle个连接 -->
		<property name="minIdle" value="5" />
		<!-- 连接池激活的最大数据库连接总数。设为0表示无限制 -->
		<property name="maxActive" value="20" />
		<!-- 最大建立连接等待时间，单位为 ms，如果超过此时间将接到异常。设为-1表示无限制 -->
		<property name="maxWait" value="60000" />
		<!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
		<property name="timeBetweenEvictionRunsMillis" value="60000" />
		<!-- 配置连接池中连接可空闲的时间(针对连接池中的连接对象.空闲超过这个时间则断开，直到连接池中的连接数到minIdle为止)，单位是毫秒 -->
		<property name="minEvictableIdleTimeMillis" value="300000" />
		<!-- 用来检测连接是否有效的sql，要求是一个查询语句 -->
		<property name="validationQuery" value="SELECT 'x' FROM DUAL" />
		<!-- 建议配置为true，不影响性能，并且保证安全性 -->
		<property name="testWhileIdle" value="true" />
		<!-- 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能 -->
		<property name="testOnBorrow" value="false" />
		<!-- 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能 -->
		<property name="testOnReturn" value="false" />
		<!-- 打开PSCache，并且指定每个连接上PSCache的大小(Oracle或mysql5.5及以上使用) -->
		<property name="poolPreparedStatements" value="true" />
		<property name="maxPoolPreparedStatementPerConnectionSize"
			value="20" />
		<!-- 配置监控统计拦截的filters -->
		<property name="filters" value="stat,log4j" />
		<!-- 配置关闭长时间不使用的连接 -->
		<!-- 是否清理removeAbandonedTimeout秒没有使用的活动连接,清理后并没有放回连接池(针对未被close的活动连接) -->
		<property name="removeAbandoned" value="true" />
		<!-- 活动连接的最大空闲时间,1800秒，也就是30分钟 -->
		<property name="removeAbandonedTimeout" value="1800" />
		<!-- 连接池收回空闲的活动连接时是否打印消息 -->
		<property name="logAbandoned" value="true" />
	</bean>

	<bean id="log4j" class="com.alibaba.druid.filter.logging.Log4jFilter">
		<property name="statementExecutableSqlLogEnable" value="true" />
	</bean>

	<bean id="dataSource"
		class="cn.ljaer.ssm.datasource.ThreadLocalRoutingDataSource">
		<property name="defaultTargetDataSource" ref="master0_datasource" />
		<property name="targetDataSources">
			<map>
				<entry key="master0" value-ref="master0_datasource" />
				<entry key="slave0" value-ref="slave0_datasource" />
				<entry key="slave1" value-ref="slave0_datasource" />
				<entry key="slave2" value-ref="slave0_datasource" />
				<entry key="slave3" value-ref="slave0_datasource" />
			</map>
		</property>
	</bean>

	<!-- 让spring管理sqlsessionfactory 使用mybatis和spring整合包中的 -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<!--dataSource属性指定要用到的连接池 -->
		<property name="dataSource" ref="dataSource" />
		<!--configLocation属性指定mybatis的核心配置文件 -->
		<property name="configLocation" value="classpath:config/mybatis/sqlMapConfig.xml" />
		<property name="mapperLocations" value="classpath:cn/ljaer/ssm/sqlMapper/*Mapper.xml" />
	</bean>

	<!-- mapper扫描器 -->
	<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
		<!-- 扫描包路径，如果需要扫描多个包，中间使用半角逗号隔开 -->
		<property name="basePackage" value="cn.ljaer.ssm.mapper"></property>
		<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
	</bean>

	<bean id="txManager"
		class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<property name="dataSource" ref="dataSource" />
	</bean>
```

事物

```
   	<bean id="txManager"
		class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
		<!-- (this dependency is defined somewhere else) -->
		<property name="dataSource" ref="dataSource" />
	</bean>

	<!-- 启用AspectJ对Annotation的支持 -->
	<aop:aspectj-autoproxy />

	<!-- Transaction Support -->
	<tx:advice id="useTxAdvice" transaction-manager="txManager">
		<tx:attributes>
			<tx:method name="*remove*" propagation="REQUIRED"
				rollback-for="java.lang.Exception" />
			<tx:method name="*save*" propagation="REQUIRED"
				rollback-for="java.lang.Exception" />
			<tx:method name="*modify*" propagation="REQUIRED"
				rollback-for="java.lang.Exception" />
			<tx:method name="*create*" propagation="REQUIRED"
				rollback-for="java.lang.Exception" />
			<tx:method name="*delete*" propagation="REQUIRED"
				rollback-for="java.lang.Exception" />
			<tx:method name="*update*" propagation="REQUIRED"
				rollback-for="java.lang.Exception" />
			<tx:method name="*add*" propagation="REQUIRED"
				rollback-for="java.lang.Exception" />
			<tx:method name="creditNoticeEntry" propagation="REQUIRED"
				rollback-for="java.lang.Exception" />

			<tx:method name="*find*" propagation="SUPPORTS" read-only="true" />
			<tx:method name="*get*" propagation="SUPPORTS" read-only="true" />
			<tx:method name="*page*" propagation="SUPPORTS" read-only="true" />
			<tx:method name="*count*" propagation="SUPPORTS" read-only="true" />
			<tx:method name="*select*" propagation="SUPPORTS"
				read-only="true" />
			<tx:method name="*query*" propagation="SUPPORTS" read-only="true" />
		</tx:attributes>
	</tx:advice>

	<!--把事务控制在Service层 -->
	<aop:config>
		<aop:pointcut id="pc"
			expression="execution(public * cn.ljaer.ssm.service.*.*(..))" />
		<aop:advisor pointcut-ref="pc" advice-ref="useTxAdvice" />
	</aop:config>
```

ThreadLocalRoutingDataSource.class

```java
package cn.ljaer.ssm.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ThreadLocalRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceTypeManager.get();
    }

}
```

DataSourceTypeManager.class

```java
package cn.ljaer.ssm.datasource;

public class DataSourceTypeManager {

    public static final String MASTER = "master";

    public static final String SLAVE = "slave";

    private static final ThreadLocal<String> dataSourceTypes = new ThreadLocal<>();

    public static String get() {
        return dataSourceTypes.get();
    }

    public static void set(String dataSourceType) {
        dataSourceTypes.set(dataSourceType);
    }

    public static void reset() {
        dataSourceTypes.remove();
    }

    public static boolean isChoiceWrite() {
        return (dataSourceTypes.get() != null && dataSourceTypes.get().contains(MASTER));
    }

    public static void markRead(String lookupKey) {
        set(lookupKey);
    }

    public static void markWrite(String lookupKey) {
        set(lookupKey);
    }
}

```

DataSourceRoutingAspectProcessor.class

```
package cn.ljaer.ssm.datasource.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.NameMatchTransactionAttributeSource;
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.ReflectionUtils;

import cn.ljaer.ssm.datasource.DataSourceTypeManager;
import cn.ljaer.ssm.datasource.ThreadLocalRoutingDataSource;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Aspect
@Order(0)
public class DataSourceRoutingAspectProcessor implements BeanPostProcessor {

    private Map<String, Boolean> readMethodMap = new HashMap<>();

    private List<String> lookupMasterKeys = new ArrayList<>();

    private List<String> lookupSlaveKeys = new ArrayList<>();

    private Boolean forceChoiceReadWhenWrite = Boolean.TRUE;

    public void setForceChoiceReadWhenWrite(boolean forceChoiceReadWhenWrite) {
        this.forceChoiceReadWhenWrite = forceChoiceReadWhenWrite;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof NameMatchTransactionAttributeSource) {
            try {
                NameMatchTransactionAttributeSource transactionAttributeSource = (NameMatchTransactionAttributeSource) bean;
                Field nameMapField = ReflectionUtils.findField(NameMatchTransactionAttributeSource.class, "nameMap");
                nameMapField.setAccessible(true);
                Map<String, TransactionAttribute> nameMap = (Map<String, TransactionAttribute>) nameMapField.get(transactionAttributeSource);

                for (Map.Entry<String, TransactionAttribute> entry : nameMap.entrySet()) {
                    RuleBasedTransactionAttribute attr = (RuleBasedTransactionAttribute) entry.getValue();

                    //仅对read-only的处理
                    if (!attr.isReadOnly()) {
                        continue;
                    }

                    String methodName = entry.getKey();
                    Boolean isForceChoiceRead = Boolean.FALSE;
                    if (forceChoiceReadWhenWrite) {
                        //不管之前操作是写，默认强制从读库读 （设置为NOT_SUPPORTED即可）
                        //NOT_SUPPORTED会挂起之前的事务
                        attr.setPropagationBehavior(Propagation.NOT_SUPPORTED.value());
                        isForceChoiceRead = Boolean.TRUE;
                    } else {
                        //否则 设置为SUPPORTS（这样可以参与到写事务）
                        attr.setPropagationBehavior(Propagation.SUPPORTS.value());
                    }
                    readMethodMap.put(methodName, isForceChoiceRead);
                }
            } catch (Exception e) {
                log.error("Init readMethodMap occur exception : ", e);
            }
        }

        if (bean instanceof ThreadLocalRoutingDataSource) {
            try {
                ThreadLocalRoutingDataSource routingDataSource = (ThreadLocalRoutingDataSource) bean;
                Field targetDataSourcesField = ReflectionUtils.findField(ThreadLocalRoutingDataSource.class, "targetDataSources");
                targetDataSourcesField.setAccessible(true);
                Map<Object, Object> targetDataSources = (Map<Object, Object>) targetDataSourcesField.get(routingDataSource);

                for (Map.Entry<Object, Object> entry : targetDataSources.entrySet()) {
                    if (entry.getKey().toString().contains(DataSourceTypeManager.MASTER)) {
                        lookupMasterKeys.add(entry.getKey().toString());
                    } else if (entry.getKey().toString().contains(DataSourceTypeManager.SLAVE)) {
                        lookupSlaveKeys.add(entry.getKey().toString());
                    }
                }
            } catch (Exception e) {
                log.error("Init lookupMasterKeys or lookupSlaveKeys occur exception : ", e);
            }

        }
        return bean;
    }

    @Pointcut("execution(public * cn.ljaer.ssm.service.*.*(..))")
    public void aspectjMethod() {
    }

    @Around(value = "aspectjMethod()")
    public Object aroundAdvice(ProceedingJoinPoint pjp) throws Throwable {
        String className = pjp.getTarget().getClass().getName();
        String methodName = pjp.getSignature().getName();

        StringBuilder logMsg = new StringBuilder("\nDataSource routing report -------- " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " -------------------------------------");
        logMsg.append("\nService   : ").append(className);
        logMsg.append("\nMethod    : ").append(methodName);
        logMsg.append("\nDataSource: ").append(DataSourceTypeManager.get()).append(" ====> ");

        if (isChoiceReadDB(pjp.getSignature().getName())) {
            Collections.shuffle(lookupSlaveKeys);
            DataSourceTypeManager.markRead(lookupSlaveKeys.get(0));
        } else {
            Collections.shuffle(lookupMasterKeys);
            DataSourceTypeManager.markWrite(lookupMasterKeys.get(0));
        }
        logMsg.append(DataSourceTypeManager.get());
        logMsg.append("\n--------------------------------------------------------------------------------------------");
        log.info(logMsg.toString());

        try {
            return pjp.proceed();
        } finally {
            DataSourceTypeManager.reset();
        }
    }

    private boolean isChoiceReadDB(String methodName) {

        String bestNameMatch = null;
        for (String mappedName : this.readMethodMap.keySet()) {
            if (PatternMatchUtils.simpleMatch(mappedName, methodName)) {
                bestNameMatch = mappedName;
                break;
            }
        }

        Boolean isForceChoiceRead = readMethodMap.get(bestNameMatch);
        //表示强制选择读库
        if (isForceChoiceRead == Boolean.TRUE) {
            return true;
        }

        //如果之前选择了写库 现在还选择 写库
        if (DataSourceTypeManager.isChoiceWrite()) {
            return false;
        }

        //表示应该选择读库
        if (isForceChoiceRead != null) {
            return true;
        }

        //默认选择 写库
        return false;
    }
}
```
