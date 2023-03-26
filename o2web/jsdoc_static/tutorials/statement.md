## 语法
### 1、JPQL语法
 查询语句支持JPA JPQL语句，如下面的语句从系统表Task中获取待办：
```sql
SELECT o FROM Task o WHERE o.person = :person
```
![JPQL](img/tutorial/statement/jpql.png)
JPQL官网：[https://www.objectdb.com/java/jpa/query/jpql/structure](https://www.objectdb.com/java/jpa/query/jpql/structure)<br />
中文简介：[https://www.codercto.com/a/4338.html](https://www.codercto.com/a/4338.html)

### 2、原生SQL语法
在v8.0及以后版本中，查询语句支持原生的SQL 语法，如下面的语句从系统表Task中获取待办：
```sql
SELECT * FROM Task WHERE person = :person
```
![SQL](img/tutorial/statement/sql.png)
w3schoole网站：[https://www.w3schools.com/sql/default.asp](https://www.w3schools.com/sql/default.asp)<br />
中文网站：[https://www.runoob.com/sql/sql-tutorial.html](https://www.runoob.com/sql/sql-tutorial.html)

### 
## where子句传参
### 1、用冒号动态传参
查询语句中的where语句的值可以使用json传入，如：<br />查询语句的设计为 ：
```sql
select o from tableName o where o.name=:n
```
在调用查询语句服务的时候传入 json：
```json
{
    "n" : "zhangsan"
}
```
最终系统在后台根据这些设计拼接成为如下语句：
```sql
select o from tableName o where o.name='zhangsan'
```
如下图所示：<br />
![用冒号动态传参](img/tutorial/statement/para1.png)
了解JPQL语句动态传参可以点击链接查看：https://www.objectdb.com/java/jpa/query/parameter
### 2、用问号和数字动态传参
在V8.0中，平台支持用问号加数字的形式来传where语句的值，作用和冒号动态传参类似。，如：<br />查询语句的设计为 ：
```sql
select o from tableName o where o.name= ?1
```
在调用查询语句服务的时候传入 json：
```json
{
    "?1" : "zhangsan"
}
```
最终系统在后台根据这些设计拼接成为如下语句：
```sql
select o from tableName o where o.name='zhangsan'
```
如下图所示：<br />
![用问号和数字动态传参](img/tutorial/statement/para2.png)
### 3、默认参数
系统中有一些默认参数，对这些默认参数，系统会自动赋值。

| **默认参数** | **含义** |
| --- | --- |
| person | 当前人 |
| identityList | 当前人身份列表 |
| unitList | 当前人所在直接组织 |
| unitAllList | 当前人所在所有组织 |
| groupList | 当前人所在群组 |
| roleList | 当前人拥有的角色 |

如有如下语句：
```json
select o from Task o where o.person = :person
```
参数`:person`为当前人，<br />在v8.0之前，外部传入参数 {person: ""}即可；<br />在v8.0之后，系统将自动解析这些默认参数，不需要再传入。<br />如当前人是`张三@zhangsan@P`，系统解析后自动拼接如下：
```json
select o from Task o where o.person = "张三@zhangsan@P"
```

## 其他注意事项

### 1、日期格式的写法
在sql中，日期格式使用文本即可，如 :"2019-12-31", "23:59:59", "2020-01-03 13:59:59"。<br />
在jpql中，对日期格式有特殊的写法，格式如下：<br />
Date - {d 'yyyy-mm-dd'} - for example: {d '2019-12-31'}<br />
Time - {t 'hh:mm:ss'} - for example: {t '23:59:59'}<br />
Timestamp(DateTime) - {ts 'yyyy-mm-dd hh:mm:ss'} - for example: {ts '2020-01-03 13:59:59'}
<br />示例如下图：<br />
![日期格式](img/tutorial/statement/datetime.png)
### 2、like的写法
如果运算符用的是 like，模糊查询，值为 "%{value}%"。
