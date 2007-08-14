//package org.otherobjects.cms.hibernate;
//
//import java.io.Serializable;
//import java.util.Date;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Table;
//
//import org.hibernate.search.annotations.DocumentId;
//import org.hibernate.search.annotations.Field;
//import org.hibernate.search.annotations.Index;
//import org.hibernate.search.annotations.Indexed;
//import org.hibernate.search.annotations.Store;
//
//@Entity
//@Indexed(index="ipo")
//@Table(name = "indexed_persistent_object")
//public class IndexedPersistentObject implements Serializable {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 2988060448054996444L;
//	
//	private Long id;
//	private String title;
//	private String summary;
//	private Date date;
//	
//	
//	@Id
//	@DocumentId
//    @GeneratedValue(strategy = GenerationType.SEQUENCE)
//    public Long getId()
//    {
//        return id;
//    }
//	
//	@Field(index=Index.UN_TOKENISED, store=Store.YES)
//	@Column(name="title", length=100)
//	public String getTitle() {
//		return title;
//	}
//
//
//	public void setTitle(String title) {
//		this.title = title;
//	}
//	
//	@Field(index=Index.TOKENIZED, store=Store.NO)
//	@Column(name="summary", length=250)
//	public String getSummary() {
//		return summary;
//	}
//
//
//	public void setSummary(String summary) {
//		this.summary = summary;
//	}
//
//	@Field(index=Index.UN_TOKENISED, store=Store.YES)
//	@Column()
//	public Date getDate() {
//		return date;
//	}
//
//
//	public void setDate(Date date) {
//		this.date = date;
//	}
//
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//	
//	
//	
//	
//}
