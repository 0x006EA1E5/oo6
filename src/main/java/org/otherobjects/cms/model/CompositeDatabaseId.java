package org.otherobjects.cms.model;

public class CompositeDatabaseId {
	private String clazz;
	private long id;
	
	public CompositeDatabaseId()
	{
		
	}
	
	public CompositeDatabaseId(String clazz, long id)
	{
		this.clazz = clazz;
		this.id = id;
	}
	
	public String getClazz() {
		return clazz;
	}
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String toString()
	{
		return clazz + "-" + id;
	}
}
