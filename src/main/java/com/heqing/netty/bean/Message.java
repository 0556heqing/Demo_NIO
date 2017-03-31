package com.heqing.netty.bean;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private long   id;			//id
	private long   receiveId;	//接受者
	private long   sendId;		//发送者
	private String content;		//内容
	private int    type;		//类型
	private int    state;		//状态
	private Date   createTime;	//创建时间
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the receive
	 */
	public long getReceiveId() {
		return receiveId;
	}
	/**
	 * @param receive the receive to set
	 */
	public void setReceiveId(long receiveId) {
		this.receiveId = receiveId;
	}
	/**
	 * @return the send
	 */
	public long getSendId() {
		return sendId;
	}
	/**
	 * @param send the send to set
	 */
	public void setSendId(long sendId) {
		this.sendId = sendId;
	}
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}
	/**
	 * @return the createTime
	 */
	public Date getCreateTime() {
		return createTime;
	}
	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String toString() {
		return "message [id=" + id + ", receiveId=" + receiveId + ", sendId=" + sendId + ", content="+ content 
				+ ", type=" + type + ", state=" + state + ", createTime=" + createTime + "]";
	}

}
