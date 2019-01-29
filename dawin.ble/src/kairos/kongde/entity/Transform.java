package kairos.kongde.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the transform database table.
 * 
 */
@Entity
@NamedQuery(name="Transform.findAll", query="SELECT t FROM Transform t")
public class Transform implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private Boolean active;

	@Column(name="from_topic")
	private String fromTopic;

	private String name;

	@Column(name="to_topic")
	private String toTopic;

	public Transform() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Boolean getActive() {
		return this.active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getFromTopic() {
		return this.fromTopic;
	}

	public void setFromTopic(String fromTopic) {
		this.fromTopic = fromTopic;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getToTopic() {
		return this.toTopic;
	}

	public void setToTopic(String toTopic) {
		this.toTopic = toTopic;
	}

}