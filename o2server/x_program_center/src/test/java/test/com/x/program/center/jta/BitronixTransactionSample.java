//package test.com.x.program.center.jta;
//
//import java.sql.PreparedStatement;
//
//import javax.sql.DataSource;
//
//import bitronix.tm.BitronixTransaction;
//import bitronix.tm.Configuration;
//import bitronix.tm.TransactionManagerServices;
//import bitronix.tm.resource.jdbc.PoolingDataSource;
//
//public class BitronixTransactionSample {
//
//	public static void main(String[] args) throws Exception {
//		System.out.println("!!!!!!!!!!!!!!!!!" + TransactionManagerServices.isTransactionManagerRunning());
//		Configuration conf = TransactionManagerServices.getConfiguration();
//		conf.setServerId("jvm-1");
//		conf.setLogPart1Filename("/data/Temp/part1.btm");
//		conf.setLogPart2Filename("/data/Temp/part2.btm");
//		// 1. 导入Bitronix相关依赖
//		// ...
//
//		// 2. 配置两个数据源，一个为主库，一个为从库
//		DataSource masterDataSource = createDataSource("mysql1",
//				"jdbc:mysql://172.16.98.230:4006/test_ray?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8",
//				"admin", "password");
//		DataSource slaveDataSource = createDataSource("mysql2",
//				"jdbc:mysql://172.16.98.230:4006/test_ray_55?autoReconnect=true&allowPublicKeyRetrieval=true&useSSL=false&useUnicode=true&characterEncoding=UTF-8&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8",
//				"admin", "password");
//
//		// 3. 配置Bitronix事务管理器
//		TransactionManagerServices.getTransactionManager().begin();
//		BitronixTransaction utx = (BitronixTransaction) TransactionManagerServices.getTransactionManager()
//				.getTransaction();
//		// 4. 使用事务管理器管理事务
//		try {
//			// 在主库中执行操作
//			PreparedStatement stmt1 = masterDataSource.getConnection()
//					.prepareStatement("INSERT INTO CTE_CODE (xid, xanswer,xmobile) VALUES (?,?,?)");
//			// 在从库中执行操作
//			PreparedStatement stmt2 = slaveDataSource.getConnection()
//					.prepareStatement("INSERT INTO CTE_CODE (xid, xanswer,xmobile) VALUES (?,?,?)");
//
//			// 设置要插入的数据
//			stmt1.setString(1, "123");
//			stmt1.setString(2, "123");
//			stmt1.setString(3, "123");
//
//			stmt2.setString(1, "123");
//			stmt2.setString(2, "123");
//			stmt2.setString(3, "123");
//
//			stmt2.execute();
//			stmt1.execute();
//			int x = 0;
//			x = 100 / x;
//			utx.commit();
//		} catch (Exception e) {
//			e.printStackTrace();
//			utx.rollback();
//		}
//	}
//
//	private static DataSource createDataSource(String uniqueName, String url, String username, String password) {
//		PoolingDataSource dataSource = new PoolingDataSource();
//		dataSource.setUniqueName(uniqueName);
//		dataSource.setClassName("com.mysql.cj.jdbc.MysqlXADataSource");
//		dataSource.getDriverProperties().setProperty("url", url);
//		dataSource.getDriverProperties().setProperty("user", username);
//		dataSource.getDriverProperties().setProperty("password", password);
//		dataSource.setMinPoolSize(1);
//		dataSource.setMaxPoolSize(5);
//		return dataSource;
//	}
//}