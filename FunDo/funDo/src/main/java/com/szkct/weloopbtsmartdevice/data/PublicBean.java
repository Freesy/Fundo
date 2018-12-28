package com.szkct.weloopbtsmartdevice.data;

import java.io.Serializable;

public class PublicBean implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String mid;
	public String id;
	public String type;
	public String face;
	public String context;
	public String datetime;
	public String endtime;
	public String flag;

	public String rank;
	public String score;
	public String count;

	public String sex;
	public String name;

	public String lon;
	public String lat;

	public String gpxName;

	public String binlon;
	public String binlat;
	public String binaddr;
	public String endaddr;
	
	public String getEndtime()
	{
		return endtime;
	}

	public void setEndtime(String endtime)
	{
		this.endtime = endtime;
	}

	public String getMid()
	{
		return mid;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public void setMid(String mid)
	{
		this.mid = mid;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getFace()
	{
		return face;
	}

	public void setFace(String face)
	{
		this.face = face;
	}

	public String getContext()
	{
		return context;
	}

	public void setContext(String context)
	{
		this.context = context;
	}

	public String getDatetime()
	{
		return datetime;
	}

	public void setDatetime(String datetime)
	{
		this.datetime = datetime;
	}

	public String getFlag()
	{
		return flag;
	}

	public void setFlag(String flag)
	{
		this.flag = flag;
	}

	public String getRank()
	{
		return rank;
	}

	public void setRank(String rank)
	{
		this.rank = rank;
	}

	public String getScore()
	{
		return score;
	}

	public void setScore(String score)
	{
		this.score = score;
	}

	public String getCount()
	{
		return count;
	}

	public void setCount(String count)
	{
		this.count = count;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getSex()
	{
		return sex;
	}

	public void setSex(String sex)
	{
		this.sex = sex;
	}

	public String getLon()
	{
		return lon;
	}

	public void setLon(String lon)
	{
		this.lon = lon;
	}

	public String getLat()
	{
		return lat;
	}

	public void setLat(String lat)
	{
		this.lat = lat;
	}

	public String getGpxName()
	{
		return gpxName;
	}

	public void setGpxName(String gpxName)
	{
		this.gpxName = gpxName;
	}

	public String getBinlon()
	{
		return binlon;
	}

	public void setBinlon(String binlon)
	{
		this.binlon = binlon;
	}

	public String getBinlat()
	{
		return binlat;
	}

	public void setBinlat(String binlat)
	{
		this.binlat = binlat;
	}

	public String getBinaddr()
	{
		return binaddr;
	}

	public void setBinaddr(String binaddr)
	{
		this.binaddr = binaddr;
	}

	public String getEndaddr()
	{
		return endaddr;
	}

	public void setEndaddr(String endaddr)
	{
		this.endaddr = endaddr;
	}
}
