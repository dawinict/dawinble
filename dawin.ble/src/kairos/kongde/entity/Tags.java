package kairos.kongde.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;


/**
 * The persistent class for the sensor database table.
 * 
 */
@Entity
@NamedQuery(name="Tags.findAll", query="SELECT s FROM Tags s")
public class Tags implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private Timestamp time;

	private int apid;

	private int tagid;

	private int rssi;

	private int sos;

	private float batt;

	public Tags() {
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public int getApid() {
		return apid;
	}

	public void setApid(int apid) {
		this.apid = apid;
	}

	public int getTagid() {
		return tagid;
	}

	public void setTagid(int tagid) {
		this.tagid = tagid;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	public int getSos() {
		return sos;
	}

	public void setSos(int sos) {
		this.sos = sos;
	}

	public float getBatt() {
		return batt;
	}

	public void setBatt(float batt) {
		this.batt = batt;
	}


}