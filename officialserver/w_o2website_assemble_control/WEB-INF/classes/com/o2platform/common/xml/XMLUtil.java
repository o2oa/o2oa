package com.o2platform.common.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;


/**
 * XML工具类
 * 
 * @项目名 ：OAService
 * @包 名 ：com.land.oaservice.xml
 * @文件名 ：XMLUtil.java
 * @单 位 ：浙江兰德纵横网络技术有限公司
 * @日 期 ：2009-9-17
 */
public class XMLUtil {
	
	public static Logger logger = Logger.getLogger(XMLUtil.class);

	public static final String TEXT = "TEXT";

	public static final String ATRRIBUTE = "ATRRIBUTE";

	public static final String SELFTEXT = "SELFTEXT";

	public static void main(String[] args) {
		//String company = "<data> <isautostart name=\"是否自动发起流程\">1</isautostart> <scale_of_marks_id name=\"单项评分标准ID\">2788898765432</scale_of_marks_id> <auto_starttime name=\"自动发起时间\">2015-09-11 09:11:15</auto_starttime> <auto_endtime name=\"自动结束时间\">2015-09-115 09:11:15</auto_endtime></data>";
		String company = "<fields><field><code>FIELD_XTJSMS</code><name>系统建设模式</name><v_type>string</v_type><v_length>1000</v_length><v_value>系统建设模式test1</v_value></field><field><code>FIELD_JSGZJDJH</code><name>建设工作进度计划</name><v_type>textarea</v_type><v_length>10000</v_length><v_value>建设工作进度计划22222222222test</v_value></field><field><code>FIELD_YJWCSJ</code><name>预计完成时间</name><v_type>date</v_type><v_length>20</v_length><v_value>预计完成时间随便填写xxxxxxxxxxxx</v_value></field><field><code>FIELD_DQJSJD</code><name>当前建设进度</name><v_type>string</v_type><v_length>1000</v_length><v_value>sssssssssssssss当前建设进度</v_value></field><field><code>FIELD_DQFGBL</code><name>当前覆盖比例</name><v_type>string</v_type><v_length>100</v_length><v_value>当前覆盖比例50%%%%%</v_value></field></fields>";
		XMLUtil XMLUtil = new XMLUtil();
		Map map = new HashMap();
		map.put("name", "TEXT");
		map.put("v_value", "TEXT");
		try {
			System.out.println(XMLUtil.getXmlNodeValueList( company , "/fields/field", map));
		} catch (DocumentException e) {
			logger.error("系统异常", e);
		}
	}

	/**
	 * 从XML文档里解析出指定路径下，指标节点的内容
	 * @param content
	 * @param xmlNodePath
	 * @param nodeName
	 * @param type
	 * @return
	 * @throws DocumentException
	 */
	public String getXmlNodeValue(String content, String xmlNodePath, String nodeName, String type) throws DocumentException {
		Document doc = getDocumentByContent(content);
		List list = doc.selectNodes( xmlNodePath );
		Element element = null;
		String result = "";
		try{
			for (int i = 0; list != null && i < list.size(); i++) {
				element = (Element)list.get(i);
				if ("TEXT".equals(type)) {// 如果是节点的文本
					if (element.element(nodeName) != null) {
						result = element.elementText(nodeName);
					}
				} else if ("ATRRIBUTE".equals(type)) {// 如果是节点的属性
					if (element.attribute(nodeName) != null) {
						result = element.attributeValue(nodeName);
					}
				} else if ("SELFTEXT".equals(type)) {
					result = element.getText();
				}
			}
		}catch(Exception e){
			logger.error("系统在尝试从XML文档中获取信息时发生错误。xmlNodePath=" + xmlNodePath, e);
		}		
		return result;
	}
	
	

	public Map getXmlNodeValue(String content, String xmlNodePath, Map attrMap) throws DocumentException {
		Document doc = getDocumentByContent(content);
		Map reslut = getNodeMap(doc, xmlNodePath, null, attrMap);
		return reslut;
	}
	
	
	public List<Map<String, Object>> getXmlNodeValueList(String content, String xmlNodePath,
			Map<String, Object> attrMap) throws DocumentException {
		Document doc = getDocumentByContent(content);
		List<Map<String, Object>> reslut = getNodeMapList(doc, xmlNodePath, null, attrMap);
		return reslut;
	}

