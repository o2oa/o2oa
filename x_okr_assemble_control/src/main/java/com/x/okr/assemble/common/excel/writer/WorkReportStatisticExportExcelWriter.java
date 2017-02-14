package com.x.okr.assemble.common.excel.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.google.gson.Gson;
import com.x.base.core.gson.XGsonBuilder;
import com.x.okr.assemble.control.timertask.entity.BaseWorkReportStatisticEntity;
import com.x.okr.assemble.control.timertask.entity.CenterWorkReportStatisticEntity;
import com.x.okr.assemble.control.timertask.entity.WorkReportProcessOpinionEntity;
import com.x.okr.entity.OkrCenterWorkReportStatistic;

public class WorkReportStatisticExportExcelWriter {
	
	
	/**
	 * 写入excel并填充内容,一个sheet只能写65536行以下，超出会报异常，写入时建议大量数据时使用AbstractExcel2007Writer
	 * @param fileOut
	 * @throws IOException
	 */
	public static void writeExcel( List<OkrCenterWorkReportStatistic> okrCenterWorkReportStatisticList, OutputStream fileOut ) throws IOException{
			//Logger logger = LoggerFactory.getLogger( WorkReportStatisticExportExcelWriter.class );
			Map<String, List<OkrCenterWorkReportStatistic>> map = new HashMap<String, List<OkrCenterWorkReportStatistic>>();
			List<OkrCenterWorkReportStatistic> okrCenterWorkReportStatisticList_result = null;
			List<BaseWorkReportStatisticEntity> baseWorkReportStatisticEntityList = null;
			List<WorkReportProcessOpinionEntity> workReportProcessOpinionEntityList = null;
			CenterWorkReportStatisticEntity centerWorkReportStatisticEntity = null;
			String statisticContent = null;
			Iterator<String> iterator = null;
			Gson gson = XGsonBuilder.pureGsonDateFormated();
			String key = null;
			String opinion = null;
			if( okrCenterWorkReportStatisticList != null && !okrCenterWorkReportStatisticList.isEmpty() ){
				for( OkrCenterWorkReportStatistic okrCenterWorkReportStatistic : okrCenterWorkReportStatisticList){
					okrCenterWorkReportStatisticList_result = map.get( okrCenterWorkReportStatistic.getDefaultWorkType() );
					if( okrCenterWorkReportStatisticList_result == null ){
						okrCenterWorkReportStatisticList_result = new ArrayList<OkrCenterWorkReportStatistic>();
						okrCenterWorkReportStatisticList_result.add( okrCenterWorkReportStatistic );
						map.put( okrCenterWorkReportStatistic.getDefaultWorkType(), okrCenterWorkReportStatisticList_result );
					}else{
						okrCenterWorkReportStatisticList_result.add( okrCenterWorkReportStatistic );
					}
				}
			}
			
			iterator = map.keySet().iterator();
			Integer workCountInType = 0;
			Integer workCountInCenter = 0;
			Integer currentRowNumber = 0;
			Workbook wb = new HSSFWorkbook();// 创建excel2003对象
			Sheet sheet = wb.createSheet( "工作汇报情况统计表" );// 创建新的工作表
			Row row = null;
			Cell cell = null;
			
			//表头//////////////////////////////////////////////////////////////
			row = sheet.createRow( currentRowNumber );
			row.createCell(0).setCellValue( "工作类别" );
			row.createCell(1).setCellValue( "重点事项" );//中心工作内容
			row.createCell(2).setCellValue( "责任部门" );
			row.createCell(3).setCellValue( "事项分解及描述" );//具体工作内容
			row.createCell(4).setCellValue( "工作汇报情况" );
			row.createCell(5).setCellValue( "工作汇报形式" );
			row.createCell(6).setCellValue( "具体行动举措" );
			row.createCell(7).setCellValue( "预期里程碑/阶段性结果标志" );
			row.createCell(8).setCellValue( "截止目前完成情况" );
			row.createCell(9).setCellValue( "下一步工作要点及需求" );
			row.createCell(10).setCellValue( "督办评价" );
			row.createCell(11).setCellValue( "领导评价" );
			
			currentRowNumber++;
			while( iterator.hasNext() ){
				key = iterator.next().toString();
				workCountInType = 0;
				okrCenterWorkReportStatisticList_result = map.get( key );
				//logger.info( "类别["+ key +"]....................." );
				if( okrCenterWorkReportStatisticList_result != null ){
					//计算工作总量，以及每个中心工作下面的工作数量
					for( OkrCenterWorkReportStatistic statistic : okrCenterWorkReportStatisticList_result ){
						workCountInCenter = 0;
						statisticContent = statistic.getReportStatistic();
						if( statisticContent != null ){
							centerWorkReportStatisticEntity = gson.fromJson( statisticContent, CenterWorkReportStatisticEntity.class );
						}
						if( centerWorkReportStatisticEntity != null ){
							baseWorkReportStatisticEntityList = centerWorkReportStatisticEntity.getWorkReportStatisticEntityList();
						}
						if( baseWorkReportStatisticEntityList != null ){
							for( BaseWorkReportStatisticEntity baseWorkReportStatisticEntity : baseWorkReportStatisticEntityList ){
								if( "1".equals( baseWorkReportStatisticEntity.getWorkLevel() ) ){
									workCountInType ++;
									workCountInCenter++;
								}
							}
						}
						//logger.info( "中心工作["+ statistic.getCenterTitle() +"]有"+ workCountInCenter +"个具体工作" );
						statistic.setWorkCount( workCountInCenter );
					}
					//logger.info( "类别["+ key +"]有"+ workCountInType +"个具体工作" );
					
					/**
					 * CellRangeAddress  对象的构造方法需要传入合并单元格的首行、最后一行、首列、最后一列。
					 */
					//第1行，到第workCountInType行，第1列到第1列
					//logger.info( ">>类别["+key+"]:" + currentRowNumber + " --> " + ( currentRowNumber + workCountInType - 1 ));
					sheet.addMergedRegion( new CellRangeAddress( currentRowNumber, ( currentRowNumber + workCountInType - 1 ), 0, 0 ));
					
					for( OkrCenterWorkReportStatistic statistic : okrCenterWorkReportStatisticList_result ){
						if( statistic.getWorkCount() > 0 ){
							//logger.info( ">>>>中心工作["+ statistic.getCenterTitle() +"]:" + currentRowNumber + " --> " + ( currentRowNumber + statistic.getWorkCount() - 1 ) );
							sheet.addMergedRegion( new CellRangeAddress( currentRowNumber, ( currentRowNumber + statistic.getWorkCount() - 1 ), 1, 1 ));
							
							statisticContent = statistic.getReportStatistic();
							if( statisticContent != null ){
								centerWorkReportStatisticEntity = gson.fromJson( statisticContent, CenterWorkReportStatisticEntity.class );
							}
							if( centerWorkReportStatisticEntity != null ){
								baseWorkReportStatisticEntityList = centerWorkReportStatisticEntity.getWorkReportStatisticEntityList();
							}
							if( baseWorkReportStatisticEntityList != null ){
								for( BaseWorkReportStatisticEntity baseWorkReportStatisticEntity : baseWorkReportStatisticEntityList ){
									if( "1".equals( baseWorkReportStatisticEntity.getWorkLevel() ) ){
										
										row = sheet.createRow( currentRowNumber );
										row.createCell(0).setCellValue( key );//"工作类别"
										row.createCell(1).setCellValue( statistic.getCenterTitle() );//"重点事项"
										row.createCell(2).setCellValue( baseWorkReportStatisticEntity.getOrganizationName() );//"责任部门"
										row.createCell(3).setCellValue( baseWorkReportStatisticEntity.getWorkDetail() );//"事项分解及描述"
										row.createCell(4).setCellValue( baseWorkReportStatisticEntity.getReportStatus() );//"工作汇报情况"
										row.createCell(5).setCellValue( baseWorkReportStatisticEntity.getReportCycle() );//"工作汇报形式"
										row.createCell(6).setCellValue( baseWorkReportStatisticEntity.getProgressAction() );//"具体行动举措"
										row.createCell(7).setCellValue( baseWorkReportStatisticEntity.getLandmarkDescription() );//"预期里程碑/阶段性结果标志"
										row.createCell(8).setCellValue( baseWorkReportStatisticEntity.getProgressDescription() );//"截止目前完成情况"
										row.createCell(9).setCellValue( baseWorkReportStatisticEntity.getWorkPlan() );//"下一步工作要点及需求"
										row.createCell(10).setCellValue( baseWorkReportStatisticEntity.getAdminSuperviseInfo() );//"督办评价"
										
										workReportProcessOpinionEntityList = baseWorkReportStatisticEntity.getOpinions();
										opinion = "";
										if( workReportProcessOpinionEntityList != null ){
											for( WorkReportProcessOpinionEntity workReportProcessOpinionEntity : workReportProcessOpinionEntityList ){
												opinion = opinion + workReportProcessOpinionEntity.getProcessorName() + ":"+workReportProcessOpinionEntity.getOpinion() +"\n";
											}
										}
										row.createCell(11).setCellValue( opinion );//"领导评价"
										
										currentRowNumber++;
									}
								}
							}
						}
					}
				}
			}
			
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			////////////////                                单元格样式设计                                                                                       //////////////////
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sheet.setDefaultColumnWidth( 20 );  
			sheet.setDefaultRowHeightInPoints( 20 );
			
			CellStyle cellStyle = wb.createCellStyle();
			cellStyle.setAlignment( CellStyle.ALIGN_CENTER ); // 居中
			cellStyle.setVerticalAlignment( CellStyle.VERTICAL_CENTER ); // 居中
			
//			row.createCell(0).setCellValue( "工作类别" );
//			row.createCell(1).setCellValue( "重点事项" );//中心工作内容
//			row.createCell(2).setCellValue( "责任部门" );
//			row.createCell(3).setCellValue( "事项分解及描述" );//具体工作内容
//			row.createCell(4).setCellValue( "工作汇报情况" );
//			row.createCell(5).setCellValue( "工作汇报形式" );
//			row.createCell(6).setCellValue( "具体行动举措" );
//			row.createCell(7).setCellValue( "预期里程碑/阶段性结果标志" );
//			row.createCell(8).setCellValue( "截止目前完成情况" );
//			row.createCell(9).setCellValue( "下一步工作要点及需求" );
//			row.createCell(10).setCellValue( "督办评价" );
//			row.createCell(11).setCellValue( "领导评价" );
			
			sheet.setColumnWidth( 0, 10*256 ); 
			sheet.setColumnWidth( 1, 50*256 );
			sheet.setColumnWidth( 2, 20*256 ); 
			sheet.setColumnWidth( 3, 50*256 );
			sheet.setColumnWidth( 4, 10*256 );
			sheet.setColumnWidth( 5, 10*256 );
			sheet.setColumnWidth( 6, 50*256 );
			sheet.setColumnWidth( 7, 50*256 );
			sheet.setColumnWidth( 8, 50*256 );
			sheet.setColumnWidth( 9, 50*256 );
			sheet.setColumnWidth( 10, 50*256 );
			sheet.setColumnWidth( 11, 50*256 );
			
			Iterator<Row> rowIterator = sheet.rowIterator();
			Iterator<Cell> cellIterator = null;
			
			while( rowIterator.hasNext() ){
				row = rowIterator.next();
				row.setHeight( (short) ( 40 * 20 ) );
				
				cellIterator = row.cellIterator();
				while( cellIterator.hasNext() ){
					cell = cellIterator.next();
					cell.setCellStyle( cellStyle );
				}
			}
			
		    wb.write(fileOut);
		    fileOut.close();
		    wb.close();
	}
}
