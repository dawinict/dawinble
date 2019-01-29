package kairos.kongde.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the transform_detail database table.
 * 
 */
@Entity
@Table(name="transform_detail")
@NamedQuery(name="TransformDetail.findAll", query="SELECT t FROM TransformDetail t")
public class TransformDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private int length;

	private int offset;

	private String remark;

	private int seq;

	@Column(name="transform_id")
	private int transformId;

	private int type;

	public TransformDetail() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLength() {
		return this.length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getOffset() {
		return this.offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
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

	public int getTransformId() {
		return this.transformId;
	}

	public void setTransformId(int transformId) {
		this.transformId = transformId;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

}