	/**
	 * 查找数据字典，根据节点路径，属性值，得到需要的对应值
	 * 
	 * @param xmlFile
	 *            XML文件名 dictionary.xml
	 * @param xmlNodePath
	 *            节点路径，如：//dictionary/itemproperties/itemproperty
	 * @param filterMap
	 *            filterMap.put("sapvalue", "01");
	 * @param getAttrName
	 *            asvalue
	 * @param type
	 *            ATRRIBUTE
	 * @return String
	 * @throws DocumentException
	 */
	public String getDectionaryValue(Document doc, String xmlNodePath,
			Map filterMap, String getAttrName, String type)
			throws DocumentException {
		// 得到item节点
		Element node = getNodeByFilter(doc, xmlNodePath, filterMap);
		String reslut = getNodeValue(node, getAttrName, type);
		return reslut;
	}

	/**
	 * 根据XML内容构造一个XML Document文档
	 * 
	 * @param xmlContent
	 * @return
	 * @throws DocumentException
	 */
	public Document getDocumentByContent(String xmlContent)
			throws DocumentException {
		Document doc = DocumentHelper.parseText(xmlContent);
		return doc;
	}

	/**
	 * 根据XML文档，指定路径和属性参数MAP，得到一个List
	 * 
	 * @param xml
	 *            XML的内容
	 * @param listNodePath
	 *            要查寻的节点，比如“//学生信息/学生”
	 */
	@SuppressWarnings("unchecked")
	public List getNodeList(Document doc, String listNodePath, Map map) {
		Map resultmap = null;
		List resultList = new ArrayList();
		try {
			List list = doc.selectNodes(listNodePath);
			for (int i = 0; list != null && i < list.size(); i++) {
				Element node = (Element) list.get(i);
				resultmap = new HashMap();
				for (Iterator iterator = map.keySet().iterator(); iterator
						.hasNext();) {
					String key = iterator.next() + "";
					String value = map.get(key) + "";
					resultmap.put(key, getNodeValue(node, key, value));
				}
				resultList.add(resultmap);
			}
			//System.out.print("");
		} catch (Exception e) {
				logger.error("系统异常", e);
		}
		return resultList;
	}

