package testdata;

public class TreeNodeTest {
/*	
	public class TreeNode implements Serializable {
		private Integer cid;
		private String cname;
		private Integer pid;
		private List nodes = new ArrayList();
		 
		public TreeNode() {
		}
		 
		//getter、setter省略
		}
	
	*//**
	* 递归算法解析成树形结构
	*
	* @param cid
	* @return
	* @author jiqinlin
	*//*
	public TreeNode recursiveTree(int cid) {
		//根据cid获取节点对象(SELECT * FROM tb_tree t WHERE t.cid=?)
		TreeNode node = personService.getreeNode(cid);
		//查询cid下的所有子节点(SELECT * FROM tb_tree t WHERE t.pid=?)
		List childTreeNodes = personService.queryTreeNode(cid);
		//遍历子节点
		for (TreeNode child : childTreeNodes) {
			TreeNode n = recursiveTree(child.getCid()); //递归
			node.getNodes().add(n);
		}

		return node;
	}
*/
	
}
