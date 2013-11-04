package chordMessage;

import java.io.Serializable;

public class MonitoringData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2230240475805009530L;
	private int node_count;
	private int reg_count;
	private int off_count;
	private int conf_count;
	private double mes_rate;
	private static final int NUMBER_OF_SUCCESSORS = (Integer
			.parseInt(System
					.getProperty("de.uniba.wiai.lspi.chord.service.impl.ChordImpl.successors")) < 1) ? 1
			: Integer
					.parseInt(System
							.getProperty("de.uniba.wiai.lspi.chord.service.impl.ChordImpl.successors"));

	public MonitoringData(int r, int o, int c,double m){
		node_count=1;
		reg_count = r;
		off_count = o;
		conf_count = c;
		mes_rate = m;
	}
	
	public void removeReplicas(){
		reg_count /= (NUMBER_OF_SUCCESSORS+1);
		off_count /= (NUMBER_OF_SUCCESSORS+1);
		conf_count /= (NUMBER_OF_SUCCESSORS+1);
	}
	
	public int getNodeCount(){
		return node_count;
	}
	
	public int getOffCount(){
		return off_count;
	}
	public int getRegCount(){
		return reg_count;
	}
	public int getConfCount(){
		return conf_count;
	}
	public double getRateCount(){
		return mes_rate;
	}
	
	public void addData(MonitoringData d){
		node_count+=d.getNodeCount();
		reg_count+=d.getRegCount();
		conf_count+=d.getConfCount();
		off_count+=d.getOffCount();
		mes_rate+=d.getRateCount();
		
	}

	@Override
	public String toString() {
		return "[Monitoring Data] [Number of Nodes =" + node_count + ", Number of Registered Users ="
				+ reg_count + ", Number of Offline messages =" + off_count + ", Number of Running Conference ="
				+ conf_count + ", (Global) Average message rate =" + String.format("%.2f", mes_rate) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + conf_count;
		long temp;
		temp = Double.doubleToLongBits(mes_rate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + node_count;
		result = prime * result + off_count;
		result = prime * result + reg_count;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MonitoringData other = (MonitoringData) obj;
		if (conf_count != other.conf_count)
			return false;
		if (Double.doubleToLongBits(mes_rate) != Double
				.doubleToLongBits(other.mes_rate))
			return false;
		if (node_count != other.node_count)
			return false;
		if (off_count != other.off_count)
			return false;
		if (reg_count != other.reg_count)
			return false;
		return true;
	}
	
	
}
