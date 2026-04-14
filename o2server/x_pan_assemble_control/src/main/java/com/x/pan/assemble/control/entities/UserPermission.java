package com.x.pan.assemble.control.entities;

public class UserPermission {

	/**
	 * 历史版本权限，1为打开该权限，0为关闭该权限，默认为1
	 */
	public int history = 1;
	/**
	 * 复制权限，1为打开该权限，0为关闭该权限
	 */
	public int copy = 1;
	/**
	 * 导出权限，1为打开该权限，0为关闭该权限
	 */
	public int export = 0;
	/**
	 * 打印权限，1为打开该权限，0为关闭该权限
	 */
	public int print = 1;

	public int getHistory() {
		return history;
	}

	public void setHistory(int history) {
		this.history = history;
	}

	public int getCopy() {
		return copy;
	}

	public void setCopy(int copy) {
		this.copy = copy;
	}

	public int getExport() {
		return export;
	}

	public void setExport(int export) {
		this.export = export;
	}

	public int getPrint() {
		return print;
	}

	public void setPrint(int print) {
		this.print = print;
	}

}
