package org.otherobjects.cms.hibernate;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.otherobjects.cms.model.AccessControlled;
import org.otherobjects.cms.model.User;

@Entity
@Table(name = "secure_object_one")
public class SecureObject1 implements AccessControlled, Serializable {
	
	private static final long serialVersionUID = -7646442676920612922L;
	
	protected Long id;
	protected User owner;
	protected String value;
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    public Long getId()
    {
        return id;
    }
	
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(nullable = false, length = 50, unique = false)
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	@ManyToOne(optional= false)
	@JoinColumn(name = "owner_id", nullable = false)
	public User getOwner() {
		return owner;
	}

}
