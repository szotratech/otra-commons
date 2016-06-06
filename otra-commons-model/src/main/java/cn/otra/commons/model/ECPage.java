package cn.otra.commons.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author xiaodx
 * @version 0.0.1
 * @describe
 *  分页对象
 * @param <T>
 */
public class ECPage<T> implements Serializable {

	private static final long serialVersionUID = -3329523117121001147L;

	public static final int DEFAULT_SIZE = 10;

	private Integer currentPage = 1;// 当前页
	private Integer totalPage = 0; // 总页数
	private Integer rowsPerPage = 0;// 每页数据行数
	private Integer totalRows = 0; // 总记录数
	private List<T> list; // 数据集

	private int visualSize = 4;// 分页中距当前页的可见页数

	/**
	 * 附加方法 取得分页显示中的开始页
	 */
	public Integer getStartPage() {
		Integer p = currentPage - visualSize;
		if (p > 0) {
			return p;
		} else {
			return 1;
		}
	}

	/**
	 * 附加方法 取得分页显示中的结束页
	 */
	public Integer getEndPage() {
		Integer p = currentPage + visualSize;
		if (p <= totalPage) {
			return p;
		} else {
			return totalPage;
		}
	}

	/**
	 * 是否最后一页
	 * @return
	 */
	public boolean isLastPage() {
		return currentPage >= totalPage;
	}
	
	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(Integer rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public Integer getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(Integer totalRows) {
		this.totalRows = totalRows;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public int getVisualSize() {
		return visualSize;
	}

	public void setVisualSize(int visualSize) {
		this.visualSize = visualSize;
	}

	@Override
	public String toString() {
		return "ECPage [totalPage=" + totalPage + ", rowsPerPage=" + rowsPerPage + ", totalRows=" + totalRows
				+ ", list=" + list + ", currentPage=" + currentPage + "]";

	}

	public static <T> ECPage<T> initPage(List<T> list,int totalRows,int rowsPerPage,int currentPage) {
		// 分页查询
		ECPage<T> page = new ECPage<T>();
		int pageNum = totalRows / rowsPerPage;
		if (totalRows % rowsPerPage == 0) {
			page.setTotalPage(pageNum);
		} else {
			page.setTotalPage(pageNum + 1);
		}
		page.setCurrentPage(currentPage);
		page.setTotalRows(totalRows);
		page.setList(list);
		page.setRowsPerPage(rowsPerPage);
		return page;
	}
	
}
