package kairos.kongde.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the sensor database table.
 * 
 */
@Entity
@NamedQuery(name="Ap.findAll", query="SELECT s FROM Ap s")
public class Ap implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private int act;

	private String mac;

	private String remark;

	private int x;

	private int y;

	private int apid;
	
	private float batt;

	public Ap() {
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

	public int getX() {
		return this.x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getApid() {
		return apid;
	}

	public void setApid(int apid) {
		this.apid = apid;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public float getBatt() {
		return batt;
	}

	public void setBatt(float batt) {
		this.batt = batt;
	}

	public int getAct() {
		return act;
	}

	public void setAct(int act) {
		this.act = act;
	}

}