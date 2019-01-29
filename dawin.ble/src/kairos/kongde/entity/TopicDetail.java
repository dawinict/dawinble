package kairos.kongde.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the topic_detail database table.
 * 
 */
@Entity
@Table(name="topic_detail")
@NamedQuery(name="TopicDetail.findAll", query="SELECT t FROM TopicDetail t")
public class TopicDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String remark;

	private int seq;

	private int size;

	@Column(name="topic_id")
	private int topicId;

	private int type;

	public TopicDetail() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public int getSeq() {
		return this.seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getTopicId() {
		return this.topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

}