	/**
	 * 
	 * 根据条件得到一个路径下的指定节点信息
	 * 
	 * @param doc
	 *            XML对象
	 * @param path
	 *            指定的XML节点路径
	 * @param filterMap
	 *            过滤的条件属性
	 * @param attributeMap
	 *            得到的参数属性
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getNodeMap(Document doc, String path, Map filterMap, Map attributeMap) {
		Map resultmap = new HashMap();
		try {
			List list = doc.selectNodes(path);
			for (int i = 0; list != null && i < list.size(); i++) {
				Element node = (Element) list.get(i);
				if (filterMap == null || filterMap.isEmpty()) {
					filterMap = new HashMap();
					for (Iterator iterator_re = attributeMap.keySet()
							.iterator(); iterator_re.hasNext();) {
						String key_re = iterator_re.next() + "";
						String value_re = attributeMap.get(key_re) + "";
						resultmap.put(key_re, getNodeValue(node, key_re,
								value_re));
					}
				}
				for (Iterator iterator = filterMap.keySet().iterator(); iterator
						.hasNext();) {
					String key_filter = iterator.next() + "";
					String value_filter = filterMap.get(key_filter) + "";
					String filetervalue = getNodeValue(node, key_filter, TEXT);
					String filetervalue1 = getNodeValue(node, key_filter,
							ATRRIBUTE);
					if ((filetervalue != null && filetervalue
							.equals(value_filter))
							|| filetervalue1 != null
							&& filetervalue1.equals(value_filter)) {
						// 就是这个条目
						// 取这个条目下面的指定信息
						for (Iterator iterator_re = attributeMap.keySet()
								.iterator(); iterator_re.hasNext();) {
							String key_re = iterator_re.next() + "";
							String value_re = attributeMap.get(key_re) + "";
							resultmap.put(key_re, getNodeValue(node, key_re,
									value_re));
						}
						i = list.size();
						break;
					}
				}
			}
		} catch (Exception e) {
				logger.error("系统异常", e);
		}
		return resultmap;
	}
	
	public List<Map<String, Object>> getNodeMapList(Document doc, String path, Map<String, Object> filterMap,
			Map<String, Object> attributeMap) {
		Map<String, Object> resultmap;
		List<Map<String, Object>> resultList = new ArrayList();
		try {
			List list = doc.selectNodes(path);
			for (int i = 0; list != null && i < list.size(); i++) {
				resultmap = new HashMap();
				Element node = (Element) list.get(i);
				if (filterMap == null || filterMap.isEmpty()) {
					filterMap = new HashMap();
					for (Iterator iterator_re = attributeMap.keySet()
							.iterator(); iterator_re.hasNext();) {
						String key_re = iterator_re.next() + "";
						String value_re = attributeMap.get(key_re) + "";
						resultmap.put(key_re, getNodeValue(node, key_re, value_re));
					}
				}
				for (Iterator iterator = filterMap.keySet().iterator(); iterator
						.hasNext();) {
					String key_filter = iterator.next() + "";
					String value_filter = filterMap.get(key_filter) + "";
					String filetervalue = getNodeValue(node, key_filter, TEXT);
					String filetervalue1 = getNodeValue(node, key_filter, ATRRIBUTE);
					if ((filetervalue != null && filetervalue
							.equals(value_filter))
							|| filetervalue1 != null
							&& filetervalue1.equals(value_filter)) {
						// 就是这个条目
						// 取这个条目下面的指定信息
						for (Iterator iterator_re = attributeMap.keySet().iterator(); iterator_re.hasNext();) {
							String key_re = iterator_re.next() + "";
							String value_re = attributeMap.get(key_re) + "";
							resultmap.put(key_re, getNodeValue(node, key_re, value_re));
						}
						i = list.size();
						break;
					}
				}
				resultList.add( resultmap );
			}
			
		} catch (Exception e) {
				logger.error("系统异常", e);
		}
		return resultList;
	}

	/**
	 * 根据节点对象，属性或节点的名称得到信息
	 * 
	 * @param node
	 * @param nName
	 * @param type
	 * @return
	 */
	public String getNodeValue(Element node, String nName, String type) {
		String result = null;
		if ("TEXT".equals(type)) {// 如果是节点的文本
			if (node.element(nName) != null) {
				result = node.elementText(nName);
			}
		} else if ("ATRRIBUTE".equals(type)) {// 如果是节点的属性
			if (node.attribute(nName) != null) {
				result = node.attributeValue(nName);
			}
		} else if ("SELFTEXT".equals(type)) {
			result = node.getText();
		}
		return result;
	}

	/**
	 * 
	 * 在文档里根据文档对象，下级路径和参数Map取一个具体的节点对象
	 * 
	 * @param doc
	 * @param path
	 * @param filterMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Element getNodeByFilter(Document doc, String path,
			Map filterMap) {
		try {
			List list = doc.selectNodes(path);
			for (int i = 0; list != null && i < list.size(); i++) {
				Element node = (Element) list.get(i);
				for (Iterator iterator = filterMap.keySet().iterator(); iterator
						.hasNext();) {
					String key_filter = iterator.next() + "";
					String value_filter = filterMap.get(key_filter) + "";
					String filetervalue = getNodeValue(node, key_filter, TEXT);
					String filetervalue1 = getNodeValue(node, key_filter,
							ATRRIBUTE);
					if ((filetervalue != null && filetervalue
							.equals(value_filter))
							|| filetervalue1 != null
							&& filetervalue1.equals(value_filter)) {
						// 就是这个条目
						return node;
					}
				}
			}
		} catch (Exception e) {
				logger.error("系统异常", e);
		}
		return null;
	}

	/**
	 * 根据节点，下级路径，条件Map得到一个具体的节点对象
	 * 
	 * @param rootNode
	 * @param path
	 * @param filterMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Element getNodeByFilter(Element rootNode, String path,
			Map filterMap) {
		try {
			List list = rootNode.selectNodes(path);
			Element node = null;
			for (int i = 0; list != null && i < list.size(); i++) {
				node = (Element) list.get(i);
				if (filterMap == null) {
					return node;
				}
				for (Iterator iterator = filterMap.keySet().iterator(); iterator
						.hasNext();) {
					String key_filter = iterator.next() + "";
					String value_filter = filterMap.get(key_filter) + "";
					String filetervalue = getNodeValue(node, key_filter, TEXT);
					String filetervalue1 = getNodeValue(node, key_filter,
							ATRRIBUTE);
					if ((filetervalue != null && filetervalue
							.equals(value_filter))
							|| filetervalue1 != null
							&& filetervalue1.equals(value_filter)) {
						// 就是这个条目
						return node;
					}
				}
			}
		} catch (Exception e) {
				logger.error("系统异常", e);
		}
		return null;
	}

	/**
	 * 根据节点，下级路径，条件Map和所取参数Map，获取某一个条目的指定信息Map
	 * 
	 * @param rootNode
	 * @param path
	 * @param filterMap
	 * @param attributeMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map getNodeMap(Element rootNode, String path, Map filterMap,
			Map attributeMap) {
		Map resultmap = new HashMap();
		try {
			List list = rootNode.selectNodes(path);
			for (int i = 0; list != null && i < list.size(); i++) {
				Element node = (Element) list.get(i);
				if (filterMap == null) {
					if (node != null) {
						for (Iterator iterator_re = attributeMap.keySet()
								.iterator(); iterator_re.hasNext();) {
							String key_re = iterator_re.next() + "";
							String value_re = attributeMap.get(key_re) + "";
							resultmap.put(key_re, getNodeValue(node, key_re,
									value_re));
						}
						i = list.size();
						break;
					}
				} else {
					for (Iterator iterator = filterMap.keySet().iterator(); iterator
							.hasNext();) {
						String key_filter = iterator.next() + "";
						String value_filter = filterMap.get(key_filter) + "";
						String filetervalue = getNodeValue(node, key_filter,
								TEXT);
						String filetervalue1 = getNodeValue(node, key_filter,
								ATRRIBUTE);
						if ((filetervalue != null && filetervalue
								.equals(value_filter))
								|| filetervalue1 != null
								&& filetervalue1.equals(value_filter)) {
							// 就是这个条目
							// 取这个条目下面的指定信息
							for (Iterator iterator_re = attributeMap.keySet()
									.iterator(); iterator_re.hasNext();) {
								String key_re = iterator_re.next() + "";
								String value_re = attributeMap.get(key_re) + "";
								resultmap.put(key_re, getNodeValue(node,
										key_re, value_re));
							}
							i = list.size();
							break;
						}
					}
				}
			}
		} catch (Exception e) {
				logger.error("系统异常", e);
		}
		return resultmap;
	}

	/**
	 * 
	 * 根据节点，下级路径，需要的参数Map取一个List<Map>
	 * 
	 * @param rootNode
	 * @param listNodePath
	 * @param map
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List getNodeList(Element rootNode, String listNodePath,
			Map map) {
		Map resultmap = new HashMap();
		List resultList = new ArrayList();
		try {
			List list = rootNode.selectNodes(listNodePath);
			for (int i = 0; list != null && i < list.size(); i++) {
				Element node = (Element) list.get(i);
				for (Iterator iterator = map.keySet().iterator(); iterator
						.hasNext();) {
					String key = iterator.next() + "";
					String value = map.get(key) + "";
					resultmap.put(key, getNodeValue(node, key, value));
				}
				resultList.add(resultmap);
			}
		} catch (Exception e) {
				logger.error("系统异常", e);
		}
		return resultList;
	}

	/**
	 * 从文件读取XML，返回字符串 
	 * 
	 * @param fileFullName
	 * @return
	 */
	public static String fileRead(String fileFullName) {
		SAXReader reader = new SAXReader();
		File file = new File(fileFullName);
		Document document = null;
		try {
			if (file.exists()) {
				document = reader.read(file);// 读取XML文件
				return document.asXML();
			}			
		} catch (DocumentException e) {
				logger.error("系统异常", e);
		}
		return "";
	}
}
